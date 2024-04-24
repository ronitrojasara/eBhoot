package in.ebhoot.android.auth;

import android.content.Context;
import android.content.res.Resources;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.ebhoot.android.R;

public class RegistrationManager {

    private static final String TAG = "RegistrationManager";
    private static final String URL = "https://ebhoot.in/wp-json/wc/v3/customers";

    private Context context;
    private RequestQueue requestQueue;

    public RegistrationManager(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public void registerUser(String email, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL, postData, listener, errorListener) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String credentials = Resources.getSystem().getString(R.string.consumer_key) + ":" + Resources.getSystem().getString(R.string.consumer_secret);
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", auth);
                return headers;
            }
        };

        // Add the request to the RequestQueue.
        requestQueue.add(jsonObjectRequest);
    }
}

