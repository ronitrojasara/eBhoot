package in.ebhoot.android.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import in.ebhoot.android.R;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextInputLayout userf = findViewById(R.id.email_login);
        TextInputLayout passf = findViewById(R.id.password_login);
        TextInputEditText username = findViewById(R.id.email_login_text);
        TextInputEditText password = findViewById(R.id.password_login_text);
        MaterialButton button = findViewById(R.id.login_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user =  username.getText().toString().trim();
                String pass =  password.getText().toString().trim();

                boolean noU = user.isEmpty();
                boolean noP = pass.isEmpty();

                if (noP || noU) {
                    if (noU) {
                        userf.setError("Please Enter Your Username or Email");
                    }
                    if (noP) {
                        passf.setError("Please Enter Your Password");
                    }
                }  else{
                    LoginManager.loginUser(LoginActivity.this, user, pass, new LoginCallback() {
                        @Override
                        public void onLoginSuccess() {
                            // Handle login success
                            findViewById(R.id.progress).setVisibility(View.GONE);
                            Snackbar.make(findViewById(R.id.login_btn), R.string.login_success, Snackbar.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onLoginFailure() {
                            // Handle login failure
                            findViewById(R.id.progress).setVisibility(View.GONE);
                            Snackbar.make(findViewById(R.id.login_btn), R.string.login_failed, Snackbar.LENGTH_INDEFINITE).setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Optional: Add action to handle login failure
                                }
                            }).show();
                        }
                    });

                    View view = getCurrentFocus();
                    if (view != null){
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                        findViewById(R.id.progress).setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    userf.setErrorEnabled(false);
                }
            }
        });
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    passf.setErrorEnabled(false);
                }
            }
        });

        MaterialButton materialButton = findViewById(R.id.login_signup);
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
    }
}
