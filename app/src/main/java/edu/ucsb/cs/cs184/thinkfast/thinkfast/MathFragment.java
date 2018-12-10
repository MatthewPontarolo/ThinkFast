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
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MathFragment extends Fragment implements Minigame {

    /*
        Types of question sets with a common relationship
     */
    String[] evens = new String[]{"1+1", "2+2"};
    String[] odds = new String[]{"1+2", "2+3"};

    /*
        Questions user should be shaking on
     */
    String[] targetQ;


    /*
        Sets of questions
     */
    ArrayList<String[]> masterSets = new ArrayList<String[]>();

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
        text.setText("Shake");

        masterSets.add(evens);
        masterSets.add(odds);

        targetQ = evens;


        timer = new Timer();

        // @TODO: Intervals to shuffle questions
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        Random rnd = new Random();
                        final TextView text = view.findViewById(R.id.question);
                        String displayQ = targetQ[rnd.nextInt(targetQ.length)];
                        setDisplayQ(displayQ);
                        // break out of thread
                    }
                });
            }
        }, 1, 2000);


        // @TODO: ShakeActivity detection
            // validate(evens, chosen)
            // on invalid ---> red screen
            // on valid ----> green screen completeMiniGame()


        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(sensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        mAccel = 0.00f;

        ShakeDetector detector = new ShakeDetector();
        detector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                Log.e("debuglog", "SHOOKEN");

                Toast.makeText(getContext(), "SHOOOKE", Toast.LENGTH_SHORT).show();
                TextView text = view.findViewById(R.id.question);

                timer.cancel();
                ((MainActivity)getActivity()).CompleteMinigame();
                //validate(targetQ, text.getText());
            }
        });


        return view;
    }

    public boolean validate(String[] questions, String chosen)
    {

        return true;
    }


    public String[] setUpQuestions(ArrayList<String[]> sets)
    {
        //foreach (sets )
        return new String[0];
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
                Log.d("debuglog", "SHOOKEN");

                Toast.makeText(getContext(), "SHAKE", Toast.LENGTH_SHORT).show();
                timer.purge();
                timer.cancel();
                ((MainActivity)getActivity()).CompleteMinigame();
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


}