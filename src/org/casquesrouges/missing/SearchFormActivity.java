/**
 * 
 */
package org.casquesrouges.missing;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

/**
 * (C) Bearstech 2011
 * @author Marcus Bauer
 *
 */
public class SearchFormActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.searchform);
	}
	
	public void onClick(View view) {
		EditText edittext = (EditText) findViewById(R.id.EditText01);
		String string  = edittext.getText().toString();
		
		Intent i = new Intent(this, SearchPersonActivity.class);
		i.putExtra("SEARCH", string);
		startActivity(i);
	}
}
