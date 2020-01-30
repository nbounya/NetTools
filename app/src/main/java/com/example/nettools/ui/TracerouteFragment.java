package com.example.nettools.ui;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nettools.R;
import com.example.nettools.tools.Tools;

public class TracerouteFragment extends Fragment {
    private View rootView;
    private String address;
    private int max_ttl;
    private int first_ttl;
    private boolean resolve = true;
    private int probes = 3;
    boolean isRunning = false;
    Tools.traceRouteTask traceroute = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_traceroute, container, false);

        TextView tracerouteResultText = rootView.findViewById(R.id.tracerouteResultText);
        tracerouteResultText.setMovementMethod(new ScrollingMovementMethod());
        Button runPingButton = (Button) rootView.findViewById(R.id.runTracerouteButton);
        runPingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                runTracerouteOnClick();
            }
        });

        return rootView;
    }

    public void runTracerouteOnClick(){
        int addressTracerouteTextId = getResources().getIdentifier("addressTracerouteText", "id", getActivity().getPackageName());
        int maxTtlTracerouteTextId = getResources().getIdentifier("maxTtlTracerouteNumber", "id", getActivity().getPackageName());
        int firstTtlTracerouteTextId = getResources().getIdentifier("firstTtlTracerouteNumber", "id", getActivity().getPackageName());
        int tracerouteResultTextId = getResources().getIdentifier("tracerouteResultText", "id", getActivity().getPackageName());
        EditText addressTracerouteText = (EditText) rootView.findViewById(addressTracerouteTextId);
        EditText maxTtlTracerouteText = (EditText) rootView.findViewById(maxTtlTracerouteTextId);
        EditText firstTtlTracerouteText = (EditText) rootView.findViewById(firstTtlTracerouteTextId);
        TextView tracerouteResultText = (TextView) rootView.findViewById(tracerouteResultTextId);
        address = addressTracerouteText.getText().toString();
        max_ttl = Integer.parseInt(maxTtlTracerouteText.getText().toString());
        first_ttl = Integer.parseInt(firstTtlTracerouteText.getText().toString());
        if(isRunning) {
            traceroute.cancel(true);
            isRunning = false;
            while (true) {
                if (traceroute.isCancelled()) {
                    break;
                }
            }
        }
        traceroute = new Tools.traceRouteTask(address, max_ttl, first_ttl, resolve, probes, tracerouteResultText);
        traceroute.execute();
        isRunning = true;
    }
}
