package me.peterjiang.testfinal;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment5 extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    static List<Map<String, String>> listdata;
    static List<Map<String, String>> listdata2;
    //    private ArrayList<String> fireBaseListEID;
    EventAdapter adapter;
    EventAdapter adapter2;
    //event list
    private Activity myActivity;
    private View inflatedView = null;
//    private ArrayList<String> fireBaseList;
//    private ArrayAdapter adapter;
    private HashMap<String, EventObject> Event;
    private ListView EventList;
    private ListView EventList2;
    private String TAG = "EVENTPAGE";
    private boolean mLocationPermissionGranted;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private SeekBar bar;
    private TextView bartext;
    private double range;
    private double initrange;


    public Fragment5() {
        // Required empty public constructor
    }

    private void getDeviceLocation() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
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

    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Play services connection suspended");
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

    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        //
        // updateMarkers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflatedView = inflater.inflate(R.layout.fragment_fragment5, container, false);
        return inflatedView;
    }


    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }


    public void onConnected(Bundle connectionHint) {
        getDeviceLocation();
        adapter = new EventAdapter(myActivity, listdata, mCurrentLocation);
        EventList.setAdapter(adapter);
        adapter2 = new EventAdapter(myActivity, listdata2, mCurrentLocation);
        EventList2.setAdapter(adapter2);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events"); // What database can I actually talk to?

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, "onChildAdded");

                if (dataSnapshot.child("EID").getValue() != null &&
                        dataSnapshot.child("fromDate").getValue() != null &&
                        dataSnapshot.child("toDate").getValue() != null &&
                        dataSnapshot.child("Name").getValue() != null &&
                        dataSnapshot.child("Owner").getValue() != null &&
                        dataSnapshot.child("fromTime").getValue() != null &&
                        dataSnapshot.child("toTime").getValue() != null &&
                        dataSnapshot.child("Place").getValue() != null &&
                        dataSnapshot.child("Desc").getValue() != null &&
                        dataSnapshot.child("longitude").getValue() != null &&
                        dataSnapshot.child("latitude").getValue() != null) {
//                    fireBaseList.add(0, dataSnapshot.child("Name").getValue().toString());
//                    fireBaseListEID.add(0,dataSnapshot.child("EID").getValue().toString());
                    int eventflag = 0;
                    Calendar c = Calendar.getInstance();

                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);

                    Date today = c.getTime();

                    String myStrDate = dataSnapshot.child("toDate").getValue().toString();
                    Date eventDate;
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                    try {
                        eventDate = format.parse(myStrDate);
                        System.out.println(eventDate);
                        if (eventDate.before(today)) {
                            eventflag = 1;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    EventObject addEvent = new EventObject(dataSnapshot.child("EID").getValue().toString(),
                            dataSnapshot.child("Name").getValue().toString(),
                            dataSnapshot.child("Owner").getValue().toString(),
                            dataSnapshot.child("fromDate").getValue().toString(),
                            dataSnapshot.child("toDate").getValue().toString(),
                            dataSnapshot.child("fromTime").getValue().toString(),
                            dataSnapshot.child("toTime").getValue().toString(),
                            dataSnapshot.child("Place").getValue().toString(),
                            dataSnapshot.child("Desc").getValue().toString());
                    addEvent.longitude = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
                    addEvent.latitude = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                    GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {
                    };

                    if (dataSnapshot.child("RSVP_attend").getValue(t) != null) {
                        addEvent.RSVP_attend = dataSnapshot.child("RSVP_attend").getValue(t);
                    }
                    if (dataSnapshot.child("RSVP_not").getValue(t) != null) {
                        addEvent.RSVP_not = dataSnapshot.child("RSVP_not").getValue(t);
                    }

                    Log.e(TAG, Double.toString(getDistance(addEvent.latitude, addEvent.longitude,
                            mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())));

                    if(mCurrentLocation == null ||
                            mCurrentLocation.getLatitude() == 0.0 || mCurrentLocation.getLongitude() == 0.0 ||
                            getDistance(addEvent.latitude, addEvent.longitude,
                            mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()) > initrange*1600) {

                    }
                    else {
                        Event.put(dataSnapshot.child("EID").getValue().toString(), addEvent);
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("EventName", dataSnapshot.child("Name").getValue().toString());
                        map.put("EventDate", dataSnapshot.child("fromDate").getValue().toString());
                        map.put("EID", dataSnapshot.child("EID").getValue().toString());
                        map.put("latitude", dataSnapshot.child("latitude").getValue().toString());
                        map.put("longitude", dataSnapshot.child("longitude").getValue().toString());
                        if (eventflag == 1) {
                            listdata2.add(0, map);
                        } else {
                            listdata.add(0, map);
                        }
//                Event.put(dataSnapshot.child("eid").getValue().toString(), dataSnapshot.getValue(EventObject.class));
//                EventObject test = dataSnapshot.getValue(EventObject.class);
//                Log.e(TAG,test.getName());
////                Log.e(TAG,dataSnapshot.child("eid").getValue().toString());
////                Log.e(TAG,dataSnapshot.getValue(EventObject.class).toString());
////                Log.e(TAG, test.getName());
//                fireBaseList.add(0, dataSnapshot.child("name").getValue().toString());
//                fireBaseListEID.add(0,dataSnapshot.child("eid").getValue().toString());
                        adapter.notifyDataSetChanged();
                    }

                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        bar = (SeekBar)inflatedView.findViewById(R.id.seekBar1); // make seekbar object
        bartext = (TextView)inflatedView.findViewById(R.id.seekbartext);
        bar.setProgress((int)initrange*10);
        bartext.setText( initrange + " mi" );
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                range = i / 10;
                bartext.setText( range + " mi" );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Fragment5 fragment5 = new Fragment5();
                fragment5.setSeekBar(range);
                getFragmentManager().beginTransaction().replace(R.id.main_fragment, fragment5).addToBackStack("OpenChatList").commit();
            }
        });


    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        myActivity = getActivity();

