package in.ebhoot.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.ebhoot.android.page.CategoryPage;
import in.ebhoot.android.R;
import in.ebhoot.android.data.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories;

    Activity activity;

    public CategoryAdapter(Activity activity, List<Category> categories) {
        this.activity = activity;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the category item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
        }

        public void bind(Category category) {
            // Add indentation based on the category's depth in the hierarchy
            StringBuilder indentation = new StringBuilder();
            for (int i = 0; i < getIndentationLevel(category); i++) {
                indentation.append("      "); // You can adjust the number of spaces for indentation as needed
            }
            if (indentation.toString().isEmpty()){
                categoryName.setTextColor(categoryName.getResources().getColor(R.color.inv,null));
                categoryName.setTextSize(17);
            }
            categoryName.setText(indentation.toString() + category.getName()+" ("+category.getCount()+")");
            categoryName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.getContext().startActivity(new Intent(v.getContext(),CategoryPage.class).putExtra("category",category));
                    activity.overridePendingTransition(android.R.anim.slide_in_left, 0);

                }
            });
        }

        // Calculate the indentation level based on the category's depth in the hierarchy
        private int getIndentationLevel(Category category) {
            int level = 0;
            Category parent = category;
            while (parent.getParentId() != 0) {
                    level++;
                    parent = getParentCategory(parent.getParentId());
            }
            return level;
        }

        // Get the parent category based on its ID
        private Category getParentCategory(int parentId) {
            for (Category cat : categories) {
                if (cat.getId() == parentId) {
                    return cat;
                }
            }
            return null;
        }
    }

}

