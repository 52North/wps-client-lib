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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.n52.geoprocessing.wps.client.model.Process;
import org.n52.geoprocessing.wps.client.model.ResponseMode;
import org.n52.geoprocessing.wps.client.model.WPSCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.opengis.wps.x100.ExecuteDocument;
import net.opengis.wps.x100.ExecuteDocument.Execute;
import net.opengis.wps.x100.ExecuteResponseDocument;
import net.opengis.wps.x100.ProcessDescriptionsDocument;
import net.opengis.wps.x20.GetResultDocument;
import net.opengis.wps.x20.GetStatusDocument;
import net.opengis.wps.x20.ProcessOfferingDocument.ProcessOffering;
import net.opengis.wps.x20.ProcessOfferingsDocument;

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

    public static final String VERSION_100 = "1.0.0";

    public static final String VERSION_200 = "2.0.0";

    // a Map of <url, all available process descriptions>
    private Map<String, List<Process>> processDescriptions;

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
        // ProcessDescriptionsDocument processDescs = describeAllProcesses(url);
        // if(processDescs != null && capsDoc != null) {
        // processDescriptions.put(url, processDescs);
        // return true;
        // }
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
     * returns the cached processdescriptions of a service.
     *
     * @param serverID
     * @return success
     * @throws IOException
     */
    private List<Process> getProcessDescriptionsFromCache(String wpsUrl)
            throws IOException {
        // if(! descriptionsAvailableInCache(wpsUrl)) {
        // try{
        // connect(wpsUrl);
        // }
        // catch(WPSClientException e) {
        // throw new IOException("Could not initialize WPS " + wpsUrl);
        // }
        // }
        return loggedServices.get(wpsUrl).getProcesses();
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
                        describeProcess(new String[] { processID }, serverID, version);
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
     * @return either an ExecuteResponseDocument or an InputStream if asked for
     *         RawData or an Exception Report
     */
    private Object execute(String serverID,
            String url,
            XmlObject execute,
            boolean rawData) throws WPSClientException {
        return retrieveExecuteResponseViaPOST(url, execute, rawData);
    }

    /**
     * Executes a process at a WPS
     *
     * @param url
     *            url of server not the entry additionally defined in the caps.
     * @param execute
     *            Execute document
     * @return either an ExecuteResponseDocument or an InputStream if asked for
     *         RawData or an Exception Report
     */
    public Object execute(String serverID,
            String url,
            ExecuteDocument execute, String version) throws WPSClientException {
        if (execute.getExecute().isSetResponseForm() == true && execute.getExecute().isSetResponseForm() == true
                && execute.getExecute().getResponseForm().isSetRawDataOutput() == true) {
            return execute(serverID, url, execute, true);
        } else {
            return execute(serverID, url, execute, false);
        }

    }
    public Object execute(String serverID,
            String url,
            org.n52.geoprocessing.wps.client.model.execution.Execute execute, String version) throws WPSClientException {

        XmlObject executeObject = encode(execute, version);

        if (execute.getResponseMode() == ResponseMode.RAW) {
            return execute(serverID, url, executeObject, true);
        } else {
            return execute(serverID, url, executeObject, false);
        }

    }

    private XmlObject encode(org.n52.geoprocessing.wps.client.model.execution.Execute execute, String version) {

        XmlObject executeObject = XmlObject.Factory.newInstance();

        switch (version) {
        case VERSION_100:

            break;

        case VERSION_200:

            break;

        default:
            break;
        }

        return executeObject;
    }

    private WPSCapabilities retrieveCapsViaGET(String url,
            String version) throws WPSClientException {
        ClientCapabiltiesRequest req = new ClientCapabiltiesRequest(version);
        url = req.getRequest(url);
        try {
            URL urlObj = new URL(url);
            urlObj.getContent();
            InputStream is = urlObj.openStream();
            XmlObject xmlObject = checkInputStream(is);
            return createWPSCapabilities(xmlObject);
        } catch (MalformedURLException e) {
            throw new WPSClientException("Capabilities URL seems to be unvalid: " + url, e);
        } catch (IOException e) {
            throw new WPSClientException("Error occured while retrieving capabilities from url: " + url, e);
        }
    }

    private WPSCapabilities createWPSCapabilities(XmlObject xmlObject) {

        if (xmlObject instanceof net.opengis.wps.x100.CapabilitiesDocument) {
            return createWPSCapabilitiesOWS11((net.opengis.wps.x100.CapabilitiesDocument) xmlObject);
        } else if (xmlObject instanceof net.opengis.wps.x20.CapabilitiesDocument) {
            return createWPSCapabilitiesOWS20((net.opengis.wps.x20.CapabilitiesDocument) xmlObject);
        }

        return new WPSCapabilities();
    }

    private WPSCapabilities createWPSCapabilitiesOWS20(net.opengis.wps.x20.CapabilitiesDocument xmlObject) {
        return new WPS20CapabilitiesParser().createWPSCapabilitiesOWS20(xmlObject);
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
            InputStream is = urlObj.openStream();
            XmlObject doc = checkInputStream(is);

            WPSCapabilities capabilities = getWPSCaps(url);

            if (doc instanceof ProcessDescriptionsDocument) {
                return createProcessDescriptionArray((ProcessDescriptionsDocument) doc, capabilities);
            } else if (doc instanceof ProcessOfferingsDocument) {
                return createProcessDescriptionArray((ProcessOfferingsDocument) doc, capabilities);
            }
        } catch (MalformedURLException e) {
            throw new WPSClientException("URL seems not to be valid: " + url, e);
        } catch (IOException e) {
            throw new WPSClientException("Error occured while receiving data", e);
        }
        LOGGER.info("No valid ProcessDescription found. Returning empty list.");
        return new ArrayList<Process>();
    }

    private List<Process> createProcessDescriptionArray(
            ProcessOfferingsDocument doc,
            WPSCapabilities capabilities) {

        List<Process> processes = new ArrayList<>();

        ProcessOffering[] processOfferings = doc.getProcessOfferings().getProcessOfferingArray();

        for (ProcessOffering processOffering : processOfferings) {

            String id = processOffering.getProcess().getIdentifier().getStringValue();

            Process process = capabilities.getProcess(id);

            processes.add(WPS20ProcessParser.completeProcess(processOffering, process));
        }

        return processes;
    }

    private List<Process> createProcessDescriptionArray(
            ProcessDescriptionsDocument doc,
            WPSCapabilities capabilities) {

        return null;
    }

    private InputStream retrieveDataViaPOST(XmlObject obj,
            String urlString) throws WPSClientException {
        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Accept-Encoding", "gzip");
            conn.setRequestProperty("Content-Type", "text/xml");
            conn.setDoOutput(true);
            obj.save(conn.getOutputStream());
            InputStream input = null;
            String encoding = conn.getContentEncoding();
            if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
                input = new GZIPInputStream(conn.getInputStream());
            } else {
                input = conn.getInputStream();
            }
            return input;
        } catch (MalformedURLException e) {
            throw new WPSClientException("URL seems to be unvalid", e);
        } catch (IOException e) {
            throw new WPSClientException("Error while transmission", e);
        }
    }

    private XmlObject checkInputStream(InputStream is) throws WPSClientException {
        try {
            XmlObject parsedXmlObject = XmlObject.Factory.parse(is);

            String exceptionText = "";
            boolean isException = false;

            if (parsedXmlObject instanceof net.opengis.ows.x11.ExceptionReportDocument) {
                net.opengis.ows.x11.ExceptionReportDocument exceptionDoc =
                        (net.opengis.ows.x11.ExceptionReportDocument) parsedXmlObject;
                exceptionText = exceptionDoc.xmlText(options);

            } else if (parsedXmlObject instanceof net.opengis.ows.x20.ExceptionReportDocument) {
                net.opengis.ows.x20.ExceptionReportDocument exceptionDoc =
                        (net.opengis.ows.x20.ExceptionReportDocument) parsedXmlObject;
                exceptionText = exceptionDoc.xmlText(options);
            }

            if (isException) {
                LOGGER.debug(exceptionText);
                throw new WPSClientException("Error occured while executing query: ", exceptionText);
            }

            return parsedXmlObject;
        } catch (XmlException e) {
            throw new WPSClientException("Error while parsing input.", e);
        } catch (IOException e) {
            throw new WPSClientException("Error occured while transfer", e);
        }
    }

    /**
     * either an ExecuteResponseDocument or an InputStream if asked for RawData
     * or an Exception Report
     *
     * @param url
     * @param execute
     * @param rawData
     * @return The execute response
     * @throws WPSClientException
     */
    private Object retrieveExecuteResponseViaPOST(String url,
            XmlObject execute,
            boolean rawData) throws WPSClientException {
        InputStream is = retrieveDataViaPOST(execute, url);
        if (rawData) {
            return is;
        }
        XmlObject resultObj = checkInputStream(is);

        if (resultObj instanceof ExecuteResponseDocument) {
            return (ExecuteResponseDocument) resultObj;
        } else if (resultObj instanceof GetStatusDocument) {
            return (GetStatusDocument) resultObj;
        } else if (resultObj instanceof GetResultDocument) {
            return (GetResultDocument) resultObj;
        }
        return resultObj;
    }

    public String[] getProcessNames(String url) throws IOException {
        List<Process> processes = getProcessDescriptionsFromCache(url);
        String[] processNames = new String[processes.size()];
        for (int i = 0; i < processNames.length; i++) {
            processNames[i] = processes.get(i).getId();
        }
        return processNames;
    }
}
