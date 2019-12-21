package tmedia.ir.melkeurmia;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import es.dmoral.toasty.Toasty;
import tmedia.ir.melkeurmia.tools.CONST;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MapLocationActivity extends AppCompatActivity {
    private MapView mapView;
    private MapboxMap _mapboxMap;
    private LatLng pickedLatLng;
    private ImageView ivLocationPicker;
    private boolean is_add_marker = false;
    private String mode;
    private boolean view_mode;
    private double lat;
    private double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Mapbox Access token
        Mapbox.getInstance(getApplicationContext(), getString(R.string.map_token));

        setContentView(R.layout.activity_map_location);


        ivLocationPicker = new ImageView(this);
        ivLocationPicker.setImageResource(R.drawable.map_marker); //reference to the drawable image



        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                _mapboxMap = mapboxMap;
                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(37.549237, 45.0528744))
                        .zoom(12)
                        .build());

                pickedLatLng = getLatLngFromMarker(); // used to pick lat lng while coming to screen
                mapboxMap.addOnCameraIdleListener(() -> { //listener for on camera idle change
                    pickedLatLng = getLatLngFromMarker();
                });

            }
        });


        if (getIntent().getExtras() != null) {
            JsonParser parser = new JsonParser();
            JsonObject object = parser.parse((String) getIntent().getExtras().get("location")).getAsJsonObject();
            lat = object.get("lat").getAsDouble();
            lng = object.get("lng").getAsDouble();




            mapView.getMapAsync(mapboxMap -> {
                is_add_marker = true;
                _mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(lat, lng))
                        .zoom(12)
                        .build());
                createLocationPickerMarker();
                pickedLatLng = new LatLng(lat, lng);

            });

        }


        Button save_location = findViewById(R.id.save_location);
        save_location.setOnClickListener(v -> {
            send();
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("انتخاب ناحیه");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        if (intent.hasExtra("mode")) {
            mode = intent.getExtras().getString("mode");
            if(mode.equals("view")){
                save_location.setVisibility(View.GONE);
                toolbar.setTitle("نمایش منطقه");
                view_mode = true;
                ivLocationPicker.setVisibility(View.GONE);
                mapView.getMapAsync(mapboxMap -> mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat,lng))
                        .snippet("Illinois")
                ));
            }
        }
    }

    private void send() {
        if (is_add_marker) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("lat", pickedLatLng.getLatitude());
            resultIntent.putExtra("lng", pickedLatLng.getLongitude());

            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Toasty.error(MapLocationActivity.this, "لطفا موقعیت ملک خود را انتخاب کنید", Toast.LENGTH_LONG).show();


        }


    }

    private LatLng getLatLngFromMarker() {
        return _mapboxMap.getProjection().fromScreenLocation(new PointF(ivLocationPicker.getLeft() + (ivLocationPicker.getWidth() / 2), ivLocationPicker.getBottom()));
    }

    public void addMarker() {
        is_add_marker = true;
        createLocationPickerMarker();
    }

    private void createLocationPickerMarker() {
        //Here image view is dynamically created

        // Statically Set drop pin in center of screen
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER); //image parameters are sent and set at the center
        float density = getResources().getDisplayMetrics().density; //get screen density
        params.bottomMargin = (int) (12 * density); //inorder to place bottom tip at the center bottom margin added - here 12 is multiplied with the screen density and added as bottom margin as this will be mostly at the centre (based on average value of all density location_picker image height)
        ivLocationPicker.setLayoutParams(params); //parameters are set to the image
        mapView.addView(ivLocationPicker); //image is added to the map
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(!view_mode){
            getMenuInflater().inflate(R.menu.map_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.map_action:
                send();
                return true;
            case R.id.map_add_action:
                addMarker();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
