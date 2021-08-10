package com.example.chatty_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.chatty_2.Adapters.UsersAdapter;
import com.example.chatty_2.Models.Users;
import com.example.chatty_2.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ActivityMainBinding binding;
    ArrayList<Users> arrayList = new ArrayList<>();
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Welcome To Chatty App");

        binding = ActivityMainBinding.inflate(getLayoutInflater()); // Replaces the need for viewbyId by using a custom made class
        setContentView(binding.getRoot());                          // for each xml file

        mAuth = FirebaseAuth.getInstance();

        UsersAdapter adapter = new UsersAdapter(arrayList,MainActivity.this);           // Making a instance of the Adapter class we made for recycler View
        binding.chatRecyclerView.setAdapter(adapter);                                          // Setting adapter to the recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this); // Making a layout manager (linear because we want to show chats from top to bottom)
        binding.chatRecyclerView.setLayoutManager(layoutManager);                              // Setting the layout manager to the recycler View

        mDatabase = FirebaseDatabase.getInstance().getReference(); // Creating a instance and then a reference of the data base



        // Making the array list of users to send to the UserAdapter every time changes are made to our realtime database
        mDatabase.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear(); // clearing the arraylist just in case
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);
                    users.setUserId(dataSnapshot.getKey()); //setting user id in the class object and giving it to the userAdapter
                    if(!(users.getUserId().equals(mAuth.getUid()))) {
                        arrayList.add(users);
                    }
                }
                adapter.notifyDataSetChanged(); // notifies if the dataset(in this case a array) is modified in any way
            }
            //-----------------------

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {  // Syntax for creating menu through a menu inflator
        MenuInflater inflater = getMenuInflater();   // You have to create a android resource file in res folder of menu type and edit the xml to create menus
        inflater.inflate(R.menu.menu,menu);          // .inflate(RR.menu.xml_file_name,parameter_the_method) in the inflate method
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // What each item in the drop down menu on the action bar does

        switch(item.getItemId()){ // getting the id of the item the user has selected

            case R.id.logout:  // What logout item will do
                mAuth.signOut(); // Signout of account
                Intent intent = new Intent(MainActivity.this,SignInActivity.class); // Go back to signIn activity
                startActivity(intent);
                break;
            case R.id.settings: // what settings item will do
                Intent intent1 = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent1);

                break;
            case R.id.menuGroupChat:

                Intent intent2 = new Intent(this,GroupChatActivity.class);
                startActivity(intent2);

                break;
        }
        return true; // Just write return true . we dont want this menu to return anything
    }
}