package in.ebhoot.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.splashscreen.SplashScreenViewProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import in.ebhoot.android.auth.SessionManager;
import in.ebhoot.android.auth.TokenManager;
import in.ebhoot.android.fragment.CategoriesFragment;
import in.ebhoot.android.fragment.HomeFragment;
import in.ebhoot.android.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    Fragment activeFragment;
    HomeFragment myFragment = new HomeFragment();
    CategoriesFragment categoriesFragment = new CategoriesFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition( () -> true);
        splashScreen.setOnExitAnimationListener(new SplashScreen.OnExitAnimationListener() {

                    public void onSplashScreenExit(@NonNull SplashScreenViewProvider splashScreenViewProvider) {
                        ObjectAnimator zoomX = ObjectAnimator.ofFloat(splashScreenViewProvider.getIconView(),
                                View.SCALE_X,
                                1.0f,
                                0.0f);
                        zoomX.setInterpolator(new OvershootInterpolator());
                        zoomX.setDuration(250L);
                        zoomX.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);

                                splashScreenViewProvider.remove();
                            }
                        });
                        ObjectAnimator zoomY = ObjectAnimator.ofFloat(splashScreenViewProvider.getIconView(),
                                View.SCALE_Y,
                                1.0f,
                                0.0f);
                        zoomY.setInterpolator(new OvershootInterpolator());
                        zoomY.setDuration(250L);
                        zoomY.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                splashScreenViewProvider.remove();
                            }
                        });
                        zoomX.start();
                        zoomY.start();

                    }
                });
new Handler().postDelayed(new Runnable() {
    @Override
    public void run() {
        splashScreen.setKeepOnScreenCondition( () -> false);
    }
},2000);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add a callback that's called when the splash screen is animating to the
        // app content.
        // Delay the transition to the main activity

        if (new SessionManager(this).isLoggedIn()){

            // Initialize TokenManager
            TokenManager tokenManager = new TokenManager(this);

            // Check if token is expired and refresh if needed
            tokenManager.refreshTokenIfNeeded();
        }



        // Check if the fragment exists in the FragmentManager
        myFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("HOME");
        categoriesFragment = (CategoriesFragment) getSupportFragmentManager().findFragmentByTag("CATE");
        profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("PRO");

        if (myFragment == null) {
            // If the fragment does not exist, create it and add it to the container
            myFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, myFragment, "HOME")
                    .commit();
            activeFragment = myFragment;
        }
        if (categoriesFragment == null){
            categoriesFragment = new CategoriesFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, categoriesFragment,"CATE")
                    .hide(categoriesFragment).commit();
        }

        if (profileFragment == null){
            profileFragment = new ProfileFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, profileFragment,"PRO")
                    .hide(profileFragment).commit();
        }


//        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, myFragment,"HOME").commit();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;


                switch (menuItem.getTitle().toString()) {
                    case "Home":
                        selectedFragment = myFragment;
                        break;
                    case "Categories":
                        selectedFragment = categoriesFragment;
                        break;
                    case "Dashboard":
                        selectedFragment = profileFragment;

                        break;
                }
                if (
                        activeFragment==null
                ){
                    activeFragment = selectedFragment;
                    getSupportFragmentManager().beginTransaction().show(activeFragment).commit();

                    if (activeFragment!=categoriesFragment){
                    getSupportFragmentManager().beginTransaction().hide(categoriesFragment).commit();}

                    if (activeFragment!=myFragment){
                        getSupportFragmentManager().beginTransaction().hide(myFragment).commit();}

                    if (profileFragment!=activeFragment){
                        getSupportFragmentManager().beginTransaction().hide(profileFragment).commit();
                    }
                }else
                if (selectedFragment != activeFragment && selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .hide(activeFragment).show(selectedFragment).commit();
                    activeFragment = selectedFragment;
                    FloatingActionButton fab = findViewById(R.id.floatab);
                    fab.hide();
                }
                return true;
            }

        });


    }
}