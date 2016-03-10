package com.ecarezone.android.patient.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.model.Chat;
import com.ecarezone.android.patient.model.rest.UploadImageResponse;
import com.ecarezone.android.patient.utils.ImageUtil;
import com.ecarezone.android.patient.utils.SinchUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by L&T Technology Services.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    public static final int DIRECTION_INCOMING = 0;
    public static final int DIRECTION_OUTGOING = 1;
    private List<Pair<Chat, Integer>> mMessages;
    private SimpleDateFormat mFormatter;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mChatUser, mChatText, mChartTime;
        public ImageView mChartImage;
        public ProgressBar mProgressBar;

        public ViewHolder(View v) {
            super(v);
            mChatUser = (TextView) v.findViewById(R.id.chatUser);
            mChatText = (TextView) v.findViewById(R.id.chatText);
            mChartTime = (TextView) v.findViewById(R.id.chatTime);
            mChartImage = (ImageView) v.findViewById(R.id.chatImage);
            mProgressBar = (ProgressBar) v.findViewById(R.id.chat_image_spinner);
        }
    }

    public void addMessage(Chat message, int direction) {
        mMessages.add(new Pair(message, direction));
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int i) {
        return mMessages.get(i).second;
    }

    public ChatAdapter(Context context) {
        mMessages = new ArrayList<Pair<Chat, Integer>>();
        mFormatter = new SimpleDateFormat("HH:mm");
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        int direction = viewType;
        int res = 0;
        if (direction == DIRECTION_INCOMING) {
            res = R.layout.chat_list_item_incoming;
        } else if (direction == DIRECTION_OUTGOING) {
            res = R.layout.chat_list_item_outgoing;
        }
        View v = LayoutInflater.from(parent.getContext())
                .inflate(res, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Chat chat = mMessages.get(position).first;
        String message = chat.getMessageText();
        String name = chat.getSenderId();
        holder.mProgressBar.setVisibility(View.GONE);

        if (chat.getSenderId().equals(LoginInfo.userName) &&
                chat.getDeviceImagePath() != null) {
            holder.mChartImage.setVisibility(View.VISIBLE);
            holder.mChatText.setVisibility(View.GONE);
            if (chat.getDiscImageFile() != null) {
                Picasso.with(mContext)
                        .load(chat.getDiscImageFile())
                        .config(Bitmap.Config.RGB_565).fit()
                        .centerCrop()
                        .into(holder.mChartImage);
            } else {
                new ImageUploadDiscTask(holder).execute(chat);
            }

        } else if (chat.getInComingImageUrl() != null) {
            holder.mChartImage.setVisibility(View.VISIBLE);
            holder.mChatText.setVisibility(View.GONE);
            Picasso.with(mContext)
                    .load(chat.getInComingImageUrl())
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            holder.mProgressBar.setVisibility(View.GONE);
                            holder.mChartImage.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            holder.mProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            holder.mProgressBar.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            holder.mChatText.setText(chat.getMessageText());
            holder.mChatText.setVisibility(View.VISIBLE);
            holder.mChartImage.setVisibility(View.GONE);
        }
        holder.mChatUser.setText(name);
        holder.mChartTime.setText(mFormatter.format(chat.getTimeStamp()));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    /*AsyncTask for uploading image to server*/
    private class ImageUploadTask extends AsyncTask<File, Void, UploadImageResponse> {
        ViewHolder holder;
        File file;
        Chat chat;

        ImageUploadTask(ViewHolder holder, Chat chat) {
            this.holder = holder;
            this.chat = chat;
        }

        @Override
        protected UploadImageResponse doInBackground(File... strings) {
            file = strings[0];
            return ImageUtil.uploadChatImage(file);
        }

        @Override
        protected void onPostExecute(UploadImageResponse uploadImageResponse) {
            SinchUtil.getSinchServiceInterface().sendMessage(chat.getReceiverId(), uploadImageResponse.data.avatarUrl);
        }
    }

    /*AsyncTask for uploading image to disc*/
    private class ImageUploadDiscTask extends AsyncTask<Chat, Void, File> {
        Chat chat;
        ViewHolder holder;

        ImageUploadDiscTask(ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        protected File doInBackground(Chat... chats) {
            chat = chats[0];
            File file = ImageUtil.uploadChatImageToDisc(chat.getDeviceImagePath());
            return file;
        }

        @Override
        protected void onPostExecute(File file) {

            chat.setDiscImageFile(file);
            Picasso.with(mContext)
                    .load(file)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            holder.mProgressBar.setVisibility(View.GONE);
                            holder.mChartImage.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            holder.mProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            holder.mProgressBar.setVisibility(View.VISIBLE);
                        }
                    });

            new ImageUploadTask(holder, chat).execute(file);
        }
    }
}
