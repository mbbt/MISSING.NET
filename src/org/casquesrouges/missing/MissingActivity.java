package org.casquesrouges.missing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/** MAIN activity of the application, displaying a login screen */
public class MissingActivity extends Activity {
	
	private EditText text;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		text = (EditText) findViewById(R.id.EditText01);
	}

	/** Handle the login, starting the main menu MenuActivity on success  */
	public void clickHandler(View view) 
	{
		switch (view.getId()) {
		case R.id.Button01:
						
			if (text.getText().length() == 10) {
				Toast.makeText(this, "Please enter a valid login",
						Toast.LENGTH_LONG).show();
				return;
			}
			
			Intent i = new Intent(MissingActivity.this, MenuActivity.class);
			startActivity(i);

			break;
		}
	}
}