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
package org.n52.geoprocessing.wps.client.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.inject.Provider;
import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.n52.geoprocessing.wps.client.encoder.stream.ExecuteRequest100Encoder;
import org.n52.geoprocessing.wps.client.encoder.stream.ExecuteRequest20Encoder;
import org.n52.geoprocessing.wps.client.model.Format;
import org.n52.geoprocessing.wps.client.model.ResponseMode;
import org.n52.geoprocessing.wps.client.model.TransmissionMode;
import org.n52.geoprocessing.wps.client.model.execution.ComplexData;
import org.n52.geoprocessing.wps.client.model.execution.ComplexDataReference;
import org.n52.geoprocessing.wps.client.model.execution.Data;
import org.n52.geoprocessing.wps.client.model.execution.Execute;
import org.n52.geoprocessing.wps.client.model.execution.ExecuteOutput;
import org.n52.geoprocessing.wps.client.model.execution.ExecutionMode;
import org.n52.geoprocessing.wps.client.model.execution.LiteralData;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.stream.xml.ElementXmlStreamWriter;
import org.n52.svalbard.encode.stream.xml.ElementXmlStreamWriterRepository;
import org.n52.svalbard.encode.stream.xml.XmlStreamWritingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.opengis.wps.x100.ComplexDataType;
import net.opengis.wps.x100.DataInputsType;
import net.opengis.wps.x100.DocumentOutputDefinitionType;
import net.opengis.wps.x100.InputReferenceType;
import net.opengis.wps.x100.InputType;
import net.opengis.wps.x100.LiteralDataType;
import net.opengis.wps.x100.OutputDefinitionType;
import net.opengis.wps.x100.ResponseDocumentType;

public class WPS100ExecuteEncoder {

    private static Logger LOGGER = LoggerFactory.getLogger(WPS100ExecuteEncoder.class);

    private static final List<Provider<ElementXmlStreamWriter>> ELEMENT_WRITERS =
            Arrays.asList(ExecuteRequest100Encoder::new);

    private Execute execute;

    public WPS100ExecuteEncoder() {
        //TODO remove
    }

    public WPS100ExecuteEncoder(Execute execute) {
        this.execute = execute;
    }

    public static String encode(Execute execute) throws XMLStreamException, EncodingException {

        ExecuteRequest100Encoder executeRequestWriter = new ExecuteRequest100Encoder();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        executeRequestWriter.setContext(
                new XmlStreamWritingContext(byteArrayOutputStream, new ElementXmlStreamWriterRepository(ELEMENT_WRITERS)::get));

        executeRequestWriter.writeElement(execute);

        return new String(byteArrayOutputStream.toByteArray());
    }

    private void addInputs(List<Data> inputs,
            net.opengis.wps.x100.ExecuteDocument.Execute executeRequest) {

        DataInputsType dataInputs = executeRequest.addNewDataInputs();

        for (Data input : inputs) {
            addInput(input, dataInputs.addNewInput());
        }

    }

    private void addInput(Data input,
            InputType newInput) {

        String id = input.getId();

        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Identifier for input cannot be empty.");
        }

        newInput.addNewIdentifier().setStringValue(id);

