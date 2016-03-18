package uk.ac.hud.tjm3.helpme.http_api;


import retrofit2.Retrofit;
import uk.ac.hud.tjm3.helpme.User;
import uk.ac.hud.tjm3.helpme.exceptions.InvalidLoginCredentialsException;
import uk.ac.hud.tjm3.helpme.exceptions.ServerConnectionErrorException;

/**
 * Created by tmkn on 17/03/16.
 */
public class UserSession {
    static UserSession instance;
    private HelpRequestService service;
    private String username;
    private String password;
    private User currentUser;

    protected UserSession() {
        this.service = this.getServiceWithoutLogin();
    }

    public UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }

        return instance;
    }

    private HelpRequestService getServiceWithoutLogin() {
        return ServiceGenerator.createService(HelpRequestService.class);
    }

    public void signIn(String username, String password) {
        this.username = username;
        this.password = password;

        try {
            this.refreshCurrentUser();
        } catch(InvalidLoginCredentialsException | ServerConnectionErrorException e) {
            this.username = null;
            this.password = null;
        }
    }

    private void refreshCurrentUser() {
        final HelpRequestService loggedInService = ServiceGenerator.createService(
                HelpRequestService.class, this.username, this.password);

        Call<User> userCall = loggedInService.getCurrentUser();

        userCall = loggedInService(new Callback<User>() {
            @Override
            private void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    throw new InvalidLoginCredentialsException();
                }

                UserSession.this.currentUser = response.body();
                UserSession.this.service = loggedInService;
            }

            @Override
            private void onFailure(Throwable t) {
                throw new ServerConnectionErrorException();
            }
        });
    }

    public void logOut() {
        this.username = null;
        this.password = null;

        this.service = this.getServiceWithoutLogin();
    }

    public User getCurrentUser() {
        this.refreshCurrentUser();
        return this.currentUser;
    }

    public boolean isAuthenticated() {
        return this.currentUser != null;
    }

    public HelpRequestService getService() {
        return this.service;
    }
}
