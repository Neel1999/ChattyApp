package com.example.chatty_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.chatty_2.Models.Users;
import com.example.chatty_2.databinding.ActivitySignInBinding;
import com.example.chatty_2.databinding.ActivitySignUpBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    ActivitySignInBinding binding;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 21;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide(); // Hides The Action bar

        binding = ActivitySignInBinding.inflate(getLayoutInflater()); // Replaces the need for viewbyId by using a custom made class
        setContentView(binding.getRoot());                            // for each xml file

        mAuth = FirebaseAuth.getInstance(); // Creating a object of Firebase Authentication for signing in

        progressDialog = new ProgressDialog(SignInActivity.this); //Creating a popup that shows loading when creating a account
        progressDialog.setTitle("Logging Into Your Account");
        progressDialog.setMessage("Wait For a Moment Please ");


        // if you dont put anything in the editText views and click on signin the app crashes
        binding.signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.emailAddress.getText().toString().isEmpty()){
                    binding.emailAddress.setError("Please Enter A Email Address"); //setting error in android edit text view
                    return; // Exit the function
                }
                if(binding.password.getText().toString().isEmpty()){
                    binding.password.setError("Please Enter A Password");   //setting error in android edit text view
                    return; // exit the function
                }

                progressDialog.show(); // Starting to show the loading popup

                                                 //email taken from edit text view        //password taken from edit text view
                mAuth.signInWithEmailAndPassword(binding.emailAddress.getText().toString(),binding.password.getText().toString())  //Signing in with Firebase
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                progressDialog.dismiss();// Stopping the Loading popup ** Very Important to do

                                if(task.isSuccessful()){

                                    Intent intent = new Intent(SignInActivity.this , MainActivity.class); // If login is successful then intent is started and the app takes
                                    startActivity(intent);                                                             //you to the main activity

                                }else{
                                    Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show(); // task.getException().getMessage() shows us why the task failed
                                }
                            }
                        });
            }
        });

        if(mAuth.getCurrentUser() != null){ // If the user is logged in already then take the user directly to main activity

            Intent intent = new Intent(SignInActivity.this,MainActivity.class); // Intent to go to main activity
            startActivity(intent); // Starting the intent to go to main activity

        }

        binding.textViewSignUp.setOnClickListener(new View.OnClickListener() {  // When this text view is Clicked the user is taken
            @Override                                                                 // to Sign up activity
            public void onClick(View v) {

                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);

            }
        });









        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) // Somethings to be declared before signing into google
                .requestIdToken(getString(R.string.default_web_client_id))                             // through the google button view below
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(SignInActivity.this, gso);


        binding.googleButton.setOnClickListener(new View.OnClickListener() { // Sign into google using the google button view
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }
    ////////////////////////////////////////// Google Sign In Code
    // Enable google authentication in firebase authentication
    // Give your support email
    // Give sha code which can be found in gradle( right side of the screen ) -> tasks -> android -. signingReport
    //(if task not found then go to file -> settings -> experimental -> tick off build task for gradle) then go and do file -> sync gradle with project

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser fireBaseUser = mAuth.getCurrentUser();

                            Users users = new Users();
                            users.setUserName(fireBaseUser.getDisplayName());
                            users.setUserId(fireBaseUser.getUid());
                            users.setProfilePic(fireBaseUser.getPhotoUrl().toString());
                            databaseReference = FirebaseDatabase.getInstance().getReference(); // Initializing firebase database reference
                            databaseReference.child("Users").child(users.getUserId()).setValue(users);


                            Intent intent = new Intent(SignInActivity.this,MainActivity.class); // When login successful take to the main activity
                            startActivity(intent);
                            Toast.makeText(SignInActivity.this, "Sign in with Google Successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignInActivity.this, "Sign Failed For Some Reason", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    /////////////////////////////////////////////////////////////////////// End of Google sign in methods

}