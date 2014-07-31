package com.fedex.packages.util;

import android.database.Cursor;

/**
 * Used to convert between weight units.
 * 
 * @author Javier Sanchez Hernandez
 * @version 1.0
 */
public class Conversions {

	private DataBaseHandler dbHandler;

	public Conversions(DataBaseHandler dbHandler) {
		this.dbHandler = dbHandler;
	}

	/**
	 * Converts a weight from one unit to another having concrete conversion.
	 * 
	 * @param originalWeight
	 * @param conversion
	 * @return
	 */
	public double convert(double originalWeight, double conversion) {
		double convertedWeight = originalWeight * conversion;
		return convertedWeight;
	}
	
	/**
	 * Converts a weight from an indicated measure to kilograms.
	 * @param originalWeight
	 * @param measure
	 * @return
	 */
	public double convertToKilograms(double originalWeight, String measure) {
		double convertedWeight = 0.0;

		double conversion = 1.0;
		Cursor c = dbHandler.getConversionToKilograms(measure);
		if (c.moveToFirst()) {
			// Recorremos el cursor hasta que no haya más registros
			do {
				conversion = c.getDouble(0);
				// System.out.println(conversion);
			} while (c.moveToNext());
		}
		c.close();
		convertedWeight = convert(originalWeight, conversion);

		return convertedWeight;
	}
}
