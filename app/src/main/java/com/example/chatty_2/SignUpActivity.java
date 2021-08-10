package com.example.chatty_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chatty_2.Models.Users;
import com.example.chatty_2.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().hide(); // hide bar on top with the app name

        binding = ActivitySignUpBinding.inflate(getLayoutInflater()); // Replaces the need for viewbyId by using a custom made class
        setContentView(binding.getRoot());                            // for each xml file

        mAuth = FirebaseAuth.getInstance(); // Will use this for user sign up
        mDatabase = FirebaseDatabase.getInstance().getReference(); // will use this database to save the info from sign in authentication

        progressDialog = new ProgressDialog(SignUpActivity.this);  //Creating a popup that shows loading when creating a account
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("Wait For a Moment Please");



        binding.signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show(); // Starting to show the loading popup

                                                        //email taken from edit text view       //password taken from edit text view
                mAuth.createUserWithEmailAndPassword(binding.emailAddress.getText().toString(),binding.password.getText().toString()) // Creating a new user using firebase
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                progressDialog.dismiss(); // Stopping the Loading popup ** Very Important to do

                                if(task.isSuccessful()){

                                    Users user = new Users(binding.userName.getText().toString(),binding.emailAddress.getText().toString(),binding.password.getText().toString()); // Creating a instance of user class to store user data
                                    String id = task.getResult().getUser().getUid(); // Getting the Uid from the authentication page in Firebase

                                    mDatabase.child("Users").child(id).setValue(user); //Storing the user object as value in the Dynamic Database

                                    Toast.makeText(SignUpActivity.this, "User is Created", Toast.LENGTH_SHORT).show(); // Toast to show that user is registered

                                }
                                else{
                                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show(); // If user not created then showing exception message in toast
                                }
                            }
                        });
            }
        });

        binding.textViewSignIn.setOnClickListener(new View.OnClickListener() {  // When this text view is Clicked the user is taken
            @Override                                                                 // to Sign In activity
            public void onClick(View v) {

                Intent intent = new Intent(SignUpActivity.this,SignInActivity.class);
                startActivity(intent);

            }
        });


    }

}
