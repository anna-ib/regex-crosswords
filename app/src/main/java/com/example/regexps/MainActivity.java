package com.example.regexps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button startBtn;

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
                        CrosswordGenerator crossword = new CrosswordGenerator(3, 3);
                        crossword.createCrossword();
                        Intent game = new Intent(MainActivity.this, GameActivity.class);
                        game.putExtra("CrosswordHorizontal", crossword.getCrosswordHorizontal());
                        game.putExtra("CrosswordVertical", crossword.getCrosswordVertical());
                        game.putExtra("height", crossword.getHeight());
                        game.putExtra("width", crossword.getWidth());
                        startActivity(game);
                    }
                }
        );

    }
}
