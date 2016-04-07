package uk.ac.hud.tjm3.helpme.http_api;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.ac.hud.tjm3.helpme.exceptions.InvalidLoginCredentialsRuntimeException;

/**
 * API service generator class
 * It generates the basic Retrofit object to use with our API.
 *
 * @author Tomasz Knapik
 */
public class ServiceGenerator {

    public static final String API_BASE_URL = "http://helpme.tmkn8.me/api/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson));

    /**
     * This method creates an API service for given resource passed in parameter.
     *
     * @param serviceClass Resource/service interface you want to use with API
     * @param username user login
     * @param password user password
     * @return API client of the given resource.
     */
    public static <S> S createService(Class<S> serviceClass, String username, String password) {
        // Create a variable to store username
        String basicAuthorizationHeader = null;

        // If username and password were given as arguments, create a HTTP authorization header
        if (username != null && password != null) {
            // Get credentials and encode them
            basicAuthorizationHeader = "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP);
        }

        // Set a constant, so the inner classes are able to read it
        final String BASIC_AUTHORIZATION_HEADER = basicAuthorizationHeader;

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                // Get the current request
                Request originalRequest = chain.request();

                // Add JSON header to our request
                Request.Builder requestBuilder = originalRequest.newBuilder()
                        .header("Accept", "application/json")
                        .method(originalRequest.method(), originalRequest.body());

                // If authorization header exists add authorization header to the HTTP request
                if (BASIC_AUTHORIZATION_HEADER != null) {
                    requestBuilder.header("Authorization", BASIC_AUTHORIZATION_HEADER);
                    requestBuilder.header("AuthorizationInformation", "Credentials provided");
                } else {
                    requestBuilder.header("AuthorizationInformation", "No credentials");
                }

                // Return response
                Request newRequest = requestBuilder.build();
                Response response = chain.proceed(newRequest);

                // If user is logged in, then check if credentials are valid
                if (BASIC_AUTHORIZATION_HEADER != null) {
                    if (response.code() == 401) {
                        throw new InvalidLoginCredentialsRuntimeException();
                    }
                }

                return response;
            }
        });

        // Set logging level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Add logging to the HTTP client
        httpClient.addInterceptor(logging);

        // Create retrofit instance
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }

    /**
     * This is a shorthand to create a service without login credentials.
     *
     * @param serviceClass Resource/service interface you want to use with API
     * @return API client of the given resource.
     */
    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null, null);
    }

}