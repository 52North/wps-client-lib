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

import org.n52.geoprocessing.wps.client.model.ComplexInputDescription;
import org.n52.geoprocessing.wps.client.model.Format;
import org.n52.geoprocessing.wps.client.model.InputDescription;
import org.n52.geoprocessing.wps.client.model.LiteralInputDescription;
import org.n52.geoprocessing.wps.client.model.LiteralOutputDescription;
import org.n52.geoprocessing.wps.client.model.OutputDescription;
import org.n52.javaps.service.xml.OWSConstants;
import org.n52.javaps.service.xml.WPSConstants;
import org.n52.svalbard.decode.stream.StreamReaderKey;
import org.n52.svalbard.decode.stream.xml.AbstractElementXmlStreamReader;

public class DescribeProcessResponseDecoder extends AbstractElementXmlStreamReader {

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

    private List<org.n52.geoprocessing.wps.client.model.Process> readProcessOfferings(StartElement elem,
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
                    readDataType(elem, reader, output);
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
        getAttribute(elem, OWSConstants.Attr.AN_REFERENCE).ifPresent(output::setDataType);
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
            LiteralInputDescription input) throws XMLStreamException {

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(OWSConstants.Elem.QN_ANY_VALUE)) {
                    input.setAnyValue(true);
                } else if (start.getName().equals(OWSConstants.Elem.QN_ALLOWED_VALUES)) {
                    readAllowedValues(elem, reader, input);
                } else if (start.getName().equals(OWSConstants.Elem.QN_DATA_TYPE)) {
                    readDataType(elem, reader, input);
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
            LiteralInputDescription input) {
        getAttribute(elem, OWSConstants.Attr.AN_REFERENCE).ifPresent(input::setDataType);        
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

}
