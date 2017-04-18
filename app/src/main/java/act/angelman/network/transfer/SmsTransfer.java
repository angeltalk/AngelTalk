package act.angelman.network.transfer;

import android.content.Context;
import android.content.Intent;
import android.util.ArrayMap;
import org.json.JSONObject;
import java.util.Map;
import act.angelman.R;
import act.angelman.domain.model.CardModel;
import act.angelman.network.service.UrlShortenerService;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class SmsTransfer {

    public static final String HTTPS_WWW_GOOGLEAPIS_COM = "https://www.googleapis.com/";
    private Context context;

    public SmsTransfer(Context context) {
        this.context = context;
    }

    public void sendSmsMessage(final String key, final CardModel cardModel, final OnCompleteListener completeListener) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(HTTPS_WWW_GOOGLEAPIS_COM).build();
        UrlShortenerService urlShortenerService = retrofit.create(UrlShortenerService.class);
        Map<String, Object> requestBodyMap = new ArrayMap<>();
        requestBodyMap.put("longUrl", context.getResources().getString(R.string.share_functions_url) + key);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(requestBodyMap)).toString());
        Call<ResponseBody> response = urlShortenerService.getShortenerUrl(context.getString(R.string.share_api_key), body);
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> rawResponse) {
                completeListener.onComplete();
                String shortenUrl;
                try {
                    shortenUrl = (String) (new JSONObject(rawResponse.body().string().replaceAll("\n", ""))).get("id");
                } catch (Exception e) {
                    shortenUrl = context.getResources().getString(R.string.share_functions_url) + key;
                }
                sendSmsIntent(shortenUrl, cardModel);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                String longUrl = context.getResources().getString(R.string.share_functions_url) + key;
                completeListener.onComplete();
                sendSmsIntent(longUrl, cardModel);
            }
        });
    }

    private void sendSmsIntent(String url, CardModel cardModel) {
        String smsBody = context.getResources().getString(R.string.share_sms_message, cardModel.name) + url;
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendIntent.putExtra("sms_body", smsBody);
        sendIntent.setType("vnd.android-dir/mms-sms");
        context.startActivity(sendIntent);
    }

    public interface OnCompleteListener {
        void onComplete();
    }

}
