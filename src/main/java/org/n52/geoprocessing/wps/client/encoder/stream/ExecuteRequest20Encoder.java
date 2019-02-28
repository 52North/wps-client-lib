/*
 * ﻿Copyright (C) 2019 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.geoprocessing.wps.client.encoder.stream;

import java.util.Collections;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.n52.geoprocessing.wps.client.model.Format;
import org.n52.geoprocessing.wps.client.model.execution.BoundingBox;
import org.n52.geoprocessing.wps.client.model.execution.BoundingBoxData;
import org.n52.geoprocessing.wps.client.model.execution.ComplexData;
import org.n52.geoprocessing.wps.client.model.execution.ComplexDataReference;
import org.n52.geoprocessing.wps.client.model.execution.Data;
import org.n52.geoprocessing.wps.client.model.execution.Execute;
import org.n52.geoprocessing.wps.client.model.execution.ExecuteOutput;
import org.n52.geoprocessing.wps.client.model.execution.LiteralData;
import org.n52.geoprocessing.wps.client.model.execution.WPSExecuteParameter;
import org.n52.geoprocessing.wps.client.xml.WPS100Constants;
import org.n52.javaps.service.xml.OWSConstants;
import org.n52.javaps.service.xml.WPSConstants;
import org.n52.javaps.service.xml.XMLSchemaConstants;
import org.n52.shetland.w3c.SchemaLocation;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.stream.xml.AbstractMultiElementXmlStreamWriter;
import org.n52.svalbard.stream.XLinkConstants;

public class ExecuteRequest20Encoder extends AbstractMultiElementXmlStreamWriter {

    private static final String SERVICE = "WPS";

    private static final String VERSION = "2.0.0";

    private static final String MIME_TYPE_TEXT_PLAIN = "text/plain";

    private static final String MIME_TYPE_TEXT_XML = "text/xml";

    @Override
    public void writeElement(Object object) throws XMLStreamException, EncodingException {
        if (object instanceof Execute) {
            writeExecute((Execute) object);
        }
    }

    private void writeNamespaces() throws XMLStreamException {
        namespace(WPSConstants.NS_WPS_PREFIX, WPSConstants.NS_WPS);
        namespace(OWSConstants.NS_OWS_PREFIX, OWSConstants.NS_OWS);
        namespace(XLinkConstants.NS_XLINK_PREFIX, XLinkConstants.NS_XLINK);
    }

    private void writeNamespacesWithSchemalocation() throws XMLStreamException {
        writeNamespaces();
        schemaLocation(
                Collections.singleton(new SchemaLocation(WPSConstants.NS_WPS, XMLSchemaConstants.WPS20_SCHEMALOCTION)));
    }

    private void writeExecute(Execute execute) throws XMLStreamException {

        element(WPSConstants.Elem.QN_EXECUTE, execute, x -> {
            writeNamespacesWithSchemalocation();
            attr(WPSConstants.Attr.AN_RESPONSE, execute.getResponseMode().toString().toLowerCase());
            attr(WPSConstants.Attr.AN_MODE, execute.getExecutionMode().toString().toLowerCase());
            attr(WPSConstants.Attr.AN_SERVICE, SERVICE);
            attr(WPSConstants.Attr.AN_VERSION, VERSION);
            element(OWSConstants.Elem.QN_IDENTIFIER, execute.getId());
            if (execute.getInputs() != null) {
                writeInputElements(execute.getInputs());
            }
            writeOutputElements(execute.getOutputs());
        });

    }

    private void writeOutputElements(List<ExecuteOutput> outputs) throws XMLStreamException {
        for (ExecuteOutput executeOutput : outputs) {
            element(WPSConstants.Elem.QN_OUTPUT, executeOutput, x -> {
                attr(WPSConstants.Attr.AN_ID, executeOutput.getId());
                attr(WPSConstants.Attr.AN_TRANSMISSION, executeOutput.getTransmissionMode().toString().toLowerCase());
                setFormat(executeOutput);
            });
        }
    }

    private void writeInputElements(List<Data> inputs) throws XMLStreamException {
        for (Data executeInput : inputs) {
            element(WPSConstants.Elem.QN_INPUT, executeInput, x -> {
                attr(WPSConstants.Attr.AN_ID, executeInput.getId());
                String title = executeInput.getTitle();
                if (title != null && !title.isEmpty()) {
                    element(OWSConstants.Elem.QN_TITLE, title);
                }
                String abstrakt = executeInput.getAbstract();
                if (abstrakt != null && !abstrakt.isEmpty()) {
                    element(OWSConstants.Elem.QN_ABSTRACT, abstrakt);
                }
                if (executeInput instanceof ComplexData) {
                    writeComplexInput((ComplexData) executeInput);
                } else if (executeInput instanceof LiteralData) {
                    writeLiteralInput((LiteralData) executeInput);
                } else if (executeInput instanceof BoundingBoxData) {
                    writeBoundingBoxInput((BoundingBoxData) executeInput);
                }
            });
        }
    }

    private void writeLiteralInput(LiteralData executeInput) throws XMLStreamException {

        Format format = executeInput.getFormat();
        String mimeType = "";

        if (format != null) {
            mimeType = format.getMimeType();
        }
        if (!mimeType.isEmpty()) {
            if (mimeType.equals(MIME_TYPE_TEXT_PLAIN)) {
                element(WPSConstants.Elem.QN_DATA, executeInput.getValue().toString());
                attr(WPSConstants.Attr.AN_MIME_TYPE, MIME_TYPE_TEXT_PLAIN);
            } else if (mimeType.equals(MIME_TYPE_TEXT_XML)) {
                element(WPSConstants.Elem.QN_DATA, executeInput, x1 -> {
                    attr(WPSConstants.Attr.AN_MIME_TYPE, MIME_TYPE_TEXT_XML);
                    element(WPSConstants.Elem.QN_LITERAL_VALUE, executeInput.getValue().toString());
                    // if()
                });
            }
        } else {
            element(WPSConstants.Elem.QN_DATA, executeInput, x1 -> {
                attr(WPSConstants.Attr.AN_MIME_TYPE, MIME_TYPE_TEXT_XML);
                element(WPSConstants.Elem.QN_LITERAL_VALUE, executeInput.getValue().toString());
                // if()
            });
        }
    }

    private void writeBoundingBoxInput(BoundingBoxData executeInput) throws XMLStreamException {

        element(WPSConstants.Elem.QN_DATA, executeInput, x -> {
            element(OWSConstants.Elem.QN_BOUNDING_BOX, executeInput, x1 -> {

                BoundingBox boundingBox = (BoundingBox) executeInput.getValue();

                attr(WPS100Constants.Attr.QN_CRS_NO_NAMESPACE, boundingBox.getCrs());
                // attr(WPS100Constants.Attr.QN_DIMENSIONS_NO_NAMESPACE, "" +
                // boundingBox.getDimensions());//TODO check optional stuff for
                // this

                String lowerCorner = boundingBox.getMinX() + " " + boundingBox.getMinY();
                String upperCorner = boundingBox.getMaxX() + " " + boundingBox.getMaxY();

                element(OWSConstants.Elem.QN_LOWER_CORNER, lowerCorner);
                element(OWSConstants.Elem.QN_UPPER_CORNER, upperCorner);

            });
        });
    }

    private void writeComplexInput(ComplexData executeInput) throws XMLStreamException {
        if (executeInput.isReference()) {
            writeComplexInputReference(executeInput);
        } else {
            element(WPSConstants.Elem.QN_DATA, executeInput, x -> {
                setFormat(executeInput);
                chars(executeInput.getValue().toString());
            });
        }
    }

    private void writeComplexInputReference(ComplexData executeInput) throws XMLStreamException {

        ComplexDataReference reference = executeInput.getReference();

        element(WPSConstants.Elem.QN_REFERENCE, reference, x -> {
            attr(XLinkConstants.Attr.QN_HREF, reference.getHref().toString());
            setFormat(executeInput);
        });
    }

    private void setFormat(WPSExecuteParameter executeParameter) throws XMLStreamException {

        Format format = executeParameter.getFormat();

        attr(WPSConstants.Attr.AN_MIME_TYPE, format.getMimeType());

        String schema = format.getSchema();

        if (schema != null && !schema.isEmpty()) {
            attr(WPSConstants.Attr.AN_SCHEMA, schema);
        }

        String encoding = format.getEncoding();

        if (encoding != null && !encoding.isEmpty()) {
            attr(WPSConstants.Attr.AN_ENCODING, encoding);
        }
    }

}
