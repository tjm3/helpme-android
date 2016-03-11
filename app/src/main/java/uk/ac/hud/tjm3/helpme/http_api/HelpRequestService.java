package uk.ac.hud.tjm3.helpme.http_api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import uk.ac.hud.tjm3.helpme.HelpRequest;
import uk.ac.hud.tjm3.helpme.HelpRequestList;

public interface HelpRequestService {
    @GET("/help-requests")
    Call<List<HelpRequest>> getHelpRequestList();

    @GET("/help-requests")
    Call<List<HelpRequest>> getHelpRequestList(
            @Query("longitude") float longitude,
            @Query("latitude") float latitude);

    @GET("/help-requests")
    Call<List<HelpRequest>> getHelpRequestList(
            @Query("longitude") float longitude,
            @Query("latitude") float latitude,
            @Query("radius") int radius);

    @POST("/help-requests")
    Call<List<HelpRequest>> postHelpRequest();

    @GET("/help-requests/{id}")
    Call<HelpRequest> getHelpRequest(
            @Path("id") int id);

    @DELETE("/help-requests/{id}")
    Call<HelpRequest> deleteHelpRequest(
            @Path("id") int id);

    @PUT("/help-requests/{id}")
    Call<HelpRequest> updateHelpRequest(
            @Path("id") int id);
}
