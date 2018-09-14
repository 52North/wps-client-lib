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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.crypto.dsig.XMLObject;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.configuration2.io.ClasspathLocationStrategy;
import org.apache.commons.configuration2.io.CombinedLocationStrategy;
import org.apache.commons.configuration2.io.DefaultFileSystem;
import org.apache.commons.configuration2.io.FileLocationStrategy;
import org.apache.commons.configuration2.io.FileLocator;
import org.apache.commons.configuration2.io.FileLocatorUtils;
import org.apache.commons.configuration2.io.FileSystem;
import org.apache.commons.configuration2.io.FileSystemLocationStrategy;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.n52.geoprocessing.wps.client.decoder.stream.DescribeProcessResponseDecoder;
import org.n52.geoprocessing.wps.client.encoder.WPS100ExecuteEncoder;
import org.n52.geoprocessing.wps.client.encoder.stream.ExecuteRequestEncoder;
import org.n52.geoprocessing.wps.client.model.Process;
import org.n52.geoprocessing.wps.client.model.ResponseMode;
import org.n52.geoprocessing.wps.client.model.WPSCapabilities;
import org.n52.geoprocessing.wps.client.model.execution.ExecutionMode;
import org.n52.janmayen.Json;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.stream.xml.ElementXmlStreamWriterRepository;
import org.n52.svalbard.encode.stream.xml.XmlStreamWritingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import net.opengis.wps.x100.ExecuteResponseDocument;
import net.opengis.wps.x100.ProcessDescriptionsDocument;

/**
 * Contains some convenient methods to access and manage Web Processing Services
 * in a very generic way.
 *
 * This is implemented as a singleton.
 *
 * @author bpross,foerster
 */

public class WPSClientSession {

    private static Logger LOGGER = LoggerFactory.getLogger(WPSClientSession.class);

    private static WPSClientSession session;

    private Map<String, WPSCapabilities> loggedServices;

    private XmlOptions options = null;

    private boolean cancel;

    public static final String VERSION_100 = "1.0.0";

    public static final String VERSION_200 = "2.0.0";

    public static final String SERVICE = "WPS";

    public static int maxNumberOfAsyncRequests = 100;

    public static int delayForAsyncRequests = 1000;

    // a Map of <url, all available process descriptions>
    private Map<String, List<Process>> processDescriptions;

    private CloseableHttpClient httpClient;

    private HttpClientBuilder httpClientBuilder;

    private String bearerToken = "";

    private boolean useBearerToken = false;

    /**
     * Initializes a WPS client session.
     *
     */
    private WPSClientSession() {
        options = new XmlOptions();
        options.setLoadStripWhitespace();
        options.setLoadTrimTextBuffer();
        loggedServices = new HashMap<String, WPSCapabilities>();
        processDescriptions = new HashMap<String, List<Process>>();
        httpClientBuilder = HttpClientBuilder.create();
//        httpClientBuilder.setConnectionManager(new BasicHttpClientConnectionManager());//TODO maybe shutdown manager
        httpClient = httpClientBuilder.build();
        loadProperties();
    }

    /*
     * @result An instance of a WPS Client session.
     */
    public static WPSClientSession getInstance() {
        if (session == null) {
            session = new WPSClientSession();
        }
        return session;
    }

    /**
     * This resets the WPSClientSession. This might be necessary, to get rid of
     * old service entries/descriptions. However, the session has to be
     * repopulated afterwards.
     */
    public static void reset() {
        session = new WPSClientSession();
    }

    /**
     * Connects to a WPS and retrieves Capabilities plus puts all available
     * Descriptions into cache.
     *
     * @param url
     *            the entry point for the service. This is used as id for
     *            further identification of the service.
     * @return true, if connect succeeded, false else.
     * @throws WPSClientException
     */
    public boolean connect(String url,
            String version) throws WPSClientException {
        LOGGER.info("CONNECT");
        if (loggedServices.containsKey(url)) {
            LOGGER.info("Service already registered: " + url);
            return false;
        }
        WPSCapabilities capsDoc = retrieveCapsViaGET(url, version);
        if (capsDoc != null) {
            loggedServices.put(url, capsDoc);
            return true;
        }
        LOGGER.warn("retrieving caps failed, caps are null");
        return false;
    }

