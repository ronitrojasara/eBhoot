package in.ebhoot.android.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import in.ebhoot.android.R;
import in.ebhoot.android.data.Product;
import in.ebhoot.android.page.ProductPage;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder> {
    private final List<Product> productList; // Assuming Product is a class representing your data model

    Activity activity;
    public ProductsAdapter(Activity activity, List<Product> productList) {
        this.activity = activity;
        this.productList = productList;
        // Populate dataList with your data here
        // For example:
//        productList.add(new Product("https://ebhoot.in/wp-content/uploads/2023/08/Untitled-design-2023-08-11T220323.261-300x300.png", "Category 1", "Product 1", "$10.00"));
//        productList.add(new Product("https://ebhoot.in/wp-content/uploads/2023/08/Untitled-design-2023-08-11T220323.261-300x300.png", "Category 2", "Product 2", "$20.00"));


    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductsViewHolder(view,activity);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product,activity);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductsViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView categoryTextView;
        private final TextView productNameTextView;
        private final TextView priceTextView;
        private final TextView rpriceTextView;

        public ProductsViewHolder(@NonNull View itemView,Activity activity) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view);
            categoryTextView = itemView.findViewById(R.id.category_text_view);
            productNameTextView = itemView.findViewById(R.id.product_name_text_view);
            priceTextView = itemView.findViewById(R.id.price_text_view);
            rpriceTextView = itemView.findViewById(R.id.price_text_view_2);
            // Set item view as square
            itemView.post(new Runnable() {
                @Override
                public void run() {
                    itemView.getLayoutParams().height = (int) (itemView.getWidth() * 1.5); // Set height equal to width
                    imageView.getLayoutParams().height = (int) (itemView.getWidth() - (10 * itemView.getResources().getDisplayMetrics().density));
                    imageView.getLayoutParams().width = (int) (itemView.getWidth() - (10 * itemView.getResources().getDisplayMetrics().density));
                }
            });

        }
        public void bind(Product product,Activity activity) {

            // Load image using Glide or any other image loading library
            Glide.with(itemView.getContext())
                    .load(product.getImageUrl()).into(imageView);
            categoryTextView.setText(product.getCategoryName());
            productNameTextView.setText(product.getProductName());
            String formattedPrice = String.format("%.2f",Float.parseFloat("0"+product.getPrice()) * 1.18);
            if (formattedPrice.contains(".00")){
                priceTextView.setText(formattedPrice.substring(0, formattedPrice.length() - 3));
            }else{

                priceTextView.setText(formattedPrice);
            }
           if (product.getRegularPrice().equals(product.getPrice())){
                rpriceTextView.setVisibility(View.GONE);
            }else {
               String formatedSale = String.format("%.2f",Float.parseFloat("0"+product.getRegularPrice()) * 1.18);
               if (formatedSale.contains(".00")){
                   rpriceTextView.setText(formatedSale.substring(0,formatedSale.length() - 3));
               }else{
                   rpriceTextView.setText(formatedSale);
               }
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, ProductPage.class).putExtra("product",product));
                    activity.overridePendingTransition(android.R.anim.slide_in_left, 0);
                }
            });
        }
        }

    }

