package in.ebhoot.android.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;

public class TokenManager {

    private static final String PREF_NAME = "JWT_TOKEN";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_EXPIRATION_TIME = "expiration_time";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    private SharedPreferences sharedPreferences;

    Context context;

    public TokenManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isTokenExpired() {
        String token = getToken();
        if (token == null) {
            return true; // Token does not exist or has not been set
        }

        long expirationTime = getExpirationTime();
        if (expirationTime <= 0) {
            return true; // Expiration time not set or invalid
        }

        // Get the current time
        long currentTimeMillis = Calendar.getInstance().getTimeInMillis();

        // Check if the token has expired
        return currentTimeMillis >= expirationTime;
    }

    public void storeToken(String accessToken, long expirationTime) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putLong(KEY_EXPIRATION_TIME, expirationTime);
        editor.apply();
    }

    public void storeUsernameAndPassword(String username, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    private String getToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    private long getExpirationTime() {
        return sharedPreferences.getLong(KEY_EXPIRATION_TIME, 0);
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public String getPassword() {
        return sharedPreferences.getString(KEY_PASSWORD, null);
    }

    public void refreshTokenIfNeeded() {
        if (isTokenExpired()) {
            // Perform token refresh here
            Log.d("TokenManager", "Token expired, refreshing...");
            // Replace this with your token refresh logic
            // After successful token refresh, update the token and expiration time
            // For example:
            LoginManager.loginUser(context, getUsername(), getPassword(), new LoginCallback() {
                @Override
                public void onLoginSuccess() {
                    // Handle login success
                }

                @Override
                public void onLoginFailure() {
                    // Handle login failure
                }
            });
        } else {
            Log.d("TokenManager", "Token is still valid.");
        }
    }
}

