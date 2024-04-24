package in.ebhoot.android.data;

import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WooCommerceAPI {
    @GET("products")
    Call<JsonArray> getProductsBy(@Query("orderby") String orderBy,@Query("order") String order,@Query("page") int page,@Query("status") String status);

    @GET("products")
    Call<JsonArray> getProductById(@Query("id") int ID);

    @GET("products")
    Call<JsonArray> getProductsByCategory(@Query("category") int categoryId,@Query("orderby") String orderBy,@Query("order") String order,@Query("page") int page,@Query("status") String status);

    @GET("products")

    Call<JsonArray> getSaleProducts(@Query("on_sale") Boolean onSale,@Query("page") int page,@Query("status") String status);

    @GET("products")

    Call<JsonArray> getFeaturedProducts(@Query("featured") Boolean featured);

    @GET("products/categories")
    Call<JsonArray> getCategories(@Query("per_page") int perPage);
}
