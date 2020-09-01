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
package org.n52.geoprocessing.wps.client;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.n52.geoprocessing.wps.client.model.ComplexInputDescription;
import org.n52.geoprocessing.wps.client.model.Format;
import org.n52.geoprocessing.wps.client.model.InputDescription;
import org.n52.geoprocessing.wps.client.model.LiteralInputDescription;
import org.n52.geoprocessing.wps.client.model.Process;
import org.n52.geoprocessing.wps.client.model.ResponseMode;
import org.n52.geoprocessing.wps.client.model.TransmissionMode;
import org.n52.geoprocessing.wps.client.model.execution.BoundingBox;
import org.n52.geoprocessing.wps.client.model.execution.BoundingBoxData;
import org.n52.geoprocessing.wps.client.model.execution.ComplexData;
import org.n52.geoprocessing.wps.client.model.execution.ComplexDataReference;
import org.n52.geoprocessing.wps.client.model.execution.Execute;
import org.n52.geoprocessing.wps.client.model.execution.ExecuteOutput;
import org.n52.geoprocessing.wps.client.model.execution.ExecutionMode;
import org.n52.geoprocessing.wps.client.model.execution.LiteralData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author foerster, bpross-52n
 */
public class ExecuteRequestBuilder {

    public static final String VERSION_100 = "1.0.0";

    public static final String VERSION_200 = "2.0.0";

    private static Logger LOGGER = LoggerFactory.getLogger(ExecuteRequestBuilder.class);

    private static final String INPUT_DESCRIPTION_IS_NULL = "InputDescription is null for: ";

    private static final String INPUT_DESCRIPTION_NOT_COMPLEX_DATA = "InputDescription is not of type complex data: ";

    private Process processDesc;

    private Execute execute;

