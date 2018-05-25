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

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.xmlbeans.XmlException;
import org.junit.Test;
import org.n52.geoprocessing.wps.client.encoder.WPS100ExecuteEncoder;
import org.n52.geoprocessing.wps.client.encoder.WPS20ExecuteEncoder;
import org.n52.geoprocessing.wps.client.model.Format;
import org.n52.geoprocessing.wps.client.model.InputDescription;
import org.n52.geoprocessing.wps.client.model.OutputDescription;
import org.n52.geoprocessing.wps.client.model.Process;

import net.opengis.wps.x100.ProcessDescriptionsDocument;
import net.opengis.wps.x20.ProcessOfferingsDocument;

public class ExecuteRequestBuilderTest {

    @Test
    public void testAddInput() throws XmlException, IOException{

        ProcessOfferingsDocument offeringsDocument = ProcessOfferingsDocument.Factory
                .parse(WPS20ProcessParserTest.class.getResourceAsStream("WPS20ProcessDescription.xml"));

        Process process = WPS20ProcessParser.parseProcess(offeringsDocument.getProcessOfferings().getProcessOfferingArray(0));

        ExecuteRequestBuilder executeRequestBuilder = new ExecuteRequestBuilder(process);

        InputDescription inputDescription = process.getInputs().get(0);

        Format inputFormat = inputDescription.getFormats().get(0);

        String complexInputData = "<text><xml>data</xml></test>";

        try {
            executeRequestBuilder.addComplexData(inputDescription.getId(), complexInputData, inputFormat.getSchema(), inputFormat.getEncoding(), inputFormat.getMimeType());
        } catch (WPSClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        OutputDescription outputDescription = process.getOutputs().get(0);

        Format outputFormat = outputDescription.getFormats().get(0);

        executeRequestBuilder.setResponseDocument(outputDescription.getId(), outputFormat.getSchema(), outputFormat.getEncoding(), outputFormat.getMimeType());

        executeRequestBuilder.setAsReference(outputDescription.getId(), true);

        System.out.println(WPS20ExecuteEncoder.encode(executeRequestBuilder.getExecute()));

        System.out.println(new WPS100ExecuteEncoder(executeRequestBuilder.getExecute()).encode());

    }

    @Test
    public void testWPS100() throws XmlException, IOException{

        ProcessDescriptionsDocument processDescriptionsDocument = ProcessDescriptionsDocument.Factory.parse(WPS100ProcessParserTest.class.getResourceAsStream("WPS100ProcessDescription.xml"));

        Process process = WPS100ProcessParser.parseProcess(processDescriptionsDocument.getProcessDescriptions().getProcessDescriptionArray(0));

        ExecuteRequestBuilder executeRequestBuilder = new ExecuteRequestBuilder(process);

        InputDescription inputDescription = process.getInputs().get(0);

        Format inputFormat = inputDescription.getFormats().get(0);

        String complexInputData = "<text><xml>data</xml></test>";

        try {
            executeRequestBuilder.addComplexData(inputDescription.getId(), complexInputData, inputFormat.getSchema(), inputFormat.getEncoding(), inputFormat.getMimeType());
        } catch (WPSClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        OutputDescription outputDescription = process.getOutputs().get(0);

        Format outputFormat = outputDescription.getFormats().get(0);

        executeRequestBuilder.setResponseDocument(outputDescription.getId(), outputFormat.getSchema(), outputFormat.getEncoding(), outputFormat.getMimeType());

        executeRequestBuilder.setAsReference(outputDescription.getId(), true);

        System.out.println(new WPS100ExecuteEncoder(executeRequestBuilder.getExecute()).encode());

    }

    //@Test
    public void testWPS20() throws Exception{

        WPSClientSession clientSession = WPSClientSession.getInstance();

        String processID = "iso19157.DQ_PositionalAccuracy.DQ_AbsoluteExternalPositionalAccuracy";

        String wpsURL = "https://tb12.dev.52north.org/data-quality-wps-proxy/service/wps";
//        String wpsURL = "http://ows.dev.52north.org:8080/SecurityProxy/service/wps";

        ClientCapabiltiesRequest capabiltiesRequest = new ClientCapabiltiesRequest("2.0.0");
//
        int statusCode = clientSession.checkService(capabiltiesRequest.getRequest(wpsURL), "");

        if(statusCode != 200){

            System.out.println("Got status code: " + statusCode);

            if(statusCode == 400){
                clientSession.setUseClientCertificate(true);
            }
        }
//
        int statusCode2 = clientSession.checkService(capabiltiesRequest.getRequest(wpsURL), "");

        System.out.println(statusCode2);

      clientSession.connect(wpsURL, "2.0.0");


//      String processID = "org.n52.geoprocessing.geotools.algorithm.CoordinateTransformationAlgorithm";

      System.out.println(clientSession.getProcessDescription(wpsURL, processID , "2.0.0"));

      org.n52.geoprocessing.wps.client.model.Process process = clientSession.getProcessDescription(wpsURL, processID, "2.0.0");

      ExecuteRequestBuilder executeRequestBuilder = new ExecuteRequestBuilder(process);

      executeRequestBuilder.addComplexDataReference("inputTargetDataset", "http://tb12.dev.52north.org/security-proxy/service/wfs?service=WFS&version=1.0.0&request=GetFeature&typeName=tb13:tnm-manhattan-streets&outputFormat=gml3&maxFeatures=50", "http://schemas.opengis.net/gml/3.1.0/base/feature.xsd", "", "text/xml; subtype=gml/3.1.0");
      executeRequestBuilder.addComplexDataReference("inputReferenceDataset", "http://tb12.dev.52north.org/security-proxy/service/wfs?service=WFS&version=1.0.0&request=GetFeature&typeName=tb13:manhattan-streets-reference&outputFormat=gml3&maxFeatures=50", "http://schemas.opengis.net/gml/3.1.0/base/feature.xsd", "", "text/xml; subtype=gml/3.1.0");
      executeRequestBuilder.addLiteralData("threshold", "10", "", "", "text/xml");
      executeRequestBuilder.addLiteralData("inputTargetField", "OBJECTID_1", "", "", "text/xml");
      executeRequestBuilder.addLiteralData("inputReferenceField", "OBJECTID_1", "", "", "text/xml");
      executeRequestBuilder.setResponseDocument("outputMetadataChunk", "", "", "text/xml");

      System.out.println(WPS20ExecuteEncoder.encode(executeRequestBuilder.getExecute()));

//      int statusCode3 = clientSession.checkService(wpsURL, WPS20ExecuteEncoder.encode(executeRequestBuilder.getExecute()).xmlText());
//
//      System.out.println(statusCode3);
//
//      if(statusCode3 == 401){
//          clientSession.setUseBearerToken(true);
//          clientSession.setBearerToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6IlJEVTNNME5HTWpaRU5FUXpSRVJCTkVSRlJFWkdRekExT1RKRVFVWkRRelkyUmtJeVJEQTBNZyJ9.eyJpc3MiOiJodHRwczovL2Jwcm9zcy01Mm4uZXUuYXV0aDAuY29tLyIsInN1YiI6IkFoc1hlelUzWEcyVFF2N3FvQ2JZcXVNenQzOW1PMklEQGNsaWVudHMiLCJhdWQiOiJodHRwOi8vdGIxMi5kZXYuNTJub3J0aC5vcmcvZGF0YS1xdWFsaXR5LXdwcy9XZWJQcm9jZXNzaW5nU2VydmljZSIsImV4cCI6MTUwNjQzODA0MCwiaWF0IjoxNTA2MzUxNjQwLCJzY29wZSI6IkV4ZWN1dGUifQ.OLjBgTPRHvJY26TX5l0QVcIQTCIA3ST5zCNamP77MMi82L5JnSxwnUrDazJ9vmDk10MGLWdtFR68LIhqs3YCAIv9SQgLhA1mjHakXSGrm6XdFM53ocF69JnVgHk9ZNLEkY-sf4ZqXMv0GVlr3Xl7fNnZylebtNsazHdByVoChFC6y28QE2xgKTl5mAivtWdQWg5ZVXWXAipT1N1B6tAu5ca-JT4ZSkfy-KjZvUdrtxLV-knnHkHjUEtxQFYE_6jjtNLuoHNSlJmL-8jzVbtwpnmPypN-5NEUNfM8TltET3kBgwaRdkhvoZn-LWSNIW9gY3Gm4nWy4H8E9FyI-Dk6jA");
//      }
//
//      Object response = clientSession.execute(wpsURL, executeRequestBuilder.getExecute(), "2.0.0");
//
//      System.out.println(response);

//      executeRequestBuilder.addLiteralData("source_epsg", "EPSG:4326", "", "", "text/plain");
//      executeRequestBuilder.addLiteralData("target_epsg", "EPSG:32118", "", "", "text/plain");
//      executeRequestBuilder.addComplexDataReference("data", "http://tb12.dev.52north.org/security-proxy/service/wfs?service=WFS&version=1.0.0&request=GetFeature&typeName=tb13:osm-manhattan-streets&maxFeatures=50&outputFormat=gml3", "http://schemas.opengis.net/gml/3.1.1/base/feature.xsd", "", "text/xml");
//      executeRequestBuilder.setResponseDocument("result", "", "", "application/x-zipped-shp");
//      executeRequestBuilder.setAsReference("result", true);


//        clientSession.connect(wpsURL, "2.0.0");
//
//
//        String processID = "org.n52.geoprocessing.geotools.algorithm.CoordinateTransformationAlgorithm";
//
//        System.out.println(clientSession.getProcessDescription(wpsURL, processID , "2.0.0"));
//
//        org.n52.geoprocessing.wps.client.model.Process process = clientSession.getProcessDescription(wpsURL, processID, "2.0.0");
//
//        ExecuteRequestBuilder executeRequestBuilder = new ExecuteRequestBuilder(process);
//
//        executeRequestBuilder.addLiteralData("source_epsg", "EPSG:4326", "", "", "text/plain");
//        executeRequestBuilder.addLiteralData("target_epsg", "EPSG:32118", "", "", "text/plain");
//        executeRequestBuilder.addComplexDataReference("data", "http://tb12.dev.52north.org/security-proxy/service/wfs?service=WFS&version=1.0.0&request=GetFeature&typeName=tb13:osm-manhattan-streets&maxFeatures=50&outputFormat=gml3", "http://schemas.opengis.net/gml/3.1.1/base/feature.xsd", "", "text/xml");
//        executeRequestBuilder.setResponseDocument("result", "", "", "application/x-zipped-shp");
//        executeRequestBuilder.setAsReference("result", true);
//
//        System.out.println(WPS20ExecuteEncoder.encode(executeRequestBuilder.getExecute()));
//
//        HttpResponse httpResponse2 = clientSession.checkService(wpsURL, WPS20ExecuteEncoder.encode(executeRequestBuilder.getExecute()).toString());

//        Object response = clientSession.execute(wpsURL, executeRequestBuilder.getExecute(), "2.0.0");
//
//        if(response instanceof InputStream){
//
//            InputStream reponseStream = (InputStream)response;
//
//            int i = -1;
//
//            while ((i = reponseStream.read()) != -1) {
//
//                System.out.print((char)i);
//
//            }
//
//        }

    }

}
