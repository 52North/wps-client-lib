package org.n52.geoprocessing.wps.client;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.n52.geoprocessing.wps.client.model.AllowedValues;
import org.n52.geoprocessing.wps.client.model.Input;
import org.n52.geoprocessing.wps.client.model.LiteralInput;
import org.n52.geoprocessing.wps.client.model.Output;
import org.n52.geoprocessing.wps.client.model.Process;
import org.n52.geoprocessing.wps.client.model.WPSParameter;
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

        List<Input> inputs = new ArrayList<>();

        for (InputDescriptionType inputDescriptionType : inputDescriptions) {

            inputs.add(createInput(inputDescriptionType));
        }

        process.setInputs(inputs);

        OutputDescriptionType[] outputDescriptions = processOffering.getProcess().getOutputArray();

        List<Output> outputs = new ArrayList<>();

        for (OutputDescriptionType outputDescriptionType : outputDescriptions) {

            outputs.add(createOutput(outputDescriptionType));
        }

        process.setOutputs(outputs);

        return process;
    }

    private static Output createOutput(OutputDescriptionType outputDescriptionType) {
        Output output = new Output();

        DataDescriptionType dataType = outputDescriptionType.getDataDescription();

        return output;
    }

    private static Input createInput(InputDescriptionType inputDescriptionType) {

        Input input = new Input();

        DataDescriptionType dataType = inputDescriptionType.getDataDescription();

        input = (Input) createWPSParameter(dataType, input);

        try {
            input.setId(inputDescriptionType.getIdentifier().getStringValue());
        } catch (Exception e) {
            LOGGER.info("Could not parse identifier of input.");
            // TODO throw exception as input must have identifier
        }

        try {
            input.setAbstract(inputDescriptionType.getAbstractArray(0).getStringValue());
        } catch (Exception e) {
            LOGGER.info("Could not parse abstract of input.");
        }

        try {
            input.setTitle(inputDescriptionType.getTitleArray(0).getStringValue());
        } catch (Exception e) {
            LOGGER.info("Could not parse title of input.");
        }

        Object minOccursObject = inputDescriptionType.getMinOccurs();

        try {
            if (minOccursObject instanceof BigInteger) {
                input.setMinOccurs(((BigInteger) minOccursObject).intValue());
            }
        } catch (Exception e) {
            LOGGER.info("Could not parse minimum occurs of input.");
        }

        Object maxOccursObject = inputDescriptionType.getMaxOccurs();

        try {
            if (maxOccursObject instanceof BigInteger) {
                input.setMaxOccurs(((BigInteger) maxOccursObject).intValue());
            }
        } catch (Exception e) {
            LOGGER.info("Could not parse maximum occurs of input.");
        }

        return input;
    }

    private static WPSParameter createWPSParameter(DataDescriptionType dataType,
            WPSParameter wpsParameter) {

        if (dataType instanceof ComplexDataType) {

            // wpsParameter = new ComplexInput();

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

            wpsParameter = new LiteralInput();

            wpsParameter = createLiteralInput(literalDataType, (LiteralInput) wpsParameter, dataTypeString);

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

    private static Input createLiteralInput(LiteralDataType literalDataType,
            LiteralInput literalInput,
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
        // TODO Auto-generated method stub
        return null;
    }

    private static org.n52.geoprocessing.wps.client.model.Format createFormat(Format formatDescription) {
        org.n52.geoprocessing.wps.client.model.Format format = new org.n52.geoprocessing.wps.client.model.Format();

        format.setMimeType(formatDescription.getMimeType());
        format.setEncoding(formatDescription.getEncoding());
        format.setSchema(formatDescription.getSchema());

        return format;
    }

}
