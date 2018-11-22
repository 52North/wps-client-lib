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
package org.n52.geoprocessing.wps.client;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStreamReader;
import java.util.List;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.junit.Test;
import org.n52.geoprocessing.wps.client.model.ExceptionReport;
import org.n52.geoprocessing.wps.client.model.Result;
import org.n52.geoprocessing.wps.client.model.StatusInfo;
import org.n52.geoprocessing.wps.client.model.WPSCapabilities;
import org.n52.geoprocessing.wps.client.model.execution.BoundingBox;
import org.n52.geoprocessing.wps.client.model.execution.BoundingBoxData;
import org.n52.geoprocessing.wps.client.model.execution.ComplexData;
import org.n52.geoprocessing.wps.client.model.execution.Data;
import org.n52.geoprocessing.wps.client.model.execution.LiteralData;
import org.n52.geoprocessing.wps.client.xml.WPSResponseReader;
import org.n52.shetland.ogc.wps.JobStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WPSResponseReaderTest {

    private static Logger LOGGER = LoggerFactory.getLogger(WPSResponseReaderTest.class);

    @Test
    public void testReadCapabilities100(){

        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLInputFactory.newInstance().createXMLEventReader(new InputStreamReader(WPSResponseReaderTest.class.getResourceAsStream("52n-capabilities-response100.xml")));
        } catch (XMLStreamException | FactoryConfigurationError e) {
            LOGGER.error(e.getMessage());
            fail();
        }

        try {
            Object o = new WPSResponseReader().readElement(xmlReader);
            assertTrue(o != null);
            assertTrue(o instanceof WPSCapabilities);
        } catch (XMLStreamException e) {
            LOGGER.error(e.getMessage());
            fail();
        }
    }

    @Test
    public void testReadCapabilities20(){

        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLInputFactory.newInstance().createXMLEventReader(new InputStreamReader(WPSResponseReaderTest.class.getResourceAsStream("52n-capabilities-response20.xml")));
        } catch (XMLStreamException | FactoryConfigurationError e) {
            LOGGER.error(e.getMessage());
            fail();
        }

        try {
            Object o = new WPSResponseReader().readElement(xmlReader);
            assertTrue(o != null);
            assertTrue(o instanceof WPSCapabilities);
        } catch (XMLStreamException e) {
            LOGGER.error(e.getMessage());
            fail();
        }
    }

    @Test
    public void testReadDescribeProcess100(){

        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLInputFactory.newInstance().createXMLEventReader(new InputStreamReader(WPSResponseReaderTest.class.getResourceAsStream("52n-describeprocess-response100.xml")));
        } catch (XMLStreamException | FactoryConfigurationError e) {
            LOGGER.error(e.getMessage());
            fail();
        }

        try {
            Object o = new WPSResponseReader().readElement(xmlReader);
            assertTrue(o != null);
            assertTrue(o instanceof List);
        } catch (XMLStreamException e) {
            LOGGER.error(e.getMessage());
            fail();
        }
    }

    @Test
    public void testReadDescribeProcess200(){

        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLInputFactory.newInstance().createXMLEventReader(new InputStreamReader(WPSResponseReaderTest.class.getResourceAsStream("52n-describeprocess-response200.xml")));
        } catch (XMLStreamException | FactoryConfigurationError e) {
            LOGGER.error(e.getMessage());
            fail();
        }

        try {
            Object o = new WPSResponseReader().readElement(xmlReader);
            assertTrue(o != null);
            assertTrue(o instanceof List);
        } catch (XMLStreamException e) {
            LOGGER.error(e.getMessage());
            fail();
        }
    }

    @Test
    public void testReadExceptionReport20(){

        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLInputFactory.newInstance().createXMLEventReader(new InputStreamReader(WPSResponseReaderTest.class.getResourceAsStream("52n-exception-report20.xml")));
        } catch (XMLStreamException | FactoryConfigurationError e) {
            LOGGER.error(e.getMessage());
            fail();
        }

        try {
            Object o = new WPSResponseReader().readElement(xmlReader);
            assertTrue(o != null);
            assertTrue(o instanceof ExceptionReport);
        } catch (XMLStreamException e) {
            LOGGER.error(e.getMessage());
            fail();
        }
    }

    @Test
    public void testReadExceptionReport100(){

        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLInputFactory.newInstance().createXMLEventReader(new InputStreamReader(WPSResponseReaderTest.class.getResourceAsStream("52n-exception-report100.xml")));
        } catch (XMLStreamException | FactoryConfigurationError e) {
            LOGGER.error(e.getMessage());
            fail();
        }

        try {
            Object o = new WPSResponseReader().readElement(xmlReader);
            assertTrue(o != null);
            assertTrue(o instanceof ExceptionReport);
        } catch (XMLStreamException e) {
            LOGGER.error(e.getMessage());
            fail();
        }
    }

    @Test
    public void testReadStatusInfoAccepted20(){

        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLInputFactory.newInstance().createXMLEventReader(new InputStreamReader(WPSResponseReaderTest.class.getResourceAsStream("52n-getstatus-response20.xml")));
        } catch (XMLStreamException | FactoryConfigurationError e) {
            LOGGER.error(e.getMessage());
            fail();
        }

        try {
            Object o = new WPSResponseReader().readElement(xmlReader);
            assertTrue(o != null);
            assertTrue(o instanceof StatusInfo);

            StatusInfo statusInfo = (StatusInfo)o;

            assertTrue(statusInfo.getStatus().equals(JobStatus.accepted()));
        } catch (XMLStreamException e) {
            LOGGER.error(e.getMessage());
            fail();
        }
    }

    @Test
    public void testReadStatusInfoAccepted100(){

        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLInputFactory.newInstance().createXMLEventReader(new InputStreamReader(WPSResponseReaderTest.class.getResourceAsStream("52n-execute-response100-status-accepted.xml")));
        } catch (XMLStreamException | FactoryConfigurationError e) {
            LOGGER.error(e.getMessage());
            fail();
        }

        try {
            Object o = new WPSResponseReader().readElement(xmlReader);
            assertTrue(o != null);
            assertTrue(o instanceof StatusInfo);

            StatusInfo statusInfo = (StatusInfo)o;

            assertTrue(statusInfo.getStatus().equals(JobStatus.accepted()));
        } catch (XMLStreamException e) {
            LOGGER.error(e.getMessage());
            fail();
        }
    }

    @Test
    public void testReadStatusInfoSucceeded100(){

        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLInputFactory.newInstance().createXMLEventReader(new InputStreamReader(WPSResponseReaderTest.class.getResourceAsStream("52n-execute-response100-status-succeeded-outputs-inline.xml")));
        } catch (XMLStreamException | FactoryConfigurationError e) {
            LOGGER.error(e.getMessage());
            fail();
        }

        try {
            Object o = new WPSResponseReader().readElement(xmlReader);
            assertTrue(o != null);
            assertTrue(o instanceof StatusInfo);

            StatusInfo statusInfo = (StatusInfo)o;

            assertTrue(statusInfo.getStatus().equals(JobStatus.succeeded()));
            assertTrue(statusInfo.getResult() != null);

            Result result = statusInfo.getResult();

            assertTrue(result.getOutputs().size() == 3);

            for (Data output : result.getOutputs()) {
                if(output.getId().equals("ComplexOutputData")){
                    assertTrue(output.getValue().equals("<testElement>testValue</testElement>"));
                } else if(output.getId().equals("LiteralOutputData")){
                    assertTrue(output.getValue().equals("Hello!"));
                } else if(output.getId().equals("BoundingBoxOutputData")){
                }
            }

        } catch (XMLStreamException e) {
            LOGGER.error(e.getMessage());
            fail();
        }
    }

    @Test
    public void testReadStatusInfoSucceededComplexOutputText100(){

        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLInputFactory.newInstance().createXMLEventReader(new InputStreamReader(WPSResponseReaderTest.class.getResourceAsStream("52n-execute-response100-status-succeeded-outputs-inline-complex-text.xml")));
        } catch (XMLStreamException | FactoryConfigurationError e) {
            LOGGER.error(e.getMessage());
            fail();
        }

        try {
            Object o = new WPSResponseReader().readElement(xmlReader);
            assertTrue(o != null);
            assertTrue(o instanceof StatusInfo);

            StatusInfo statusInfo = (StatusInfo)o;

            assertTrue(statusInfo.getStatus().equals(JobStatus.succeeded()));
            assertTrue(statusInfo.getResult() != null);

            Result result = statusInfo.getResult();

            assertTrue(result.getOutputs().size() == 3);

            for (Data output : result.getOutputs()) {
                if(output.getId().equals("ComplexOutputData")){
                    assertTrue(output.getValue().equals("{\"test\":\"json\"}"));
                } else if(output.getId().equals("LiteralOutputData")){
                    assertTrue(output.getValue().equals("Hello!"));
                } else if(output.getId().equals("BBOXOutputData")){
                }
            }

        } catch (XMLStreamException e) {
            LOGGER.error(e.getMessage());
            fail();
        }
    }

    @Test
    public void testReadStatusInfoSucceededComplexOutputCDATA100(){

        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLInputFactory.newInstance().createXMLEventReader(new InputStreamReader(WPSResponseReaderTest.class.getResourceAsStream("52n-execute-response100-status-succeeded-outputs-inline-complex-cdata.xml")));
        } catch (XMLStreamException | FactoryConfigurationError e) {
            LOGGER.error(e.getMessage());
            fail();
        }

        try {
            Object o = new WPSResponseReader().readElement(xmlReader);
            assertTrue(o != null);
            assertTrue(o instanceof StatusInfo);

            StatusInfo statusInfo = (StatusInfo)o;

            assertTrue(statusInfo.getStatus().equals(JobStatus.succeeded()));
            assertTrue(statusInfo.getResult() != null);

            Result result = statusInfo.getResult();

            assertTrue(result.getOutputs().size() == 3);

            for (Data output : result.getOutputs()) {
                if(output.getId().equals("ComplexOutputData")){
                    assertTrue(output.getValue().equals("{\"test\":\"json\"}"));
                } else if(output.getId().equals("LiteralOutputData")){
                    assertTrue(output.getValue().equals("Hello!"));
                } else if(output.getId().equals("BBOXOutputData")){
                    assertTrue(output instanceof BoundingBoxData);
                    checkBBoxData((BoundingBoxData)output);
                }
            }

        } catch (XMLStreamException e) {
            LOGGER.error(e.getMessage());
            fail();
        }
    }

    @Test
    public void testReadStatusInfoSucceededOutputReference100(){

        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLInputFactory.newInstance().createXMLEventReader(new InputStreamReader(WPSResponseReaderTest.class.getResourceAsStream("52n-execute-response100-status-succeeded-outputs-inline-reference.xml")));
        } catch (XMLStreamException | FactoryConfigurationError e) {
            LOGGER.error(e.getMessage());
            fail();
        }

        try {
            Object o = new WPSResponseReader().readElement(xmlReader);
            assertTrue(o != null);
            assertTrue(o instanceof StatusInfo);

            StatusInfo statusInfo = (StatusInfo)o;

            assertTrue(statusInfo.getStatus().equals(JobStatus.succeeded()));
            assertTrue(statusInfo.getResult() != null);

            Result result = statusInfo.getResult();

            assertTrue(result.getOutputs().size() == 3);

            for (Data output : result.getOutputs()) {
                if(output.getId().equals("ComplexOutputData")){
                    assertTrue(output instanceof ComplexData);
                    ComplexData complexData = (ComplexData)output;
                    assertTrue(complexData.isReference());
                    assertTrue(complexData.getReference().getHref().toString().equals("http://geoprocessing.demo.52north.org:8080/latest-wps/RetrieveResultServlet?id=c682fc28-321e-4567-a433-5a6945cffe45ComplexOutputData.d2a0002f-d203-47a9-860b-72cbfb0fbff8"));
                } else if(output.getId().equals("LiteralOutputData")){
                    assertTrue(output instanceof LiteralData);
                    assertTrue(output.getValue().equals("Hello!"));
                } else if(output.getId().equals("BBOXOutputData")){
                    assertTrue(output instanceof BoundingBoxData);
                    checkBBoxData((BoundingBoxData)output);
                }
            }

        } catch (XMLStreamException e) {
            LOGGER.error(e.getMessage());
            fail();
        }
    }

    @Test
    public void testReadResult200(){

        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLInputFactory.newInstance().createXMLEventReader(new InputStreamReader(WPSResponseReaderTest.class.getResourceAsStream("52n-execute-response200-outputs-inline.xml")));
        } catch (XMLStreamException | FactoryConfigurationError e) {
            LOGGER.error(e.getMessage());
            fail();
        }

        try {
            Object o = new WPSResponseReader().readElement(xmlReader);
            assertTrue(o != null);
            assertTrue(o instanceof Result);

            Result result = (Result)o;

            assertTrue(result.getOutputs().size() == 3);

            for (Data output : result.getOutputs()) {
                if(output.getId().equals("ComplexOutputData")){
                    assertTrue(output.getValue().equals("<test>value</test>"));
                } else if(output.getId().equals("LiteralOutputData")){
                    assertTrue(output.getValue().equals("0.05"));
                } else if(output.getId().equals("BoundingBoxOutputData")){
                    assertTrue(output instanceof BoundingBoxData);
                    checkBBoxData((BoundingBoxData)output);
                }
            }

        } catch (XMLStreamException e) {
            LOGGER.error(e.getMessage());
            fail();
        }
    }

    @Test
    public void testReadResult200ComplexOutputText(){

        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLInputFactory.newInstance().createXMLEventReader(new InputStreamReader(WPSResponseReaderTest.class.getResourceAsStream("52n-execute-response200-outputs-inline-complexoutput-text.xml")));
        } catch (XMLStreamException | FactoryConfigurationError e) {
            LOGGER.error(e.getMessage());
            fail();
        }

        try {
            Object o = new WPSResponseReader().readElement(xmlReader);
            assertTrue(o != null);
            assertTrue(o instanceof Result);

            Result result = (Result)o;

            assertTrue(result.getOutputs().size() == 3);

            for (Data output : result.getOutputs()) {
                if(output.getId().equals("ComplexOutputData")){
                    assertTrue(output.getValue().equals("{\"test\":\"json\"}"));
                } else if(output.getId().equals("LiteralOutputData")){
                    assertTrue(output.getValue().equals("0.05"));
                } else if(output.getId().equals("BoundingBoxOutputData")){
                    assertTrue(output instanceof BoundingBoxData);
                    checkBBoxData((BoundingBoxData)output);
                }
            }

        } catch (XMLStreamException e) {
            LOGGER.error(e.getMessage());
            fail();
        }
    }

    @Test
    public void testReadResult200ComplexOutputCDATA(){

        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLInputFactory.newInstance().createXMLEventReader(new InputStreamReader(WPSResponseReaderTest.class.getResourceAsStream("52n-execute-response200-outputs-inline-complexoutput-cdata.xml")));
        } catch (XMLStreamException | FactoryConfigurationError e) {
            LOGGER.error(e.getMessage());
            fail();
        }

        try {
            Object o = new WPSResponseReader().readElement(xmlReader);
            assertTrue(o != null);
            assertTrue(o instanceof Result);

            Result result = (Result)o;

            assertTrue(result.getOutputs().size() == 3);

            for (Data output : result.getOutputs()) {
                if(output.getId().equals("ComplexOutputData")){
                    assertTrue(output.getValue().equals("{\"test\":\"json\"}"));
                } else if(output.getId().equals("LiteralOutputData")){
                    assertTrue(output.getValue().equals("0.05"));
                } else if(output.getId().equals("BoundingBoxOutputData")){
                    assertTrue(output instanceof BoundingBoxData);
                    checkBBoxData((BoundingBoxData)output);
                }
            }

        } catch (XMLStreamException e) {
            LOGGER.error(e.getMessage());
            fail();
        }
    }
    
    @Test
    public void testReadResult200ComplexOutputReference(){
        
        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLInputFactory.newInstance().createXMLEventReader(new InputStreamReader(WPSResponseReaderTest.class.getResourceAsStream("52n-getresult-response200.xml")));
        } catch (XMLStreamException | FactoryConfigurationError e) {
            LOGGER.error(e.getMessage());
            fail();
        }
        
        try {
            Object o = new WPSResponseReader().readElement(xmlReader);
            assertTrue(o != null);
            assertTrue(o instanceof Result);
            
            Result result = (Result)o;
            
            assertTrue(result.getOutputs().size() == 1);
            
            for (Data output : result.getOutputs()) {
                if(output.getId().equals("result")){
                    
                    assertTrue(output instanceof ComplexData);
                    
                    ComplexData complexData = (ComplexData)output;
                    
                    assertTrue(complexData.isReference());
                    
                    assertTrue(complexData.getReference() != null);
                    
                    assertTrue(!complexData.getReference().getHref().toString().isEmpty());
                }
            }
            
        } catch (XMLStreamException e) {
            LOGGER.error(e.getMessage());
            fail();
        }
    }

    private void checkBBoxData(BoundingBoxData output) {
        BoundingBox boundingBox = (BoundingBox) output.getValue();
        assertTrue(boundingBox.getCrs().equals("EPSG:4328"));
        assertTrue(boundingBox.getDimensions() == 2);
        assertTrue(boundingBox.getMinX() == -180d);
        assertTrue(boundingBox.getMinY() == -90d);
        assertTrue(boundingBox.getMaxX() == 180d);
        assertTrue(boundingBox.getMaxY() == 90d);

    }
}
