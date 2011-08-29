package org.casquesrouges.missing;


import java.io.IOException;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ImageViewActivity extends Activity {
	
	ImageView imageView;
	TextView textView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.geopic_view);
		
		Button button= (Button)findViewById(R.id.getimage_button);
		button.setOnClickListener(getImageListener);
		imageView = (ImageView)findViewById(R.id.image_view);
		textView = (TextView)findViewById(R.id.textview_exif);
		Toast.makeText(this, "opening", Toast.LENGTH_SHORT) .show();
		openFile(Environment.getExternalStorageDirectory().getPath() + "/geophoto.jpg");
	}
	

	View.OnClickListener getImageListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View view) {
			finish();
		}
	};

	void openFile(String uri){
		Bitmap bitmap = BitmapFactory.decodeFile(uri);
		if (bitmap != null)
			imageView.setImageBitmap(bitmap);
		
		try {
			ExifInterface mExifInterface = new ExifInterface(uri);
			String str = mExifInterface.getAttribute("UserComment");
			if (str != null) {
				System.out.println(str);
				textView.setText(str);
			} else {
				mExifInterface.setAttribute("UserComment","2010:12:07 11:20:34, TAG1, TAG2");
				mExifInterface.saveAttributes();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
