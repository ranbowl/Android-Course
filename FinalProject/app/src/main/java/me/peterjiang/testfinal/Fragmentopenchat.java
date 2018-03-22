package me.peterjiang.testfinal;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.PreviousMessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;

import java.io.File;
import java.util.Hashtable;
import java.util.List;


public class Fragmentopenchat extends Fragment {
    private static final int REQUEST_PICK_IMAGE = 100;
    private static final String identifier = "SendBirdOpenChat";
    View rootView;
    private ListView mListView;
    private SendBirdChatAdapter mAdapter;
    private EditText mEtxtMessage;
    private Button mBtnSend;
    private ImageButton mBtnUpload;
    private ProgressBar mProgressBtnUpload;
    private String mChannelUrl;
    private OpenChannel mOpenChannel;
    private PreviousMessageListQuery mPrevMessageListQuery;
    private boolean mIsUploading;
    private boolean isTwoPane;

    private Activity myActivity;

    public Fragmentopenchat() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fragmentopenchat, container, false);

//        mChannelUrl = getArguments().getString("channel_url");
//
//        initUIComponents(rootView);
//
//        enterChannel(mChannelUrl);

        return rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        myActivity = getActivity();

        Display display = myActivity.getWindowManager().getDefaultDisplay();
        isTwoPane = display.getWidth() > display.getHeight();

        initUIComponents(rootView);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (!mIsUploading) {
            SendBird.removeChannelHandler(identifier);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mIsUploading) {
            SendBird.addChannelHandler(identifier, new SendBird.ChannelHandler() {
                @Override
                public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                    if (baseChannel.getUrl().equals(mChannelUrl)) {
                        mAdapter.appendMessage(baseMessage);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onMessageDeleted(BaseChannel channel, long msgId) {
                    if (channel.getUrl().equals(mChannelUrl)) {
                        boolean deleteMsg = false;

                        for (int i = 0; i < mAdapter.getCount(); i++) {
                            BaseMessage msg = (BaseMessage) mAdapter.getItem(i);
                            if (msg.getMessageId() == msgId) {
                                mAdapter.delete(msg);
                                deleteMsg = true;
                                break;
                            }
                        }

                        if (deleteMsg) {
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });

            loadPrevMessages(true);
        } else {
            mIsUploading = false;

            /**
             * Set this as true to restart auto-background detection,
             * when your Activity is shown again after the external Activity is finished.
             */
            SendBird.setAutoBackgroundDetection(true);
        }
    }

    @Override
    public void onDestroy() {
        exitChannel();
        super.onDestroy();
    }

    private void loadPrevMessages(final boolean refresh) {
        if (mOpenChannel == null) {
            return;
        }

        if (refresh || mPrevMessageListQuery == null) {
            mPrevMessageListQuery = mOpenChannel.createPreviousMessageListQuery();
        }

        if (mPrevMessageListQuery.isLoading()) {
            return;
        }

        if (!mPrevMessageListQuery.hasMore()) {
            return;
        }

        mPrevMessageListQuery.load(30, true, new PreviousMessageListQuery.MessageListQueryResult() {
            @Override
            public void onResult(List<BaseMessage> list, SendBirdException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (refresh) {
                    mAdapter.clear();
                }

                for (BaseMessage message : list) {
                    mAdapter.insertMessage(message);
                }
                mAdapter.notifyDataSetChanged();
                mListView.setSelection(list.size());
            }
        });
    }

    private void enterChannel(String channelUrl) {
        OpenChannel.getChannel(channelUrl, new OpenChannel.OpenChannelGetHandler() {
            @Override
            public void onResult(final OpenChannel openChannel, SendBirdException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                openChannel.enter(new OpenChannel.OpenChannelEnterHandler() {
                    @Override
                    public void onResult(SendBirdException e) {
                        if (e != null) {
                            Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mOpenChannel = openChannel;
                        ((TextView) rootView.findViewById(R.id.txt_channel_name)).setText("#"+openChannel.getName());

                        loadPrevMessages(true);
                    }
                });
            }
        });
    }

    private void exitChannel() {
        if (mOpenChannel != null) {
            mOpenChannel.exit(null);
        }
    }

    private void initUIComponents(View rootView) {
        mAdapter = new SendBirdChatAdapter(myActivity);

        mListView = (ListView) rootView.findViewById(R.id.list);
        turnOffListViewDecoration(mListView);
        mListView.setAdapter(mAdapter);

//        if(!isTwoPane){
//            ((AppCompatActivity) myActivity).getSupportActionBar().setHomeButtonEnabled(true);
//        }

        mBtnSend = (Button) rootView.findViewById(R.id.btn_send);
        mBtnUpload = (ImageButton) rootView.findViewById(R.id.btn_upload);
        mProgressBtnUpload = (ProgressBar) rootView.findViewById(R.id.progress_btn_upload);
        mEtxtMessage = (EditText) rootView.findViewById(R.id.etxt_message);

        mBtnSend.setEnabled(false);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });

        mBtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.requestReadWriteStoragePermissions(getActivity())) {
                    mIsUploading = true;

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE);

                    /**
                     * Set this as false to maintain SendBird connection,
                     * even when an external Activity is started.
                     */
                    SendBird.setAutoBackgroundDetection(false);
                }
            }
        });

        mEtxtMessage.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        send();
                    }
                    return true; // Do not hide keyboard.
                }
                return false;
            }
        });
        mEtxtMessage.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        mEtxtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mBtnSend.setEnabled(s.length() > 0);
            }
        });

        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Helper.hideKeyboard(getActivity());
                return false;
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (view.getFirstVisiblePosition() == 0 && view.getChildCount() > 0 && view.getChildAt(0).getTop() == 0) {
                        loadPrevMessages(false);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Select")
                        .setItems(new String[]{"Delete Message", "Block User"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        final BaseMessage msg0 = (BaseMessage) mAdapter.getItem(position);
                                        mOpenChannel.deleteMessage(msg0, new BaseChannel.DeleteMessageHandler() {
                                            @Override
                                            public void onResult(SendBirdException e) {
                                                if (e != null) {
                                                    Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    return;
                                                }

                                                Toast.makeText(getActivity(), "Message deleted.", Toast.LENGTH_SHORT).show();
                                                // Message will be deleted at ChannelHandler.
                                            }
                                        });
                                        break;

                                    case 1:
                                        BaseMessage msg1 = (BaseMessage) mAdapter.getItem(position);
                                        User target = null;

                                        if (msg1 instanceof AdminMessage) {
                                            Toast.makeText(getActivity(), "Admin message can not be deleted.", Toast.LENGTH_SHORT).show();
                                            return;
                                        } else if (msg1 instanceof UserMessage) {
                                            target = ((UserMessage) msg1).getSender();
                                        } else if (msg1 instanceof FileMessage) {
                                            target = ((FileMessage) msg1).getSender();
                                        }

                                        SendBird.blockUser(target, new SendBird.UserBlockHandler() {
                                            @Override
                                            public void onBlocked(User user, SendBirdException e) {
                                                if (e != null) {
                                                    Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    return;
                                                }

                                                Toast.makeText(getActivity(), user.getNickname() + " is blocked.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        break;
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null).create().show();

                return true;
            }
        });
    }

    private void showUploadProgress(boolean tf) {
        if (tf) {
            mBtnUpload.setEnabled(false);
            mBtnUpload.setVisibility(View.INVISIBLE);
            mProgressBtnUpload.setVisibility(View.VISIBLE);
        } else {
            mBtnUpload.setEnabled(true);
            mBtnUpload.setVisibility(View.VISIBLE);
            mProgressBtnUpload.setVisibility(View.GONE);
        }
    }

    private void turnOffListViewDecoration(ListView listView) {
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setHorizontalFadingEdgeEnabled(false);
        listView.setVerticalFadingEdgeEnabled(false);
        listView.setHorizontalScrollBarEnabled(false);
        listView.setVerticalScrollBarEnabled(true);
        listView.setSelector(new ColorDrawable(0x00ffffff));
        listView.setCacheColorHint(0x00000000); // For Gingerbread scrolling bug fix
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PICK_IMAGE && data != null && data.getData() != null) {
                upload(data.getData());
            }
        }
    }

    private void send() {
        mOpenChannel.sendUserMessage(mEtxtMessage.getText().toString(), new BaseChannel.SendUserMessageHandler() {
            @Override
            public void onSent(UserMessage userMessage, SendBirdException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                mAdapter.appendMessage(userMessage);
                mAdapter.notifyDataSetChanged();

                mEtxtMessage.setText("");
            }
        });

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Helper.hideKeyboard(getActivity());
        }
    }

    private void upload(Uri uri) {
        Hashtable<String, Object> info = Helper.getFileInfo(getActivity(), uri);
        final String path = (String) info.get("path");
        final File file = new File(path);
        final String name = file.getName();
        final String mime = (String) info.get("mime");
        final int size = (Integer) info.get("size");

        if (path == null) {
            Toast.makeText(getActivity(), "Uploading file must be located in local storage.", Toast.LENGTH_LONG).show();
        } else {
            showUploadProgress(true);
            mOpenChannel.sendFileMessage(file, name, mime, size, "", new BaseChannel.SendFileMessageHandler() {
                @Override
                public void onSent(FileMessage fileMessage, SendBirdException e) {
                    showUploadProgress(false);
                    if (e != null) {
                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mAdapter.appendMessage(fileMessage);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void setURL(String getURL){
        mChannelUrl = getURL;
//        initUIComponents(rootView);
        enterChannel(mChannelUrl);
    }
}

