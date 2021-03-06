package uk.ac.hud.tjm3.helpme.http_api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import uk.ac.hud.tjm3.helpme.HelpRequest;
import uk.ac.hud.tjm3.helpme.HelpRequestReply;
import uk.ac.hud.tjm3.helpme.User;

public interface HelpRequestService {
    @GET("users/me/")
    Call<User> getCurrentUser();

    @PUT("users/me/")
    Call<User> updateCurrentUser(@Body User user);

    @GET("users/{id}/")
    Call<User> getUser(
      @Path("id") int id);

    @GET("help-requests/")
    Call<List<HelpRequest>> getHelpRequestList();

    @GET("help-requests/")
    Call<List<HelpRequest>> getHelpRequestList(
            @Query("user_longitude") double longitude,
            @Query("user_latitude") double latitude);

    @GET("help-requests/")
    Call<List<HelpRequest>> getHelpRequestList(
            @Query("user_longitude") float longitude,
            @Query("user_latitude") float latitude,
            @Query("radius") int radius);

    @POST("help-requests/")
    Call<HelpRequest> postHelpRequest(@Body HelpRequest helpRequest);

    @GET("help-requests/{id}/")
    Call<HelpRequest> getHelpRequest(
            @Path("id") int id);

    @DELETE("help-requests/{id}/")
    Call<HelpRequest> deleteHelpRequest(
            @Path("id") int id);

    @PUT("help-requests/{id}/")
    Call<HelpRequest> updateHelpRequest(
            @Path("id") int id, @Body HelpRequest helpRequest);

    @POST("help-request-replies/")
    Call<HelpRequestReply> sendHelpRequestReply(@Body HelpRequestReply helpRequestReply);
}
