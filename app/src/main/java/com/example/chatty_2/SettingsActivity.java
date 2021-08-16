package com.example.chatty_2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatty_2.Models.Users;
import com.example.chatty_2.databinding.ActivitySettingsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;

    FirebaseStorage storage;
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();


        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();



        reference.child("Users").child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() { // updating the profilePic, username , about for the users in the
            // settings activity with the info the user has already had in the realtime database
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.avatar_image_view_white)
                        .into(binding.imageProfile); // updating profile pic

                binding.editTextSetStatus.setText(users.getStatus()); // updating status
                binding.editTextSetUserName.setText(users.getUserName()); // updating username
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.buttonSave.setOnClickListener(new View.OnClickListener() { // // updating the profilePic, username , about for the users in the database
            @Override
            public void onClick(View v) {
                String userName = binding.editTextSetUserName.getText().toString(); // getting username string
                String status = binding.editTextSetStatus.getText().toString(); // getting status string

                HashMap<String, Object> obj = new HashMap<>(); // putting both values in a hashmap with the name of the keys the same as nodes we want to update in the realtime database
                obj.put("userName",userName);
                obj.put("status", status);

                reference.child("Users").child(firebaseAuth.getUid()).updateChildren(obj); // updating the database
                Toast.makeText(SettingsActivity.this, "Your User Name and Status have been updated", Toast.LENGTH_SHORT).show();

            }
        });



        binding.addImage.setOnClickListener(new View.OnClickListener() { // getting image from the local storage on your phone using common intent
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT); // getting content
                intent.setType("image/*"); // content type (image)
                startActivityForResult(intent, 69);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // storing the pics in firebase storage and realtime database
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getData() != null) { // if the data received is not null
            Uri uri1 = data.getData(); // getting that data to a uri
            binding.imageProfile.setImageURI(uri1); // setting the pic in the view

            final StorageReference storageReference = storage.getReference().child("ProfilePics").child(firebaseAuth.getUid()); // making a instance of the firebase storage and storing pic uri in that
            storageReference.putFile(uri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { // on success of uploading uri of pic into firebase storage , store uri string in firebase realtime database
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() { // Get uri from uploading picture on firebase storage
                        @Override
                        public void onSuccess(Uri uri) {
                            reference.child("Users").child(firebaseAuth.getUid()) // and put it in the realtime database so the recycler view can use it in the main activity
                                    .child("profilePic").setValue(uri.toString());
                        }
                    });

                    Toast.makeText(SettingsActivity.this, "Profile Pic Updated", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}