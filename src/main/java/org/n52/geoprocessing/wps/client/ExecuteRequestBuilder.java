/*
 * ﻿Copyright (C) ${inceptionYear} - ${currentYear} 52°North Initiative for Geospatial Open Source
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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.n52.geoprocessing.wps.client.model.ComplexInputDescription;
import org.n52.geoprocessing.wps.client.model.Format;
import org.n52.geoprocessing.wps.client.model.InputDescription;
import org.n52.geoprocessing.wps.client.model.LiteralInputDescription;
import org.n52.geoprocessing.wps.client.model.OutputDescription;
import org.n52.geoprocessing.wps.client.model.Process;
import org.n52.geoprocessing.wps.client.model.ResponseMode;
import org.n52.geoprocessing.wps.client.model.TransmissionMode;
import org.n52.geoprocessing.wps.client.model.execution.ComplexInput;
import org.n52.geoprocessing.wps.client.model.execution.ComplexInputReference;
import org.n52.geoprocessing.wps.client.model.execution.Execute;
import org.n52.geoprocessing.wps.client.model.execution.ExecuteOutput;
import org.n52.geoprocessing.wps.client.model.execution.ExecutionMode;
import org.n52.geoprocessing.wps.client.model.execution.LiteralInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author foerster, bpross-52n
 */
public class ExecuteRequestBuilder {
    private Process processDesc;

    private Execute execute;

    public static final String VERSION_100 = "1.0.0";

    public static final String VERSION_200 = "2.0.0";

    private static Logger LOGGER = LoggerFactory.getLogger(ExecuteRequestBuilder.class);

    public ExecuteRequestBuilder(Process processDesc) {
        this.processDesc = processDesc;
        execute = new Execute();
        //set synchronous execute by default
        execute.setExecutionMode(ExecutionMode.SYNC);
        execute.setId(processDesc.getId());
    }

    public ExecuteRequestBuilder(Process processDesc, Execute execute) {
        this.processDesc = processDesc;
        this.execute = execute;
    }

    /**
     * add an input element. sets the data in the xml request
     *
     * @param parameterID
     *            the ID of the input (see process description)
     * @param value
     *            the actual value as String (for xml data xml for binary data
     *            is should be base64 encoded data)
     * @param schema
     *            schema if applicable otherwise null
     * @param encoding
     *            encoding if not the default encoding (for default encoding set
     *            it to null) (i.e. binary data, use base64)
     * @param mimeType
     *            mimetype of the data, has to be set
     * @throws WPSClientException
     */
    public void addComplexData(String parameterID,
            String value,
            String schema,
            String encoding,
            String mimeType) throws WPSClientException {
        InputDescription inputDesc = getParameterDescription(parameterID);
        if (inputDesc == null) {
            throw new IllegalArgumentException("inputDesription is null for: " + parameterID);
        }
//        if (!(inputDesc instanceof ComplexInputDescription)) {
//            throw new IllegalArgumentException("inputDescription is not of type ComplexData: " + parameterID);
//        }

        ComplexInput input = new ComplexInput();

        input.setId(inputDesc.getId());

        setComplexData(input, value, schema, mimeType, encoding);

    }

    /**
     * add an input element. sets the data in the xml request
     *
     * @param parameterID
     *            the ID of the input (see process description)
     * @param value
     *            the actual value as InputStream
     * @param schema
     *            schema if applicable otherwise null
     * @param encoding
     *            encoding if not the default encoding (for default encoding set
     *            it to null) (i.e. binary data, use base64)
     * @param mimeType
     *            mimetype of the data, has to be set
     * @throws WPSClientException
     */
    public void addComplexData(String parameterID,
            InputStream value,
            String schema,
            String encoding,
            String mimeType) throws WPSClientException {
        InputDescription inputDesc = getParameterDescription(parameterID);
        if (inputDesc == null) {
            throw new IllegalArgumentException("inputDesription is null for: " + parameterID);
        }
//        if (!(inputDesc instanceof ComplexInputDescription)) {
//            throw new IllegalArgumentException("inputDescription is not of type ComplexData: " + parameterID);
//        }

        ComplexInput input = new ComplexInput();

        input.setId(inputDesc.getId());

        setComplexData(input, value, schema, mimeType, encoding);

    }

