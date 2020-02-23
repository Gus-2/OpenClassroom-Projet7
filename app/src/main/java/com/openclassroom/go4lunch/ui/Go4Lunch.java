package com.openclassroom.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.models.DetailsPlaces;
import com.openclassroom.go4lunch.models.NearbyPlaces;
import com.openclassroom.go4lunch.ui.drawerMenu.SettingFragment;
import com.openclassroom.go4lunch.ui.drawerMenu.YourLunchFragment;
import com.openclassroom.go4lunch.ui.listRestaurant.RestaurantFragment;
import com.openclassroom.go4lunch.ui.map.MapFragment;
import com.openclassroom.go4lunch.ui.workmates.WorkmatesFragment;
import com.openclassroom.go4lunch.utils.RetrofitStreams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class Go4Lunch extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    private static final int RC_SIGN_IN = 123;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    // Responsible for fetching the current location of the device
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;
    private boolean mLocationPermissionGranted;
    public final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =1;
    private Location mLastKnownLocation;
    private NearbyPlaces nearbyLocations;
    private Disposable disposable;
    private List<DetailsPlaces> detailsPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLocationPermission();

        checkGooglePlayServices();

        startSignInActivity();

        drawerLayout = findViewById(R.id.activity_main_drawer_layout);

        bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Places.initialize(this, getResources().getString(R.string.map_key));
        placesClient = Places.createClient(this);

        this.configureNavigationBottom();

        this.configureToolBar();

        this.configureDrawerLayout();

        this.configureNavigationView();

        getDeviceLocation();

    }

    public NearbyPlaces getNearbyLocations() {
        return nearbyLocations;
    }

    public DetailsPlaces getDetailsPlaces(int position){
        return detailsPlaces.get(position);
    }

    public List<DetailsPlaces> getDetailsPlaces(){
        return detailsPlaces;
    }

    public Location getLocation(){
        getDeviceLocation();
        return mLastKnownLocation;
    }

    public boolean getLocationGranted(){
        return mLocationPermissionGranted;
    }

    private void startDefaultFragment(){
        Bundle bundle = new Bundle();

        bundle.putParcelable("Location", mLastKnownLocation);
        bundle.putBoolean("LocationGranted", mLocationPermissionGranted);
        bundle.putParcelable("NearbyLocation", nearbyLocations);

        MapFragment mapFragment = new MapFragment();
        mapFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mapFragment)
                .commitAllowingStateLoss();
    }

    private void startSignInActivity(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList( //EMAIL
                                        new AuthUI.IdpConfig.FacebookBuilder().build(),
                                        new AuthUI.IdpConfig.GoogleBuilder().build())) // SUPPORT GOOGLE
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    private void showSnackBar(DrawerLayout drawerLayout, String message){
        Snackbar.make(drawerLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){ // SUCCESS
                showSnackBar(this.drawerLayout, getString(R.string.connection_secceded));
            }else{ // ERRORS
                if(response == null){
                    showSnackBar(this.drawerLayout, getString(R.string.error_authentification_canceled));
                }else if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                    showSnackBar(this.drawerLayout, getString(R.string.error_no_internet));
                }else if(response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR){
                    showSnackBar(this.drawerLayout, getString(R.string.error_unknow_error));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.activity_main_drawer_lunch :
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new YourLunchFragment()).commit();
                break;
            case R.id.activity_main_drawer_settings :
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingFragment()).commit();
                break;
            case R.id.activity_main_drawer_logout :
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // user is now signed out
                                startActivity(new Intent(Go4Lunch.this, Go4Lunch.class));
                                finish();
                            }
                        });
                break;
            default:
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void configureToolBar(){
        this.toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
    }

    private void configureDrawerLayout(){
        this.drawerLayout = findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // 3 - Configure NavigationView
    private void configureNavigationView(){
        this.navigationView = findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
    }

    private void configureNavigationBottom(){
        this.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.action_map){
                    startDefaultFragment();
                    return true;
                } else if (menuItem.getItemId() == R.id.action_restaurant){
                    RestaurantFragment restaurantFragment = new RestaurantFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("NearbyPlaces", nearbyLocations);
                    bundle.putParcelableArrayList("DetailsPlaces", (ArrayList<? extends Parcelable>) detailsPlaces);
                    bundle.putParcelable("Location", mLastKnownLocation);
                    restaurantFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, restaurantFragment).commit();
                    return true;
                } else if (menuItem.getItemId() == R.id.action_workmate){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WorkmatesFragment()).commit();
                    return true;
                }
                return false;
            }
        });
    }

    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    private synchronized void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                mFusedLocationProviderClient.getLastLocation()
                        .addOnCompleteListener(new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                if (task.isSuccessful()) {
                                    Go4Lunch.this.mLastKnownLocation = task.getResult();
                                    executeHttpRequestWithRetrofit(task.getResult().getLatitude() + "," + task.getResult().getLongitude());
                                } else {
                                    Toast.makeText(getApplicationContext(), "Veuillez autoriser la locations de l'appareil via les paramètres", Toast.LENGTH_LONG);
                                }
                            }
                        });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }

    }


    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    private void executeHttpRequestWithRetrofit(String location){

        this.disposable = RetrofitStreams.getNearbyRestaurantThenFetchTheirDetails(
                location,getResources().getString(R.string.map_key)).subscribeWith(new DisposableObserver<List<DetailsPlaces>>(){

            @Override
            public void onNext(List<DetailsPlaces> detailsPlaces) {
                Go4Lunch.this.detailsPlaces = detailsPlaces;
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Go4Lunch.this.nearbyLocations = RetrofitStreams.nearbyPlaces;
                startDefaultFragment();
            }
        });
    }


}