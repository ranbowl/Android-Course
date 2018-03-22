package me.peterjiang.testfinal;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCreateEvent extends Fragment {

//    private EditText fromDateP;
    private EditText newEventName;
    private EditText newEventPlace;
    private EditText newEventDesc;
//    private DatePicker dpResult;
//    private TimePicker tpResult;
    private Button button;


    private EditText fromDateEtxt;
    private EditText toDateEtxt;
    private EditText fromTimeEtxt;
    private EditText toTimeEtxt;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private TimePickerDialog fromTimePickerDialog;
    private TimePickerDialog toTimePickerDialog;

    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;





    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    private View inflatedView = null;
    private Activity myActivity;
    private EventObject temp;
    private String address;

    private double longitude, latitude;
    public FragmentCreateEvent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getContext().getTheme().applyStyle(R.style.AppTheme, true);
        this.inflatedView = inflater.inflate(R.layout.fragment_fragment_create_event, container, false);

        return inflatedView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 123 && resultCode == 321) {
            address = intent.getStringExtra("address");
            longitude = intent.getDoubleExtra("longitude", -1);
            latitude = intent.getDoubleExtra("latitude", -1);
            newEventPlace.setText(address);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myActivity = getActivity();

//        fromDateP = (EditText) inflatedView.findViewById(R.id.fromDateP);
        newEventName = (EditText) inflatedView.findViewById(R.id.event_name);
        newEventPlace = (EditText) inflatedView.findViewById(R.id.event_place);
        newEventDesc = (EditText) inflatedView.findViewById(R.id.event_desc);
//        dpResult = (DatePicker) inflatedView.findViewById(R.id.datePicker);
//        tpResult = (TimePicker) inflatedView.findViewById(R.id.timePicker);
        button = (Button) inflatedView.findViewById(R.id.button_add_event);

        newEventPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), map_dialog.class);
                startActivityForResult(i, 123);
            }
        });





        dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        timeFormatter = new SimpleDateFormat("h:mm a", Locale.US);
        fromDateEtxt = (EditText) inflatedView.findViewById(R.id.etxt_fromdate);
        fromDateEtxt.setInputType(InputType.TYPE_NULL);
//        fromDateEtxt.requestFocus();

        toDateEtxt = (EditText) inflatedView.findViewById(R.id.etxt_todate);
        toDateEtxt.setInputType(InputType.TYPE_NULL);

        fromTimeEtxt = (EditText) inflatedView.findViewById(R.id.etxt_fromtime);
        fromTimeEtxt.setInputType(InputType.TYPE_NULL);

        toTimeEtxt = (EditText) inflatedView.findViewById(R.id.etxt_totime);
        toTimeEtxt.setInputType(InputType.TYPE_NULL);

        fromDateEtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromDatePickerDialog.show();
            }
        });
        toDateEtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDatePickerDialog.show();
            }
        });
        fromTimeEtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromTimePickerDialog.show();
            }
        });
        toTimeEtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toTimePickerDialog.show();
            }
        });

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(getContext(), R.style.MyDialogTheme, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fromDateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(getContext(), R.style.MyDialogTheme, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                toDateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        fromTimePickerDialog = new TimePickerDialog(getContext(), R.style.MyDialogTheme, new TimePickerDialog.OnTimeSetListener() {

            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                Calendar newTime = Calendar.getInstance();
                newTime.set(0, 0, 0, selectedHour, selectedMinute);
                fromTimeEtxt.setText(timeFormatter.format(newTime.getTime()));
            }

        },newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), false);

        toTimePickerDialog = new TimePickerDialog(getContext(), R.style.MyDialogTheme, new TimePickerDialog.OnTimeSetListener() {

            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                Calendar newTime = Calendar.getInstance();
                newTime.set(0, 0, 0, selectedHour, selectedMinute);
                toTimeEtxt.setText(timeFormatter.format(newTime.getTime()));
            }

        },newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), false);





//        test=new EventObject();

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (user != null && !newEventName.getText().toString().trim().equals("")) {

                    if(newEventName.getText().toString().isEmpty()
                            || fromDateEtxt.getText().toString().isEmpty() || toDateEtxt.getText().toString().isEmpty()
                            || fromTimeEtxt.getText().toString().isEmpty() || toTimeEtxt.getText().toString().isEmpty()){
                        Toast.makeText(myActivity,"Event name/date/time cannot be empty ", Toast.LENGTH_SHORT).show();
                        return;
                    }

//                    int day = dpResult.getDayOfMonth();
//                    int month = dpResult.getMonth() + 1;
//                    int year = dpResult.getYear();
//                    int hour   = tpResult.getCurrentHour();
//                    int minute = tpResult.getCurrentMinute();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events"); // What database can I actually talk to?
                    String EID = Helper.md5(user.getUid() + newEventName.getText().toString().trim() + fromDateEtxt.getText().toString() + fromTimeEtxt.getText().toString());
                    DatabaseReference newevent = ref.child(EID);
//                    newevent.child("eid").setValue(EID);
//                    newevent.child("name").setValue(newEventName.getText().toString().trim());
//                    newevent.child("owner").setValue(user.getUid());
//                    newevent.child("date").setValue(year+"/"+month+"/"+day);
//                    newevent.child("time").setValue(hour+":"+minute);
//                    if(!newEventPlace.getText().toString().trim().equals("")) {
//                        newevent.child("place").setValue(newEventPlace.getText().toString());
//                    }
//                    else{
//                        newevent.child("place").setValue("TBD");
//                    }
//                    if(!newEventDesc.getText().toString().trim().equals("")) {
//                        newevent.child("desc").setValue(newEventDesc.getText().toString());
//                    }
//                    else{
//                        newevent.child("desc").setValue("This guy is lazy. No description. ");
//                    }
                    temp = new EventObject(EID, newEventName.getText().toString(), user.getUid(),
                            fromDateEtxt.getText().toString(), toDateEtxt.getText().toString(),
                            fromTimeEtxt.getText().toString(), toTimeEtxt.getText().toString(),
                            newEventPlace.getText().toString(),
                            newEventDesc.getText().toString());
                    newevent.setValue(temp);
                    newevent.child("longitude").setValue(longitude);

                    newevent.child("latitude").setValue(latitude);
                    Toast.makeText(myActivity, "Add event successful! ", Toast.LENGTH_LONG).show();
                    Fragment5 fragment5 = new Fragment5();
                    fragment5.setSeekBar(10);
                    getFragmentManager().beginTransaction().replace(R.id.main_fragment, fragment5).commit();
                }
            }

        });




    }
//
//    public void myClickMethod(View view){
//        if(view == fromDateEtxt) {
//            fromDatePickerDialog.show();
//        } else if(view == toDateEtxt) {
//            toDatePickerDialog.show();
//        }
//    }



}
