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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.n52.geoprocessing.wps.client.model.AllowedValues;
import org.n52.geoprocessing.wps.client.model.InputDescription;
import org.n52.geoprocessing.wps.client.model.LiteralInputDescription;
import org.n52.geoprocessing.wps.client.model.LiteralOutputDescription;
import org.n52.geoprocessing.wps.client.model.OutputDescription;
import org.n52.geoprocessing.wps.client.model.Process;
import org.n52.geoprocessing.wps.client.model.WPSDescriptionParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.opengis.wps.x100.ComplexDataCombinationType;
import net.opengis.wps.x100.ComplexDataDescriptionType;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.LiteralInputType;
import net.opengis.wps.x100.LiteralOutputType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessBriefType;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.opengis.wps.x100.SupportedComplexDataInputType;
import net.opengis.wps.x100.SupportedComplexDataType;


public class WPS100ProcessParser {

    private static Logger LOGGER = LoggerFactory.getLogger(WPS100ProcessParser.class);

    public static Process parseProcess(ProcessDescriptionType processDescriptionType){

        return completeProcess(processDescriptionType, createProcess(processDescriptionType));
    }

    public static Process createProcess(ProcessBriefType processSummaryType) {

        LOGGER.trace(processSummaryType.toString());

        String id = "";

        try {
            id = processSummaryType.getIdentifier().getStringValue();
        } catch (Exception e) {
            LOGGER.info("Could not get id from process.", e);
        }

        String abstrakt = "";

        if(processSummaryType.getAbstract() != null){
            try {
                abstrakt = processSummaryType.getAbstract().getStringValue();//TODO
               } catch (Exception e) {
                LOGGER.info("Could not get abstract from process.", e);
            }
        }else{
            LOGGER.info("Could not get abstract from process, element is emtpy.");
        }

        String title = "";

        if(processSummaryType.getTitle() != null){
            try {
                title = processSummaryType.getTitle().getStringValue();
            } catch (Exception e) {
                LOGGER.info("Could not get title from process.", e);
            }
        }else{
            LOGGER.info("Could not get title from process, element is emtpy.");
        }

        return createProcess(id, abstrakt, title);
    }

    private static Process createProcess(String id,
            String abstrakt,
            String title) {
        return createProcess(id, abstrakt, title, false);
    }

    private static Process createProcess(ProcessDescriptionType processDescriptionType) {

        LOGGER.trace(processDescriptionType.toString());

        String id = "";

        try {
            id = processDescriptionType.getIdentifier().getStringValue();
        } catch (Exception e) {
            LOGGER.info("Could not get id from process.", e);
        }

        String abstrakt = "";

        if(processDescriptionType.getAbstract() != null){
            try {
                abstrakt = processDescriptionType.getAbstract().getStringValue();//TODO
               } catch (Exception e) {
                LOGGER.info("Could not get abstract from process.", e);
            }
        }else{
            LOGGER.info("Could not get abstract from process, element is emtpy.");
        }

        String title = "";

        if(processDescriptionType.getTitle() != null){
            try {
                title = processDescriptionType.getTitle().getStringValue();
            } catch (Exception e) {
                LOGGER.info("Could not get title from process.", e);
            }
        }else{
            LOGGER.info("Could not get title from process, element is emtpy.");
        }

        return createProcess(id, abstrakt, title, processDescriptionType.getStatusSupported());
    }

    private static Process createProcess(String id,
            String abstrakt,
            String title,
            boolean statusSupported) {

        Process process = new Process();

        process.setId(id);

        process.setAbstract(abstrakt);

        process.setTitle(title);

        process.setStatusSupported(statusSupported);

        return process;
    }

    public static Process completeProcess(ProcessDescriptionType processDescriptionType,
            Process process) {

        InputDescriptionType[] inputDescriptions = processDescriptionType.getDataInputs().getInputArray();

        List<InputDescription> inputs = new ArrayList<>();

        for (InputDescriptionType inputDescriptionType : inputDescriptions) {

            inputs.add(createInput(inputDescriptionType));
        }

        process.setInputs(inputs);

        OutputDescriptionType[] outputDescriptions = processDescriptionType.getProcessOutputs().getOutputArray();

        List<OutputDescription> outputs = new ArrayList<>();

        for (OutputDescriptionType outputDescriptionType : outputDescriptions) {

            outputs.add(createOutput(outputDescriptionType));
        }

        process.setOutputs(outputs);

        return process;
    }

    private static OutputDescription createOutput(OutputDescriptionType outputDescriptionType) {

        OutputDescription output = new OutputDescription();

        if (outputDescriptionType.isSetComplexOutput()) {

            SupportedComplexDataType dataType = outputDescriptionType.getComplexOutput();

            output = (OutputDescription) createWPSParameter(dataType, output);
        }else if(outputDescriptionType.isSetLiteralOutput()){

            LiteralOutputType dataType = outputDescriptionType.getLiteralOutput();
            output = (OutputDescription) createWPSParameter(dataType, output);

        }else if(outputDescriptionType.isSetBoundingBoxOutput()){

        }

        try {
            output.setId(outputDescriptionType.getIdentifier().getStringValue());
        } catch (Exception e) {
            LOGGER.info("Could not parse identifier of output: " + output.getId());
            // TODO throw exception as output must have identifier
        }

        try {
            output.setAbstract(outputDescriptionType.getAbstract().getStringValue());
        } catch (Exception e) {
            LOGGER.info("Could not parse abstract of output: " + output.getId());
        }

        try {
            output.setTitle(outputDescriptionType.getTitle().getStringValue());
        } catch (Exception e) {
            LOGGER.info("Could not parse title of output: " + output.getId());
        }

        return output;
    }

