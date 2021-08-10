package com.example.chatty_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chatty_2.Adapters.MessageAdapter;
import com.example.chatty_2.Models.Messages;
import com.example.chatty_2.databinding.ActivityChattingBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChattingActivity extends AppCompatActivity {

    ActivityChattingBinding binding;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide(); // action bar hiding done

        binding = ActivityChattingBinding.inflate(getLayoutInflater()); // binding assigned
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance(); // authentication got
        databaseReference = FirebaseDatabase.getInstance().getReference(); // Firebase database reference Created

        final String senderId = mAuth.getUid(); // getting the id of the person who is logged in and sending messages
        String receiverId = getIntent().getStringExtra("UserId"); // Getting receiver id from main Activity (the guy we are sending messages to )
        String profilePic = getIntent().getStringExtra("ProfilePic"); // Getting receiver profile pic url from main Activity
        String userName = getIntent().getStringExtra("UserName"); // Getting receiver name from main Activity

        Picasso.get().load(profilePic).placeholder(R.drawable.avatar_image_view_white).into(binding.imageView9); // Setting receiver photo
        binding.textUserName.setText(userName);

        binding.imageViewBack.setOnClickListener(new View.OnClickListener() { // on clicking the back button we go back to the main activity
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChattingActivity.this,MainActivity.class); // Intent to go back to the main activity
                startActivity(intent);
            }
        });

        final ArrayList<Messages> messagesArrayList = new ArrayList<>(); // making a arraylist to store all the data from firebaseDatabase to give to messageAdapter class

        final MessageAdapter messageAdapter = new MessageAdapter(messagesArrayList,this,receiverId); // making a message adapter by putting the arraylist and context to put into the recycler view
        binding.chatRecyclerView.setAdapter(messageAdapter); // putting the messageAdapter in the recycler view

        LinearLayoutManager layoutManager = new LinearLayoutManager(this); // Making a linear layout manager to put into recycler view ( layout managers only take context as a parameter)
        binding.chatRecyclerView.setLayoutManager(layoutManager); // putting the layout manager into recycler view

         final String senderNode = senderId+receiverId; // creating a unique id to store sender messages to put as a node in firebase realtime database
         final String receiverNode = receiverId+senderId; // creating a unique id to store receiver messages to put as a node in firebase realtime database
        // we are creating two different ids because i want to implement delete functionality later


        databaseReference.child("chats").child(senderNode).addValueEventListener(new ValueEventListener() { // getting the arraylist of messages which will go into message adapter
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { // A DataSnapshot is an efficiently generated, immutable copy of the data at a Database location
                messagesArrayList.clear(); // clearing the arraylist
                for(DataSnapshot snapshot1 : snapshot.getChildren()) { // looping through all the data locations using .getChildren iterable
                    Messages messages2 = snapshot1.getValue(Messages.class); // retrieving the messages object from the snapshot using .getvalue method
                    messages2.setMessageId(snapshot1.getKey());
                    messagesArrayList.add(messages2);                        // adding the messages obj to the arraylist
                }
                messageAdapter.notifyDataSetChanged();      //Notifies the attached observers that the underlying data has been changed and any View reflecting the data set should refresh itself.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.sendButton.setOnClickListener(new View.OnClickListener() { // button that will sent the data to the realtime database
            @Override
            public void onClick(View v) {
            String messageStr = binding.editTextSendMessage.getText().toString(); // getting the message typed
            final Messages messagesObj = new Messages(senderId,messageStr); // creating the message object
            messagesObj.setTimeStamp(new Date().getTime()); // setting the time stamp using date.getTime
            binding.editTextSendMessage.setText(""); // After you send the message to the database clear the edit text view to write new messages

            databaseReference.child("chats")  // adding to the chats node
                    .child(senderNode)        // adding to the sender node
                    .push()                   // creating a new node for each messages
                    .setValue(messagesObj)    // setting the value of the node
                    .addOnSuccessListener(new OnSuccessListener<Void>() { // on success this happens
                        @Override
                        public void onSuccess(Void unused) {
                            databaseReference.child("chats") // adding to the chats node
                                    .child(receiverNode)     // adding to the receiver node
                                    .push()                  // creating a new node for each messages
                                    .setValue(messagesObj)   // setting the value of the node
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    });
                        }
                    });

            }
        });








    }
}