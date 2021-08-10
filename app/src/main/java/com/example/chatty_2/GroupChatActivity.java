package com.example.chatty_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chatty_2.Adapters.MessageAdapter;
import com.example.chatty_2.Models.Messages;
import com.example.chatty_2.databinding.ActivityGroupChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {

    ActivityGroupChatBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imageViewBack.setOnClickListener(new View.OnClickListener() { // Going back to main Activity
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupChatActivity.this,MainActivity.class);
                startActivity(intent);

            }
        });

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final ArrayList<Messages> messagesArrayList = new ArrayList<>();

        final String userId = FirebaseAuth.getInstance().getUid();


        MessageAdapter messageAdapter = new MessageAdapter(messagesArrayList,GroupChatActivity.this);
        binding.chatRecyclerView.setAdapter(messageAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(GroupChatActivity.this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        databaseReference.child("Group Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String messageStr = binding.editTextSendMessage.getText().toString();
                final Messages messagesObj = new Messages(userId,messageStr);
                messagesObj.setTimeStamp(new Date().getTime());
                binding.editTextSendMessage.setText("");

                databaseReference.child("Group Chat")
                        .push()
                        .setValue(messagesObj)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });
            }
        });


    }
}