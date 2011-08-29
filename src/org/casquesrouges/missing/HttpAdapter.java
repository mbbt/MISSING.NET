/* Copyright (C) 2011 Bearstech
 * License: Apache License, version 2.0
 * Code is based on a tutorial from Greg Zavitz, http://masl.cis.gvsu.edu
 */

package org.casquesrouges.missing;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


/**
 * Asynchronous HTTP connections
 * 
 */
public class HttpAdapter implements Runnable {

	public static final int START   = 0;
	public static final int ERROR   = 1;
	public static final int SUCCESS = 2;
	public static final int HTTPERROR = 3;
	

	private static final int GET  = 0;
	private static final int POST = 1;
	private static final int FILE = 2;

	private String url;
	private int method;
	private Handler handler;
	private Context ctx;
	private List<NameValuePair> data;
	private int personID;
	private String picFile;
	private String creator_id;
	private SharedPreferences mPrefs;
	private String login = "";
	private String pass = "";

	private DefaultHttpClient httpClient;

	// ==============
	// Constructors
	// ==============
	public HttpAdapter() {
		this(new Handler(), null);
	}

	public HttpAdapter(Handler _handler, Context _ctx) {
		handler = _handler;
		if(_ctx!=null) {
			ctx = _ctx;
			mPrefs = ctx.getSharedPreferences(SyncActivity.PREFS_NAME, 0);
			login = mPrefs.getString(SyncActivity.PREFS_LOGIN, "");
			pass  = mPrefs.getString(SyncActivity.PREFS_PASS, "");
		}
	}

	// ==============
	// Public methods
	// ==============
	public void get(String url) {
		create(GET, url, null, null, 0, null);
	}

	public void post(String url, List<NameValuePair> data) {
		create(POST, url, data, null, 0, null);
	}
	
	public void file(String url, String picFile, int personID, String creator_id){
		create(FILE, url, null, picFile, personID, creator_id);
	}
	
	// ===============
	// Helper methods 
	// ===============
	public void create(int method, String url, List<NameValuePair> data, String picFile, int personID, String creator_id) {
		this.method = method;
		this.url = url;
		this.data = data;
		this.picFile = picFile;
		this.personID = personID;
		this.creator_id = creator_id;
		ConnectionManager.getInstance().push(this);
	}


	@Override
	public void run() {
		handler.sendMessage(Message.obtain(handler, HttpAdapter.START));
		httpClient = new DefaultHttpClient();
		
		httpClient.getCredentialsProvider().setCredentials(
		                new AuthScope(null, 80), 
		                new UsernamePasswordCredentials(login, pass));
		
		
		HttpConnectionParams.setSoTimeout(httpClient.getParams(), 25000);
		try {
			HttpResponse response = null;
			switch (method) {
			case GET:
				response = httpClient.execute(new HttpGet(url));
				break;
			case POST:
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(data));
				response = httpClient.execute(httpPost);
				break;
			case FILE:
				File input = new File(picFile);

				HttpPost post = new HttpPost(url);
				MultipartEntity multi = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

				multi.addPart("person_id",  new StringBody(Integer.toString(personID)));
				multi.addPart("creator_id", new StringBody(creator_id));
				multi.addPart("file", new FileBody(input));
				post.setEntity(multi);

				Log.d("MISSING", "http FILE: " + url + " pic: " + picFile);
				
				response = httpClient.execute(post);
				
				break;
			}

			processEntity(response);

		} catch (Exception e) {
			handler.sendMessage(Message.obtain(handler,
					HttpAdapter.ERROR, e));
		}
		ConnectionManager.getInstance().didComplete(this);
	}

	private void processEntity(HttpResponse response) throws IllegalStateException,  IOException {
		Message message;
		int statusCode = response.getStatusLine().getStatusCode();
		Log.d("MISSING", "status: " + statusCode);
		
		if(statusCode == 200) {
			HttpEntity entity = response.getEntity();
			BufferedReader br = new BufferedReader( new InputStreamReader( entity.getContent() ));
			String line, result = "";
			while ((line = br.readLine()) != null)
				result += line;
			message = Message.obtain(handler, SUCCESS, result);
		}
		else {
			Integer intStatusCode = new Integer(statusCode);
			message = Message.obtain(handler, HTTPERROR, intStatusCode.toString());
		}
		handler.sendMessage(message);
	}


	public static class ConnectionManager {
		
		public static final int MAX_CONNECTIONS = 5;
	
		private ArrayList<Runnable> active = new ArrayList<Runnable>();
		private ArrayList<Runnable> queue = new ArrayList<Runnable>();
	
		private static ConnectionManager instance;
	
		public static ConnectionManager getInstance() {
			if (instance == null)
				instance = new ConnectionManager();
			return instance;
		}
	
		public void push(Runnable runnable) {
			queue.add(runnable);
			if (active.size() < MAX_CONNECTIONS)
				startNext();
		}
	
		private void startNext() {
			if (!queue.isEmpty()) {
				Runnable next = queue.get(0);
				queue.remove(0);
				active.add(next);
	
				Thread thread = new Thread(next);
				thread.start();
			}
		}
	
		public void didComplete(Runnable runnable) {
			active.remove(runnable);
			startNext();
		}
	
	}


}

