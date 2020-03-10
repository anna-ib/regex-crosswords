package com.example.regexps;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import static android.graphics.Color.WHITE;

public class InputFragment extends Fragment {
    private int inputId;
    private View view;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.on_long_click_layout,  container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            inputId = bundle.getInt("inputId");

            TextView vRegexp = view.findViewById(R.id.verticalText);
            vRegexp.setTextColor(WHITE);
            vRegexp.setText(bundle.getString("verticalRegexp"));

            TextView hRegexp = view.findViewById(R.id.horizontalText);
            hRegexp.setTextColor(WHITE);
            hRegexp.setText(bundle.getString("horizontalRegexp"));
        }
        setListenerOnInput();
        return view;
    }

    private void setListenerOnInput() {
        EditText input = view.findViewById(R.id.input);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getActivity() != null) {
                    EditText activityInput = getActivity().findViewById(inputId);
                    activityInput.setText(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
