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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Joiner;

public class PostClient {

    public String buildRequest(String value) throws UnsupportedEncodingException {

        String data = URLEncoder.encode("operation", "UTF-8") + "=" + URLEncoder.encode("process", "UTF-8");
        data += "&" + URLEncoder.encode("payload", "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");

        return data;
    }

    public static String sendRequest(String targetURL,
            String payload) throws IOException {
        // Construct data
        String payloadP = URLEncoder.encode(payload, "UTF-8");

        payloadP = "request=" + payloadP;

        InputStream in = sendRequestForInputStream(targetURL, payloadP);

        // Get the response
        BufferedReader rd = new BufferedReader(new InputStreamReader(in));
        List<String> lines = new LinkedList<String>();
        String line;
        while ((line = rd.readLine()) != null) {
            lines.add(line);
        }
        rd.close();
        return Joiner.on('\n').join(lines);
    }

    public static InputStream sendRequestForInputStream(String targetURL,
            String payload) throws IOException {
        // Send data
        URL url = new URL(targetURL);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestProperty("Accept-Encoding", "gzip");
        conn.setRequestProperty("Content-Type", "text/xml");

        conn.setDoOutput(true);

        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

        wr.write(payload);
        wr.close();

        if (conn.getResponseCode() >= 400) {
            return conn.getErrorStream();
        } else {
            return conn.getInputStream();
        }
    }

    public static String checkForExceptionReport(String targetURL,
            String payload) throws IOException {
        // Send data
        URL url = new URL(targetURL);

        URLConnection conn = url.openConnection();

        conn.setDoOutput(true);

        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

        wr.write(payload);
        wr.close();

        try {
            conn.getInputStream();
        } catch (IOException e) {
            /*
             * expected, ignore
             */
        }

        InputStream error = ((HttpURLConnection) conn).getErrorStream();

        String exceptionReport = "";

        int data = error.read();
        while (data != -1) {
            exceptionReport = exceptionReport + (char) data;
            data = error.read();
        }
        error.close();

        return exceptionReport;
    }

    public static String getAsyncDoc(String response) throws IOException, ParserConfigurationException, SAXException {

        Document doc;
        doc = parseXML(response);

        NodeList executeResponse = doc.getElementsByTagName("wps:ExecuteResponse");

        NamedNodeMap attributes = executeResponse.item(0).getAttributes();
        String statusLocation = attributes.getNamedItem("statusLocation").getNodeValue();
        String[] splittedURL = statusLocation.split("RetrieveResultServlet?");

        String referencedDocument = GetClient.sendRequest(splittedURL[0] + "RetrieveResultServlet", splittedURL[1]);

        for (int i = 0; i < WPSClientSession.maxNumberOfAsyncRequests; i++) {
            if (!referencedDocument.contains("ProcessSucceeded") && !referencedDocument.contains("ProcessFailed")) {
                try {
                    System.out.println("WPS process still processing. Waiting...");
                    Thread.sleep(WPSClientSession.delayForAsyncRequests);
                    referencedDocument =
                            GetClient.sendRequest(splittedURL[0] + "RetrieveResultServlet", splittedURL[1]);
                } catch (InterruptedException ignore) {
                    // do nothing
                }
            } else {
                return referencedDocument;
            }
        }
        throw new IOException("Could not receive result from WPS.");
    }

    public static Document parseXML(String xmlString) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        StringReader inStream = new StringReader(xmlString);
        InputSource inSource = new InputSource(inStream);
        return documentBuilder.parse(inSource);
    }
}
