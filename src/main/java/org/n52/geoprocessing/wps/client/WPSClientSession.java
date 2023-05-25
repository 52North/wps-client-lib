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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.stream.XMLEventReader;
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
import org.apache.xmlbeans.XmlObject;
import org.n52.geoprocessing.wps.client.encoder.stream.ExecuteRequest100Encoder;
import org.n52.geoprocessing.wps.client.encoder.stream.ExecuteRequest20Encoder;
import org.n52.geoprocessing.wps.client.model.Process;
import org.n52.geoprocessing.wps.client.model.ResponseMode;
import org.n52.geoprocessing.wps.client.model.StatusInfo;
import org.n52.geoprocessing.wps.client.model.WPSCapabilities;
import org.n52.geoprocessing.wps.client.model.execution.ExecutionMode;
import org.n52.geoprocessing.wps.client.xml.WPSResponseReader;
import org.n52.janmayen.Json;
import org.n52.shetland.ogc.wps.JobStatus;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.stream.xml.ElementXmlStreamWriterRepository;
import org.n52.svalbard.encode.stream.xml.XmlStreamWritingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import org.n52.geoprocessing.wps.client.model.Result;

/**
 * Contains some convenient methods to access and manage Web Processing Services
 * in a very generic way.
 *
 * This is implemented as a singleton.
 *
 * @author bpross,foerster
 */

public class WPSClientSession {

    public static final String VERSION_100 = "1.0.0";

    public static final String VERSION_200 = "2.0.0";

    public static final String SERVICE = "WPS";

    private static final String AUTHORIZATION = "Authorization";

    private static final String GOT_HTTP_ERROR = "Got HTTP error code, response: ";

    private static Logger LOGGER = LoggerFactory.getLogger(WPSClientSession.class);

    private static WPSClientSession session;

    private static int maxNumberOfAsyncRequests = 100;

    private static int delayForAsyncRequests = 1000;

    private Map<String, WPSCapabilities> loggedServices;

    private boolean cancel;

    // a Map of <url, all available process descriptions>
    private Map<String, List<Process>> processDescriptions;

    private CloseableHttpClient httpClient;

    private HttpClientBuilder httpClientBuilder;

    private String bearerToken = "";

    private boolean useBearerToken;

    /**
     * Initializes a WPS client session.
     *
     */
    public WPSClientSession() {
        loggedServices = new HashMap<String, WPSCapabilities>();
        processDescriptions = new HashMap<String, List<Process>>();
        httpClientBuilder = HttpClientBuilder.create();
        httpClient = httpClientBuilder.build();
        loadProperties();
    }

