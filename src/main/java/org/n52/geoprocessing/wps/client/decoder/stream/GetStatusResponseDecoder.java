/*
 * ﻿Copyright (C) 2019 52°North Initiative for Geospatial Open Source
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
package org.n52.geoprocessing.wps.client.decoder.stream;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.n52.geoprocessing.wps.client.model.StatusInfo;
import org.n52.javaps.service.xml.WPSConstants;
import org.n52.shetland.ogc.wps.JobStatus;
import org.n52.svalbard.decode.stream.StreamReaderKey;
import org.n52.svalbard.decode.stream.xml.AbstractElementXmlStreamReader;
import org.n52.svalbard.decode.stream.xml.XmlStreamReaderKey;

public class GetStatusResponseDecoder extends AbstractElementXmlStreamReader {

    private static final HashSet<XmlStreamReaderKey> KEYS =
            new HashSet<>(Arrays.asList(new XmlStreamReaderKey(WPSConstants.Elem.QN_STATUS_INFO)));

    @Override
    public Set<StreamReaderKey> getKeys() {
        return Collections.unmodifiableSet(KEYS);
    }

    public Object readElement(XMLEventReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(WPSConstants.Elem.QN_STATUS_INFO)) {
                    return readGetStatusRequest(start, reader);
                }
            }
        }
        throw eof();
    }

    public StatusInfo readGetStatusRequest(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {

        StatusInfo statusInfo = new StatusInfo();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(WPSConstants.Elem.QN_JOB_ID)) {
                    statusInfo.setJobId(readJobId(elem, reader));
                } else if (start.getName().equals(WPSConstants.Elem.QN_STATUS)) {
                    statusInfo.setStatus(readStatus(elem, reader));
                } else if (start.getName().equals(WPSConstants.Elem.QN_EXPIRATION_DATE)) {
                    statusInfo.setExpirationDate(readOffsetTime(elem, reader));
                } else if (start.getName().equals(WPSConstants.Elem.QN_ESTIMATED_COMPLETION)) {
                    statusInfo.setEstimatedCompletion(readOffsetTime(elem, reader));
                } else if (start.getName().equals(WPSConstants.Elem.QN_NEXT_POLL)) {
                    statusInfo.setNextPoll(readOffsetTime(elem, reader));
                } else if (start.getName().equals(WPSConstants.Elem.QN_PERCENT_COMPLETED)) {
                    statusInfo.setPercentCompleted(readPercentCompleted(elem, reader));
                } else {
                    throw unexpectedTag(start);
                }
            } else if (event.isEndElement()) {

                return statusInfo;
            }
        }
        throw eof();
    }

    private String readJobId(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {
        return reader.getElementText();
    }

    private JobStatus readStatus(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {
        return new JobStatus(reader.getElementText());
    }

    private OffsetDateTime readOffsetTime(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {
        return OffsetDateTime.parse(reader.getElementText());
    }

    private short readPercentCompleted(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {
        return Short.parseShort(reader.getElementText());
    }

}
