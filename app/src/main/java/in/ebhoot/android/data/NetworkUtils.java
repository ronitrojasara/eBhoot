package in.ebhoot.android.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkUtils {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    // This method can be used to display a toast message indicating network status
    public static void showNetworkMessage(Context context, boolean isConnected) {
        if (!isConnected) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }
}