    public ExecuteRequestBuilder(Process processDesc) {
        this.processDesc = processDesc;
        execute = new Execute();
        // set synchronous execute by default
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
     *             if input description was not found
     */
    public void addComplexData(String parameterID,
            String value,
            String schema,
            String encoding,
            String mimeType) throws WPSClientException {
        InputDescription inputDesc = getParameterDescription(parameterID);
        if (inputDesc == null) {
            throw new IllegalArgumentException(INPUT_DESCRIPTION_IS_NULL + parameterID);
        }
        // if (!(inputDesc instanceof ComplexInputDescription)) {
        // throw new IllegalArgumentException("inputDescription is not of type
        // ComplexData: " + parameterID);
        // }

        ComplexData input = new ComplexData();

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
     *             if input description was not found
     */
    public void addComplexData(String parameterID,
            InputStream value,
            String schema,
            String encoding,
            String mimeType) throws WPSClientException {
        InputDescription inputDesc = getParameterDescription(parameterID);
        if (inputDesc == null) {
            throw new IllegalArgumentException(INPUT_DESCRIPTION_IS_NULL + parameterID);
        }
        // if (!(inputDesc instanceof ComplexInputDescription)) {
        // throw new IllegalArgumentException("inputDescription is not of type
        // ComplexData: " + parameterID);
        // }

        ComplexData input = new ComplexData();

        input.setId(inputDesc.getId());

        setComplexData(input, value, schema, mimeType, encoding);

    }

    private void setComplexData(ComplexData input,
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

    /**
     * Add literal data to the request
     *
     * @param parameterID
     *            the ID of the input paramter according to the describe process
     * @param value
     *            the actual value as InputStream
     * @param schema
     *            schema if applicable otherwise null
     * @param encoding
     *            encoding if not the default encoding (for default encoding set
     *            it to null) (i.e. binary data, use base64)
     * @param mimetype
     *            mimetype of the data, has to be set
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
            throw new IllegalArgumentException(INPUT_DESCRIPTION_IS_NULL + parameterID);
        }
        if (inputDesc instanceof ComplexInputDescription) {
            throw new IllegalArgumentException(INPUT_DESCRIPTION_NOT_COMPLEX_DATA + parameterID);
        }

        LiteralData literalInput = new LiteralData();
        literalInput.setId(parameterID);
        literalInput.setValue(value);
        literalInput.setFormat(createFormat(schema, mimetype, encoding));
        literalInput.setDataType(((LiteralInputDescription) inputDesc).getDataType());
        execute.addInput(literalInput);
    }

    public void addBoundingBoxData(String parameterID,
            BoundingBox value,
            String schema,
            String encoding,
            String mimetype) {
        InputDescription inputDesc = this.getParameterDescription(parameterID);
        if (inputDesc == null) {
            throw new IllegalArgumentException(INPUT_DESCRIPTION_IS_NULL + parameterID);
        }
        if (inputDesc instanceof ComplexInputDescription) {
            throw new IllegalArgumentException("InputDescription is not of type bounding box data: " + parameterID);
        }

        BoundingBoxData boundingBoxInput = new BoundingBoxData();
        boundingBoxInput.setId(parameterID);
        boundingBoxInput.setValue(value);
        boundingBoxInput.setFormat(createFormat(schema, mimetype, encoding));
        execute.addInput(boundingBoxInput);
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
     *             if the reference URL is malformed
     */
    public void addComplexDataReference(String parameterID,
            String value,
            String schema,
            String encoding,
            String mimetype) throws MalformedURLException {
        InputDescription inputDesc = getParameterDescription(parameterID);
        if (inputDesc == null) {
            throw new IllegalArgumentException(INPUT_DESCRIPTION_IS_NULL + parameterID);
        }
        if (inputDesc instanceof LiteralInputDescription) {
            throw new IllegalArgumentException(INPUT_DESCRIPTION_NOT_COMPLEX_DATA + parameterID);
        }

        ComplexData input = new ComplexData();
        input.setId(parameterID);
        ComplexDataReference complexInputReference = new ComplexDataReference();
        complexInputReference.setHref(new URL(value));
        input.setReference(complexInputReference);
        input.setFormat(createFormat(schema, mimetype, encoding));
        execute.addInput(input);
    }

    /**
     * this sets store for the specific output.
     *
     * @param outputName
     *            the id of the output
     * @param asReference
     *            true if output should be requested as reference, false
     *            otherwise
     * @return true if reference is supported, false otherwise
     */
    public boolean setAsReference(String outputName,
            boolean asReference) {

        if (!processDesc.isReferenceSupported()) {
            LOGGER.info("Tried to set asReference, but it is not supported.");
            return false;
        }

        ExecuteOutput executeOutput = getOutputDefinition(outputName);

        // if setAsReference, set respective Enum
        if (asReference) {
            executeOutput.setTransmissionMode(TransmissionMode.REFERENCE);
        }

        return true;
    }

    private ExecuteOutput getOutputDefinition(String outputName) {
        List<ExecuteOutput> outputs = execute.getOutputs();
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
     * @param outputIdentifier identifier of the output
     * @param schema
     *            if applicable otherwise null
     * @param encoding
     *            if default encoding = null, otherwise base64
     * @param mimeType
     *            requested mimetype of the output according to the process
     *            description. if not set, default mime type is used.
     * @return true
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
            String mimeType) {

        ExecuteOutput output = new ExecuteOutput();

        output.setId(outputIdentifier);

        output.setFormat(createFormat(schema, mimeType, encoding));

        execute.addOutput(output);

    }

    public void addOutput(String outputIdentifier,
            String schema,
            String encoding,
            String mimeType) {

        ExecuteOutput output = new ExecuteOutput();

        output.setId(outputIdentifier);

        output.setFormat(createFormat(schema, mimeType, encoding));

        execute.addOutput(output);

    }

    /**
     * XML representation of the created request.
     *
     * @return execute object
     */
    public Execute getExecute() {
        return execute;
    }
    
    
    /**
     * Resets the internal execute member to the initial state.
     */
    public void reset() {
        execute = new Execute();
        // set synchronous execute by default
        execute.setExecutionMode(ExecutionMode.SYNC);
        execute.setId(processDesc.getId());        
    }

    /**
     *
     * @param id
     *            id of the input
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

}
