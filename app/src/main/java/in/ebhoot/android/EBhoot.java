package in.ebhoot.android;

import com.google.android.material.color.DynamicColors;

public class EBhoot extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
