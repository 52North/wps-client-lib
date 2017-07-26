/**
 * Copyright (C) 2007-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *       • Apache License, version 2.0
 *       • Apache Software License, version 1.0
 *       • GNU Lesser General Public License, version 3
 *       • Mozilla Public License, versions 1.0, 1.1 and 2.0
 *       • Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.geoprocessing.wps.client.example;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.n52.geoprocessing.wps.client.ExecuteResponseAnalyser;
import org.n52.geoprocessing.wps.client.WPSClientException;
import org.n52.geoprocessing.wps.client.WPSClientSession;
import org.n52.geoprocessing.wps.client.model.Input;
import org.n52.geoprocessing.wps.client.model.Process;
import org.n52.geoprocessing.wps.client.model.WPSCapabilities;

import net.opengis.wps.x100.CapabilitiesDocument;
import net.opengis.wps.x100.ExecuteDocument;
import net.opengis.wps.x100.ExecuteResponseDocument;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.ProcessBriefType;
import net.opengis.wps.x100.ProcessDescriptionType;

public class WPSClientExample {

	public void testExecute() {

		String wpsURL = "http://geoprocessing.demo.52north.org:8080/wps/WebProcessingService";

		String processID = "org.n52.wps.server.algorithm.SimpleBufferAlgorithm";
		
		String version = "2.0.0";

//		try {
//			ProcessDescriptionType describeProcessDocument = requestDescribeProcess(
//					wpsURL, processID);
//			System.out.println(describeProcessDocument);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		try {
            WPSCapabilities cpbDoc = requestGetCapabilities(wpsURL, "1.0.0");
            
            System.out.println(cpbDoc); 

			Process describeProcessDocument = requestDescribeProcess(
					wpsURL, processID, version);
			
		} catch (WPSClientException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
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

		List<Input> inputList = processDescription.getInputs();

		for (Input input : inputList) {
			System.out.println(input.getId());
		}
		return processDescription;
	}

	public static void main(String[] args) {
		
		//TODO find way to initialize parsers/generators
		
		WPSClientExample client = new WPSClientExample();
		client.testExecute();
	}

}