package me.peterjiang.testfinal;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.OpenChannelListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

//Open Chat Fragment

public class Fragment2 extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = Fragment2.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    LocationManager locationManager;
    private ListView mListView;
    private OpenChannelListQuery mChannelListQuery;
    private SendBirdChannelAdapter mAdapter;
    private boolean isTwoPane;
    private View rootView;
    private boolean mLocationPermissionGranted;
    private Location mCurrentLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
//    private Fragment2 mSendBirdChannelListFragment;
    private Activity myActivity;
    private HashMap<String, String> AllDistance;
    private SeekBar bar;
    private TextView bartext;
    private double range;
    private double initrange;

    public Fragment2() {
    }

    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fragment2, container, false);


        return rootView;
    }

    public void onConnected(Bundle connectionHint) {
        getDeviceLocation();
        AllDistance = new HashMap<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("OpenChannel");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    double lat = Double.parseDouble(i.child("latitude").getValue().toString());
                    double lng = Double.parseDouble(i.child("longitude").getValue().toString());
                    double distance = 0;
                    if(mCurrentLocation!=null) {
                        distance = getDistance(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), lat, lng);
                    }
                    distance = Math.floor(distance * 100) / 100;
                    AllDistance.put(i.getKey().toString(), String.valueOf(distance));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        mSendBirdChannelListFragment = new Fragment2();
        initUIComponents(rootView);
        mListView.setAdapter(mAdapter);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();

        mChannelListQuery = OpenChannel.createOpenChannelListQuery();
        mChannelListQuery.next(new OpenChannelListQuery.OpenChannelListQueryResultHandler() {
            @Override
            public void onResult(List<OpenChannel> channels, SendBirdException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (channels.size() <= 0) {
                    Toast.makeText(getActivity(), "No channels were found.", Toast.LENGTH_SHORT).show();
                } else {
                    mAdapter.addAll(channels);
                }
            }
        });

        bar = (SeekBar)rootView.findViewById(R.id.seekBar1); // make seekbar object
        bartext = (TextView)rootView.findViewById(R.id.seekbartext);
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
                  Fragment2 fragment2 = new Fragment2();
                fragment2.setSeekBar(range);
                getFragmentManager().beginTransaction().replace(R.id.openChatFragment, fragment2).addToBackStack("OpenChatList").commit();
            }
        });


    }

    public void setSeekBar(double i){
        initrange = i;
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

    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        //
        // updateMarkers();
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
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


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        myActivity = getActivity();

        Display display = myActivity.getWindowManager().getDefaultDisplay();
        isTwoPane = display.getWidth() > display.getHeight();


        FloatingActionButton fab = (FloatingActionButton) myActivity.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View viewCreate = myActivity.getLayoutInflater().inflate(R.layout.sendbird_view_open_create_channel, null);
                final EditText chName = (EditText) viewCreate.findViewById(R.id.etxt_chname);
                new AlertDialog.Builder(myActivity, R.style.MyDialogTheme)
                        .setView(viewCreate)
                        .setTitle("Create Open Channel")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                List<User> operators = new ArrayList<>();
                                operators.add(SendBird.getCurrentUser());

                                OpenChannel.createChannel(chName.getText().toString(), null, null, operators, new OpenChannel.OpenChannelCreateHandler() {
                                    @Override
                                    public void onResult(OpenChannel openChannel, SendBirdException e) {
                                        if (e != null) {
                                            Toast.makeText(myActivity, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        if (!mChannelListQuery.hasNext()) {
                                            mAdapter.add(openChannel);
                                        }

//                                        Intent intent = new Intent(SendBirdOpenChannelListActivity.this, SendBirdOpenChatActivity.class);
//                                        intent.putExtra("channel_url", openChannel.getUrl());
//                                        startActivity(intent);
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("OpenChannel");
                                        DatabaseReference openChannelinDB = ref.child(openChannel.getUrl());
                                        String name = openChannel.getName();
                                        String imageUrl = openChannel.getCoverUrl();
                                        getDeviceLocation();
                                        openChannelinDB.setValue(mCurrentLocation);
                                        openChannelinDB.child("name").setValue(name);
                                        openChannelinDB.child("cover").setValue(imageUrl);



                                        Fragmentopenchat fragmentopenchat = new Fragmentopenchat();
                                        fragmentopenchat.setURL(openChannel.getUrl());
                                        getFragmentManager().beginTransaction().replace(R.id.main_fragment, fragmentopenchat).addToBackStack( "OpenChat" ).commit();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", null).create().show();

//                mSettingsContainer.setVisibility(View.GONE);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });


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


    private void initUIComponents(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.list);
        mAdapter = new SendBirdChannelAdapter(getActivity(), AllDistance, initrange);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OpenChannel channel = mAdapter.getItem(position);
//                Intent intent = new Intent(getActivity(), SendBirdOpenChatActivity.class);
//                intent.putExtra("channel_url", channel.getUrl());
//                startActivity(intent);

                Fragmentopenchat fragmentopenchat = new Fragmentopenchat();
                fragmentopenchat.setURL(channel.getUrl());
                getFragmentManager().beginTransaction().replace(R.id.main_fragment, fragmentopenchat).addToBackStack( "Chat" ).commit();
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= (int) (totalItemCount * 0.8f)) {
                    loadMoreChannels();
                }
            }
        });


    }

    private void loadMoreChannels() {
        if (mChannelListQuery != null && mChannelListQuery.hasNext() && !mChannelListQuery.isLoading()) {
            mChannelListQuery.next(new OpenChannelListQuery.OpenChannelListQueryResultHandler() {
                @Override
                public void onResult(List<OpenChannel> channels, SendBirdException e) {
                    if (e != null) {
                        return;
                    }

                    mAdapter.addAll(channels);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        buildGoogleApiClient();
        mGoogleApiClient.connect();

    }

    public static class SendBirdChannelAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private final ArrayList<OpenChannel> mItemList;
        private HashMap<String, String> distanceInfo;
        private double range;

        public SendBirdChannelAdapter(Context context, HashMap<String, String> distanceInfo, double range) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mItemList = new ArrayList<>();
            this.distanceInfo = distanceInfo;
            this.range = range;
        }

        @Override
        public int getCount() {
            return mItemList.size();
        }

        @Override
        public OpenChannel getItem(int position) {
            return mItemList.get(position);
        }

        public void clear() {
            mItemList.clear();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void add(OpenChannel channel) {
            mItemList.add(channel);
            notifyDataSetChanged();
        }

//        public void addAll(Collection<OpenChannel> channels) {
//            mItemList.addAll(channels);
//            notifyDataSetChanged();
//        }

        public void addAll(Collection<OpenChannel> channels) {
            for(OpenChannel channel:channels){
                if(distanceInfo.get(channel.getUrl())!= null && Double.parseDouble(distanceInfo.get(channel.getUrl())) > range*1600){
                    Log.e(TAG, distanceInfo.get(channel.getUrl()));
                }
                else{
                    mItemList.add(channel);
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.sendbird_view_open_channel, parent, false);
                viewHolder.setView("img_thumbnail", convertView.findViewById(R.id.img_thumbnail));
                viewHolder.setView("txt_topic", convertView.findViewById(R.id.txt_topic));
                viewHolder.setView("txt_desc", convertView.findViewById(R.id.txt_desc));
                viewHolder.setView("txt_distance", convertView.findViewById(R.id.txt_distance));
                convertView.setTag(viewHolder);
            }

            OpenChannel item = getItem(position);

            viewHolder = (ViewHolder) convertView.getTag();
            Helper.displayUrlImage(viewHolder.getView("img_thumbnail", ImageView.class), item.getCoverUrl());
            viewHolder.getView("txt_topic", TextView.class).setText("#" + item.getName());
            viewHolder.getView("txt_desc", TextView.class).setText("" + item.getParticipantCount() + ((item.getParticipantCount() <= 1) ? " Member" : " Members"));
            if(distanceInfo.get(item.getUrl()) != null) {
                viewHolder.getView("txt_distance", TextView.class).setText( String.format( "%.2f miles", Double.parseDouble(distanceInfo.get(item.getUrl()))/1600));
            }
            return convertView;
        }

        private static class ViewHolder {
            private Hashtable<String, View> holder = new Hashtable<>();

            public void setView(String k, View v) {
                holder.put(k, v);
            }

            public View getView(String k) {
                return holder.get(k);
            }

            public <T> T getView(String k, Class<T> type) {
                return type.cast(getView(k));
            }
        }
    }
}