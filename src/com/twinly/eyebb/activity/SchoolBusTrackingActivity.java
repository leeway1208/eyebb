package com.twinly.eyebb.activity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.twinly.eyebb.R;
import com.twinly.eyebb.utils.DirectionsJSONParser;

/**
 * @author eyebb team
 * 
 * @category SchoolBusTrackingActivity
 * 
 *           this activity shows the lane track or the school bus.(useless
 *           class)
 */
public class SchoolBusTrackingActivity extends FragmentActivity {

	private GoogleMap mMap;
	private ArrayList<LatLng> routePoints;
	private ArrayList<LatLng> displayPoints;
	private PolylineOptions polylineOptions;
	private Polyline polyline;
	private Marker marker;
	private int mProgress;
	private int speed = 5;
	Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_school_bus);
		setTitle(getString(R.string.text_school_bus_tracking));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setUpMapIfNeeded();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setUpMapIfNeeded() {

		if (mMap == null) {
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	private void setUpMap() {
		mMap.setMyLocationEnabled(true);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
				22.3371689, 114.179077), 16));

		marker = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0))
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_home_schoolbus)));

		polylineOptions = new PolylineOptions();
		polyline = mMap.addPolyline(polylineOptions.color(
				getResources().getColor(R.color.red)).width(8));

		getRoutePoints();
		interpolateRoutePoints();

		mMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {

			@Override
			public void onMapLoaded() {
				drawRunner.run();
			}
		});
	}

	Runnable drawRunner = new Runnable() {

		@Override
		public void run() {
			if (mProgress < routePoints.size()) {
				displayPoints.add(routePoints.get(mProgress));
				polyline.setPoints(displayPoints);
				marker.setPosition(routePoints.get(mProgress));

				mHandler.postDelayed(drawRunner, speed);
			}
			mProgress++;
		}
	};

	private void getRoutePoints() {
		JSONObject jObject;
		List<List<HashMap<String, String>>> routes = null;
		routePoints = new ArrayList<LatLng>();
		displayPoints = new ArrayList<LatLng>();

		try {
			jObject = new JSONObject(getFromAssets("RouteData"));
			DirectionsJSONParser parser = new DirectionsJSONParser();

			// Starts parsing data
			routes = parser.parse(jObject);
			// System.out.println("do in background:" + routes);
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<HashMap<String, String>> path = routes.get(0);

		for (int j = 0; j < path.size(); j++) {
			HashMap<String, String> point = path.get(j);
			double lat = Double.parseDouble(point.get("lat"));
			double lng = Double.parseDouble(point.get("lng"));
			LatLng position = new LatLng(lat, lng);
			routePoints.add(position);
		}
	}

	private void interpolateRoutePoints() {
		ArrayList<LatLng> tempRoutePoints = new ArrayList<LatLng>();
		for (int i = 0; i < routePoints.size() - 1; i++) {
			tempRoutePoints.addAll(test(routePoints.get(i),
					routePoints.get(i + 1)));
		}
		tempRoutePoints.add(routePoints.get(routePoints.size() - 1));
		routePoints = tempRoutePoints;
	}

	private ArrayList<LatLng> test(LatLng start, LatLng end) {
		ArrayList<LatLng> result = new ArrayList<LatLng>();
		float[] distance = new float[1];
		Location.distanceBetween(start.latitude, start.longitude, end.latitude,
				end.longitude, distance);
		if (distance[0] >= 5) {
			LatLng middle = new LatLng((start.latitude + end.latitude) / 2,
					(start.longitude + end.longitude) / 2);
			result.addAll(test(start, middle));
			result.addAll(test(middle, end));
		} else {
			result.add(start);
		}
		return result;
	}

	private String getFromAssets(String fileName) {
		String result = "";
		try {
			InputStreamReader inputReader = new InputStreamReader(
					getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			while ((line = bufReader.readLine()) != null) {
				result += line;
				result += "\n";
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
