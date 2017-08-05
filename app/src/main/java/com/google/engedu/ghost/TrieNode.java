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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Random;

public class TrieNode {
    private HashMap<String, TrieNode> children;
    private boolean isWord;

    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    public void add(String s) {
        if(s.length() == 0){ //Final letter, here's our finished word
            this.isWord = true;//This means the current node
        }
        else{
            String firstL = s.substring(0,1);
            TrieNode child = children.get(firstL);
            if(child==null){
                TrieNode newNode = new TrieNode();
                children.put(firstL,newNode);
                newNode.add(s.substring(1));
            }
            else{
                child.add(s.substring(1));
            }

            /*
            TrieNode babyNode = new TrieNode();
            String letter = s.substring(0,1);
            String leftover = s.substring(1);
            babyNode.add(leftover);
            children.put(letter, babyNode);
            */

        }
    }

    public boolean isWord(String s) {
        if(s.length() == 1){ //We're on the final letter
            TrieNode end = children.get(s.substring(0,1));//Check the last node
            if(end == null)
                return false;
            else if(end.isWord)
                return true;
            else
                return false;
        }
        else if(s.length() > 1){
            TrieNode nextLetter = children.get(s.substring(0,1));
            if(nextLetter == null)//Word DNE
                return false;
            else{ //There is a next letter
                String leftover = s.substring(1);
                return nextLetter.isWord(leftover);
            }
        }
        else{//An empty string isn't a word
            return false;
        }
    }

    public String getAnyWordStartingWith(String s) {
        if(s.length() == 0){ //We've already gotten the prefix
            Set<String> keys = children.keySet();
            int size = keys.size();
            //More possible letters form this point on
            String[] letters = keys.toArray(new String[size]);//All possible letters
            Random r = new Random();
            int index = r.nextInt(size);//Pick a random letter
            return letters[index];
        }
        else{ //Still adding prefix up
            System.out.println("SIZE:"+s.length());
            String prefix = s.substring(0,1);
            String suffix = s.substring(1);
            System.out.println("Prefix1: "+ prefix);
            System.out.println("ABOUT TO CHECK NEXT PREFIX");
            TrieNode nextLetter = children.get(prefix);
            if(nextLetter == null){
                System.out.println("RETURNING NULL");
                return null;
                /*
                char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
                Random p = new Random();
                int index = p.nextInt(26);
                String randLetter = Character.toString(alphabet[index]);
                return randLetter;
                */
            }
            System.out.println("Prefix: "+ prefix);
            System.out.println("Suffix: "+ suffix);
            System.out.println("Calling GAWS recursively");
            return nextLetter.getAnyWordStartingWith(suffix);
        }
    }



    public String getGoodWordStartingWith(String s) {
        return null;
    }
}
