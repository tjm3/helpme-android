package uk.ac.hud.tjm3.helpme;

import java.util.List;

/**
 * Created by tmkn8 on 26/02/16.
 */
public class HelpRequestList {
    private float currentLatitude;
    private float currentLongitude;
    private int radius;
    private List<HelpRequest> helpRequests;

    public float getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(float currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public float getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(float currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public List<HelpRequest> getHelpRequests() {
        return helpRequests;
    }

    public void setHelpRequests(List<HelpRequest> helpRequests) {
        this.helpRequests = helpRequests;
    }
}
