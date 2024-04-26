package in.ebhoot.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import in.ebhoot.android.R;
import in.ebhoot.android.data.CategoriesManager;
import in.ebhoot.android.data.Category;
import in.ebhoot.android.data.NetworkUtils;
import in.ebhoot.android.ui.CategoryAdapter;

public class CategoriesFragment extends Fragment {
    private List<Category> categoryList;
    private CategoryAdapter categoryAdapter;
    RecyclerView recyclerView;

    public CategoriesFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        AppBarLayout appBarLayout = view.findViewById(R.id.appBarLayout);
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav);
        SearchView searchView = view.findViewById(R.id.search_view);
        OnBackPressedDispatcher onBackPressedDispatcher = this.requireActivity().getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (searchView.isShowing()){
                    searchView.hide();
                }else{
                    requireActivity().finish();
                }
            }
        });


        recyclerView = view.findViewById(R.id.sub_categories);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        categoryList = new ArrayList<>();

        if (NetworkUtils.isNetworkConnected(getContext())) {
            // Network is available, proceed with Retrofit request
            // Make Retrofit API call here
            retry();
        } else {
            RelativeLayout relativeLayout = view.findViewById(R.id.error_nic);
            relativeLayout.setVisibility(View.VISIBLE);
            MaterialButton materialButton = relativeLayout.findViewById(R.id.rty);
            materialButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetworkUtils.isNetworkConnected(getContext())) {
                        // Network is available, proceed with Retrofit request
                        // Make Retrofit API call here
                        relativeLayout.setVisibility(View.GONE);
                        retry();
                    }

                }
            });
            // Network is not available, show a message or take appropriate action
            NetworkUtils.showNetworkMessage(getContext(), false);
        }

        CoordinatorLayout coordinatorLayout = view.findViewById(R.id.layout_cate);
        MaterialButton materialButton = view.findViewById(R.id.material_button);
        MaterialToolbar materialToolbar = view.findViewById(R.id.topAppBar);
        SearchBar searchBar = view.findViewById(R.id.search_bar);
        float y = materialToolbar.getY();
        searchView.addTransitionListener(
                (search, previousState, newState) -> {
                    if (newState == SearchView.TransitionState.SHOWN) {
                        // Handle search view opened.
                        bottomNavigationView.setVisibility(View.INVISIBLE);
                        coordinatorLayout.setFitsSystemWindows(false);


                    } else {
                        coordinatorLayout.setFitsSystemWindows(true);

                        bottomNavigationView.setVisibility(View.VISIBLE);
                    }
                });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                // Calculate the total scroll range
                int scrollRange = appBarLayout.getTotalScrollRange();

                // Calculate the scroll percentage
                float percentage = (float) Math.abs(verticalOffset) / (float) scrollRange;

                materialToolbar.setTranslationY(percentage * 200);
                if (percentage == 1) {

                    materialButton.setVisibility(View.INVISIBLE);
                    searchBar.setVisibility(View.INVISIBLE);
                } else if (percentage == 0) {


                } else {

                    materialButton.setVisibility(View.VISIBLE);
                    searchBar.setVisibility(View.VISIBLE);
                }
            }
        });
        return view;
    }

    private void retry() {

        new CategoriesManager(requireContext(), new CategoriesManager.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(JsonArray response) {
                if (response!=null){
                    Map<Integer, Category> categoryMap = new HashMap<>();
                    // Map to store categories by their IDs

                    // Iterate over the JSON response to build the hierarchy
                    for (int i = 0; i < response.size(); i++) {
                        JsonObject jsonObject = response.get(i).getAsJsonObject();
                        int id = jsonObject.get("id").getAsInt();
                        int parentId = jsonObject.get("parent").getAsInt();
                        String name = jsonObject.get("name").getAsString().replace("&amp;", "&");
                        int count = jsonObject.get("count").getAsInt();

                        Category category = new Category(id, name, count, parentId);
//                        if (count!=0){
                        // Add the category to the map
                        categoryMap.put(id, category);

                        categoryList.add(category);
//                    }
                    }

                    for (Category cate:categoryList) {
                        int parentId = cate.getParentId();
                        if (parentId != 0) {
                            Category parentCategory = categoryMap.get(parentId);
                            if (parentCategory != null) {
                                if (parentCategory.getSubcategories() == null) {
                                    parentCategory.setSubcategories(new ArrayList<>());
                                }
                                parentCategory.getSubcategories().add(cate);
                            }
                        }
                    }

                    List<Category> categories = new ArrayList<>();

                    categoryList.forEach(new Consumer<Category>() {
                        @Override
                        public void accept(Category category) {
                            if(category.getParentId()==0){
                                categories.add(category);
                                if (category.getSubcategories()!=null){
                                    category.getSubcategories().forEach(new Consumer<Category>() {
                                        @Override
                                        public void accept(Category category) {
                                            categories.add(category);
                                            if (category.getSubcategories() != null) {
                                                category.getSubcategories().forEach(new Consumer<Category>() {
                                                    @Override
                                                    public void accept(Category category) {
                                                        categories.add(category);
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
//                            if (PcategoryList.contains(category)){
//                                categories.add(category);
//                            }
//                            if (category.getSubcategories()!=null && category.getParentId()!=0){
//                                categories.add(category);
//                            }
//                            if (category.getSubcategories()!=null){
//                                categories.addAll(category.getSubcategories());
//                            }
                        }


                    });
//                    categoryMap.forEach(new BiConsumer<Integer, Category>() {
//
//                        @Override
//                        public void accept(Integer integer, Category category) {
//
//                        }
//                    });


                    categoryAdapter = new CategoryAdapter(requireActivity(),categories);
                    recyclerView.setAdapter(categoryAdapter);

                }
            }
        }).fetchCategories();
    }


}
