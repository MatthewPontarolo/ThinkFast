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
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CallOutFragment extends Fragment implements Minigame {

    HashMap<String, String[]> dict = new HashMap<>();

    public static String chosenWord = "";
    static TextView guess;

    public static SpeechRecognizer speech;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.callout_fragment, container,false);

        dict.put("Fruits", new String[] { "apple", "banana", "coconut", "guava", "honeydew", "mango", "peach", "pomegranate", "raspberry", "strawberry" });
        dict.put("Vegetables", new String[] { "artichoke", "broccoli", "carrot", "eggplant", "fennel", "garlic", "horseradish", "lettuce", "potato", "onion", "radish" });
        dict.put("Weather/Disasters", new String[] { "raincloud", "thunderstorm", "lightning", "hailstorm", "tornado", "hurricane", "mudslide", "forest fire", "volcano", "earthquake", "flooding", "monsoon", "typhoon", "sandstorm" });
        dict.put("Countries", new String[] { "Algeria", "Belarus", "Cambodia", "Denmark", "Ethiopia", "Finland", "Germany", "Hungary", "Iceland", "Jamaica", "Kuwait", "Lithuania", "Morocco", "Nigeria", "Oman", "Pakistan", "Qatar", "Russia", "Saudi Arabia", "Taiwan", "Ukraine", "Vanuatu", "Wales", "Yemen", "Zimbabwe", "Costa Rica", "England", "Somalia", "Kazakhstan", "Tunisia", "Canada", "Sweden", "Brazil", "Argentina", "Panama", "Mexico" });
        dict.put("US Presidents", new String[] { "Washington", "Lincoln", "Cleveland", "Coolidge", "Obama", "Jefferson", "Madison", "Monroe", "Jackson", "Buchanan", "Filmore", "Roosevelt", "Harding", "Truman", "Reagan", "Kennedy", "Clinton", "Nixon" });
        dict.put("Jobs", new String[] { "firefighter", "policeman", "nurse", "programmer", "professor", "waitress", "urban planner", "architect", "surgeon", "janitor", "accountant", "lawyer", "police officer", "teacher", "technician", "handyman", "engineer", "chemist", "receptionist", "salesperson", "diplomat", "politician", "soldier", "lifeguard", "actress", "plumber", "electrician", "musician", "artist" });
        dict.put("Colors", new String[] { "fuchsia", "magenta", "crimson", "yellow", "maroon", "bronze", "tangerine", "burgundy", "cerulean", "lavender", "periwinkle" });

        requestRecordAudioPermission();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Random rnd = new Random();
        String cat = (String)dict.keySet().toArray()[rnd.nextInt(dict.keySet().size())];
        chosenWord = dict.get(cat)[rnd.nextInt(dict.get(cat).length)];

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
        guess = view.findViewById(R.id.word);
        guess.setText(construct);
        guess.setTextColor(Color.BLACK);

        final TextView cate = view.findViewById(R.id.catTxt);
        cate.setText(cat);

        Log.d("debuglog", "word: " + chosenWord);

        establishSpeech();

        return view;
    }

    public void establishSpeech() {
        speech = SpeechRecognizer.createSpeechRecognizer(getContext());
        final Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getContext().getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, SpeechRecognizer.RESULTS_RECOGNITION);

        speech.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d("debuglog", "ready for speech " + params.toString());
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
                if (error == 7 || error == 6 || error == 8) {
                    speech.destroy();
                    establishSpeech();
                    //speech.startListening(recognizerIntent);
                }
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> strs = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Boolean correct = false;
                for (String result : strs) {
                    Log.d("debuglog", "found word " + result + " and looking for " + chosenWord);
                    if (result.toLowerCase().contains(chosenWord.toLowerCase())
                            || result.toLowerCase().equals(chosenWord.toLowerCase())) {
                        Log.d("debuglog", "Correct!");
                        correct = true;

                        try {
                            getActivity().runOnUiThread(new Runnable() {
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
                        } catch (NullPointerException e) {
                            Log.d("debuglog", e.toString());
                        }

                        ((MainActivity)getActivity()).CompleteMinigame();
                        break;
                    }
                }

                if (!correct) {
                    //speech.stopListening();
                    //speech.startListening(recognizerIntent);
                    speech.destroy();
                    establishSpeech();
                } else {
                    speech.stopListening();
                    speech.cancel();
                    speech.destroy();
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                Log.d("debuglog", "partial results obtanied!");
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                Log.d("debuglog", "event " + eventType + " happened!");
            }
        });
        //speech.cancel();
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        speech.cancel();
        speech.destroy();
    }

    public void gameOver() {
        getView().post(new Runnable(){
            public void run(){
                speech.stopListening();
                speech.destroy();
            }
        });
    }

    private void requestRecordAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String requiredPermission = Manifest.permission.RECORD_AUDIO;
            requestPermissions(new String[]{requiredPermission}, 101);
        }
    }
}
