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

public class WhoisFragment extends Fragment {

    private String address;
    private View rootView;
    boolean isRunning = false;
    Tools.whoisTask whois = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_whois, container, false);
        TextView whoisResultText = (TextView) rootView.findViewById(R.id.whoisResultText);
        whoisResultText.setMovementMethod(new ScrollingMovementMethod());
        Button runPingButton = (Button) rootView.findViewById(R.id.runWhoisButton);
        runPingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                runWhoisOnClick();
            }
        });

        return rootView;
    }

    public void runWhoisOnClick(){
        int addressWhoisTextId = getResources().getIdentifier("addressWhoisText", "id", getActivity().getPackageName());
        int whoisResultTextId = getResources().getIdentifier("whoisResultText", "id", getActivity().getPackageName());
        EditText addressWhoisText = (EditText) rootView.findViewById(addressWhoisTextId);
        TextView whoisResultText = (TextView) rootView.findViewById(whoisResultTextId);
        address = addressWhoisText.getText().toString();
        if(isRunning) {
            whois.cancel(true);
            isRunning = false;
        }
        whois = new Tools.whoisTask(address, whoisResultText);
        whois.execute();
        isRunning = true;
    }
}