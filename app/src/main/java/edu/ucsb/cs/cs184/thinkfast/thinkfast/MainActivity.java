package edu.ucsb.cs.cs184.thinkfast.thinkfast;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    static Timer timer = new Timer();
    static long gameoverTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameoverTime = System.currentTimeMillis() + 60000;
        RefreshTimer();
    }

    public static void CompleteMinigame() {
        gameoverTime += 10000;
        RefreshTimer();
    }

    public static void RefreshTimer() {
        timer.cancel();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //GAME OVER
                Log.d("debuglog", "Game over");
            }
        }, new Date(gameoverTime));
    }
}
