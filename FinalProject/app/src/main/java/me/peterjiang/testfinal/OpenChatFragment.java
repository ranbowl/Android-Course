package me.peterjiang.testfinal;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * A simple {@link Fragment} subclass.
 */
public class OpenChatFragment extends Fragment {


    public OpenChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_open_chat_view, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Fragment2 fragment2 = new Fragment2();
        fragment2.setSeekBar(10);
        getFragmentManager().beginTransaction().replace(R.id.openChatFragment, fragment2).addToBackStack("OpenChatList").commit();
        final map mapFragment = new map();
        Switch mapSwitch = (Switch) getActivity().findViewById(R.id.mapSwitch);

        mapSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    getFragmentManager().beginTransaction().replace(R.id.openChatFragment, mapFragment).addToBackStack("OpenChatList").commit();
                } else {
                    fragment2.setSeekBar(10);
                    getFragmentManager().beginTransaction().replace(R.id.openChatFragment, fragment2).addToBackStack("OpenChatList").commit();
                }
            }
        });

    }
}
