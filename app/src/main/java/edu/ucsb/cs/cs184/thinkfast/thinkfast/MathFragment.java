package edu.ucsb.cs.cs184.thinkfast.thinkfast;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.SensorEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MathFragment extends Fragment implements Minigame {

    /*
        Types of question sets with a common relationship
     */
    // @TODO: check for overlaps in categories...
    String[] evens = new String[]{"1+1", "2+2", "0+2", "2*3", "4*3", "2+4*6", "2", "54", "44", "78", "10-2", "12*2-8", "3/2 + 2/4", "102", "4+8*10", "8*11", "14", "24+4", "10*5-5+8+3"};
    String[] odds = new String[]{"1+2", "2+3", "1+8", "3*7", "3*9", "2+1*3", "4+3", "7+8*3", "33", "3", "7", "11", "13", "23", "31", "7*11", "49", "7*13"};
    String[] primes = new String[]{"1", "3", "7", "11", "13", "23", "31", "1+2", "2+3", "2+1*3", "4+3", "7+8*3"};
    String[] threes = new String[]{"78", "54", "3", "33", "1+2", "2*3", "4*3", "1+8", "3*9", "3*7", "102", "4+8*10"};
    String[] sevens = new String[]{"4+8*10", "3*7", "4+3", "7*11", "14", "24+4", "49", "10*5-5+8+3", "7*13"};

    String[] list;


    /*
        Questions user should be shaking on
     */
    String[] targetQ;

    /*
        Questions displayed to users
     */
    ArrayList<String> masterQ;


    /*
        Sets of questions <Type, Question List>
     */
    Map<String, String[]> masterSets = new HashMap<>();


    TextView text;
    TextView prompt;
    Timer timer;
    int count;
    View view;
    String type;
    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity

    private static final int SHAKE_THRESHOLD = 800;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.math_fragment, container, false);
        text = view.findViewById(R.id.question);
        prompt = view.findViewById(R.id.prompt);
        //text.setText("Shake on Evens");

        masterSets.put("Evens", evens);
        masterSets.put("Odds", odds);
        masterSets.put("Primes", primes);
        masterSets.put("Multiples of 3", threes);
        masterSets.put("Multiples of 7", sevens);

        // reset
        count = 0;

        list = new String[]{"Evens", "Odds", "Primes", "Multiples of 3", "Multiples of 7"};

        // choose a target category randomly
        //targetQ = evens;
        Random rnd = new Random();
        type = list[rnd.nextInt(list.length)];
        targetQ = masterSets.get(type);
        //Toast.makeText(getContext(), "Shake on " + type, Toast.LENGTH_SHORT).show();
        //String str = "Shake on " + type + "!";
        //prompt.setText(str);
        Log.d("debuglog", "Type Chosen: " + type);


        masterQ = setUpQuestions(masterSets);

        timer = new Timer();

        try
        {
            timer.wait(1000);

        }
        catch (Exception e)
        {
            Log.d("debuglog", "Timer wait failed....");
        }

        // Intervals to shuffle questions
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Random rnd = new Random();

                            String str = "Shake on " + type + "!";
                            prompt.setText(str);
                            String displayQ;
                            // give them a freebie on the first one
                            // if they miss, random
                            if (count == 0)
                            {
                                displayQ = targetQ[rnd.nextInt(targetQ.length)];
                                count++;
                            }
                            else
                            {
                                displayQ = masterQ.get(rnd.nextInt(masterQ.size()));
                            }
                            setDisplayQ(displayQ);
                        }
                    });
                } catch (NullPointerException e) {
                    Log.d("debuglog", e.toString());
                }
            }
        }, 1, 2000);


        // ShakeActivity detection
            // @TODO: on invalid ---> red screen
            // @TODO: on valid ----> green screen completeMiniGame()

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(sensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        mAccel = 0.00f;

        return view;
    }

    public void validate(String[] questions, String chosen)
    {
        for (int i = 0; i < questions.length; i++)
        {
            // if found
            if (questions[i].equals(chosen))
            {
                Toast.makeText(getContext(), "Correct!", Toast.LENGTH_SHORT).show();
                Log.d("debuglog", "Correct!");
                mSensorManager.unregisterListener(sensorListener);
                ((MainActivity)getActivity()).CompleteMinigame();

                return;
            }
        }
        Toast.makeText(getContext(), "Incorrect!", Toast.LENGTH_SHORT).show();
        Log.d("debuglog", "Incorrect!");

        // continue shuffling
        restartTimer();
    }


    public ArrayList<String> setUpQuestions(Map<String, String[]> sets)
    {
        //foreach (sets )
        ArrayList<String> masterQ = new ArrayList<>();

        for (String[] type : sets.values())
        {
            for (int i = 0; i < type.length; i++)
            {
                masterQ.add(type[i]);
            }
        }

        return masterQ;
    }
    public void setDisplayQ(String displayQ)
    {
        text.setText(displayQ);
    }

    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;

            if (mAccel > 8)
            {
                Log.d("debuglog", "shook on " + text.getText().toString());

                //Toast.makeText(getContext(), "SHAKE", Toast.LENGTH_SHORT).show();

                // cancel all tasks
                timer.purge();
                timer.cancel();

                // check if valid shake
                validate(targetQ, text.getText().toString());

            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)

        {

        }
    };

    public void restartTimer()
    {
        timer = new Timer();

        // Intervals to shuffle questions
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Random rnd = new Random();

                            String str = "Shake on " + type + "!";
                            prompt.setText(str);
                            String displayQ = masterQ.get(rnd.nextInt(masterQ.size()));
                            setDisplayQ(displayQ);
                        }
                    });
                } catch (NullPointerException e) {
                    Log.d("debuglog", e.toString());
                }
            }
        }, 1, 2000);

    }

    public void gameOver() {

    }
}