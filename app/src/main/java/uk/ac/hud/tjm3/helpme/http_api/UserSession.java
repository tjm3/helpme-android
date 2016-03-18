package uk.ac.hud.tjm3.helpme.http_api;

import retrofit2.Retrofit;

/**
 * Created by tmkn on 17/03/16.
 */
public class UserSession {
    static UserSession instance;
    private HelpRequestService service;

    protected UserSession() {
        this.service = ServiceGenerator.createService(HelpRequestService.class);
    }

    public UserSession getInstance() {
        if(instance == null) {
            instance = new UserSession();
        }

        return instance;
    }

    public HelpRequestService signIn(String username, String password) {
        return this.service;
        // TODO: Log in
    }
}
