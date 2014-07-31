package com.fedex.packages.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a conversion between two measures.
 * 
 * @author Javier Sanchez Hernandez
 * @version 1.0
 */
public class Conversion {
	private String conversion_from;
	private String conversion_to;
	private double conversion_value;

	public Conversion(JSONObject object) {
		try {
			this.conversion_from = object.getString("from");
			this.conversion_to = object.getString("to");
			this.conversion_value = object.getDouble("conversion");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the conversion_from
	 */
	public String getConversion_from() {
		return conversion_from;
	}

	/**
	 * @param conversion_from
	 *            the conversion_from to set
	 */
	public void setConversion_from(String conversion_from) {
		this.conversion_from = conversion_from;
	}

	/**
	 * @return the conversion_to
	 */
	public String getConversion_to() {
		return conversion_to;
	}

	/**
	 * @param conversion_to
	 *            the conversion_to to set
	 */
	public void setConversion_to(String conversion_to) {
		this.conversion_to = conversion_to;
	}

	/**
	 * @return the conversion_value
	 */
	public double getConversion_value() {
		return conversion_value;
	}

	/**
	 * @param conversion_value
	 *            the conversion_value to set
	 */
	public void setConversion_value(double conversion_value) {
		this.conversion_value = conversion_value;
	}

	// User.fromJson(jsonArray);
	public static ArrayList<Conversion> fromJson(JSONArray jsonObjects) {
		ArrayList<Conversion> conversions = new ArrayList<Conversion>();
		for (int i = 0; i < jsonObjects.length(); i++) {
			try {
				conversions.add(new Conversion(jsonObjects.getJSONObject(i)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return conversions;
	}
}
