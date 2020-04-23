package org.n52.geoprocessing.wps.client.model;

import java.util.Objects;

public class UOM implements Comparable<UOM>{

    private boolean defaultUOM;
    
    private String uomString;
    
    public UOM(String uomString) {
        this(uomString, false);
    }
    
    public UOM(String uomString, boolean defaultUOM) {
        this.uomString = uomString;
        this.defaultUOM = defaultUOM;
    }
    
    public String getUomString() {
        return uomString;
    }

    public void setUomString(String uomString) {
        this.uomString = uomString;
    }

    public boolean isDefaultUOM() {
        return defaultUOM;
    }

    public void setDefaultUOM(boolean defaultUOM) {
        this.defaultUOM = defaultUOM;
    }

    @Override
    public int compareTo(UOM o) {
        return this.uomString.compareTo(o.uomString);
    }    
    
    @Override
    public boolean equals(Object obj) {
        
        if (obj instanceof UOM) {
            
            UOM other = (UOM) obj;
            
            return uomString.equals(other.uomString);
        }
        
        return false;
    }
}
