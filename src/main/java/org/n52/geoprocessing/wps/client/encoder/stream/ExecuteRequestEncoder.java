package org.n52.geoprocessing.wps.client.encoder.stream;

import java.util.Collections;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.n52.geoprocessing.wps.client.model.Format;
import org.n52.geoprocessing.wps.client.model.execution.ComplexInput;
import org.n52.geoprocessing.wps.client.model.execution.ComplexInputReference;
import org.n52.geoprocessing.wps.client.model.execution.Execute;
import org.n52.geoprocessing.wps.client.model.execution.ExecuteInput;
import org.n52.geoprocessing.wps.client.model.execution.ExecuteOutput;
import org.n52.geoprocessing.wps.client.model.execution.LiteralInput;
import org.n52.geoprocessing.wps.client.model.execution.WPSExecuteParameter;
import org.n52.javaps.service.xml.OWSConstants;
import org.n52.javaps.service.xml.WPSConstants;
import org.n52.javaps.service.xml.XMLSchemaConstants;
import org.n52.shetland.w3c.SchemaLocation;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.stream.xml.AbstractMultiElementXmlStreamWriter;
import org.n52.svalbard.stream.XLinkConstants;

public class ExecuteRequestEncoder extends AbstractMultiElementXmlStreamWriter {

    private final String service = "WPS";

    private final String version = "2.0.0";

    private final String mimeTypeTextPlain = "text/plain";

    private final String mimeTypeTextXML = "text/xml";

    @Override
    public void writeElement(Object object) throws XMLStreamException, EncodingException {
        if (object instanceof Execute) {
            writeExecute((Execute) object);
        }
    }

    private void writeNamespaces() throws XMLStreamException {
        namespace(WPSConstants.NS_WPS_PREFIX, WPSConstants.NS_WPS);
        namespace(OWSConstants.NS_OWS_PREFIX, OWSConstants.NS_OWS);
        namespace(XLinkConstants.NS_XLINK_PREFIX, XLinkConstants.NS_XLINK);
    }

    private void writeNamespacesWithSchemalocation() throws XMLStreamException {
        writeNamespaces();
        schemaLocation(
                Collections.singleton(new SchemaLocation(WPSConstants.NS_WPS, XMLSchemaConstants.WPS20_SCHEMALOCTION)));
    }

    private void writeExecute(Execute execute) throws XMLStreamException {

        element(WPSConstants.Elem.QN_EXECUTE, execute, x -> {
            writeNamespacesWithSchemalocation();
            attr(WPSConstants.Attr.AN_RESPONSE, execute.getResponseMode().toString().toLowerCase());
            attr(WPSConstants.Attr.AN_MODE, execute.getExecutionMode().toString().toLowerCase());
            attr(WPSConstants.Attr.AN_SERVICE, service);
            attr(WPSConstants.Attr.AN_VERSION, version);
            element(OWSConstants.Elem.QN_IDENTIFIER, execute.getId());
            if (execute.getInputs() != null) {
                writeInputElements(execute.getInputs());
            }
            writeOutputElements(execute.getOutputs());
        });

    }

    private void writeOutputElements(List<ExecuteOutput> outputs) throws XMLStreamException {
        for (ExecuteOutput executeOutput : outputs) {
            element(WPSConstants.Elem.QN_OUTPUT, executeOutput, x -> {
                attr(WPSConstants.Attr.AN_ID, executeOutput.getId());
                attr(WPSConstants.Attr.AN_TRANSMISSION, executeOutput.getTransmissionMode().toString().toLowerCase());                
                setFormat(executeOutput);
            });
        }
    }

    private void writeInputElements(List<ExecuteInput> inputs) throws XMLStreamException {
        for (ExecuteInput executeInput : inputs) {
            element(WPSConstants.Elem.QN_INPUT, executeInput, x -> {
                attr(WPSConstants.Attr.AN_ID, executeInput.getId());
                String title = executeInput.getTitle();
                if (title != null && !title.isEmpty()) {
                    element(OWSConstants.Elem.QN_TITLE, title);
                }
                String abstrakt = executeInput.getAbstract();
                if (abstrakt != null && !abstrakt.isEmpty()) {
                    element(OWSConstants.Elem.QN_ABSTRACT, abstrakt);
                }
                if (executeInput instanceof ComplexInput) {
                    writeComplexInput((ComplexInput) executeInput);
                } else if (executeInput instanceof LiteralInput) {
                    writeLiteralInput((LiteralInput) executeInput);
                }
            });
        }
    }

    private void writeLiteralInput(LiteralInput executeInput) throws XMLStreamException {

            Format format = executeInput.getFormat();
            String mimeType = "";

            if (format != null) {
                mimeType = format.getMimeType();
            }
            if (!mimeType.isEmpty()) {
                if (mimeType.equals(mimeTypeTextPlain)) {
                    element(WPSConstants.Elem.QN_DATA, executeInput.getValue().toString());
                    attr(WPSConstants.Attr.AN_MIME_TYPE, mimeTypeTextPlain);
                } else if (mimeType.equals(mimeTypeTextXML)) {
                    element(WPSConstants.Elem.QN_DATA, executeInput, x1 -> {
                        attr(WPSConstants.Attr.AN_MIME_TYPE, mimeTypeTextXML);
                        element(WPSConstants.Elem.QN_LITERAL_VALUE, executeInput.getValue().toString());
                        // if()
                    });
                }
            } else {
                element(WPSConstants.Elem.QN_DATA, executeInput, x1 -> {
                    attr(WPSConstants.Attr.AN_MIME_TYPE, mimeTypeTextXML);
                    element(WPSConstants.Elem.QN_LITERAL_VALUE, executeInput.getValue().toString());
                    // if()
                });
            }
    }

    private void writeComplexInput(ComplexInput executeInput) throws XMLStreamException {
        if (executeInput.isReference()) {
            writeComplexInputReference(executeInput);
        } else {
            element(WPSConstants.Elem.QN_DATA, executeInput, x -> {
                setFormat(executeInput);
                chars(executeInput.getValue().toString());
            });
        }
    }

    private void writeComplexInputReference(ComplexInput executeInput) throws XMLStreamException {

        ComplexInputReference reference = executeInput.getReference();

        element(WPSConstants.Elem.QN_REFERENCE, reference, x -> {
            attr(XLinkConstants.Attr.QN_HREF, reference.getHref().toString());
            setFormat(executeInput);
        });
    }

    private void setFormat(WPSExecuteParameter executeParameter) throws XMLStreamException {

        Format format = executeParameter.getFormat();

        attr(WPSConstants.Attr.AN_MIME_TYPE, format.getMimeType());

        String schema = format.getSchema();

        if (schema != null && !schema.isEmpty()) {
            attr(WPSConstants.Attr.AN_SCHEMA, schema);
        }

        String encoding = format.getEncoding();

        if (encoding != null && !encoding.isEmpty()) {
            attr(WPSConstants.Attr.AN_ENCODING, encoding);
        }
    }

}
