package com.example.regexps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
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

public class GameActivity extends AppCompatActivity{

    private String[] reCrosswordHorizontal, reCrosswordVertical;
    private int height, width;
    SwipeFragment swipeFragment;
    InputFragment inputFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent game = getIntent();
        this.reCrosswordHorizontal = game.getStringArrayExtra("CrosswordHorizontal");
        this.reCrosswordVertical = game.getStringArrayExtra("CrosswordVertical");
        this.height = game.getIntExtra("height", 3);
        this.width = game.getIntExtra("width", 3);
        swipeFragment = new SwipeFragment();
        inputFragment = new InputFragment();

        addListenersOnBackground();
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
            //textView.setMaxLines(1);
            layout.setId(i + this.width*this.height);

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
            //textView.setMaxLines(1);
            layout.setId(i + this.width * this.height + this.width);
            layout.setPadding(0, 0, 8, 0);

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
        final Intent focusIntent = new Intent(GameActivity.this, View.OnFocusChangeListener.class);
        focusIntent.putExtra("width", this.width);
        focusIntent.putExtra("height", this.height);

        for (int i = 0; i < this.width * this.height; i++) {
            final EditText input = findViewById(i);

            Bundle bundle = new Bundle();
            bundle.putInt("inputId", input.getId());
            bundle.putString("verticalRegexp", this.reCrosswordVertical[i / this.width]);
            bundle.putString("horizontalRegexp", this.reCrosswordHorizontal[i % this.width]);
            final Intent longClickIntent = new Intent(GameActivity.this, View.OnLongClickListener.class);
            longClickIntent.putExtra("bundle", bundle);

            input.setOnFocusChangeListener(
                    new View.OnFocusChangeListener() {
                        int height = focusIntent.getIntExtra("height", 3);
                        int width = focusIntent.getIntExtra("width", 3);
                        int inputWidth = (input.getId()) % this.width;
                        int inputHeight = (input.getId()) / this.width;
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
                                LinearLayout regexp = findViewById(this.height*this.width + inputWidth);
                                regexp.setBackgroundColor(0xFFD2DAE2);
                                regexp = findViewById(this.height*this.width + this.width + inputHeight);
                                regexp.setBackgroundColor(0xFFD2DAE2);
                                for (int i = 0; i < this.height; i++) {
                                    EditText textInput = findViewById(i * this.width + inputWidth);
                                    textInput.setBackgroundResource(R.drawable.semi_active_input);
                                }
                                for (int i = 0; i < this.width; i++) {
                                    EditText textInput = findViewById(i + inputHeight * this.width);
                                    textInput.setBackgroundResource(R.drawable.semi_active_input);
                                }
                                input.setBackgroundResource(R.drawable.active_input);
                            }
                            else {
                                LinearLayout regexp = findViewById(this.height*this.width + inputWidth);
                                regexp.setBackgroundColor(0x00000000);
                                regexp = findViewById(this.height*this.width + this.width + inputHeight);
                                regexp.setBackgroundColor(0x00000000);
                                for (int i = 0; i < this.height; i++) {
                                    EditText textInput = findViewById(i * this.width + inputWidth);
                                    textInput.setBackgroundResource(R.drawable.input);
                                }
                                for (int i = 0; i < this.width; i++) {
                                    EditText textInput = findViewById(i + inputHeight * this.width);
                                    textInput.setBackgroundResource(R.drawable.input);
                                }
                                input.setBackgroundResource(R.drawable.input);
                            }
                        }
                    }
            );

            input.setOnLongClickListener(
                    new View.OnLongClickListener() {
                        public boolean onLongClick(View view) {
                            if (getSupportFragmentManager().findFragmentByTag("verticalSwipe") == null &&
                                    getSupportFragmentManager().findFragmentByTag("horizontalSwipe") == null &&
                                    getSupportFragmentManager().findFragmentByTag("inputFragment") == null) {
                                inputFragment.setArguments(longClickIntent.getBundleExtra("bundle"));
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.add(android.R.id.content, inputFragment, "inputFragment").commit();
                                fragmentTransaction.addToBackStack("inputFragment");
                            }
                            return true;
                        }
                    }
            );
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addListenersOnBackground() {
        final Bundle bundle = new Bundle();
        bundle.putStringArray("horizontalList", this.reCrosswordHorizontal);
        bundle.putStringArray("verticalList", this.reCrosswordVertical);

        ConstraintLayout layout = findViewById(R.id.game_layout);
        layout.setOnTouchListener(new OnSwipeTouchListener(GameActivity.this) {

            public boolean onSwipeTop() {
                if (getSupportFragmentManager().findFragmentByTag("verticalSwipe") != null) {
                    getSupportFragmentManager().beginTransaction().remove(swipeFragment).commit();
                    getSupportFragmentManager().popBackStack();
                }
                return true;
            }
            public boolean onSwipeRight() {
                if (getSupportFragmentManager().findFragmentByTag("verticalSwipe") == null &&
                        getSupportFragmentManager().findFragmentByTag("horizontalSwipe") == null &&
                        getSupportFragmentManager().findFragmentByTag("inputFragment") == null) {
                    swipeFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(android.R.id.content, swipeFragment, "horizontalSwipe").commit();
                    fragmentTransaction.addToBackStack("horizontalSwipe");
                }
                return true;
            }
            public boolean onSwipeLeft() {
                if (getSupportFragmentManager().findFragmentByTag("horizontalSwipe") != null) {
                    getSupportFragmentManager().beginTransaction().remove(swipeFragment).commit();
                    getSupportFragmentManager().popBackStack();
                }
                return true;
            }
            public boolean onSwipeBottom() {
                if (getSupportFragmentManager().findFragmentByTag("verticalSwipe") == null &&
                        getSupportFragmentManager().findFragmentByTag("horizontalSwipe") == null &&
                        getSupportFragmentManager().findFragmentByTag("inputFragment") == null) {
                    swipeFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(android.R.id.content, swipeFragment, "verticalSwipe").commit();
                    fragmentTransaction.addToBackStack("verticalSwipe");
                }
                return true;
            }
            public boolean onTouch(View v, MotionEvent event) {
                if (getSupportFragmentManager().findFragmentByTag("inputFragment") != null) {
                    getSupportFragmentManager().beginTransaction().remove(inputFragment).commit();
                    getSupportFragmentManager().popBackStack();
                }
                else if (getSupportFragmentManager().findFragmentByTag("verticalSwipe") != null ||
                        getSupportFragmentManager().findFragmentByTag("horizontalSwipe") != null) {
                    getSupportFragmentManager().beginTransaction().remove(swipeFragment).commit();
                    getSupportFragmentManager().popBackStack();
                }
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    private void check() {
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
