package org.casquesrouges.missing;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

public class AddPersonActivity extends Activity {

	private static final int REQUEST_CODE_CAM_ACTIVITY = 22; 
	private MissingDbAdapter mDbAdapter;
	private String imageFile = "";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.add_person);
		
	        mDbAdapter = new MissingDbAdapter(this);
	        mDbAdapter.open();
		
		AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.AutoCompleteTextView01);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.country_list_item, COUNTRIES);
		textView.setAdapter(adapter);

		Log.d("MISSING","add person onCreate()");
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		
		ImageView imageView = (ImageView)findViewById(R.id.image_view);
		
		/* camera activity result */

		if (requestCode == REQUEST_CODE_CAM_ACTIVITY && resultCode == RESULT_OK) {  
			Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
			imageView.setImageBitmap(thumbnail);
			imageView.setVisibility(ImageView.VISIBLE);
			

			try {
				File dir = new File(Environment.getExternalStorageDirectory()+"/cr2/");
				if(!dir.exists()) dir.mkdirs();
				File file = new File(dir, "PIC"+System.currentTimeMillis()+".jpg");

				imageFile = file.getAbsolutePath();
				FileOutputStream out = new FileOutputStream(file);
				thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, out);
				
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(this, "ERROR pic save " + e.toString(), Toast.LENGTH_LONG).show();
			}
		}  else if (requestCode == REQUEST_CODE_CAM_ACTIVITY && resultCode == RESULT_CANCELED) {
			Toast.makeText(this, "No picture was taken", Toast.LENGTH_SHORT).show();
		}
	} 
	
	
	//-------------------------------------------------------------------------
	// UI callbacks
	//-------------------------------------------------------------------------
	public void onClickHandler(View view) {
		switch (view.getId()) {
		case R.id.Button01:
			takePicture();
			break;
		case R.id.Button02:
			addPersonToDatabase();
		}
	}
	

	public void takePicture() {
		
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			Log.d("MISSING", "No SDCARD?");
		else
			startActivityForResult(cameraIntent, REQUEST_CODE_CAM_ACTIVITY);
	}

	
	/**
	 * 
	 */
	public void addPersonToDatabase()
	{
		ContentValues data = new ContentValues();
		EditText editText;
		RadioButton radioButton;
		String string;
		int integer;
		boolean checked;
		
		//--------------------------------------------------------------------
		// evaluate form fields
		//--------------------------------------------------------------------
		
		/* gender */
		radioButton = (RadioButton) findViewById(R.id.RadioButton01);
		checked = radioButton.isChecked();
		if(checked) 
			data.put(MissingDbAdapter.COL_SEX, "male");
		else 
			data.put(MissingDbAdapter.COL_SEX, "female");
		
		/* first name */
		editText = (EditText) findViewById(R.id.EditText01);
		string = editText.getText().toString();
		data.put(MissingDbAdapter.COL_FIRSTNAME, string);
		
		/* last name */
		editText = (EditText) findViewById(R.id.EditText02);
		string  = editText.getText().toString();
		data.put(MissingDbAdapter.COL_LASTNAME, string);
		
		/* day, month, year: date-of-birth --- age */
		editText = (EditText) findViewById(R.id.EditText03);
		try {
			integer = Integer.parseInt( editText.getText().toString() );
		} catch (Exception e) {
			integer = 0;
		}
		data.put(MissingDbAdapter.COL_AGE, integer);
		
		/* address */
		editText = (EditText) findViewById(R.id.EditText06);
		string  = editText.getText().toString();
		data.put(MissingDbAdapter.COL_ADDRESS, string);
		
		/* city */
		editText = (EditText) findViewById(R.id.EditText08);
		string  = editText.getText().toString();
		data.put(MissingDbAdapter.COL_CITY, string);
		
		/* country */
		editText = (EditText) findViewById(R.id.AutoCompleteTextView01);
		string  = editText.getText().toString();
		data.put(MissingDbAdapter.COL_COUNTRY, string);
		
		/* description */
		editText = (EditText) findViewById(R.id.EditText09);
		string  = editText.getText().toString();
		data.put(MissingDbAdapter.COL_DESC, string);
		
		/* status */
		radioButton = (RadioButton) findViewById(R.id.RadioButton03);
		checked = radioButton.isChecked();
		if(checked) 
			data.put(MissingDbAdapter.COL_STATUS, "missing");
		else 
			data.put(MissingDbAdapter.COL_STATUS, "found");
		
		/* picture */
		data.put(MissingDbAdapter.COL_PICURL, imageFile);
		
		//---------------------------------------------------------------------
		

		long res = mDbAdapter.createPerson(data);
		if(res == -1)
			Log.d("MISSING", "ERROR on add person to db");
		else
			Log.d("MISSING","SUCCESS add person to db");
		
		Intent i = new Intent(AddPersonActivity.this, AddPersonDoneActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(i);
		finish();
	}
	
	static final String[] COUNTRIES = new String[] {
		  "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra",
		  "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina",
		  "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan",
		  "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium",
		  "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia",
		  "Bosnia and Herzegovina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory",
		  "British Virgin Islands", "Brunei", "Bulgaria", "Burkina Faso", "Burundi",
		  "Cote d'Ivoire", "Cambodia", "Cameroon", "Canada", "Cape Verde",
		  "Cayman Islands", "Central African Republic", "Chad", "Chile", "China",
		  "Christmas Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo",
		  "Cook Islands", "Costa Rica", "Croatia", "Cuba", "Cyprus", "Czech Republic",
		  "Democratic Republic of the Congo", "Denmark", "Djibouti", "Dominica", "Dominican Republic",
		  "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea",
		  "Estonia", "Ethiopia", "Faeroe Islands", "Falkland Islands", "Fiji", "Finland",
		  "Former Yugoslav Republic of Macedonia", "France", "French Guiana", "French Polynesia",
		  "French Southern Territories", "Gabon", "Georgia", "Germany", "Ghana", "Gibraltar",
		  "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau",
		  "Guyana", "Haiti", "Heard Island and McDonald Islands", "Honduras", "Hong Kong", "Hungary",
		  "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Jamaica",
		  "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kuwait", "Kyrgyzstan", "Laos",
		  "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg",
		  "Macau", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands",
		  "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia", "Moldova",
		  "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia",
		  "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand",
		  "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "North Korea", "Northern Marianas",
		  "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru",
		  "Philippines", "Pitcairn Islands", "Poland", "Portugal", "Puerto Rico", "Qatar",
		  "Reunion", "Romania", "Russia", "Rwanda", "Sqo Tome and Principe", "Saint Helena",
		  "Saint Kitts and Nevis", "Saint Lucia", "Saint Pierre and Miquelon",
		  "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Saudi Arabia", "Senegal",
		  "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands",
		  "Somalia", "South Africa", "South Georgia and the South Sandwich Islands", "South Korea",
		  "Spain", "Sri Lanka", "Sudan", "Suriname", "Svalbard and Jan Mayen", "Swaziland", "Sweden",
		  "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "The Bahamas",
		  "The Gambia", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey",
		  "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Virgin Islands", "Uganda",
		  "Ukraine", "United Arab Emirates", "United Kingdom",
		  "United States", "United States Minor Outlying Islands", "Uruguay", "Uzbekistan",
		  "Vanuatu", "Vatican City", "Venezuela", "Vietnam", "Wallis and Futuna", "Western Sahara",
		  "Yemen", "Yugoslavia", "Zambia", "Zimbabwe"
		};
}