        if (input instanceof ComplexData) {
            addComplexInput((ComplexData) input, newInput);
        }else if(input instanceof LiteralData){
            addLiteralInput((LiteralData) input, newInput);
        }//TODO BBox input

    }

    //TODO UOM etc.
    private void addLiteralInput(LiteralData input,
            InputType newInput) {

        String valueString = "" + input.getValue();

        LiteralDataType data = newInput.addNewData().addNewLiteralData();

        data.setStringValue(valueString);
    }

    private void addComplexInput(ComplexData input,
            InputType newInput) {

        Format format = input.getFormat();

        String encoding = format.getEncoding();

        String schema = format.getSchema();

        if (input.isReference()) {

            InputReferenceType reference = newInput.addNewReference();

            reference.setMimeType(format.getMimeType());

            if (encoding != null && !encoding.isEmpty()) {
                reference.setEncoding(encoding);
            }

            if (schema != null && !schema.isEmpty()) {
                reference.setSchema(schema);
            }

            ComplexDataReference complexInputReference = input.getReference();

            if (complexInputReference.getHref() != null) {
                reference.setHref(complexInputReference.getHref().toString());
            } else if (complexInputReference.getBody() != null) {
                reference.setBody(complexInputReference.getBody());
            } else if (complexInputReference.getBodyReference() != null) {
                reference.addNewBodyReference().setHref(complexInputReference.getBodyReference().toString());
            }
        } else {
            ComplexDataType data = newInput.addNewData().addNewComplexData();

            Object value = input.getValue();

            if (value instanceof String) {

                String valueString = (String) value;

                try {
                    data.set(XmlObject.Factory.parse(valueString));
                } catch (XmlException e) {

                    LOGGER.warn("error parsing data String as xml node, trying to parse data as xs:string");

                    XmlString xml = XmlString.Factory.newInstance();

                    xml.setStringValue(valueString);

                    data.set(xml);

                }

            } else if (value instanceof InputStream) {

                InputStream stream = (InputStream) value;

                try {
                    data.set(XmlObject.Factory.parse(stream));
                } catch (XmlException e) {

                    LOGGER.warn("error parsing data stream as xml node, trying to parse data as xs:string");

                    String text = "";

                    int i = -1;

                    try {
                        while ((i = stream.read()) != -1) {
                            text = text + (char) i;
                        }
                    } catch (IOException e1) {
                        LOGGER.error("error parsing stream", e);
                    }

                    XmlString xml = XmlString.Factory.newInstance();

                    xml.setStringValue(text);

                    data.set(xml);
                } catch (IOException e) {
                    LOGGER.error("error parsing stream", e);
                }
            }

            data.setMimeType(format.getMimeType());

            if (encoding != null && !encoding.isEmpty()) {
                data.setEncoding(encoding);
            }

            if (schema != null && !schema.isEmpty()) {
                data.setSchema(schema);
            }

        }

    }

    private void addOutputs(List<ExecuteOutput> outputs,
            net.opengis.wps.x100.ExecuteDocument.Execute executeRequest) {

        //TODO differentiate between complex, literal and bbox output
        if (execute.getResponseMode().equals(ResponseMode.DOCUMENT)) {

            ResponseDocumentType responseForm = executeRequest.addNewResponseForm().addNewResponseDocument();

            if(execute.getExecutionMode().equals(ExecutionMode.ASYNC)){
                responseForm.setStatus(true);
            }

            for (ExecuteOutput executeOutput : outputs) {
                DocumentOutputDefinitionType output = responseForm.addNewOutput();
                addOutput(executeOutput, executeRequest, output);
            }
        }else if (execute.getResponseMode().equals(ResponseMode.DOCUMENT)) {

            if(outputs.size() > 1){
                LOGGER.info("Raw data was requested, but more than one output was requested. Using the first output.");
            }

            ExecuteOutput executeOutput = outputs.get(0);

            addRawData(executeOutput, executeRequest);
        }

    }

    private void addRawData(ExecuteOutput executeOutput,
            net.opengis.wps.x100.ExecuteDocument.Execute executeRequest) {

        OutputDefinitionType output = executeRequest.addNewResponseForm().addNewRawDataOutput();

        String id = executeOutput.getId();

        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Identifier for output cannot be empty.");
        }

        output.addNewIdentifier().setStringValue(id);

        Format format = executeOutput.getFormat();

        String encoding = format.getEncoding();

        String schema = format.getSchema();

        output.setMimeType(format.getMimeType());

        if (encoding != null && !encoding.isEmpty()) {
            output.setEncoding(encoding);
        }

        if (schema != null && !schema.isEmpty()) {
            output.setSchema(schema);
        }

    }

    private void addOutput(ExecuteOutput executeOutput,
            net.opengis.wps.x100.ExecuteDocument.Execute executeRequest,
            DocumentOutputDefinitionType output) {

        String id = executeOutput.getId();

        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Identifier for output cannot be empty.");
        }

        output.addNewIdentifier().setStringValue(id);

        Format format = executeOutput.getFormat();

        String encoding = format.getEncoding();

        String schema = format.getSchema();

        output.setMimeType(format.getMimeType());

        if (encoding != null && !encoding.isEmpty()) {
            output.setEncoding(encoding);
        }

        if (schema != null && !schema.isEmpty()) {
            output.setSchema(schema);
        }
        output.setAsReference(executeOutput.getTransmissionMode().equals(TransmissionMode.REFERENCE));

    }

}
