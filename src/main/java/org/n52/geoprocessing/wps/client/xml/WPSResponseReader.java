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
package org.n52.geoprocessing.wps.client.xml;

import java.util.Set;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.n52.geoprocessing.wps.client.decoder.stream.DescribeProcess100ResponseDecoder;
import org.n52.geoprocessing.wps.client.decoder.stream.DescribeProcessResponseDecoder;
import org.n52.geoprocessing.wps.client.decoder.stream.ExceptionReport100Decoder;
import org.n52.geoprocessing.wps.client.decoder.stream.ExceptionReport20Decoder;
import org.n52.geoprocessing.wps.client.decoder.stream.GetCapabilities100ResponseDecoder;
import org.n52.geoprocessing.wps.client.decoder.stream.GetCapabilitiesResponseDecoder;
import org.n52.geoprocessing.wps.client.decoder.stream.GetResultResponseDecoder;
import org.n52.geoprocessing.wps.client.decoder.stream.GetStatusResponse100Decoder;
import org.n52.geoprocessing.wps.client.decoder.stream.GetStatusResponseDecoder;
import org.n52.javaps.service.xml.OWSConstants;
import org.n52.javaps.service.xml.WPSConstants;
import org.n52.svalbard.decode.stream.StreamReaderKey;
import org.n52.svalbard.decode.stream.xml.AbstractElementXmlStreamReader;

public class WPSResponseReader extends AbstractElementXmlStreamReader {

    public Object readElement(XMLEventReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(WPSConstants.Elem.QN_STATUS_INFO)) {
                    return readStatusInfoResponse(start, reader);
                } else if (start.getName().equals(WPSConstants.Elem.QN_PROCESS_OFFERINGS)) {
                    return readProcessOfferingsResponse(start, reader);
                } else if (start.getName().equals(WPS100Constants.Elem.QN_PROCESS_DESCRIPTIONS)) {
                    return readDescribeProcessResponse100(start, reader);
                } else if (start.getName().equals(WPSConstants.Elem.QN_CAPABILITIES)) {
                    return readCapabilitiesResponse20(start, reader);
                } else if (start.getName().equals(WPS100Constants.Elem.QN_CAPABILITIES)) {
                    return readCapabilitiesResponse100(start, reader);
                } else if (start.getName().equals(WPSConstants.Elem.QN_RESULT)) {
                    return readResultResponse(start, reader);
                } else if (start.getName().equals(OWSConstants.Elem.QN_EXCEPTION_REPORT)) {
                    return readExceptionReport20(start, reader);
                } else if (start.getName().equals(OWS11Constants.Elem.QN_EXCEPTION_REPORT)) {
                    return readExceptionReport100(start, reader);
                } else if (start.getName().equals(WPS100Constants.Elem.QN_EXECUTE_RESPONSE)) {
                    return readExecuteResponse(start, reader);
                } else {
                    throw unexpectedTag(start);
                }
            }
        }
        throw eof();
    }

    private Object readExecuteResponse(StartElement start,
            XMLEventReader reader) throws XMLStreamException {
        return new GetStatusResponse100Decoder().readExecuteResponse(start, reader);
    }

    private Object readExceptionReport100(StartElement start,
            XMLEventReader reader) throws XMLStreamException {
        return new ExceptionReport100Decoder().readExceptionReport(start, reader);
    }

    private Object readExceptionReport20(StartElement start,
            XMLEventReader reader) throws XMLStreamException {
        return new ExceptionReport20Decoder().readExceptionReport(start, reader);
    }

    private Object readDescribeProcessResponse100(StartElement start,
            XMLEventReader reader) throws XMLStreamException {
        return new DescribeProcess100ResponseDecoder().readProcessDescriptions(start, reader);
    }

    private Object readCapabilitiesResponse100(StartElement start,
            XMLEventReader reader) throws XMLStreamException {
        return new GetCapabilities100ResponseDecoder().readCapabilities(start, reader);
    }

    private Object readResultResponse(StartElement start,
            XMLEventReader reader) throws XMLStreamException {
        return new GetResultResponseDecoder().readGetResultRequest(start, reader);
    }

    private Object readCapabilitiesResponse20(StartElement start,
            XMLEventReader reader) throws XMLStreamException {
        return new GetCapabilitiesResponseDecoder().readCapabilities(start, reader);
    }

    private Object readProcessOfferingsResponse(StartElement start,
            XMLEventReader reader) throws XMLStreamException {
        return new DescribeProcessResponseDecoder().readProcessOfferings(start, reader);
    }

    private Object readStatusInfoResponse(StartElement start,
            XMLEventReader reader) throws XMLStreamException {
        return new GetStatusResponseDecoder().readGetStatusRequest(start, reader);
    }

    @Override
    public Set<StreamReaderKey> getKeys() {
        // TODO Auto-generated method stub
        return null;
    }
}
