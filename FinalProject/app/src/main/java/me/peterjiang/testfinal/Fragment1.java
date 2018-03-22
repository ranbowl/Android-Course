package me.peterjiang.testfinal;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Fragment1 extends Fragment {

    //Profile Fragment

    public ProgressBar progressBar;
    private MainActivity myActivity;
    private View inflatedView = null;
    private Button btnChangeScreenname, btnChangeEmail, btnChangePassword, btnSendResetEmail, btnRemoveUser,
            changeEmail, changeScreenname, changePassword, sendEmail, remove, signOut;
    private TextView screenname;
    private EditText oldEmail, newEmail, password, newPassword, oldScreenname, newScreenname;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private Location mCurrentLocation;
    public Fragment1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        Context AppTheme = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
//        inflater = (LayoutInflater) AppTheme.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        this.inflatedView = inflater.inflate(R.layout.fragment_fragment1, container, false);
//        return inflatedView;
        this.inflatedView = inflater.inflate(R.layout.fragment_fragment1, container, false);
        return inflatedView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        myActivity = (MainActivity) getActivity();
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseHelper user_cus = new FirebaseHelper();


        btnChangeScreenname = (Button) inflatedView.findViewById(R.id.change_screenname_button);
        btnChangeEmail = (Button) inflatedView.findViewById(R.id.change_email_button);
        btnChangePassword = (Button) inflatedView.findViewById(R.id.change_password_button);
        btnSendResetEmail = (Button) inflatedView.findViewById(R.id.sending_pass_reset_button);
        btnRemoveUser = (Button) inflatedView.findViewById(R.id.remove_user_button);
        changeEmail = (Button) inflatedView.findViewById(R.id.changeEmail);
        changeScreenname = (Button) inflatedView.findViewById(R.id.changeScreenname);
        changePassword = (Button) inflatedView.findViewById(R.id.changePass);
        sendEmail = (Button) inflatedView.findViewById(R.id.send);
        remove = (Button) inflatedView.findViewById(R.id.remove);
        signOut = (Button) inflatedView.findViewById(R.id.sign_out);

        screenname = (TextView) inflatedView.findViewById(R.id.screenname);

        oldScreenname = (EditText) inflatedView.findViewById(R.id.old_screenname);
        newScreenname = (EditText) inflatedView.findViewById(R.id.new_screenname);
        oldEmail = (EditText) inflatedView.findViewById(R.id.old_email);
        newEmail = (EditText) inflatedView.findViewById(R.id.new_email);
        password = (EditText) inflatedView.findViewById(R.id.password);
        newPassword = (EditText) inflatedView.findViewById(R.id.newPassword);

        oldScreenname.setVisibility(View.GONE);
        newScreenname.setVisibility(View.GONE);
        oldEmail.setVisibility(View.GONE);
        newEmail.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changeEmail.setVisibility(View.GONE);
        changeScreenname.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
        sendEmail.setVisibility(View.GONE);
        remove.setVisibility(View.GONE);

        progressBar = (ProgressBar) inflatedView.findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        mCurrentLocation = myActivity.mCurrentLocation;
        if (user != null && mCurrentLocation != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users"); // What database can I actually talk to?
            DatabaseReference Users = ref.child(user.getUid());
            Users.child("latitude").setValue(mCurrentLocation.getLatitude());
            Users.child("longitude").setValue(mCurrentLocation.getLongitude());
            Users.child("Screenname").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if(snapshot.getValue() != null) {
                        screenname.setText("Hi " + snapshot.getValue().toString() + "!");
//                    Log.e("Fragmen1", snapshot.getValue().toString());
                    }
                    else {
                        screenname.setText("Please change your nickname!");
                    }
                }
                @Override public void onCancelled(DatabaseError error) { }
            });
        }

        btnChangeScreenname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldScreenname.setVisibility(View.GONE);
                newScreenname.setVisibility(View.VISIBLE);
                oldEmail.setVisibility(View.GONE);
                newEmail.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.GONE);
                changeScreenname.setVisibility(View.VISIBLE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
            }
        });

        changeScreenname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newScreenname.getText().toString().trim().equals("")) {

                    //add to customized firebase
                    user_cus.updateScreenname(newScreenname.getText().toString().trim(), user.getUid());
                    screenname.setText("Hi "+newScreenname.getText().toString().trim()+"!");

                    //add to firebaseuser
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(newScreenname.getText().toString().trim())
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(myActivity, "Nickname "+ newScreenname.getText().toString().trim()+" updated!" , Toast.LENGTH_LONG).show();
//                                        screenname.setText("Hi "+user.getDisplayName()+"!");
                                        newScreenname.setVisibility(View.GONE);
                                        changeScreenname.setVisibility(View.GONE);
                                    }
                                    else{
                                        Toast.makeText(myActivity, "Nickname update failed!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                    myActivity.mNickname = newScreenname.getText().toString().trim();
                    myActivity.disconnect();
                    myActivity.connect();
                    progressBar.setVisibility(View.GONE);


                } else if (newScreenname.getText().toString().trim().equals("")) {
                    newScreenname.setError("Enter screen name");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.GONE);
                newEmail.setVisibility(View.VISIBLE);
                oldScreenname.setVisibility(View.GONE);
                newScreenname.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.VISIBLE);
                changeScreenname.setVisibility(View.GONE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
            }
        });

        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newEmail.getText().toString().trim().equals("")) {
                    user.updateEmail(newEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(myActivity, "Email address is updated. Please sign in with new email id!", Toast.LENGTH_LONG).show();
                                        signOut();
                                        progressBar.setVisibility(View.GONE);
                                        btnChangeEmail.setVisibility(View.GONE);
                                        newEmail.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(myActivity, "Failed to update email!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                } else if (newEmail.getText().toString().trim().equals("")) {
                    newEmail.setError("Enter email");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.GONE);
                newEmail.setVisibility(View.GONE);
                oldScreenname.setVisibility(View.GONE);
                newScreenname.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.VISIBLE);
                changeEmail.setVisibility(View.GONE);
                changeScreenname.setVisibility(View.GONE);
                changePassword.setVisibility(View.VISIBLE);
                sendEmail.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newPassword.getText().toString().trim().equals("")) {
                    if (newPassword.getText().toString().trim().length() < 6) {
                        newPassword.setError("Password too short, enter minimum 6 characters");
                        progressBar.setVisibility(View.GONE);
                    } else {
                        user.updatePassword(newPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(myActivity, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                            signOut();
                                            progressBar.setVisibility(View.GONE);
                                            newPassword.setVisibility(View.GONE);
                                            btnChangePassword.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(myActivity, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }
                } else if (newPassword.getText().toString().trim().equals("")) {
                    newPassword.setError("Enter password");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnSendResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.VISIBLE);
                newEmail.setVisibility(View.GONE);
                oldScreenname.setVisibility(View.GONE);
                newScreenname.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.GONE);
                changeScreenname.setVisibility(View.GONE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.VISIBLE);
                remove.setVisibility(View.GONE);
            }
        });

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (!oldEmail.getText().toString().trim().equals("")) {
                    auth.sendPasswordResetEmail(oldEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(myActivity, "Reset password email is sent!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(myActivity, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                } else {
                    oldEmail.setError("Enter email");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    View viewCreate = myActivity.getLayoutInflater().inflate(R.layout.remove_user, null);
                    new AlertDialog.Builder(myActivity, R.style.MyDialogTheme)
                            .setView(viewCreate)
                            .setTitle("Remove User")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    user.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(myActivity, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(myActivity, SignupActivity.class));
                                                        myActivity.finish();
                                                        progressBar.setVisibility(View.GONE);
                                                    } else {
                                                        Toast.makeText(myActivity, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                                        progressBar.setVisibility(View.GONE);
                                                    }
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("No", null).create().show();
                }
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }


    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    public void onResume(){
        super.onResume();
        progressBar.setVisibility(View.GONE);

    }

}
