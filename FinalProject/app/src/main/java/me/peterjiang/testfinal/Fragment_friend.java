package me.peterjiang.testfinal;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserListQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_friend extends Fragment {
    private ListView mListView;
    private UserListQuery mUserListQuery;
    private SendBirdFriendAdapter mAdapter;
    private HashSet<String> mSelectedUserIds;
    private boolean isTwoPane;
    private MainActivity myActivity;
    private View rootView;
    private HashMap<String, String> AllDistance;
    private Location mLocation;
    public Fragment_friend() {
        // Required empty public constructor
    }


    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        myActivity = (MainActivity) getActivity();
        mLocation = myActivity.mCurrentLocation;
        AllDistance = new HashMap<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    if (i.child("latitude").getValue() != null && i.child("longitude").getValue() != null) {
                        double lat = Double.parseDouble(i.child("latitude").getValue().toString());
                        double lng = Double.parseDouble(i.child("longitude").getValue().toString());
                        double distance = getDistance(mLocation.getLatitude(), mLocation.getLongitude(), lat, lng);
                        distance = Math.floor(distance * 100) / 100;
                        AllDistance.put(i.getKey().toString(), String.valueOf(distance));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        initUIComponents(rootView);
        mUserListQuery = SendBird.createUserListQuery();
        loadMoreUsers();
        Display display = myActivity.getWindowManager().getDefaultDisplay();
        isTwoPane = display.getWidth() > display.getHeight();

        FloatingActionButton fab = (FloatingActionButton) myActivity.findViewById(R.id.fab_friend);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] userIds = mSelectedUserIds.toArray(new String[0]);
                if (userIds.length > 0) {
                    View cur_view = getActivity().getLayoutInflater().inflate(R.layout.sendbird_view_group_create_channel, null);
                    final EditText chName = (EditText) cur_view.findViewById(R.id.etxt_chname_group);
//                    final EditText chName = (EditText) view.findViewById(R.id.etxt_chname);
//                    final CheckBox distinct = (CheckBox) view.findViewById(R.id.chk_distinct_user);

                    new AlertDialog.Builder(getActivity())
                            .setView(cur_view)
                            .setTitle("Create Chat now.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                     GroupChannel.createChannelWithUserIds(Arrays.asList(userIds), false, chName.getText().toString(), null, null, new GroupChannel.GroupChannelCreateHandler() {
                                        @Override
                                        public void onResult(GroupChannel groupChannel, SendBirdException e) {
                                            if (e != null) {
                                                Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            Fragmentgroupchat fragment = new Fragmentgroupchat();
                                            fragment.setURL(groupChannel.getUrl(), groupChannel);
                                            getFragmentManager().beginTransaction().replace(R.id.main_fragment, fragment).addToBackStack( "GroupChat" ).commit();
//                                            //mAdapter.replace(groupChannel);
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Cancel", null).create().show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fragment_friend, container, false);

        return rootView;
    }


    private void initUIComponents(View rootView) {
        mSelectedUserIds = new HashSet<>();
        mListView = (ListView) rootView.findViewById(R.id.list_firend);
        mAdapter = new SendBirdFriendAdapter(getActivity(), AllDistance);



        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= (int) (totalItemCount * 0.8f)) {
                    loadMoreUsers();
                }
            }
        });
        mListView.setAdapter(mAdapter);
    }
    private void loadMoreUsers() {
        if (mUserListQuery != null && mUserListQuery.hasNext() && !mUserListQuery.isLoading()) {
            mUserListQuery.next(new UserListQuery.UserListQueryResultHandler() {
                @Override
                public void onResult(List<User> list, SendBirdException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mAdapter.addAll(list);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }



    public class SendBirdFriendAdapter extends BaseAdapter {
        private final Context mContext;
        private final LayoutInflater mInflater;
        private final ArrayList<User> mItemList;
        HashMap<String, String> distanceInfo;

        public SendBirdFriendAdapter(Context context, HashMap<String, String> distanceInfo) {
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mItemList = new ArrayList<>();
            this.distanceInfo = distanceInfo;
        }

        @Override
        public int getCount() {
            return mItemList.size();
        }

        @Override
        public User getItem(int position) {
            return mItemList.get(position);
        }

        public void clear() {
            mItemList.clear();
        }

        public User remove(int index) {
            return mItemList.remove(index);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addAll(Collection<User> users) {
            mItemList.addAll(users);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SendBirdFriendAdapter.ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new SendBirdFriendAdapter.ViewHolder();

                convertView = mInflater.inflate(R.layout.sendbird_view_user, parent, false);
                viewHolder.setView("root_view", convertView);
                viewHolder.setView("img_thumbnail", convertView.findViewById(R.id.img_thumbnail));
                viewHolder.setView("txt_name", convertView.findViewById(R.id.txt_name));
                viewHolder.setView("chk_select", convertView.findViewById(R.id.chk_select));
                viewHolder.setView("txt_status", convertView.findViewById(R.id.txt_status));
                viewHolder.setView("txt_user_distance", convertView.findViewById(R.id.txt_user_distance));
                convertView.setTag(viewHolder);
            }

            final User item = getItem(position);
            viewHolder = (SendBirdFriendAdapter.ViewHolder) convertView.getTag();
            Helper.displayUrlImage(viewHolder.getView("img_thumbnail", ImageView.class), item.getProfileUrl());
            viewHolder.getView("txt_name", TextView.class).setText(item.getNickname());

            viewHolder.getView("txt_user_distance", TextView.class).setText(distanceInfo.get(item.getUserId()) + " meters");
            viewHolder.getView("chk_select", CheckBox.class).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mSelectedUserIds.add(item.getUserId());
                    } else {
                        mSelectedUserIds.remove(item.getUserId());
                    }
                }
            });
            viewHolder.getView("chk_select", CheckBox.class).setChecked(mSelectedUserIds.contains(item.getUserId()));


            if (item.getConnectionStatus() == User.ConnectionStatus.ONLINE) {
                viewHolder.getView("txt_status", TextView.class).setText("Online");
            } else {
                viewHolder.getView("txt_status", TextView.class).setText("");
            }
            return convertView;
        }

        private class ViewHolder {
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
