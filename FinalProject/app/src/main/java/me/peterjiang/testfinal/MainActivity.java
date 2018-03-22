package me.peterjiang.testfinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    public static final String TAG = "MainActivity";
    // The desired interval for location updates. Inexact. Updates may be more or less frequent.
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    // The fastest rate for active location updates. Exact. Updates will never be more frequent
    // than this value.
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    /**
     * To test push notifications with your own appId, you should replace google-services.json with yours.
     * Also you need to set Server API Token and Sender ID in SendBird dashboard.
     * Please carefully read "Push notifications" section in SendBird Android documentation
     */
    private static final String appId = "EA7935EB-565B-400A-A23A-98FD1E9CC3EE"; /* Sample SendBird Application */
    public static String sUserId;
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    // The geographical location where the device is currently located.
    public Location mCurrentLocation;
    public String mNickname;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient mGoogleApiClient;
    // A request object to store parameters for requests to the FusedLocationProviderApi.
    private LocationRequest mLocationRequest;
    private boolean mLocationPermissionGranted;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private Toolbar toolbar;
    private boolean isTwoPane;


    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        createLocationRequest();
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Play services connection suspended");
    }


    public void onConnected(Bundle connectionHint) {
        getDeviceLocation();
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseHelper user_cus = new FirebaseHelper();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        Display display = getWindowManager().getDefaultDisplay();
        isTwoPane = display.getWidth() > display.getHeight();


        sUserId = getPreferences(Context.MODE_PRIVATE).getString("user_id", "");
        mNickname = getPreferences(Context.MODE_PRIVATE).getString("nickname", "");

        SendBird.init(appId, this);

        if(user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users"); // What database can I actually talk to?
            DatabaseReference Users = ref.child(user.getUid());
            Users.child("Screenname").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
//                    while(snapshot.getValue() == null) {
                    if (snapshot.getValue() != null) {
                        mNickname = snapshot.getValue().toString();
                    }
//                    }
//                    mNickname = snapshot.getValue().toString();

//                    else{
//                        mNickname = "Unknown";
//                    }

                }
                @Override public void onCancelled(DatabaseError error) { }
            });
            sUserId = user.getUid().toString();
//            if(user.getDisplayName() != null) {
            mNickname = user.getDisplayName();
//            }
//            else{
//                mNickname = "Unknown";
//            }
            connect();

            Users.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot snapshot, String s) {
//                    if(snapshot.child("Screenname").getValue() != null) {
//                        mNickname = snapshot.child("Screenname").getValue().toString();
//                        Log.e(TAG + " added", mNickname);
//                        disconnect();
//                        connect();
//                    }
//                    else{
//                        Log.e("UsersAdded", snapshot.getValue().toString());
//                    }
////                    else{
////                        mNickname = "Unknown";
////                    }
                }
                @Override
                public void onChildRemoved(DataSnapshot snapshot) {

                }
                @Override
                public void onChildChanged(DataSnapshot snapshot, String s) {
//                    if(snapshot.child("Screenname").getValue() != null) {
//                        mNickname = snapshot.child("Screenname").getValue().toString();
//                        Log.e(TAG+" changed",mNickname);
//                        disconnect();
//                        connect();
//                    }
//                    else{
//                        Log.e("UsersChanges", snapshot.getValue().toString());
//                    }
//                    mNickname = snapshot.getValue().toString();
////                    mNickname = user.getDisplayName();
//                    disconnect();
//                    connect();
//                    else{
//                        mNickname = "Unknown";
//                    }

                }
                @Override
                public void onChildMoved(DataSnapshot snapshot, String s) {

                }
                @Override
                public void onCancelled(DatabaseError databaseError){

                }
            });

        }



        if(!isTwoPane){
            toolbar = (Toolbar) this.findViewById(R.id.customToolbar);
            setSupportActionBar(toolbar);
//            Fragment2 fragment2 = new Fragment2();
//            getFragmentManager().beginTransaction().replace(R.id.main_fragment, fragment2).commit();
            Fragment1 fragment1 = new Fragment1();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, fragment1).commit();