    private void setComplexData(ComplexInput input,
            Object value,
            String schema,
            String mimeType,
            String encoding) {

        input.setFormat(createFormat(schema, mimeType, encoding));

        input.setValue(value);

        execute.addInput(input);

    }

    private Format createFormat(String schema,
            String mimeType,
            String encoding) {

        Format format = new Format();

        if (schema != null) {
            format.setSchema(schema);
        }
        if (mimeType != null) {
            format.setMimeType(mimeType);
        }
        if (encoding != null) {
            format.setEncoding(encoding);
        }

        return format;
    }


//    /**
//     * add an input element. sets the data in the xml request
//     *
//     * @param parameterID
//     *            the ID of the input (see process description)
//     * @param value
//     *            the actual value as String (for xml data xml for binary data
//     *            is should be base64 encoded data)
//     * @param schema
//     *            schema if applicable otherwise null
//     * @param encoding
//     *            encoding if not the default encoding (for default encoding set
//     *            it to null) (i.e. binary data, use base64)
//     * @param mimeType
//     *            mimetype of the data, has to be set
//     * @throws WPSClientException
//     */
//    public void addComplexData(String parameterID,
//            String value,
//            String schema,
//            String encoding,
//            String mimeType,
//            boolean asReference) throws WPSClientException {
//
//        if (asReference) {
//            addComplexDataReference(parameterID, value, schema, encoding, mimeType);
//        } else {
//
//            InputDescriptionType inputDesc = getParameterDescription(parameterID);
//            if (inputDesc == null) {
//                throw new IllegalArgumentException("inputDescription is null for: " + parameterID);
//            }
//            if (inputDesc.getComplexData() == null) {
//                throw new IllegalArgumentException("inputDescription is not of type ComplexData: " + parameterID);
//            }
//
//            InputType input = execute.getExecute().getDataInputs().addNewInput();
//            input.addNewIdentifier().setStringValue(inputDesc.getIdentifier().getStringValue());
//
//            ComplexDataType data = input.addNewData().addNewComplexData();
//
//            setComplexData(data, value, schema, mimeType, encoding);
//        }
//
//    }
//
//    /**
//     * add an input element. sets the data in the xml request
//     *
//     * @param inputType
//     */
//    public void addComplexData(InputType inputType) {
//
//        String parameterID = inputType.getIdentifier().getStringValue();
//
//        InputDescriptionType inputDesc = getParameterDescription(parameterID);
//        if (inputDesc == null) {
//            throw new IllegalArgumentException("inputDescription is null for: " + parameterID);
//        }
//        if (inputDesc.getComplexData() == null) {
//            throw new IllegalArgumentException("inputDescription is not of type ComplexData: " + parameterID);
//        }
//
//        InputType[] newInputTypeArray;
//
//        InputType[] currentInputTypeArray = execute.getExecute().getDataInputs().getInputArray();
//
//        if (currentInputTypeArray != null) {
//            newInputTypeArray = Arrays.copyOf(currentInputTypeArray, currentInputTypeArray.length + 1);
//        } else {
//            newInputTypeArray = new InputType[1];
//        }
//
//        newInputTypeArray[newInputTypeArray.length - 1] = inputType;
//
//        execute.getExecute().getDataInputs().setInputArray(newInputTypeArray);
//
//    }
//
    /**
     * Add literal data to the request
     *
     * @param parameterID
     *            the ID of the input paramter according to the describe process
     * @param value
     *            the value. other types than strings have to be converted to
     *            string. The datatype is automatically determined and set
     *            accordingly to the process description
     */
    public void addLiteralData(String parameterID,
            String value,
            String schema,
            String encoding,
            String mimetype) {
        InputDescription inputDesc = this.getParameterDescription(parameterID);
        if (inputDesc == null) {
            throw new IllegalArgumentException("inputDescription is null for: " + parameterID);
        }
        if (inputDesc instanceof ComplexInputDescription) {
            throw new IllegalArgumentException("inputDescription is not of type complex data: " + parameterID);
        }

        LiteralInput literalInput = new LiteralInput();
        literalInput.setId(parameterID);
        literalInput.setValue(value);
        literalInput.setFormat(createFormat(schema, mimetype, encoding));
        literalInput.setDataType(((LiteralInputDescription)inputDesc).getDataType());
        execute.addInput(literalInput);
    }

