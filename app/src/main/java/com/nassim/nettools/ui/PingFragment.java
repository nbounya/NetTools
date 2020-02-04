package com.nassim.nettools.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.nassim.nettools.R;
import com.nassim.nettools.tools.Tools;

public class PingFragment extends Fragment {
    private View rootView;
    private String address;
    private int packets;
    private int ttl;
    private boolean isRunning = false;
    Tools.pingTask ping = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ping, container, false);
        Button runPingButton = (Button) rootView.findViewById(R.id.runPingButton);
        runPingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                runPingOnClick();
            }
        });

        return rootView;
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
        ttl = Integer.parseInt(ttlText.getText().toString());
        if(isRunning) {
            ping.cancel(true);
            isRunning = false;
        }
        ping = new Tools.pingTask(address, packets, ttl, 1, false, pingResultText);
        ping.execute();
        isRunning = true;
    }
}