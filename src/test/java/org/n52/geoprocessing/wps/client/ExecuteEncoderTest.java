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
package org.n52.geoprocessing.wps.client;

import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlValidationError;
import org.junit.Before;
import org.junit.Test;
import org.n52.geoprocessing.wps.client.encoder.WPS100ExecuteEncoder;
import org.n52.geoprocessing.wps.client.encoder.WPS20ExecuteEncoder;
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
import org.n52.svalbard.encode.exception.EncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExecuteEncoderTest {

    private static Logger LOGGER = LoggerFactory.getLogger(ExecuteEncoderTest.class);

    private Execute execute;

    @Before
    public void createExecute(){

        execute = new Execute();

        execute.setExecutionMode(ExecutionMode.ASYNC);

        execute.setResponseMode(ResponseMode.RAW);

        execute.setId("org.n52.wps-client-lib.testprocess");

        ComplexData complexInput = new ComplexData();

        complexInput.setId("complexInput");

        Format format = new Format();

        format.setMimeType("text/xml");

        complexInput.setFormat(format);

        String xmlInput = "<xml xmlns=\"http://test.org\"><test/></xml>";

        complexInput.setValue(xmlInput);

        execute.setInputs(Arrays.asList(new Data[]{complexInput}));

        ExecuteOutput output = new ExecuteOutput();

        output.setId("complexOutput");

        output.setFormat(format);

        output.setTransmissionMode(TransmissionMode.VALUE);

        execute.setOutputs(Arrays.asList(new ExecuteOutput[]{output}));

    }


    @Test
    public void encodeWPS20ExecuteAsyncRawComplexValueInComplexValueOut(){

//        Execute execute = new Execute();
//
//        execute.setExecutionMode(ExecutionMode.ASYNC);
//
//        execute.setResponseMode(ResponseMode.RAW);
//
//        execute.setId("org.n52.wps-client-lib.testprocess");
//
//        ComplexData complexInput = new ComplexData();
//
//        complexInput.setId("complexInput");
//
//        Format format = new Format();
//
//        format.setMimeType("text/xml");
//
//        complexInput.setFormat(format);
//
//        String xmlInput = "<xml xmlns=\"http://test.org\"><test/></xml>";
//
//        complexInput.setValue(xmlInput);
//
//        execute.setInputs(Arrays.asList(new Data[]{complexInput}));
//
//        ExecuteOutput output = new ExecuteOutput();
//
//        output.setId("complexOutput");
//
//        output.setFormat(format);
//
//        output.setTransmissionMode(TransmissionMode.VALUE);
//
//        execute.setOutputs(Arrays.asList(new ExecuteOutput[]{output}));

        try {
            String executeRequestString = WPS20ExecuteEncoder.encode(execute);

            net.opengis.wps.x20.ExecuteDocument.Factory.parse(executeRequestString).validate();

            //TODO add assertions

        } catch (EncodingException | XMLStreamException | XmlException e) {
            LOGGER.error(e.getMessage());
            fail();
        }

    }

    @Test
    public void encodeWPS20ExecuteAsyncDocumentComplexReferenceInComplexValueOut() throws MalformedURLException{

        Execute execute = new Execute();

        execute.setExecutionMode(ExecutionMode.ASYNC);

        execute.setResponseMode(ResponseMode.DOCUMENT);

        execute.setId("org.n52.wps-client-lib.testprocess");

        ComplexData complexInput = new ComplexData();

        complexInput.setId("complexInput");

        Format format = new Format();

        format.setMimeType("text/xml");

        complexInput.setFormat(format);

        String url = "http://test.org/xyz";

        ComplexDataReference complexInputReference = new ComplexDataReference();

        complexInputReference.setHref(new URL(url));

        complexInput.setReference(complexInputReference);

        execute.setInputs(Arrays.asList(new Data[]{complexInput}));

        ExecuteOutput output = new ExecuteOutput();

        output.setId("complexOutput");

        output.setFormat(format);

        output.setTransmissionMode(TransmissionMode.VALUE);

        execute.setOutputs(Arrays.asList(new ExecuteOutput[]{output}));

        try {
            String executeRequestString = WPS20ExecuteEncoder.encode(execute);

            net.opengis.wps.x20.ExecuteDocument.Factory.parse(executeRequestString).validate();

            //TODO add assertions

        } catch (EncodingException | XMLStreamException | XmlException e) {
            LOGGER.error(e.getMessage());
            fail();
        }

    }

    @Test
    public void encodeWPS100ExecuteAsyncDocumentComplexReferenceInComplexValueOut() throws MalformedURLException{

        Execute execute = new Execute();

        execute.setExecutionMode(ExecutionMode.ASYNC);

        execute.setResponseMode(ResponseMode.DOCUMENT);

        execute.setId("org.n52.wps-client-lib.testprocess");

        ComplexData complexInput = new ComplexData();

        complexInput.setId("complexInput");

        Format format = new Format();

        format.setMimeType("text/xml");

        complexInput.setFormat(format);

        String url = "http://test.org/xyz";

        ComplexDataReference complexInputReference = new ComplexDataReference();

        complexInputReference.setHref(new URL(url));

        complexInput.setReference(complexInputReference);

        execute.setInputs(Arrays.asList(new Data[]{complexInput}));

        ExecuteOutput output = new ExecuteOutput();

        output.setId("complexOutput");

        output.setFormat(format);

        output.setTransmissionMode(TransmissionMode.VALUE);

        execute.setOutputs(Arrays.asList(new ExecuteOutput[]{output}));

        try {
            String executeRequestString = WPS100ExecuteEncoder.encode(execute);

            List<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>();
            XmlOptions validationOptions = new XmlOptions();
            validationOptions.setErrorListener(validationErrors);

            boolean validXML = net.opengis.wps.x100.ExecuteDocument.Factory.parse(executeRequestString).validate(validationOptions);

            if(!validXML){

                Iterator<XmlValidationError> iter = validationErrors.iterator();
                while (iter.hasNext())
                {
                    LOGGER.error(iter.next() + "\n");
                }

                fail();
            }

            //TODO add assertions

        } catch (EncodingException | XMLStreamException | XmlException e) {
            LOGGER.error(e.getMessage());
            fail();
        }

    }

    @Test
    public void encodeWPS100ExecuteAsyncDocumentComplexReferenceInComplexValueOutReference() throws MalformedURLException{

        Execute execute = new Execute();

        execute.setExecutionMode(ExecutionMode.ASYNC);

        execute.setResponseMode(ResponseMode.DOCUMENT);

        execute.setId("org.n52.wps-client-lib.testprocess");

        ComplexData complexInput = new ComplexData();

        complexInput.setId("complexInput");

        Format format = new Format();

        format.setMimeType("text/xml");

        complexInput.setFormat(format);

        String url = "http://test.org/xyz";

        ComplexDataReference complexInputReference = new ComplexDataReference();

        complexInputReference.setHref(new URL(url));

        complexInput.setReference(complexInputReference);

        execute.setInputs(Arrays.asList(new Data[]{complexInput}));

        ExecuteOutput output = new ExecuteOutput();

        output.setId("complexOutput");

        output.setFormat(format);

        output.setTransmissionMode(TransmissionMode.REFERENCE);

        execute.setOutputs(Arrays.asList(new ExecuteOutput[]{output}));

        try {
            String executeRequestString = WPS100ExecuteEncoder.encode(execute);

            List<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>();
            XmlOptions validationOptions = new XmlOptions();
            validationOptions.setErrorListener(validationErrors);

            boolean validXML = net.opengis.wps.x100.ExecuteDocument.Factory.parse(executeRequestString).validate(validationOptions);

            if(!validXML){

                Iterator<XmlValidationError> iter = validationErrors.iterator();
                while (iter.hasNext())
                {
                    LOGGER.error(iter.next() + "\n");
                }

                fail();
            }

            //TODO add assertions

        } catch (EncodingException | XMLStreamException | XmlException e) {
            LOGGER.error(e.getMessage());
            fail();
        }

    }

    @Test
    public void encodeWPS100ExecuteAsyncDocumentAllInputOutputTypesValue() throws MalformedURLException{

        Execute execute = new Execute();

        execute.setExecutionMode(ExecutionMode.ASYNC);

        execute.setResponseMode(ResponseMode.DOCUMENT);

        execute.setId("org.n52.wps-client-lib.testprocess");

        ComplexData complexInput = new ComplexData();

        complexInput.setId("complexInput");

        Format format = new Format();

        format.setMimeType("text/xml");

        complexInput.setFormat(format);

        String xmlInput = "<xml xmlns=\"http://test.org\"><test/></xml>";

        complexInput.setValue(xmlInput);

        LiteralData literalInput = new LiteralData();

        literalInput.setId("literalInput");

        literalInput.setDataType("xs:String");

        literalInput.setValue("test");

        BoundingBoxData boundingBoxInput = new BoundingBoxData();

        boundingBoxInput.setId("boundingBoxInput");

        BoundingBox boundingBox = new BoundingBox();

        boundingBox.setCrs("EPSG:4326");

        double minX = -180;
        double minY = -90;
        double maxX = 180;
        double maxY = 90;

		boundingBox.setMinX(minX);
		boundingBox.setMinY(minY);
		boundingBox.setMaxX(maxX);
        boundingBox.setMaxY(maxY);

        boundingBoxInput.setValue(boundingBox);

        execute.setInputs(Arrays.asList(new Data[]{complexInput, literalInput, boundingBoxInput}));

        ExecuteOutput output = new ExecuteOutput();

        output.setId("complexOutput");

        output.setFormat(format);

        output.setTransmissionMode(TransmissionMode.VALUE);

        execute.setOutputs(Arrays.asList(new ExecuteOutput[]{output}));

        try {
            String executeRequestString = WPS100ExecuteEncoder.encode(execute);

            List<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>();
            XmlOptions validationOptions = new XmlOptions();
            validationOptions.setErrorListener(validationErrors);

            boolean validXML = net.opengis.wps.x100.ExecuteDocument.Factory.parse(executeRequestString).validate(validationOptions);

            if(!validXML){

                Iterator<XmlValidationError> iter = validationErrors.iterator();
                while (iter.hasNext())
                {
                    LOGGER.error(iter.next() + "\n");
                }

                fail();
            }

            //TODO add assertions

        } catch (EncodingException | XMLStreamException | XmlException e) {
            LOGGER.error(e.getMessage());
            fail();
        }

    }

}
