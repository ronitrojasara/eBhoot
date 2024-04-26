package in.ebhoot.android.auth;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginManager {

    public static void loginUser(Context context, String username, String password, LoginCallback callback) {
        String url = "https://ebhoot.in/wp-json/jwt-auth/v1/token";

        JSONObject postData = new JSONObject();
        try {
            postData.put("username", username);
            postData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle successful login
                        try {
                            String token = response.getString("token");

                            // Initialize TokenManager
                            TokenManager tokenManager = new TokenManager(context);
                            // Store username and password
                            tokenManager.storeUsernameAndPassword(username, password);
                            // Store token and its expiration time
                            tokenManager.storeToken(token, System.currentTimeMillis() + (60 * 60 * 1000 * 24 * 6)); // Expire in 1 week but setting 6 days


                            SessionManager sessionManager = new SessionManager(context);
                            sessionManager.setLoggedIn(true);

                            // Notify callback about login success
                            callback.onLoginSuccess();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle login error
                        callback.onLoginFailure();

                    }
                });

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }
}
