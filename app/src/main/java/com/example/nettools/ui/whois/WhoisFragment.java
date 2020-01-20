package com.example.nettools.ui.whois;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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

public class WhoisFragment extends Fragment {

    private WhoisViewModel whoisViewModel;
    private String address;
    private View rootView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        whoisViewModel =
                ViewModelProviders.of(this).get(WhoisViewModel.class);
        View root = inflater.inflate(R.layout.fragment_whois, container, false);
        whoisViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });

        rootView = root;
        TextView whoisResultText = (TextView) rootView.findViewById(R.id.whoisResultText);
        whoisResultText.setMovementMethod(new ScrollingMovementMethod());
        Button runPingButton = (Button) root.findViewById(R.id.runWhoisButton);
        runPingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                runWhoisOnClick();
            }
        });

        return root;
    }

    public void runWhoisOnClick(){
        int addressWhoisTextId = getResources().getIdentifier("addressWhoisText", "id", getActivity().getPackageName());
        int whoisResultTextId = getResources().getIdentifier("whoisResultText", "id", getActivity().getPackageName());
        EditText addressWhoisText = (EditText) rootView.findViewById(addressWhoisTextId);
        TextView whoisResultText = (TextView) rootView.findViewById(whoisResultTextId);
        address = addressWhoisText.getText().toString();
        new Tools.whoisTask(address, whoisResultText).execute();
    }
}