package ro.pub.cs.systems.eim.practicaltest02v9;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // This method will be triggered once the map is ready.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set up the map with a marker at Ghelmegioaia, Romania
        LatLng ghelmegioaia = new LatLng(44.7768, 23.3964);  // Coordinates of Ghelmegioaia, Romania
        mMap.addMarker(new MarkerOptions().position(ghelmegioaia).title("Marker in Ghelmegioaia"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ghelmegioaia, 12));  // Zoom level can be adjusted
    }
}
