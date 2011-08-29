/*
* Copyright (C) 2008 Google Inc.
* Copyright (C) 2011 Bearstech, Author: Marcus Bauer
* 
* Licensed under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License. You may obtain a copy of
* the License at
* 
* http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations under
* the License.
*/

package org.casquesrouges.missing;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
* Simple database access helper class. Defines the basic CRUD operations
* for the Missing Database.
*/
public class MissingDbAdapter {

	private static final String	DB_NAME	 = "missing.db";
	private static final String	DB_TABLE = "persons";
	// THE VERSION NEEDS TO BE BUMPED FOR EVERY NEW DISASTER
	// IN ORDER TO CLEAR THE DATABASE
	private static final int	DB_VERSION = 17;
	
	// the following declarations are for javadoc
	public static final String COL_ROWID	 = "_id";
	public static final String COL_TIMESTAMP = "timestamp";
	public static final String COL_SYNCED	 = "synced";
	
	public static final String COL_DISASTER	 = "disaster";
	public static final String COL_PERSONID	 = "id";
	public static final String COL_SEX	 = "sex";
	public static final String COL_FIRSTNAME = "first_name";
	public static final String COL_LASTNAME	 = "last_name";
	public static final String COL_AGE	 = "age";
	public static final String COL_ADDRESS	 = "address";
	public static final String COL_CITY	 = "city";
	public static final String COL_COUNTRY	 = "country";
	public static final String COL_DESC	 = "other_characteristic_features";
	public static final String COL_STATUS	 = "status";
	public static final String COL_PICURL	 = "picture_url";

	
	private static final String TAG = "MissingDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	/**
	* Database creation sql statement
	*/
	private static final String DB_SQL_CREATE =
		"CREATE TABLE persons (" +
			COL_ROWID	+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			COL_TIMESTAMP	+ " DATETIME DEFAULT CURRENT_TIMESTAMP," + 
			COL_SYNCED	+ " BOOLEAN DEFAULT false," + 
			COL_DISASTER	+ " TEXT," +
			COL_PERSONID	+ " INTEGER," +
			COL_SEX		+ " TEXT," +
			COL_FIRSTNAME	+ " TEXT," +
			COL_LASTNAME	+ " TEXT COLLATE NOCASE," +
			COL_AGE		+ " INTEGER," +
			COL_ADDRESS	+ " TEXT," +
			COL_CITY	+ " TEXT," +
			COL_COUNTRY	+ " TEXT," +
			COL_DESC	+ " TEXT," +
			COL_STATUS	+ " TEXT," +
			COL_PICURL	+ " TEXT);";
	
	
	private final Context mCtx;
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
	
		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_SQL_CREATE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which does destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
			onCreate(db);
		}
	}
	
	/**
	* Constructor - takes the context to allow the database to be
	* opened/created
	* 
	* @param ctx the Context within which to work
	*/
	public MissingDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}
	
	/**
	* Open the database. If it cannot be opened, try to create a new
	* instance of the database. If it cannot be created, throw an exception to
	* signal the failure
	* 
	* @return this (self reference, allowing this to be chained in an
	*         initialization call)
	* @throws SQLException if the database could be neither opened or created
	*/
	public MissingDbAdapter open() throws SQLException
	{	
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();

		return this;
	}
	
	public void close() {
		mDbHelper.close();
	}
	
	
	/**
	* Create a new record using the data provided. If the record is
	* successfully created return the new rowId for that record, otherwise return
	* a -1 to indicate failure.
	* 
	* @param title the title of the record
	* @param body the body of the record
	* @return rowId or -1 if failed
	*/
	public long createPerson(ContentValues data) {

		try {
			return mDb.insertOrThrow(DB_TABLE, null, data);
		} catch (Exception e) {
			Toast.makeText(mCtx, "create person ERROR " + e.toString(), Toast.LENGTH_LONG).show();
			Log.d("MISSING", e.toString());
			return -1;
		}
	}
	
	/**
	* Delete the record with the given rowId
	* 
	* @param rowId id of record to delete
	* @return true if deleted, false otherwise
	*/
	public boolean deletePerson(long rowId) {
	
		return mDb.delete(DB_TABLE, COL_ROWID + "=" + rowId, null) > 0;
	}
	
	/**
	* Return a Cursor over the list of all records in the database
	* 
	* @return Cursor over all records
	*/
	public Cursor fetchAllPersons() {
	
		return mDb.query(DB_TABLE, null, //new String[] {COL_ROWID, COL_LASTNAME}, 
				null, null, null, null, COL_LASTNAME);
	}
	
	/**
	* Return a Cursor over the list of all records in the database
	* 
	* @return Cursor over all records
	*/
	public Cursor fetchPersonsByLastname(String search) {
	
		return mDb.query(DB_TABLE, null, 
				COL_LASTNAME + " LIKE '" + search + "%'", null, null, null, COL_LASTNAME);
	}
	
	/**
	* Return a Cursor positioned at the record that matches the given rowId
	* 
	* @param rowId id of record to retrieve
	* @return Cursor positioned to matching record, if found
	* @throws SQLException if record could not be found/retrieved
	*/
	public Cursor fetchPerson(long rowId) throws SQLException {
	
		Cursor mCursor = mDb.query(true, DB_TABLE, 
					null, //new String[] {COL_ROWID, COL_LASTNAME}, 
					COL_ROWID + "=" + rowId, 
					null, null, null, null, null);
			if (mCursor != null) {
				mCursor.moveToFirst();
			}
		return mCursor;
	
	}
	
	public Cursor fetchSyncPerson() throws SQLException {
		
		Cursor mCursor = mDb.query(true, DB_TABLE, 
					null,
					COL_PERSONID + " isnull", 
					null, null, null, null, null);
		if (mCursor != null) {
			try {
				boolean result = mCursor.moveToFirst();
				if(!result) { 
					mCursor = null;
				}
				
			} catch (Exception e) {
				Toast.makeText(mCtx, e.toString(), Toast.LENGTH_LONG).show();
				mCursor = null;

			}
		}
		return mCursor;
	
	}
	
	/**
	* Update the record using the details provided. The record to be updated is
	* specified using the rowId, and it is altered to use the title and body
	* values passed in
	* 
	* @param rowId id of record to update
	* @param title value to set record title to
	* @param body value to set record body to
	* @return true if the record was successfully updated, false otherwise
	*/
	public boolean updatePerson(long rowId, String title, String body) {
		ContentValues args = new ContentValues();
		args.put(COL_LASTNAME, title);
		args.put(COL_SEX, body);
		
		return mDb.update(DB_TABLE, args, COL_ROWID + "=" + rowId, null) > 0;
	}
	
	public boolean resetPersons() {
		ContentValues args = new ContentValues();
		args.putNull(COL_PERSONID);
		
		int affectedRows = mDb.update(DB_TABLE, args, null, null);
		Toast.makeText(mCtx, "Rows resetted: " + affectedRows, Toast.LENGTH_LONG).show();
		return true;
	}
	
	public boolean updatePersonId(long rowId, long personId) {
		ContentValues args = new ContentValues();
		args.put(COL_PERSONID, personId);
		
		return mDb.update(DB_TABLE, args, COL_ROWID + "=" + rowId, null) > 0;
	}

}

