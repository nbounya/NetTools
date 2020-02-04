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

public class HttpFragment extends Fragment {
    private View rootView;
    private String url;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_http, container, false);
        Button runPingButton = (Button) rootView.findViewById(R.id.runHttpButton);
        runPingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                runHttpStatusOnClick();
            }
        });

        return rootView;
    }

    public void runHttpStatusOnClick(){
        int urlHttpTextId = getResources().getIdentifier("urlHttpText", "id", getActivity().getPackageName());
        int httpResultTextId = getResources().getIdentifier("httpResultText", "id", getActivity().getPackageName());
        EditText urlHttpText = (EditText) rootView.findViewById(urlHttpTextId);
        TextView httpResultText = (TextView) rootView.findViewById(httpResultTextId);
        url = urlHttpText.getText().toString();

        new Tools.HttpStatusTask(url, httpResultText).execute();
    }
}