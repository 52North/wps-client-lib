/*
 * ﻿Copyright (C) 2020 52°North Initiative for Geospatial Open Source
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

import java.util.Set;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.n52.geoprocessing.wps.client.model.ExceptionReport;
import org.n52.geoprocessing.wps.client.model.OWSExceptionElement;
import org.n52.geoprocessing.wps.client.xml.OWS11Constants;
import org.n52.svalbard.decode.stream.StreamReaderKey;
import org.n52.svalbard.decode.stream.xml.AbstractElementXmlStreamReader;

public class ExceptionReport100Decoder extends AbstractElementXmlStreamReader {

    @Override
    public Object readElement(XMLEventReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(OWS11Constants.Elem.QN_EXCEPTION_REPORT)) {
                    return readExceptions(start, reader);
                }
            }
        }
        throw eof();
    }

    public Object readExceptionReport(StartElement start,
            XMLEventReader reader) throws XMLStreamException {

        if (start.getName().equals(OWS11Constants.Elem.QN_EXCEPTION_REPORT)) {
            return readExceptions(start, reader);
        }
        throw eof();
    }

    private ExceptionReport readExceptions(StartElement start,
            XMLEventReader reader) throws XMLStreamException {

        ExceptionReport exceptionReport = new ExceptionReport();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(OWS11Constants.Elem.QN_EXCEPTION)) {
                    exceptionReport.addException(readException(elem, reader));
                }
            }
        }
        return exceptionReport;
    }

    private OWSExceptionElement readException(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {

        OWSExceptionElement exception = new OWSExceptionElement();

        getAttribute(elem, OWS11Constants.Attr.AN_EXCEPTION_CODE).ifPresent(exception::setExceptionCode);
        getAttribute(elem, OWS11Constants.Attr.AN_LOCATOR).ifPresent(exception::setLocator);

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(OWS11Constants.Elem.QN_EXCEPTION_TEXT)) {
                    exception.setExceptionText(reader.getElementText());
                }
            }
        }
        return exception;
    }

    @Override
    public Set<StreamReaderKey> getKeys() {
        // TODO Auto-generated method stub
        return null;
    }

}
