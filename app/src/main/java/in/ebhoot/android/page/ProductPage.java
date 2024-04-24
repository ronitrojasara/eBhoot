package in.ebhoot.android.page;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import in.ebhoot.android.R;
import in.ebhoot.android.data.Product;
import in.ebhoot.android.data.ProductsManager;

public class ProductPage extends AppCompatActivity {

    int id;
    String name;
    String imgurl;
    String category;
    String price;
    String regularPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(()->false);
        setContentView(R.layout.product_layout);
        MaterialButton materialButton = findViewById(R.id.close);
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
        Product product = getIntent().getParcelableExtra("product");
        if (product!=null){
            id = product.getId();
            imgurl = product.getImageUrl();
            name = product.getProductName();
            category = product.getCategoryName();
            price = product.getPrice();
            regularPrice = product.getRegularPrice();
        }
        TextView textView2 = findViewById(R.id.name);
        TextView textView3 = findViewById(R.id.name_top);
        textView2.setText(name);
        textView3.setText("ID-"+id+" | "+name);

        TextView textView = findViewById(R.id.price);
        TextView textView0 = findViewById(R.id.sale);
        TextView textView1 = findViewById(R.id.gst);

        Glide.with(this)
                .load(imgurl).into((ImageView) findViewById(R.id.image));
        String formattedPrice = String.format("%.2f",Float.parseFloat("0"+price) * 1.18);
        if (formattedPrice.contains(".00")){
            textView.setText("₹"+ formattedPrice.substring(0,formattedPrice.length()-3));
        }else{
            textView.setText("₹"+ formattedPrice);
        }
        if (regularPrice.equals(price)) {
            textView0.setVisibility(View.GONE);
        }else{
            String formattedSale = String.format("%.2f",Float.parseFloat("0"+regularPrice) * 1.18);
            if (formattedSale.contains(".00")){
                textView0.setText("₹"+ formattedSale.substring(0,formattedSale.length()-3));
            }else{
                textView0.setText("₹"+ formattedSale);
            }
        }
        String formattedGst = String.format("%.2f",Float.parseFloat("0"+price)* 0.18);
        if (formattedGst.contains(".00")){
            textView1.setText(" (₹"+price +" + ₹"+ formattedGst.substring(0,formattedGst.length()-3) +" GST)");
        }else{
            textView1.setText(" (₹"+price +" + ₹"+ formattedGst +" GST)");
        }

        new ProductsManager(this, new ProductsManager.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(JsonArray result) {
                if (result != null) {
                    try {
                        JsonObject jsonObject = result.get(0).getAsJsonObject();
                        String htmlString = jsonObject.get("description").getAsString();
                        Log.d("hello", htmlString);
                        WebView webView = findViewById(R.id.description);
                        webView.loadData(htmlString, "text/html", "UTF-8");
                    } catch (JsonParseException | IndexOutOfBoundsException e) {
                        Log.e("TAG", "Error parsing JSON" + result + "" + result.size(), e);
                    }
                }
            }
        }).fetchProduct(id);

    }

}
