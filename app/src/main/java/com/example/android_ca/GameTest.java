package com.example.android_ca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameTest extends AppCompatActivity implements Chronometer.OnChronometerTickListener {

    private List<Bitmap> selectedImgs = new ArrayList<>();
    private List<Bitmap> duplicatedImgs = new ArrayList<>();
    private Bitmap[] bitmaparray = new Bitmap[12];
    private int firstClickId = -1;
    private int secondClickId = -1;
    private Bitmap[] originalArray = new Bitmap[12];
    private int counter;
    private int clickCounter = 0;
    private Chronometer chronometer;
    private long start, stop, duration;

    private int[] viewId_list = {
        R.id.A1, R.id.A2, R.id.A3, R.id.A4, R.id.A5,
                R.id.A6, R.id.A7, R.id.A8, R.id.A9, R.id.A10,
                R.id.A11, R.id.A12
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_test);

        getSelectedImgs();
        duplicateImgs();
        Collections.shuffle(duplicatedImgs);
        for(int i=0; i<12; i++)
        {
            bitmaparray[i] = duplicatedImgs.get(i);
        }

        Bitmap questionMarkPicture = BitmapFactory.decodeResource(getResources(), R.drawable.question);

        for (int i = 0; i < 12; i++) {
            originalArray[i] = questionMarkPicture;
        }

        // load images onto views for first time
        refreshImgs();

        for (int j = 0; j < 12; j++) {
            ImageView v = (ImageView)findViewById(viewId_list[j]);
            v.setImageBitmap(originalArray[j]);

        }

        for (int j =0; j < 12; j++){
            ImageView v = (ImageView)findViewById(viewId_list[j]);
            v.setImageBitmap(originalArray[j]);

            v.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //check 2nd click and prevent self click
                    if(firstClickId != -1  && firstClickId != v.getId()){
                        for(int L = 0; L<12;L++){
                            if (viewId_list[L] == v.getId()) {
                                secondClickId = L;
                                System.out.println("SecondClick");

                                // flip image for second click
                                originalArray[secondClickId] = duplicatedImgs.get(secondClickId);
                                refreshImgs();

                                break;
                            }
                        }
                        // check whether two images match
                        TextView counterText = findViewById(R.id.count);
                        if(bitmaparray[firstClickId] == bitmaparray[secondClickId]){
                            v.setEnabled(false);
                            System.out.println("Matches");

                            // if match, increase counter
                            counter++;
                            counterText.setText(counter + " of 6 images matched");
                            System.out.println(counter);

                            // check for end game condition
                            if (counter == 6) {
                                chronometer.stop();
                                stop = System.currentTimeMillis();
                                duration = stop - start;
                                System.out.println(duration);
                                Intent intent = getIntent();
                                long player1 = intent.getLongExtra("Timing", 0);
                                if (player1 != 0){
                                    if(player1 > duration)
                                        System.out.println("player1 win");
                                    else if (player1 < duration)
                                        System.out.println("player2 win");
                                    else
                                        System.out.println("Draw .. both stupid");
                                }
                                endGame();
                            }
                        } else {
                                ImageView v1 = findViewById(viewId_list[firstClickId]);
                                v1.setEnabled(true);
                                System.out.println("No Match");

                                clickCounter++;

                                // if no match, change both images back to question marks
                                originalArray[firstClickId] = questionMarkPicture;
                                originalArray[secondClickId] = questionMarkPicture;
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshImgs();
                                    }
                                }, 1000);

                        }
                        firstClickId = -1;
                        secondClickId = -1;
                    }
                    //first click
                    else {
                        for (int k = 0; k<12; k++) {
                            //to check the postiion of the button clicked
                            if (viewId_list[k] == v.getId()) {
                                firstClickId = k;
                                v.setEnabled(false);
                                System.out.println("FirstClick");

                                // start timer
                                if (clickCounter == 0) {
                                    chronometer = (Chronometer) findViewById(R.id.chronometer);
                                    chronometer.setOnChronometerTickListener(GameTest.this);
                                    chronometer.start();
                                    chronometer.setBase(SystemClock.elapsedRealtime());// reset
                                    start = System.currentTimeMillis();
                                }

                                clickCounter++;

                                // flip image for first click
                                originalArray[firstClickId] = duplicatedImgs.get(firstClickId);
                                refreshImgs();

                                break;
                            }
                        }
                    }
                }

            });
        }

    }

    private void getSelectedImgs(){
        for (int i = 0; i < 6; i++) {
            String name = "image" + i;
            FileInputStream fileInputStream;
            Bitmap bitmap = null;
            try {
                fileInputStream = getApplicationContext().openFileInput(name);
                bitmap = BitmapFactory.decodeStream(fileInputStream);
                selectedImgs.add(bitmap);
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void duplicateImgs()
    {
        for(Bitmap img:selectedImgs){
            Bitmap copiedimg = img;
            duplicatedImgs.add(img);
            duplicatedImgs.add(copiedimg);
        }
    }

    private void refreshImgs() {
        for (int i = 0; i < 12; i++) {
            ImageView v = (ImageView)findViewById(viewId_list[i]);
            v.setImageBitmap(originalArray[i]);
        }
    }

    private void endGame() {
        Toast.makeText(this, "Game ended!", Toast.LENGTH_SHORT).show();

        Button player2 = findViewById(R.id.player2);
        Button restart = findViewById(R.id.restartGameButton);
        Button main = findViewById(R.id.restartMainButton);
        player2.setVisibility(View.VISIBLE);
        main.setVisibility(View.VISIBLE);
        restart.setVisibility(View.VISIBLE);

        player2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("Timing",duration);
                finish();
                startActivity(intent);
            }
        });

        // restart btn will restart game
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameTest.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }

    @Override
    public void onChronometerTick(Chronometer chronometer) {
        String time = chronometer.getText().toString();
//        if (time.equals("00:00")) {
//            Toast.makeText(this, "Time is up~", Toast.LENGTH_SHORT).show();
//        }
    }
}