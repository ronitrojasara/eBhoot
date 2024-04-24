package in.ebhoot.android.auth;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.Objects;

import in.ebhoot.android.R;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Instantiate the RegistrationManager
        RegistrationManager registrationManager = new RegistrationManager(this);

// Define the response and error listeners
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                findViewById(R.id.progress_reg).setVisibility(View.GONE);
                // Handle successful registration response
                Snackbar.make(findViewById(R.id.reg_btn),"Email Sent! Check your inbox for confirmation.",Snackbar.LENGTH_INDEFINITE).setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle registration error
                findViewById(R.id.progress_reg).setVisibility(View.GONE);
                Snackbar.make(findViewById(R.id.reg_btn), Objects.requireNonNull(error.getMessage()),Snackbar.LENGTH_INDEFINITE).setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
            }
        };

        TextInputLayout textInputLayout = findViewById(R.id.email_register);
        TextInputEditText textInputEditText = findViewById(R.id.email_reg_text);
// Call the registerUser method with the email
        MaterialButton materialButton = findViewById(R.id.reg_btn);
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = textInputEditText.getText().toString().trim();
                if (email.isEmpty()){
                    textInputLayout.setError("Please enter your email");
                }else{
                    registrationManager.registerUser(textInputEditText.getText().toString().trim(), listener, errorListener);
                    View view = getCurrentFocus();
                    if (view != null){
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                        findViewById(R.id.progress_reg).setVisibility(View.VISIBLE);
                    }
                }
            }

        });
        MaterialButton materialButton1 = findViewById(R.id.back_to_login);
        materialButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
