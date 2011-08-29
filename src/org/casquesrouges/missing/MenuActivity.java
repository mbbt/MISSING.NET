package org.casquesrouges.missing;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/** Activity for displaying the main menu in form of a List */
public class MenuActivity extends ListActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) 
	{	
		super.onCreate(icicle);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// menu entries
		String[] menuEntries = new String[] { getText(R.string.menu_add).toString(), 
				getText(R.string.menu_search).toString(),
				getText(R.string.menu_map).toString(),
				getText(R.string.menu_sync).toString(),
				getText(R.string.menu_info).toString()};
		this.setListAdapter(new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, menuEntries));
		

	}

	/** ListItem click Handler */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		Object itemObject = this.getListAdapter().getItem(position);
		
		
		Intent i;
		switch (position) {
		case 0:
			i = new Intent(MenuActivity.this, AddPersonActivity.class);
			startActivity(i);
			break;
		case 1:
			i = new Intent(MenuActivity.this, SearchFormActivity.class);
			startActivity(i);
			break;
		case 2:
			i = new Intent(MenuActivity.this, OsmActivity.class);
			startActivity(i);
			break;
		case 3:
			i = new Intent(MenuActivity.this, SyncActivity.class);
			startActivity(i);
			break;
		case 4:
			i = new Intent(MenuActivity.this, InformationActivity.class);

			startActivity(i);
			break;
		case 5:
			i = new Intent(MenuActivity.this, ImageViewActivity.class);
			startActivity(i);
			break;
		}
	}
}