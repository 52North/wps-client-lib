/*
 * ﻿Copyright (C) ${inceptionYear} - ${currentYear} 52°North Initiative for Geospatial Open Source
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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.n52.geoprocessing.wps.client.model.Format;
import org.n52.geoprocessing.wps.client.model.ResponseMode;
import org.n52.geoprocessing.wps.client.model.TransmissionMode;
import org.n52.geoprocessing.wps.client.model.execution.ComplexInput;
import org.n52.geoprocessing.wps.client.model.execution.Execute;
import org.n52.geoprocessing.wps.client.model.execution.ExecuteInput;
import org.n52.geoprocessing.wps.client.model.execution.ExecuteOutput;
import org.n52.geoprocessing.wps.client.model.execution.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.opengis.ows.x20.ExceptionReportDocument.ExceptionReport;

public class ExecuteTest {

    private static Logger LOGGER = LoggerFactory.getLogger(ExecuteTest.class);

//    @Test
    public void testExecuteSuccess() throws WPSClientException, IOException {

//        String url = "http://geoprocessing.demo.52north.org:8080/wps/WebProcessingService";
        String url = "http://localhost:8080/wps/WebProcessingService";

        String version = "2.0.0";

        WPSClientSession session = WPSClientSession.getInstance();

        session.connect(url, version);

        Object response = session.execute(url, createExecute(), version);
    }

//    @Test
    public void testExecuteFailure() throws WPSClientException, IOException {

//        String url = "http://geoprocessing.demo.52north.org:8080/wps/WebProcessingService";
        String url = "http://localhost:8080/wps/WebProcessingService";

        String version = "2.0.0";

        WPSClientSession session = WPSClientSession.getInstance();

        session.connect(url, version);

        Object response = session.execute(url, createExecuteFailure(), version);

        assertTrue(response instanceof ExceptionReport);
    }

//    @Test
    public void testExecuteAsyncSuccess() throws WPSClientException, IOException {

//        String url = "http://geoprocessing.demo.52north.org:8080/wps/WebProcessingService";
        String url = "http://localhost:8080/wps/WebProcessingService";

        String version = "2.0.0";

        WPSClientSession session = WPSClientSession.getInstance();

        session.connect(url, version);

        Object response = session.execute(url, createExecute(ExecutionMode.ASYNC, ResponseMode.DOCUMENT, TransmissionMode.VALUE, false), version);

//        assertTrue(response instanceof ExceptionReport);
    }

//    @Test
    public void testExecuteAsyncFailure() throws WPSClientException, IOException {

//        String url = "http://geoprocessing.demo.52north.org:8080/wps/WebProcessingService";
        String url = "http://localhost:8080/wps/WebProcessingService";

        String version = "2.0.0";

        WPSClientSession session = WPSClientSession.getInstance();

        session.connect(url, version);

        Object response = session.execute(url, createExecute(ExecutionMode.ASYNC, ResponseMode.DOCUMENT, TransmissionMode.VALUE, true), version);

//        assertTrue(response instanceof ExceptionReport);
    }

    @Test
    public void testExecuteAsyncRawData() throws WPSClientException, IOException {

//        String url = "http://geoprocessing.demo.52north.org:8080/wps/WebProcessingService";
        String url = "http://localhost:8080/wps/WebProcessingService";

        String version = "2.0.0";

        WPSClientSession session = WPSClientSession.getInstance();

        session.connect(url, version);

        Object response = session.execute(url, createExecute(ExecutionMode.ASYNC, ResponseMode.RAW, TransmissionMode.VALUE, false), version);

//        assertTrue(response instanceof ExceptionReport);
    }

    private Execute createExecute(ExecutionMode executionMode, ResponseMode responseMode, TransmissionMode transmissionMode, boolean testFailure) {

        Execute execute = new Execute();

        execute.setExecutionMode(executionMode);

        execute.setResponseMode(responseMode);

//        if(testFailure){
//            execute.setId("non-existing-process");
//        }else{
            execute.setId("org.n52.wps.server.algorithm.test.EchoProcess");
//        }

        ComplexInput complexInput = new ComplexInput();


        if(testFailure){
            complexInput.setId("non-existing-input-id");
        }else{
            complexInput.setId("complexInput");
        }

        Format format = new Format();

        format.setMimeType("text/xml");

        complexInput.setFormat(format);

        String xmlInput = "<xml><test/></xml>";

        complexInput.setValue(xmlInput);

        execute.setInputs(Arrays.asList(new ExecuteInput[] { complexInput }));

        ExecuteOutput output = new ExecuteOutput();

        output.setId("complexOutput");

        output.setFormat(format);

        output.setTransmissionMode(transmissionMode);

        execute.setOutputs(Arrays.asList(new ExecuteOutput[] { output }));

        return execute;
    }

    private Execute createExecute() {

        Execute execute = new Execute();

        execute.setExecutionMode(ExecutionMode.SYNC);

        execute.setResponseMode(ResponseMode.DOCUMENT);

        execute.setId("org.n52.wps.server.algorithm.test.EchoProcess");

        ComplexInput complexInput = new ComplexInput();

        complexInput.setId("complexInput");

        Format format = new Format();

        format.setMimeType("text/xml");

        complexInput.setFormat(format);

        String xmlInput = "<xml><test/></xml>";
//        String xmlInput = "<xml xmlns=\"http://test.org\"><test/></xml>";

        complexInput.setValue(xmlInput);

        execute.setInputs(Arrays.asList(new ExecuteInput[] { complexInput }));

        ExecuteOutput output = new ExecuteOutput();

        output.setId("complexOutput");

        output.setFormat(format);

        output.setTransmissionMode(TransmissionMode.VALUE);

        execute.setOutputs(Arrays.asList(new ExecuteOutput[] { output }));

        return execute;

    }

    private Execute createExecuteFailure() {

        Execute execute = new Execute();

        execute.setExecutionMode(ExecutionMode.SYNC);

        execute.setResponseMode(ResponseMode.DOCUMENT);

        execute.setId("non-existing-process");

        ComplexInput complexInput = new ComplexInput();

        complexInput.setId("complexInput");

        Format format = new Format();

        format.setMimeType("text/xml");

        complexInput.setFormat(format);

        String xmlInput = "<xml><test/></xml>";

        complexInput.setValue(xmlInput);

        execute.setInputs(Arrays.asList(new ExecuteInput[] { complexInput }));

        ExecuteOutput output = new ExecuteOutput();

        output.setId("complexOutput");

        output.setFormat(format);

        output.setTransmissionMode(TransmissionMode.VALUE);

        execute.setOutputs(Arrays.asList(new ExecuteOutput[] { output }));

        return execute;

    }

    private Execute createExecuteAsync() {

        Execute execute = new Execute();

        execute.setExecutionMode(ExecutionMode.ASYNC);

        execute.setResponseMode(ResponseMode.DOCUMENT);

        execute.setId("org.n52.wps.server.algorithm.test.EchoProcess");

        ComplexInput complexInput = new ComplexInput();

        complexInput.setId("complexInput");

        Format format = new Format();

        format.setMimeType("text/xml");

        complexInput.setFormat(format);

        String xmlInput = "<xml><test/></xml>";

        complexInput.setValue(xmlInput);

        execute.setInputs(Arrays.asList(new ExecuteInput[] { complexInput }));

        ExecuteOutput output = new ExecuteOutput();

        output.setId("complexOutput");

        output.setFormat(format);

        output.setTransmissionMode(TransmissionMode.VALUE);

        execute.setOutputs(Arrays.asList(new ExecuteOutput[] { output }));

        return execute;

    }

}