    /**
     * Sets a reference to input data
     *
     * @param parameterID
     *            ID of the input element
     * @param value
     *            reference URL
     * @param schema
     *            schema if applicable otherwise null
     * @param encoding
     *            encoding if applicable (typically not), otherwise null
     * @param mimetype
     *            mimetype of the input according to the process description.
     *            has to be set
     * @throws MalformedURLException
     */
    public void addComplexDataReference(String parameterID,
            String value,
            String schema,
            String encoding,
            String mimetype) throws MalformedURLException {
        InputDescription inputDesc = getParameterDescription(parameterID);
        if (inputDesc == null) {
            throw new IllegalArgumentException("inputDescription is null for: " + parameterID);
        }
        if (inputDesc instanceof LiteralInputDescription) {
            throw new IllegalArgumentException("inputDescription is not of type complexData: " + parameterID);
        }

        ComplexInput input = new ComplexInput();
        input.setId(parameterID);
        ComplexInputReference complexInputReference = new ComplexInputReference();
        complexInputReference.setHref(new URL(value));
        input.setReference(complexInputReference);
        input.setFormat(createFormat(schema, mimetype, encoding));
        execute.addInput(input);
    }
//
//    /**
//     * checks, if the execute, which has been build is valid according to the
//     * process description.
//     *
//     * @return
//     */
//    public boolean isExecuteValid() {
//        return true;
//    }
//
//    /**
//     * this sets store for the specific output.
//     *
//     * @param outputName
//     * @param storeSupport
//     * @return
//     */
//    public boolean setStoreSupport(String outputName,
//            boolean storeSupport) {
//        // DocumentOutputDefinitionType outputDef = null;
//
//        if (!execute.getExecute().isSetResponseForm()) {
//            execute.getExecute().addNewResponseForm();
//        }
//
//        ResponseFormType responseForm = execute.getExecute().getResponseForm();
//
//        if (!responseForm.isSetResponseDocument()) {
//            responseForm.addNewResponseDocument();
//        }
//
//        ResponseDocumentType responseDocument = responseForm.getResponseDocument();
//
//        responseDocument.setStoreExecuteResponse(storeSupport);
//
//        return true;
//    }
//
    /**
     * this sets store for the specific output.
     *
     * @param outputName
     * @param asReference
     * @return
     */
    public boolean setAsReference(String outputName,
            boolean asReference) {

        if(!processDesc.isReferenceSupported()){
            LOGGER.info("Tried to set asReference, but it is not supported.");
            return false;
        }

        ExecuteOutput executeOutput = getOutputDefinition(outputName);

        //if setAsReference, set respective Enum
        if(asReference){
            executeOutput.setTransmissionMode(TransmissionMode.REFERENCE);
        }

        return true;
    }
//
//    /**
//     * this sets store for the specific output.
//     *
//     * @param outputName
//     * @param status
//     * @return
//     */
//    public boolean setStatus(String outputName,
//            boolean status) {
//        if (!execute.getExecute().isSetResponseForm()) {
//            execute.getExecute().addNewResponseForm();
//        }
//
//        ResponseFormType responseForm = execute.getExecute().getResponseForm();
//
//        if (!responseForm.isSetResponseDocument()) {
//            responseForm.addNewResponseDocument();
//        }
//
//        ResponseDocumentType responseDocument = responseForm.getResponseDocument();
//
//        responseDocument.setStatus(status);
//
//        return true;
//    }
//
//    /**
//     * Set this if you want the data to a schema offered in the process
//     * description
//     *
//     * @param schema
//     * @param outputName
//     * @return
//     */
//    public boolean setSchemaForOutput(String schema,
//            String outputName) {
//        if (!execute.getExecute().isSetResponseForm()) {
//            execute.getExecute().addNewResponseForm();
//        }
//        if (!execute.getExecute().getResponseForm().isSetResponseDocument()) {
//            execute.getExecute().getResponseForm().addNewResponseDocument();
//        }
//        OutputDescriptionType outputDesc = getOutputDescription(outputName);
//        DocumentOutputDefinitionType outputDef = getOutputDefinition(outputName);
//        if (outputDef == null) {
//            outputDef = execute.getExecute().getResponseForm().getResponseDocument().addNewOutput();
//            outputDef.setIdentifier(outputDesc.getIdentifier());
//        }
//        String defaultSchema = outputDesc.getComplexOutput().getDefault().getFormat().getSchema();
//        if ((defaultSchema != null && defaultSchema.equals(schema)) || (defaultSchema == null && schema == null)) {
//            outputDef.setSchema(schema);
//            return true;
//        } else {
//            for (ComplexDataDescriptionType data : outputDesc.getComplexOutput().getSupported().getFormatArray()) {
//                if (data.getSchema() != null && data.getSchema().equals(schema)) {
//                    outputDef.setSchema(schema);
//                    return true;
//                } else if ((data.getSchema() == null && schema == null)) {
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }
//
//    /**
//     * sets the desired mimetype of the output. if not set, the default mimetype
//     * will be used as stated in the process description
//     *
//     * @param mimeType
//     *            the name of the mimetype as announced in the
//     *            processdescription
//     * @param outputName
//     *            the Identifier of the output element
//     * @return success
//     */
//    public boolean setMimeTypeForOutput(String mimeType,
//            String outputName) {
//        if (!execute.getExecute().isSetResponseForm()) {
//            execute.getExecute().addNewResponseForm();
//        }
//        if (!execute.getExecute().getResponseForm().isSetResponseDocument()) {
//            execute.getExecute().getResponseForm().addNewResponseDocument();
//        }
//        OutputDescriptionType outputDesc = getOutputDescription(outputName);
//        DocumentOutputDefinitionType outputDef = getOutputDefinition(outputName);
//        if (outputDef == null) {
//            outputDef = execute.getExecute().getResponseForm().getResponseDocument().addNewOutput();
//            outputDef.setIdentifier(outputDesc.getIdentifier());
//        }
//
//        String defaultMimeType = outputDesc.getComplexOutput().getDefault().getFormat().getMimeType();
//        if (defaultMimeType == null) {
//            defaultMimeType = "text/xml";
//        }
//        if (defaultMimeType.equals(mimeType)) {
//            outputDef.setMimeType(mimeType);
//            return true;
//        } else {
//            for (ComplexDataDescriptionType data : outputDesc.getComplexOutput().getSupported().getFormatArray()) {
//                String m = data.getMimeType();
//                if (m != null && m.equals(mimeType)) {
//                    outputDef.setMimeType(mimeType);
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }
//
//    /**
//     * sets the encoding. necessary if data should not be retrieved in the
//     * default encoding (i.e. binary data in XML responses not raw data
//     * responses)
//     *
//     * @param encoding
//     *            use base64
//     * @param outputName
//     *            ID of the output
//     * @return
//     */
//    public boolean setEncodingForOutput(String encoding,
//            String outputName) {
//        if (!execute.getExecute().isSetResponseForm()) {
//            execute.getExecute().addNewResponseForm();
//        }
//        if (!execute.getExecute().getResponseForm().isSetResponseDocument()) {
//            execute.getExecute().getResponseForm().addNewResponseDocument();
//        }
//        OutputDescription outputDesc = getOutputDescription(outputName);
//        ExecuteOutput outputDef = getOutputDefinition(outputName);
//        if (outputDef == null) {
//            outputDef = execute.getExecute().getResponseForm().getResponseDocument().addNewOutput();
//            outputDef.setId(outputDesc.getId());
//        }
//
//        String defaultEncoding = getDefaultFormat(outputDesc).getEncoding();
//        if (defaultEncoding == null) {
//            defaultEncoding = IOHandler.DEFAULT_ENCODING;
//        }
//        if (defaultEncoding.equals(encoding)) {
//            return true;
//        } else {
//            ComplexDataDescriptionType[] supportedFormats =
//                    outputDesc.getComplexOutput().getSupported().getFormatArray();
//            for (ComplexDataDescriptionType data : supportedFormats) {
//                String e = data.getEncoding();
//                if (e != null && e.equals(encoding)) {
//                    outputDef.setEncoding(encoding);
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }

    private Format getDefaultFormat(OutputDescription outputDesc){
        List<Format> formats = outputDesc.getFormats();

        for (Format format : formats) {
            if(format.isDefault()){
                return format;
            }
        }

        throw new IllegalArgumentException("Output description has no default format.");
    }

    private OutputDescription getOutputDescription(String outputName) {
        for (OutputDescription outputDesc : processDesc.getOutputs()) {
            if (outputDesc.getId().equals(outputName)) {
                return outputDesc;
            }
        }

        return null;
    }

    private ExecuteOutput getOutputDefinition(String outputName) {
        List<ExecuteOutput> outputs =
                execute.getOutputs();
        for (ExecuteOutput outputDef : outputs) {
            if (outputDef.getId().equals(outputName)) {
                return outputDef;
            }
        }

        return null;
    }

    public boolean setResponseDocument(String outputIdentifier,
            String schema,
            String encoding,
            String mimeType) {

        setOutput(outputIdentifier, schema, encoding, mimeType);

        execute.setResponseMode(ResponseMode.DOCUMENT);

        return false;
    }

    /**
     * Asks for data as raw data, i.e. without WPS XML wrapping
     *
     * @param schema
     *            if applicable otherwise null
     * @param encoding
     *            if default encoding = null, otherwise base64
     * @param mimeType
     *            requested mimetype of the output according to the process
     *            description. if not set, default mime type is used.
     * @return
     */
    public boolean setRawData(String outputIdentifier,
            String schema,
            String encoding,
            String mimeType) {

        setOutput(outputIdentifier, schema, encoding, mimeType);

        execute.setResponseMode(ResponseMode.RAW);

        return true;
    }

    public boolean setAsynchronousExecute() {
        if (!processDesc.isStatusSupported()) {
            LOGGER.info("Tried to set asynchronous execute, but it is not supported.");
            return false;
        }
        execute.setExecutionMode(ExecutionMode.ASYNC);
        return true;
    }

    private void setOutput(String outputIdentifier,
            String schema,
            String encoding,
            String mimeType){

        ExecuteOutput output = new ExecuteOutput();

        output.setId(outputIdentifier);

        output.setFormat(createFormat(schema, mimeType, encoding));

        execute.addOutput(output);

    }

    /**
     * XML representation of the created request.
     *
     * @return
     */
    public Execute getExecute() {
        return execute;
    }

