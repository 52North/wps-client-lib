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

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.n52.geoprocessing.wps.client.decoder.stream.GetCapabilitiesResponseDecoder;
import org.n52.geoprocessing.wps.client.model.WPSCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WPS20CapabilitiesParser {

    private static Logger LOGGER = LoggerFactory.getLogger(WPS20CapabilitiesParser.class);

    public WPS20CapabilitiesParser() {}

    public WPSCapabilities createWPSCapabilitiesOWS20(InputStream in) throws XMLStreamException {

//        LOGGER.trace("Parsing capabilities: " + xmlObject);

//        WPSCapabilities result = new WPSCapabilities();
//
//        net.opengis.ows.x20.ServiceIdentificationDocument.ServiceIdentification xmlServiceIdentification =
//                xmlObject.getCapabilities().getServiceIdentification();
//
//        ServiceIdentification serviceIdentification = createServiceIdentification(xmlServiceIdentification);
//
//        result.setServiceIdentification(serviceIdentification);
//
//        net.opengis.ows.x20.ServiceProviderDocument.ServiceProvider xmlServiceProvider =
//                xmlObject.getCapabilities().getServiceProvider();
//
//        ServiceProvider serviceProvider = createServiceProvider(xmlServiceProvider);
//
//        result.setServiceProvider(serviceProvider);
//
//        List<Process> processes = createProcesses(xmlObject.getCapabilities().getContents().getProcessSummaryArray());
//
//        result.setProcesses(processes);

        return new GetCapabilitiesResponseDecoder().readElement(XMLInputFactory.newInstance().createXMLEventReader(new InputStreamReader(in)));
    }

}
