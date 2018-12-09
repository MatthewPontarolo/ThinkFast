package edu.ucsb.cs.cs184.thinkfast.thinkfast;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

                    }
                });
            }
        }, 1, 2000);


        // @TODO: ShakeActivity detection
            // validate(evens, chosen)

        return view;
    }

    public boolean validate(String[] questions, String chosen)
    {

        return false;
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
}