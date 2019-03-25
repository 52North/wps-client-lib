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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.n52.geoprocessing.wps.client.model.AllowedValues;

import org.n52.geoprocessing.wps.client.model.BoundingBoxInputDescription;
import org.n52.geoprocessing.wps.client.model.BoundingBoxOutputDescription;
import org.n52.geoprocessing.wps.client.model.ComplexInputDescription;
import org.n52.geoprocessing.wps.client.model.Format;
import org.n52.geoprocessing.wps.client.model.InputDescription;
import org.n52.geoprocessing.wps.client.model.LiteralInputDescription;
import org.n52.geoprocessing.wps.client.model.LiteralOutputDescription;
import org.n52.geoprocessing.wps.client.model.OutputDescription;
import org.n52.geoprocessing.wps.client.model.Range;
import org.n52.javaps.service.xml.OWSConstants;
import org.n52.javaps.service.xml.WPSConstants;
import org.n52.svalbard.decode.stream.StreamReaderKey;
import org.n52.svalbard.decode.stream.xml.AbstractElementXmlStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DescribeProcessResponseDecoder extends AbstractElementXmlStreamReader {

    private static Logger LOGGER = LoggerFactory.getLogger(DescribeProcessResponseDecoder.class);

    private static final String ASYNC_EXECUTE = "async-execute";

    private static final String REFERENCE = "reference";

    @Override
    public List<org.n52.geoprocessing.wps.client.model.Process> readElement(XMLEventReader reader)
            throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(WPSConstants.Elem.QN_PROCESS_OFFERINGS)) {
                    return readProcessOfferings(start, reader);
                }
            }
        }
        throw eof();
    }

    private List<org.n52.geoprocessing.wps.client.model.Process> readDescribeProcessResponse(StartElement start,
            XMLEventReader reader) throws XMLStreamException {

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (start.getName().equals(WPSConstants.Elem.QN_PROCESS_OFFERINGS)) {
                    return readProcessOfferings(elem, reader);
                } else {
                    throw unexpectedTag(start);
                }
            }
        }
        throw eof();
    }

    public List<org.n52.geoprocessing.wps.client.model.Process> readProcessOfferings(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {

        List<org.n52.geoprocessing.wps.client.model.Process> processes = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(WPSConstants.Elem.QN_PROCESS_OFFERING)) {
                    return readProcessOffering(start, reader);
                } else {
                    throw unexpectedTag(start);
                }
            } else if (event.isEndElement()) {
                return processes;
            }
        }
        throw eof();
    }

    private List<org.n52.geoprocessing.wps.client.model.Process> readProcessOffering(StartElement start,
            XMLEventReader reader) throws XMLStreamException {

        List<org.n52.geoprocessing.wps.client.model.Process> processes = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(WPSConstants.Elem.QN_PROCESS)) {
                    processes.add(readProcess(start, reader));
                } else {
                    throw unexpectedTag(elem);
                }
            }
        }
        return processes;
    }

    private org.n52.geoprocessing.wps.client.model.Process readProcess(StartElement processOfferingsElement,
            XMLEventReader reader) throws XMLStreamException {

        org.n52.geoprocessing.wps.client.model.Process process = new org.n52.geoprocessing.wps.client.model.Process();

        try {
            String jobControlOptions
                    = getAttribute(processOfferingsElement, WPSConstants.Attr.AN_JOB_CONTROL_OPTIONS).get();

            process.setStatusSupported(jobControlOptions.contains(ASYNC_EXECUTE));
        } catch (Exception e) {
            LOGGER.info("Job control options attribute not present.");
        }

        try {
            String outputTransmission
                    = getAttribute(processOfferingsElement, WPSConstants.Attr.AN_OUTPUT_TRANSMISSION).get();

            process.setReferenceSupported(outputTransmission.contains(REFERENCE));
        } catch (Exception e) {
            LOGGER.info("Output transmission attribute not present.");
        }

        List<InputDescription> inputs = new ArrayList<>();

        List<OutputDescription> outputs = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(OWSConstants.Elem.QN_TITLE)) {
                    process.setTitle(reader.getElementText());
                } else if (elem.getName().equals(OWSConstants.Elem.QN_ABSTRACT)) {
                    process.setAbstract(reader.getElementText());
                } else if (elem.getName().equals(OWSConstants.Elem.QN_IDENTIFIER)) {
                    process.setId(reader.getElementText());
                } else if (elem.getName().equals(WPSConstants.Elem.QN_INPUT)) {
                    inputs.add(readInput(elem, reader));
                } else if (elem.getName().equals(WPSConstants.Elem.QN_OUTPUT)) {
                    outputs.add(readOutput(elem, reader));
                }
            }
        }

        process.setInputs(inputs);
        process.setOutputs(outputs);

        return process;
    }

    private OutputDescription readOutput(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {

        OutputDescription output = new OutputDescription();

        String title = "";
        String id = "";
        String abstrakt = "";

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(OWSConstants.Elem.QN_TITLE)) {
                    title = reader.getElementText();
                } else if (start.getName().equals(OWSConstants.Elem.QN_ABSTRACT)) {
                    abstrakt = reader.getElementText();
                } else if (start.getName().equals(OWSConstants.Elem.QN_IDENTIFIER)) {
                    id = reader.getElementText();
                } else if (start.getName().equals(WPSConstants.Elem.QN_COMPLEX_DATA)) {
                    readComplexData(start, reader, output);
                } else if (start.getName().equals(WPSConstants.Elem.QN_LITERAL_DATA)) {
                    output = new LiteralOutputDescription();
                    readLiteralData(start, reader, (LiteralOutputDescription) output);
                } else if (start.getName().equals(WPSConstants.Elem.QN_BOUNDING_BOX_DATA)) {
                    output = new BoundingBoxOutputDescription();
                    readBoundingBoxData(start, reader, (BoundingBoxOutputDescription) output);
                } else {
                    throw unexpectedTag(start);
                }
            } else if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if (end.getName().equals(WPSConstants.Elem.QN_OUTPUT)) {
                    output.setTitle(title);
                    output.setId(id);
                    output.setAbstract(abstrakt);
                    return output;
                }

            }
        }

        throw eof();
    }

    private void readBoundingBoxData(StartElement start,
            XMLEventReader reader,
            BoundingBoxOutputDescription output) throws XMLStreamException {

        List<Format> formats = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(WPSConstants.Elem.QN_FORMAT)) {
                    formats.add(readFormat(elem, reader));
                } else if (elem.getName().equals(WPSConstants.Elem.QN_SUPPORTED_CRS)) {
                    readSupportedCRS(elem, reader, output);
                } else {
                    throw unexpectedTag(elem);
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPSConstants.Elem.QN_BOUNDING_BOX_DATA)) {
                    output.setFormats(formats);
                    return;
                }
            }
        }
        throw eof();

    }

    private void readBoundingBoxData(StartElement start,
            XMLEventReader reader,
            BoundingBoxInputDescription input) throws XMLStreamException {

        List<Format> formats = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(WPSConstants.Elem.QN_FORMAT)) {
                    formats.add(readFormat(elem, reader));
                } else if (elem.getName().equals(WPSConstants.Elem.QN_SUPPORTED_CRS)) {
                    readSupportedCRS(elem, reader, input);
                } else {
                    throw unexpectedTag(elem);
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPSConstants.Elem.QN_BOUNDING_BOX_DATA)) {
                    input.setFormats(formats);
                    return;
                }
            }
        }
        throw eof();

    }

    private void readSupportedCRS(StartElement elem,
            XMLEventReader reader,
            BoundingBoxOutputDescription output) {
        // TODO Auto-generated method stub

    }

    private void readSupportedCRS(StartElement elem,
            XMLEventReader reader,
            BoundingBoxInputDescription input) {
        // TODO Auto-generated method stub

    }

    private void readLiteralData(StartElement start,
            XMLEventReader reader,
            LiteralOutputDescription output) throws XMLStreamException {

        List<Format> formats = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(WPSConstants.Elem.QN_FORMAT)) {
                    formats.add(readFormat(elem, reader));
                } else if (elem.getName().equals(WPSConstants.Elem.QN_LITERAL_DATA_DOMAIN)) {
                    readLiteralDataDomain(elem, reader, output);
                } else {
                    throw unexpectedTag(elem);
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPSConstants.Elem.QN_LITERAL_DATA)) {
                    output.setFormats(formats);
                    return;
                }
            }
        }
        throw eof();

    }

    private void readLiteralData(StartElement start,
            XMLEventReader reader,
            LiteralInputDescription input) throws XMLStreamException {

        List<Format> formats = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(WPSConstants.Elem.QN_FORMAT)) {
                    formats.add(readFormat(elem, reader));
                } else if (elem.getName().equals(WPSConstants.Elem.QN_LITERAL_DATA_DOMAIN)) {
                    readLiteralDataDomain(elem, reader, input);
                } else {
                    throw unexpectedTag(elem);
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPSConstants.Elem.QN_LITERAL_DATA)) {
                    input.setFormats(formats);
                    return;
                }
            }
        }
        throw eof();
    }

    private void readLiteralDataDomain(StartElement elem,
            XMLEventReader reader,
            LiteralOutputDescription output) throws XMLStreamException {

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(OWSConstants.Elem.QN_ANY_VALUE)) {
                    output.setAnyValue(true);
                } else if (start.getName().equals(OWSConstants.Elem.QN_DATA_TYPE)) {
                    readDataType(start, reader, output);
                } else {
                    throw unexpectedTag(start);
                }
            } else if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if (end.getName().equals(WPSConstants.Elem.QN_LITERAL_DATA_DOMAIN)) {
                    return;
                }
            }
        }

    }

    private void readLiteralDataDomain(StartElement elem,
            XMLEventReader reader,
            LiteralInputDescription input) throws XMLStreamException {

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(OWSConstants.Elem.QN_ANY_VALUE)) {
                    input.setAnyValue(true);
                } else if (start.getName().equals(OWSConstants.Elem.QN_ALLOWED_VALUES)) {
                    readAllowedValues(start, reader, input);
                } else if (start.getName().equals(OWSConstants.Elem.QN_DATA_TYPE)) {
                    readDataType(start, reader, input);
                } else if (start.getName().equals(OWSConstants.Elem.QN_DEFAULT_VALUE)) {
                    readDefaultValue(start, reader, input);
                } else {
                    throw unexpectedTag(start);
                }
            } else if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if (end.getName().equals(WPSConstants.Elem.QN_LITERAL_DATA_DOMAIN)) {
                    return;
                }
            }
        }
    }

    private void readDataType(StartElement elem,
            XMLEventReader reader,
            LiteralOutputDescription output) {
        getAttribute(elem, OWSConstants.Attr.QN_REFERENCE).ifPresent(output::setDataType);
    }

    private void readDataType(StartElement elem,
            XMLEventReader reader,
            LiteralInputDescription input) {
        getAttribute(elem, OWSConstants.Attr.QN_REFERENCE).ifPresent(input::setDataType);
    }

    private void readComplexData(StartElement start,
            XMLEventReader reader,
            OutputDescription output) throws XMLStreamException {

        List<Format> formats = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(WPSConstants.Elem.QN_FORMAT)) {
                    formats.add(readFormat(elem, reader));
                } else {
                    throw unexpectedTag(elem);
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPSConstants.Elem.QN_COMPLEX_DATA)) {
                    output.setFormats(formats);
                    return;
                }
            }
        }

    }

    private void readComplexData(StartElement start,
            XMLEventReader reader,
            InputDescription input) throws XMLStreamException {

        List<Format> formats = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(WPSConstants.Elem.QN_FORMAT)) {
                    formats.add(readFormat(elem, reader));
                } else {
                    throw unexpectedTag(elem);
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPSConstants.Elem.QN_COMPLEX_DATA)) {
                    input.setFormats(formats);
                    return;
                }
            }
        }
        throw eof();
    }

    private InputDescription readInput(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {

        InputDescription input = new InputDescription();

        String title = "";
        String id = "";
        String abstrakt = "";

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(OWSConstants.Elem.QN_TITLE)) {
                    title = reader.getElementText();
                } else if (start.getName().equals(OWSConstants.Elem.QN_ABSTRACT)) {
                    abstrakt = reader.getElementText();
                } else if (start.getName().equals(OWSConstants.Elem.QN_IDENTIFIER)) {
                    id = reader.getElementText();
                } else if (start.getName().equals(WPSConstants.Elem.QN_COMPLEX_DATA)) {
                    input = new ComplexInputDescription();
                    readComplexData(start, reader, input);
                } else if (start.getName().equals(WPSConstants.Elem.QN_LITERAL_DATA)) {
                    input = new LiteralInputDescription();
                    readLiteralData(start, reader, (LiteralInputDescription) input);
                } else if (start.getName().equals(WPSConstants.Elem.QN_BOUNDING_BOX_DATA)) {
                    input = new BoundingBoxInputDescription();
                    readBoundingBoxData(start, reader, (BoundingBoxInputDescription) input);
                } else {
                    throw unexpectedTag(start);
                }
            } else if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if (end.getName().equals(WPSConstants.Elem.QN_INPUT)) {
                    input.setTitle(title);
                    input.setId(id);
                    input.setAbstract(abstrakt);
                    return input;
                }

            }
        }

        throw eof();
    }

    private void readDefaultValue(StartElement elem,
            XMLEventReader reader,
            LiteralInputDescription input) throws XMLStreamException {
        input.setDefaultValue(reader.getElementText());

    }

    private void readAllowedValues(StartElement elem,
            XMLEventReader reader,
            LiteralInputDescription input) throws XMLStreamException {

        AllowedValues allowedValues = new AllowedValues();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(OWSConstants.Elem.QN_VALUE)) {
                    allowedValues.addAllowedValue(reader.getElementText());
                } else if (start.getName().equals(OWSConstants.Elem.QN_RANGE)) {
                    readRangeValue(start, reader, allowedValues);
                } else {
                    throw unexpectedTag(start);
                }
            } else if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if (end.getName().equals(OWSConstants.Elem.QN_ALLOWED_VALUES)) {
                    input.setAllowedValues(allowedValues);
                    return;
                }
            }
        }
    }

    private Format readFormat(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {
        Format format = new Format();

        getAttribute(elem, WPSConstants.Attr.AN_MIME_TYPE).ifPresent(format::setMimeType);
        getAttribute(elem, WPSConstants.Attr.AN_SCHEMA).ifPresent(format::setSchema);
        getAttribute(elem, WPSConstants.Attr.AN_ENCODING).ifPresent(format::setEncoding);
        Optional<String> defaultValueAsStringOptional = getAttribute(elem, WPSConstants.Attr.AN_DEFAULT);

        if (defaultValueAsStringOptional.isPresent()) {
            format.setDefault(Boolean.parseBoolean(defaultValueAsStringOptional.get()));
        }

        return format;
    }

    @Override
    public Set<StreamReaderKey> getKeys() {
        return Collections.emptySet();
    }

    private void readRangeValue(StartElement elem,
            XMLEventReader reader, AllowedValues allowedValues) throws XMLStreamException {
        Range range = new Range();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(OWSConstants.Elem.QN_MINIMUM_VALUE)) {
                    range.setMinimumValue(reader.getElementText());
                } else if (start.getName().equals(OWSConstants.Elem.QN_MAXIMUM_VALUE)) {
                    range.setMinimumValue(reader.getElementText());
                } else if (start.getName().equals(OWSConstants.Elem.QN_SPACING)) {
                    range.setSpacing(reader.getElementText());
                } else {
                    throw unexpectedTag(start);
                }
            } else if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if (end.getName().equals(OWSConstants.Elem.QN_RANGE)) {
                    allowedValues.addRange(range);
                    return;
                }
            }
        }
    }

}
