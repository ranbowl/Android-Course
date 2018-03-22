package me.peterjiang.testfinal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.UserMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class SendBirdChatAdapter extends BaseAdapter {
    private static final int TYPE_UNSUPPORTED = 0;
    private static final int TYPE_USER_MESSAGE = 1;
    private static final int TYPE_FILE_MESSAGE = 2;
    private static final int TYPE_ADMIN_MESSAGE = 3;

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final ArrayList<Object> mItemList;

    public SendBirdChatAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItemList = new ArrayList<>();
    }

    public void delete(Object message) {
        mItemList.remove(message);
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return mItemList.get(position);
    }

    public void clear() {
        mItemList.clear();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void insertMessage(BaseMessage message) {
        mItemList.add(0, message);
    }

    public void appendMessage(BaseMessage message) {
        mItemList.add(message);
    }

    @Override
    public int getItemViewType(int position) {
        Object item = mItemList.get(position);
        if (item instanceof UserMessage) {
            return TYPE_USER_MESSAGE;
        } else if (item instanceof FileMessage) {
            return TYPE_FILE_MESSAGE;
        } else if (item instanceof AdminMessage) {
            return TYPE_ADMIN_MESSAGE;
        }

        return TYPE_UNSUPPORTED;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final Object item = getItem(position);

        if (convertView == null || ((ViewHolder) convertView.getTag()).getViewType() != getItemViewType(position)) {
            viewHolder = new ViewHolder();
            viewHolder.setViewType(getItemViewType(position));

            switch (getItemViewType(position)) {
                case TYPE_UNSUPPORTED:
                    convertView = new View(mInflater.getContext());
                    convertView.setTag(viewHolder);
                    break;
                case TYPE_USER_MESSAGE: {
                    TextView tv;

                    convertView = mInflater.inflate(R.layout.sendbird_view_open_user_message, parent, false);
                    tv = (TextView) convertView.findViewById(R.id.txt_message);
                    viewHolder.setView("message", tv);
                    convertView.setTag(viewHolder);
                    break;
                }
                case TYPE_ADMIN_MESSAGE: {
                    convertView = mInflater.inflate(R.layout.sendbird_view_admin_message, parent, false);
                    viewHolder.setView("message", convertView.findViewById(R.id.txt_message));
                    convertView.setTag(viewHolder);
                    break;
                }
                case TYPE_FILE_MESSAGE: {
                    TextView tv;

                    convertView = mInflater.inflate(R.layout.sendbird_view_open_file_message, parent, false);
                    tv = (TextView) convertView.findViewById(R.id.txt_sender_name);
                    viewHolder.setView("txt_sender_name", tv);

                    viewHolder.setView("img_file_container", convertView.findViewById(R.id.img_file_container));

                    viewHolder.setView("image_container", convertView.findViewById(R.id.image_container));
                    viewHolder.setView("img_thumbnail", convertView.findViewById(R.id.img_thumbnail));
                    viewHolder.setView("txt_image_name", convertView.findViewById(R.id.txt_image_name));
                    viewHolder.setView("txt_image_size", convertView.findViewById(R.id.txt_image_size));

                    viewHolder.setView("file_container", convertView.findViewById(R.id.file_container));
                    viewHolder.setView("txt_file_name", convertView.findViewById(R.id.txt_file_name));
                    viewHolder.setView("txt_file_size", convertView.findViewById(R.id.txt_file_size));

                    convertView.setTag(viewHolder);

                    break;
                }
            }
        }

        viewHolder = (ViewHolder) convertView.getTag();
        switch (getItemViewType(position)) {
            case TYPE_UNSUPPORTED:
                break;
            case TYPE_USER_MESSAGE:
                final UserMessage message = (UserMessage) item;
                viewHolder.getView("message", TextView.class).setText(Html.fromHtml("<font color='#824096'><b>" + message.getSender().getNickname() + "</b></font>: " + message.getMessage()));
                break;
            case TYPE_ADMIN_MESSAGE:
                AdminMessage adminMessage = (AdminMessage) item;
                viewHolder.getView("message", TextView.class).setText(Html.fromHtml(adminMessage.getMessage()));
                break;
            case TYPE_FILE_MESSAGE:
                final FileMessage fileLink = (FileMessage) item;

                viewHolder.getView("txt_sender_name", TextView.class).setText(Html.fromHtml("<font color='#824096'><b>" + fileLink.getSender().getNickname() + "</b></font>: "));
                if (fileLink.getType().toLowerCase().startsWith("image")) {
                    viewHolder.getView("file_container").setVisibility(View.GONE);

                    viewHolder.getView("image_container").setVisibility(View.VISIBLE);
                    viewHolder.getView("txt_image_name", TextView.class).setText(fileLink.getName());
                    viewHolder.getView("txt_image_size", TextView.class).setText(Helper.readableFileSize(fileLink.getSize()));
                    Helper.displayUrlImage(viewHolder.getView("img_thumbnail", ImageView.class), fileLink.getUrl());
                } else {
                    viewHolder.getView("image_container").setVisibility(View.GONE);

                    viewHolder.getView("file_container").setVisibility(View.VISIBLE);
                    viewHolder.getView("txt_file_name", TextView.class).setText(fileLink.getName());
                    viewHolder.getView("txt_file_size", TextView.class).setText(Helper.readableFileSize(fileLink.getSize()));
                }
                viewHolder.getView("img_file_container").setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(mContext)
                                .setTitle("SendBird")
                                .setMessage("Do you want to download this file?")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            Helper.downloadUrl(fileLink.getUrl(), fileLink.getName(), mContext);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .create()
                                .show();
                    }
                });
                break;
        }

        return convertView;
    }

    private class ViewHolder {
        private Hashtable<String, View> holder = new Hashtable<>();
        private int type;

        public int getViewType() {
            return this.type;
        }

        public void setViewType(int type) {
            this.type = type;
        }

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