//    /**
//     * return a KVP representation for the created execute document.
//     *
//     * @return KVP request string
//     * @throws UnsupportedEncodingException
//     *             if the URL encoding using UTF-8 fails
//     */
//    public String getExecuteAsGETString() throws UnsupportedEncodingException {
//        String request = "?service=wps&request=execute&version=1.0.0&identifier=";
//        request = request + processDesc.getIdentifier().getStringValue();
//        request = request + "&DataInputs=";
//        InputType[] inputs = execute.getExecute().getDataInputs().getInputArray();
//        int inputCounter = 0;
//        for (InputType input : inputs) {
//
//            request = request + input.getIdentifier().getStringValue();
//
//            if (input.isSetReference()) {
//                // reference
//                InputReferenceType reference = input.getReference();
//                request = request + "=" + "@xlink:href=" + URLEncoder.encode(reference.getHref(), "UTF-8");
//                if (reference.isSetEncoding()) {
//                    request = request + "@encoding=" + reference.getEncoding();
//                }
//                if (reference.isSetMimeType()) {
//                    request = request + "@format=" + reference.getMimeType();
//                }
//                if (reference.isSetEncoding()) {
//                    request = request + "@schema=" + reference.getSchema();
//                }
//            }
//            if (input.isSetData()) {
//                if (input.getData().isSetComplexData()) {
//                    // complex
//                    ComplexDataType complexData = input.getData().getComplexData();
//                    request = request + "=" + URLEncoder.encode(input.getData().getComplexData().xmlText(), "UTF-8");
//                    if (complexData.isSetEncoding()) {
//                        request = request + "@encoding=" + complexData.getEncoding();
//                    }
//                    if (complexData.isSetMimeType()) {
//                        request = request + "@format=" + complexData.getMimeType();
//                    }
//                    if (complexData.isSetEncoding()) {
//                        request = request + "@schema=" + complexData.getSchema();
//                    }
//                }
//                if (input.getData().isSetLiteralData()) {
//                    // literal
//                    LiteralDataType literalData = input.getData().getLiteralData();
//                    request = request + "=" + literalData.getStringValue();
//                    if (literalData.isSetDataType()) {
//                        request = request + "@datatype=" + literalData.getDataType();
//                    }
//                    if (literalData.isSetUom()) {
//                        request = request + "@datatype=" + literalData.getUom();
//                    }
//                }
//            }
//            // concatenation for next input element
//            inputCounter = inputCounter + 1;
//            if (inputCounter < inputs.length) {
//                request = request + ";";
//            }
//
//        }
//        if (execute.getExecute().getResponseForm().getResponseDocument() == null) {
//            throw new RuntimeException("ResponseDocument missing");
//        }
//        DocumentOutputDefinitionType[] outputs =
//                execute.getExecute().getResponseForm().getResponseDocument().getOutputArray();
//        int outputCounter = 0;
//        if (execute.getExecute().getResponseForm().isSetRawDataOutput()) {
//            request = request + "&rawdataoutput=";
//        } else {
//            request = request + "&responsedocument=";
//        }
//        for (DocumentOutputDefinitionType output : outputs) {
//            request = request + output.getIdentifier().getStringValue();
//            if (output.isSetEncoding()) {
//                request = request + "@encoding=" + output.getEncoding();
//            }
//            if (output.isSetMimeType()) {
//                request = request + "@format=" + output.getMimeType();
//            }
//            if (output.isSetEncoding()) {
//                request = request + "@schema=" + output.getSchema();
//            }
//            if (output.isSetUom()) {
//                request = request + "@datatype=" + output.getUom();
//            }
//            // concatenation for next output element
//            outputCounter = outputCounter + 1;
//            if (outputCounter < outputs.length) {
//                request = request + ";";
//            }
//        }
//
//        if (execute.getExecute().getResponseForm().getResponseDocument().isSetStoreExecuteResponse()) {
//            request = request + "&storeExecuteResponse=true";
//        }
//        if (execute.getExecute().getResponseForm().getResponseDocument().isSetStatus()) {
//            request = request + "&status=true";
//        }
//        if (execute.getExecute().getResponseForm().getResponseDocument().isSetLineage()) {
//            request = request + "&lineage=true";
//        }
//
//        return request;
//    }

    /**
     *
     * @param id
     * @return the specified parameterdescription. if not available it returns
     *         null.
     */
    private InputDescription getParameterDescription(String id) {
        List<InputDescription> inputDescs = processDesc.getInputs();
        for (InputDescription inputDesc : inputDescs) {
            if (inputDesc.getId().equals(id)) {
                return inputDesc;
            }
        }
        return null;
    }

