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
import java.util.Set;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.n52.geoprocessing.wps.client.model.Address;
import org.n52.geoprocessing.wps.client.model.ContactInfo;
import org.n52.geoprocessing.wps.client.model.Phone;
import org.n52.geoprocessing.wps.client.model.Process;
import org.n52.geoprocessing.wps.client.model.ServiceContact;
import org.n52.geoprocessing.wps.client.model.ServiceIdentification;
import org.n52.geoprocessing.wps.client.model.ServiceProvider;
import org.n52.geoprocessing.wps.client.model.WPSCapabilities;
import org.n52.javaps.service.xml.OWSConstants;
import org.n52.javaps.service.xml.WPSConstants;
import org.n52.svalbard.decode.stream.StreamReaderKey;
import org.n52.svalbard.decode.stream.xml.AbstractElementXmlStreamReader;

public class GetCapabilitiesResponseDecoder extends AbstractElementXmlStreamReader {

    public WPSCapabilities readElement(XMLEventReader reader) throws XMLStreamException {

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(WPSConstants.Elem.QN_CAPABILITIES)) {
                    return readCapabilities(start, reader);
                }
            }
        }
        throw eof();
    }

    public WPSCapabilities readCapabilities(StartElement start,
            XMLEventReader reader) throws XMLStreamException {

        WPSCapabilities wpsCapabilities = new WPSCapabilities();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(OWSConstants.Elem.QN_SERVICE_IDENTIFICATION)) {
                    wpsCapabilities.setServiceIdentification(readServiceIdentification(elem, reader));
                } else if (elem.getName().equals(OWSConstants.Elem.QN_SERVICE_PROVIDER)) {
                    wpsCapabilities.setServiceProvider(readServiceProvider(elem, reader));
                } else if (elem.getName().equals(OWSConstants.Elem.QN_OPERATIONS_METADATA)) {
                    readOperationsMetadata(elem, reader);
                } else if (elem.getName().equals(OWSConstants.Elem.QN_LANGUAGES)) {
                    readLanguages(elem, reader);
                } else if (elem.getName().equals(WPSConstants.Elem.QN_CONTENTS)) {
                    wpsCapabilities.setProcesses(readContents(elem, reader));
                } else {
                    throw unexpectedTag(elem);
                }
            } else if (event.isEndElement()) {
                return wpsCapabilities;
            }
        }
        throw eof();
    }

    private void readLanguages(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();

            if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();

                if (endElement.getName().equals(OWSConstants.Elem.QN_OPERATIONS_METADATA)) {
                    return;
                }
            }
        }
        throw eof();
    }

    private void readOperationsMetadata(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();

            if (event.isEndElement()) {

                EndElement endElement = event.asEndElement();

                if (endElement.getName().equals(OWSConstants.Elem.QN_LANGUAGES)) {
                    return;
                }
            }
        }
        throw eof();
    }

    private List<Process> readContents(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {
        // TODO Auto-generated method stub
        List<Process> processes = new ArrayList<>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(WPSConstants.Elem.QN_PROCESS_SUMMARY)) {
                    processes.add(createProcessSummary(start, reader));
                }
            } else if (event.isEndElement()) {

                EndElement endElement = event.asEndElement();

                if (endElement.getName().equals(WPSConstants.Elem.QN_CONTENTS)) {
                    return processes;
                }
            }
        }

        throw eof();
    }

    private Process createProcessSummary(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {
        Process process = new Process();

        String jobControlOptions = getAttribute(elem, WPSConstants.Attr.AN_JOB_CONTROL_OPTIONS).get();
        if (jobControlOptions.contains("async-execute")) {
            process.setStatusSupported(true);
        }

        String outputTransmission = getAttribute(elem, WPSConstants.Attr.AN_OUTPUT_TRANSMISSION).get();
        if (outputTransmission.contains("reference")) {
            process.setReferenceSupported(true);
        }

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(OWSConstants.Elem.QN_TITLE)) {
                    process.setTitle(reader.getElementText());
                } else if (start.getName().equals(OWSConstants.Elem.QN_ABSTRACT)) {
                    process.setAbstract(reader.getElementText());
                } else if (start.getName().equals(OWSConstants.Elem.QN_IDENTIFIER)) {
                    process.setId(reader.getElementText());
                } else if (start.getName().equals(OWSConstants.Elem.QN_KEYWORDS)) {
                    // process.setKeyWords(readKeywords(start, reader));//TODO
                    // just consume keywords for now
                    readKeywords(start, reader);
                } else if (start.getName().equals(OWSConstants.Elem.QN_METADATA)) {
                    // do nothing
                    continue;
                } else {
                    throw unexpectedTag(start);
                }
            } else if (event.isEndElement()) {

                EndElement endElement = event.asEndElement();

                if (endElement.getName().equals(WPSConstants.Elem.QN_PROCESS_SUMMARY)) {
                    return process;
                }
            }
        }

        throw eof();
    }

    private ServiceProvider readServiceProvider(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {
        ServiceProvider serviceProvider = new ServiceProvider();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(OWSConstants.Elem.QN_PROVIDER_NAME)) {
                    serviceProvider.setProviderName(reader.getElementText());
                } else if (start.getName().equals(OWSConstants.Elem.QN_PROVIDER_SITE)) {
                    serviceProvider.setProviderSite(reader.getElementText());
                } else if (start.getName().equals(OWSConstants.Elem.QN_SERVICE_CONTACT)) {
                    serviceProvider.setServiceContact(readServiceContact(elem, reader));
                } else {
                    throw unexpectedTag(start);
                }
            } else if (event.isEndElement()) {

                EndElement endElement = event.asEndElement();

                if (endElement.getName().equals(OWSConstants.Elem.QN_SERVICE_PROVIDER)) {
                    return serviceProvider;
                }
            }
        }

        throw eof();
    }

    private ServiceContact readServiceContact(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {
        ServiceContact serviceContact = new ServiceContact();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(OWSConstants.Elem.QN_INDIVIDUAL_NAME)) {
                    serviceContact.setIndividualName(reader.getElementText());
                } else if (start.getName().equals(OWSConstants.Elem.QN_POSITION_NAME)) {
                    serviceContact.setPositionName(reader.getElementText());
                } else if (start.getName().equals(OWSConstants.Elem.QN_CONTACT_INFO)) {
                    serviceContact.setContactInfo(readContactInfo(elem, reader));
                } else if (start.getName().equals(OWSConstants.Elem.QN_ROLE)) {
                    // TODO
                    continue;
                } else {
                    throw unexpectedTag(start);
                }
            } else if (event.isEndElement()) {

                EndElement endElement = event.asEndElement();

                if (endElement.getName().equals(OWSConstants.Elem.QN_SERVICE_CONTACT)) {
                    return serviceContact;
                }
            }
        }

        throw eof();
    }

    private ContactInfo readContactInfo(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {

        ContactInfo contactInfo = new ContactInfo();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(OWSConstants.Elem.QN_ADDRESS)) {
                    contactInfo.setAddress(readAddress(start, reader));
                } else if (start.getName().equals(OWSConstants.Elem.QN_PHONE)) {
                    contactInfo.setPhone(readPhone(start, reader));
                } else if (start.getName().equals(OWSConstants.Elem.QN_ONLINE_RESOURCE)) {
                    // TODO
                    continue;
                } else if (start.getName().equals(OWSConstants.Elem.QN_HOURS_OF_SERVICE)) {
                    // TODO
                    continue;
                } else if (start.getName().equals(OWSConstants.Elem.QN_CONTACT_INSTRUCTIONS)) {
                    // TODO
                    continue;
                } else {
                    throw unexpectedTag(start);
                }
            } else if (event.isEndElement()) {

                EndElement endElement = event.asEndElement();

                if (endElement.getName().equals(OWSConstants.Elem.QN_CONTACT_INFO)) {
                    return contactInfo;
                }
            }
        }

        throw eof();
    }

    private Phone readPhone(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {
        Phone phone = new Phone();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(OWSConstants.Elem.QN_VOICE)) {
                    phone.setVoice(reader.getElementText());
                } else if (start.getName().equals(OWSConstants.Elem.QN_FACSIMILE)) {
                    phone.setFacsimile(reader.getElementText());
                } else {
                    throw unexpectedTag(start);
                }
            } else if (event.isEndElement()) {

                EndElement endElement = event.asEndElement();

                if (endElement.getName().equals(OWSConstants.Elem.QN_PHONE)) {
                    return phone;
                }
            }
        }

        throw eof();
    }

    private Address readAddress(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {

        Address address = new Address();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(OWSConstants.Elem.QN_DELIVERY_POINT)) {
                    address.setDeliveryPoint(reader.getElementText());
                } else if (start.getName().equals(OWSConstants.Elem.QN_CITY)) {
                    address.setCity(reader.getElementText());
                } else if (start.getName().equals(OWSConstants.Elem.QN_ADMINISTRATIVE_AREA)) {
                    address.setAdministrativeArea(reader.getElementText());
                } else if (start.getName().equals(OWSConstants.Elem.QN_POSTAL_CODE)) {
                    address.setPostalCode(reader.getElementText());
                } else if (start.getName().equals(OWSConstants.Elem.QN_COUNTRY)) {
                    address.setCountry(reader.getElementText());
                } else if (start.getName().equals(OWSConstants.Elem.QN_ELECTRONIC_MAIL_ADDRESS)) {
                    address.setElectronicMailAddress(reader.getElementText());
                } else {
                    throw unexpectedTag(start);
                }
            } else if (event.isEndElement()) {

                EndElement endElement = event.asEndElement();

                if (endElement.getName().equals(OWSConstants.Elem.QN_ADDRESS)) {
                    return address;
                }
            }
        }

        throw eof();
    }

    private ServiceIdentification readServiceIdentification(StartElement elem,
            XMLEventReader reader) throws XMLStreamException {

        ServiceIdentification serviceIdentification = new ServiceIdentification();

        List<String> accessConstraints = new ArrayList<String>();
        List<String> serviceTypeVersions = new ArrayList<String>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().equals(OWSConstants.Elem.QN_TITLE)) {
                    serviceIdentification.setTitle(reader.getElementText());
                } else if (start.getName().equals(OWSConstants.Elem.QN_ABSTRACT)) {
                    serviceIdentification.setAbstract(reader.getElementText());
                } else if (start.getName().equals(OWSConstants.Elem.QN_KEYWORDS)) {
                    serviceIdentification.setKeyWords(readKeywords(start, reader));
                } else if (start.getName().equals(OWSConstants.Elem.QN_ACCESS_CONSTRAINTS)) {
                    accessConstraints.add(reader.getElementText());
                } else if (start.getName().equals(OWSConstants.Elem.QN_FEES)) {
                    serviceIdentification.setFees(reader.getElementText());
                } else if (start.getName().equals(OWSConstants.Elem.QN_SERVICE_TYPE_VERSION)) {
                    serviceTypeVersions.add(reader.getElementText());
                } else if (start.getName().equals(OWSConstants.Elem.QN_SERVICE_TYPE)) {
                    // do nothing
                    continue;
                } else {
                    throw unexpectedTag(start);
                }
            } else if (event.isEndElement()) {

                EndElement endElement = event.asEndElement();

                if (endElement.getName().equals(OWSConstants.Elem.QN_SERVICE_IDENTIFICATION)) {
                    serviceIdentification.setServiceTypeVersions(serviceTypeVersions);
                    serviceIdentification.setAccessConstraints(accessConstraints);

                    return serviceIdentification;
                }
            }
        }

        throw eof();
    }

    private List<String> readKeywords(StartElement start,
            XMLEventReader reader) throws XMLStreamException {
        List<String> keywords = new ArrayList<String>();

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement elem = event.asStartElement();
                if (elem.getName().equals(OWSConstants.Elem.QN_KEYWORD)) {
                    keywords.add(reader.getElementText());
                } else {
                    throw unexpectedTag(elem);
                }
            } else if (event.isEndElement()) {
                return keywords;
            }
        }
        throw eof();
    }

    @Override
    public Set<StreamReaderKey> getKeys() {
        return Collections.emptySet();
    }

}
