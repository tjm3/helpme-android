package uk.ac.hud.tjm3.helpme;

import android.content.Context;
import android.location.LocationManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.hud.tjm3.helpme.exceptions.InvalidLoginCredentialsRuntimeException;
import uk.ac.hud.tjm3.helpme.http_api.HelpRequestService;
import uk.ac.hud.tjm3.helpme.http_api.ServiceGenerator;
import uk.ac.hud.tjm3.helpme.http_api.UserSession;

/**
 * Created by tmkn8 on 26/02/16.
 */
public class HelpRequestList {
    final static String TAG = "HELP_REQUEST_LIST";
    private double currentLatitude;
    private double currentLongitude;
    private int radius;
    private List<HelpRequest> helpRequests;
    private HelpRequestService service;

    public HelpRequestList(HelpRequestService service) {
        this.service = service;
        this.helpRequests = new ArrayList<HelpRequest>();
    }

    public double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(double currentLongitude) {
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

    public void reloadData() {
        Log.d(TAG, "reload data initiated");

        Call<List<HelpRequest>> call = this.service.getHelpRequestList(5, 5);
        call.enqueue(new Callback<List<HelpRequest>>() {
            @Override
            public void onResponse(Call<List<HelpRequest>> call, Response<List<HelpRequest>> response) {
                if(!response.isSuccess()) {
                    Log.d(TAG, "HTTP request to get a list of help requests failed.");

                    // User not logged in
                    if(response.code() == 401) {
                        throw new InvalidLoginCredentialsRuntimeException();
                    }

                    throw new RuntimeException("HTTP request to get a list of help requests failed");
                }

                for(HelpRequest helpRequest : response.body()) {
                    HelpRequestList.this.helpRequests.add(helpRequest);
                    Log.d(TAG, helpRequest.toString());
                }
            }

            @Override
            public void onFailure(Call<List<HelpRequest>> call, Throwable t) {
                throw new RuntimeException();
            }
        });
    }
}
