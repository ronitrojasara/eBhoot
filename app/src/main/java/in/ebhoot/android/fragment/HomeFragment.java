package in.ebhoot.android.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.List;

import in.ebhoot.android.R;
import in.ebhoot.android.data.NetworkUtils;
import in.ebhoot.android.data.Product;
import in.ebhoot.android.data.ProductsManager;
import in.ebhoot.android.ui.ProductsAdapter;

public class HomeFragment extends Fragment {

    final int[] aj = {0};
    List<Product> productList;
    boolean Loading = false;
    String orderBy = "date";
    String order = "desc";
    private ProductsAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        MaterialToolbar materialToolbar = view.findViewById(R.id.topAppBar);
        SearchView searchView = view.findViewById(R.id.search_view_home);
        OnBackPressedDispatcher onBackPressedDispatcher = requireActivity().getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (searchView.isShowing()) {
                    searchView.hide();
                } else {
                    requireActivity().finish();
                }
            }
        });
        Menu menu = materialToolbar.getMenu();
        CoordinatorLayout coordinatorLayout = view.findViewById(R.id.layout_home);
        menu.findItem(R.id.search_btn).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                coordinatorLayout.setFitsSystemWindows(false);
                searchView.show();

                return true;
            }
        });


        FloatingActionButton fabScrollToTop = requireActivity().findViewById(R.id.floatab);
        fabScrollToTop.hide(); // Initially hide FAB

        productList = new ArrayList<>();
//        loadMoreItems(view);
        if (NetworkUtils.isNetworkConnected(requireContext())) {
            // Network is available, proceed with Retrofit request
            // Make Retrofit API call here
            loadMoreItems(view, orderBy, order, ++aj[0]);
        } else {
            // Network is not available, show a message or take appropriate action
            NetworkUtils.showNetworkMessage(requireContext(), false);
        }

        NestedScrollView nestedScrollView = view.findViewById(R.id.nest);
        final int[] s = {0};


        fabScrollToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nestedScrollView.smoothScrollTo(0, 0);
                fabScrollToTop.hide();
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_home);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Set GridLayoutManager with 2 columns
        adapter = new ProductsAdapter(requireActivity(), productList); // Create your adapter instance
        recyclerView.setAdapter(adapter); // Set adapter to RecyclerView

//                return true;
//            }
//        });
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                Log.d("hello",""+(++s[0]));
//


                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    if (!Loading && !nestedScrollView.canScrollVertically(1)) {
                        // End has been reached, load more items
                        if (NetworkUtils.isNetworkConnected(getContext())) {
                            // Network is available, proceed with Retrofit request
                            // Make Retrofit API call here
                            loadMoreItems(view, orderBy, order, ++aj[0]);

                        } else {
                            // Network is not available, show a message or take appropriate action
                            NetworkUtils.showNetworkMessage(getContext(), false);
                        }

                    }
                    if (!nestedScrollView.canScrollVertically(-1) && fabScrollToTop.getVisibility() == View.VISIBLE) {
                        fabScrollToTop.hide();
                    } else if (fabScrollToTop.getVisibility() != View.VISIBLE && nestedScrollView.canScrollVertically(-1) && bottomNavigationView.getSelectedItemId()==bottomNavigationView.getMenu().getItem(0).getItemId()) {
                        fabScrollToTop.show();
                    }
                }
//                lastS[0] = nestedScrollView.getScrollY();
            }
        });


        searchView.addTransitionListener(
                (search, previousState, newState) -> {
                    if (newState == SearchView.TransitionState.SHOWN) {
                        // Handle search view opened.
                        bottomNavigationView.setVisibility(View.INVISIBLE);
                        fabScrollToTop.hide();
                    } else if (newState == SearchView.TransitionState.HIDING) {
                        coordinatorLayout.setFitsSystemWindows(true);
                    } else {

                        fabScrollToTop.show();
                        bottomNavigationView.setVisibility(View.VISIBLE);
                    }
                });


        MaterialButton sort = view.findViewById(R.id.sort);
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(requireContext(), v);
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
                        adapter.notifyDataSetChanged();
                        if (NetworkUtils.isNetworkConnected(getContext())) {
                            // Network is available, proceed with Retrofit request
                            // Make Retrofit API call here
                            loadMoreItems(view, orderBy, order, ++aj[0]);
                        } else {
                            // Network is not available, show a message or take appropriate action
                            NetworkUtils.showNetworkMessage(getContext(), false);
                        }

                        sort.setText(item.getTitle());
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        return view;
    }

    private void loadMoreItems(View view, String orderBy, String order, int page) {
        Loading = true;
        ProgressBar progressBar = view.findViewById(R.id.progress_sale);
        progressBar.setVisibility(View.VISIBLE);

        new ProductsManager(getContext(), new ProductsManager.OnTaskCompleted() {

            @Override
            public void onTaskCompleted(JsonArray result) {
                Loading = false;
                progressBar.setVisibility(View.GONE);
                if (result != null) {

                    try {
                        // Iterate through the JSON array and convert each JSON object into a data object
                        for (int i = 0; i < result.size(); i++) {
                            JsonObject jsonObject = result.get(i).getAsJsonObject();
                            String imageUrl = "";

                            imageUrl = jsonObject.getAsJsonArray("images").get(0).getAsJsonObject().get("src").getAsString();


                            String categoryName = jsonObject.getAsJsonArray("categories").get(0).getAsJsonObject().get("name").getAsString().replace("&amp;", "&");
                            String productName = jsonObject.get("name").getAsString();
                            int id = jsonObject.get("id").getAsInt();
                            String price = jsonObject.get("price").getAsString();
                            String rprice = jsonObject.get("regular_price").getAsString();
//                                Log.d("hello",rprice);

                            // Create a Product object from the parsed JSON data
                            productList.add(new Product(id, imageUrl, categoryName, productName, price, rprice));
                            adapter.notifyItemChanged(i * (aj[0] * 10));

                        }
                    } catch (JsonParseException | IndexOutOfBoundsException e) {
                        Log.e("TAG", "Error parsing JSON" + result + "" + result.size(), e);
                    }
                }


            }


        }).fetchProductsBy(orderBy, order, page);

    }


}