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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.n52.geoprocessing.wps.client.model.Format;
import org.n52.geoprocessing.wps.client.model.Result;
import org.n52.geoprocessing.wps.client.model.execution.BoundingBox;
import org.n52.geoprocessing.wps.client.model.execution.BoundingBoxData;
import org.n52.geoprocessing.wps.client.model.execution.ComplexData;
import org.n52.geoprocessing.wps.client.model.execution.ComplexDataReference;
import org.n52.geoprocessing.wps.client.model.execution.Data;
import org.n52.geoprocessing.wps.client.xml.WPS100Constants;
import org.n52.javaps.service.xml.OWSConstants;
import org.n52.javaps.service.xml.WPSConstants;
import org.n52.svalbard.decode.stream.StreamReaderKey;
import org.n52.svalbard.decode.stream.xml.AbstractElementXmlStreamReader;
import org.n52.svalbard.decode.stream.xml.XmlStreamReaderKey;
import org.n52.svalbard.stream.XLinkConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetResultResponseDecoder extends AbstractElementXmlStreamReader {

    private static Logger LOGGER = LoggerFactory.getLogger(GetResultResponseDecoder.class);

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
                if (start.getName().equals(WPSConstants.Elem.QN_RESULT)) {
                    return readGetResultRequest(start, reader);
                }
            }
        }
        throw eof();
    }

    public Result readGetResultRequest(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {

        Result result = new Result();

        List<Data> outputs = new ArrayList<Data>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(WPSConstants.Elem.QN_JOB_ID)) {
//                    result.setJobId(readJobId(elem, reader));
                } else if (start.getName().equals(WPSConstants.Elem.QN_OUTPUT)) {
                    outputs.add(readOutput(start, reader));
                } else if (start.getName().equals(WPSConstants.Elem.QN_EXPIRATION_DATE)) {
//                    result.setExpirationDate(readOffsetTime(elem, reader));
                } else {
                    throw unexpectedTag(start);
                }
            } else if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if (end.getName().equals(WPSConstants.Elem.QN_RESULT)) {
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

        getAttribute(start, WPSConstants.Attr.AN_ID).ifPresent(output::setId);

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(OWSConstants.Elem.QN_TITLE)) {
                    output.setTitle(reader.getElementText());
                } else if (elem.getName().equals(OWSConstants.Elem.QN_ABSTRACT)) {
                    output.setAbstract(reader.getElementText());
                } else if (elem.getName().equals(OWSConstants.Elem.QN_METADATA)) {
                    output.addMetadata(readMetadata(elem, reader));
                } else if (elem.getName().equals(WPSConstants.Elem.QN_DATA)) {
                    output = readData(elem, reader, output);
                } else if (elem.getName().equals(WPSConstants.Elem.QN_REFERENCE)) {
                    output = readReference(elem, reader, output.asComplexData());
                }
            }else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPSConstants.Elem.QN_OUTPUT)) {
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

        getAttribute(elem, WPSConstants.Attr.AN_MIME_TYPE).ifPresent(format::setMimeType);
        getAttribute(elem, WPSConstants.Attr.AN_SCHEMA).ifPresent(format::setSchema);
        getAttribute(elem, WPSConstants.Attr.AN_ENCODING).ifPresent(format::setEncoding);

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

        Format format = new Format();

        getAttribute(start, WPSConstants.Attr.AN_MIME_TYPE).ifPresent(format::setMimeType);
        getAttribute(start, WPSConstants.Attr.AN_SCHEMA).ifPresent(format::setSchema);
        getAttribute(start, WPSConstants.Attr.AN_ENCODING).ifPresent(format::setEncoding);

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();

                if (elem.getName().equals(WPSConstants.Elem.QN_LITERAL_VALUE)) {
                    output = output.asLiteralData();
                    output.setValue(reader.getElementText());//TODO check if mime type is text/xml
                } else if (elem.getName().equals(OWSConstants.Elem.QN_BOUNDING_BOX)) {
                    readBoundingBoxData(elem, reader, output.asBoundingBoxData());
                }else {
                    //complex data XML
                    readComplexDataXML(elem, reader, output);
                    output.setFormat(format);
                    return output;
                }
            }else if(event.isCharacters()){
                String data = event.asCharacters().getData().trim();
                if(data.isEmpty()){
                    continue;
                }
                output = output.asComplexData();
                readComplexData(data, reader, output);
                output.setFormat(format);
                return output;
            }
            else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                QName elementName = elem.getName();
                if (elementName.equals(WPSConstants.Elem.QN_DATA)) {
                    output.setFormat(format);
                    return output;
                }
            }
        }
        throw eof();
    }

    private Data readComplexData(String dataString,
            XMLEventReader reader, Data output) throws XMLStreamException {

        StringBuilder data = new StringBuilder();

        data.append(dataString);

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if(event.isCharacters()){
                data.append(event.asCharacters().getData());
            } else {
                output.setValue(data.toString());
                return output;
            }
        }
        throw eof();
    }

    private Data readComplexDataXML(StartElement startElement,
            XMLEventReader reader, Data output) throws XMLStreamException {

        StringBuilder data = new StringBuilder();

        data.append(startElement.toString());

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if(event.isEndElement()){
                EndElement endElement = event.asEndElement();
                if(endElement.getName().equals(WPSConstants.Elem.QN_DATA)){
                    output.setValue(data.toString());
                    return output;
                }
            }
            data.append(event.toString());
        }
        throw eof();
    }

    private void readBoundingBoxData(StartElement start,
            XMLEventReader reader,
            BoundingBoxData output) throws XMLStreamException {

        BoundingBox boundingBox = new BoundingBox();

        getAttribute(start, OWSConstants.Attr.AN_CRS).ifPresent(boundingBox::setCrs);

        getAttribute(start, OWSConstants.Attr.AN_DIMENSIONS).ifPresent(new Consumer<String>() {

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
                if (elem.getName().equals(OWSConstants.Elem.QN_LOWER_CORNER)) {
                    lowerCorner = reader.getElementText();
                } else if (elem.getName().equals(OWSConstants.Elem.QN_UPPER_CORNER)) {
                    upperCorner = reader.getElementText();
                }
            }else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(OWSConstants.Elem.QN_BOUNDING_BOX)) {
                    setBoundingBoxCoordinates(lowerCorner, upperCorner, boundingBox, crs);
                    output.setValue(boundingBox);
                    return;
                }
            }
        }

    }

    private void setBoundingBoxCoordinates(String lowerCorner, String upperCorner, BoundingBox boundingBox, String crs){

        //TODO check crs axis order
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

}
