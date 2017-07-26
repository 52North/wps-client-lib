package org.n52.geoprocessing.wps.client;

import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.n52.geoprocessing.wps.client.model.Process;

import net.opengis.wps.x20.ProcessOfferingsDocument;

public class WPS20ProcessParserTest {

	public static void main(String[] args) throws Exception {
		
		ProcessOfferingsDocument offeringsDocument = ProcessOfferingsDocument.Factory.parse(WPS20ProcessParserTest.class.getResourceAsStream("WPS20ProcessDescription.xml"));
		
		Process process = new Process();
					
		WPS20ProcessParser.parse(offeringsDocument.getProcessOfferings().getProcessOfferingArray(0), process);		
	}
	
}
