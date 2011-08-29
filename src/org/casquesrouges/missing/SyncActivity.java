package org.casquesrouges.missing;

/*
 * 
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SyncActivity extends Activity {

	/*
	 * THIS NEEDS TO BE MODIFIED FOR EVERY NEW DISASTER.
	 * FOR THE FIRST DISASTER THE _SERVER_ NEEDS TO POINT
	 * TO THE PROD.
	 * It was by request of Casques Rouges that the user
	 * cannot choose the disaster.
	 * */
	String DISASTER = "irene";
	String SERVER	= "http://www.missing.net";
	/* ------ END NEW DISASTER ------*/
	
	
	String TAG = "MISSING";
	
	public static final String PREFS_NAME = "MISSING prefs";
	public static final String PREFS_LOGIN = "login";
	public static final String PREFS_PASS = "pass";
	private SharedPreferences mPrefs;
	
	private MissingDbAdapter mDbAdapter;
	private TextView textView;
	private EditText editText01, editText02;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.syncstarter);
		textView = (TextView) findViewById(R.id.TextView04);
		editText01 = (EditText) findViewById(R.id.EditText01);
		editText02 = (EditText) findViewById(R.id.EditText02);
		
		mPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		editText01.setText(mPrefs.getString(PREFS_LOGIN,""));
		editText02.setText(mPrefs.getString(PREFS_PASS,""));
		
		editText01.addTextChangedListener(new TextWatcher(){
			@Override        
			public void afterTextChanged(Editable s) {
				final SharedPreferences.Editor edit = mPrefs.edit();
				edit.putString(PREFS_LOGIN, editText01.getText().toString());
				edit.commit();
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count){}
		}); 

		editText02.addTextChangedListener(new TextWatcher(){
			@Override        
			public void afterTextChanged(Editable s) {
				final SharedPreferences.Editor edit = mPrefs.edit();
				edit.putString(PREFS_PASS, editText02.getText().toString());
				edit.commit();
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count){}
		}); 
		
	        mDbAdapter = new MissingDbAdapter(this);
	        mDbAdapter.open();
	}
    
	public void onClickHandler(View view) {
		switch (view.getId()) {
		case R.id.Button01:
						
			textView.setText("Synchronising Data with Server");
			
			startSyncUpload(-1, -1, null);
			break;

		}
	}
	
	public void startSyncUpload(int rowID, int personID, String picFile){
		
		if(rowID != -1) {
			
			mDbAdapter.updatePersonId(rowID, personID);
			
			if(picFile.length() > 1)
				getPhotoUploadUrl(personID, picFile);
			
			startSyncUpload(-1,-1,null);
		}
		else {
			Cursor cursor = mDbAdapter.fetchSyncPerson();
			String picUrl;
			
			if(cursor != null) {
				startManagingCursor(cursor);
				
				try {
					picUrl = cursor.getString(cursor.getColumnIndex(MissingDbAdapter.COL_PICURL));
				} catch (Exception e) {
					Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
					picUrl = "";
				}
	
				
				List<NameValuePair> httpPostData = new ArrayList<NameValuePair>(2);  
				httpPostData.add(new BasicNameValuePair(MissingDbAdapter.COL_SEX,
						cursor.getString(cursor.getColumnIndex(MissingDbAdapter.COL_SEX))));  
				httpPostData.add(new BasicNameValuePair(MissingDbAdapter.COL_FIRSTNAME,
						cursor.getString(cursor.getColumnIndex(MissingDbAdapter.COL_FIRSTNAME))));  
				httpPostData.add(new BasicNameValuePair(MissingDbAdapter.COL_LASTNAME,
						cursor.getString(cursor.getColumnIndex(MissingDbAdapter.COL_LASTNAME))));  
				httpPostData.add(new BasicNameValuePair(MissingDbAdapter.COL_AGE,
						cursor.getString(cursor.getColumnIndex(MissingDbAdapter.COL_AGE))));
				httpPostData.add(new BasicNameValuePair(MissingDbAdapter.COL_ADDRESS,
						cursor.getString(cursor.getColumnIndex(MissingDbAdapter.COL_ADDRESS))));  
				httpPostData.add(new BasicNameValuePair(MissingDbAdapter.COL_CITY,
						cursor.getString(cursor.getColumnIndex(MissingDbAdapter.COL_CITY))));  
				httpPostData.add(new BasicNameValuePair(MissingDbAdapter.COL_COUNTRY,
						cursor.getString(cursor.getColumnIndex(MissingDbAdapter.COL_COUNTRY))));  
				httpPostData.add(new BasicNameValuePair(MissingDbAdapter.COL_DESC,
						cursor.getString(cursor.getColumnIndex(MissingDbAdapter.COL_DESC))));  
				httpPostData.add(new BasicNameValuePair(MissingDbAdapter.COL_STATUS,
						cursor.getString(cursor.getColumnIndex(MissingDbAdapter.COL_STATUS))));  
				
				String url = SERVER + "/api/disasters/" + DISASTER;
				
				//Toast.makeText(this, "sqlite rowID: " + cursor.getString(cursor.getColumnIndex(MissingDbAdapter.COL_ROWID)), Toast.LENGTH_LONG).show();	
				
				postPersonHttpRequest(httpPostData, url, cursor.getInt(cursor.getColumnIndex(MissingDbAdapter.COL_ROWID)), picUrl);
			}
			else
				startSyncDownload();
		}
	}
	
	public void getPhotoUploadUrl(int personID, String picFile) {
		String url = SERVER + "/api/disasters/" + DISASTER + "/" + personID + "/photo";
		getPersonPhotoUrlHttpRequest(url, personID, picFile);
		Log.d(TAG, "getPhotoUploadUrl(): " + url +", " + personID +", "+ picFile);
	}
	
	public void startPhotoUpload(int personID, String picFile, String uploadUrl, String creator_id) {
		
		Log.d(TAG,"startPhotoUpload(): " + personID + " " + picFile);
		Log.d(TAG,"startPhotoUpload(): " + uploadUrl + " creator: " + creator_id);
		
		postPhotoHttpRequest(uploadUrl, picFile, personID, creator_id);
	}
	
	public void startSyncDownload(){
		// For future use
		textView.setText("Success");
	}
	
	public void postPersonHttpRequest(List<NameValuePair> data, String url, int rowID, String picUrl) {

		final int _rowID = rowID;
		final String _picUrl = picUrl;
		// define the handler
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				switch (message.what) {
				case HttpAdapter.START: {
					//textView.setText("Starting connection...");
					break;
				}
				case HttpAdapter.SUCCESS: {
					String response = (String) message.obj;
					//textView.setText(response);
					
					int personID = -1;
					try {
						JSONObject json = new JSONObject(response);
						personID = json.getInt("id");
						Log.d("MISSING",json.toString());
					} catch (Exception e) {
						Toast.makeText(SyncActivity.this, e.toString(), Toast.LENGTH_LONG).show();
					}
					textView.setText("Uploaded record " + _rowID);
					startSyncUpload(_rowID, personID, _picUrl);
					break;
				}
				case HttpAdapter.HTTPERROR: {
					String response = (String) message.obj;
					textView.setText(response);
					
					int statusCode = Integer.parseInt(response);
					
					if(statusCode == 401)
						textView.setText("Permission denied. Please check Login and Password");
					else
						textView.setText("An error occurred. ("+statusCode+")" );
					break;
					
				}
				case HttpAdapter.ERROR: {
					Exception e = (Exception) message.obj;
					e.printStackTrace();
					textView.setText("Connection failed.");
					break;
				}
				}
			}
		};
		
		new HttpAdapter(handler, this).post(url,data);
	}
	
	public void getPersonPhotoUrlHttpRequest(String url, int personID, String picUrl) {
		
		final int _personID = personID;
		final String _picUrl = picUrl;

		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				switch (message.what) {
				case HttpAdapter.START: {
					break;
				}
				case HttpAdapter.SUCCESS: {
					String response = (String) message.obj;
					
					String picUploadUrl = null;
					String creator_id = null;
					try {
						JSONObject json = new JSONObject(response);
						picUploadUrl = json.getString("form");
						creator_id = json.getString("creator_id");
					} catch (Exception e) {
						Toast.makeText(SyncActivity.this, e.toString(), Toast.LENGTH_LONG).show();
					}
					
					startPhotoUpload(_personID, _picUrl, picUploadUrl, creator_id);
					break;
				}
				case HttpAdapter.ERROR: {
					Exception e = (Exception) message.obj;
					Log.d(TAG, e.toString());
					textView.setText("Connection failed.");
					break;
				}
				}
			}
		};
		
		new HttpAdapter(handler, this).get(url);
	}

	public void postPhotoHttpRequest(String url, String picFile, int personID, String creator_id) {
		
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				switch (message.what) {
				case HttpAdapter.START: {
					break;
				}
				case HttpAdapter.SUCCESS: {
					//String response = (String) message.obj;
					
					Log.d(TAG, "picture successfull uploaded");
					break;
				}
				case HttpAdapter.ERROR: {
					Exception e = (Exception) message.obj;

					Log.d(TAG, e.toString());
					break;
				}
				}
			}
		};
		
		new HttpAdapter(handler, this).file(url, picFile, personID, creator_id);
	}
	
}