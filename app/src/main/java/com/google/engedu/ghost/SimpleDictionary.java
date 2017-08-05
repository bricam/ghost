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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
              words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        if(prefix == ""){//Empty prefix
            Random rand = new Random();
            int n = rand.nextInt(words.size());
            return words.get(n);
        }
        int bsr; //Binary Search Result
        bsr = binarySearch(prefix);
        if(bsr == -1234) { //No results found
            return null;
        }
        else{
            return words.get(bsr); //return the word found
        }
    }

    @Override
    public String getGoodWordStartingWith(String prefix) {
        String BUGTEST = "abcdefg";

        ArrayList<String> evenWords = new ArrayList<String>();
        ArrayList<String> oddWords = new ArrayList<String>();


        int leftBou = getStartingBound(prefix);
        int rightBou = getEndBound(prefix);
        //System.out.println(leftBou);
        //System.out.println(rightBou);


        //Computer went first, so we want a word of even length
        if ((prefix.length() % 2) == 0) {
            for(int x = leftBou; x <= rightBou; ++x) {//Traverse dictionary range
                if ((words.get(x).length() % 2) == 0) {
                    evenWords.add(words.get(x));
                }
            }
        }
        //Player went fist, so we want a word of odd length
        else {
            for(int x = leftBou; x <= rightBou; ++x) {//Traverse dictionary range
                if ((words.get(x).length() % 2) == 1) {
                    oddWords.add(words.get(x));
                }
            }
        }

        //Appropriate list is full by this point

        Random r = new Random();
        int index;

        if(((prefix.length() % 2) == 0 )&&((evenWords.size() > 0))) { //even case
            int size = evenWords.size();
            index = r.nextInt(size);
            return evenWords.get(index);
        }
        else if(((prefix.length() % 2) == 1 )&& (oddWords.size() > 0)){ //odd case
            int size = oddWords.size();
            index = r.nextInt(size);
            return oddWords.get(index);
        }
        else if(evenWords.size() > 0){
            int size = evenWords.size();
            index = r.nextInt(size);
            return evenWords.get(index);
        }
        else{
            int size = oddWords.size();
            index = r.nextInt(size);
            return oddWords.get(index);
        }

        //return BUGTEST;
    }

    //Gets index of 1st element in our dictionary w/ the prefix
    private int getStartingBound(String prefix){
        int low = 0;
        int high = words.size() -1;
        while(low != high){
            int mid = (low+high) / 2;
            String item = words.get(mid);
            if(item.compareToIgnoreCase(prefix) < 0 ) { //Item is before the prefix
                low = mid + 1;
            }
            else{ //Item is a word that has or is after the prefix
                //We can ignore all of these things that come after
                high = mid;
            }
        }
        return low; //low and high will both point to the first element
    }

    //Gets index of last element in our dictionary w/ the prefix
    private int getEndBound(String prefix){
        int low = 0;
        int high = words.size()- 1;
        int result = -1;
        while(low <= high) {
            int mid = (low+high) / 2;
            String item = words.get(mid);
            if(item.startsWith(prefix)) {
                result = mid;
                low = mid + 1;
            }
            else if(item.compareToIgnoreCase(prefix) > 0){
                high = mid -1;
            }
            else{
                low = mid + 1;
            }
        }
        return result;
    }


    private int binarySearch(String prefix){ //My binary search implementation
        int low = 0;
        int high = words.size() - 1;
        while(high >= low){
            int middle = (low + high) / 2;
            String item = words.get(middle);
            if(item.startsWith(prefix)){
                return middle;
            }
            if(item.compareToIgnoreCase(prefix) < 0 ) { //item comes before prefix
                low = middle + 1;
            }
            if(item.compareToIgnoreCase(prefix) > 0){//item comes after prefix
                high = middle - 1;
            }
        }
        return -1234; //Not found
    }


}
