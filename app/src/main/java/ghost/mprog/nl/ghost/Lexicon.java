package ghost.mprog.nl.ghost;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;

class Lexicon {
    private HashSet<String> baseWordList;
    private HashSet<String> filteredWordList;

    Lexicon(Context context, String language, String first_letter){
        DatabaseHandler db = new DatabaseHandler(context);
        this.baseWordList = db.getWords(language, first_letter);
        this.reset();
    }

    public HashSet<String> filter(String filterString){
        for (Iterator<String> i = this.filteredWordList.iterator(); i.hasNext();) {
            String el = i.next();
            if (el != null && !el.startsWith(filterString)) {
                i.remove();
            }
        }
        return this.filteredWordList;
    }

    public int count(){
        return this.filteredWordList.size();
    }

    public String result(){
        if (this.count() == 1){
            Iterator setIterator = this.filteredWordList.iterator();
            return setIterator.next().toString();
        } else {
            return null;
        }
    }

    public boolean contains(String word){
        return this.filteredWordList.contains(word);
    }

    public void reset(){
        this.filteredWordList = new HashSet<>(this.baseWordList);
    }

    private boolean readLexicons(String sourcePath){
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(sourcePath);
        InputStreamReader inputReader = new InputStreamReader(inputStream);
        BufferedReader buffReader = new BufferedReader(inputReader);
        this.baseWordList = new HashSet<>();
        String line;
        try {
            while (( line = buffReader.readLine()) != null) {
                this.baseWordList.add(line.toLowerCase());
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
