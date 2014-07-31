package com.fedex.packages.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Represents a country identified by its code.
 * 
 * @author Javier Sanchez Hernandez
 * @version 1.0
 */
public class Country {
	private String name;

	public Country(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	// User.fromJson(jsonArray);
	public static ArrayList<Country> fromJson(JSONArray jsonObjects) {
		ArrayList<Country> countries = new ArrayList<Country>();
		for (int i = 0; i < jsonObjects.length(); i++) {
			try {
				countries.add(new Country(jsonObjects.getJSONObject(i)
						.getString("destination")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return countries;
	}
}