    /**
     * removes a service from the session
     *
     * @param url
     */
    public void disconnect(String url) {
        if (loggedServices.containsKey(url)) {
            loggedServices.remove(url);
            processDescriptions.remove(url);
            LOGGER.info("service removed successfully: " + url);
        }
    }

    /**
     * returns the serverIDs of all loggedServices
     *
     * @return
     */
    public List<String> getLoggedServices() {
        return new ArrayList<String>(loggedServices.keySet());
    }

    /**
     * informs you if the descriptions for the specified service is already in
     * the session. in normal case it should return true :)
     *
     * @param serverID
     * @return success
     */
    public boolean descriptionsAvailableInCache(String serverID) {
        return processDescriptions.containsKey(serverID);
    }

    /**
     * return the processDescription for a specific process from Cache.
     *
     * @param serverID
     * @param processID
     * @return a ProcessDescription for a specific process from Cache.
     * @throws IOException
     */
    public Process getProcessDescription(String serverID,
            String processID,
            String version) throws IOException {
        List<Process> processes = getProcessDescriptionsFromCache(serverID);
        for (Process process : processes) {
            if (process.getId().equals(processID)) {
                if (process.getInputs() == null || process.getInputs().isEmpty()) {
                    try {
                        process = describeProcess(new String[] { processID }, serverID, version).get(0);
                    } catch (WPSClientException e) {
                        LOGGER.error("Could not fetch processdescription for process: " + processID, e);
                    }
                }
                return process;
            }
        }
        return null;
    }

    /**
     * Delivers all ProcessDescriptions from a WPS
     *
     * @param wpsUrl
     *            the URL of the WPS
     * @return An Array of ProcessDescriptions
     * @throws IOException
     */
    public List<Process> getAllProcessDescriptions(String wpsUrl)
            throws IOException {
        return getProcessDescriptionsFromCache(wpsUrl);
    }

    /**
     * looks up, if the service exists already in session.
     */
    public boolean serviceAlreadyRegistered(String serverID) {
        return loggedServices.containsKey(serverID);
    }

    /**
     * provides you the cached capabilities for a specified service.
     *
     * @param url
     * @return
     */
    public WPSCapabilities getWPSCaps(String url) {
        return loggedServices.get(url);
    }

    /**
     * retrieves the desired description for a service. the retrieved
     * information will not be held in cache!
     *
     * @param processIDs
     *            one or more processIDs
     * @param serverID
     * @throws WPSClientException
     */
    public List<Process> describeProcess(String[] processIDs,
            String serverID,
            String version) throws WPSClientException {
        return retrieveDescriptionViaGET(processIDs, serverID, version);
    }

    /**
     * Executes a process at a WPS
     *
     * @param url
     *            url of server not the entry additionally defined in the caps.
     * @param execute
     *            Execute document
     * @param version the version of the WPS
     *
     * @return either an ExecuteResponseDocument or an InputStream if asked for
     *         RawData or an Exception Report
     * @throws IOException
     */
    public Object execute(String url,
            org.n52.geoprocessing.wps.client.model.execution.Execute execute, String version) throws WPSClientException, IOException {

        boolean requestRawData = execute.getResponseMode() == ResponseMode.RAW;
        boolean requestAsync = execute.getExecutionMode() == ExecutionMode.ASYNC;//TODO: what about AUTO mode?

        Object executeObject = encode(execute, version);

        return execute(url, executeObject, requestRawData, requestAsync);
    }

    public String[] getProcessNames(String url) throws IOException {
        List<Process> processes = getProcessDescriptionsFromCache(url);
        String[] processNames = new String[processes.size()];
        for (int i = 0; i < processNames.length; i++) {
            processNames[i] = processes.get(i).getId();
        }
        return processNames;
    }

    public void cancelAsyncExecute(){
        setCancel(true);
    }

