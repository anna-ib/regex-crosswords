package com.example.regexps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameActivity extends AppCompatActivity {

    private String[] reCrosswordHorizontal;
    private String[] reCrosswordVertical;
    private int height;
    private int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent game = getIntent();
        this.reCrosswordHorizontal = game.getStringArrayExtra("CrosswordHorizontal");
        this.reCrosswordVertical = game.getStringArrayExtra("CrosswordVertical");
        this.height = game.getIntExtra("height", 3);
        this.width = game.getIntExtra("width", 3);

        addListenerOnBtn();
        displayGame();
    }

    private void displayGame () {
        LinearLayout table = findViewById(R.id.inputs_grid);
        GradientDrawable border = new GradientDrawable();
        border.setStroke(1, 0xFF6C7578);
        table.setBackground(border);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f);
        FrameLayout.LayoutParams textParams = new  FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        InputFilter[] filters = { new InputFilter.LengthFilter(1) };

        LinearLayout horizontalList = findViewById(R.id.horizontal_list);
        for (int i = 0; i < this.width; i++) {
            LinearLayout layout = new LinearLayout(this);
            TextView textView = new TextView(this);
            textView.setMaxLines(1);
            textView.setText(reCrosswordHorizontal[i]);
            textView.setRotation(90);
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
            horizontalList.addView(layout, layoutParams);
            layout.addView(textView, textParams);
        }

        LinearLayout verticalList = findViewById(R.id.vertical_list);
        for (int i = 0; i < this.height; i++) {
            LinearLayout layout = new LinearLayout(this);
            TextView textView = new TextView(this);
            textView.setMaxLines(1);
            textView.setText(reCrosswordVertical[i]);
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
            verticalList.addView(layout, layoutParams);
            layout.addView(textView, textParams);
        }

        for (int i = 0; i < this.height; i++) {
            LinearLayout row = new LinearLayout(this);
            table.addView(row, layoutParams);

            for (int j = 0; j < this.width; j++) {
                LinearLayout layout = new LinearLayout(this);
                row.addView(layout, layoutParams);
                EditText textInput = new EditText(this);

                textInput.setId(i*this.width + j);
                textInput.setBackgroundResource(R.drawable.input);
                textInput.setFilters(filters);
                textInput.setGravity(Gravity.CENTER);
                textInput.setCursorVisible(false);
                textInput.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

                layout.addView(textInput, textParams);
            }
        }

        addListenersOnInputs();
    }

    private void addListenerOnBtn () {
        Button checkBtn = findViewById(R.id.check_button);
        checkBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { check(); }
                }
        );
    }

    private void addListenersOnInputs () {
        for (int i = 0; i < this.width * this.height; i++) {
            final EditText input = findViewById(i);
            input.setOnFocusChangeListener(
                    new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) input.setBackgroundResource(R.drawable.active_input);
                            else input.setBackgroundResource(R.drawable.input);
                        }
                    }
            );
        }
    }

    private void check () {
        int count = 0;
        for (int i = 0; i < this.height; i++) {
            StringBuilder str = new StringBuilder();
            for (int j = 0; j < this.width; j++) {
                final EditText elem = findViewById(i * this.width + j);
                str.append(elem.getText().toString());
            }
            final Pattern r = Pattern.compile(reCrosswordVertical[i]);
            final Matcher m = r.matcher(str.toString());
            if (!m.matches()) {
                Toast.makeText(
                        GameActivity.this,
                        "Not ok, height: " + (i + 1),
                        Toast.LENGTH_LONG
                ).show();
                count += 1;
            }
        }

        for (int i = 0; i < this.width; i++) {
            StringBuilder str = new StringBuilder();
            for (int j = 0; j < this.height; j++) {
                final EditText elem = findViewById(j * this.width + i);
                str.append(elem.getText().toString());
            }
            final Pattern r = Pattern.compile(reCrosswordHorizontal[i]);
            final Matcher m = r.matcher(str.toString());
            if (!m.matches()) {
                Toast.makeText(
                        GameActivity.this,
                        "Not ok, width: " + (i + 1),
                        Toast.LENGTH_LONG
                ).show();
                count += 1;
            }
        }

        if (count == 0) {
            Toast.makeText(
                    GameActivity.this,
                    "Ok!!!!!",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}
