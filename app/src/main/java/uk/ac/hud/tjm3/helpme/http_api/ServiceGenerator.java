package uk.ac.hud.tjm3.helpme.http_api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * API service generator class
 *
 * It generates the basic Retrofit object to use with our API.
 *
 * @author Tomasz Knapik
 */
public class ServiceGenerator {

    public static final String API_BASE_URL = "http://helpme.tmkn8.me/api";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    /**
     * This method creates an API service for given resource passed in parameter.
     * @param serviceClass Resource/service interface you want to use with API
     * @return API client of the given resource.
     */
    public static <S> S createService(Class<S> serviceClass) {
        // Set logging level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Add logging to the HTTP client
        httpClient.addInterceptor(logging);

        // Create retrofit instance
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }

}