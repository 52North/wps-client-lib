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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.n52.geoprocessing.wps.client.model.Format;
import org.n52.geoprocessing.wps.client.model.Result;
import org.n52.geoprocessing.wps.client.model.StatusInfo;
import org.n52.geoprocessing.wps.client.model.execution.BoundingBox;
import org.n52.geoprocessing.wps.client.model.execution.ComplexData;
import org.n52.geoprocessing.wps.client.model.execution.ComplexDataReference;
import org.n52.geoprocessing.wps.client.model.execution.Data;
import org.n52.geoprocessing.wps.client.xml.OWS11Constants;
import org.n52.geoprocessing.wps.client.xml.WPS100Constants;
import org.n52.shetland.ogc.wps.JobStatus;
import org.n52.svalbard.decode.stream.StreamReaderKey;
import org.n52.svalbard.decode.stream.xml.AbstractElementXmlStreamReader;
import org.n52.svalbard.decode.stream.xml.XmlStreamReaderKey;
import org.n52.svalbard.stream.XLinkConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetStatusResponse100Decoder extends AbstractElementXmlStreamReader {

    private static Logger LOGGER = LoggerFactory.getLogger(GetStatusResponse100Decoder.class);

    private static final HashSet<XmlStreamReaderKey> KEYS =
            new HashSet<>(Arrays.asList(new XmlStreamReaderKey(WPS100Constants.Elem.QN_EXECUTE_RESPONSE)));

    @Override
    public Set<StreamReaderKey> getKeys() {
        return Collections.unmodifiableSet(KEYS);
    }

    public Object readElement(XMLEventReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(WPS100Constants.Elem.QN_EXECUTE_RESPONSE)) {
                    return readExecuteResponse(start, reader);
                }
            }
        }
        throw eof();
    }

    public StatusInfo readExecuteResponse(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {

        StatusInfo statusInfo = new StatusInfo();

        try {
            String statusLocation = getAttribute(elem, WPS100Constants.Attr.AN_STATUS_LOCATION).get();

            statusInfo.setStatusLocation(statusLocation);
        } catch (Exception e) {
            LOGGER.info("Status location attribute not present.");
        }

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(WPS100Constants.Elem.QN_STATUS)) {
                    statusInfo.setStatus(readStatus(start, reader));
                } else if (start.getName().equals(WPS100Constants.Elem.QN_PROCESS)) {
                    skipProcessElement(reader);
                } else if (start.getName().equals(WPS100Constants.Elem.QN_PROCESS_OUTPUTS)) {
                    statusInfo.setResult(readResult(start, reader));
                } else {
                    throw unexpectedTag(start);
                }
            } else if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                if (endElement.getName().equals(WPS100Constants.Elem.QN_EXECUTE_RESPONSE)) {
                    return statusInfo;
                }
            }
        }
        throw eof();
    }

    private Result readResult(StartElement start,
            XMLEventReader reader) throws XMLStreamException {
        Result result = new Result();

        List<Data> outputs = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_OUTPUT)) {
                    outputs.add(readOutput(elem, reader));
                }
            } else if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                if (endElement.getName().equals(WPS100Constants.Elem.QN_PROCESS_OUTPUTS)) {
                    result.setOutputs(outputs);
                    return result;
                }
            }
        }
        throw eof();
    }

    private Data readOutput(StartElement start,
            XMLEventReader reader) throws XMLStreamException {

        Data output = new Data();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(OWS11Constants.Elem.QN_IDENTIFIER)) {
                    output.setId(reader.getElementText());
                } else if (elem.getName().equals(OWS11Constants.Elem.QN_TITLE)) {
                    output.setTitle(reader.getElementText());
                } else if (elem.getName().equals(OWS11Constants.Elem.QN_ABSTRACT)) {
                    output.setAbstract(reader.getElementText());
                } else if (elem.getName().equals(OWS11Constants.Elem.QN_METADATA)) {
                    output.addMetadata(readMetadata(elem, reader));
                } else if (elem.getName().equals(WPS100Constants.Elem.QN_DATA)) {
                    output = readData(elem, reader, output);
                } else if (elem.getName().equals(WPS100Constants.Elem.QN_REFERENCE)) {
                    output = readReference(elem, reader, output.asComplexData());
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_OUTPUT)) {
                    return output;
                }
            }
        }
        throw eof();
    }

    private Data readReference(StartElement elem,
            XMLEventReader reader,
            ComplexData output) throws XMLStreamException {

        Format format = new Format();

        getAttribute(elem, WPS100Constants.Attr.AN_MIME_TYPE).ifPresent(format::setMimeType);
        getAttribute(elem, WPS100Constants.Attr.AN_SCHEMA).ifPresent(format::setSchema);
        getAttribute(elem, WPS100Constants.Attr.AN_ENCODING).ifPresent(format::setEncoding);

        ComplexDataReference complexDataReference = new ComplexDataReference();

        getAttribute(elem, XLinkConstants.Attr.AN_HREF).ifPresent(new Consumer<String>() {

            @Override
            public void accept(String t) {

                URL href;
                try {
                    href = new URL(t);
                    complexDataReference.setHref(href);
                } catch (MalformedURLException e) {
                    LOGGER.error("Malformed URL: " + t);
                }

            }
        });

        output.setFormat(format);
        output.setReference(complexDataReference);

        return output;
    }

    private Data readData(StartElement start,
            XMLEventReader reader,
            Data output) throws XMLStreamException {

        Data outputCopy = output;

        Format format = new Format();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_COMPLEX_DATA)) {
                    outputCopy = outputCopy.asComplexData();
                    getAttribute(elem, WPS100Constants.Attr.AN_MIME_TYPE).ifPresent(format::setMimeType);
                    getAttribute(elem, WPS100Constants.Attr.AN_SCHEMA).ifPresent(format::setSchema);
                    getAttribute(elem, WPS100Constants.Attr.AN_ENCODING).ifPresent(format::setEncoding);
                    readComplexData(elem, reader, outputCopy);
                    outputCopy.setFormat(format);
                } else if (elem.getName().equals(WPS100Constants.Elem.QN_LITERAL_DATA)) {
                    outputCopy = outputCopy.asLiteralData();
                    outputCopy.setValue(reader.getElementText());
                } else if (elem.getName().equals(WPS100Constants.Elem.QN_BOUNDING_BOX_DATA)) {
                    outputCopy = outputCopy.asBoundingBoxData();
                    readBoundingBoxData(elem, reader, outputCopy);
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_DATA)) {
                    outputCopy.setFormat(format);
                    return outputCopy;
                }
            }
        }
        throw eof();

    }

    private Data readComplexData(StartElement start,
            XMLEventReader reader,
            Data output) throws XMLStreamException {

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isCharacters()) {
                String chars = event.asCharacters().getData();
                if (chars.trim().isEmpty()) {
                    continue;
                } else {
                    return readComplexDataString(chars, reader, output);
                }
            } else if (event.isStartElement()) {
                return readComplexDataXML(event.asStartElement(), reader, output);
            }
        }
        throw eof();
    }

    private Data readComplexDataXML(StartElement startElement,
            XMLEventReader reader,
            Data output) throws XMLStreamException {

        StringBuilder data = new StringBuilder();

        data.append(startElement.toString());

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                if (endElement.getName().equals(WPS100Constants.Elem.QN_COMPLEX_DATA)) {
                    output.setValue(data.toString());
                    return output;
                }
            }
            data.append(event.toString());
        }
        throw eof();
    }

    private Data readComplexDataString(String dataString,
            XMLEventReader reader,
            Data output) throws XMLStreamException {

        StringBuilder data = new StringBuilder();

        data.append(dataString);

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isCharacters()) {
                data.append(event.asCharacters().getData());
            } else {
                output.setValue(data.toString());
                return output;
            }
        }
        throw eof();
    }

    private void readBoundingBoxData(StartElement start,
            XMLEventReader reader,
            Data output) throws XMLStreamException {

        BoundingBox boundingBox = new BoundingBox();

        getAttribute(start, OWS11Constants.Attr.AN_CRS).ifPresent(boundingBox::setCrs);

        getAttribute(start, OWS11Constants.Attr.AN_DIMENSIONS).ifPresent(new Consumer<String>() {

            @Override
            public void accept(String t) {
                boundingBox.setDimensions(Integer.parseInt(t));
            }
        });

        String crs = boundingBox.getCrs();

        String lowerCorner = "";
        String upperCorner = "";

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(OWS11Constants.Elem.QN_LOWER_CORNER)) {
                    lowerCorner = reader.getElementText();
                } else if (elem.getName().equals(OWS11Constants.Elem.QN_UPPER_CORNER)) {
                    upperCorner = reader.getElementText();
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_BOUNDING_BOX_DATA)) {
                    setBoundingBoxCoordinates(lowerCorner, upperCorner, boundingBox, crs);
                    output.setValue(boundingBox);
                    return;
                }
            }
        }

    }

    private void setBoundingBoxCoordinates(String lowerCorner,
            String upperCorner,
            BoundingBox boundingBox,
            String crs) {

        // TODO check crs axis order
        String[] coordinates = lowerCorner.split(" ");

        boundingBox.setMinX(Double.parseDouble(coordinates[0]));
        boundingBox.setMinY(Double.parseDouble(coordinates[1]));

        coordinates = upperCorner.split(" ");

        boundingBox.setMaxX(Double.parseDouble(coordinates[0]));
        boundingBox.setMaxY(Double.parseDouble(coordinates[1]));
    }

    private String readMetadata(StartElement elem,
            XMLEventReader reader) {
        return "";
    }

    private void skipProcessElement(XMLEventReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                if (endElement.getName().equals(WPS100Constants.Elem.QN_PROCESS)) {
                    return;
                }
            }
        }
    }

    private JobStatus readStatus(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(WPS100Constants.Elem.QN_PROCESS_ACCEPTED)) {
                    return JobStatus.accepted();
                } else if (start.getName().equals(WPS100Constants.Elem.QN_PROCESS_STARTED)) {
                    return JobStatus.running();
                } else if (start.getName().equals(WPS100Constants.Elem.QN_PROCESS_FAILED)) {
                    return JobStatus.failed();
                } else if (start.getName().equals(WPS100Constants.Elem.QN_PROCESS_PAUSED)) {
                    // TODO
                    return JobStatus.running();
                } else if (start.getName().equals(WPS100Constants.Elem.QN_PROCESS_SUCCEEDED)) {
                    return JobStatus.succeeded();
                } else {
                    throw unexpectedTag(start);
                }
            }
        }
        throw eof();
    }

}
