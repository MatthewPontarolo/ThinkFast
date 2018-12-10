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
    String[] evens = new String[]{"1+1", "2+2", "0+2", "2*3", "4*3", "2+4*6", "2", "54", "44", "78"};
    String[] odds = new String[]{"1+2", "2+3", "1+8", "3*7", "3*9", "2+1*3", "4+3", "7+8*3", "33"};
    String[] primes = new String[]{"1", "3", "5", "7", "11", "13", "23", "29", "31", "2+3", "2+1*3", "4+3", "7+8*3"};
    //String[] integers = new String[]{"1", "44", "123", "54", "69", "78"};

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
    Timer timer;
    int count = 0;
    View view;
    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity

    private static final int SHAKE_THRESHOLD = 800;

    private Runnable thread;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.math_fragment, container, false);
        text = view.findViewById(R.id.question);
        //text.setText("Shake on Evens");

        masterSets.put("Evens", evens);
        masterSets.put("Odds", odds);
        masterSets.put("Primes", primes);
        //masterSets.put("Integers", integers);

        list = new String[]{"Evens", "Odds", "Primes"};

        // choose a target category randomly
        //targetQ = evens;
        Random rnd = new Random();
        String type = list[rnd.nextInt(list.length)];
        targetQ = masterSets.get(type);
        Toast.makeText(getContext(), "Shake on " + type, Toast.LENGTH_SHORT).show();
        Log.d("debuglog", "Type Chosen:" + type);


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
                getActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        Random rnd = new Random();
                        String displayQ = masterQ.get(rnd.nextInt(masterQ.size()));
                        setDisplayQ(displayQ);
                    }
                });
            }
        }, 1, 3000);


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
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void restartTimer()
    {
        timer = new Timer();

        // Intervals to shuffle questions
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        Random rnd = new Random();
                        String displayQ = masterQ.get(rnd.nextInt(masterQ.size()));
                        setDisplayQ(displayQ);
                    }
                });
            }
        }, 1, 2000);

    }
}