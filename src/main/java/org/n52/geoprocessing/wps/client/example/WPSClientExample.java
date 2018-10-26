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
package org.n52.geoprocessing.wps.client.example;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.n52.geoprocessing.wps.client.ExecuteRequestBuilder;
import org.n52.geoprocessing.wps.client.ExecuteResponseAnalyser;
import org.n52.geoprocessing.wps.client.WPSClientException;
import org.n52.geoprocessing.wps.client.WPSClientSession;
import org.n52.geoprocessing.wps.client.model.InputDescription;
import org.n52.geoprocessing.wps.client.model.Process;
import org.n52.geoprocessing.wps.client.model.WPSCapabilities;
import org.n52.geoprocessing.wps.client.model.execution.Execute;

import net.opengis.wps.x100.CapabilitiesDocument;
import net.opengis.wps.x100.ExecuteDocument;
import net.opengis.wps.x100.ExecuteResponseDocument;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.ProcessBriefType;
import net.opengis.wps.x100.ProcessDescriptionType;

public class WPSClientExample {

    public void testExecute(String version) {

        String wpsURL = "http://geoprocessing.demo.52north.org:8080/wps/WebProcessingService";

        String processID = "org.n52.wps.server.algorithm.test.EchoProcess";

//        try {
//            ProcessDescriptionType describeProcessDocument = requestDescribeProcess(
//                    wpsURL, processID);
//            System.out.println(describeProcessDocument);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {
            WPSCapabilities cpbDoc = requestGetCapabilities(wpsURL, version);

            System.out.println(cpbDoc);

            Process describeProcessDocument = requestDescribeProcess(
                    wpsURL, processID, version);

            ExecuteRequestBuilder builder = new ExecuteRequestBuilder(describeProcessDocument);

            builder.addComplexData("complexInput", "<test>input</test>", "", "", "text/xml");

            builder.setResponseDocument("complexOutput", "", "", "text/xml");

            builder.setAsynchronousExecute();

            execute(wpsURL, builder.getExecute(), version);

        } catch (WPSClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute(String url, Execute execute, String version){

        WPSClientSession wpsClient = WPSClientSession.getInstance();

        try {
            Object o = wpsClient.execute(url, execute, version);

            System.out.println(o);

        } catch (WPSClientException | IOException e) {
            System.out.println(e.getMessage());
        }

    }

    public WPSCapabilities requestGetCapabilities(String url, String version)
            throws WPSClientException {

        WPSClientSession wpsClient = WPSClientSession.getInstance();

        wpsClient.connect(url, version);

        WPSCapabilities capabilities = wpsClient.getWPSCaps(url);

        List<Process> processList = capabilities.getProcesses();

        System.out.println("Processes in capabilities:");
        for (Process process : processList) {
            System.out.println(process.getId());
        }
        return capabilities;
    }

    public Process requestDescribeProcess(String url,
            String processID, String version) throws IOException {

        WPSClientSession wpsClient = WPSClientSession.getInstance();

        Process processDescription = wpsClient
                .getProcessDescription(url, processID, version);

        List<InputDescription> inputList = processDescription.getInputs();

        for (InputDescription input : inputList) {
            System.out.println(input.getId());
        }
        return processDescription;
    }

    public static void main(String[] args) {

        //TODO find way to initialize parsers/generators

        WPSClientExample client = new WPSClientExample();
        client.testExecute("2.0.0");
    }

}
