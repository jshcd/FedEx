package com.fedex.packages.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fedex.packages.model.Conversion;
import com.fedex.packages.model.FedExPackage;

/**
 * This class is a handler for the database. It provides methods to select,
 * insert or modify data, create the database, etc.
 * 
 * @author Javier Sanchez Hernandez
 * @version 1.0
 */
public class DataBaseHandler extends SQLiteOpenHelper {
	/**
	 * The tag to be shown on the applications logs.
	 */
	private final static String TAG = DataBaseHandler.class.getSimpleName();
	/**
	 * The database file name.
	 */
	private static String DB_NAME = "fedex.sqlite";
	/**
	 * The database object.
	 */
	private SQLiteDatabase myDataBase;
	/**
	 * The application context.
	 */
	private final Context myContext;
	/**
	 * The path where the database file is stored.
	 */
	private static String DB_PATH;

	/**
	 * Constructor of the class.
	 * 
	 * @param context
	 *            The application context.
	 */
	public DataBaseHandler(Context context) {
		super(context, DB_NAME, null, 1);
		myContext = context;
		DB_PATH = myContext.getFilesDir().getAbsolutePath().replace("files", "databases") + File.separator + DB_NAME;
		//DB_PATH = myContext.getDatabasePath(DB_NAME).getAbsolutePath();
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * 
	 * @throws IOException
	 */
	public void createDataBase() throws IOException {
		boolean dbExist = checkDataBase();
		if (dbExist) {
			// do nothing - database already exist
		} else {
			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are going to be able to overwrite that
			// database with our database.
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false otherwise.
	 */
	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		try {
			String myPath = DB_PATH;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);
		//} catch (SQLiteCantOpenDatabaseException e) {
		//	Log.e(TAG, "DataBase can not be opened");
		} catch (SQLiteException e) {
			Log.e(TAG, "DataBase does not exist yet");
		}
		if (checkDB != null) {

		}
		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 */
	private void copyDataBase() throws IOException {
		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);
		// Path to the just created empty db
		String outFileName = DB_PATH;
		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);
		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	/**
	 * Opens the database if exists in the path.
	 * 
	 * @throws SQLException
	 */
	public void openDataBase() throws SQLException {
		// Open the database
		String myPath = DB_PATH;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();
		super.close();
	}

	
	public Cursor getPackages() {
		String qry = "SELECT id_package, destination, weight, measure FROM packages";
		//System.out.println(qry);
		Cursor c = myDataBase.rawQuery(qry, null);
		return c;
	}
	
	public Cursor getPackagesByCountry(String countryName) {
		String qry = "SELECT id_package, destination, weight, measure FROM packages WHERE destination = '" + countryName + "'";
		//System.out.println(qry);
		Cursor c = myDataBase.rawQuery(qry, null);
		return c;
	}
	
	public Cursor getCountries() {
		String qry = "SELECT DISTINCT destination FROM packages";
		//System.out.println(qry);
		Cursor c = myDataBase.rawQuery(qry, null);
		return c;
	}
	
	public void insertPackage(FedExPackage fedExPackage) {
		String id = fedExPackage.getId();
		String destination = fedExPackage.getDestination();
		double weight = fedExPackage.getWeight();
		String measure = fedExPackage.getMeasure();
		String qry = "INSERT INTO packages (id_package, destination, weight, measure) "
				+ "VALUES ('"
				+ id
				+ "', '"
				+ destination
				+ "', "
				+ weight
				+ ", '"
				+ measure
				+ "')";
		//System.out.println(qry);
		myDataBase.execSQL(qry);
	}

	public void emptyPackagesTable() {
		String qry = "DELETE FROM packages WHERE 1";
		//System.out.println(qry);
		myDataBase.execSQL(qry);
	}
	
	public void insertConversion(Conversion conversion) {
		String conversion_from = conversion.getConversion_from();
		String conversion_to = conversion.getConversion_to();
		double conversion_value = conversion.getConversion_value();
		
		String qry = "INSERT INTO conversions (conversion_from, converstion_to, converstion_value) "
				+ "VALUES ('"
				+ conversion_from
				+ "', '"
				+ conversion_to
				+ "', "
				+ conversion_value
				+ ")";
		//System.out.println(qry);
		myDataBase.execSQL(qry);
	}
	
	public Cursor getConversionToKilograms(String measure) {
		String qry = "SELECT converstion_value FROM conversions WHERE converstion_to = 'KG' AND conversion_from = '" + measure + "'";
		//System.out.println(qry);
		Cursor c = myDataBase.rawQuery(qry, null);
		return c;
	}

	public void emptyConversionsTable() {
		String qry = "DELETE FROM conversions WHERE 1";
		//System.out.println(qry);
		myDataBase.execSQL(qry);
	}
}
