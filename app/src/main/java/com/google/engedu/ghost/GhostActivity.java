/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        InputStream words;
        try {
            words = getAssets().open("words.txt");
            dictionary = new FastDictionary(words);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Handling reset button
        final Button resetB = (Button) findViewById(R.id.resetButton);
        resetB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStart(v);
            }
        });

        //Handling challenge button
        final Button challengeB = (Button) findViewById(R.id.challenge);
        challengeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                challengeHandler();
            }
        });

        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     *
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        Button challengeB = (Button) findViewById(R.id.challenge);
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        challengeB.setEnabled(true);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        Button challengeB = (Button) findViewById(R.id.challenge);
        // Do computer turn stuff then make it the user's turn again
        TextView text = (TextView) findViewById(R.id.ghostText);
        String startingFrag = text.getText().toString();
        //If the player's last letter made a word
        if (startingFrag.length() >= 4 && dictionary.isWord(startingFrag)) {
            label = (TextView) findViewById(R.id.gameStatus);
            label.setText("COMPUTER WINS");
            challengeB.setEnabled(false);
        }
        //Looking for possible longer word
        else if (dictionary.getAnyWordStartingWith(startingFrag) == null) {//DNE
            label = (TextView) findViewById(R.id.gameStatus);
            label.setText("COMPUTER WINS");
            challengeB.setEnabled(false);
        }
        else {//Exists
            int index = startingFrag.length();
            System.out.println("Calling GAWS from compTurn");
            String potentialWord = startingFrag + dictionary.getAnyWordStartingWith(startingFrag);
            System.out.println(potentialWord);
            text.setText(potentialWord);
            userTurn = true;
            label.setText(USER_TURN);

        }


    }

    /**
     * Handler for user key presses.
     *
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //If a letter

        if (event.getKeyCode() >= 29 && event.getKeyCode() <= 54) {
            //Add letter to word fragment
            TextView text = (TextView) findViewById(R.id.ghostText);
            char unicodeChar = (char) event.getUnicodeChar();
            String newFrag = text.getText().toString() + unicodeChar;
            text.setText(newFrag); //Letter is added
            //Check whether current word frag is a complete word
            if (newFrag.length() >= 4 && dictionary.isWord(newFrag)) {
                TextView label = (TextView) findViewById(R.id.gameStatus);
                label.setText("WORD FOUND");
            }
            computerTurn(); //Computer goes next
        }
        return super.onKeyUp(keyCode, event);
    }


    private void challengeHandler() {
        TextView text = (TextView) findViewById(R.id.ghostText);
        TextView label = (TextView) findViewById(R.id.gameStatus);
        String curFrag;
        Button challengeB = (Button) findViewById(R.id.challenge);

        curFrag = text.getText().toString();
        if(curFrag.length() < 4){
            label.setText("You need at least 4 letters to challenge!");
        }
        else if (dictionary.isWord(curFrag)) { //It is a word
            //User win
            label.setText("You win!");
            challengeB.setEnabled(false);
        }
        else if (dictionary.getAnyWordStartingWith(curFrag) == null) { //No more possible words
            //User win
            label.setText("You win!");
            challengeB.setEnabled(false);
        }
        else { //A word can be made
            //Computer win
            String possibleWord = curFrag;
            while(true){
                String newLetter =  dictionary.getAnyWordStartingWith(possibleWord);
                possibleWord += newLetter;
                if(dictionary.isWord(possibleWord))
                    break;
            }
            label.setText("Computer wins!");
            text.setText(possibleWord);
            challengeB.setEnabled(false);
        }
    }
}