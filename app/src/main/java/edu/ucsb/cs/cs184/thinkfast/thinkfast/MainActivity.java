package edu.ucsb.cs.cs184.thinkfast.thinkfast;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    static Timer timer = new Timer();
    static long gameoverTime;
    static Timer textTimer = new Timer();
    int score = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragmentHolder, new StartFragment());
        fragmentTransaction.commit();

        Log.d("debuglog", "starting up");
    }

    public void Begin() {
        gameoverTime = System.currentTimeMillis() + 50000;
        RefreshTimer();

        final TextView timerText = findViewById(R.id.timerText);
        final TextView scoreText = findViewById(R.id.scoreText);
        textTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        int secs = (int)((gameoverTime - System.currentTimeMillis()) / 1000 + .5);
                        String txt = secs + " secs";
                        timerText.setText(txt);
                        if (secs <= 10) {
                            timerText.setTextColor(Color.rgb((10 - secs)*25, 0, 0));
                        } else {
                            timerText.setTextColor(Color.BLACK);
                        }
                        String scr = "Score: " + score;
                        scoreText.setText(scr);
                    }
                });
            }
        }, 1, 1000);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentHolder, (Fragment)GetMinigame());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void CompleteMinigame() {
        gameoverTime += 3000;
        score++;
        RefreshTimer();

        Log.d("debuglog", "completed minigame");

        Timer moveTimer = new Timer();
        moveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragmentHolder, (Fragment)GetMinigame());
                        transaction.commit();
                    }
                });
            }
        }, 1500);
    }

    public void RefreshTimer() {
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //GAME OVER
                Log.d("debuglog", "Game over");
                textTimer.cancel();
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        final TextView timerText = findViewById(R.id.timerText);
                        timerText.setText(getString(R.string.gameoverTxt));
                        Restart();
                    }
                });
            }
        }, new Date(gameoverTime));
    }

    public Minigame GetMinigame() {
        Random random = new Random();
        switch(random.nextInt(2)) {
            case 0:
                //return new CallOutFragment();
                return new TouchMazeFragment();

            case 1:
                return new MathFragment();
            case 2:
                return new TouchMazeFragment();
            default:
                return new TouchMazeFragment();

        }

    }

    public int getScore(){
        return score;
    }

    public void Restart(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentHolder, new StartFragment());
        transaction.addToBackStack(null);
        transaction.commit();
        textTimer = new Timer();
        score = 0;
    };
}
