package angeltalk.plus.network.service;


import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UrlShortenerService {

    @POST("urlshortener/v1/url")
    Call<ResponseBody> getShortenerUrl(@Query("key") String apiKey, @Body RequestBody params);

}
