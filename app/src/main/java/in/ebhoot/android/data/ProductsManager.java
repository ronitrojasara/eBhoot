package in.ebhoot.android.data;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import in.ebhoot.android.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductsManager {
    private static final String TAG = "WooCommerceAPIHandler";
    private static final String BASE_URL = "https://ebhoot.in/wp-json/wc/v3/";

    private final Context mContext;
    private OnTaskCompleted listener;
    private OnTaskComplete listener1;

    private final WooCommerceAPI service;

    public ProductsManager(Context context, OnTaskCompleted listener) {
        this.mContext = context;
        this.listener = listener;

        // Initialize Retrofit service
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(WooCommerceAPI.class);
    }
    public ProductsManager(Context context, OnTaskComplete listener1) {
        this.mContext = context;
        this.listener1 = listener1;

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
        String credentials = mContext.getString(R.string.consumer_key_r) + ":" + mContext.getString(R.string.consumer_secret_r);
        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    }

    public void fetchFeaturedProducts() {
        Call<JsonArray> call = service.getFeaturedProducts(true);

        fetchProducts(call);
    }

    public void fetchProduct(int Id) {
        Call<JsonObject> call = service.getProductById(Id);
        fetchProduct(call);
    }

    public void fetchOnSaleProducts(int page) {
        Call<JsonArray> call = service.getSaleProducts(true, page, "publish");
        fetchProducts(call);
    }

    public void fetchProductsByCategory(int categoryId, String orderBy, String order, int page) {
        Call<JsonArray> call = service.getProductsByCategory(categoryId, orderBy, order, page, "publish");
        fetchProducts(call);
    }

    public void fetchProductsBy(String orderBy, String order, int page) {
        Call<JsonArray> call = service.getProductsBy(orderBy, order, page, "publish");
        fetchProducts(call);
    }

    private void fetchProduct(Call<JsonObject> call){
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    listener1.onTaskCompleted(response.body());
                } else {
                    Log.d(TAG, "Error fetching products: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Log.d(TAG, "Failure fetching products: " + throwable.getMessage());

            }
        });
    }

    private void fetchProducts(Call<JsonArray> call) {

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    listener.onTaskCompleted(response.body());
                } else {
                    Log.d(TAG, "Error fetching products: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.d(TAG, "Failure fetching products: " + t.getMessage());
            }
        });
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(JsonArray result);
    }
    public interface OnTaskComplete {
        void onTaskCompleted(JsonObject result);
    }
}
