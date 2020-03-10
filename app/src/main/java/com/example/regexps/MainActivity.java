package com.example.regexps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MainActivity extends AppCompatActivity {

    Button startBtn;
    BottomSheetDialog bottomSelectComplexity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addListenerOnBtn();
    }

    public void addListenerOnBtn () {
        startBtn = findViewById(R.id.button_start);
        startBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialogNotificationAction();
                    }
                }
        );

    }
    private void showDialogNotificationAction() {

        final View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog,
                new ViewGroup(this) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {}
        },
                false);
        bottomSelectComplexity = new BottomSheetDialog(this);
        bottomSelectComplexity.setContentView(sheetView);
        bottomSelectComplexity.show();

        FrameLayout bottomSheet = bottomSelectComplexity.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) bottomSheet.setBackground(null);

        LinearLayout eazy = sheetView.findViewById(R.id.bottom_sheet_eazy);
        LinearLayout medium = sheetView.findViewById(R.id.bottom_sheet_medium);
        LinearLayout hard = sheetView.findViewById(R.id.bottom_sheet_hard);
        LinearLayout cancel = sheetView.findViewById(R.id.bottom_sheet_cancel);

        eazy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSelectComplexity.dismiss();
                startGame(3, 3);
            }
        });
        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSelectComplexity.dismiss();
                startGame(4, 4);
            }
        });
        hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSelectComplexity.dismiss();
                startGame(5, 5);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSelectComplexity.dismiss();
            }
        });
    }

    void startGame(int crosswordWidth, int crosswordHeight) {
        CrosswordGenerator crossword = new CrosswordGenerator(crosswordWidth, crosswordHeight);
        crossword.createCrossword();
        Intent game = new Intent(MainActivity.this, GameActivity.class);
        game.putExtra("CrosswordHorizontal", crossword.getCrosswordHorizontal());
        game.putExtra("CrosswordVertical", crossword.getCrosswordVertical());
        game.putExtra("height", crossword.getHeight());
        game.putExtra("width", crossword.getWidth());
        startActivity(game);
    }
}
