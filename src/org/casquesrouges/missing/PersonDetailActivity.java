/**
 * 
 */
package org.casquesrouges.missing;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Marcus Bauer, Bearstech 2011
 *
 */
public class PersonDetailActivity extends Activity {
	private MissingDbAdapter mDbAdapter;
	private long mRowId;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.persondetail);
		
		Intent intent = getIntent();
		mRowId = intent.getLongExtra("ROWID", -1);
		
	        mDbAdapter = new MissingDbAdapter(this);
	        mDbAdapter.open();

	        populateFields();
	}
	
	@Override
	public void onPause() {
		
		super.onPause();
		
		ImageView imageview = (ImageView) findViewById(R.id.ImageView01);
		imageview.setVisibility(ImageView.GONE);
	}
	
	
	private void populateFields() {
		TextView textview;
		if (mRowId != -1) {
			Cursor cursor = mDbAdapter.fetchPerson(mRowId);
			startManagingCursor(cursor);
			
			
			try {
				String picUri = cursor.getString(cursor.getColumnIndex(MissingDbAdapter.COL_PICURL));
				Bitmap bitmap = BitmapFactory.decodeFile(picUri);
				ImageView imageview = (ImageView) findViewById(R.id.ImageView01);
				imageview.setImageBitmap(bitmap);
				if (imageview != null)
					imageview.setVisibility(ImageView.VISIBLE);
			} catch (Exception e) {
				// do nothing
			}

			textview = (TextView) findViewById(R.id.TextView03);
			textview.setText(cursor.getString(cursor.getColumnIndex(MissingDbAdapter.COL_LASTNAME)));
			

			textview = (TextView) findViewById(R.id.TextView05);
			textview.setText(cursor.getString(cursor.getColumnIndex(MissingDbAdapter.COL_FIRSTNAME)));			

			
			textview = (TextView) findViewById(R.id.TextView07);
			textview.setText(cursor.getString(cursor.getColumnIndex(MissingDbAdapter.COL_SEX)));
			
			textview = (TextView) findViewById(R.id.TextView09);
			textview.setText(cursor.getString(cursor.getColumnIndex(MissingDbAdapter.COL_STATUS)));			

			
			textview = (TextView) findViewById(R.id.TextView11);
			textview.setText(cursor.getString(cursor.getColumnIndex(MissingDbAdapter.COL_AGE)));
			
			
			textview = (TextView) findViewById(R.id.TextView13);
			textview.setText(cursor.getString(cursor.getColumnIndex(MissingDbAdapter.COL_ADDRESS)));
			
			
			textview = (TextView) findViewById(R.id.TextView15);
			textview.setText(cursor.getString(cursor.getColumnIndex(MissingDbAdapter.COL_CITY)));
			
			
			textview = (TextView) findViewById(R.id.TextView17);
			textview.setText(cursor.getString(cursor.getColumnIndex(MissingDbAdapter.COL_COUNTRY)));
			
			
			textview = (TextView) findViewById(R.id.TextView19);
			textview.setText(cursor.getString(cursor.getColumnIndex(MissingDbAdapter.COL_DESC)));
		}
	}
}
