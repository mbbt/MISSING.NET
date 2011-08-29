/*
* Copyright (C) 2008 Google Inc.
* Copyright (C) 2011 Bearstech
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.casquesrouges.missing;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


/** 
* Simple List of Records.
* 
*/
public class SearchPersonActivity extends ListActivity 
{	
	private MissingDbAdapter mDbHelper;
	String search;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_list);
		
		Intent intent = getIntent();
		search = intent.getStringExtra("SEARCH");
		
		mDbHelper = new MissingDbAdapter(this);
		mDbHelper.open();
		
		fillData();		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, PersonDetailActivity.class);
		i.putExtra("ROWID", id);
		// Activity returns a result if called with startActivityForResult
		Toast.makeText(getApplicationContext(), "onitem - record: " + Integer.toString(position)
				+ " LONG " + Long.toString(id),  Toast.LENGTH_SHORT).show();
		startActivity(i);
	}

	private void fillData() {
		Cursor cursor;
		try {
			if (search.length() == 0)
				cursor = mDbHelper.fetchAllPersons();
			else
				cursor = mDbHelper.fetchPersonsByLastname(search);
			startManagingCursor(cursor);
			
			// the fields to display in the list
			String[] from = new String[]{
					MissingDbAdapter.COL_LASTNAME, 
					MissingDbAdapter.COL_FIRSTNAME, 
					MissingDbAdapter.COL_SEX, 
					MissingDbAdapter.COL_PERSONID,};
			
			// the fields to bind those fields to
			int[] to = new int[]{R.id.text1, R.id.text2, R.id.text3, R.id.text4};
			
			// now a simple cursor adapter and set it to display
			SimpleCursorAdapter records = new SimpleCursorAdapter(
					this, R.layout.record_row, cursor, from, to);
			setListAdapter(records);
		}catch (Exception e) {
			Toast.makeText(this, "error: " + e.toString(), Toast.LENGTH_LONG).show();
		}
	}
}
