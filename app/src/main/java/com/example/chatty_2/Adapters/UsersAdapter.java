package com.example.chatty_2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatty_2.ChattingActivity;
import com.example.chatty_2.Models.Users;
import com.example.chatty_2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {  // To create recycler view you gotta extend
                                                                                   // RecyclerView.Adapter<UsersAdapter.ViewHolder>
                                                                                   //ViewHolder here is the custom inner class created below

    ArrayList<Users> arrayList ;   // This is gonna acquire the list of users we have in firebase
                                    // (User is a class we created before and is located in the model package)
    Context context;

    public UsersAdapter(ArrayList<Users> arrayList, Context context) { // Constructor of the field defined in the class
        this.arrayList = arrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // This method assigns what all the views in the recycler view will look like
        View view = LayoutInflater.from(context).inflate(R.layout.sample_user_for_recyclerview,parent,false); //Inflating the xml we made in the layout folder
        return new ViewHolder(view); // Returning the xml we just inflated into a view
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) { // Here we assign the specific values of views in our custom made xml file

        Users users = arrayList.get(position); // getting user according to their position in the recyclerView
        Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.avatar_image_view).into(holder.profile_image); // Using the picasso library to load a image into our image view // we are loading the image url we get from google // till our image loads we can show a image through placeholder method
        holder.textViewUserName.setText(users.getUserName());// setting user name

        // Showing the last message
        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(FirebaseAuth.getInstance().getUid()+ users.getUserId())
                .orderByChild("timeStamp") // ordering the values in the senderRoom node on the basis of time stamp in descending order, the last message will be on the top
                .addListenerForSingleValueEvent(new ValueEventListener() { // used for when you want to extract a single value from the realtime database
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()){
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                holder.textViewLastMessage.setText(dataSnapshot.child("message").getValue(String.class));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        // ---------------------------------------


        // we use into to specify where we want to load the image (in image view in this case)

        holder.itemView.setOnClickListener(new View.OnClickListener() {  // putting onclick listener on each item in recycler view
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ChattingActivity.class); // Creating a Intent to go to the Chatting Activity
                intent.putExtra("ProfilePic",users.getProfilePic()); // Sending profile pic url from mainActivity to chatting activity
                intent.putExtra("UserId",users.getUserId()); // Sending UserId from mainActivity to chatting activity
                intent.putExtra("UserName",users.getUserName());// Sending UserName from mainActivity to chatting activity
                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }







    public class ViewHolder extends RecyclerView.ViewHolder{  // This inner Class is used to initialize all the views inside
        ImageView profile_image;                              // our sample_user_for_recyclerview xml file
        TextView textViewUserName,textViewLastMessage;        // This is mandatory step

        public ViewHolder(@NonNull View itemView) {           // This is where you initialize the ids by using itemHolder parameter
            super(itemView);
            profile_image = itemView.findViewById(R.id.profile_image);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            textViewLastMessage = itemView.findViewById(R.id.textViewLastMessage);
        }
    }
}
