package ro.pub.cs.systems.eim.practicaltest02v9;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PracticalTest02MainActivityv9 extends AppCompatActivity {

    private static final String TAG = "AnagramApp";
    private static final String ACTION_ANAGRAMS_FOUND = "com.yourapp.ANAGRAMS_FOUND";
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02v9);

        EditText wordInput = findViewById(R.id.word_input);
        EditText minLettersInput = findViewById(R.id.min_letters_input);
        Button requestButton = findViewById(R.id.request_button);
        resultTextView = findViewById(R.id.result_text);
        Button mapButton = findViewById(R.id.map_button);

        // Set up the button click listener to fetch anagrams
        requestButton.setOnClickListener(v -> {
            String word = wordInput.getText().toString().trim();
            String minLetters = minLettersInput.getText().toString().trim();

            if (!word.isEmpty() && !minLetters.isEmpty()) {
                fetchAnagrams(word, minLetters);
            }
        });

        // Set up the button to go to the MapActivity
        mapButton.setOnClickListener(v -> {
            Intent intent = new Intent(PracticalTest02MainActivityv9.this, MapActivity.class);
            startActivity(intent);
        });

        // Register receiver to listen for anagram results
        LocalBroadcastManager.getInstance(this).registerReceiver(anagramReceiver, new IntentFilter(ACTION_ANAGRAMS_FOUND));
    }

    // BroadcastReceiver to receive the anagram data and update the UI
    private final BroadcastReceiver anagramReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<String> anagrams = intent.getStringArrayListExtra("anagrams");
            if (anagrams != null && !anagrams.isEmpty()) {
                // Display the anagrams in the UI
                StringBuilder resultText = new StringBuilder();
                for (String anagram : anagrams) {
                    resultText.append(anagram).append("\n");
                }
                resultTextView.setText(resultText.toString());
            } else {
                resultTextView.setText("No anagrams found.");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister receiver when activity is destroyed
        LocalBroadcastManager.getInstance(this).unregisterReceiver(anagramReceiver);
    }

    // Method to fetch anagrams from the API
    private void fetchAnagrams(String word, String minLetters) {
        String urlString = "http://www.anagramica.com/all/" + word;

        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);

                // Read the response from the API
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse and process the response
                parseAnagramsResponse(response.toString(), Integer.parseInt(minLetters));
            } catch (IOException e) {
                Log.e(TAG, "Network error", e);
            }
        }).start();
    }

    // Method to parse the anagram response and send it via a broadcast
    private void parseAnagramsResponse(String responseBody, int minLetters) {
        try {
            // Log the entire JSON response
            Log.d(TAG, "JSON Response: " + responseBody);

            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(responseBody);

            // Check if the "all" key exists and contains anagrams
            if (jsonResponse.has("all")) {
                JSONArray anagramsArray = jsonResponse.getJSONArray("all");

                ArrayList<String> anagramsList = new ArrayList<>();

                // Add anagrams to the list if they meet the minimum letter criteria
                for (int i = 0; i < anagramsArray.length(); i++) {
                    String anagram = anagramsArray.getString(i);
                    if (anagram.length() >= minLetters) {
                        anagramsList.add(anagram);
                        Log.d(TAG, "Parsed Anagram: " + anagram);  // Log each valid anagram
                    }
                }

                // Send the anagrams via a local broadcast
                Intent broadcastIntent = new Intent(ACTION_ANAGRAMS_FOUND);
                broadcastIntent.putStringArrayListExtra("anagrams", anagramsList);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
            } else {
                Log.e(TAG, "\"all\" key not found in the response.");
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSON Parsing error", e);
        }
    }
}
