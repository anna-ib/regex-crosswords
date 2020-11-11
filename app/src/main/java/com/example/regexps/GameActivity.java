package com.example.regexps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameActivity extends AppCompatActivity{

    private String[] reCrosswordHorizontal, reCrosswordVertical;
    private int height, width;
    private Timer timer;
    private int mistakesNum = 0, maxMistakesNum = 3;
    // SwipeFragment swipeFragment;
    // InputFragment inputFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game1);

        Intent game = getIntent();
        this.reCrosswordHorizontal = game.getStringArrayExtra("CrosswordHorizontal");
        this.reCrosswordVertical = game.getStringArrayExtra("CrosswordVertical");
        this.height = game.getIntExtra("height", 3);
        this.width = game.getIntExtra("width", 3);
        timer = new Timer ((TextView) findViewById(R.id.timer));
        // swipeFragment = new SwipeFragment();
        // inputFragment = new InputFragment();

        //addListenersOnBackground();
        addListenerOnBtns();
        displayGame();
        timer.startTimer();
    }

    // TODO: Set Listeners on background
    // TODO: Colors
    // TODO: Create "EndOfGame" activity


    private void displayGame () {
        /* IDs:
        * inputs            |                      0 <= id < width * height
        * horizontal regexes|         width * height <= id < width * height + width
        * vertical regexes  | width * height + width <= id < width * height + width + height
        */

        // Setting border for table of inputs
        LinearLayout table = findViewById(R.id.inputs_grid);
        GradientDrawable border = new GradientDrawable();
        border.setStroke(1, ContextCompat.getColor(getBaseContext(), R.color.border));
        table.setBackground(border);

        // Setting the size of the input table depending on the window size
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        try {
            display.getRealSize(size); // TODO: API 17 current min is 16
        }
        catch (NoSuchMethodError err) {
            display.getSize(size);
        }
        double curSideWidth = size.x * 0.8 / this.width;
        double curSideHeight = size.y * 0.5 / this.height;
        int inputSideSize = (int)Math.floor(Math.min(curSideWidth, curSideHeight));

        ViewGroup.LayoutParams tableParams = table.getLayoutParams();
        tableParams.height = inputSideSize * this.height;
        tableParams.width = inputSideSize * this.width;
        table.setLayoutParams(tableParams);

        // TODO
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f);

        // Adds regex text to horizontal column
        LinearLayout horizontalList = findViewById(R.id.horizontal_list);
        fillWithRegexes(horizontalList, this.reCrosswordHorizontal, this.width * this.height, params);

        // Adds regex text to vertical column
        LinearLayout verticalList = findViewById(R.id.vertical_list);
        fillWithRegexes(verticalList, this.reCrosswordVertical, this.width * this.height + this.width, params);

        fillWithInputs(table, params);
        addListenersOnInputs();
        addListenersOnRegexes();


    }

    private void fillWithRegexes (LinearLayout list, String[] regExes, int baseId, LinearLayout.LayoutParams params) {
        /* TODO */
        if (regExes == null) {
            /* TODO: Logs and error */
            return ;
        }
        for (int i = 0; i < regExes.length; i++) {
            LinearLayout layout = new LinearLayout(this);
            TextView textView = new TextView(this);
            layout.setId(i + baseId);
            layout.setFocusable(true);
            layout.setFocusableInTouchMode(true);
            layout.setClickable(true);

            textView.setText(regExes[i]);
            textView.setGravity(Gravity.CENTER);
            list.addView(layout, params);
            layout.addView(textView, params);
        }
    }

    private void fillWithInputs (LinearLayout table, LinearLayout.LayoutParams params) {
        // TODO
        InputFilter[] filters = { new InputFilter.LengthFilter(1) };

        for (int i = 0; i < this.height; i++) {
            LinearLayout row = new LinearLayout(this);
            table.addView(row, params);

            for (int j = 0; j < this.width; j++) {
                LinearLayout layout = new LinearLayout(this);
                row.addView(layout, params);
                EditText textInput = new EditText(this);

                textInput.setId(i * this.width + j);
                textInput.setBackgroundResource(R.drawable.input);
                textInput.setFilters(filters);
                textInput.setGravity(Gravity.CENTER);
                textInput.setCursorVisible(false);
                textInput.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                textInput.setShowSoftInputOnFocus(false); // TODO: API 21 current min is 16

                layout.addView(textInput, params);
            }
        }
    }

    private void addListenerOnBtns () {
        Button checkBtn = findViewById(R.id.check_button);
        checkBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkCrossword();
                    }
                }
        );
        Button pauseBtn = findViewById(R.id.pause_button);
        pauseBtn.setOnClickListener(
                new View.OnClickListener() {
                    boolean isPaused = false;

                    @Override
                    public void onClick(View v) {
                        if (isPaused) {
                            isPaused = false;
                            timer.continueTimer();
                        }
                        else {
                            isPaused = true;
                            timer.pauseTimer();
                        }
                    }
                }
        );

    }

    private void addListenersOnInputs () {
        final Intent focusIntent = new Intent(GameActivity.this, View.OnFocusChangeListener.class);
        focusIntent.putExtra("width", this.width);
        focusIntent.putExtra("height", this.height);

        for (int i = 0; i < this.width * this.height; i++) {
            final EditText input = findViewById(i);

            input.setOnFocusChangeListener(
                    new View.OnFocusChangeListener() {
                        int height = focusIntent.getIntExtra("height", 3);
                        int width = focusIntent.getIntExtra("width", 3);
                        int inputWidth = (input.getId()) % width;
                        int inputHeight = (input.getId()) / width;
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            // Changes the colors of the corresponding regexes and input fields

                            // Regexes in "Vertical" and "Horizontal" columns
                            LinearLayout verticalRegex = findViewById(height * width + inputWidth);
                            LinearLayout horizontalRegex = findViewById(height * width + width + inputHeight);

                            if (hasFocus) {
                                verticalRegex.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.semiActive));
                                horizontalRegex.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.semiActive));
                                setBkgOnSemiActiveInputs(R.drawable.semi_active_input, inputHeight, inputWidth);
                                input.setBackgroundResource(R.drawable.active_input);
                            }
                            else {
                                verticalRegex.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.transparent));
                                horizontalRegex.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.transparent));
                                setBkgOnSemiActiveInputs(R.drawable.input, inputHeight, inputWidth);
                                input.setBackgroundResource(R.drawable.input);
                            }
                        }
                    }
            );

            input.setOnLongClickListener(
                    new View.OnLongClickListener() {
                        public boolean onLongClick(View view) {
                            showSoftKeyboard(input);
                            return true;
                        }
                    }
            );
        }
    }

    private void addListenersOnRegexes () {
        int baseId = this.height * this.height;
        final Intent focusIntent = new Intent(GameActivity.this, View.OnClickListener.class);
        focusIntent.putExtra("width", this.width);
        focusIntent.putExtra("height", this.height);
        for (int i = baseId; i < baseId + this.height + this.width; i++) {
            final LinearLayout regex = findViewById(i);

            regex.setOnFocusChangeListener(
                    new View.OnFocusChangeListener() {
                        int height = focusIntent.getIntExtra("height", 3);
                        int width = focusIntent.getIntExtra("width", 3);
                        int baseId = height * width;
                        int regexId = regex.getId();
                        Integer inputsHeight = regexId - baseId < width ? null : regexId - baseId - width;
                        Integer inputsWidth = regexId - baseId < width ? regexId - baseId : null;
                        public void onFocusChange (View v, boolean hasFocus) {
                            if (hasFocus) {
                                regex.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.semiActive));
                                setBkgOnSemiActiveInputs(R.drawable.semi_active_input, inputsHeight, inputsWidth);
                            }
                            else {
                                regex.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.transparent));
                                setBkgOnSemiActiveInputs(R.drawable.input, inputsHeight, inputsWidth);
                            }
                        }
                    }
            );
        }

    }

    private void setBkgOnSemiActiveInputs (int resource, Integer inputHeight, Integer inputWidth) {
        /*
        Changes the background color to the "resource" color at the input field
        located at the width "inputWidth" and the height "inputHeight"
         */
        if (inputWidth != null) {
            for (int i = 0; i < this.height; i++) {
                EditText textInput = findViewById(i * this.width + inputWidth);
                textInput.setBackgroundResource(resource);
            }
        }
        if (inputHeight != null) {
            for (int i = 0; i < this.width; i++) {
                EditText textInput = findViewById(inputHeight * this.width + i);
                textInput.setBackgroundResource(resource);
            }
        }
    }

    private void showSoftKeyboard (View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.showSoftInput(view, 0);
    }

   /*
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
    */

    private void checkCrossword () {
        boolean endOfGame;
        for (int i = 0; i < this.height; i++) {
            StringBuilder str = new StringBuilder();
            for (int j = 0; j < this.width; j++) {
                final EditText elem = findViewById(i * this.width + j);
                str.append(elem.getText().toString());
            }
            final Pattern r = Pattern.compile(reCrosswordVertical[i]);
            final Matcher m = r.matcher(str.toString());
            if (!m.matches()) {
                endOfGame = incrMistakesNum();
                Toast.makeText(
                        GameActivity.this,
                        "Not ok, height: " + (i + 1),
                        Toast.LENGTH_LONG
                ).show();
                if (endOfGame) System.out.println("END");
                return;
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
                endOfGame = incrMistakesNum();
                Toast.makeText(
                        GameActivity.this,
                        "Not ok, width: " + (i + 1),
                        Toast.LENGTH_LONG
                ).show();
                if (endOfGame) System.out.println("END");
                return;
            }
        }
        Toast.makeText(
                GameActivity.this,
                "Ok!!!!!",
                Toast.LENGTH_LONG
        ).show();

    }

    private boolean incrMistakesNum () {
        this.mistakesNum += 1;
        TextView mistakesNumText = findViewById(R.id.mistakes);
        mistakesNumText.setText(String.format("Mistakes: %d/%d", this.mistakesNum, this.maxMistakesNum));
        return this.mistakesNum >= this.maxMistakesNum;
    }
}
