package boom.realmaps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends
        FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SliderLayout sliderShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sliderShow = (SliderLayout) findViewById(R.id.slider);
        sliderIntro();
    }

    public void onClickUpload(View view) {
        Button uploadAct = (Button) view;
        Intent myIntent = new Intent(this, Upload.class);
        startActivity(myIntent);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
       /*
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */
    }

    public void onSearch(View v) {
        EditText address = (EditText) findViewById(R.id.locationAddress);
        String add = address.getText().toString();
        List<Address> addressList = null;

        if(!add.isEmpty()) {
            Geocoder geo = new Geocoder(this);
            if (add != null || !add.equals("")) {
                try {
                    addressList = geo.getFromLocationName(add, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Address address1 = addressList.get(0);

                LatLng lat = new LatLng(address1.getLatitude(), address1.getLongitude());
                mMap.addMarker(new MarkerOptions().position(lat).title("Marker"));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(lat));
                Toast.makeText(getBaseContext(), "Found Location!", Toast.LENGTH_LONG).show();
                slider();
                //mMap.animateCamera(CameraUpdateFactory.newLatLng(lat));
            }
        } else {
            //error popup message
            Toast.makeText(getBaseContext(), "Please enter something", Toast.LENGTH_LONG).show();
        }
    }

    public void slider() {
        TextSliderView textSliderView = new TextSliderView(this);
        textSliderView
                .description("Game of Thrones")
                .image("http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");

        sliderShow.addSlider(textSliderView);
    }

    public void sliderIntro() {
        TextSliderView textSliderView = new TextSliderView(this);
        textSliderView
                .description("Travel")
                .image("http://www.exclusivegrouptravel.com/Careers/Beachchairs.jpg");

        sliderShow.addSlider(textSliderView);
    }


    @Override
    protected void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();
    }
}