    public int checkService(String url, String payload){

//        CloseableHttpClient httpClient = httpClientBuilder.build();

        CloseableHttpResponse response = null;

        if(payload == null || payload.isEmpty()){

            HttpGet get = new HttpGet(url);

            try {

                response = httpClient.execute(get);
            } catch (IOException e) {
                LOGGER.error("IOException while trying to access: " + url);
            }
        }else{

            HttpPost post = new HttpPost(url);

            StringEntity stringEntity = null;
            try {
                stringEntity = new StringEntity(payload);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("Unsupported encoding in payload: " + payload);
            }

            post.setEntity(stringEntity);

            try {
                response = httpClient.execute(post);
            } catch (IOException e) {
                LOGGER.error("IOException while trying to access: " + url);
            }
        }

        int result = response.getStatusLine().getStatusCode();

        try {
            response.close();
        } catch (IOException e) {
            LOGGER.error("Could not close HTTPResponse ", e);
        }

        return result;
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public boolean isUseBearerToken() {
        return useBearerToken;
    }

    public void setUseBearerToken(boolean useBearerToken) {
        this.useBearerToken = useBearerToken;
    }

    private synchronized boolean isCancel() {
        return cancel;
    }

    private synchronized void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    private List<Process> getProcessDescriptionsFromCache(String wpsUrl)
            throws IOException {
        return loggedServices.get(wpsUrl).getProcesses();
    }

    private Object execute(String url,
            Object executeObject,
            boolean rawData, boolean requestAsync) throws WPSClientException, IOException {
        return retrieveExecuteResponseViaPOST(url, executeObject, rawData, requestAsync);
    }

    private Object encode(org.n52.geoprocessing.wps.client.model.execution.Execute execute, String version) {

        switch (version) {
        case VERSION_100:
            return new WPS100ExecuteEncoder(execute).encode();

        case VERSION_200:
            
            ExecuteRequestEncoder executeRequestWriter = new ExecuteRequestEncoder();

            OutputStream out  = new ByteArrayOutputStream();
            
            try {                
                executeRequestWriter.setContext(
                        new XmlStreamWritingContext(out, new ElementXmlStreamWriterRepository(Arrays.asList(ExecuteRequestEncoder::new))::get));
                executeRequestWriter.writeElement(execute);
            } catch (EncodingException | XMLStreamException e) {
                // TODO Auto-generated catch block
                LOGGER.error("");
            }
            
            return out;

        default:
            return XmlObject.Factory.newInstance();//TODO
        }
    }

    private WPSCapabilities retrieveCapsViaGET(String url,
            String version) throws WPSClientException {
        ClientCapabiltiesRequest req = new ClientCapabiltiesRequest(version);
        url = req.getRequest(url);
        try {
            URL urlObj = new URL(url);
            String responseString = retrieveResponseOrExceptionReportInpustream(urlObj);
            Object xmlObject = checkInputStream(responseString);
            return createWPSCapabilities(xmlObject);
        } catch (MalformedURLException e) {
            throw new WPSClientException("Capabilities URL seems to be unvalid: " + url, e);
        } catch (IOException e) {
            throw new WPSClientException("Error occured while retrieving capabilities from url: " + url, e);
        } catch (XMLStreamException e) {
            // TODO Auto-generated catch block
            LOGGER.error("");
            throw new WPSClientException("Error occured while retrieving capabilities from url: " + url, e);
        }
    }

    private String retrieveResponseOrExceptionReportInpustream(URL url) throws WPSClientException, IOException {

        HttpGet get = new HttpGet(url.toString());

        if(isUseBearerToken()){
            get.addHeader("Authorization", getBearerToken());
        }

        CloseableHttpResponse response = httpClient.execute(get);

        String responseString = parseInputStreamToString(response.getEntity().getContent());

        try {
            checkStatusCode(response);
        } catch (Exception e) {
            throw new WPSClientException("Got HTTP error code, response: " + responseString);
        }finally {
            response.close();
        }

        return responseString;
    }

    private void checkStatusCode(CloseableHttpResponse response) throws WPSClientException {

        int statusCode = response.getStatusLine().getStatusCode();

        if(statusCode >= 400){
            throw new WPSClientException("Got HTTP error code: " + statusCode);
        }

    }

    private String retrieveResponseOrExceptionReportInpustream(URL url,
            String executeObject) throws WPSClientException, IOException {

        HttpPost post = new HttpPost(url.toString());

        post.addHeader("Accept-Encoding", "gzip");
        post.addHeader("Content-Type", "text/xml");

        if(isUseBearerToken()){
            post.addHeader("Authorization", getBearerToken());
        }

        post.setEntity(new StringEntity(executeObject));

        CloseableHttpResponse response = httpClient.execute(post);

        String responseString = parseInputStreamToString(response.getEntity().getContent());

        try {
            checkStatusCode(response);
        } catch (Exception e) {
            throw new WPSClientException("Got HTTP error code, response: " + responseString);
        }finally {
            response.close();
        }

        return responseString;
    }

    private String parseInputStreamToString(InputStream in) throws IOException{

        String content = "";

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

        String line = "";

        while((line = bufferedReader.readLine()) != null){
            content = content.concat(line);
        }
        return content;

    }

    private WPSCapabilities createWPSCapabilities(Object object) throws XMLStreamException {

        if (object instanceof net.opengis.wps.x100.CapabilitiesDocument) {
            return createWPSCapabilitiesOWS11((net.opengis.wps.x100.CapabilitiesDocument) object);
        } else if (object instanceof InputStream) {
            return createWPSCapabilitiesOWS20((InputStream) object);
        }

        return new WPSCapabilities();
    }

    private WPSCapabilities createWPSCapabilitiesOWS20(InputStream in) throws XMLStreamException {
        return new WPS20CapabilitiesParser().createWPSCapabilitiesOWS20(in);
    }

    private WPSCapabilities createWPSCapabilitiesOWS11(net.opengis.wps.x100.CapabilitiesDocument xmlObject) {
        return new WPS100CapabilitiesParser().createWPSCapabilitiesOWS100(xmlObject);
    }

    private List<Process> retrieveDescriptionViaGET(String[] processIDs,
            String url,
            String version) throws WPSClientException {
        ClientDescribeProcessRequest req = new ClientDescribeProcessRequest(version);
        req.setIdentifier(processIDs);
        String requestURL = req.getRequest(url);
        try {
            URL urlObj = new URL(requestURL);
            String responseString = retrieveResponseOrExceptionReportInpustream(urlObj);
            Object doc = checkInputStream(responseString);

            WPSCapabilities capabilities = getWPSCaps(url);

            if (doc instanceof ProcessDescriptionsDocument) {
                return createProcessDescriptionArray((ProcessDescriptionsDocument) doc, capabilities);
            } else if (doc instanceof InputStream) {
                return createProcessDescriptionArray((InputStream) doc, capabilities);
            }
        } catch (MalformedURLException e) {
            throw new WPSClientException("URL seems not to be valid: " + url, e);
        } catch (IOException e) {
            throw new WPSClientException("Error occured while receiving data", e);
        } catch (XMLStreamException e) {
            // TODO Auto-generated catch block
            LOGGER.error("");
        }
        LOGGER.info("No valid ProcessDescription found. Returning empty list.");
        return new ArrayList<Process>();
    }

    private List<Process> createProcessDescriptionArray(
            InputStream in,
            WPSCapabilities capabilities) throws XMLStreamException {

        return new DescribeProcessResponseDecoder().readElement(XMLInputFactory.newInstance().createXMLEventReader(new InputStreamReader(in)));
    }

    private List<Process> createProcessDescriptionArray(
            ProcessDescriptionsDocument doc,
            WPSCapabilities capabilities) {

        return null;
    }

    private String retrieveDataViaPOST(Object executeObject,
            String urlString) throws WPSClientException {
        try {
            URL url = new URL(urlString);
            
            String content = "";
            
            if(executeObject instanceof XMLObject){
                content = ((XmlObject)executeObject).xmlText();
            }else if(executeObject instanceof ByteArrayOutputStream){
                content = ((ByteArrayOutputStream)executeObject).toString();
            }
            
            return retrieveResponseOrExceptionReportInpustream(url, content);
        } catch (MalformedURLException e) {
            throw new WPSClientException("URL seems to be invalid: " + urlString, e);
        } catch (IOException e) {
            throw new WPSClientException("Error while transmission", e);
        }
    }

    private Object checkInputStream(String responseString) throws WPSClientException {
        try {

            LOGGER.trace("Got response:" + responseString);

            XmlObject parsedXmlObject = XmlObject.Factory.parse(responseString, options);

            String exceptionText = "";
            boolean isException = false;

            if (parsedXmlObject instanceof net.opengis.ows.x11.ExceptionReportDocument) {
                net.opengis.ows.x11.ExceptionReportDocument exceptionDoc =
                        (net.opengis.ows.x11.ExceptionReportDocument) parsedXmlObject;
                exceptionText = exceptionDoc.xmlText(options);
                isException = true;

            } 
//            else if (parsedXmlObject instanceof net.opengis.ows.x20.ExceptionReportDocument) {
//                net.opengis.ows.x20.ExceptionReportDocument exceptionDoc =
//                        (net.opengis.ows.x20.ExceptionReportDocument) parsedXmlObject;
//                exceptionText = exceptionDoc.xmlText(options);
//                isException = true;
//            }

            if (isException) {
                LOGGER.error("Received ExceptionReport from WPS.");
                LOGGER.trace(exceptionText);
//                throw new WPSClientException("Error occurred while executing query: ", exceptionText);
            }
            
            if(parsedXmlObject.getDomNode().getFirstChild().getNamespaceURI().equals("http://www.opengis.net/wps/1.0.0")){
                return parsedXmlObject;
            }else {
                return new ByteArrayInputStream(responseString.getBytes());
            }

//            return parsedXmlObject;
        } catch (XmlException e) {
            throw new WPSClientException("Error while parsing input.", e);
        }
    }

    /**
     * either an ExecuteResponseDocument or an InputStream if asked for RawData
     * or an Exception Report
     *
     * @param url
     * @param executeObject
     * @param rawData
     * @param requestAsync
     * @return The execute response
     * @throws WPSClientException
     * @throws IOException
     */
    private Object retrieveExecuteResponseViaPOST(String url,
            Object executeObject,
            boolean rawData, boolean requestAsync) throws WPSClientException, IOException {

        String responseString = retrieveDataViaPOST(executeObject, url);

        if (rawData && !requestAsync) {
            return responseString;
        }

        Object resultObj = checkInputStream(responseString);

        if (resultObj instanceof ExecuteResponseDocument) {

            if(requestAsync){
                return getAsyncDoc(url, resultObj);
            }

            return (ExecuteResponseDocument) resultObj;
        } 
//        else if (resultObj instanceof StatusInfoDocument) {//TODO
//            return getAsyncDoc(url, resultObj);
//        } else if (resultObj instanceof ResultDocument) {
//            return (ResultDocument) resultObj;
//        }
        return resultObj;
    }

    private String createGetStatusURLWPS20(String url, String jobID) throws MalformedURLException{

        String urlSpec = url.endsWith("?") ? url : url.concat("?");

        urlSpec = urlSpec.concat("service=WPS&version=2.0.0&request=GetStatus&jobID=" + jobID);

        return urlSpec;

    }

    private String createGetResultURLWPS20(String url, String jobID) throws MalformedURLException{

        String urlSpec = url.endsWith("?") ? url : url.concat("?");

        urlSpec = urlSpec.concat("service=WPS&version=2.0.0&request=GetResult&jobID=" + jobID);

        return urlSpec;

    }

    private XmlObject getAsyncDoc(String url, Object responseObject) throws IOException, WPSClientException {

        String getStatusURL = "";
        boolean processSuceeded = false;
        boolean processFailed = false;

        if (responseObject instanceof ExecuteResponseDocument) {
            ExecuteResponseDocument executeResponseDocument = (ExecuteResponseDocument) responseObject;

            processSuceeded = executeResponseDocument.getExecuteResponse().getStatus().isSetProcessSucceeded();

            if(processSuceeded){
                return executeResponseDocument;
            }

            processFailed = executeResponseDocument.getExecuteResponse().getStatus().isSetProcessSucceeded();

            if(processFailed){
                return executeResponseDocument.getExecuteResponse().getStatus().getProcessFailed().getExceptionReport();
            }

            getStatusURL = executeResponseDocument.getExecuteResponse().getStatusLocation();

        } //TODO
//        else if (responseObject instanceof StatusInfoDocument) {
//
//            StatusInfoDocument statusInfoDocument = (StatusInfoDocument) responseObject;
//            String jobID = statusInfoDocument.getStatusInfo().getJobID();
//            processSuceeded = statusInfoDocument.getStatusInfo().getStatus().equals("Succeeded");
//            processFailed = statusInfoDocument.getStatusInfo().getStatus().equals("Failed");
//
//            // if succeeded, return result, otherwise GetResult operation will return ExceptionReport
//            if(processSuceeded || processFailed){
//
//                String getResultURL = createGetResultURLWPS20(url, jobID);
//
//                return checkInputStream(retrieveResponseOrExceptionReportInpustream(new URL(getResultURL)));
//            }
//
//            getStatusURL = createGetStatusURLWPS20(url, jobID);
//
//        }

        if(isCancel()){
            LOGGER.info("Asynchronous Execute operation canceled.");
            return XmlObject.Factory.newInstance();//TODO
        }

        //assume process is still running, pause configured amount of time
        try {
            LOGGER.info("Let Thread sleep millis: " + delayForAsyncRequests);
            Thread.sleep(delayForAsyncRequests);
        } catch (InterruptedException e) {
            LOGGER.error("Could not let Thread sleep millis: " + delayForAsyncRequests, e);
        }

        return getAsyncDoc(url, checkInputStream(retrieveResponseOrExceptionReportInpustream(new URL(getStatusURL))));
    }

    private void loadProperties() {

        String fileName = "wps-client.properties";
        String defaultFileName = "wps-client-default.properties";

        Optional<JsonNode> propertyNodeOptional = Optional.empty();

        URL fileURL = null;
        // always check propertyFilename first
        fileURL = locateFile(fileName);
        // check if the strategies found something
        if (fileURL != null) {
            try {
                propertyNodeOptional = Optional.of(Json.loadURL(fileURL));
            } catch (IOException e) {
                LOGGER.error("Could not read property file.", e);
            }
        }
        fileURL = locateFile(defaultFileName);
        try {
            propertyNodeOptional = Optional.of(Json.loadURL(fileURL));
        } catch (IOException e) {
            LOGGER.error("Could not read  default property file.", e);
        }

        if(propertyNodeOptional.isPresent()){
            JsonNode propertyNode = propertyNodeOptional.get();
            setProperties(propertyNode);
        }
    }

    private URL locateFile(String fileName) {
        FileLocationStrategy strategy = new CombinedLocationStrategy(
                Arrays.asList(new FileSystemLocationStrategy(), new ClasspathLocationStrategy()));
        FileSystem fileSystem = new DefaultFileSystem();
        FileLocator locator = FileLocatorUtils.fileLocator().locationStrategy(strategy).fileName(fileName).create();
        return strategy.locate(fileSystem, locator);
    }

    private void setProperties(JsonNode propertyNode) {

        JsonNode settingsNode = propertyNode.get("settings");

        if (settingsNode == null) {
            LOGGER.info("Properties JSON malformed.");
            return;
        }

        JsonNode maxNumberOfAsyncRequestsNode = settingsNode.get("maxNumberOfAsyncRequests");

        if (maxNumberOfAsyncRequestsNode != null) {
            JsonNode valueNode = maxNumberOfAsyncRequestsNode.get("value");
            if (valueNode == null) {
                LOGGER.info("Properties JSON malformed.");
            }else{
                maxNumberOfAsyncRequests = valueNode.asInt();
            }
        } else {
            LOGGER.info("Property maxNumberOfAsyncRequests not present, defaulting to: " + maxNumberOfAsyncRequests);
        }

        JsonNode delayForAsyncRequestsNode = settingsNode.get("delayForAsyncRequests");

        if (delayForAsyncRequestsNode != null) {
            JsonNode valueNode = delayForAsyncRequestsNode.get("value");
            if (valueNode == null) {
                LOGGER.info("Properties JSON malformed.");
            }else{
                delayForAsyncRequests = valueNode.asInt();
            }
        } else {
            LOGGER.info("Property delayForAsyncRequests not present, defaulting to: " + delayForAsyncRequests);
        }
    }
}
