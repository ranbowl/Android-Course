package me.peterjiang.testfinal;


import android.app.Activity;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sendbird.android.User;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEventDetail extends Fragment {

    //Event Detail Page

    private Activity myActivity;
    private View inflatedView = null;
    private EventObject displayEvent  = new EventObject();
    private TextView eventname, eventdate, eventtime, eventplace, eventdesc;
//    private String eid, name, owner, date, time, place, desc;
    private Button btnRSVP, btnRSVPN, btnRSVPC, btnDelete;
//    private ArrayList<String> RSVP_attend, RSVP_not;
//    private ArrayList<String> AttendList = new ArrayList<>(), NotList = new ArrayList<>();
    private ArrayAdapter adapter,adapter2;
    private ListView AttendLV, NotLV;
    private String UserName;
    private boolean Expiredflag = false;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;


    private String TAG = "EVENTDETAIL";

    public FragmentEventDetail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflatedView = inflater.inflate(R.layout.fragment_fragment_event_detail, container, false);
        return inflatedView;

    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        myActivity = getActivity();

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseHelper user_cus = new FirebaseHelper();
        if(user.getDisplayName()!=null) {
            UserName = user.getDisplayName();
        }
        else{
            Log.e(TAG,"UserName == null");
        }


        eventname = (TextView) inflatedView.findViewById(R.id.eventname);
        eventdate = (TextView) inflatedView.findViewById(R.id.eventdate);
        eventtime = (TextView) inflatedView.findViewById(R.id.eventtime);
        eventplace = (TextView) inflatedView.findViewById(R.id.eventplace);
        eventdesc = (TextView) inflatedView.findViewById(R.id.eventdesc);

        btnRSVPC = (Button) inflatedView.findViewById(R.id.RSVP_cancel);
        btnRSVP = (Button) inflatedView.findViewById(R.id.RSVP_attend);
        btnRSVPN = (Button) inflatedView.findViewById(R.id.RSVP_not);
        btnDelete = (Button) inflatedView.findViewById(R.id.Event_delete);
        btnRSVP.setVisibility(View.GONE);
        btnRSVPN.setVisibility(View.GONE);
        btnRSVPC.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);

//        adapter = new ArrayAdapter<>(myActivity,android.R.layout.simple_list_item_1,AttendList);
//        adapter2 = new ArrayAdapter<>(myActivity,android.R.layout.simple_list_item_1,NotList);
        adapter = new ArrayAdapter<>(myActivity,android.R.layout.simple_list_item_1,displayEvent.RSVP_attend);
        adapter2 = new ArrayAdapter<>(myActivity,android.R.layout.simple_list_item_1,displayEvent.RSVP_not);
        AttendLV = (ListView) myActivity.findViewById(R.id.AttendLV);
        NotLV = (ListView) myActivity.findViewById(R.id.NotLV);
        AttendLV.setAdapter(adapter);
        NotLV.setAdapter(adapter2);

//        RSVP_attend = new ArrayList<>();
//        RSVP_not = new ArrayList<>();


//        Log.e(TAG, displayEvent.getEID());
//        Log.e(TAG, displayEvent.getName());

        eventname.setText(displayEvent.Name);
        eventdate.setText(displayEvent.fromDate +" " + displayEvent.fromTime);
        eventtime.setText(displayEvent.toDate +" " + displayEvent.toTime);
        eventplace.setText(displayEvent.Place);
        eventdesc.setText(displayEvent.Desc);

        eventplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), event_map.class);
                i.putExtra("longitude", displayEvent.longitude);
                i.putExtra("latitude", displayEvent.latitude);
                i.putExtra("name", displayEvent.Name);
                startActivity(i);
            }
        });








//        btnRSVP.setVisibility(View.GONE);
//        btnRSVPN.setVisibility(View.GONE);
//        btnRSVPC.setVisibility(View.GONE);

        if((displayEvent.RSVP_attend != null && displayEvent.RSVP_attend.contains(user.getDisplayName())) || (displayEvent.RSVP_not != null && displayEvent.RSVP_not.contains(user.getDisplayName()))) {
            btnRSVPC.setVisibility(View.VISIBLE);
            Log.e(TAG, "RSVPed");
        }
        else{
            btnRSVP.setVisibility(View.VISIBLE);
            btnRSVPN.setVisibility(View.VISIBLE);
            Log.e(TAG, "NO RSVP");
        }

