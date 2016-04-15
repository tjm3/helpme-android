package uk.ac.hud.tjm3.helpme.http_api;

import android.content.Intent;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.hud.tjm3.helpme.LoginActivity;
import uk.ac.hud.tjm3.helpme.User;
import uk.ac.hud.tjm3.helpme.exceptions.InvalidLoginCredentialsRuntimeException;

/**
 * Created by tmkn on 17/03/16.
 */
public class UserSession {
    final static String TAG = "USER_SESSION";
    static UserSession instance;
    private HelpRequestService service;
    private User currentUser;

    protected UserSession() {
        this.service = ServiceGenerator.createService(HelpRequestService.class);
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }

        return instance;
    }

    public boolean isAuthenticated() {

        Call<User> call = this.service.getCurrentUser();

        try {
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccess()) {
                        UserSession.this.currentUser = response.body();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    throw new RuntimeException("No connection to the network or our server is not responding.");
                }
            });
        } catch (InvalidLoginCredentialsRuntimeException e) {
            return false;
        }

        return true;
    }

    public HelpRequestService getService() {
        return service;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public void logout() {
        this.currentUser = null;
        this.service = ServiceGenerator.createService(HelpRequestService.class);
    }

    public void setService(HelpRequestService service) {
        this.service = service;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
