/*
 * ﻿Copyright (C) 2023 52°North Initiative for Geospatial Open Source
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

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlException;
import org.junit.Test;
import org.n52.geoprocessing.wps.client.encoder.WPS100ExecuteEncoder;
import org.n52.geoprocessing.wps.client.model.Format;
import org.n52.geoprocessing.wps.client.model.InputDescription;
import org.n52.geoprocessing.wps.client.model.OutputDescription;
import org.n52.geoprocessing.wps.client.model.Process;
import org.n52.svalbard.encode.exception.EncodingException;

import net.opengis.wps.x100.ProcessDescriptionsDocument;

public class ExecuteRequestBuilderTest {

//    @Test
//    public void testAddInput() throws XmlException, IOException{
//
//        ProcessOfferingsDocument offeringsDocument = ProcessOfferingsDocument.Factory
//                .parse(WPS20ProcessParserTest.class.getResourceAsStream("WPS20ProcessDescription.xml"));
//
//        Process process = WPS20ProcessParser.parseProcess(offeringsDocument.getProcessOfferings().getProcessOfferingArray(0));
//
//        ExecuteRequestBuilder executeRequestBuilder = new ExecuteRequestBuilder(process);
//
//        InputDescription inputDescription = process.getInputs().get(0);
//
//        Format inputFormat = inputDescription.getFormats().get(0);
//
//        String complexInputData = "<text><xml>data</xml></test>";
//
//        try {
//            executeRequestBuilder.addComplexData(inputDescription.getId(), complexInputData, inputFormat.getSchema(), inputFormat.getEncoding(), inputFormat.getMimeType());
//        } catch (WPSClientException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        OutputDescription outputDescription = process.getOutputs().get(0);
//
//        Format outputFormat = outputDescription.getFormats().get(0);
//
//        executeRequestBuilder.setResponseDocument(outputDescription.getId(), outputFormat.getSchema(), outputFormat.getEncoding(), outputFormat.getMimeType());
//
//        executeRequestBuilder.setAsReference(outputDescription.getId(), true);
//
//        System.out.println(WPS20ExecuteEncoder.encode(executeRequestBuilder.getExecute()));
//
//        System.out.println(new WPS100ExecuteEncoder(executeRequestBuilder.getExecute()).encode());
//
//    }

    @Test
    public void testWPS100() throws XmlException, IOException{

//        ProcessDescriptionsDocument processDescriptionsDocument = ProcessDescriptionsDocument.Factory.parse(WPS100ProcessParserTest.class.getResourceAsStream("WPS100ProcessDescription.xml"));
//
//        Process process = WPS100ProcessParser.parseProcess(processDescriptionsDocument.getProcessDescriptions().getProcessDescriptionArray(0));
//
//        ExecuteRequestBuilder executeRequestBuilder = new ExecuteRequestBuilder(process);
//
//        InputDescription inputDescription = process.getInputs().get(0);
//
//        Format inputFormat = inputDescription.getFormats().get(0);
//
//        String complexInputData = "<text><xml>data</xml></test>";
//
//        try {
//            executeRequestBuilder.addComplexData(inputDescription.getId(), complexInputData, inputFormat.getSchema(), inputFormat.getEncoding(), inputFormat.getMimeType());
//        } catch (WPSClientException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        OutputDescription outputDescription = process.getOutputs().get(0);
//
//        Format outputFormat = outputDescription.getFormats().get(0);
//
//        executeRequestBuilder.setResponseDocument(outputDescription.getId(), outputFormat.getSchema(), outputFormat.getEncoding(), outputFormat.getMimeType());
//
//        executeRequestBuilder.setAsReference(outputDescription.getId(), true);
//
//        try {
//            System.out.println(WPS100ExecuteEncoder.encode(executeRequestBuilder.getExecute()));
//        } catch (EncodingException | XMLStreamException e) {
//            fail(e.getMessage());
//        }

    }

//    @Test
//    public void testWPS20() throws Exception{
//
//        ProcessOfferingsDocument processDescriptionsDocument = ProcessOfferingsDocument.Factory.parse(WPS20ProcessParserTest.class.getResourceAsStream("WPS20ProcessDescription.xml"));
//
//        Process process = WPS20ProcessParser.parseProcess(processDescriptionsDocument.getProcessOfferings().getProcessOfferingArray(0));
//
//        ExecuteRequestBuilder executeRequestBuilder = new ExecuteRequestBuilder(process);
//
//        InputDescription inputDescription = process.getInputs().get(0);
//
//        Format inputFormat = inputDescription.getFormats().get(0);
//
//        String complexInputData = "<text><xml>data</xml></test>";
//
//        try {
//            executeRequestBuilder.addComplexData(inputDescription.getId(), complexInputData, inputFormat.getSchema(), inputFormat.getEncoding(), inputFormat.getMimeType());
//        } catch (WPSClientException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        OutputDescription outputDescription = process.getOutputs().get(0);
//
//        Format outputFormat = outputDescription.getFormats().get(0);
//
//        executeRequestBuilder.setResponseDocument(outputDescription.getId(), outputFormat.getSchema(), outputFormat.getEncoding(), outputFormat.getMimeType());
//
//        executeRequestBuilder.setAsReference(outputDescription.getId(), true);
//
//        System.out.println(WPS20ExecuteEncoder.encode(executeRequestBuilder.getExecute()));
//
//    }

}
