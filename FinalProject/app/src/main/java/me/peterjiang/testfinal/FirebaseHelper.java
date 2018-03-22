package me.peterjiang.testfinal;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by PeterJiang on 11/6/16.
 */

public class FirebaseHelper {

    public static final String TAG = "FirebaseHelper";
    private Activity mActivity;
    final public ArrayList<String> result = new ArrayList<>();


//    public FirebaseHelper(Activity myActivity) {
    public FirebaseHelper() {
//        mActivity = myActivity;
    }

    public void addUser(String getEmail){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users"); // What database can I actually talk to?
        DatabaseReference user = ref.child(getEmail);
//        DatabaseReference time = yj221.child(latesttime);
//        time.child("Time").setValue(latesttime);
        user.child("Uid").setValue(getEmail, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null){
                    //Error!
//                    Toast.makeText(mActivity, "Failed to add to customized firebase", Toast.LENGTH_LONG).show();

                }
                else{
                    //Success
//                    Toast.makeText(mActivity, "Add user successful!", Toast.LENGTH_LONG).show();

                }
            }
        });



    }

    public void getScreenname(String getID){
        result.add("");
//        final String result;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users"); // What database can I actually talk to?
        DatabaseReference user = ref.child(getID);
        user.child("Screenname").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                result.add(0, snapshot.getValue().toString());  //prints "Do you have data? You'll love Firebase."
//                result = snapshot.getValue().toString();
//                System.out.println(snapshot.getValue().toString());
                Log.e(TAG+"getScreen", snapshot.getValue().toString());

            }
            @Override public void onCancelled(DatabaseError error) { }
        });
//        return result;
    }

    public void updateScreenname(String getNewScreenname, String getID){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users"); // What database can I actually talk to?
        DatabaseReference user = ref.child(getID);
//        DatabaseReference time = yj221.child(latesttime);
//        time.child("Time").setValue(latesttime);
        user.child("Uid").setValue(getID);
        user.child("Screenname").setValue(getNewScreenname, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null){
                    //Error!
//                    Toast.makeText(mActivity, "Failed to update screen name!", Toast.LENGTH_LONG).show();

                }
                else{
                    //Success
//                    Toast.makeText(mActivity, "Screen name updated!", Toast.LENGTH_LONG).show();

                }
            }
        });



    }



    public ArrayList<String> getStringfromFireBase(){

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Students"); // What database can I actually talk to?

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String s) {
                DatabaseReference netid = ref.child(snapshot.getKey());
                netid.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        result.add(0, dataSnapshot.child("date").toString() +
                                dataSnapshot.child("netid").toString() +
                                dataSnapshot.child("x").toString() +
                                dataSnapshot.child("y").toString()
                        );
                        if(result.size() > 200){
                            result.subList(200, result.size()).clear();
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onChildRemoved(DataSnapshot snapshot) {

            }
            @Override
            public void onChildChanged(DataSnapshot snapshot, String s) {

            }
            @Override
            public void onChildMoved(DataSnapshot snapshot, String s) {

            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }
        });



        return result;

    }


    public String returnString(String Value){
        return Value;
    }

}