    /*
     * @result An instance of a WPS Client session.
     */
    public static WPSClientSession getInstance() {
        synchronized (WPSClientSession.class) {
            if (session == null) {
                session = new WPSClientSession();
            }
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
     * @param version
     *            the version of the WPS
     * @return true, if connect succeeded, false else.
     * @throws WPSClientException
     *             if the capabilities could not be requested
     */
    public boolean connect(String url,
            String version) throws WPSClientException {
        LOGGER.info("CONNECT");
        if (loggedServices.containsKey(url)) {
            LOGGER.info("Service already registered: " + url);
            return false;
        }
        WPSCapabilities capsDoc = retrieveCapsViaGET(url, version);
        loggedServices.put(url, capsDoc);
        return true;
    }

    /**
     * removes a service from the session
     *
     * @param url
     *            WPS URL
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
     * @return List of logged service URLs
     */
    public List<String> getLoggedServices() {
        return new ArrayList<String>(loggedServices.keySet());
    }

    /**
     * informs you if the descriptions for the specified service is already in
     * the session. in normal case it should return true :)
     *
     * @param serverID
     *            WPS URL
     * @return success true if the the process descriptions of the WPS are
     *         cached
     */
    public boolean descriptionsAvailableInCache(String serverID) {
        return processDescriptions.containsKey(serverID);
    }

    /**
     * return the processDescription for a specific process from Cache.
     *
     * @param serverID
     *            WPS URL
     * @param processID
     *            id of the process
     * @param version
     *            the version of the WPS
     * @return a ProcessDescription for a specific process from Cache.
     */
    public Process getProcessDescription(String serverID,
            String processID,
            String version) {
        List<Process> processes = getProcessDescriptionsFromCache(serverID);
        for (Process process : processes) {
            if (process.getId().equals(processID)) {
                if (process.getInputs() == null || process.getInputs().isEmpty()) {
                    try {
                        return describeProcess(new String[] { processID }, serverID, version).get(0);
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
     */
    public List<Process> getAllProcessDescriptions(String wpsUrl) {
        return getProcessDescriptionsFromCache(wpsUrl);
    }

    /**
     * looks up, if the service exists already in session.
     *
     * @param serverID
     *            the URL of the WPS
     * @return true if the service exists in this session
     */
    public boolean serviceAlreadyRegistered(String serverID) {
        return loggedServices.containsKey(serverID);
    }

    /**
     * provides you the cached capabilities for a specified service.
     *
     * @param url
     *            WPS URL
     * @return WPSCapabilities object
     */
    public WPSCapabilities getWPSCaps(String url) {
        return loggedServices.get(url);
    }

    /**
     * retrieves the desired description for a service. the retrieved
     * information will not be held in cache!
     *
     * @param processIDs
     *            one or more process IDs
     * @param serverID
     *            WPS URL
     * @param version
     *            WPS version
     * @throws WPSClientException
     *             of the process description could not be requested
     * @return list of process objects
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
     * @param version
     *            the version of the WPS
     *
     * @return either an ExecuteResponseDocument or an InputStream if asked for
     *         RawData or an Exception Report
     * @throws WPSClientException
     *             if the initial execute request failed
     * @throws IOException
     *             if subsequent requests failed in async mode
     */
    public Object executeAsyncGetResponseImmediately(String url,
            org.n52.geoprocessing.wps.client.model.execution.Execute execute,
            String version) throws WPSClientException, IOException {

        Object executeObject = encode(execute, version);

        return retrieveAsyncExecuteResponseViaPOSTImmediately(url, executeObject);
    }

    /**
     * Executes a process at a WPS
     *
     * @param url
     *            url of server not the entry additionally defined in the caps.
     * @param execute
     *            Execute document
     * @param version
     *            the version of the WPS
     *
     * @return either an ExecuteResponseDocument or an InputStream if asked for
     *         RawData or an Exception Report
     * @throws WPSClientException
     *             if the initial execute request failed
     * @throws IOException
     *             if subsequent requests failed in async mode
     */
    public Object execute(String url,
            org.n52.geoprocessing.wps.client.model.execution.Execute execute,
            String version) throws WPSClientException, IOException {

        boolean requestRawData = execute.getResponseMode() == ResponseMode.RAW;
        boolean requestAsync = execute.getExecutionMode() == ExecutionMode.ASYNC;
        // TODO: what about AUTO mode?

        Object executeObject = encode(execute, version);

        return execute(url, executeObject, requestRawData, requestAsync);
    }

    private Object execute(String url,
            Object executeObject,
            boolean rawData,
            boolean requestAsync) throws WPSClientException, IOException {
        return retrieveExecuteResponseViaPOST(url, executeObject, rawData, requestAsync);
    }

    public String[] getProcessNames(String url) throws IOException {
        List<Process> processes = getProcessDescriptionsFromCache(url);
        String[] processNames = new String[processes.size()];
        for (int i = 0; i < processNames.length; i++) {
            processNames[i] = processes.get(i).getId();
        }
        return processNames;
    }

    public void cancelAsyncExecute() {
        setCancel(true);
    }

    public int checkService(String url,
            String payload) {

        String ioException = "IOException while trying to access: ";

        CloseableHttpResponse response = null;

        if (payload == null || payload.isEmpty()) {

            HttpGet get = new HttpGet(url);

            try {

                response = httpClient.execute(get);
            } catch (IOException e) {
                LOGGER.error(ioException + url);
            }
        } else {

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
                LOGGER.error(ioException + url);
            }
        }

        int result = -1;

        if (response != null) {
            result = response.getStatusLine().getStatusCode();

            try {
                response.close();
            } catch (IOException e) {
                LOGGER.error("Could not close HTTPResponse ", e);
            }
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

    public Result retrieveProcessResult(String url,
            String jobId) throws IOException, WPSClientException {
        try {
            String targetUrl = this.createGetResultURLWPS20(url, jobId);
            Object result = retrieveResponseOrExceptionReportInpustream(new URL(targetUrl));

            if (result != null && result instanceof Result) {
                return (Result) result;
            }

            throw new WPSClientException("Invalid response from WPS: " + result);
        } catch (MalformedURLException ex) {
            throw new IOException(ex.getMessage(), ex);
        }
    }

    /**
     * Get the result from a status location URL
     *
     * @param statusLocation
     *            The status location URL
     *
     * @return the result object
     * @throws MalformedURLException
     *             if the status location URL is malformed
     * @throws WPSClientException
     *             if an exception concerning WPS communication occurred
     * @throws IOException
     *             if en exception during the general HTTPVcommunication
     *             occurred
     */
    public Object getResultFromStatusLocation(String statusLocation)
            throws MalformedURLException, WPSClientException, IOException {

        Object statusInfoObject = retrieveResponseOrExceptionReportInpustream(new URL(statusLocation));

        String baseURL = statusLocation.substring(0, statusLocation.indexOf("?"));

        return getAsyncDoc(baseURL, statusInfoObject);
    }

    private synchronized boolean isCancel() {
        return cancel;
    }

    private synchronized void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    private List<Process> getProcessDescriptionsFromCache(String wpsUrl) {
        return loggedServices.get(wpsUrl).getProcesses();
    }

    private OutputStream encode(org.n52.geoprocessing.wps.client.model.execution.Execute execute,
            String version) {

        OutputStream out = new ByteArrayOutputStream();

        switch (version) {
        case VERSION_100:

            ExecuteRequest100Encoder executeRequestWriter = new ExecuteRequest100Encoder();

            try {
                executeRequestWriter.setContext(new XmlStreamWritingContext(out,
                        new ElementXmlStreamWriterRepository(Arrays.asList(ExecuteRequest100Encoder::new))::get));
                executeRequestWriter.writeElement(execute);
            } catch (EncodingException | XMLStreamException e) {
                LOGGER.error(e.getMessage());
            }

            return out;

        case VERSION_200:

            ExecuteRequest20Encoder executeRequestWriter20 = new ExecuteRequest20Encoder();

            try {
                executeRequestWriter20.setContext(new XmlStreamWritingContext(out,
                        new ElementXmlStreamWriterRepository(Arrays.asList(ExecuteRequest20Encoder::new))::get));
                executeRequestWriter20.writeElement(execute);
            } catch (EncodingException | XMLStreamException e) {
                LOGGER.error(e.getMessage());
            }

            return out;

        default:
            return out;
        }
    }

    private WPSCapabilities retrieveCapsViaGET(String url,
            String version) throws WPSClientException {
        ClientCapabiltiesRequest req = new ClientCapabiltiesRequest(version);
        String getRequestURL = req.getRequest(url);
        try {
            URL urlObj = new URL(getRequestURL);
            Object responseObject = retrieveResponseOrExceptionReportInpustream(urlObj);
            if (responseObject instanceof WPSCapabilities) {
                return (WPSCapabilities) responseObject;
            } else {
                throw new WPSClientException("Did not get (valid) capabilities, got: " + responseObject);
            }
        } catch (MalformedURLException e) {
            throw new WPSClientException("Capabilities URL seems to be unvalid: " + url, e);
        } catch (IOException e) {
            throw new WPSClientException("Error occured while retrieving capabilities from url: " + url, e);
        }
    }

    private Object retrieveResponseOrExceptionReportInpustream(URL url) throws WPSClientException, IOException {

        HttpGet get = new HttpGet(url.toString());

        if (isUseBearerToken()) {
            get.addHeader(AUTHORIZATION, getBearerToken());
        }

        try(CloseableHttpResponse response = httpClient.execute(get)){
            Object responseObject = parseInputStreamToString(response.getEntity().getContent());

            try {
                checkStatusCode(response);
            } catch (Exception e) {
                throw new WPSClientException(GOT_HTTP_ERROR + responseObject);
            }
            return responseObject;
        }
    }

    private Object retrieveResponseOrExceptionReportInpustream(URL url,
            String executeObject) throws WPSClientException, IOException {

        HttpPost post = new HttpPost(url.toString());

        post.addHeader("Accept-Encoding", "gzip");
        post.addHeader("Content-Type", "text/xml");

        if (isUseBearerToken()) {
            post.addHeader(AUTHORIZATION, getBearerToken());
        }

        post.setEntity(new StringEntity(executeObject));

       try(CloseableHttpResponse response = httpClient.execute(post)){
            Object responseObject = parseInputStreamToString(response.getEntity().getContent());

            try {
                checkStatusCode(response);
            } catch (Exception e) {
                throw new WPSClientException(GOT_HTTP_ERROR + responseObject);
            }

            return responseObject;
       }
    }

    private void checkStatusCode(CloseableHttpResponse response) throws WPSClientException {

        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode >= 400) {
            throw new WPSClientException("Got HTTP error code: " + statusCode);
        }

    }

    private Object parseInputStreamToString(InputStream in) throws IOException, WPSClientException {

        XMLEventReader xmlReader = null;
        try {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            xmlInputFactory.setProperty("javax.xml.stream.supportDTD", false);
            xmlReader = xmlInputFactory.createXMLEventReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            return new WPSResponseReader().readElement(xmlReader);
        } catch (XMLStreamException e) {
            throw new WPSClientException("Could not decode Inputstream.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Process> retrieveDescriptionViaGET(String[] processIDs,
            String url,
            String version) throws WPSClientException {
        ClientDescribeProcessRequest req = new ClientDescribeProcessRequest(version);
        req.setIdentifier(processIDs);
        String requestURL = req.getRequest(url);
        try {
            URL urlObj = new URL(requestURL);
            Object responseObject = retrieveResponseOrExceptionReportInpustream(urlObj);

            if (responseObject instanceof List) {

                return (List<Process>) responseObject;
            }
        } catch (MalformedURLException e) {
            throw new WPSClientException("URL seems not to be valid: " + url, e);
        } catch (IOException e) {
            throw new WPSClientException("Error occured while receiving data", e);
        }
        LOGGER.info("No valid ProcessDescription found. Returning empty list.");
        return new ArrayList<Process>();
    }

    private Object retrieveDataViaPOST(Object executeObject,
            String urlString) throws WPSClientException {
        try {
            URL url = new URL(urlString);

            String content = "";

            if (executeObject instanceof ByteArrayOutputStream) {
                content = ((ByteArrayOutputStream) executeObject).toString(StandardCharsets.UTF_8.name());
            }

            return retrieveResponseOrExceptionReportInpustream(url, content);
        } catch (MalformedURLException e) {
            throw new WPSClientException("URL seems to be invalid: " + urlString, e);
        } catch (IOException e) {
            throw new WPSClientException("Error while transmission", e);
        }
    }

    /**
     * either an ExecuteResponseDocument or an InputStream if asked for RawData
     * or an Exception Report
     *
     * @param url
     *            WPS url
     * @param executeObject
     *            encoded execute request
     * @param rawData
     *            indicates if raw data should be requested
     * @param requestAsync
     *            indicates if request should be async
     * @return The execute response
     * @throws WPSClientException
     *             if the initial execute request failed
     * @throws IOException
     *             if subsequent requests failed in async mode
     */
    private Object retrieveExecuteResponseViaPOST(String url,
            Object executeObject,
            boolean rawData,
            boolean requestAsync) throws WPSClientException, IOException {

        Object responseObject = retrieveDataViaPOST(executeObject, url);

        if (rawData && !requestAsync) {
            return responseObject;
        }

        if (responseObject instanceof StatusInfo) {

            if (requestAsync) {
                return getAsyncDoc(url, responseObject);
            }

            return (StatusInfo) responseObject;
        }
        // TODO when does this happen?!
        return responseObject;
    }

    /**
     * Retrieves an StatusInfoDocument
     *
     * @param url
     *            WPS url
     * @param executeObject
     *            encoded execute request
     * @throws WPSClientException
     *             if the initial execute request failed
     * @throws IOException
     *             if subsequent requests failed in async mode
     */
    private Object retrieveAsyncExecuteResponseViaPOSTImmediately(String url,
            Object executeObject) throws WPSClientException, IOException {

        Object responseObject = retrieveDataViaPOST(executeObject, url);

        if (!(responseObject instanceof StatusInfo)) {
            throw new WPSClientException("Async response is not an StatusInfo, instead: " + responseObject.getClass());
        }

        StatusInfo statusInfoDocument = (StatusInfo) responseObject;

        String statusLocation = statusInfoDocument.getStatusLocation();
        String jobID = statusInfoDocument.getJobId();

        if (statusLocation == null || statusLocation.isEmpty()) {
            String getStatusURL = createGetStatusURLWPS20(url, jobID);
            statusInfoDocument.setStatusLocation(getStatusURL);
        }

        return statusInfoDocument;
    }

    private String createGetStatusURLWPS20(String url,
            String jobID) throws MalformedURLException {

        String urlSpec = url.endsWith("?") ? url : url.concat("?");

        urlSpec = urlSpec.concat("service=WPS&version=2.0.0&request=GetStatus&jobID=" + jobID);

        return urlSpec;

    }

    private String createGetResultURLWPS20(String url,
            String jobID) throws MalformedURLException {

        String urlSpec = url.endsWith("?") ? url : url.concat("?");

        urlSpec = urlSpec.concat("service=WPS&version=2.0.0&request=GetResult&jobID=" + jobID);

        return urlSpec;

    }

    private Object getAsyncDoc(String url,
            Object responseObject) throws IOException, WPSClientException {

        String getStatusURL = "";
        boolean processSuceeded = false;
        boolean processFailed = false;

        if (responseObject instanceof StatusInfo) {

            StatusInfo statusInfoDocument = (StatusInfo) responseObject;
            String jobID = statusInfoDocument.getJobId();
            processSuceeded = statusInfoDocument.getStatus().equals(JobStatus.succeeded());
            processFailed = statusInfoDocument.getStatus().equals(JobStatus.failed());

            // if succeeded, return result, otherwise GetResult operation will
            // return ExceptionReport
            if (processSuceeded || processFailed) {

                String getResultURL = statusInfoDocument.getStatusLocation();

                String statusLocation = statusInfoDocument.getStatusLocation();

                if (statusLocation == null || statusLocation.isEmpty()) {
                    getResultURL = createGetResultURLWPS20(url, jobID);
                }

                return retrieveResponseOrExceptionReportInpustream(new URL(getResultURL));
            }

            String statusLocation = statusInfoDocument.getStatusLocation();

            if (statusLocation != null && !(statusLocation.isEmpty())) {
                getStatusURL = statusLocation;
            } else {
                getStatusURL = createGetStatusURLWPS20(url, jobID);
            }
        }

        if (isCancel()) {
            LOGGER.info("Asynchronous Execute operation canceled.");
            // TODO
            return XmlObject.Factory.newInstance();
        }

        // assume process is still running, pause configured amount of time
        try {
            LOGGER.info("Let Thread sleep millis: " + delayForAsyncRequests);
            Thread.sleep(delayForAsyncRequests);
        } catch (InterruptedException e) {
            LOGGER.error("Could not let Thread sleep millis: " + delayForAsyncRequests, e);
        }

        return getAsyncDoc(url, retrieveResponseOrExceptionReportInpustream(new URL(getStatusURL)));
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

        if (propertyNodeOptional.isPresent()) {
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
        String jsonMalformed = "Properties JSON malformed.";
        String value = "value";

        if (settingsNode == null) {
            LOGGER.info(jsonMalformed);
            return;
        }

        JsonNode maxNumberOfAsyncRequestsNode = settingsNode.get("maxNumberOfAsyncRequests");

        if (maxNumberOfAsyncRequestsNode != null) {
            JsonNode valueNode = maxNumberOfAsyncRequestsNode.get(value);
            if (valueNode == null) {
                LOGGER.info(jsonMalformed);
            } else {
                maxNumberOfAsyncRequests = valueNode.asInt();
            }
        } else {
            LOGGER.info("Property maxNumberOfAsyncRequests not present, defaulting to: " + maxNumberOfAsyncRequests);
        }

        JsonNode delayForAsyncRequestsNode = settingsNode.get("delayForAsyncRequests");

        if (delayForAsyncRequestsNode != null) {
            JsonNode valueNode = delayForAsyncRequestsNode.get(value);
            if (valueNode == null) {
                LOGGER.info(jsonMalformed);
            } else {
                delayForAsyncRequests = valueNode.asInt();
            }
        } else {
            LOGGER.info("Property delayForAsyncRequests not present, defaulting to: " + delayForAsyncRequests);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if(this.httpClient != null){
            try{
                this.httpClient.close();
            }catch(IOException e){
                LOGGER.error("unable to close http client during garbage collection", e);
            }
        }
    }
}
