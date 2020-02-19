package com.nassim.nettools.ui;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.nassim.nettools.R;
import com.nassim.nettools.tools.Tools;

public class IfconfigFragment extends Fragment {

    private View rootView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_ifconfig, container, false);
        TextView whoisResultText = (TextView) rootView.findViewById(R.id.ifconfigResultText);
        whoisResultText.setMovementMethod(new ScrollingMovementMethod());
        Button runPingButton = (Button) rootView.findViewById(R.id.runIfconfigButton);
        runPingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                runIfconfigOnClick();
            }
        });

        return rootView;
    }

    public void runIfconfigOnClick(){
        int ifconfigResultTextId = getResources().getIdentifier("ifconfigResultText", "id", getActivity().getPackageName());
        TextView ifconfigResultText = (TextView) rootView.findViewById(ifconfigResultTextId);
        new Tools.IfconfigTask(ifconfigResultText).execute();
    }
}