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
package org.n52.geoprocessing.wps.client.xml;

import javax.xml.namespace.QName;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public interface WPS100Constants {
    String NS_WPS = "http://www.opengis.net/wps/1.0.0";

    String NS_WPS_PREFIX = "wps";

    static QName wps(String element) {
        return new QName(NS_WPS, element, NS_WPS_PREFIX);
    }

    static QName noNamespace(String element) {
        return new QName(element);
    }

    public interface Attr {
        String AN_DATA_TYPE = "dataType";

        QName QN_DATA_TYPE = wps(AN_DATA_TYPE);

        String AN_DEFAULT = "default";

        QName QN_DEFAULT = wps(AN_DEFAULT);

        String AN_ENCODING = "encoding";

        QName QN_ENCODING = wps(AN_ENCODING);

        String AN_CRS = "crs";

        QName QN_CRS = wps(AN_CRS);

        QName QN_CRS_NO_NAMESPACE = noNamespace(AN_CRS);

        String AN_DIMENSIONS = "dimensions";

        QName QN_DIMENSIONS = wps(AN_DIMENSIONS);

        QName QN_DIMENSIONS_NO_NAMESPACE = noNamespace(AN_DIMENSIONS);

        String AN_ID = "id";

        QName QN_ID = wps(AN_ID);

        String AN_MAXIMUM_MEGABYTES = "maximumMegabytes";

        QName QN_MAXIMUM_MEGABYTES = wps(AN_MAXIMUM_MEGABYTES);

        String AN_MIME_TYPE = "mimeType";

        QName QN_MIME_TYPE = wps(AN_MIME_TYPE);

        String AN_PROCESS_VERSION = "processVersion";

        QName QN_PROCESS_VERSION = wps(AN_PROCESS_VERSION);

        String AN_SCHEMA = "schema";

        QName QN_SCHEMA = wps(AN_SCHEMA);

        String AN_SERVICE = "service";

        QName QN_SERVICE = wps(AN_SERVICE);

        String AN_AS_REFERENCE = "asReference";

        QName QN_AS_REFERENCE = noNamespace(AN_AS_REFERENCE);

        String AN_STORE_EXECUTE_RESPONSE = "storeExecuteResponse";

        QName QN_STORE_EXECUTE_RESPONSE = noNamespace(AN_STORE_EXECUTE_RESPONSE);

        String AN_STORE_SUPPORTED = "storeSupported";

        QName QN_AN_STORE_SUPPORTED = noNamespace(AN_STORE_SUPPORTED);

        String AN_STATUS = "status";

        QName QN_STATUS = noNamespace(AN_STATUS);

        String AN_STATUS_LOCATION = "statusLocation";

        QName QN_STATUS_LOCATION = noNamespace(AN_STATUS_LOCATION);

        String AN_UOM = "uom";

        QName QN_UOM = wps(AN_UOM);

        String AN_VERSION = "version";

        QName QN_VERSION = wps(AN_VERSION);
    }

    public interface Elem {
        String EN_BODY = "Body";

        QName QN_BODY = wps(EN_BODY);

        String EN_BODY_REFERENCE = "BodyReference";

        QName QN_BODY_REFERENCE = wps(EN_BODY_REFERENCE);

        String EN_BOUNDING_BOX_DATA = "BoundingBoxData";

        QName QN_BOUNDING_BOX_DATA = wps(EN_BOUNDING_BOX_DATA);

        QName QN_BOUNDING_BOX_DATA_NO_NAMESPACE = noNamespace(EN_BOUNDING_BOX_DATA);

        String EN_CAPABILITIES = "Capabilities";

        QName QN_CAPABILITIES = wps(EN_CAPABILITIES);

        String EN_COMPLEX_DATA = "ComplexData";

        QName QN_COMPLEX_DATA = wps(EN_COMPLEX_DATA);

        QName QN_COMPLEX_DATA_NO_NAMESPACE = noNamespace(EN_COMPLEX_DATA);

        String EN_CONTENTS = "Contents";

        QName QN_CONTENTS = wps(EN_CONTENTS);

        String EN_CRS = "CRS";

        QName QN_CRS = wps(EN_CRS);

        QName QN_CRS_NO_NAMESPACE = noNamespace(EN_CRS);

        String EN_DATA = "Data";

        QName QN_DATA = wps(EN_DATA);

        String EN_DATA_DESCRIPTION = "DataDescription";

        QName QN_DATA_DESCRIPTION = wps(EN_DATA_DESCRIPTION);

        String EN_DESCRIBE_PROCESS = "DescribeProcess";

        QName QN_DESCRIBE_PROCESS = wps(EN_DESCRIBE_PROCESS);

        String EN_ESTIMATED_COMPLETION = "EstimatedCompletion";

        QName QN_ESTIMATED_COMPLETION = wps(EN_ESTIMATED_COMPLETION);

        String EN_EXECUTE = "Execute";

        QName QN_EXECUTE = wps(EN_EXECUTE);

        String EN_EXECUTE_RESPONSE = "ExecuteResponse";

        QName QN_EXECUTE_RESPONSE = wps(EN_EXECUTE_RESPONSE);

        String EN_RESPONSE_FORM = "ResponseForm";

        QName QN_RESPONSE_FORM = wps(EN_RESPONSE_FORM);

        String EN_RESPONSE_DOCUMENT = "ResponseDocument";

        QName QN_RESPONSE_DOCUMENT = wps(EN_RESPONSE_DOCUMENT);

        String EN_RAW_DATA_OUTPUT = "RawDataOutput";

        QName QN_RAW_DATA_OUTPUT = wps(EN_RAW_DATA_OUTPUT);

        String EN_EXPIRATION_DATE = "ExpirationDate";

        QName QN_EXPIRATION_DATE = wps(EN_EXPIRATION_DATE);

        String EN_EXTENSION = "Extension";

        QName QN_EXTENSION = wps(EN_EXTENSION);

        String EN_FORMAT = "Format";

        QName QN_FORMAT = wps(EN_FORMAT);

        QName QN_FORMAT_NO_NAMESPACE = noNamespace(EN_FORMAT);

        String EN_GENERIC_PROCESS = "GenericProcess";

        QName QN_GENERIC_PROCESS = wps(EN_GENERIC_PROCESS);

        String EN_GET_CAPABILITIES = "GetCapabilities";

        QName QN_GET_CAPABILITIES = wps(EN_GET_CAPABILITIES);

        String EN_INPUT = "Input";

        QName QN_INPUT = wps(EN_INPUT);

        QName QN_INPUT_NO_NAMESPACE = noNamespace(EN_INPUT);

        String EN_DATA_INPUTS = "DataInputs";

        QName QN_DATA_INPUTS = wps(EN_DATA_INPUTS);

        QName QN_DATA_INPUTS_NO_NAMESPACE = noNamespace(EN_DATA_INPUTS);

        String EN_JOB_ID = "JobID";

        QName QN_JOB_ID = wps(EN_JOB_ID);

        String EN_LITERAL_DATA = "LiteralData";

        QName QN_LITERAL_DATA = wps(EN_LITERAL_DATA);

        QName QN_LITERAL_DATA_NO_NAMESPACE = noNamespace(EN_LITERAL_DATA);

        String EN_LITERAL_DATA_DOMAIN = "LiteralDataDomain";

        QName QN_LITERAL_DATA_DOMAIN = new QName(EN_LITERAL_DATA_DOMAIN);

        String EN_LITERAL_VALUE = "LiteralValue";

        QName QN_LITERAL_VALUE = wps(EN_LITERAL_VALUE);

        String EN_NEXT_POLL = "NextPoll";

        QName QN_NEXT_POLL = wps(EN_NEXT_POLL);

        String EN_OUTPUT = "Output";

        QName QN_OUTPUT = wps(EN_OUTPUT);

        QName QN_OUTPUT_NO_NAMESPACE = noNamespace(EN_OUTPUT);

        String EN_COMPLEX_OUTPUT = "ComplexOutput";

        QName QN_COMPLEX_OUTPUT = wps(EN_COMPLEX_OUTPUT);

        QName QN_COMPLEX_OUTPUT_NO_NAMESPACE = noNamespace(EN_COMPLEX_OUTPUT);

        String EN_LITERAL_OUTPUT = "LiteralOutput";

        QName QN_LITERAL_OUTPUT = wps(EN_LITERAL_OUTPUT);

        QName QN_LITERAL_OUTPUT_NO_NAMESPACE = noNamespace(EN_LITERAL_OUTPUT);

        String EN_BOUNDING_BOX_OUTPUT = "BoundingBoxOutput";

        QName QN_BOUNDING_BOX_OUTPUT = wps(EN_BOUNDING_BOX_OUTPUT);

        QName QN_BOUNDING_BOX_OUTPUT_NO_NAMESPACE = noNamespace(EN_BOUNDING_BOX_OUTPUT);

        String EN_PROCESS_OUTPUTS = "ProcessOutputs";

        QName QN_PROCESS_OUTPUTS_NO_NAMESPACE = noNamespace(EN_PROCESS_OUTPUTS);

        QName QN_PROCESS_OUTPUTS = wps(EN_PROCESS_OUTPUTS);

        String EN_PERCENT_COMPLETED = "PercentCompleted";

        QName QN_PERCENT_COMPLETED = wps(EN_PERCENT_COMPLETED);

        String EN_PROCESS = "Process";

        QName QN_PROCESS = wps(EN_PROCESS);

        String EN_PROCESS_OFFERING = "ProcessOffering";

        QName QN_PROCESS_OFFERING = wps(EN_PROCESS_OFFERING);

        String EN_PROCESS_OFFERINGS = "ProcessOfferings";

        QName QN_PROCESS_OFFERINGS = wps(EN_PROCESS_OFFERINGS);

        String EN_PROCESS_DESCRIPTIONS = "ProcessDescriptions";

        QName QN_PROCESS_DESCRIPTIONS = wps(EN_PROCESS_DESCRIPTIONS);

        String EN_PROCESS_DESCRIPTION = "ProcessDescription";

        QName QN_PROCESS_DESCRIPTION = noNamespace(EN_PROCESS_DESCRIPTION);

        String EN_REFERENCE = "Reference";

        QName QN_REFERENCE = wps(EN_REFERENCE);

        String EN_RESULT = "Result";

        QName QN_RESULT = wps(EN_RESULT);

        String EN_STATUS = "Status";

        QName QN_STATUS = wps(EN_STATUS);

        String EN_STATUS_INFO = "StatusInfo";

        QName QN_STATUS_INFO = wps(EN_STATUS_INFO);

        String EN_DEFAULT = "Default";

        QName QN_DEFAULT = wps(EN_DEFAULT);

        QName QN_DEFAULT_NO_NAMESPACE = noNamespace(EN_DEFAULT);

        String EN_SUPPORTED = "Supported";

        QName QN_SUPPORTED = wps(EN_SUPPORTED);

        QName QN_SUPPORTED_NO_NAMESPACE = noNamespace(EN_SUPPORTED);

        String EN_WSDL = "WSDL";

        QName QN_WSDL = wps(EN_WSDL);

        String EN_LANGUAGE = "Language";

        QName QN_LANGUAGE = wps(EN_LANGUAGE);

        String EN_LANGUAGES = "Languages";

        QName QN_LANGUAGES = wps(EN_LANGUAGES);

        String EN_PROCESS_ACCEPTED = "ProcessAccepted";

        QName QN_PROCESS_ACCEPTED = wps(EN_PROCESS_ACCEPTED);

        String EN_PROCESS_FAILED = "ProcessFailed";

        QName QN_PROCESS_FAILED = wps(EN_PROCESS_FAILED);

        String EN_PROCESS_PAUSED = "ProcessPaused";

        QName QN_PROCESS_PAUSED = wps(EN_PROCESS_PAUSED);

        String EN_PROCESS_STARTED = "ProcessStarted";

        QName QN_PROCESS_STARTED = wps(EN_PROCESS_STARTED);

        String EN_PROCESS_SUCCEEDED = "ProcessSucceeded";

        QName QN_PROCESS_SUCCEEDED = wps(EN_PROCESS_SUCCEEDED);

        String EN_MIME_TYPE = "MimeType";

        QName QN_MIME_TYPE = wps(EN_MIME_TYPE);

        QName QN_MIME_TYPE_NO_NAMESPACE = noNamespace(EN_MIME_TYPE);

        String EN_SCHEMA = "Schema";

        QName QN_SCHEMA = wps(EN_SCHEMA);

        QName QN_SCHEMA_NO_NAMESPACE = noNamespace(EN_SCHEMA);

        String EN_ENCODING = "Encoding";

        QName QN_ENCODING = wps(EN_ENCODING);

        QName QN_ENCODING_NO_NAMESPACE = noNamespace(EN_ENCODING);
        
        String EN_DEFAULT_VALUE = "DefaultValue";

        QName QN_DEFAULT_VALUE = wps(EN_DEFAULT_VALUE);

        QName QN_DEFAULT_VALUE_NO_NAMESPACE = noNamespace(EN_DEFAULT_VALUE);
    }

}
