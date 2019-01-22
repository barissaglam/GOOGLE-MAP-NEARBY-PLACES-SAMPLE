package saglam.baris.googlenearby;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saglam.baris.googlenearby.R;
import saglam.baris.googlenearby.api.ApiClient;
import saglam.baris.googlenearby.api.ApiService;
import saglam.baris.googlenearby.model.NearbyModel;
import saglam.baris.googlenearby.other.CustomInfoWindowAdapter;
import saglam.baris.googlenearby.other.MarkerClusterRenderer;
import saglam.baris.googlenearby.other.MyClusterItem;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private FusedLocationProviderClient client;

    private ClusterManager<MyClusterItem> mClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        requestPermission();

        client = LocationServices.getFusedLocationProviderClient(this);

        GoogleMapOptions options = new GoogleMapOptions();
        options.zoomControlsEnabled(true);
        options.compassEnabled(true);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .replace(R.id.map, mapFragment);
        ft.commit();
        mapFragment.getMapAsync(this);



    }

    private void setUpClusterer() {
        mClusterManager = new ClusterManager<MyClusterItem>(this, mMap);
        mClusterManager.setRenderer(new MarkerClusterRenderer(this, mMap, mClusterManager));
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyClusterItem>() {
            @Override
            public boolean onClusterClick(Cluster<MyClusterItem> cluster) {
                LatLngBounds.Builder builder = LatLngBounds.builder();
                for (ClusterItem item : cluster.getItems()) {
                    builder.include(item.getPosition());
                }
                final LatLngBounds bounds = builder.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                return true;
            }
        });

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        getMyLocation();
    }

    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                getResult(location, 1500, "cafe");

            }
        });
    }

    private void addMarkerMyLocation(){

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));

        setUpClusterer();
    }


    private void getResult(final Location location, int radius, String type) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<NearbyModel> getNearbyPlaces = apiService.getNearbyPlaces(location.getLatitude()+","+location.getLongitude(), radius, type);

        getNearbyPlaces.enqueue(new Callback<NearbyModel>() {
            @Override
            public void onResponse(Call<NearbyModel> call, Response<NearbyModel> response) {
                Log.d("onResponse", response.body().getStatus());
                try {
                    mMap.clear();
                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    MarkerOptions myMarkerOpt = new MarkerOptions();
                    myMarkerOpt.title("My Location");
                    myMarkerOpt.snippet("TDG Bilişim");
                    myMarkerOpt.icon(BitmapDescriptorFactory.
                            fromResource(R.drawable  .marker_my));
                    myMarkerOpt.position(latLng);
                    mMap.addMarker(myMarkerOpt);
                    // This loop will go through all the results and add marker on each location.
                    for (int i = 0; i < response.body().getResults().size(); i++) {
                        Double lat = response.body().getResults().get(i).getGeometry().getLocation().getLat();
                        Double lng = response.body().getResults().get(i).getGeometry().getLocation().getLng();

                        String placeName = response.body().getResults().get(i).getName();
                        String vicinity = response.body().getResults().get(i).getVicinity();
                        /*markerOptions.position(latLng);
                        markerOptions.title(placeName);
                        markerOptions.snippet(vicinity);

                        mMap.addMarker(markerOptions);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));*/
                        MyClusterItem infoWindowItem = new MyClusterItem(lat, lng, placeName, vicinity);
                        mClusterManager.addItem(infoWindowItem);
                        mClusterManager.cluster();
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                        Location l = new Location("");
                        l.setLatitude(lat);
                        l.setLongitude(lng);

                        Log.i("MESAFE",location.distanceTo(l)/1000+" km\n");
                    }
                } catch (Exception e) {
                    Log.d("onResponse", e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<NearbyModel> call, Throwable t) {
                Log.d("onResponse", t.getMessage());
            }
        });
    }
}
