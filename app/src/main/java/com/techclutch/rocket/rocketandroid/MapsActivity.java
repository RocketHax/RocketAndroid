package com.techclutch.rocket.rocketandroid;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.techclutch.rocket.rocketandroid.api.RestService;
import com.techclutch.rocket.rocketandroid.api.model.Location;

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
import static com.techclutch.rocket.rocketandroid.util.MapsUtil.getEvacMarker;
import static com.techclutch.rocket.rocketandroid.util.MapsUtil.getFireMarker;
import static com.techclutch.rocket.rocketandroid.util.MapsUtil.getMarker;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        OnCameraMoveStartedListener,
        OnCameraMoveListener,
        OnCameraMoveCanceledListener,
        OnCameraIdleListener, {

    @BindView(fab_report_fire)
    FloatingActionButton fabReportFire;

    @BindView(R.id.fab_get_me_out)
    FloatingActionButton fabGetMeOut;

    private String[] colors = {"#7fff7272", "#7f31c7c5", "#7fff8a00"};

    private static final int TAKE_PIC_CODE = 1001;
    private static final int ANIM_DURATION = 1000;
    private static final float DEFAULT_ZOOM = 10.0f;
    private GoogleMap mMap;
    private MarkerOptions myLocation;
    private static final LatLng EVAC_POINT = new LatLng(39.528271, -105.317200);//39.952757, -105.526083
    private static final LatLng DISASTER_POINT = new LatLng(39.451833, -105.181297);//39.984 latitude, -105.489
    private static final LatLng FIRESTATION_POINT = new LatLng(39.498242, -105.334889);//39.967422, -105.516022
    private RestService restService = new RestService();

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

        mMap.getUiSettings().setZoomGesturesEnabled(true);
        myLocation = new MarkerOptions().position(DISASTER_POINT).title("You are here").icon(getFireMarker());
        mMap.addMarker(myLocation);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(DISASTER_POINT));
        // zoom values range from 2-21
        mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
    }

    @OnClick(fab_report_fire)
    void onReportFireClicked() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivityForResult(intent, TAKE_PIC_CODE);
    }

    @OnClick(R.id.fab_get_me_out)
    void onGetMeOutClicked() {
        GoogleDirection.withServerKey(getString(R.string.google_maps_key))
                .from(DISASTER_POINT)
                .to(EVAC_POINT)
                .avoid(AvoidType.FERRIES)
                .avoid(AvoidType.HIGHWAYS)
                .alternativeRoute(true)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            mMap.addMarker(new MarkerOptions().position(DISASTER_POINT).icon(getFireMarker()));
                            mMap.addMarker(new MarkerOptions().position(EVAC_POINT).icon(getEvacMarker()));
                            for (int i = 0; i < direction.getRouteList().size(); i++) {
                                Route route = direction.getRouteList().get(i);
                                String color = colors[i % colors.length];
                                ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                mMap.addPolyline(DirectionConverter.createPolyline(MapsActivity.this, directionPositionList, 5, Color.parseColor(color)));
                                mMap.animateCamera(getCameraUpdatePosition(DISASTER_POINT), Math.max(ANIM_DURATION, 1), null);
                            }
                        } else {
                            // Do something
                            Snackbar.make(fabReportFire, "Direction failed", Snackbar.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Photo success " + imagePath + bearing, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCameraIdle() {
        Toast.makeText(this, "The camera has stopped moving. Fetch the data from the server!", Toast.LENGTH_SHORT).show();
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        getFireData(bounds);
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

    private void getFireData(LatLngBounds bounds) {
        if (restService != null) {
            Call<List<Location>> results = restService.getFireService().getFireList(bounds.northeast.latitude + "", bounds.southwest.latitude + "", bounds.northeast.longitude + "", bounds.southwest.latitude + "");
            results.enqueue(new Callback<List<Location>>() {
                @Override
                public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
                    populateMap(response.body(), MarkerType.FIRE);
                }

                @Override
                public void onFailure(Call<List<Location>> call, Throwable t) {

                }
            });
        }
    }

    private void populateMap(List<Location> results, MarkerType type) {
        for (Location location : results) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).icon(getMarker(type)));
        }
    }
}
