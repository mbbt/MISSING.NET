/**
 * 
 */
package org.casquesrouges.missing;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.MyLocationOverlay;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;


	    public class OsmActivity extends Activity {

	    	// ===========================================================
	    	// Constants
	    	// ===========================================================
		public static final String PREFS_SCROLL_X = "scrollX";
		public static final String PREFS_SCROLL_Y = "scrollY";
		public static final String PREFS_ZOOM_LEVEL = "zoomLevel";
		
	    	// ===========================================================
	    	// Fields
	    	// ===========================================================

	    	private MapView mOsmv;
		private MyLocationOverlay mLocationOverlay;
		private ResourceProxy mResourceProxy;
		private SharedPreferences mPrefs;


	    	// ===========================================================
	    	// Constructors
	    	// ===========================================================
	    	/** Called when the activity is first created. */
	    	@Override
	    	public void onCreate(final Bundle savedInstanceState) {
	    		super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);

			mResourceProxy = new ResourceProxyImpl(getApplicationContext());

	    		final RelativeLayout rl = new RelativeLayout(this);
	    		mPrefs = getSharedPreferences(SyncActivity.PREFS_NAME, MODE_PRIVATE);


	    		this.mOsmv = new MapView(this, 256);
	    		rl.addView(this.mOsmv, new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
	    				LayoutParams.FILL_PARENT));
	    		this.mOsmv.setBuiltInZoomControls(true);
	                this.mOsmv.setMultiTouchControls(true);

	    		
			/*
			 * Location Overlay
			 */
	    		Context ctx = null;
	    		try {

	    		ctx = this.getBaseContext();

		
	    		this.mLocationOverlay = new MyLocationOverlay(ctx, this.mOsmv,
					mResourceProxy);

			this.mOsmv.getOverlays().add(this.mLocationOverlay);
			} catch (Exception e) {
				Toast.makeText(this, "overlay" + e.toString(), Toast.LENGTH_SHORT).show();
			}

	    		/* MiniMap */
	    		{
	    			MinimapOverlay miniMapOverlay = new MinimapOverlay(this,
	    					mOsmv.getTileRequestCompleteHandler());
	    			this.mOsmv.getOverlays().add(miniMapOverlay);
	    		}

	    		this.setContentView(rl);

			mOsmv.getController().setZoom(mPrefs.getInt(PREFS_ZOOM_LEVEL, 1));
			mOsmv.scrollTo(mPrefs.getInt(PREFS_SCROLL_X, 0), mPrefs.getInt(PREFS_SCROLL_Y, 0));


	    	}


	    	// ===========================================================
	    	// Methods from SuperClass/Interfaces
	    	// ===========================================================
		@Override
		protected void onResume() {
			
			try {
				this.mLocationOverlay.enableMyLocation();
				mOsmv.setTileSource(TileSourceFactory.getTileSource(1));
				} catch (Exception e) {
					Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
				}
				
			super.onResume();
		}
		
		@Override
		protected void onPause() {
						
			final SharedPreferences.Editor edit = mPrefs.edit();

			edit.putInt(PREFS_SCROLL_X, mOsmv.getScrollX());
			edit.putInt(PREFS_SCROLL_Y, mOsmv.getScrollY());
			edit.putInt(PREFS_ZOOM_LEVEL, mOsmv.getZoomLevel());
			edit.commit();
			
			try {
				this.mLocationOverlay.disableMyLocation();
			} catch (Exception unused) {
			}

			super.onPause();
		}

}

	    
	    
	    
	    

