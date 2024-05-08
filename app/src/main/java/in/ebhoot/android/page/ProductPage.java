package in.ebhoot.android.page;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    int stock;
    boolean s_manage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> false);
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
        if (product != null) {
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
        textView3.setText("ID-" + id + " | " + name);

        TextView textView = findViewById(R.id.price);
        TextView textView0 = findViewById(R.id.sale);
        TextView textView1 = findViewById(R.id.gst);

        Glide.with(this)
                .load(imgurl).into((ImageView) findViewById(R.id.image));
        String formattedPrice = String.format("%.2f", Float.parseFloat("0" + price) * 1.18);
        if (formattedPrice.contains(".00")) {
            textView.setText("₹" + formattedPrice.substring(0, formattedPrice.length() - 3));
        } else {
            textView.setText("₹" + formattedPrice);
        }
        if (regularPrice.equals(price)) {
            textView0.setVisibility(View.GONE);
        } else {
            String formattedSale = String.format("%.2f", Float.parseFloat("0" + regularPrice) * 1.18);
            if (formattedSale.contains(".00")) {
                textView0.setText("₹" + formattedSale.substring(0, formattedSale.length() - 3));
            } else {
                textView0.setText("₹" + formattedSale);
            }
        }
        String formattedGst = String.format("%.2f", Float.parseFloat("0" + price) * 0.18);
        if (formattedGst.contains(".00")) {
            textView1.setText(" (₹" + price + " + ₹" + formattedGst.substring(0, formattedGst.length() - 3) + " GST)");
        } else {
            textView1.setText(" (₹" + price + " + ₹" + formattedGst + " GST)");
        }

        EditText editText = findViewById(R.id.q);
        MaterialButton materialButton1 = findViewById(R.id.min);
        MaterialButton materialButton2 = findViewById(R.id.max);
        new ProductsManager(this, new ProductsManager.OnTaskComplete() {
            @Override
            public void onTaskCompleted(JsonObject result) {
                findViewById(R.id.load).setVisibility(View.GONE);
                if (result != null) {
                    try {
                        String htmlString = result.get("description").getAsString();
                        TextView stockview = findViewById(R.id.stock);
                        stockview.setVisibility(View.VISIBLE);

                        if (result.get("stock_status").getAsString().equals("instock")){
                            s_manage = result.get("manage_stock").getAsBoolean();
                        if(s_manage){
                             stock = result.get("stock_quantity").getAsInt();
                             stockview.setText(""+stock+"\nin stock");
                        }else {
                            stockview.setText("Available\nin stock");
                        }
                        }else{
                            stockview.setTextColor(Color.RED);
                            stockview.setText("0\nsold out");

                            findViewById(R.id.to_cart).setEnabled(false);
                            editText.setVisibility(View.GONE);
                            materialButton1.setVisibility(View.GONE);
                            materialButton2.setVisibility(View.GONE);
                        }
                        Log.d("hello", htmlString);
                        WebView webView = findViewById(R.id.description);
                        webView.loadData(htmlString, "text/html", "UTF-8");
                    } catch (JsonParseException | IndexOutOfBoundsException e) {
                        Log.e("TAG", "Error parsing JSON" + result + "" + result.size(), e);
                    }
                }
            }
        }).fetchProduct(id);


        final int[] q = {1};
        materialButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                q[0] = Integer.parseInt("0"+editText.getText().toString());
                q[0]--;
                editText.setText(""+q[0]);
            }
        });
        materialButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                q[0] = Integer.parseInt("0"+editText.getText().toString());
                q[0]++;
                editText.setText(""+q[0]);
            }

        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (Integer.parseInt("0"+s) == 1){
                    materialButton1.setEnabled(false);
                }else {
                    if (!materialButton1.isEnabled()){
                        materialButton1.setEnabled(true);
                    }
                }

                if (s_manage) {
                    if (Integer.parseInt("0" + s) >= stock) {
                        materialButton2.setEnabled(false);
                    } else {
                        if (!materialButton2.isEnabled()) {
                            materialButton2.setEnabled(true);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Integer.parseInt("0"+s.toString()) <= 0 ){
                    editText.setText("1");
                }

                if (!s.toString().equals("" + Integer.parseInt("0" + s.toString()))){
                    editText.setText(""+Integer.parseInt("0" + s.toString()));
                }
                if (s_manage){
                if (Integer.parseInt("0"+s) > stock){
                    editText.setText(""+stock);
                }
                }
            }
        });
    }

}
