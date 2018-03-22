package me.peterjiang.testfinal;


import android.app.Activity;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.PreviousMessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserListQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_invite extends Fragment {
    private ListView mListView;
    private SendBirdUserinviteAdapter mAdapter;
    private String mChannelUrl;
    private PreviousMessageListQuery mPrevMessageListQuery;
    private boolean mIsUploading;
    private UserListQuery mUserListQuery;
    private HashSet<String> mSelectedUserIds;
    private View rootView;
    private boolean isTwoPane;
    private Activity myActivity;
    private GroupChannel mGroupChannel;

    public Fragment_invite() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_fragment_invite, container, false);
        initUIComponents(rootView);
        mUserListQuery = SendBird.createUserListQuery();
        loadMoreUsers();

        return rootView;
    }

    private void initUIComponents(View rootView) {
        mSelectedUserIds = new HashSet<>();
        mListView = (ListView) rootView.findViewById(R.id.list_user);
        mAdapter = new SendBirdUserinviteAdapter(getActivity());

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

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        myActivity = getActivity();

        Display display = myActivity.getWindowManager().getDefaultDisplay();
        isTwoPane = display.getWidth() > display.getHeight();

        FloatingActionButton fab = (FloatingActionButton) myActivity.findViewById(R.id.fab_userinvite);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] userIds = mSelectedUserIds.toArray(new String[0]);
                if (userIds.length > 0) {
//                    View cur_view = getActivity().getLayoutInflater().inflate(R.layout.sendbird_view_group_create_channel, null);
////                    final EditText chName = (EditText) view.findViewById(R.id.etxt_chname);
////                    final CheckBox distinct = (CheckBox) view.findViewById(R.id.chk_distinct_user);
//
//                    new AlertDialog.Builder(getActivity())
//                            .setView(cur_view)
//                            .setTitle("Create Group Channel")
//                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    GroupChannel.createChannelWithUserIds(Arrays.asList(userIds), false,new GroupChannel.GroupChannelCreateHandler() {
//                                        @Override
//                                        public void onResult(GroupChannel groupChannel, SendBirdException e) {
//                                            if (e != null) {
//                                                Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                                return;
//                                            }
//                                            Fragment3 fragment = new Fragment3();
//                                            getFragmentManager().beginTransaction().replace(R.id.main_fragment, fragment).commit();
//                                            //mAdapter.replace(groupChannel);
//                                        }
//
//                                    });
//                                }
//                            })
//                            .setNegativeButton("Cancel", null).create().show();



                            mGroupChannel.inviteWithUserIds(Arrays.asList(userIds),new GroupChannel.GroupChannelInviteHandler() {
                                @Override
                                public void onResult(SendBirdException e) {
                                    if (e != null) {
                                        // Error.
                                        return;
                                    }
                                }
                            });
                    Fragment3 fragment = new Fragment3();
                    getFragmentManager().beginTransaction().replace(R.id.main_fragment, fragment).commit();
                }
            }
        });

    }


    public void setURL(GroupChannel groupchanel){
        mGroupChannel = groupchanel;
        //initUIComponents(rootView);
        //enterChannel(mChannelUrl);
    }


    public class SendBirdUserinviteAdapter extends BaseAdapter {
        private final Context mContext;
        private final LayoutInflater mInflater;
        private final ArrayList<User> mItemList;

        public SendBirdUserinviteAdapter(Context context) {
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mItemList = new ArrayList<>();
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
            SendBirdUserinviteAdapter.ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new SendBirdUserinviteAdapter.ViewHolder();

                convertView = mInflater.inflate(R.layout.sendbird_view_user, parent, false);
                viewHolder.setView("root_view", convertView);
                viewHolder.setView("img_thumbnail", convertView.findViewById(R.id.img_thumbnail));
                viewHolder.setView("txt_name", convertView.findViewById(R.id.txt_name));
                viewHolder.setView("chk_select", convertView.findViewById(R.id.chk_select));
                viewHolder.setView("txt_status", convertView.findViewById(R.id.txt_status));

                convertView.setTag(viewHolder);
            }

            final User item = getItem(position);
            viewHolder = (SendBirdUserinviteAdapter.ViewHolder) convertView.getTag();
            Helper.displayUrlImage(viewHolder.getView("img_thumbnail", ImageView.class), item.getProfileUrl());
            viewHolder.getView("txt_name", TextView.class).setText(item.getNickname());
            viewHolder.getView("chk_select", CheckBox.class).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mSelectedUserIds.add(item.getUserId());
                    } else {
                        mSelectedUserIds.remove(item.getUserId());
                    }
//                    if (mSelectedUserIds.size() <= 0) {
//                        ((Button) getActivity().findViewById(R.id.btn_ok)).setTextColor(Color.parseColor("#6f5ca7"));
//                    } else {
//                        ((Button) getActivity().findViewById(R.id.btn_ok)).setTextColor(Color.parseColor("#35f8ca"));
//                    }
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
