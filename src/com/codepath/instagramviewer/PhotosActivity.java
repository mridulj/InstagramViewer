package com.codepath.instagramviewer;

import java.util.ArrayList;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.*;
import org.apache.http.Header;

public class PhotosActivity extends Activity {

	public static final String CLIENT_ID="fb40e758f4d544829c938371447cf17d";
	private ArrayList<InstagramPhoto> photos;
	private InstagramPhotosAdapter aPhotos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photos);
		fetchPopularPhotos();
	}

	private void fetchPopularPhotos(){
		photos = new ArrayList<InstagramPhoto>();
		aPhotos = new InstagramPhotosAdapter(this, photos);
		ListView lvPhotos = (ListView) findViewById(R.id.lvPhotos);
		lvPhotos.setAdapter(aPhotos);
		
		String popularurl = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(popularurl, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				
				JSONArray photosJSON = null;
				try{
					photos.clear();
					photosJSON = response.getJSONArray("data");
					for(int i=0;i < photosJSON.length(); i++){
						JSONObject photoJSON = photosJSON.getJSONObject(i);
						InstagramPhoto photo = new InstagramPhoto();
						photo.username = photoJSON.getJSONObject("user").getString("username");
						if (photoJSON.getJSONObject("caption") != null) {
							photo.caption = photoJSON.getJSONObject("caption").getString("text");
						}
						photo.imageUrl = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
						photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
						photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");
						photos.add(photo);
					}
					aPhotos.notifyDataSetChanged();
				}catch(JSONException e){
					e.printStackTrace();
				}
				
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photos, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
