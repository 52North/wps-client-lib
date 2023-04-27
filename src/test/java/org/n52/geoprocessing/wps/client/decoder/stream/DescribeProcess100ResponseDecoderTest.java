/*
 * ﻿Copyright (C) 2023 52°North Initiative for Geospatial Open Source
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


import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.n52.geoprocessing.wps.client.model.Process;


/**
 *
 * @author Matthes Rieke <m.rieke@52north.org>
 */
public class DescribeProcess100ResponseDecoderTest {

    @Test
    public void testEsaGpodParsing() throws XMLStreamException {
        DescribeProcess100ResponseDecoder decoder = new DescribeProcess100ResponseDecoder();
        XMLEventReader xmlReader = XMLInputFactory.newInstance()
                .createXMLEventReader(new InputStreamReader(this.getClass().getResourceAsStream("/esa-gpod-process-description.xml"),
                        StandardCharsets.UTF_8));
        List<Process> result = decoder.readElement(xmlReader);

        Assert.assertThat(result, CoreMatchers.notNullValue());
        Assert.assertThat(result.size(), CoreMatchers.is(1));

        Process process = result.get(0);

        Assert.assertThat(process.getId(), CoreMatchers.equalTo("GPODTEST"));
        Assert.assertThat(process.getInputs().size(), CoreMatchers.is(2));
        Assert.assertThat(process.getOutputs().size(), CoreMatchers.is(1));
    }



}
