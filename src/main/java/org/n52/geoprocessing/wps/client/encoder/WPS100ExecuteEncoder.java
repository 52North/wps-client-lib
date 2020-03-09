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
package org.n52.geoprocessing.wps.client.encoder;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.inject.Provider;
import javax.xml.stream.XMLStreamException;

import org.n52.geoprocessing.wps.client.encoder.stream.ExecuteRequest100Encoder;
import org.n52.geoprocessing.wps.client.model.execution.Execute;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.stream.xml.ElementXmlStreamWriter;
import org.n52.svalbard.encode.stream.xml.ElementXmlStreamWriterRepository;
import org.n52.svalbard.encode.stream.xml.XmlStreamWritingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WPS100ExecuteEncoder {

    private static Logger LOGGER = LoggerFactory.getLogger(WPS100ExecuteEncoder.class);

    private static final List<Provider<ElementXmlStreamWriter>> ELEMENT_WRITERS =
            Arrays.asList(ExecuteRequest100Encoder::new);

    private Execute execute;

    public WPS100ExecuteEncoder(Execute execute) {
        this.execute = execute;
    }

    public static String encode(Execute execute) throws XMLStreamException, EncodingException {

        ExecuteRequest100Encoder executeRequestWriter = new ExecuteRequest100Encoder();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        executeRequestWriter.setContext(new XmlStreamWritingContext(byteArrayOutputStream,
                new ElementXmlStreamWriterRepository(ELEMENT_WRITERS)::get));

        executeRequestWriter.writeElement(execute);

        return new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
    }

}
