package com.techclutch.rocket.rocketandroid.util;

import com.techclutch.rocket.rocketandroid.api.model.Location;


import java.util.ArrayList;

/**
 * Created by ArifH_AW17 on 4/30/2017.
 */

public class FireDepartment {

    public static double MinSafeDistanceKM = 0.37;

    private ArrayList<Location> fireNodes = new ArrayList<Location>();

    //Road Safety check
    public boolean IsSafeRoad(ArrayList<Location> roadNodes)
    {
        for(int i = 0; i < roadNodes.size(); ++i)
        {
            Location curr = roadNodes.get(i);
            for(int j = 0; j < getFireNodes().size(); ++j) {
                Location other = getFireNodes().get(j);
                if(GPSMathProcessor.Get().CalculateDistance(curr, other) < MinSafeDistanceKM)
                    return false;
            }
        }

        return true;
    }

    public ArrayList<ArrayList<Location>> RemoveDangerousRoad(ArrayList<ArrayList<Location>> roads)
    {
        ArrayList<ArrayList<Location>> result = new ArrayList<ArrayList<Location>>();

        for(int i = 0; i < roads.size(); ++i)
        {
            if(IsSafeRoad(roads.get(i)))
                result.add(roads.get(i));
        }

        return result;
    }

    public ArrayList<Location> getFireNodes() {
        return fireNodes;
    }

    public void setFireNodes(ArrayList<Location> fireNodes) {
        this.fireNodes = fireNodes;
    }
}