//        fireBaseList = new ArrayList<>();
//        fireBaseListEID = new ArrayList<>();
        Event = new HashMap<>();
        listdata = new ArrayList<>();
        listdata2 = new ArrayList<>();
        // adapter = new EventAdapter(myActivity,listdata);
        EventList = (ListView) inflatedView.findViewById(R.id.eventlist);


        EventList2 = (ListView) inflatedView.findViewById(R.id.eventlist2);


        EventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                Toast.makeText(myActivity, "Stop Clicking me", Toast.LENGTH_SHORT).show();
                FragmentEventDetail fragmentEventDetail = new FragmentEventDetail();
//                String name = fireBaseList.get(position);
//                String eid = fireBaseListEID.get(position);
                String eid = listdata.get(position).get("EID");
                fragmentEventDetail.getevent(Event.get(eid));
//                Log.e(TAG,"EVENT EID: "+ eid);
//                Log.e(TAG,Event.get(eid).getName());
                getFragmentManager().beginTransaction().replace(R.id.main_fragment, fragmentEventDetail).addToBackStack("Event").commit();
            }
        });

        EventList2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                Toast.makeText(myActivity, "Stop Clicking me", Toast.LENGTH_SHORT).show();
                FragmentEventDetail fragmentEventDetail = new FragmentEventDetail();
//                String name = fireBaseList.get(position);
//                String eid = fireBaseListEID.get(position);
                String eid = listdata2.get(position).get("EID");
                fragmentEventDetail.getevent(Event.get(eid));
                fragmentEventDetail.setExpired(true);
//                Log.e(TAG,"EVENT EID: "+ eid);
//                Log.e(TAG,Event.get(eid).getName());
                getFragmentManager().beginTransaction().replace(R.id.main_fragment, fragmentEventDetail).addToBackStack("Event").commit();
            }
        });



        FloatingActionButton fab = (FloatingActionButton) myActivity.findViewById(R.id.eventfab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentCreateEvent fragmentCreateEvent = new FragmentCreateEvent();
                getFragmentManager().beginTransaction().replace(R.id.main_fragment, fragmentCreateEvent).addToBackStack("Event").commit();

            }
        });



    }

    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }

    public void setSeekBar(double i){
        initrange = i;
    }

}
