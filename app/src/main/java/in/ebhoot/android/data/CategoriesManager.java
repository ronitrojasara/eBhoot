package in.ebhoot.android.data;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.google.gson.JsonArray;

import in.ebhoot.android.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CategoriesManager {
    Context context;
    private static final String TAG = "WooCommerceAPIHandler";
    private static final String BASE_URL = "https://ebhoot.in/wp-json/wc/v3/";

    private final CategoriesManager.OnTaskCompleted listener;
    private final WooCommerceAPI service;
    public CategoriesManager(Context context, OnTaskCompleted listener) {
        this.context = context;
        this.listener = listener;

        // Initialize Retrofit service
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(WooCommerceAPI.class);
    }

    private OkHttpClient getOkHttpClient() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header("Authorization", getAuthHeader())
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        });
        return httpClient.build();
    }
    private String getAuthHeader() {
        String credentials = context.getString(R.string.consumer_key_r) + ":" + context.getString(R.string.consumer_secret_r);
        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    }

    public void fetchCategories(){
        Call<JsonArray> call = service.getCategories(100);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.d("hello",response.message());
                if (response.isSuccessful()) {
                    listener.onTaskCompleted(response.body());
                } else {
                    Log.d(TAG, "Error fetching categories: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.d(TAG, "Failure fetching categories: " + t.getMessage());
            }
        });
    }
    public interface OnTaskCompleted {
        void onTaskCompleted(JsonArray result);
    }

    }
