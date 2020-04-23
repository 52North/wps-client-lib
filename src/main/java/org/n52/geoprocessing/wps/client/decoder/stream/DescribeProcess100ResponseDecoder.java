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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.n52.geoprocessing.wps.client.model.BoundingBoxInputDescription;
import org.n52.geoprocessing.wps.client.model.BoundingBoxOutputDescription;
import org.n52.geoprocessing.wps.client.model.ComplexInputDescription;
import org.n52.geoprocessing.wps.client.model.Format;
import org.n52.geoprocessing.wps.client.model.InputDescription;
import org.n52.geoprocessing.wps.client.model.LiteralInputDescription;
import org.n52.geoprocessing.wps.client.model.LiteralOutputDescription;
import org.n52.geoprocessing.wps.client.model.OutputDescription;
import org.n52.geoprocessing.wps.client.model.UOM;
import org.n52.geoprocessing.wps.client.xml.OWS11Constants;
import org.n52.geoprocessing.wps.client.xml.WPS100Constants;
import org.n52.svalbard.decode.stream.StreamReaderKey;
import org.n52.svalbard.decode.stream.xml.AbstractElementXmlStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DescribeProcess100ResponseDecoder extends AbstractElementXmlStreamReader {

    private static Logger LOGGER = LoggerFactory.getLogger(DescribeProcess100ResponseDecoder.class);

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
                    processes.add(readProcess(start, reader));
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

        try {
            String storeSupported = getAttribute(start, WPS100Constants.Attr.AN_STORE_SUPPORTED).get();

            process.setStatusSupported(Boolean.parseBoolean(storeSupported));
        } catch (Exception e) {
            LOGGER.info("Status supported attribute not present.");
        }

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
                } else if (elem.getName().equals(OWS11Constants.Elem.QN_METADATA)) {
                    readMetadata(elem, reader);
                } else if (elem.getName().equals(WPS100Constants.Elem.QN_DATA_INPUTS_NO_NAMESPACE)) {
                    process.setInputs(readDataInputs(elem, reader));
                } else if (elem.getName().equals(WPS100Constants.Elem.QN_PROCESS_OUTPUTS_NO_NAMESPACE)) {
                    process.setOutputs(readProcessOutputs(elem, reader));
                }
            }
        }

        return process;
    }

    private void readMetadata(StartElement elem,
            XMLEventReader reader) {
        // TODO Auto-generated method stub

    }

    private List<OutputDescription> readProcessOutputs(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {

        List<OutputDescription> outputs = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(WPS100Constants.Elem.QN_OUTPUT_NO_NAMESPACE)) {
                    outputs.add(readOutput(start, reader));
                }
            } else if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if (end.getName().equals(WPS100Constants.Elem.QN_PROCESS_OUTPUTS_NO_NAMESPACE)) {
                    return outputs;
                }

            }
        }
        throw eof();
    }

    private List<InputDescription> readDataInputs(StartElement start,
            XMLEventReader reader) throws XMLStreamException {

        List<InputDescription> inputs = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_INPUT_NO_NAMESPACE)) {
                    inputs.add(readInput(elem, reader));
                }
            } else if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if (end.getName().equals(WPS100Constants.Elem.QN_DATA_INPUTS_NO_NAMESPACE)) {
                    return inputs;
                }

            }
        }
        throw eof();
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
                } else if (start.getName().equals(OWS11Constants.Elem.QN_METADATA)) {
                    readMetadata(elem, reader);
                } else if (start.getName().equals(WPS100Constants.Elem.QN_COMPLEX_OUTPUT_NO_NAMESPACE)) {
                    readComplexData(start, reader, output);
                } else if (start.getName().equals(WPS100Constants.Elem.QN_LITERAL_OUTPUT_NO_NAMESPACE)) {
                    output = new LiteralOutputDescription();
                    readLiteralData(start, reader, (LiteralOutputDescription) output);
                } else if (start.getName().equals(WPS100Constants.Elem.QN_BOUNDING_BOX_OUTPUT_NO_NAMESPACE)) {
                    output = new BoundingBoxOutputDescription();
                    readBoundingBoxData(start, reader, (BoundingBoxOutputDescription) output);
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
                if (elem.getName().equals(OWS11Constants.Elem.QN_ANY_VALUE)) {
                    output.setAnyValue(true);
                } else if (elem.getName().equals(OWS11Constants.Elem.QN_DATA_TYPE)) {
                    readDataType(elem, reader, output);
                } else {
                    throw unexpectedTag(elem);
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_LITERAL_OUTPUT_NO_NAMESPACE)) {
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
                if (elem.getName().equals(OWS11Constants.Elem.QN_ANY_VALUE)) {
                    input.setAnyValue(true);
                } else if (elem.getName().equals(OWS11Constants.Elem.QN_ALLOWED_VALUES)) {
                    readAllowedValues(elem, reader, input);
                } else if (elem.getName().equals(OWS11Constants.Elem.QN_DATA_TYPE)) {
                    readDataType(elem, reader, input);
                } else if (elem.getName().equals(WPS100Constants.Elem.QN_DEFAULT_VALUE_NO_NAMESPACE)) {
                    readDefaultValue(elem, reader, input);
                } else if (elem.getName().equals(WPS100Constants.Elem.QN_UOMS_NO_NAMESPACE)) {
                    readUOMs(elem, reader, input);
                } else {
                    throw unexpectedTag(elem);
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_LITERAL_DATA_NO_NAMESPACE)) {
                    input.setFormats(formats);
                    return;
                }
            }
        }
        throw eof();
    }

    private void readUOMs(StartElement elem,
            XMLEventReader reader,
            LiteralInputDescription input) throws XMLStreamException {
        
        List<UOM> uoms = getDefaultAndSupported(elem, reader);
        
        input.setUoms(uoms);
        
    }
    
    private List<UOM> getDefaultAndSupported(StartElement start,
            XMLEventReader reader) throws XMLStreamException {

        List<UOM> result = new ArrayList<>();
        
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_DEFAULT_NO_NAMESPACE)) {
                    //move forward to ows:UOM element
                    reader.nextTag();
                    String uomString = readUOM(reader);
                    UOM uom = new UOM(uomString, true);
                    if(!result.contains(uom)) {
                        result.add(uom);
                    }
                } else if (elem.getName().equals(WPS100Constants.Elem.QN_SUPPORTED_NO_NAMESPACE)) {
                    
                    List<UOM> supportedUoms = readSupportedUOMs(reader);
                    
                    for (UOM uom : supportedUoms) {
                        if(!result.contains(uom)) {
                            result.add(uom);
                        }
                    }
                } else {
                    throw unexpectedTag(elem);
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_UOMS_NO_NAMESPACE)) {
                    return result;
                }
            }
        }
        throw eof();
    }
    
    private List<UOM> readSupportedUOMs(XMLEventReader reader) throws XMLStreamException {

        List<UOM> result = new ArrayList<>();
        
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(OWS11Constants.Elem.QN_UOM)) {
                    String uomString = readUOM(reader);
                    result.add(new UOM(uomString, false));
                } else {
                    throw unexpectedTag(elem);
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_SUPPORTED_NO_NAMESPACE)) {
                    return result;
                }
            }
        }
        throw eof();
        
    }

    private String readUOM(XMLEventReader reader) throws XMLStreamException {

        String uomString = reader.getElementText();
        return uomString;

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
                } else if (start.getName().equals(WPS100Constants.Elem.QN_DEFAULT_VALUE_NO_NAMESPACE)) {
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

    private void readDataType(StartElement elem,
            XMLEventReader reader,
            LiteralOutputDescription output) {
        getAttribute(elem, OWS11Constants.Attr.QN_REFERENCE).ifPresent(output::setDataType);
    }

    private void readDataType(StartElement elem,
            XMLEventReader reader,
            LiteralInputDescription input) {
        getAttribute(elem, OWS11Constants.Attr.QN_REFERENCE).ifPresent(input::setDataType);
    }

    private void readComplexData(StartElement start,
            XMLEventReader reader,
            OutputDescription output) throws XMLStreamException {

        List<Format> formats = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_DEFAULT_NO_NAMESPACE)) {
                    formats.add(readFormat(true, reader));
                } else if (elem.getName().equals(WPS100Constants.Elem.QN_SUPPORTED_NO_NAMESPACE)) {
                    formats.addAll(readFormats(reader));
                } else {
                    throw unexpectedTag(elem);
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_COMPLEX_OUTPUT_NO_NAMESPACE)) {
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
                if (elem.getName().equals(WPS100Constants.Elem.QN_DEFAULT_NO_NAMESPACE)) {
                    formats.add(readFormat(true, reader));
                } else if (elem.getName().equals(WPS100Constants.Elem.QN_SUPPORTED_NO_NAMESPACE)) {
                    formats.addAll(readFormats(reader));
                } else {
                    throw unexpectedTag(elem);
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_COMPLEX_DATA_NO_NAMESPACE)) {
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
                if (start.getName().equals(OWS11Constants.Elem.QN_TITLE)) {
                    title = reader.getElementText();
                } else if (start.getName().equals(OWS11Constants.Elem.QN_ABSTRACT)) {
                    abstrakt = reader.getElementText();
                } else if (start.getName().equals(OWS11Constants.Elem.QN_IDENTIFIER)) {
                    id = reader.getElementText();
                } else if (start.getName().equals(OWS11Constants.Elem.QN_METADATA)) {
                    readMetadata(start, reader);
                } else if (start.getName().equals(WPS100Constants.Elem.QN_COMPLEX_DATA_NO_NAMESPACE)) {
                    input = new ComplexInputDescription();
                    readComplexData(start, reader, input);
                } else if (start.getName().equals(WPS100Constants.Elem.QN_LITERAL_DATA_NO_NAMESPACE)) {
                    input = new LiteralInputDescription();
                    readLiteralData(start, reader, (LiteralInputDescription) input);
                } else if (start.getName().equals(WPS100Constants.Elem.QN_BOUNDING_BOX_DATA_NO_NAMESPACE)) {
                    input = new BoundingBoxInputDescription();
                    readBoundingBoxData(start, reader, (BoundingBoxInputDescription) input);
                } else {
                    throw unexpectedTag(start);
                }
            } else if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if (end.getName().equals(WPS100Constants.Elem.QN_INPUT_NO_NAMESPACE)) {
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
            BoundingBoxOutputDescription output) throws XMLStreamException {

        List<Format> formats = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_BOUNDING_BOX_OUTPUT_NO_NAMESPACE)) {
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
                if (elem.getName().equals(WPS100Constants.Elem.QN_DEFAULT_NO_NAMESPACE)) {
                    readCRS(true, reader, input);
                } else if (elem.getName().equals(WPS100Constants.Elem.QN_SUPPORTED_NO_NAMESPACE)) {
                    readSupportedCRS(elem, reader, input);
                } else {
                    throw unexpectedTag(elem);
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_BOUNDING_BOX_DATA_NO_NAMESPACE)) {
                    input.setFormats(formats);
                    return;
                }
            }
        }
        throw eof();
    }

    // TODO implement
    private void readCRS(boolean b,
            XMLEventReader reader,
            BoundingBoxInputDescription input) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_CRS_NO_NAMESPACE)) {
                    return;
                }
            }
        }

    }

    // TODO implement
    private void readSupportedCRS(StartElement start,
            XMLEventReader reader,
            BoundingBoxInputDescription input) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_SUPPORTED_NO_NAMESPACE)) {
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

    // TODO implement
    private void readAllowedValues(StartElement start,
            XMLEventReader reader,
            LiteralInputDescription input) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(OWS11Constants.Elem.QN_ALLOWED_VALUES)) {
                    return;
                }
            }
        }

    }

    private Collection<? extends Format> readFormats(XMLEventReader reader) throws XMLStreamException {

        List<Format> formats = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_FORMAT_NO_NAMESPACE)) {
                    formats.add(readFormat(false, reader));
                } else {
                    throw unexpectedTag(elem);
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_SUPPORTED_NO_NAMESPACE)) {
                    return formats;
                }
            }
        }
        throw eof();
    }

    private Format readFormat(boolean isDefaultFormat,
            XMLEventReader reader) throws XMLStreamException {
        Format format = new Format();
        format.setDefault(isDefaultFormat);

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_MIME_TYPE_NO_NAMESPACE)) {
                    format.setMimeType(reader.getElementText());
                } else if (elem.getName().equals(WPS100Constants.Elem.QN_SCHEMA_NO_NAMESPACE)) {
                    format.setSchema(reader.getElementText());
                } else if (elem.getName().equals(WPS100Constants.Elem.QN_ENCODING_NO_NAMESPACE)) {
                    format.setEncoding(reader.getElementText());
                } else if (elem.getName().equals(WPS100Constants.Elem.QN_FORMAT_NO_NAMESPACE)) {
                    continue;
                } else {
                    throw unexpectedTag(elem);
                }
            } else if (event.isEndElement()) {
                EndElement elem = event.asEndElement();
                if (elem.getName().equals(WPS100Constants.Elem.QN_FORMAT_NO_NAMESPACE)) {
                    return format;
                }
            }
        }
        throw eof();
    }

    @Override
    public Set<StreamReaderKey> getKeys() {
        return Collections.emptySet();
    }

}
