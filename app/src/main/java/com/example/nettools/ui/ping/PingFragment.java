package com.example.nettools.ui.ping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.nettools.R;
import com.example.nettools.tools.Tools;

public class PingFragment extends Fragment {

    private PingViewModel pingViewModel;
    private View rootView;
    private String address;
    private int packets;
    private int ttl;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        pingViewModel =
                ViewModelProviders.of(this).get(PingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_ping, container, false);
        pingViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });

        rootView = root;
        Button runPingButton = (Button) root.findViewById(R.id.runPingButton);
        runPingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                runPingOnClick();
            }
        });

        return root;
    }


    public void runPingOnClick(){
        int addressTextId = getResources().getIdentifier("addressPingText", "id", getActivity().getPackageName());
        int packetsTextId = getResources().getIdentifier("packetsPingNumber", "id", getActivity().getPackageName());
        int ttlTextId = getResources().getIdentifier("ttlPingNumber", "id", getActivity().getPackageName());
        int pingResultTextId = getResources().getIdentifier("pingResultText", "id", getActivity().getPackageName());
        EditText addressText = (EditText) rootView.findViewById(addressTextId);
        EditText packetsText = (EditText) rootView.findViewById(packetsTextId);
        EditText ttlText = (EditText) rootView.findViewById(ttlTextId);
        TextView pingResultText = (TextView) rootView.findViewById(pingResultTextId);
        address = addressText.getText().toString();
        packets = Integer.parseInt(packetsText.getText().toString());
        ttl = Integer.parseInt(ttlText.getText().toString());;

        new Tools.pingTask(address, packets, ttl, 1, pingResultText).execute();
    }
}