//            Log.e(TAG,"fragment 2 replaced");
        }
        auth.addAuthStateListener(authListener);

    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        /*
         * Sets the desired interval for active location updates. This interval is
         * inexact. You may not receive updates at all if no location sources are available, or
         * you may receive them slower than requested. You may also receive updates faster than
         * requested if other applications are requesting location at a faster interval.
         */
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        /*
         * Sets the fastest rate for active location updates. This interval is exact, and your
         * application will never receive updates faster than this value.
         */
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void getDeviceLocation() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         * Also request regular updates about the device location.
         */
        if (mLocationPermissionGranted) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }


        // Build the Play services client for use by the Fused Location Provider and the Places API.
        buildGoogleApiClient();
        mGoogleApiClient.connect();


        //get firebase auth instance


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); // reads XML
        inflater.inflate(R.menu.menu_main, menu); // to create
        return super.onCreateOptionsMenu(menu); // the menu


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.profile) {
            Fragment1 fragment1 = new Fragment1();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, fragment1).commit();

        }
        else if (item.getItemId() == R.id.friend) {
            //friend & 1-1 chat
            Fragment4 fragment4 = new Fragment4();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, fragment4).addToBackStack("Friend").commit();

        }
        else if (item.getItemId() == R.id.group) {
            //group chat
//            Intent intent = new Intent(MainActivity.this, SendBirdGroupChannelListActivity.class);
//            startActivity(intent);

            Fragment3 fragment3 = new Fragment3();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, fragment3).addToBackStack("GroupChat").commit();

        }
        else if (item.getItemId() == R.id.event) {
//            //Event Page
            Fragment5 fragment5 = new Fragment5();
            fragment5.setSeekBar(10);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, fragment5).addToBackStack("Event").commit();
        }
//        else if (item.getItemId() == R.id.event) {
////            //Event Page
//            FragmentCreateEvent fragmentCreateEvent = new FragmentCreateEvent();
//            getFragmentManager().beginTransaction().replace(R.id.main_fragment, fragmentCreateEvent).addToBackStack( "Event" ).commit();
//        }
        else{
            //Openchat Here
            //  Fragment2 fragment2 = new Fragment2();
            // getFragmentManager().beginTransaction().replace(R.id.main_fragment, fragment2).addToBackStack( "OpenChat" ).commit();
            OpenChatFragment openChatFragment = new OpenChatFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, openChatFragment).commit();
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            getDeviceLocation();
        }
    }

  /*  @Override
    public void onStart() {
        super.onStart();

    }*/

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
            //disconnect();
        }
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    private void setState(State state) {
        switch (state) {
            case DISCONNECTED:
//                ((Button) findViewById(R.id.btn_connect)).setText("Connect");
//                findViewById(R.id.btn_connect).setEnabled(true);
//                findViewById(R.id.btn_open_channel_list).setEnabled(false);
//                findViewById(R.id.btn_group_channel_list).setEnabled(false);
                break;

            case CONNECTING:
//                ((Button) findViewById(R.id.btn_connect)).setText("Connecting...");
//                findViewById(R.id.btn_connect).setEnabled(false);
//                findViewById(R.id.btn_open_channel_list).setEnabled(false);
//                findViewById(R.id.btn_group_channel_list).setEnabled(false);
                Toast.makeText(this, "Connecting... ", Toast.LENGTH_SHORT).show();
                break;

            case CONNECTED:
//                ((Button) findViewById(R.id.btn_connect)).setText("Disconnect");
//                findViewById(R.id.btn_connect).setEnabled(true);
//                findViewById(R.id.btn_open_channel_list).setEnabled(true);
//                findViewById(R.id.btn_group_channel_list).setEnabled(true);
                Toast.makeText(this, "Connected! ", Toast.LENGTH_SHORT).show();

                break;
        }
    }

    public void connect() {
        SendBird.connect(sUserId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (e != null) {
                    Toast.makeText(MainActivity.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    setState(State.DISCONNECTED);
                    return;
                }

                String nickname = mNickname;

                SendBird.updateCurrentUserInfo(nickname, null, new SendBird.UserInfoUpdateHandler() {
                    @Override
                    public void onUpdated(SendBirdException e) {
                        if (e != null) {
                            Toast.makeText(MainActivity.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            setState(State.DISCONNECTED);
                            return;
                        }

                        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
                        editor.putString("user_id", sUserId);
                        editor.putString("nickname", mNickname);
                        editor.commit();

                        setState(State.CONNECTED);
                        Log.e(TAG, "SendBirdConnect Successful");
                    }
                });

                if (FirebaseInstanceId.getInstance().getToken() == null) return;

                SendBird.registerPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(), new SendBird.RegisterPushTokenWithStatusHandler() {
                    @Override
                    public void onRegistered(SendBird.PushTokenRegistrationStatus pushTokenRegistrationStatus, SendBirdException e) {
                        if (e != null) {
                            Toast.makeText(MainActivity.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
            }
        });

        setState(State.CONNECTING);
    }

    public void disconnect() {
        SendBird.disconnect(new SendBird.DisconnectHandler() {
            @Override
            public void onDisconnected() {
                setState(State.DISCONNECTED);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private enum State {DISCONNECTED, CONNECTING, CONNECTED}

//    @Override
//    public void onClick(View view) {
//        FragmentCreateEvent.myClickMethod(view);
//    }
}