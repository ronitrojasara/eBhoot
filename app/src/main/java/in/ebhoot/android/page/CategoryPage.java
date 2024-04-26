package in.ebhoot.android.page;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.splashscreen.SplashScreen;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import in.ebhoot.android.R;
import in.ebhoot.android.data.Category;
import in.ebhoot.android.data.NetworkUtils;
import in.ebhoot.android.data.Product;
import in.ebhoot.android.data.ProductsManager;
import in.ebhoot.android.ui.CategoryAdapter;
import in.ebhoot.android.ui.ProductsAdapter;

public class CategoryPage extends AppCompatActivity {
    final int[] aj = {0};
    List<Product> productList;
    boolean Loading = false;
    String orderBy = "date";
    ProductsAdapter productsAdapter;
    String order = "desc";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> false);
        setContentView(R.layout.category_layout);
        Category category = getIntent().getParcelableExtra("category");
        Chip finder_chip = findViewById(R.id.finder);
        finder_chip.setText("Find within " + category.getName());
        List<Category> categoryList = new ArrayList<>();
        if (category.getSubcategories() != null) {
            category.getSubcategories().forEach(new Consumer<Category>() {
                @Override
                public void accept(Category category2) {
                    category2.setParentId(0);
                    categoryList.add(category2);
                    if (category.getSubcategories() != null) {
                        categoryList.addAll(category2.getSubcategories());
                    }
                }
            });
        }

        CategoryAdapter categoryAdapter = new CategoryAdapter(this, categoryList);
        RecyclerView recyclerView = findViewById(R.id.sub_categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(categoryAdapter);
        MaterialButton sort = findViewById(R.id.sort);
        MaterialButton materialButton = findViewById(R.id.material_button);
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, android.R.anim.slide_out_right);
            }
        });
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(0, android.R.anim.slide_out_right);
            }
        });
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                    popupMenu.getMenuInflater().inflate(R.menu.sort_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            aj[0] = 0;
                            switch (item.getTitle().toString()) {
                                case "Date: Newest to oldest":
                                    orderBy = "date";
                                    order = "desc";
                                    break;
                                case "Date: Oldest to newest":
                                    orderBy = "date";
                                    order = "asc";
                                    break;
                                case "Title: A to Z":
                                    orderBy = "title";
                                    order = "asc";
                                    break;
                                case "Title: Z to A":
                                    orderBy = "title";
                                    order = "desc";
                                    break;
                                case "Price: Lowest to highest":
                                    orderBy = "price";
                                    order = "asc";
                                    break;
                                case "Price: Highest to lowest":
                                    orderBy = "price";
                                    order = "desc";
                                    break;
                                case "Rating: High to low":
                                    orderBy = "rating";
                                    order = "desc";
                                    break;
                                case "Popularity: High to Low":
                                    orderBy = "popularity";
                                    order = "desc";
                                    break;

                            }
                            productList.clear();
                            productsAdapter.notifyDataSetChanged();
                            if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
                                // Network is available, proceed with Retrofit request
                                // Make Retrofit API call here
                                loadMoreItems(category.getId(), orderBy, order, ++aj[0]);
                            } else {
                                // Network is not available, show a message or take appropriate action
                                NetworkUtils.showNetworkMessage(getApplicationContext(), false);
                            }

                            sort.setText(item.getTitle());
                            return true;
                        }
                    });
                    popupMenu.show();

                }
            }
        });
        productList = new ArrayList<>();
        productsAdapter = new ProductsAdapter(this, productList);
        RecyclerView recyclerView1 = findViewById(R.id.recycler_view_products_cate);
        recyclerView1.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView1.setAdapter(productsAdapter);
        if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
            // Network is available, proceed with Retrofit request
            // Make Retrofit API call here
            loadMoreItems(category.getId(), orderBy, order, ++aj[0]);

        } else {
            // Network is not available, show a message or take appropriate action
            NetworkUtils.showNetworkMessage(getApplicationContext(), false);
        }

        ScrollView scrollView = findViewById(R.id.scroll);
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!scrollView.canScrollVertically(1) && !Loading) {
                    if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
                        // Network is available, proceed with Retrofit request
                        // Make Retrofit API call here
                        loadMoreItems(category.getId(), orderBy, order, ++aj[0]);
                    } else {
                        // Network is not available, show a message or take appropriate action
                        NetworkUtils.showNetworkMessage(getApplicationContext(), false);
                    }

                }

            }
        });


    }

    private void loadMoreItems(int category, String orderBy, String order, int page) {
        Loading = true;
        ProgressBar progressBar = findViewById(R.id.progress_sale);
        progressBar.setVisibility(View.VISIBLE);

        new ProductsManager(getApplicationContext(), new ProductsManager.OnTaskCompleted() {

            @Override
            public void onTaskCompleted(JsonArray result) {
                Loading = false;
                progressBar.setVisibility(View.GONE);
                if (result != null) {
                    if (result.size() < 10) {
                        Loading = true;
                    }
                    try {
                        // Iterate through the JSON array and convert each JSON object into a data object
                        for (int i = 0; i < result.size(); i++) {
                            JsonObject jsonObject = result.get(i).getAsJsonObject();
                            String imageUrl = "";

                            imageUrl = jsonObject.getAsJsonArray("images").get(0).getAsJsonObject().get("src").getAsString();

//                                            int lastIndex = imageUrl.lastIndexOf('.');
//                                            imageUrl = imageUrl.substring(0, lastIndex) + "-150x150." + imageUrl.substring(lastIndex + 1);
//                                Log.d("hello",imageUrl);
                            String categoryName = jsonObject.getAsJsonArray("categories").get(0).getAsJsonObject().get("name").getAsString().replace("&amp;", "&");
                            String productName = jsonObject.get("name").getAsString();
                            int id = jsonObject.get("id").getAsInt();
                            String price = jsonObject.get("price").getAsString();
                            String rprice = jsonObject.get("regular_price").getAsString();
//                                Log.d("hello",rprice);

                            // Create a Product object from the parsed JSON data
                            productList.add(new Product(id, imageUrl, categoryName, productName, price, rprice));
                            productsAdapter.notifyItemChanged(i * (aj[0] * 10));

                        }
                    } catch (JsonParseException | IndexOutOfBoundsException e) {
                        Log.e("TAG", "Error parsing JSON" + result + "" + result.size(), e);
                    }
                }


            }


        }).fetchProductsByCategory(category, orderBy, order, page);

    }

}
