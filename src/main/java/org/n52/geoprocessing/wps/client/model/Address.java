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
package org.n52.geoprocessing.wps.client.model;

public class Address {

    private String deliveryPoint;

    private String city;

    private String administrativeArea;

    private String postalCode;

    private String country;

    private String electronicMailAddress;

    public String getDeliveryPoint() {
        return deliveryPoint;
    }

    public void setDeliveryPoint(String deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAdministrativeArea() {
        return administrativeArea;
    }

    public void setAdministrativeArea(String administrativeArea) {
        this.administrativeArea = administrativeArea;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getElectronicMailAddress() {
        return electronicMailAddress;
    }

    public void setElectronicMailAddress(String electronicMailAddress) {
        this.electronicMailAddress = electronicMailAddress;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("DeliveryPoint: " + getDeliveryPoint() + "\n");
        stringBuilder.append("\t\t\t\t\t\t\t\t PostalCode: " + getPostalCode() + "\n");
        stringBuilder.append("\t\t\t\t\t\t\t\t City: " + getCity() + "\n");
        stringBuilder.append("\t\t\t\t\t\t\t\t Administrative area: " + getAdministrativeArea() + "\n");
        stringBuilder.append("\t\t\t\t\t\t\t\t Country: " + getCountry() + "\n");
        stringBuilder.append("\t\t\t\t\t\t\t\t Electronic mail address: " + getElectronicMailAddress() + "\n");

        return stringBuilder.toString();
    }
}
