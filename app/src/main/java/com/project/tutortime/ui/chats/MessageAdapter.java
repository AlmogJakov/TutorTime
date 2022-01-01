package com.project.tutortime.ui.chats;
import com.project.tutortime.R;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * This class communicate between  the Messages data and the ui
 * This class has two ViewHolder , 1 for received messages and 1 for sent messages
 */
public class MessageAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 0;

    private Context mContext;
    private List<Message> mMessages;

    public MessageAdapter(Context context, List<Message> messageList) {
        mContext = context;
        mMessages = messageList;
    }

    /**
     * This method return the ViewHolder depends on the VIEW_TYPE
     * @return the message View
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
            /* if the message is sent by this user the view should display it on the left side */
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.message_item_me, parent, false);
            return new SentMessageHolder(view);
        } else{ /* the message received from the other user and should display on the right side */
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.message_item_other, parent, false);
            return new ReceivedMessageHolder(view);
        }
    }

    /**
     * This method pass the message to the ViewHolder depends on the message type
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = (Message) mMessages.get(position);
            /* bind the message depend on the view type */
        if(holder.getItemViewType()==1) {
            ((SentMessageHolder) holder).bind(message);}
        else{
            ((ReceivedMessageHolder) holder).bind(message);
            }

        }

    /**
     * This method return the type of specific message
     * @param position the current position in the messages list
     * @return 1 for sent 0 for received
     */
    @Override
    public int getItemViewType(int position) {
        Message message = (Message) mMessages.get(position);

        if (message.getSenderID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            /*  the current user is the sender of the message */
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            /* some other user sent the message */
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    /**
     * This method return the amount of the messages in the list
     * @return the active messages
     */
    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    /**
     * This class hold the sentMessage View objects
     */
    private  class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeText;

        SentMessageHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.message_text);
            timeText = (TextView) itemView.findViewById(R.id.date_text);

        }
        void bind(Message message) {
            /* set the message text and time */
            messageText.setText(message.getMessageText());
            timeText.setText(DateFormat.format("dd-MM(HH:mm)", message.getTime ()));
        }
    }
    /**
     * This class hold the ReceivedMessage View objects
     */
    private  class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeText;
        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.message_text);
            timeText = (TextView) itemView.findViewById(R.id.date_text);

        }
        void bind(Message message) {
            /* set the message text and time */
            messageText.setText(message.getMessageText());
            timeText.setText(DateFormat.format("dd-MM(HH:mm)", message.getTime ()));

        }
    }

    /**
     * This method update the list after a change in the data base
     * @param messages the updated messages list
     */
    public void updateList(List<Message> messages){
        this.mMessages = messages;
        this.notifyDataSetChanged();
    }

}