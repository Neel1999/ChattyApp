package com.example.chatty_2.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatty_2.Models.Messages;
import com.example.chatty_2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class MessageAdapter extends RecyclerView.Adapter {

    int SENDER = 1;
    int RECEIVER = 2;

    ArrayList<Messages> messagesArrayList;
    Context context;
    String receiverId;

    public MessageAdapter(ArrayList<Messages> messagesArrayList, Context context, String receiverId) { // Another constructor to receive receiver id
        this.messagesArrayList = messagesArrayList;
        this.context = context;
        this.receiverId = receiverId;
    }

    public MessageAdapter(ArrayList<Messages> messagesArrayList, Context context) {   // Constructor to get the arraylist of messages and the activity context
        this.messagesArrayList = messagesArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // inflating both the views based on the conditions
        if (viewType == SENDER) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) { // perform specific actions on the views of the recycler views

        Messages messages = messagesArrayList.get(position); // Getting the message in this particular position

        holder.itemView.setOnClickListener(new View.OnClickListener() { // itemview is used to set what happens when you press the views in the recycler view
            // We are goint to implement a option to delete a message
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setTitle("Delete Message") // Its like a pop up box which pops up when the view is clicked
                        .setMessage("Are You Sure You Want To Delete This Message")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                String senderRoom = FirebaseAuth.getInstance().getUid()+ receiverId; // getting the senderRoom node in firebase
                                reference.child("chats")
                                        .child(senderRoom)
                                        .child(messages.getMessageId())
                                        .setValue(null); // deleting only the senderRoom node of the message by setting its value to null
                                // Remember that this message is only deleted in the senderRoom but the receiver room message still exits
                                // This message will only be deleted for sender and not the receiver

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // dismissing the pop up box
                    }
                }).show(); // showing the dialog box
            }
        });


        if (holder.getClass() == SenderViewHolder.class) {  // Setting the text views for the messages in our holders
            ((SenderViewHolder)holder).textViewSender.setText(messages.getMessage());
        } else {
            ((ReceiverViewHolder)holder).textViewReceiver.setText((messages.getMessage()));
        }

    }


    @Override
    public int getItemViewType(int position) { // This method is called separately because we want to differentiate between
                                                // the two views in our recycler view
        if (messagesArrayList.get(position).getUid().equals(FirebaseAuth.getInstance().getUid())) {
            return SENDER;
        } else {
            return RECEIVER;
        }
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    } // giving the size of the array
}


// Because this recycler has two layouts that need to be included , we have to also create two view holders inner classes
// which will initialize all the text views for use
class ReceiverViewHolder extends RecyclerView.ViewHolder { // First ViewHolder for Receiver Text Chat

    TextView textViewReceiver, textViewReceiverTime;

    public ReceiverViewHolder(@NonNull View itemView) { // Initializing the views in the holder
        super(itemView);
        textViewReceiver = itemView.findViewById(R.id.textViewReceiver);
        textViewReceiverTime = itemView.findViewById(R.id.textViewReceiverTime);

    }
}

class SenderViewHolder extends RecyclerView.ViewHolder { // Second ViewHolder For Sender text chat

    TextView textViewSender, textViewSenderTime;

    public SenderViewHolder(@NonNull View itemView) { // Initializing the views in the holder
        super(itemView);
        textViewSender = itemView.findViewById(R.id.textViewSenderText);
        textViewSenderTime = itemView.findViewById(R.id.textViewSenderTime);

    }

}
