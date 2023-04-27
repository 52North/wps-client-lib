/*
 * ﻿Copyright (C) 2023 52°North Initiative for Geospatial Open Source
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
package org.n52.geoprocessing.wps.client.model;

import java.time.OffsetDateTime;

import org.n52.shetland.ogc.wps.JobStatus;

public class StatusInfo extends WPSResponse {

    private JobStatus status;

    private String statusLocation;

    private String message;

    private String jobId;

    private String version;

    private short percentCompleted;

    private OffsetDateTime expirationDate;

    private OffsetDateTime estimatedCompletion;

    private OffsetDateTime nextPoll;

    private Result result;

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus jobStatus) {
        this.status = jobStatus;
    }

    public short getPercentCompleted() {
        return percentCompleted;
    }

    public void setPercentCompleted(short percentCompleted) {
        this.percentCompleted = percentCompleted;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStatusLocation() {
        return statusLocation;
    }

    public void setStatusLocation(String statusLocation) {
        this.statusLocation = statusLocation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public OffsetDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(OffsetDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public OffsetDateTime getEstimatedCompletion() {
        return estimatedCompletion;
    }

    public void setEstimatedCompletion(OffsetDateTime estimatedCompletion) {
        this.estimatedCompletion = estimatedCompletion;
    }

    public OffsetDateTime getNextPoll() {
        return nextPoll;
    }

    public void setNextPoll(OffsetDateTime nextPoll) {
        this.nextPoll = nextPoll;
    }

    public Result getResult() {
        return result;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(getStatus());

        return stringBuilder.toString();
    }

}
