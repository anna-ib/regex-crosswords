package com.example.regexps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
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
        this.reCrosswordHorizontal = game.getStringArrayExtra("reCrosswordHorizontal");
        this.reCrosswordVertical = game.getStringArrayExtra("reCrosswordVertical");
        this.height = game.getIntExtra("height", 3);
        this.width = game.getIntExtra("width", 3);
        addListenerOnBtn();
        displayGame();

    }

    private void displayGame () {
        ListView horizontalList = findViewById(R.id.horizontal_list);
        ArrayAdapter<String> horizontalAdapter = new ArrayAdapter<>(this, R.layout.regular_expression, this.reCrosswordHorizontal);
        horizontalList.setAdapter(horizontalAdapter);

        ListView verticalList = findViewById(R.id.vertical_list);
        ArrayAdapter<String> verticalAdapter = new ArrayAdapter<>(this, R.layout.regular_expression, this.reCrosswordVertical);
        verticalList.setAdapter(verticalAdapter);

        TableLayout table = findViewById(R.id.tableLayout);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < this.height; i++) {
            final TableRow row = new TableRow(this);
            row.setTag("row" + i);
            table.addView(row, rowParams);
            for (int j = 0; j < this.width; j++) {
                final EditText textInput = new EditText(this);
                textInput.setId(i*this.width + j);
                textInput.setFilters(new InputFilter[] { new InputFilter.LengthFilter(1) });
                row.addView(textInput);
            }
        }
    }

    private void addListenerOnBtn () {
        Button checkBtn = findViewById(R.id.checkBtn);
        checkBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { check(); }
                }
        );
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
