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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;
import org.n52.geoprocessing.wps.client.encoder.WPS20ExecuteEncoder;
import org.n52.geoprocessing.wps.client.model.Format;
import org.n52.geoprocessing.wps.client.model.ResponseMode;
import org.n52.geoprocessing.wps.client.model.TransmissionMode;
import org.n52.geoprocessing.wps.client.model.execution.ComplexInput;
import org.n52.geoprocessing.wps.client.model.execution.ComplexInputReference;
import org.n52.geoprocessing.wps.client.model.execution.Execute;
import org.n52.geoprocessing.wps.client.model.execution.ExecuteInput;
import org.n52.geoprocessing.wps.client.model.execution.ExecuteOutput;
import org.n52.geoprocessing.wps.client.model.execution.ExecutionMode;
import org.n52.svalbard.encode.exception.EncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteEncoderTest {

    private static Logger LOGGER = LoggerFactory.getLogger(ExecuteEncoderTest.class);

    @Test
    public void encodeWPS20ExecuteAsyncRawComplexValueInComplexValueOut(){

        Execute execute = new Execute();

        execute.setExecutionMode(ExecutionMode.ASYNC);

        execute.setResponseMode(ResponseMode.RAW);

        execute.setId("org.n52.wps-client-lib.testprocess");

        ComplexInput complexInput = new ComplexInput();

        complexInput.setId("complexInput");

        Format format = new Format();

        format.setMimeType("text/xml");

        complexInput.setFormat(format);

        String xmlInput = "<xml xmlns=\"http://test.org\"><test/></xml>";

        complexInput.setValue(xmlInput);

        execute.setInputs(Arrays.asList(new ExecuteInput[]{complexInput}));

        ExecuteOutput output = new ExecuteOutput();

        output.setId("complexOutput");

        output.setFormat(format);

        output.setTransmissionMode(TransmissionMode.VALUE);

        execute.setOutputs(Arrays.asList(new ExecuteOutput[]{output}));

        try {
            WPS20ExecuteEncoder.encode(execute);
        } catch (EncodingException | XMLStreamException e) {
            // TODO Auto-generated catch block
            LOGGER.error("");
        }

    }

    @Test
    public void encodeWPS20ExecuteAsyncDocomentComplexReferenceInComplexValueOut() throws MalformedURLException{

        Execute execute = new Execute();

        execute.setExecutionMode(ExecutionMode.ASYNC);

        execute.setResponseMode(ResponseMode.DOCUMENT);

        execute.setId("org.n52.wps-client-lib.testprocess");

        ComplexInput complexInput = new ComplexInput();

        complexInput.setId("complexInput");

        Format format = new Format();

        format.setMimeType("text/xml");

        complexInput.setFormat(format);

        String url = "http://test.org/xyz";

        ComplexInputReference complexInputReference = new ComplexInputReference();

        complexInputReference.setHref(new URL(url));

        complexInput.setReference(complexInputReference);

        execute.setInputs(Arrays.asList(new ExecuteInput[]{complexInput}));

        ExecuteOutput output = new ExecuteOutput();

        output.setId("complexOutput");

        output.setFormat(format);

        output.setTransmissionMode(TransmissionMode.VALUE);

        execute.setOutputs(Arrays.asList(new ExecuteOutput[]{output}));

        try {
            WPS20ExecuteEncoder.encode(execute);
        } catch (EncodingException | XMLStreamException e) {
            // TODO Auto-generated catch block
            LOGGER.error("");
        }

    }

}
