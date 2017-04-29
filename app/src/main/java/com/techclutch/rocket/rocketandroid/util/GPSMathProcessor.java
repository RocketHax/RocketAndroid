package com.techclutch.rocket.rocketandroid.util;

import com.techclutch.rocket.rocketandroid.api.model.Location;

import java.util.ArrayList;

/**
 * Created by ArifH_AW17 on 4/29/2017.
 */

public class GPSMathProcessor {

    //Constants
    final int DistancePrecision = 1;
    final double EarthRadius = 6371e3; // Earth radius = 6,371km

    //Core Instance for lazy bum
    static GPSMathProcessor Processor = new GPSMathProcessor();

    //For lazy bum
    public static GPSMathProcessor Get()
    {
        return Processor;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //Basic Calculations//////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////

    public double Deg2Rad(double degrees)
    {
        return degrees * Math.PI / 180.0;
    }

    public double Rad2Deg(double radians)
    {
        return radians * (180.0 / Math.PI);
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //Distance in KM, using Haversine formula/////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////

    public double CalculateDistance(Location from, Location to)
    {
        double fromLatRad = Deg2Rad(from.getLatitude());
        double toLatRad = Deg2Rad(to.getLatitude());
        double fromLongRad = Deg2Rad(from.getLongitude());
        double toLongRad = Deg2Rad(to.getLongitude());


        double dlat1lat2 = toLatRad - fromLatRad;
        double dlong1long2 = toLongRad - fromLongRad;

        double a = Math.sin(dlat1lat2 / 2) * Math.sin(dlat1lat2 / 2) +
                Math.cos(fromLatRad) * Math.cos(toLatRad) *
                        Math.sin(dlong1long2 / 2) * Math.sin(dlong1long2 / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        //Set to one decimal places
        return Math.round((EarthRadius * c) / 1000);
    }

    //Calculate total distance
    public double CalculateTotalDistance(Location[] locations)
    {
        double totalDistance = 0.0;

        for (int i = 0; i < locations.length - 1; i++)
        {
            Location current = locations[i];
            Location next = locations[i + 1];

            totalDistance += CalculateDistance(current, next);
        }

        return totalDistance;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //Mid Point calculation///////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////

    public Location CalculateMiddle(Location from, Location to)
    {
        double lat1Rad = Deg2Rad(from.getLatitude());
        double lat2Rad = Deg2Rad(to.getLatitude());
        double long1Rad = Deg2Rad(from.getLongitude());
        double long2Rad = Deg2Rad(to.getLongitude());

        double Bx = Math.cos(lat2Rad) * Math.cos(long2Rad - long1Rad);
        double By = Math.cos(lat2Rad) * Math.sin(long2Rad - long1Rad);
        double latM = Math.atan2(Math.sin(lat1Rad) + Math.sin(lat2Rad),
                Math.sqrt((Math.cos(lat1Rad) + Bx) * (Math.cos(lat1Rad) + Bx) + By * By));
        double longM = long1Rad + Math.atan2(By, Math.cos(lat1Rad) + Bx);

        return new Location("", Rad2Deg(latM), Rad2Deg(longM), 0.0, "MidPoint", "");
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //Bearing Intersection////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////

    public Location CalculateIntersection(Location p1, Location p2)
    {
        //φ is latitude, λ is longitude, R is earth’s radius (mean radius = 6, 371km);
        //note that angles need to be in radians to pass to trig functions!

        //φ1, λ1, θ13: 1st start point & (initial)bearing from 1st point towards intersection point
        //φ2, λ2, θ23 : 2nd start point & (initial)bearing from 2nd point towards intersection point
        //φ3, λ3 : intersection point

        double φ1 = Deg2Rad(p1.getLatitude());
        double λ1 = Deg2Rad(p1.getLongitude());
        double φ2 = Deg2Rad(p2.getLatitude());
        double λ2 = Deg2Rad(p2.getLongitude());
        double θ13 = Deg2Rad(p1.getBearing());
        double θ23 = Deg2Rad(p2.getBearing());
        double Δφ = φ2 - φ1;
        double Δλ = λ2 - λ1;

        double δ12 = 2 * Math.asin(Math.sqrt(Math.sin(Δφ / 2) * Math.sin(Δφ / 2) + Math.cos(φ1) * Math.cos(φ2) * Math.sin(Δλ / 2) * Math.sin(Δλ / 2)));

        if (δ12 == 0)
            return null;

        double θa;
        if (!Double.isNaN(Math.acos((Math.sin(φ2) - Math.sin(φ1) * Math.cos(δ12)) / (Math.sin(δ12) * Math.cos(φ1)))))
            θa = Math.acos((Math.sin(φ2) - Math.sin(φ1) * Math.cos(δ12)) / (Math.sin(δ12) * Math.cos(φ1)));
        else
            θa = 0;

        double θb = Math.acos((Math.sin(φ1) - Math.sin(φ2) * Math.cos(δ12)) / (Math.sin(δ12) * Math.cos(φ2)));
        double θ12 = Math.sin(λ2 - λ1) > 0 ? θa : 2 * Math.PI - θa;
        double θ21 = Math.sin(λ2 - λ1) > 0 ? 2 * Math.PI - θb : θb;
        double α1 = (θ13 - θ12 + Math.PI) % (2 * Math.PI) - Math.PI; // angle 2-1-3
        double α2 = (θ21 - θ23 + Math.PI) % (2 * Math.PI) - Math.PI; // angle 1-2-3

        if (Math.sin(α1) == 0 && Math.sin(α2) == 0)
            return null; // infinite intersections

        if (Math.sin(α1) * Math.sin(α2) < 0)
            return null;

        double α3 = Math.acos(-Math.cos(α1) * Math.cos(α2) + Math.sin(α1) * Math.sin(α2) * Math.cos(δ12));
        double δ13 = Math.atan2(Math.sin(δ12) * Math.sin(α1) * Math.sin(α2), Math.cos(α2) + Math.cos(α1) * Math.cos(α3));
        double φ3 = Math.asin(Math.sin(φ1) * Math.cos(δ13) + Math.cos(φ1) * Math.sin(δ13) * Math.cos(θ13));
        double Δλ13 = Math.atan2(Math.sin(θ13) * Math.sin(δ13) * Math.cos(φ1), Math.cos(δ13) - Math.sin(φ1) * Math.sin(φ3));
        double λ3 = λ1 + Δλ13;

        double rLat = Rad2Deg(φ3);
        double rLong = (Rad2Deg(λ3) + 540) % 360 - 180;

        return new Location("", Rad2Deg(rLat), Rad2Deg(rLong), 0.0, "IntersectionPoint", "");
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //Region//////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////

    //Should return number of coordinates where N > 1, Count = N(N-1)/2
    //Returns bunch of GPS points to represent the region triangulated.
    public ArrayList<Location> TriangulateRegion(ArrayList<Location> reports)
    {
        if (reports == null || reports.size() < 2)
            return null;

        ArrayList<Location> resultingRegion = new ArrayList<Location>();

        for (int i = 0; i < reports.size(); ++i)
        {
            Location currReport = reports.get(i);

            for(int j = i + 1; j < reports.size(); ++j)
            {
                Location otherReport = reports.get(j);
                Location res = CalculateIntersection(currReport, otherReport);
                res.setName("RegionPoint");
                resultingRegion.add(res);
            }
        }

        return resultingRegion;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //Find Most///////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////

    Location FindMostNorth(ArrayList<Location> locations)
    {
        if(locations.isEmpty())
            return null;

        if(locations.size() == 1)
            return locations.get(0);

        Location mostN = locations.get(0);

        for(int i = 0; i < locations.size(); ++i)
        {
            Location curr = locations.get(i);

            if(mostN.getLatitude() < curr.getLatitude())
                mostN = curr;
        }

        return mostN;
    }

    Location FindMostEast(ArrayList<Location> locations)
    {
        if(locations.isEmpty())
            return null;

        if(locations.size() == 1)
            return locations.get(0);

        Location mostE = locations.get(0);

        for(int i = 0; i < locations.size(); ++i)
        {
            Location curr = locations.get(i);

            if(mostE.getLongitude() < curr.getLongitude())
                mostE = curr;
        }

        return mostE;
    }

    Location FindMostSouth(ArrayList<Location> locations)
    {
        if(locations.isEmpty())
            return null;

        if(locations.size() == 1)
            return locations.get(0);

        Location mostS = locations.get(0);

        for(int i = 0; i < locations.size(); ++i)
        {
            Location curr = locations.get(i);

            if(mostS.getLatitude() > curr.getLatitude())
                mostS = curr;
        }

        return mostS;
    }

    Location FindMostWest(ArrayList<Location> locations)
    {
        if(locations.isEmpty())
            return null;

        if(locations.size() == 1)
            return locations.get(0);

        Location mostW = locations.get(0);

        for(int i = 0; i < locations.size(); ++i)
        {
            Location curr = locations.get(i);

            if(mostW.getLongitude() > curr.getLongitude())
                mostW = curr;
        }

        return mostW;
    }

    Location FindMostNorthEastCorner(ArrayList<Location> locations)
    {
        if(locations.isEmpty())
            return null;

        if(locations.size() == 1)
            return locations.get(0);

        Location mostN = FindMostNorth(locations);
        Location mostE = FindMostEast(locations);

        mostN.setBearing(80);
        mostE.setBearing(10);

        Location cornerNE = CalculateIntersection(mostN, mostE);

        return cornerNE;
    }

    Location FindMostSouthWestCorner(ArrayList<Location> locations)
    {
        if(locations.isEmpty())
            return null;

        if(locations.size() == 1)
            return locations.get(0);

        Location mostS = FindMostSouth(locations);
        Location mostW = FindMostWest(locations);

        mostS.setBearing(-170);
        mostW.setBearing(-100);

        Location cornerSE = CalculateIntersection(mostS, mostW);

        return cornerSE;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //GPS Coordinate within radius of GPS Coordinate//////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////

    public boolean GPSPointWithinRadius(Location p1, Location p2, double radiusKM)
    {
        return CalculateDistance(p1, p2) < radiusKM;
    }

}
