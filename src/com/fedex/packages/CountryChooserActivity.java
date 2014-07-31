package com.fedex.packages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.fedex.packages.model.Conversion;
import com.fedex.packages.model.CustomListItem;
import com.fedex.packages.model.FedExPackage;
import com.fedex.packages.util.DataBaseHandler;
import com.fedex.packages.view.MyListAdapter;

/**
 * Shows a list of countries to be selected by the user.
 * 
 * @author Javier Sanchez Hernandez
 * @version 1.0
 */
public class CountryChooserActivity extends ActionBarActivity {
	private final String TAG = CountryChooserActivity.class.getSimpleName();
	private ListView mListView;
	private DataBaseHandler dbHandler;
	private ArrayList<CustomListItem> countries = new ArrayList<CustomListItem>();
	private final String PREFS_NAME = "status_preferences";
	private boolean orientationChanged = false;
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_country_chooser);
		
		// Initialize the ListView
		mListView = (ListView) findViewById(R.id.listViewCountryChooser);
		
		// Set a listener to the ListView
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				CustomListItem country = countries.get(arg2);
				// Create an Intent to open the CountryCodeActivity
				Intent i = new Intent(getBaseContext(),
						CountryCodeActivity.class);
				// Indicate which is the selected country
				i.putExtra("country", country.getText());
				// Start the CountryCodeActivity
				startActivity(i);
			}
		});
		
		// Initialize the database handler
		dbHandler = new DataBaseHandler(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		// Check if the application is being launched for first time or not
		if (settings.getBoolean("my_first_time", true)) {
			// The application is being launched for first time
			Log.d(TAG, "First execution");
			try {
				// Create the database
				dbHandler.createDataBase();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// record the fact that the app has been started at least once
			settings.edit().putBoolean("my_first_time", false).commit();
		}
		if (!orientationChanged) {
			updateDatabase();
		} else {
			reloadInterface();
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
		orientationChanged = true;

	}
	
	/**
	 * Initializes the options menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.country_chooser, menu);
		return true;
	}
	
	/**
	 * Manages the menu option selected.
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_reload:
			updateDatabase();
			break;
		}
		return true;
	}

	/**
	 * Updates the database giving the data from the web service and in the end,
	 * updates the user interface.
	 */
	private void updateDatabase() {
		// Shows a ProgressDialog for not to allow the user interact with the user interface
		dialog = ProgressDialog.show(CountryChooserActivity.this, "",
				getResources().getString(R.string.loading_please_wait), true);
		dialog.show();

		System.out.println("Beginning download");
		// Download the measure conversions data
		final DownloaderTask downloadConversions = new DownloaderTask();
		downloadConversions.execute(Constants.CONVERSIONS_WEBSERVICE_URL);
		
		// Download the packages data
		final DownloaderTask downloadPackages = new DownloaderTask();
		downloadPackages.execute(Constants.PACKAGES_WEBSERVICE_URL);

		// Create and start a thread to parse the data and to save it on the database
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Log.d(TAG, "Starting JSON parsing");
					JSONArray conversions = downloadConversions.get();
					JSONArray packages = downloadPackages.get();
					Log.d(TAG, "Finishing JSON parsing");

					ArrayList<FedExPackage> fedExPackages = FedExPackage
							.fromJson(packages);

					ArrayList<Conversion> mConversionsList = Conversion
							.fromJson(conversions);
					
					// Save into database
					Log.d(TAG, "Opening database");
					dbHandler.openDataBase();
					
					Log.d(TAG, "Starting database update");
					// Empty the table
					dbHandler.emptyPackagesTable();
					// Insert the packages data
					for (FedExPackage fedExPackage : fedExPackages) {
						dbHandler.insertPackage(fedExPackage);
					}

					// Empty the table
					dbHandler.emptyConversionsTable();
					// Insert the conversions data
					for (Conversion mConversion : mConversionsList) {
						dbHandler.insertConversion(mConversion);
					}
					Log.d(TAG, "Finishing database update");
				} catch (SQLiteException e) {
					Log.e(TAG, e.toString());
				} catch (InterruptedException e) {
					Log.e(TAG, e.toString());
				} catch (ExecutionException e) {
					Log.e(TAG, e.toString());
				} finally {
					dbHandler.close();
					Log.d(TAG, "Closing database");
				}
				// Reload user interface
				reloadInterface();
			}
		}).start();

	}

	/**
	 * Takes the data from the database and updates the list of countries
	 */
	private void reloadInterface() {
		// Remove all fields from the ArrayList
		countries.removeAll(countries);

		// Get all country identifiers without repetition
		dbHandler.openDataBase();
		Cursor c = dbHandler.getCountries();
		if (c.moveToFirst()) {
			// We walk along the cursor until no more records
			do {
				String destination = c.getString(0);
				CustomListItem listItem = new CustomListItem(destination);
				countries.add(listItem);
			} while (c.moveToNext());
		}
		c.close();
		dbHandler.close();

		// Set the Adapter and close the ProgressDialog
		CountryChooserActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mListView.setAdapter(new MyListAdapter(
						getApplicationContext(), countries));
				dialog.dismiss();
			}
		});
	}

	/**
	 * Internal class used to download data from the Internet.
	 * 
	 * @author Javier Sanchez Hernandez
	 * @version 1.0
	 */
	private class DownloaderTask extends AsyncTask<String, String, JSONArray> {
		/**
		 * The tag to show on the log messages.
		 */
		private final String TAG = DownloaderTask.class.getSimpleName();

		/**
		 * Downloads data from the given URL and returns it as a
		 * <code>JSONArray</code>.
		 * 
		 * @see JSONArray
		 */
		@Override
		protected JSONArray doInBackground(String... params) {
			String FeedPath = params[0];
			InputStream is = null;
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(FeedPath);
				HttpResponse response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			} catch (Exception e) {
				Log.e(TAG, "Error in http connection: " + e.toString());
			}

			String response = responseToString(is);
			JSONArray array = null;
			try {
				array = new JSONArray(response);
			} catch (JSONException e) {
				Log.e(TAG, "Error parsing data: " + e.toString());
			}
			return array;
		}

		/**
		 * Takes the data from the <code>InputStream</code> and stores it on a
		 * <code>String</code> object.
		 * 
		 * @param is
		 *            The input stream with the data given from the Internet.
		 * @return An <code>String</code> object containing all the information
		 *         given from the <code>InputStream</code>.
		 */
		private String responseToString(InputStream is) {
			String result = "";
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
			} catch (Exception e) {
				Log.e(TAG, "Error converting result " + e.toString());
			}
			return result;
		}
	}
}
