package com.fedex.packages.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a package sent by FedEx.
 * 
 * @author Javier Sanchez Hernandez
 * @version 1.0
 */
public class FedExPackage {
	private String id;
	private String destination;
	private double weight;
	private String measure;

	public FedExPackage(JSONObject object) {
		try {
			this.id = object.getString("id");
			this.destination = object.getString("destination");
			this.weight = object.getDouble("weight");
			this.measure = object.getString("measure");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the destination
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * @param weight
	 *            the weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}

	/**
	 * @return the measure
	 */
	public String getMeasure() {
		return measure;
	}

	/**
	 * @param measure
	 *            the measure to set
	 */
	public void setMeasure(String measure) {
		this.measure = measure;
	}

	// User.fromJson(jsonArray);
	public static ArrayList<FedExPackage> fromJson(JSONArray jsonObjects) {
		ArrayList<FedExPackage> packages = new ArrayList<FedExPackage>();
		for (int i = 0; i < jsonObjects.length(); i++) {
			try {
				packages.add(new FedExPackage(jsonObjects.getJSONObject(i)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return packages;
	}
}
