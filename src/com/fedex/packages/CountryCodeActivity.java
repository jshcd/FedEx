package com.fedex.packages;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.fedex.packages.model.CustomListItem;
import com.fedex.packages.util.Conversions;
import com.fedex.packages.util.DataBaseHandler;
import com.fedex.packages.view.MyListAdapter;

/**
 * Shows a list with the information (total weight to transport and number of
 * airplanes needed) and the products to send to the selected country.
 * 
 * @author Javier Sanchez Hernandez
 * @version 1.0
 */
public class CountryCodeActivity extends ActionBarActivity {
	private final String TAG = CountryCodeActivity.class.getSimpleName();
	private String countryName;
	private ListView mListView;
	private DataBaseHandler dbHandler;
	private ArrayList<CustomListItem> packages = new ArrayList<CustomListItem>();
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_country_code);

		Bundle b = getIntent().getExtras();
		countryName = b.getString("country");

		// Get action bar
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(countryName);
		// Enable Back navigation
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Initialize the ListView
		mListView = (ListView) findViewById(R.id.listViewCountryCode);
		// Initialize the database handler
		dbHandler = new DataBaseHandler(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Shows a ProgressDialog for not to allow the user interact with the
		// user interface
		dialog = ProgressDialog.show(CountryCodeActivity.this, "",
				getResources().getString(R.string.loading_please_wait), true);
		dialog.show();

		// Create and start a thread to calculate the total weight and to show
		// the required data on the ListView
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					dbHandler.openDataBase();

					double totalWeight = 0.0;

					Cursor c = dbHandler.getPackagesByCountry(countryName);
					if (c.moveToFirst()) {
						// We walk along the cursor until no more records adding
						// each package to the list and adding its weight to the
						// total weight needed to calculate the number of
						// airplanes needed
						do {
							String destination = c.getString(0);
							double weight = c.getDouble(2);
							String measure = c.getString(3);

							// Convert weight to kilograms
							Conversions conversionHandler = new Conversions(
									dbHandler);
							double convertedWeight = conversionHandler
									.convertToKilograms(weight, measure);

							// Increment the total Weight
							totalWeight += convertedWeight;

							CustomListItem listItem = new CustomListItem(
									destination);
							packages.add(listItem);
						} while (c.moveToNext());
					}
					c.close();

					// Add the Products header title
					CustomListItem listItem = new CustomListItem(getResources()
							.getString(R.string.products), true);
					packages.add(0, listItem);

					// Add the total weight field
					listItem = new CustomListItem(Integer.toString((int) Math
							.ceil(totalWeight))
							+ " "
							+ getResources().getString(R.string.kilograms));
					packages.add(0, listItem);

					// Calculates the total number of airplanes
					int numberOfAirplanes = (int) Math.ceil(totalWeight
							/ Constants.AIRBUS_A330_300_CAPACITY);

					// Add the total number of airplanes field
					listItem = new CustomListItem(numberOfAirplanes + " "
							+ getResources().getString(R.string.airplanes));
					packages.add(0, listItem);

					// Add the Information header title
					listItem = new CustomListItem(getResources().getString(
							R.string.information), true);
					packages.add(0, listItem);

					// Set the Adapter and close the ProgressDialog
					CountryCodeActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mListView.setAdapter(new MyListAdapter(
									getApplicationContext(), packages));
							dialog.dismiss();
						}
					});

				} catch (SQLiteException e) {
					Log.e(TAG, e.toString());
				} finally {
					dbHandler.close();
				}
			}
		}).start();

	}

	/**
	 * Manages the menu option selected.
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return true;
	}
}
