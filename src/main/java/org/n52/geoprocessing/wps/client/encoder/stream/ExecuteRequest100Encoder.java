/*
 * ﻿Copyright (C) 2018 52°North Initiative for Geospatial Open Source
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
import org.n52.geoprocessing.wps.client.model.ResponseMode;
import org.n52.geoprocessing.wps.client.model.TransmissionMode;
import org.n52.geoprocessing.wps.client.model.execution.BoundingBox;
import org.n52.geoprocessing.wps.client.model.execution.BoundingBoxData;
import org.n52.geoprocessing.wps.client.model.execution.ComplexData;
import org.n52.geoprocessing.wps.client.model.execution.ComplexDataReference;
import org.n52.geoprocessing.wps.client.model.execution.Data;
import org.n52.geoprocessing.wps.client.model.execution.Execute;
import org.n52.geoprocessing.wps.client.model.execution.ExecuteOutput;
import org.n52.geoprocessing.wps.client.model.execution.ExecutionMode;
import org.n52.geoprocessing.wps.client.model.execution.LiteralData;
import org.n52.geoprocessing.wps.client.model.execution.WPSExecuteParameter;
import org.n52.geoprocessing.wps.client.xml.OWS11Constants;
import org.n52.geoprocessing.wps.client.xml.WPS100Constants;
import org.n52.geoprocessing.wps.client.xml.WPS100XMLSchemaConstants;
import org.n52.shetland.w3c.SchemaLocation;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.stream.xml.AbstractMultiElementXmlStreamWriter;
import org.n52.svalbard.stream.XLinkConstants;

public class ExecuteRequest100Encoder extends AbstractMultiElementXmlStreamWriter {

    private static final String TRUE = "true";

    private static final String SERVICE = "WPS";

    private static final String VERSION = "1.0.0";

    private static final String MIME_TYPE_TEXT_PLAIN = "text/plain";

    private static final String MIME_TYPE_TEXT_XML = "text/xml";

    @Override
    public void writeElement(Object object) throws XMLStreamException, EncodingException {
        if (object instanceof Execute) {
            writeExecute((Execute) object);
        }
    }

    private void writeNamespaces() throws XMLStreamException {
        namespace(WPS100Constants.NS_WPS_PREFIX, WPS100Constants.NS_WPS);
        namespace(OWS11Constants.NS_OWS_PREFIX, OWS11Constants.NS_OWS);
        namespace(XLinkConstants.NS_XLINK_PREFIX, XLinkConstants.NS_XLINK);
    }

    private void writeNamespacesWithSchemalocation() throws XMLStreamException {
        writeNamespaces();
        schemaLocation(Collections
                .singleton(new SchemaLocation(WPS100Constants.NS_WPS, WPS100XMLSchemaConstants.WPS100_SCHEMALOCTION)));
    }

    private void writeExecute(Execute execute) throws XMLStreamException {

        element(WPS100Constants.Elem.QN_EXECUTE, execute, x -> {
            writeNamespacesWithSchemalocation();
            attr(WPS100Constants.Attr.AN_SERVICE, SERVICE);
            attr(WPS100Constants.Attr.AN_VERSION, VERSION);
            element(OWS11Constants.Elem.QN_IDENTIFIER, execute.getId());
            if (execute.getInputs() != null) {
                writeInputElements(execute.getInputs());
            }

            element(WPS100Constants.Elem.QN_RESPONSE_FORM, execute.getOutputs(), x1 -> {
                writeResponseForm(execute);
            });
        });
    }

    private void writeResponseForm(Execute execute) throws XMLStreamException {

        if (execute.getResponseMode().equals(ResponseMode.DOCUMENT)) {

            element(WPS100Constants.Elem.QN_RESPONSE_DOCUMENT, execute.getOutputs(), x1 -> {
                if (execute.getExecutionMode().equals(ExecutionMode.ASYNC)) {
                    attr(WPS100Constants.Attr.AN_STORE_EXECUTE_RESPONSE, TRUE);
                    attr(WPS100Constants.Attr.AN_STATUS, TRUE);
                    // TODO check (could be not supported in rare cases)
                }
                writeOutputElements(execute.getOutputs());
            });

        } else if (execute.getResponseMode().equals(ResponseMode.RAW)) {
            // TODO
            writeRawData();
        }
    }

    private void writeRawData() {
        // TODO Auto-generated method stub
    }

    private void writeOutputElements(List<ExecuteOutput> outputs) throws XMLStreamException {

        for (ExecuteOutput executeOutput : outputs) {
            element(WPS100Constants.Elem.QN_OUTPUT, executeOutput, x1 -> {
                if (executeOutput.getTransmissionMode().equals(TransmissionMode.REFERENCE)) {
                    attr(WPS100Constants.Attr.AN_AS_REFERENCE, TRUE);
                }
                setFormat(executeOutput);
                element(OWS11Constants.Elem.QN_IDENTIFIER, executeOutput.getId());
            });
        }
    }

    private void writeInputElements(List<Data> inputs) throws XMLStreamException {

        element(WPS100Constants.Elem.QN_DATA_INPUTS, inputs, x -> {
            for (Data executeInput : inputs) {
                element(WPS100Constants.Elem.QN_INPUT, executeInput, x1 -> {
                    element(OWS11Constants.Elem.QN_IDENTIFIER, executeInput.getId());
                    String title = executeInput.getTitle();
                    if (title != null && !title.isEmpty()) {
                        element(OWS11Constants.Elem.QN_TITLE, title);
                    }
                    String abstrakt = executeInput.getAbstract();
                    if (abstrakt != null && !abstrakt.isEmpty()) {
                        element(OWS11Constants.Elem.QN_ABSTRACT, abstrakt);
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
        });
    }

    private void writeBoundingBoxInput(BoundingBoxData executeInput) throws XMLStreamException {

        element(WPS100Constants.Elem.QN_DATA, executeInput, x -> {
            element(WPS100Constants.Elem.QN_BOUNDING_BOX_DATA, executeInput, x1 -> {

                BoundingBox boundingBox = (BoundingBox) executeInput.getValue();

                attr(WPS100Constants.Attr.QN_CRS_NO_NAMESPACE, boundingBox.getCrs());
                // attr(WPS100Constants.Attr.QN_DIMENSIONS_NO_NAMESPACE, "" +
                // boundingBox.getDimensions());//TODO check optional stuff for
                // this

                String lowerCorner = boundingBox.getMinX() + " " + boundingBox.getMinY();
                String upperCorner = boundingBox.getMaxX() + " " + boundingBox.getMaxY();

                element(OWS11Constants.Elem.QN_LOWER_CORNER, lowerCorner);
                element(OWS11Constants.Elem.QN_UPPER_CORNER, upperCorner);

            });
        });
    }

    private void writeLiteralInput(LiteralData executeInput) throws XMLStreamException {

        Format format = executeInput.getFormat();
        String mimeType = "";

        if (format != null) {
            mimeType = format.getMimeType();
        }
        if (!mimeType.isEmpty()) {
            if (mimeType.equals(MIME_TYPE_TEXT_PLAIN)) {
                element(WPS100Constants.Elem.QN_DATA, executeInput.getValue().toString());
            } else if (mimeType.equals(MIME_TYPE_TEXT_XML)) {
                element(WPS100Constants.Elem.QN_DATA, executeInput, x1 -> {
                    element(WPS100Constants.Elem.QN_LITERAL_DATA, executeInput.getValue().toString());
                    // if()
                });
            }
        } else {
            element(WPS100Constants.Elem.QN_DATA, executeInput, x1 -> {
                element(WPS100Constants.Elem.QN_LITERAL_DATA, executeInput.getValue().toString());
                // if()
            });
        }
    }

    private void writeComplexInput(ComplexData executeInput) throws XMLStreamException {
        if (executeInput.isReference()) {
            writeComplexInputReference(executeInput);
        } else {
            element(WPS100Constants.Elem.QN_DATA, executeInput, x -> {
                element(WPS100Constants.Elem.QN_COMPLEX_DATA, executeInput, x1 -> {
                    setFormat(executeInput);
                    chars(executeInput.getValue().toString());
                });
            });
        }
    }

    private void writeComplexInputReference(ComplexData executeInput) throws XMLStreamException {

        ComplexDataReference reference = executeInput.getReference();

        element(WPS100Constants.Elem.QN_REFERENCE, reference, x -> {
            attr(XLinkConstants.Attr.QN_HREF, reference.getHref().toString());
            setFormat(executeInput);
        });
    }

    private void setFormat(WPSExecuteParameter executeParameter) throws XMLStreamException {

        Format format = executeParameter.getFormat();

        attr(WPS100Constants.Attr.AN_MIME_TYPE, format.getMimeType());

        String schema = format.getSchema();

        if (schema != null && !schema.isEmpty()) {
            attr(WPS100Constants.Attr.AN_SCHEMA, schema);
        }

        String encoding = format.getEncoding();

        if (encoding != null && !encoding.isEmpty()) {
            attr(WPS100Constants.Attr.AN_ENCODING, encoding);
        }
    }

}