//    private void setComplexData(ComplexDataType data,
//            Object value,
//            String schema,
//            String mimeType,
//            String encoding) {
//
//        if (value instanceof String) {
//
//            String valueString = (String) value;
//
//            try {
//                data.set(XmlObject.Factory.parse(valueString));
//            } catch (XmlException e) {
//
//                LOGGER.warn("error parsing data String as xml node, trying to parse data as xs:string");
//
//                XmlString xml = XmlString.Factory.newInstance();
//
//                xml.setStringValue(valueString);
//
//                data.set(xml);
//
//            }
//
//        } else if (value instanceof InputStream) {
//
//            InputStream stream = (InputStream) value;
//
//            try {
//                data.set(XmlObject.Factory.parse(stream));
//            } catch (XmlException e) {
//
//                LOGGER.warn("error parsing data stream as xml node, trying to parse data as xs:string");
//
//                String text = "";
//
//                int i = -1;
//
//                try {
//                    while ((i = stream.read()) != -1) {
//                        text = text + (char) i;
//                    }
//                } catch (IOException e1) {
//                    LOGGER.error("error parsing stream", e);
//                }
//
//                XmlString xml = XmlString.Factory.newInstance();
//
//                xml.setStringValue(text);
//
//                data.set(xml);
//            } catch (IOException e) {
//                LOGGER.error("error parsing stream", e);
//            }
//
//        }
//        if (schema != null) {
//            data.setSchema(schema);
//        }
//        if (mimeType != null) {
//            data.setMimeType(mimeType);
//        }
//        if (encoding != null) {
//            data.setEncoding(encoding);
//        }
//
//    }
}
