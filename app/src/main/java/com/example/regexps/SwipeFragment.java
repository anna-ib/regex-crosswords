package com.example.regexps;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import static android.graphics.Color.WHITE;

public class SwipeFragment extends Fragment {
    private int typeOfFragment = -1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipe_layout,  container, false);

        Bundle bundle = this.getArguments();
        //if (bundle != null) typeOfFragment = bundle.getInt("typeOfList");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f);
        FrameLayout.LayoutParams textParams = new  FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        switch (getTag()){
            case "horizontalSwipe": {
                String[] list;
                if (bundle != null) {
                    list = bundle.getStringArray("verticalList");
                    if (list != null) {
                        LinearLayout listLayout = view.findViewById(R.id.fragment_list);
                        listLayout.setOrientation(LinearLayout.VERTICAL);
                        for (String text : list) {
                            LinearLayout layout = new LinearLayout(getContext());
                            TextView textView = new TextView(getContext());

                            //textView.setMaxLines(1);
                            textView.setText(text);
                            textView.setTextColor(WHITE);
                            //textView.setId(i + this.width * this.height + this.width);
                            textView.setGravity(Gravity.CENTER);
                            listLayout.addView(layout, layoutParams);
                            layout.addView(textView, textParams);
                        }
                    }
                }
                break;
            }
            case "verticalSwipe": {
                String[] list;
                if (bundle != null) {
                    list = bundle.getStringArray("horizontalList");
                    if (list != null) {
                        LinearLayout listLayout = view.findViewById(R.id.fragment_list);
                        listLayout.setOrientation(LinearLayout.HORIZONTAL);
                        for (String text : list) {
                            LinearLayout layout = new LinearLayout(getContext());
                            TextView textView = new TextView(getContext());

                            //textView.setMaxLines(1);
                            textView.setText(text);
                            textView.setTextColor(WHITE);
                            //textView.setId(i + this.width * this.height + this.width);
                            textView.setGravity(Gravity.CENTER);
                            textView.setRotation(90);
                            listLayout.addView(layout, layoutParams);
                            layout.addView(textView, textParams);
                        }
                    }
                }
                break;
            }
        }
        return view;
    }
}
