package com.techclutch.rocket.rocketandroid.util;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.techclutch.rocket.rocketandroid.R;

/**
 * Created by Arman on 4/29/2017.
 */

public class MapsUtil {

    private static final float ANIMATION_ZOOM = 15.5f;
    private static final float ANIMATION_BEARING = 0;
    private static final float ANIMATION_TILT = 25;

    public static BitmapDescriptor getFireMarker() {
        return BitmapDescriptorFactory.fromResource(R.drawable.ic_heat);
    }

    public static BitmapDescriptor getEvacMarker() {
        return BitmapDescriptorFactory.fromResource(R.drawable.ic_evacuation);
    }

    public static BitmapDescriptor getStationMarker() {
        return BitmapDescriptorFactory.fromResource(R.drawable.ic_heat);
    }

    public static BitmapDescriptor getYourLocationMarker() {
        return BitmapDescriptorFactory.fromResource(R.drawable.ic_evacuation);
    }

    public static CameraUpdate getCameraUpdatePosition(LatLng point) {
        final CameraPosition fromPosition =
                new CameraPosition.Builder().target(point)
                        .zoom(ANIMATION_ZOOM)
                        .bearing(ANIMATION_BEARING)
                        .tilt(ANIMATION_BEARING)
                        .build();
        return CameraUpdateFactory.newCameraPosition(fromPosition);
    }
}
