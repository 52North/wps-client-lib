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
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.n52.geoprocessing.wps.client.decoder.stream.DescribeProcessResponseDecoder;
import org.n52.geoprocessing.wps.client.model.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WPS20ProcessParser {

    private static Logger LOGGER = LoggerFactory.getLogger(WPS20ProcessParser.class);

    public static Process parseProcess(InputStream in) throws XMLStreamException {

        XMLEventReader xmlReader = XMLInputFactory.newInstance().createXMLEventReader(new InputStreamReader(in));

        List<org.n52.geoprocessing.wps.client.model.Process> processes =
                new DescribeProcessResponseDecoder().readElement(xmlReader);

        return processes.get(0);
    }
}
