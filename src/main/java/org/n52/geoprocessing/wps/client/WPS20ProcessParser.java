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
import org.n52.geoprocessing.wps.client.model.OutputDescription;
import org.n52.geoprocessing.wps.client.model.Process;
import org.n52.geoprocessing.wps.client.model.WPSDescriptionParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.opengis.ows.x20.BoundingBoxType;
import net.opengis.wps.x20.BoundingBoxDataDocument.BoundingBoxData;
import net.opengis.wps.x20.ComplexDataType;
import net.opengis.wps.x20.DataDescriptionType;
import net.opengis.wps.x20.FormatDocument.Format;
import net.opengis.wps.x20.InputDescriptionType;
import net.opengis.wps.x20.LiteralDataType;
import net.opengis.wps.x20.LiteralDataType.LiteralDataDomain;
import net.opengis.wps.x20.OutputDescriptionType;
import net.opengis.wps.x20.ProcessOfferingDocument.ProcessOffering;

public class WPS20ProcessParser {

    private static Logger LOGGER = LoggerFactory.getLogger(WPS20ProcessParser.class);

    public static Process completeProcess(ProcessOffering processOffering,
            Process process) {

        InputDescriptionType[] inputDescriptions = processOffering.getProcess().getInputArray();

        List<InputDescription> inputs = new ArrayList<>();

        for (InputDescriptionType inputDescriptionType : inputDescriptions) {

            inputs.add(createInput(inputDescriptionType));
        }

        process.setInputs(inputs);

        OutputDescriptionType[] outputDescriptions = processOffering.getProcess().getOutputArray();

        List<OutputDescription> outputs = new ArrayList<>();

        for (OutputDescriptionType outputDescriptionType : outputDescriptions) {

            outputs.add(createOutput(outputDescriptionType));
        }

        process.setOutputs(outputs);

        return process;
    }

    private static OutputDescription createOutput(OutputDescriptionType outputDescriptionType) {
        OutputDescription output = new OutputDescription();

        DataDescriptionType dataType = outputDescriptionType.getDataDescription();

        output = (OutputDescription) createWPSParameter(dataType, output);
        try {
            output.setId(outputDescriptionType.getIdentifier().getStringValue());
        } catch (Exception e) {
            LOGGER.info("Could not parse identifier of output: " + output.getId());
            // TODO throw exception as output must have identifier
        }

        try {
            output.setAbstract(outputDescriptionType.getAbstractArray(0).getStringValue());
        } catch (Exception e) {
            LOGGER.info("Could not parse abstract of output: " + output.getId());
        }

        try {
            output.setTitle(outputDescriptionType.getTitleArray(0).getStringValue());
        } catch (Exception e) {
            LOGGER.info("Could not parse title of output: " + output.getId());
        }

        return output;
    }

    private static InputDescription createInput(InputDescriptionType inputDescriptionType) {

        InputDescription input = new InputDescription();

        DataDescriptionType dataType = inputDescriptionType.getDataDescription();

        input = (InputDescription) createWPSParameter(dataType, input);

        try {
            input.setId(inputDescriptionType.getIdentifier().getStringValue());
        } catch (Exception e) {
            LOGGER.info("Could not parse identifier of input: " + input.getId());
            // TODO throw exception as input must have identifier
        }

        try {
            input.setAbstract(inputDescriptionType.getAbstractArray(0).getStringValue());
        } catch (Exception e) {
            LOGGER.info("Could not parse abstract of input: " + input.getId());
        }

        try {
            input.setTitle(inputDescriptionType.getTitleArray(0).getStringValue());
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

    private static WPSDescriptionParameter createWPSParameter(DataDescriptionType dataType,
            WPSDescriptionParameter wpsParameter) {

        if (dataType instanceof ComplexDataType) {

            ComplexDataType complexDataType = (ComplexDataType) dataType;

            Format[] formatDescriptions = complexDataType.getFormatArray();

            List<org.n52.geoprocessing.wps.client.model.Format> formats = new ArrayList<>();

            for (Format format : formatDescriptions) {
                formats.add(createFormat(format));
            }

            wpsParameter.setFormats(formats);

        } else if (dataType instanceof LiteralDataType) {

            LiteralDataType literalDataType = (LiteralDataType) dataType;

            String dataTypeString = literalDataType.getLiteralDataDomainArray(0).getDataType().getReference();

            if(wpsParameter instanceof InputDescription){
                wpsParameter = new LiteralInputDescription();

                wpsParameter = createLiteralInput(literalDataType, (LiteralInputDescription) wpsParameter, dataTypeString);
            }

            Format[] formatDescriptions = literalDataType.getFormatArray();

            List<org.n52.geoprocessing.wps.client.model.Format> formats = new ArrayList<>();

            for (Format format : formatDescriptions) {
                formats.add(createFormat(format));
            }

            wpsParameter.setFormats(formats);

        } else if (dataType instanceof BoundingBoxType) {

            BoundingBoxData boundingBoxDataType = (BoundingBoxData) dataType;
            // boundingBoxDataType.

            // TODO supported crs
        }
        return wpsParameter;

    }

    private static InputDescription createLiteralInput(LiteralDataType literalDataType,
            LiteralInputDescription literalInput,
            String dataTypeString) {

        literalInput.setDataType(dataTypeString);

        try {

            boolean anyValue = false;

            LiteralDataDomain literalDataDomain = literalDataType.getLiteralDataDomainArray(0);

            if (literalDataDomain.isSetAnyValue()) {
                anyValue = true;
                literalInput.setAnyValue(anyValue);
            }

            if (literalDataDomain.isSetAllowedValues()) {
                literalInput.setAllowedValues(createAllowedValues(literalDataDomain.getAllowedValues()));
            }

            if (literalDataDomain.isSetDefaultValue()) {
                literalInput.setDefaultValue(literalDataDomain.getDefaultValue().getStringValue());
            }

        } catch (Exception e) {
            LOGGER.info("LiteralInput has no LiteralDataDomain: " + literalInput.getId());
        }

        return literalInput;
    }

    private static AllowedValues createAllowedValues(
            net.opengis.ows.x20.AllowedValuesDocument.AllowedValues allowedValues) {
        // TODO implement
        return new AllowedValues();
    }

    private static org.n52.geoprocessing.wps.client.model.Format createFormat(Format formatDescription) {
        org.n52.geoprocessing.wps.client.model.Format format = new org.n52.geoprocessing.wps.client.model.Format();

        format.setMimeType(formatDescription.getMimeType());
        format.setEncoding(formatDescription.getEncoding());
        format.setSchema(formatDescription.getSchema());

        return format;
    }

}