//        Log.e(TAG,displayEvent.Owner.toString()+"=="+user.getUid());
        if(displayEvent.Owner.equals(user.getUid())){
            btnDelete.setVisibility(View.VISIBLE);
        }

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View viewCreate = myActivity.getLayoutInflater().inflate(R.layout.event_delete, null);
                new AlertDialog.Builder(myActivity, R.style.MyDialogTheme)
                        .setView(viewCreate)
                        .setTitle("Delete Event")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events"); // What database can I actually talk to?
                                ref.child(displayEvent.EID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Fragment5 fragment5 = new Fragment5();
                                        getFragmentManager().beginTransaction().replace(R.id.main_fragment, fragment5).commit();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null).create().show();
            }
        });


        btnRSVP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events"); // What database can I actually talk to?
                DatabaseReference event = ref.child(displayEvent.EID);

//                Log.e(TAG,Integer.toString(displayEvent.RSVP_attend.size()));
                if(UserName != null) {
                    displayEvent.attend(UserName);
//                adapter.add(UserName);
//                Log.e(TAG,Integer.toString(displayEvent.RSVP_attend.size()));
//                    Log.e(TAG, (displayEvent.RSVP_attend.get(0)));
//                    Log.e(TAG, "AttendAdd");
                    event.setValue(displayEvent).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                            Log.e(TAG, "btnGONE");
                            btnRSVP.setVisibility(View.GONE);
                            btnRSVPN.setVisibility(View.GONE);
                            btnRSVPC.setVisibility(View.VISIBLE);
                            if (displayEvent.Owner.equals(user.getUid())) {
                                btnDelete.setVisibility(View.VISIBLE);
                            }

                        }
                    });
//                AttendList = displayEvent.RSVP_attend;
                    adapter.notifyDataSetChanged();
                }
            }
        });

        btnRSVPN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events"); // What database can I actually talk to?
                DatabaseReference event = ref.child(displayEvent.EID);

                if(UserName != null) {

                    displayEvent.notgo(UserName);
//                adapter2.add(UserName);
                    event.setValue(displayEvent).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            btnRSVP.setVisibility(View.GONE);
                            btnRSVPN.setVisibility(View.GONE);
                            btnRSVPC.setVisibility(View.VISIBLE);
                            if (displayEvent.Owner.equals(user.getUid())) {
                                btnDelete.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    adapter2.notifyDataSetChanged();
                }
            }
        });

        btnRSVPC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events"); // What database can I actually talk to?
                DatabaseReference event = ref.child(displayEvent.EID);

                if(UserName != null) {

                    displayEvent.cancel(UserName);
                    event.setValue(displayEvent).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            btnRSVP.setVisibility(View.VISIBLE);
                            btnRSVPN.setVisibility(View.VISIBLE);
                            btnRSVPC.setVisibility(View.GONE);
                            if (displayEvent.Owner.equals(user.getUid())) {
                                btnDelete.setVisibility(View.VISIBLE);
                            }
                        }
                    });
//                AttendList = displayEvent.RSVP_attend;
                    adapter.notifyDataSetChanged();
                    adapter2.notifyDataSetChanged();
                }
            }
        });

        if(Expiredflag){
            btnRSVP.setVisibility(View.GONE);
            btnRSVPN.setVisibility(View.GONE);
            btnRSVPC.setVisibility(View.GONE);
        }



    }

    public void setExpired(boolean setE){
        Expiredflag = setE;
    }



    public void getevent(EventObject getEvent){
//        displayEvent = getEvent;
//        Log.e(TAG, displayEvent.getEID());
//        Log.e(TAG, displayEvent.getName());

        displayEvent.EID = getEvent.EID;
        displayEvent.Name = getEvent.Name;
        displayEvent.Owner = getEvent.Owner;
        displayEvent.fromDate = getEvent.fromDate;
        displayEvent.toDate = getEvent.toDate;
        displayEvent.fromTime = getEvent.fromTime;
        displayEvent.toTime = getEvent.toTime;
        displayEvent.Place = getEvent.Place;
        displayEvent.Desc = getEvent.Desc;
        displayEvent.latitude = getEvent.latitude;
        displayEvent.longitude = getEvent.longitude;
//        if(getEvent.RSVP_attend != null) {
//            displayEvent.RSVP_attend = getEvent.RSVP_attend;
//        }
//        else{
//            displayEvent.RSVP_attend = new ArrayList<>();
//        }
//        if(getEvent.RSVP_not != null) {
//            displayEvent.RSVP_not = getEvent.RSVP_not;
//        }
//        else{
//            displayEvent.RSVP_not = new ArrayList<>();
//        }
        if(getEvent.RSVP_attend != null) {
            displayEvent.RSVP_attend = getEvent.RSVP_attend;
//            Log.e(TAG, "getEventAttend not null");
        }
        if(getEvent.RSVP_not != null) {
            displayEvent.RSVP_not = getEvent.RSVP_not;
        }
//        AttendList = displayEvent.RSVP_attend;
//        NotList = displayEvent.RSVP_not;

    }



    //sign out method
    public void signOut() {
        auth.signOut();
    }

}
