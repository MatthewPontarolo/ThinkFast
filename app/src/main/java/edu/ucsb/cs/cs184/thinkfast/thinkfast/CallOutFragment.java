package edu.ucsb.cs.cs184.thinkfast.thinkfast;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class CallOutFragment extends Fragment implements Minigame {

    String[] dictionary = new String[]{ "apple", "banana", "coconut", "guava", "honeydew", "mango", "peach", "pomegranate", "strawberry",
                                        "artichoke", "broccoli", "carrot", "eggplant", "fennel", "garlic", "horseradish", "lettuce", "potato", "onion", "radish",
                                        "raincloud", "thunderstorm", "lightning", "hailstorm", "tornado", "hurricane", "mudslide", "forest fire",
                                        "Algeria", "Belarus", "Cambodia", "Denmark", "Ethiopia", "Finland", "Germany", "Hungary", "Iceland", "Jamaica", "Kuwait", "Lithuania", "Morocco", "Nigeria", "Oman", "Pakistan", "Qatar", "Russia", "Saudi Arabia", "Taiwan", "Ukraine", "Vanuatu", "Wales", "Yemen", "Zimbabwe" };

    String chosenWord = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.callout_fragment, container,false);

        requestRecordAudioPermission();

        Random rnd = new Random();
        chosenWord = dictionary[rnd.nextInt(dictionary.length)];

        char[] blanked = chosenWord.toCharArray();
        int numBlanks = (int)(chosenWord.length() / 2.0 + .5) - 1;
        int blankCount = 0;
        while (blankCount < numBlanks) {
            int select = rnd.nextInt(blanked.length);
            if (blanked[select] != '_' && blanked[select] != ' ') {
                blanked[select] = '_';
                blankCount++;
            }
        }
        String construct = "";
        for (char c : blanked) {
            construct += c + " ";
        }
        final TextView guess = view.findViewById(R.id.word);
        guess.setText(construct);
        guess.setTextColor(Color.BLACK);

        Log.d("debuglog", "word: " + chosenWord);

        final SpeechRecognizer speech = SpeechRecognizer.createSpeechRecognizer(getContext());
        final Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speech.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d("debuglog", "ready for speech");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d("debuglog", "speech beginning");
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
                Log.d("debuglog", "speech ended");
            }

            @Override
            public void onError(int error) {
                Log.d("debuglog", "error " + error);
                if (error == 7|| error == 6) {
                    speech.cancel();
                    speech.startListening(recognizerIntent);
                }
                /*if (error == 9 || error == 2) {
                    ((MainActivity)getActivity()).CompleteMinigame();
                }*/
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> strs = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Boolean correct = false;
                for (String result : strs) {
                    Log.d("debuglog", "found word " + result);
                    if (result.toLowerCase().equals(chosenWord.toLowerCase())) {
                        Log.d("debuglog", "Correct!");
                        correct = true;

                        getActivity().runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                String construct = "";
                                for (char c : chosenWord.toCharArray()) {
                                    construct += c + " ";
                                }
                                guess.setText(construct);
                                guess.setTextColor(Color.GREEN);
                            }
                        });

                        ((MainActivity)getActivity()).CompleteMinigame();
                    }
                }

                if (!correct) {
                    speech.cancel();
                    speech.startListening(recognizerIntent);
                } else {
                    speech.cancel();
                    speech.stopListening();
                    speech.destroy();
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                Log.d("debuglog", "partial results obtanied!");
            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getContext().getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        speech.cancel();
        speech.startListening(recognizerIntent);

        return view;
    }

    private void requestRecordAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String requiredPermission = Manifest.permission.RECORD_AUDIO;
            requestPermissions(new String[]{requiredPermission}, 101);
        }
    }
}
