package com.techclutch.rocket.rocketandroid;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveCanceledListener;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techclutch.rocket.rocketandroid.api.RestService;
import com.techclutch.rocket.rocketandroid.api.model.Location;
import com.techclutch.rocket.rocketandroid.api.model.Locations;
import com.techclutch.rocket.rocketandroid.api.model.ModisLocation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.techclutch.rocket.rocketandroid.R.id.fab_report_fire;
import static com.techclutch.rocket.rocketandroid.util.MapsUtil.getCameraUpdatePosition;
import static com.techclutch.rocket.rocketandroid.util.MapsUtil.getMarker;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        OnCameraMoveStartedListener,
        OnCameraMoveListener,
        OnCameraMoveCanceledListener,
        OnCameraIdleListener {

    @BindView(fab_report_fire)
    FloatingActionButton fabReportFire;

    @BindView(R.id.fab_get_me_out)
    FloatingActionButton fabGetMeOut;

    private String[] colors = {"#7fff7272", "#7f31c7c5", "#7fff8a00"};

    private static final int TAKE_PIC_CODE = 1001;
    private static final int ANIM_DURATION = 1000;
    private static final float DEFAULT_ZOOM = 10.0f;
    private GoogleMap mMap;
    private static final LatLng EVAC_POINT = new LatLng(39.952757, -105.526083);//39.952757, -105.526083
    private static final LatLng DISASTER_POINT = new LatLng(39.989519, -105.476594);//39.989519, -105.476594
    private static final LatLng FIRESTATION_POINT = new LatLng(39.967422, -105.516022);//39.967422, -105.516022
    private RestService restService = new RestService();
    private List<Marker> fireMarkers = new ArrayList<>();
    private List<Marker> evacMarkers = new ArrayList<>();
    private List<Marker> stationMarkers = new ArrayList<>();
    private Locations fireLocation = new Locations();
    private Locations evacLocation = new Locations();
    private Locations stationLocation = new Locations();
    private Locations activeDataLocation = new Locations();

    private Marker myLocationMarker;
    private LatLng myLocation = DISASTER_POINT;

    public enum MarkerType {
        FIRE,
        EVAC,
        STATION,
        MYLOCATION
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);

        mMap.getUiSettings().setZoomGesturesEnabled(true);
        MarkerOptions location = new MarkerOptions().position(myLocation).title("You are here").icon(getMarker(MarkerType.MYLOCATION));
        myLocationMarker = mMap.addMarker(location);
        // zoom values range from 2-21
        mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
        mMap.animateCamera(getCameraUpdatePosition(DISASTER_POINT));
        //testWebService();
        getEvacuationData(myLocation);
//        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
//        getFireData(bounds);
        getFireModisData();
    }

    private void testWebService() {
        //citiesJSON?north=44.1&south=-9.9&east=-22.4&west=55.2&lang=de&username=demo
        Call<Void> results = restService.getFireService().getCities(44.1, 9.9, 22.4, 55.2, "de", "demo");
        results.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("testWebService", response.toString());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("testWebService", t.getMessage());
            }
        });
    }

    @OnClick(fab_report_fire)
    void onReportFireClicked() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivityForResult(intent, TAKE_PIC_CODE);
    }

    // find route from station
    @OnClick(R.id.fab_save_me)
    void onSaveMe() {
        plotRoutes(FIRESTATION_POINT, myLocation, getString(R.string.help_coming), 0);
//        activeDataLocation = stationLocation;
//        if (!activeDataLocation.getLocations().isEmpty()) {
//            plotRoutes(getActiveLatLng(0), myLocation, getString(R.string.help_coming), 0);
//        } else {
//            Snackbar.make(fabReportFire, getString(R.string.no_station), Snackbar.LENGTH_SHORT).show();
//        }
    }

    // find evac route
    @OnClick(R.id.fab_get_me_out)
    void onGetMeOutClicked() {
        plotRoutes(myLocation, EVAC_POINT, getString(R.string.help_coming), 0);
//        activeDataLocation = evacLocation;
//        if (!activeDataLocation.getLocations().isEmpty()) {
//            plotRoutes(getActiveLatLng(0), myLocation, "", 0);
//        } else {
//            Snackbar.make(fabReportFire, getString(R.string.no_evac), Snackbar.LENGTH_SHORT).show();
//        }
    }

    private LatLng getActiveLatLng(int index) {
        return new LatLng(activeDataLocation.getLocations().get(index).getLatitude(), activeDataLocation.getLocations().get(index).getLongitude());
    }

    private void plotRoutes(final LatLng from, final LatLng to, final String message, final int index) {
        GoogleDirection.withServerKey(getString(R.string.google_maps_key))
                .from(from)
                .to(to)
                .avoid(AvoidType.FERRIES)
                .avoid(AvoidType.HIGHWAYS)
                .alternativeRoute(true)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            for (int i = 0; i < direction.getRouteList().size(); i++) {
                                Route route = direction.getRouteList().get(i);
                                String color = colors[i % colors.length];
                                ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                mMap.addPolyline(DirectionConverter.createPolyline(MapsActivity.this, directionPositionList, 5, Color.parseColor(color)));
                                mMap.animateCamera(getCameraUpdatePosition(from), Math.max(ANIM_DURATION, 1), null);
                                if (!message.isEmpty()) {
                                    //Snackbar.make(fabReportFire, getString(R.string.help_coming), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        } else {
//                            if (index < activeDataLocation.getLocations().size()) {
//                                int next_index = index + 1;
//                                plotRoutes(getActiveLatLng(next_index), EVAC_POINT, "", next_index);
//                            } else {
//                                Snackbar.make(fabReportFire, getString(R.string.no_route), Snackbar.LENGTH_SHORT).show();
//                            }
                            //Snackbar.make(fabReportFire, getString(R.string.no_route), Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Snackbar.make(fabReportFire, t.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == TAKE_PIC_CODE && resultCode == RESULT_OK && intent != null) {
            String imagePath = intent.getStringExtra("imagepath");
            double bearing = intent.getDoubleExtra("bearing", 0);
            //Toast.makeText(this, "Photo success " + imagePath + bearing, Toast.LENGTH_SHORT).show();
            sendFireReport(imagePath, myLocation, bearing);
        }
    }

    @Override
    public void onCameraIdle() {
        //LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        //getFireData(bounds);
        //getEvacuationData(myLocation);
    }

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int i) {

    }

    private void sendFireReport(String imagePath, LatLng location, double bearing) {
        if (restService != null) {
            //todo image upload
            Location loc = new Location("", location.latitude, location.longitude, bearing, "", "");
            Call<Location> results = restService.getFireService().reportFire(loc);
            results.enqueue(new Callback<Location>() {
                @Override
                public void onResponse(Call<Location> call, Response<Location> response) {
                    Snackbar.make(fabReportFire, getString(R.string.report_sent), Snackbar.LENGTH_SHORT).show();
                    Log.d("sendFireReport", response.toString());
                }

                @Override
                public void onFailure(Call<Location> call, Throwable t) {
                    Snackbar.make(fabReportFire, t.getMessage(), Snackbar.LENGTH_SHORT).show();
                    Log.e("sendFireReport", t.toString());
                }
            });
        }
    }

    private void getFireModisData() {
        if (restService != null) {
            Call<List<ModisLocation>> results = restService.getFireService().getFireModis();
            results.enqueue(new Callback<List<ModisLocation>>() {
                @Override
                public void onResponse(Call<List<ModisLocation>> call, Response<List<ModisLocation>> response) {
                    if(response.body() != null && !response.body().isEmpty()) {
                        clearMarkers(fireMarkers);
                        fireLocation.clear();
                        Locations tempList = new Locations();
                        for(ModisLocation location : response.body()) {
                            tempList.getLocations().add(new Location("", location.getLatitude(), location.getLongitude(), 0, "", ""));
                        }
                        fireLocation = tempList;
                        if(fireLocation != null && !fireLocation.getLocations().isEmpty()) {
                            populateMap(fireMarkers, fireLocation.getLocations(), MarkerType.FIRE);
                        }
                    }

                    Log.d("getFireData", response.toString());
                }

                @Override
                public void onFailure(Call<List<ModisLocation>> call, Throwable t) {
                    Log.d("getFireData", t.getMessage());
                }
            });
        }
    }

    private void getFireData(LatLngBounds bounds) {
        if (restService != null) {
            Call<Locations> results = restService.getFireService().getFireList(bounds.northeast.latitude, bounds.southwest.latitude, bounds.northeast.longitude, bounds.southwest.latitude);
            results.enqueue(new Callback<Locations>() {
                @Override
                public void onResponse(Call<Locations> call, Response<Locations> response) {
                    clearMarkers(fireMarkers);
                    fireLocation = response.body();
                    if(fireLocation != null && !fireLocation.getLocations().isEmpty()) {
                        populateMap(fireMarkers, fireLocation.getLocations(), MarkerType.FIRE);
                    }
                    Log.d("getFireData", response.toString());
                }

                @Override
                public void onFailure(Call<Locations> call, Throwable t) {
                    Snackbar.make(fabReportFire, getString(R.string.error_service), Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getEvacuationData(LatLng location) {
        if (restService != null) {
            Call<Locations> results = restService.getFireService().getEvacuationList(location.latitude, location.longitude);
            results.enqueue(new Callback<Locations>() {
                @Override
                public void onResponse(Call<Locations> call, Response<Locations> response) {
                    clearMarkers(evacMarkers);
                    evacLocation = response.body();
                    if(evacLocation!= null && !evacLocation.getLocations().isEmpty()) {
                        populateMap(evacMarkers, evacLocation.getLocations(), MarkerType.EVAC);
                    }
                    Log.d("getEvacuationData", response.toString());
                }

                @Override
                public void onFailure(Call<Locations> call, Throwable t) {
                    Snackbar.make(fabReportFire, getString(R.string.error_service), Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void populateMap(List<Marker> markers, List<Location> results, MarkerType type) {
        for (Location location : results) {
            markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()))
                    //.title(location.getName())
                    .icon(getMarker(type))));
        }
    }

    private void clearMarkers(List<Marker> markers) {
        for (Marker marker : markers) {
            marker.remove();
        }

        markers.clear();
    }
}
