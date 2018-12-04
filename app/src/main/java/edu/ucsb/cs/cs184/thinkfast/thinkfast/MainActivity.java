package edu.ucsb.cs.cs184.thinkfast.thinkfast;

import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    static Timer timer = new Timer();
    static long gameoverTime;
    static Timer textTimer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameoverTime = System.currentTimeMillis() + 60000;
        RefreshTimer();

        final TextView timerText = findViewById(R.id.timerText);
        textTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        int secs = (int)((gameoverTime - System.currentTimeMillis()) / 1000);
                        String txt = secs + " secs";
                        timerText.setText(txt);
                        if (secs <= 10) {
                            timerText.setTextColor(Color.rgb((10 - secs)*25, 0, 0));
                        } else {
                            timerText.setTextColor(Color.BLACK);
                        }
                    }
                });
            }
        }, 1000, 1000);

        getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragmentHolder, new StartFragment());
        fragmentTransaction.commit();

        Log.d("debuglog", "starting up");
    }

    public void Begin() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentHolder, new CallOutFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void CompleteMinigame() {
        gameoverTime += 10000;
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
                        transaction.replace(R.id.fragmentHolder, new CallOutFragment());
                        transaction.commit();
                        Log.d("debuglog", "creating a new minigame");
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
                    }
                });
            }
        }, new Date(gameoverTime));
    }
}
