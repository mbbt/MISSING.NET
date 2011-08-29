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

/**
 * @author m
 *
 */
public class AddPersonDoneActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.addpersondone);
	}
	
	public void onClickMenu(View view) {
		Intent i = new Intent(this, MenuActivity.class);
		i.putExtra("Value1", "This value one for ActivityTwo ");
		startActivity(i);
	}
	
	public void onClickAdd(View view) {
		Intent i = new Intent(this, AddPersonActivity.class);
		i.putExtra("Value1", "This value one for ActivityTwo ");
		startActivity(i);
	}
}
