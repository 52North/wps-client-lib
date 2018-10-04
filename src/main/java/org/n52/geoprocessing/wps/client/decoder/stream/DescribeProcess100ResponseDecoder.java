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

import org.n52.geoprocessing.wps.client.model.BoundingBoxInputDescription;
import org.n52.geoprocessing.wps.client.model.ComplexInputDescription;
import org.n52.geoprocessing.wps.client.model.Format;
import org.n52.geoprocessing.wps.client.model.InputDescription;
import org.n52.geoprocessing.wps.client.model.LiteralInputDescription;
import org.n52.geoprocessing.wps.client.model.LiteralOutputDescription;
import org.n52.geoprocessing.wps.client.model.OutputDescription;
import org.n52.geoprocessing.wps.client.xml.OWS11Constants;
import org.n52.geoprocessing.wps.client.xml.WPS100Constants;
import org.n52.svalbard.decode.stream.StreamReaderKey;
import org.n52.svalbard.decode.stream.xml.AbstractElementXmlStreamReader;

public class DescribeProcess100ResponseDecoder extends AbstractElementXmlStreamReader {

    @Override
    public List<org.n52.geoprocessing.wps.client.model.Process> readElement(XMLEventReader reader)
            throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(WPS100Constants.Elem.QN_PROCESS_DESCRIPTIONS)) {
                    return readProcessDescriptions(start, reader);
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
                if (start.getName().equals(WPS100Constants.Elem.QN_PROCESS_DESCRIPTIONS)) {
                    return readProcessDescriptions(elem, reader);
                } else {
                    throw unexpectedTag(start);
                }
            }
        }
        throw eof();
    }

    public List<org.n52.geoprocessing.wps.client.model.Process> readProcessDescriptions(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {

        List<org.n52.geoprocessing.wps.client.model.Process> processes = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(WPS100Constants.Elem.QN_PROCESS_DESCRIPTION)) {
                    processes.add(readProcess(elem, reader));
                } else {
                    throw unexpectedTag(start);
                }
            }
        }
        return processes;
    }

    private List<org.n52.geoprocessing.wps.client.model.Process> readProcessOffering(StartElement start,
            XMLEventReader reader) throws XMLStreamException {

        List<org.n52.geoprocessing.wps.client.model.Process> processes = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_PROCESS)) {
                    processes.add(readProcess(elem, reader));
                } else {
                    throw unexpectedTag(start);
                }
            }
        }
        return processes;
    }

    private org.n52.geoprocessing.wps.client.model.Process readProcess(StartElement start,
            XMLEventReader reader) throws XMLStreamException {

        org.n52.geoprocessing.wps.client.model.Process process = new org.n52.geoprocessing.wps.client.model.Process();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(OWS11Constants.Elem.QN_TITLE)) {
                    process.setTitle(reader.getElementText());
                } else if (elem.getName().equals(OWS11Constants.Elem.QN_ABSTRACT)) {
                    process.setAbstract(reader.getElementText());
                } else if (elem.getName().equals(OWS11Constants.Elem.QN_IDENTIFIER)) {
                    process.setId(reader.getElementText());
                } else if (elem.getName().equals(WPS100Constants.Elem.QN_DATA_INPUTS)) {
                    process.setInputs(readDataInputs(elem, reader));
                } else if (elem.getName().equals(WPS100Constants.Elem.QN_PROCESS_OUTPUTS_NO_NAMESPACE)) {
                    process.setOutputs(readProcessOutputs(elem, reader));
                }
            }
        }

        return process;
    }

    private List<OutputDescription> readProcessOutputs(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {

        List<OutputDescription> outputs = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                if (elem.getName().equals(WPS100Constants.Elem.QN_INPUT)) {
                    outputs.add(readOutput(elem, reader));
                }
            }
        }
        return outputs;
    }

    private List<InputDescription> readDataInputs(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {

        List<InputDescription> inputs = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                if (elem.getName().equals(WPS100Constants.Elem.QN_INPUT)) {
                    inputs.add(readInput(elem, reader));
                }
            }
        }
        return inputs;
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
                if (start.getName().equals(OWS11Constants.Elem.QN_TITLE)) {
                    title = reader.getElementText();
                } else if (start.getName().equals(OWS11Constants.Elem.QN_ABSTRACT)) {
                    abstrakt = reader.getElementText();
                } else if (start.getName().equals(OWS11Constants.Elem.QN_IDENTIFIER)) {
                    id = reader.getElementText();
                } else if (start.getName().equals(WPS100Constants.Elem.QN_COMPLEX_DATA)) {
                    readComplexData(start, reader, output);
                } else if (start.getName().equals(WPS100Constants.Elem.QN_LITERAL_DATA)) {
                    output = new LiteralOutputDescription();
                    readLiteralData(start, reader, (LiteralOutputDescription) output);
                } else {
                    throw unexpectedTag(start);
                }
            } else if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if (end.getName().equals(WPS100Constants.Elem.QN_OUTPUT_NO_NAMESPACE)) {
                    output.setTitle(title);
                    output.setId(id);
                    output.setAbstract(abstrakt);
                    return output;
                }

            }
        }

        throw eof();
    }

    private void readLiteralData(StartElement start,
            XMLEventReader reader,
            LiteralOutputDescription output) throws XMLStreamException {

        List<Format> formats = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_FORMAT)) {
                    formats.add(readFormat(elem, reader));
                } else if (elem.getName().equals(WPS100Constants.Elem.QN_LITERAL_DATA_DOMAIN)) {
                    readLiteralDataDomain(elem, reader, output);
                } else {
                    throw unexpectedTag(elem);
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_LITERAL_DATA)) {
                    output.setFormats(formats);
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
                if (start.getName().equals(OWS11Constants.Elem.QN_ANY_VALUE)) {
                    output.setAnyValue(true);
                } else if (start.getName().equals(OWS11Constants.Elem.QN_DATA_TYPE)) {
                    readDataType(elem, reader, output);
                } else {
                    throw unexpectedTag(start);
                }
            } else if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if (end.getName().equals(WPS100Constants.Elem.QN_LITERAL_DATA_DOMAIN)) {
                    return;
                }
            }
        }

    }

    private void readDataType(StartElement elem,
            XMLEventReader reader,
            LiteralOutputDescription output) {
        getAttribute(elem, OWS11Constants.Attr.AN_REFERENCE).ifPresent(output::setDataType);
    }

    private void readComplexData(StartElement start,
            XMLEventReader reader,
            OutputDescription output) throws XMLStreamException {

        List<Format> formats = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_FORMAT)) {
                    formats.add(readFormat(elem, reader));
                } else {
                    throw unexpectedTag(elem);
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_COMPLEX_DATA)) {
                    output.setFormats(formats);
                    return;
                }
            }
        }

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
                if (start.getName().equals(OWS11Constants.Elem.QN_TITLE)) {
                    title = reader.getElementText();
                } else if (start.getName().equals(OWS11Constants.Elem.QN_ABSTRACT)) {
                    abstrakt = reader.getElementText();
                } else if (start.getName().equals(OWS11Constants.Elem.QN_IDENTIFIER)) {
                    id = reader.getElementText();
                } else if (start.getName().equals(WPS100Constants.Elem.QN_COMPLEX_DATA)) {
                    input = new ComplexInputDescription();
                    readComplexData(start, reader, input);
                } else if (start.getName().equals(WPS100Constants.Elem.QN_LITERAL_DATA)) {
                    input = new LiteralInputDescription();
                    readLiteralData(start, reader, (LiteralInputDescription) input);
                } else if (start.getName().equals(WPS100Constants.Elem.QN_BOUNDING_BOX_DATA)) {
                    input = new BoundingBoxInputDescription();
                    readBoundingBoxData(start, reader, (BoundingBoxInputDescription) input);
                } else {
                    throw unexpectedTag(start);
                }
            } else if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if (end.getName().equals(WPS100Constants.Elem.QN_INPUT)) {
                    input.setTitle(title);
                    input.setId(id);
                    input.setAbstract(abstrakt);
                    return input;
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
                if (elem.getName().equals(WPS100Constants.Elem.QN_FORMAT)) {
                    formats.add(readFormat(elem, reader));
                } else if (elem.getName().equals(WPS100Constants.Elem.QN_SUPPORTED_CRS)) {
                    readSupportedCRS(elem, reader, input);
                } else {
                    throw unexpectedTag(elem);
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_BOUNDING_BOX_DATA)) {
                    input.setFormats(formats);
                    return;
                }
            }
        }
        throw eof();

    }

    private void readSupportedCRS(StartElement elem,
            XMLEventReader reader,
            BoundingBoxInputDescription input) {
        // TODO Auto-generated method stub

    }

    private void readLiteralData(StartElement start,
            XMLEventReader reader,
            LiteralInputDescription input) throws XMLStreamException {

        List<Format> formats = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_FORMAT)) {
                    formats.add(readFormat(elem, reader));
                } else if (elem.getName().equals(WPS100Constants.Elem.QN_LITERAL_DATA_DOMAIN)) {
                    readLiteralDataDomain(elem, reader, input);
                } else {
                    throw unexpectedTag(elem);
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_LITERAL_DATA)) {
                    input.setFormats(formats);
                    return;
                }
            }
        }
        throw eof();
    }

    private void readLiteralDataDomain(StartElement elem,
            XMLEventReader reader,
            LiteralInputDescription input) throws XMLStreamException {

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(OWS11Constants.Elem.QN_ANY_VALUE)) {
                    input.setAnyValue(true);
                } else if (start.getName().equals(OWS11Constants.Elem.QN_ALLOWED_VALUES)) {
                    readAllowedValues(elem, reader, input);
                } else if (start.getName().equals(OWS11Constants.Elem.QN_DATA_TYPE)) {
                    readDataType(elem, reader, input);
                } else if (start.getName().equals(OWS11Constants.Elem.QN_DEFAULT_VALUE)) {
                    readDefaultValue(elem, reader, input);
                } else {
                    throw unexpectedTag(start);
                }
            } else if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if (end.getName().equals(WPS100Constants.Elem.QN_LITERAL_DATA_DOMAIN)) {
                    return;
                }
            }
        }
    }

    private void readDefaultValue(StartElement elem,
            XMLEventReader reader,
            LiteralInputDescription input) throws XMLStreamException {
        input.setDefaultValue(reader.getElementText());

    }

    private void readDataType(StartElement elem,
            XMLEventReader reader,
            LiteralInputDescription input) {
        getAttribute(elem, OWS11Constants.Attr.AN_REFERENCE).ifPresent(input::setDataType);
    }

    private void readAllowedValues(StartElement elem,
            XMLEventReader reader,
            LiteralInputDescription input) {
        // TODO Auto-generated method stub

    }

    private void readComplexData(StartElement start,
            XMLEventReader reader,
            InputDescription input) throws XMLStreamException {

        List<Format> formats = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_FORMAT)) {
                    formats.add(readFormat(elem, reader));
                } else {
                    throw unexpectedTag(elem);
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_COMPLEX_DATA)) {
                    input.setFormats(formats);
                    return;
                }
            }
        }
        throw eof();
    }

    private Format readFormat(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {
        Format format = new Format();

        getAttribute(elem, WPS100Constants.Attr.AN_MIME_TYPE).ifPresent(format::setMimeType);
        getAttribute(elem, WPS100Constants.Attr.AN_SCHEMA).ifPresent(format::setSchema);
        getAttribute(elem, WPS100Constants.Attr.AN_ENCODING).ifPresent(format::setEncoding);
        Optional<String> defaultValueAsStringOptional = getAttribute(elem, WPS100Constants.Attr.AN_DEFAULT);

        if (defaultValueAsStringOptional.isPresent()) {
            format.setDefault(Boolean.parseBoolean(defaultValueAsStringOptional.get()));
        }

        return format;
    }

    @Override
    public Set<StreamReaderKey> getKeys() {
        return Collections.emptySet();
    }

}
