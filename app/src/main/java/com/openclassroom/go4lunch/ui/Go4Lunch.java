package com.openclassroom.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.messaging.FirebaseMessaging;
import com.openclassroom.go4lunch.BuildConfig;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.database.FirebaseHelper;
import com.openclassroom.go4lunch.models.DataUserConnected;
import com.openclassroom.go4lunch.models.DetailsPlaces;
import com.openclassroom.go4lunch.models.NearbyPlaces;
import com.openclassroom.go4lunch.models.Result;
import com.openclassroom.go4lunch.service.MyFirebaseMessagingService;
import com.openclassroom.go4lunch.ui.chat.ChatFragment;
import com.openclassroom.go4lunch.ui.detailsRestaurant.DetailRestaurantActivity;
import com.openclassroom.go4lunch.ui.drawerMenu.SettingFragment;
import com.openclassroom.go4lunch.ui.listRestaurant.RestaurantFragment;
import com.openclassroom.go4lunch.ui.map.MapFragment;
import com.openclassroom.go4lunch.ui.workmates.WorkmatesFragment;
import com.openclassroom.go4lunch.utils.Checks;
import com.openclassroom.go4lunch.utils.ConstantString;
import com.openclassroom.go4lunch.utils.RestaurantDetailFormat;
import com.openclassroom.go4lunch.utils.RetrofitStreams;
import com.openclassroom.go4lunch.utils.SecurityChecks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class Go4Lunch extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    private static final int RC_SIGN_IN = 123;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    public final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =1;
    private Location mLastKnownLocation;
    private NearbyPlaces nearbyLocations;
    private List<DetailsPlaces> detailsPlaces;
    TextView tvDrawerTvUserMail;
    TextView tvDrawerTvUserNameFirstname;
    CircleImageView ivDrawerImagerUser;
    private Disposable disposable;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if(!getSharedPreferences(ConstantString.NOTIFICATION_ENABLE, MODE_PRIVATE).contains(ConstantString.EATING)){
            FirebaseMessaging.getInstance().subscribeToTopic(ConstantString.TOPIG_TO_SUBSCRIBE);
            SharedPreferences.Editor editor = getSharedPreferences(ConstantString.NOTIFICATION_ENABLE, MODE_PRIVATE).edit();
            editor.putBoolean(ConstantString.EATING, false);
            editor.apply();
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            startSignInActivity();
        }else{
            MyFirebaseMessagingService.getToken(getApplicationContext());
            startActivity();
        }
        drawerLayout = findViewById(R.id.activity_main_drawer_layout);
    }




    private void startDefaultFragment(){

        if(getSupportActionBar() != null)
        getSupportActionBar().setTitle(R.string.hungry);

        Bundle bundle = new Bundle();

        bundle.putParcelable(ConstantString.LOCATION, mLastKnownLocation);
        bundle.putParcelable(ConstantString.NEARBY_LOCATION, nearbyLocations);

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
                        .setTheme(R.style.AppThemeFirebaseAuthentification)
                        .setLogo(R.drawable.eat)
                        .setAvailableProviders(
                                Arrays.asList( //EMAIL
                                        new AuthUI.IdpConfig.FacebookBuilder().build(),
                                        new AuthUI.IdpConfig.GoogleBuilder().build())) // SUPPORT GOOGLE
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            startActivity();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            Toast.makeText(this, getResources().getString(R.string.allow_location_permission), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getResources().getString(R.string.allow_location_services));
                builder.setPositiveButton(ConstantString.OK_LOWERCASE, (dialog, id) -> finish());
                builder.create();
                builder.show();
            }
        }
    }



    public void startActivity(){
        SecurityChecks.checkGooglePlayServices(getApplicationContext());
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        Places.initialize(this, BuildConfig.API_KEY);
        Places.createClient(this);
        bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);
        this.configureNavigationBottom();
        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();

        NavigationView navigationView = findViewById(R.id.activity_main_nav_view);
        View headerView = navigationView.getHeaderView(0);
        tvDrawerTvUserMail = headerView.findViewById(R.id.drawer_tv_user_mail);
        tvDrawerTvUserNameFirstname = headerView.findViewById(R.id.drawer_tv_user_name_firstname);
        ivDrawerImagerUser = headerView.findViewById(R.id.drawer_iv_image_user);
        tvDrawerTvUserNameFirstname.setText(user.getDisplayName());
        tvDrawerTvUserMail.setText(user.getEmail());
        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(ivDrawerImagerUser);
        getDeviceLocation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            this.handleResponseAfterSignIn(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
                    checkIfUserIsInDataBase();
                }
                getLocationPermission();
            }
        }
    }

    public void checkIfUserIsInDataBase(){

        FirebaseHelper.getUserData(user.getUid()).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                    if(task.getResult() != null && task.getResult().size() != 1)
                    createUserIntoDataBase();
            }
        });
    }

    public void createUserIntoDataBase(){
        FirebaseHelper.createUser(user.getUid(), user.getDisplayName(), Objects.requireNonNull(user.getPhotoUrl()).toString()).addOnCompleteListener(task -> Toast.makeText(getApplicationContext(), getResources().getString(R.string.account_created), Toast.LENGTH_SHORT).show());
    }

    private void showSnackBar(DrawerLayout drawerLayout, String message){
        Snackbar.make(drawerLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                showSnackBar(this.drawerLayout, getString(R.string.connection_secceded));
            }else{
                if(response == null){
                    finish();
                }else if(response.getError() != null && response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                    showSnackBar(this.drawerLayout, getString(R.string.error_no_internet));
                }else if(response.getError() != null && response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR){
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
                DocumentReference documentReference = FirebaseHelper.getUserDocument(user.getUid());
                Go4Lunch activity = this;
                documentReference.get().addOnCompleteListener(task -> {
                    DataUserConnected dataUserConnected = Objects.requireNonNull(task.getResult()).toObject(DataUserConnected.class);
                    if(dataUserConnected != null && dataUserConnected.getChoosenRestaurantForTheDay() != null && Checks.checkIfGoodDate(dataUserConnected.getDateOfTheChoosenRestaurant())){
                        int findInNearbyPlaceList = RestaurantDetailFormat.getPositionFromPlaceID(activity.getNearbyLocations(), dataUserConnected.getChoosenRestaurantForTheDay());
                        if(findInNearbyPlaceList != -1){
                            Result result = activity.getNearbyLocations().getResults().get(RestaurantDetailFormat.getPositionFromPlaceID(activity.getNearbyLocations(), dataUserConnected.getChoosenRestaurantForTheDay()));
                            Intent intent = new Intent(getApplicationContext(), DetailRestaurantActivity.class);
                            DetailsPlaces detailPlace = RestaurantDetailFormat.getDetailPlacesFromPlaceID(getDetailsPlaces(), dataUserConnected.getChoosenRestaurantForTheDay());
                            intent.putExtra(ConstantString.DETAIL_PLACE, detailPlace);
                            intent.putExtra(ConstantString.RESULT, result);
                            intent.putExtra(ConstantString.UNKNOW, false);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(getApplicationContext(), DetailRestaurantActivity.class);
                            DetailsPlaces detailPlace = RestaurantDetailFormat.getDetailPlacesFromPlaceID(getDetailsPlaces(), dataUserConnected.getChoosenRestaurantForTheDay());
                            intent.putExtra(ConstantString.UNKNOW, true);
                            startActivity(intent);
                        }
                    }else{
                        Toast.makeText(activity, getResources().getString(R.string.message_restaurant_not_choosen), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.activity_main_drawer_settings :
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingFragment()).commit();
                break;
            case R.id.activity_main_drawer_logout :
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(task -> {
                            startActivity(new Intent(Go4Lunch.this, Go4Lunch.class));
                            finish();
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

    private void configureNavigationView(){
        NavigationView navigationView = findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
    }

    private void configureNavigationBottom(){
        this.bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            if(menuItem.getItemId() == R.id.action_map){
                startDefaultFragment();
                return true;
            } else if (menuItem.getItemId() == R.id.action_restaurant){
                if(getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.hungry);
                RestaurantFragment restaurantFragment = new RestaurantFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(ConstantString.NEARBY_PLACES, nearbyLocations);
                bundle.putParcelableArrayList(ConstantString.DETAILS_PLACES, (ArrayList<? extends Parcelable>) detailsPlaces);
                bundle.putParcelable(ConstantString.LOCATION, mLastKnownLocation);
                restaurantFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, restaurantFragment).commit();
                return true;
            } else if (menuItem.getItemId() == R.id.action_workmate){
                if(getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.available_workmates);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WorkmatesFragment()).commit();
                return true;
            }else if(menuItem.getItemId() == R.id.action_chat){
                if(getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.chat);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatFragment()).commit();
                return true;
            }
            return false;
        });
    }

    private void executeHttpRequestWithRetrofit(String location){

         this.disposable = RetrofitStreams.getNearbyRestaurantThenFetchTheirDetails(
                location,BuildConfig.API_KEY).subscribeWith(new DisposableObserver<List<DetailsPlaces>>(){

            @Override
            public void onNext(List<DetailsPlaces> detailsPlaces) {
                Go4Lunch.this.detailsPlaces = detailsPlaces;
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_retrieving_data), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Go4Lunch.this.nearbyLocations = RetrofitStreams.nearbyPlaces;
                startDefaultFragment();
            }
        });
    }


    public void getDeviceLocation() {
        try {
            mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Go4Lunch.this.mLastKnownLocation = task.getResult();
                        if(task.getResult() != null){
                            executeHttpRequestWithRetrofit(task.getResult().getLatitude() + "," + task.getResult().getLongitude());
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_get_location), Toast.LENGTH_LONG).show();
                    }
                });

        } catch(SecurityException e)  {
            e.printStackTrace();
        }
    }

    public NearbyPlaces getNearbyLocations() {
        return nearbyLocations;
    }

    public List<DetailsPlaces> getDetailsPlaces(){
        return detailsPlaces;
    }

    public Location getLocation(){
        getDeviceLocation();
        return mLastKnownLocation;
    }

    public Location getLastKnownLocation(){
        return mLastKnownLocation;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        return false;
    }
}