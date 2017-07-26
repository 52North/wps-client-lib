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
import org.n52.geoprocessing.wps.client.model.WPSCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.opengis.wps.x100.ExecuteDocument;
import net.opengis.wps.x100.ExecuteResponseDocument;
import net.opengis.wps.x100.ProcessDescriptionsDocument;
import net.opengis.wps.x20.GetResultDocument;
import net.opengis.wps.x20.GetStatusDocument;
import net.opengis.wps.x20.ProcessOfferingDocument.ProcessOffering;
import net.opengis.wps.x20.ProcessOfferingsDocument;

/**
 * Contains some convenient methods to access and manage Web Processing Services in a very
 * generic way.
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
	private String version;

	// a Map of <url, all available process descriptions>
	private Map<String, List<org.n52.geoprocessing.wps.client.model.Process>> processDescriptions;

	/**
	 * Initializes a WPS client session.
	 *
	 */
	private WPSClientSession() {
		options = new XmlOptions();
		options.setLoadStripWhitespace();
		options.setLoadTrimTextBuffer();
		loggedServices = new HashMap<String, WPSCapabilities>();
		processDescriptions = new HashMap<String, List<org.n52.geoprocessing.wps.client.model.Process>>();
	}

	/*
	 * @result An instance of a WPS Client session.
	 */
	public static WPSClientSession getInstance() {
		if(session == null) {
			session = new WPSClientSession();
		}
		return session;
	}
	/**
	 * This resets the WPSClientSession. This might be necessary, to get rid of old service entries/descriptions. However, the session has to be repopulated afterwards.
	 */
	public static void reset() {
		session = new WPSClientSession();
	}

	/**
	 * Connects to a WPS and retrieves Capabilities plus puts all available Descriptions into cache.
	 * @param url the entry point for the service. This is used as id for further identification of the service.
	 * @return true, if connect succeeded, false else.
	 * @throws WPSClientException
	 */
	public boolean connect(String url, String version) throws WPSClientException {
		LOGGER.info("CONNECT");
		this.version = version;
		if(loggedServices.containsKey(url)) {
			LOGGER.info("Service already registered: " + url);
			return false;
		}
		WPSCapabilities capsDoc = retrieveCapsViaGET(url, version);
		if(capsDoc != null) {
			loggedServices.put(url, capsDoc);
			return true;
		}
//		ProcessDescriptionsDocument processDescs = describeAllProcesses(url);
//		if(processDescs != null && capsDoc != null) {
//			processDescriptions.put(url, processDescs);
//			return true;
//		}
		LOGGER.warn("retrieving caps failed, caps are null");
		return false;
	}

	/**
	 * removes a service from the session
	 * @param url
	 */
	public void disconnect(String url) {
		if(loggedServices.containsKey(url)) {
			loggedServices.remove(url);
			processDescriptions.remove(url);
			LOGGER.info("service removed successfully: " + url);
		}
	}

	/**
	 * returns the serverIDs of all loggedServices
	 * @return
	 */
	public List<String> getLoggedServices() {
		return new ArrayList<String>(loggedServices.keySet());
	}

	/**
	 * informs you if the descriptions for the specified service is already in the session.
	 * in normal case it should return true :)
	 * @param serverID
	 * @return success
	 */
	public boolean descriptionsAvailableInCache(String serverID) {
		return processDescriptions.containsKey(serverID);
	}

	/**
	 * returns the cached processdescriptions of a service.
	 * @param serverID
	 * @return success
	 * @throws IOException
	 */
	private List<org.n52.geoprocessing.wps.client.model.Process> getProcessDescriptionsFromCache(String wpsUrl) throws IOException {
//		if(! descriptionsAvailableInCache(wpsUrl)) {
//			try{
//				connect(wpsUrl);
//			}
//			catch(WPSClientException e) {
//				throw new IOException("Could not initialize WPS " + wpsUrl);
//			}
//		}
		return loggedServices.get(wpsUrl).getProcesses();
	}



	/**
	 * return the processDescription for a specific process from Cache.
	 * @param serverID
	 * @param processID
	 * @return a ProcessDescription for a specific process from Cache.
	 * @throws IOException
	 */
	public org.n52.geoprocessing.wps.client.model.Process getProcessDescription(String serverID, String processID, String version) throws IOException {
		List<org.n52.geoprocessing.wps.client.model.Process> processes = getProcessDescriptionsFromCache(serverID);
		for(org.n52.geoprocessing.wps.client.model.Process process : processes) {
			if(process.getId().equals(processID)) {
				if(process.getInputs() == null || process.getInputs().isEmpty()){
					try {
						describeProcess(new String[]{processID}, serverID, version);
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
	 * @param wpsUrl the URL of the WPS
	 * @return An Array of ProcessDescriptions
	 * @throws IOException
	 */
	public List<org.n52.geoprocessing.wps.client.model.Process> getAllProcessDescriptions(String wpsUrl) throws IOException{
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
	 * @param url
	 * @return
	 */
	public WPSCapabilities getWPSCaps(String url) {
		return loggedServices.get(url);
	}

	/**
	 * retrieves the desired description for a service. the retrieved information will not be held in cache!
	 * @param processIDs one or more processIDs
	 * @param serverID
	 * @throws WPSClientException
	 */
	public List<org.n52.geoprocessing.wps.client.model.Process> describeProcess(String[] processIDs, String serverID, String version) throws WPSClientException {
//		WPSCapabilities caps = this.loggedServices.get(serverID);
//		Operation[] operations = caps.getCapabilities().getOperationsMetadata().getOperationArray();
//		String url = null;
//		for(Operation operation : operations){
//			if(operation.getName().equals("DescribeProcess")) {
//				url = operation.getDCPArray()[0].getHTTP().getGetArray()[0].getHref();
//			}
//		}
//		if(url == null) {
//			throw new WPSClientException("Missing DescribeOperation in Capabilities");
//		}
		return retrieveDescriptionViaGET(processIDs, serverID, version);
	}

	/**
	 * Executes a process at a WPS
	 *
	 * @param url url of server not the entry additionally defined in the caps.
	 * @param execute Execute document
	 * @return either an ExecuteResponseDocument or an InputStream if asked for RawData or an Exception Report
	 */
	private Object execute(String serverID, String url, ExecuteDocument execute, boolean rawData) throws WPSClientException{
		execute.getExecute().setVersion(version);
		return retrieveExecuteResponseViaPOST(url, execute,rawData);
	}

	/**
	 * Executes a process at a WPS
	 *
	 * @param url url of server not the entry additionally defined in the caps.
	 * @param execute Execute document
	 * @return either an ExecuteResponseDocument or an InputStream if asked for RawData or an Exception Report
	 */
	public Object execute(String serverID, String url, ExecuteDocument execute) throws WPSClientException{
		if(execute.getExecute().isSetResponseForm()==true && execute.getExecute().isSetResponseForm()==true && execute.getExecute().getResponseForm().isSetRawDataOutput()==true){
			return execute(serverID, url, execute,true);
		}else{
			return execute(serverID, url, execute,false);
		}

	}

	private WPSCapabilities retrieveCapsViaGET(String url, String version) throws WPSClientException {
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
		
		if(xmlObject instanceof net.opengis.wps.x100.CapabilitiesDocument){
			return createWPSCapabilitiesOWS11((net.opengis.wps.x100.CapabilitiesDocument)xmlObject);
		}else if(xmlObject instanceof net.opengis.wps.x20.CapabilitiesDocument){
			return createWPSCapabilitiesOWS20((net.opengis.wps.x20.CapabilitiesDocument)xmlObject);
		}
		
		return new WPSCapabilities();
	}

	private WPSCapabilities createWPSCapabilitiesOWS20(net.opengis.wps.x20.CapabilitiesDocument xmlObject) {
		return new WPS20CapabilitiesParser().createWPSCapabilitiesOWS20(xmlObject);
	}

	private WPSCapabilities createWPSCapabilitiesOWS11(net.opengis.wps.x100.CapabilitiesDocument xmlObject) {
		return new WPS100CapabilitiesParser().createWPSCapabilitiesOWS100(xmlObject);
	}

	private List<org.n52.geoprocessing.wps.client.model.Process> retrieveDescriptionViaGET(String[] processIDs, String url, String version) throws WPSClientException{
		ClientDescribeProcessRequest req = new ClientDescribeProcessRequest(version);
		req.setIdentifier(processIDs);
		String requestURL = req.getRequest(url);
		try {
			URL urlObj = new URL(requestURL);
			InputStream is = urlObj.openStream();
			XmlObject doc = checkInputStream(is);
			
			WPSCapabilities capabilities = getWPSCaps(url);
			
			if(doc instanceof ProcessDescriptionsDocument){
				return createProcessDescriptionArray((ProcessDescriptionsDocument)doc, capabilities);
			}else if(doc instanceof net.opengis.wps.x20.ProcessOfferingsDocument){
				return createProcessDescriptionArray((net.opengis.wps.x20.ProcessOfferingsDocument)doc, capabilities);
			}
		} catch (MalformedURLException e) {
			throw new WPSClientException("URL seems not to be valid: " + url, e);
		}
		catch (IOException e) {
			throw new WPSClientException("Error occured while receiving data", e);
		}
		LOGGER.info("No valid ProcessDescription found. Returning empty list.");
		return new ArrayList<org.n52.geoprocessing.wps.client.model.Process>();
	}

	private List<org.n52.geoprocessing.wps.client.model.Process> createProcessDescriptionArray(ProcessOfferingsDocument doc, WPSCapabilities capabilities) {

		List<org.n52.geoprocessing.wps.client.model.Process> processes = new ArrayList<>();
		
        ProcessOffering[] processOfferings = doc.getProcessOfferings().getProcessOfferingArray();
        
        for (ProcessOffering processOffering : processOfferings) {
    		
            String id = processOffering.getProcess().getIdentifier().getStringValue();
            
            Process process = capabilities.getProcess(id);
            		
			processes.add(WPS20ProcessParser.completeProcess(processOffering, process));
		}
		
		return processes;
	}

	private List<org.n52.geoprocessing.wps.client.model.Process> createProcessDescriptionArray(ProcessDescriptionsDocument doc, WPSCapabilities capabilities) {
		
		return null;
	}

	private InputStream retrieveDataViaPOST(XmlObject obj, String urlString) throws WPSClientException{
		try {
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("Accept-Encoding", "gzip");
			conn.setRequestProperty("Content-Type", "text/xml");
			conn.setDoOutput(true);
			obj.save(conn.getOutputStream());
			InputStream input = null;
			String encoding = conn.getContentEncoding();
			if(encoding != null && encoding.equalsIgnoreCase("gzip")) {
				input = new GZIPInputStream(conn.getInputStream());
			}
			else {
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
				net.opengis.ows.x11.ExceptionReportDocument exceptionDoc = (net.opengis.ows.x11.ExceptionReportDocument) parsedXmlObject;
				exceptionText = exceptionDoc.xmlText(options);

			} else if (parsedXmlObject instanceof net.opengis.ows.x20.ExceptionReportDocument) {
				net.opengis.ows.x20.ExceptionReportDocument exceptionDoc = (net.opengis.ows.x20.ExceptionReportDocument) parsedXmlObject;
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
	 * either an ExecuteResponseDocument or an InputStream if asked for RawData or an Exception Report
	 * @param url
	 * @param doc
	 * @param rawData
	 * @return The execute response
	 * @throws WPSClientException
	 */
	private Object retrieveExecuteResponseViaPOST(String url, ExecuteDocument doc, boolean rawData) throws WPSClientException{
		InputStream is = retrieveDataViaPOST(doc, url);
		if(rawData) {
			return is;
		}
		XmlObject resultObj = checkInputStream(is);
		
		if(resultObj instanceof ExecuteResponseDocument){
			return (ExecuteResponseDocument) resultObj;
		}else if(resultObj instanceof GetStatusDocument){
			return (GetStatusDocument) resultObj;
		}else if(resultObj instanceof GetResultDocument){
			return (GetResultDocument) resultObj;
		}
		return resultObj;
	}

	public String[] getProcessNames(String url) throws IOException {
		List<org.n52.geoprocessing.wps.client.model.Process> processes = getProcessDescriptionsFromCache(url);
		String[] processNames = new String[processes.size()];
		for(int i = 0; i<processNames.length; i++){
			processNames[i] = processes.get(i).getId();
		}
		return processNames;
	}
}
