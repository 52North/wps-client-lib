package org.n52.geoprocessing.wps.client.model;

public class BoundingBox {

    private double[] lowerCorner;
    private double[] upperCorner;
    private int dimensions;
    private String crs;
    
	public double[] getLowerCorner() {
		return lowerCorner;
	}
	
	public void setLowerCorner(double[] lowerCorner) {
		this.lowerCorner = lowerCorner;
	}
	
	public double[] getUpperCorner() {
		return upperCorner;
	}
	
	public void setUpperCorner(double[] upperCorner) {
		this.upperCorner = upperCorner;
	}
	
	public int getDimensions() {
		return dimensions;
	}
	
	public void setDimensions(int dimensions) {
		this.dimensions = dimensions;
	}
	
	public String getCrs() {
		return crs;
	}
	
	public void setCrs(String crs) {
		this.crs = crs;
	}
	
}
