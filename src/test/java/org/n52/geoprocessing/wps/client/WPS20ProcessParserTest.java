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
package org.n52.geoprocessing.wps.client;

import org.junit.Test;
import org.n52.geoprocessing.wps.client.model.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WPS20ProcessParserTest {

    private static Logger LOGGER = LoggerFactory.getLogger(WPS20ProcessParserTest.class);

    @Test
    public void testParseWPS20Process() throws Exception {
//
//        ProcessOfferingsDocument offeringsDocument = ProcessOfferingsDocument.Factory
//                .parse(WPS20ProcessParserTest.class.getResourceAsStream("WPS20ProcessDescription.xml"));

        Process process = WPS20ProcessParser.parseProcess(WPS20ProcessParserTest.class.getResourceAsStream("WPS20ProcessDescription.xml"));

        LOGGER.info(process.getInputs().get(0).getId());
    }

}