    private static OutputDescription createWPSParameter(LiteralOutputType dataType,
            OutputDescription output) {

        LiteralOutputDescription outputDescription = new LiteralOutputDescription();

        outputDescription.setDataType(dataType.getDataType().getReference());

        //TODO, what about datatype, uom and such!?

        return outputDescription;
    }

    private static InputDescription createInput(InputDescriptionType inputDescriptionType) {

        InputDescription input = null;

        if (inputDescriptionType.isSetComplexData()) {

            input = new InputDescription();

            SupportedComplexDataInputType dataType = inputDescriptionType.getComplexData();

            input = (InputDescription) createWPSParameter(dataType, input);

        }else if (inputDescriptionType.isSetLiteralData()) {

            input = new LiteralInputDescription();

            LiteralInputType dataType = inputDescriptionType.getLiteralData();

            input = (InputDescription) createWPSParameter(dataType, (LiteralInputDescription)input);

        }else if (inputDescriptionType.isSetBoundingBoxData()) {

            //TODO

        }

        try {
            input.setId(inputDescriptionType.getIdentifier().getStringValue());
        } catch (Exception e) {
            LOGGER.info("Could not parse identifier of input: " + input.getId());
            // TODO throw exception as input must have identifier
        }

        try {
            input.setAbstract(inputDescriptionType.getAbstract().getStringValue());
        } catch (Exception e) {
            LOGGER.info("Could not parse abstract of input: " + input.getId());
        }

        try {
            input.setTitle(inputDescriptionType.getTitle().getStringValue());
        } catch (Exception e) {
            LOGGER.info("Could not parse title of input: " + input.getId());
        }

        Object minOccursObject = inputDescriptionType.getMinOccurs();

        try {
            if (minOccursObject instanceof BigInteger) {
                input.setMinOccurs(((BigInteger) minOccursObject).intValue());
            }
        } catch (Exception e) {
            LOGGER.info("Could not parse minimum occurs of input: " + input.getId());
        }

        Object maxOccursObject = inputDescriptionType.getMaxOccurs();

        try {
            if (maxOccursObject instanceof BigInteger) {
                input.setMaxOccurs(((BigInteger) maxOccursObject).intValue());
            }
        } catch (Exception e) {
            LOGGER.info("Could not parse maximum occurs of input: " + input.getId());
        }

        return input;
    }

    private static InputDescription createWPSParameter(LiteralInputType literalDataType,
            LiteralInputDescription literalInput) {

        literalInput.setDataType(literalDataType.getDataType().getReference());

        try {

            boolean anyValue = false;

            if (literalDataType.isSetAnyValue()) {
                anyValue = true;
                literalInput.setAnyValue(anyValue);
            }

            if (literalDataType.isSetAllowedValues()) {
                literalInput.setAllowedValues(createAllowedValues(literalDataType.getAllowedValues()));
            }

            if (literalDataType.isSetDefaultValue()) {
                literalInput.setDefaultValue(literalDataType.getDefaultValue());
            }

        } catch (Exception e) {
            LOGGER.info("LiteralInput has no LiteralDataDomain: " + literalInput.getId());
        }

        return literalInput;
    }

    private static WPSDescriptionParameter createWPSParameter(SupportedComplexDataType complexDataType,
            WPSDescriptionParameter wpsParameter) {

        List<org.n52.geoprocessing.wps.client.model.Format> formats = new ArrayList<>();

        ComplexDataCombinationType formatDescriptions = complexDataType.getDefault();

        ComplexDataDescriptionType defaultFormat = null;

        if(formatDescriptions != null){
            defaultFormat = formatDescriptions.getFormat();
            formats.add(createFormat(defaultFormat, true));
        }

        ComplexDataDescriptionType[] supportedFormats = complexDataType.getSupported().getFormatArray();

        for (ComplexDataDescriptionType format : supportedFormats) {
            formats.add(createFormat(format, false));
        }

        wpsParameter.setFormats(formats);

        return wpsParameter;

    }

    private static AllowedValues createAllowedValues(
            net.opengis.ows.x11.AllowedValuesDocument.AllowedValues allowedValues) {
        // TODO implement
        return new AllowedValues();
    }

    private static org.n52.geoprocessing.wps.client.model.Format createFormat(ComplexDataDescriptionType formatDescription, boolean isDefaultFormat) {
        org.n52.geoprocessing.wps.client.model.Format format = new org.n52.geoprocessing.wps.client.model.Format();

        format.setMimeType(formatDescription.getMimeType());
        format.setEncoding(formatDescription.getEncoding());
        format.setSchema(formatDescription.getSchema());
        format.setDefault(isDefaultFormat);

        return format;
    }

}
