package dictionary;

import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;



//Entry class:
//   String vars for:
//      word, type, definition
//   Optional pointer to next link for separate chaining.
//
//   ~Entry(string, string, string):
//       Setter method for adding data
class Entry{
    String word;
    String type;
    String def;
    
    Entry nextLink;
    
    Entry(String w, String t, String d){
        word = w;
        type = t;
        def = d;
        nextLink = null;
    }
}
//////////////////////////////////////////////////////////




public class Dictionary {
    static int recursive_counter = 0;

    public static void main(String[] args) throws Exception {
        
        //////Read file
        //////Add objects to entry objects
        //////Arraylist >> Classic array
        ///////Set tablesize
        String fileName = "/Users/ty/NetBeansProjects/dictionary/src/dictionary/dictionary.txt";
        Scanner read = new Scanner (new File(fileName));
        read.useDelimiter("[\\|\\n]");
        String word, type, def;

        ArrayList<Entry> words = new ArrayList<Entry>();
        while (read.hasNext()){
           int index = 0; 
           word = read.next();
           type = read.next();
           def = read.next();

           Entry ent = new Entry(word, type, def);
           words.add(ent);
        }
        read.close();

        Entry [] wordarray = words.toArray(new Entry[words.size()]);
        int load = words.size();
        int tablesize = words.size() * 4;
        /////////////////////////////////////////////////////////////////////////////////




        //Call functions to add data
        Entry[] linearmap = new Entry[tablesize];
        linearmap = addLinear(linearmap, wordarray, tablesize, words);

        Entry[] quadmap = new Entry[tablesize];
        quadmap = addLinear(quadmap, wordarray, tablesize, words);

        Entry[] chainmap = new Entry[tablesize];
        chainmap = addChain(chainmap, wordarray, tablesize, words);

        Entry[] dubmap = new Entry[tablesize];
        dubmap = addDub(dubmap, wordarray, tablesize, words);
        /////////////////////////////////////////////////////////////




        //Program driver:
        //stdin lookup word
        //call lookup functions
        //continue until user exit
        String findword;
        String cont;
        do{

        Scanner reader = new Scanner(System.in);  
        System.out.print("Enter a word: ");
        findword = reader.nextLine(); 
        System.out.println( "Total words: " + load + "\n");


        chainSearch(findword, tablesize, chainmap, load);
        linearSearch(findword, tablesize, linearmap, load);
        quadraticSearch(findword, tablesize, quadmap, load);
        dubsearch(findword, tablesize, dubmap, load);

        System.out.println("\n" + "Press any key to continue (or 'n' to quit)");
        cont = reader.nextLine();

        }while(!cont.equals("n"));
        /////////////////////////////////////////////////////////////////
        
        
    }//end main()




    //hash(word, tablesize):m
    //   Generate table key
    //   Implements the Fowler-Noll-Vo hash function
    //   return |hash % tablesize|
    //
    //hash2(word, tablesize):m
    //   Generate table key
    //   Implements a rotating hash function
    //   return |hash % tablesize|
    public static int hash(String word, int size){
        long sum = 2166136261L;
        for (int i = 0; i < word.length(); i++){
         sum = (sum * 16777619) ^ word.toCharArray()[i];
    }
        return Math.abs((int)sum % size);
    }
       
    public static int hash2(String word, int size){
        long sum = 0;
        for (int i = 0; i < word.length(); i++){
            sum = (sum << 4) ^ (sum >> 28) ^ word.toCharArray()[i];
    }
        return Math.abs((int)sum % size);
    }
    /////////////////////////////////////////////////////////////////




    //Recursive helper functions for linked list used in chainSearch()
    //   chain(Entry* collision loc, Entry* word to add):
    //          If we're at the end of the chain
    //              nextLink = *word to add
    //          Else
    //              Recursively check the next link
    //   findLink(Entry* current, String word)
    //      If current link.word == the lookup word, return current
    //      Else recursively check next link.
    public static void chain(Entry start, Entry link){
        if(start.nextLink == null)
            start.nextLink = link;
        else{
            start = start.nextLink;
            chain(start, link);
        }
    }
    
    public static Entry findLink(Entry e, String word){
        recursive_counter++;
        if(e.word.equals(word))
            return e;
        else
        return findLink(e.nextLink, word);
    }
    ////////////////////////////////////////////////////////////////




    //addLinear(entry[] map, entry[] word list, int tablesize, arraylist<entry> words):
    //   Iterate through unhashed word list, hash words and place in map
    //        When the index is already used:
    //          Go up the array linearly until empty slot is found
    public static Entry[] addLinear(Entry[] linearmap, Entry[] wordarray, int tablesize, ArrayList<Entry> words ){
    for(int i = 0; i < words.size(); i++){
        int key = hash(wordarray[i].word, tablesize);
        if(linearmap[key] == null){
        linearmap[key] = wordarray[i];
        }
        else{
            int k = key;
            while(linearmap[k] != null)
                k++;
            linearmap[key] = wordarray[i];
        }
    }
    
        return linearmap;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////




    //addQuad(entry[] map, entry[] word list, int tablesize, arraylist<entry> words):
    //   Iterate through unhashed word list, hash words and place in map
    //        When the index is already used:
    //          Iterate up the array quadratically until empty slot is found
    public static Entry[] addQuad(Entry[] quadmap, Entry[] wordarray, int tablesize, ArrayList<Entry> words ){
        for(int i = 0; i < words.size(); i++){
            int key = hash(wordarray[i].word, tablesize);
            if(quadmap[key] == null){
                quadmap[key] = wordarray[i];
            }
            else{
                int k = 1;
                while(quadmap[k] != null){
                    k++;
                    key += Math.pow(k, 2);
                }
                quadmap[key] = wordarray[i];
            }
        }
              return quadmap;  
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    



    //addChain(entry[] map, entry[] word list, int tablesize, arraylist<entry> words):
    //  Iterate through unhashed word list, hash words and place in map
    //      When the index is already used:
    //          Create new entry object with word data
    //          Call chain(Entry* collision loc, Entry* new loc)  
    public static Entry[] addChain(Entry[] chainmap, Entry[] wordarray, int tablesize, ArrayList<Entry> words ){
        for(int i = 0; i < words.size(); i++){
            int key = hash(wordarray[i].word, tablesize);
            if(chainmap[key] == null){
                chainmap[key] = wordarray[i];
            }
            else{
                Entry link = new Entry(wordarray[i].word, wordarray[i].type, wordarray[i].def);
                chain(chainmap[key], link);
        }
    }
        return chainmap;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////




    //addDub(entry[] map, entry[] word list, int tablesize, arraylist<entry> words):
    //  Iterate through unhashed word list, hash words and place in map
    //      Doublehash when index is already in use
    public static Entry[] addDub(Entry[] dubmap, Entry[] wordarray, int tablesize, ArrayList<Entry> words ){
        for(int i = 0; i < words.size(); i++){
            int key = hash(wordarray[i].word, tablesize);
            if(dubmap[key] == null){
                dubmap[key] = wordarray[i];
            }
            else{
                key = hash2(wordarray[i].word, tablesize);
                dubmap[key] = wordarray[i];
            }
        }
        return dubmap;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    
    
    
    
    //chainSearch(String findword, int tablesize, Entry[] chainmap, int load):
    //      Reverses the process of adding data via separate chaining
    //          Will find any word with 100% accuracy
    //          Prints results /including/ the word, type, definition
    //          Handles exceptions (typically nullpointer/ arrayoutofbounds)
    public static void chainSearch(String findword, int tablesize, Entry[] chainmap, int load){
        int inv = 1;
        boolean found = false;
        int key = hash(findword, tablesize);
        try{
            if(findword.equals(chainmap[key].word)){
                     found = true;
                     System.out.println(chainmap[key].word + "\n" + chainmap[key].type + "\n" + chainmap[key].def);
                     System.out.println("-Separate chaining-    " + "Tablesize:" + tablesize
                                        + "     Lambda:" + (double)load/tablesize + "      Success:" 
                                        + found + "     Items investigated:" + inv);
            }
            else{
                found = true;
                System.out.println("Chaining");
                System.out.println(findLink(chainmap[key], findword).word + "\n"
                                    + findLink(chainmap[key], findword).type + "\n"
                                    + findLink(chainmap[key], findword).def
                );
                System.out.println("-Separate chaining-    " + "Tablesize:" + tablesize
                                    +"     Lambda:" + (double)load/tablesize +"      Success:"
                                    + found + "     Items investigated:" + recursive_counter/3);
            }
        }
        catch(Exception e){
            found = false;
            System.out.println("-Separate chaining-    " + "Tablesize:" + tablesize
                               + "     Lambda:" + (double)load/tablesize + "      Success:"
                               + found + "     Items investigated:" + "unknown");
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////




    //dubSearch(String findword, int tablesize, Entry[] dubmap, int load):
    //      Reverses the process of adding data via double hashing
    //          Very high accuracy using this technique
    //          Prints results
    //          Handles exceptions (typically nullpointer/ arrayoutofbounds)
    public static void dubsearch(String findword, int tablesize, Entry[] dubmap, int load){
        int inv = 1;
        boolean found = false;
        int key = hash(findword, tablesize);
        
        try{
            if(findword.equals(dubmap[key].word)){
                found = true;
                 System.out.println("-Double hashing-    " + "Tablesize:" + tablesize
                                        + "     Lambda:" + (double)load/tablesize + "      Success:" 
                                        + found + "     Items investigated:" + inv);
            }
            else{
                inv++;
                key = hash2(findword, tablesize);
                if(findword.equals(dubmap[key].word)){
                    found = true;
                }
                 System.out.println("-Double hashing-    " + "Tablesize:" + tablesize
                                        + "     Lambda:" + (double)load/tablesize + "      Success:" 
                                        + found + "     Items investigated:" + inv);
            } 
        }
        catch(Exception E){
            found = false;
            System.out.println("-Double hashing-    " + "Tablesize:" + tablesize
                                        + "     Lambda:" + (double)load/tablesize + "      Success:" 
                                        + found + "     Items investigated:" + inv);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////




    //linearSearch(String findword, int tablesize, Entry[] linearmap, int load):
    //      Essentially reverses the process of adding data via linear probing
    //          Will find any word with 100% accuracy
    //          Prints results
    //          Handles exceptions (typically nullpointer/ arrayoutofbounds)
    public static void linearSearch(String findword, int tablesize, Entry[] linearmap, int load){
        int inv = 1;
        boolean found = false;
        int key = hash(findword, tablesize);
        
        try{
            if(findword.equals(linearmap[key].word)){
                 found = true;
                 System.out.println("-Linear search-    " + "Tablesize:" + tablesize
                                        + "     Lambda:" + (double)load/tablesize + "      Success:" 
                                        + found + "     Items investigated:" + inv);
           }
            else if(!findword.equals(linearmap[key].word)){

                int k = key;
                while(!(findword.equals(linearmap[key].word))){
                    k++;
                    inv++;
                    if(k > 600000)
                        break;
                }
                System.out.println("-Linear search-    " + "Tablesize:" + tablesize
                                        + "     Lambda:" + (double)load/tablesize + "      Success:" 
                                        + found + "     Items investigated:" + inv);
            }
        }
        catch(Exception e){
            found = false;
            System.out.println("-Linear search-    " + "Tablesize:" + tablesize
                                        + "     Lambda:" + (double)load/tablesize + "      Success:" 
                                        + found + "     Items investigated:" + inv);
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    
    

    
    //quadraticSearch(String findword, int tablesize, Entry[] quadmap, int load):
    //      Reverses the process of adding data via quadratic probing
    //          Not great accuracy using this
    //          Prints results
    //          Handles exceptions (typically nullpointer/ arrayoutofbounds)
     public static void quadraticSearch(String findword, int tablesize, Entry[] quadmap, int load){
        int inv = 1;
        boolean found = false;
        int key = hash(findword, tablesize);
        
        try{
            if(findword.equals(quadmap[key].word)){
                 found = true;
                 System.out.println("-Quadratic search-    " + "Tablesize:" + tablesize
                                        + "     Lambda:" + (double)load/tablesize + "      Success:" 
                                        + found + "     Items investigated:" + inv);
           }
            else if(!findword.equals(quadmap[key].word)){
                int k = key;
                int j = 1;
                while(!(findword.equals(quadmap[key].word))){
                    j++;
                    inv++;
                    key += Math.pow(j, 2);
                    if(k > 600000)
                        break;
                }
                 System.out.println("-Quadratic search-    " + "Tablesize:" + tablesize
                                        + "     Lambda:" + (double)load/tablesize + "      Success:" 
                                        + found + "     Items investigated:" + inv);
            }
        }
        catch(Exception e){
            found = false;
            System.out.println("-Quadratic search-    " + "Tablesize:" + tablesize
                                        + "     Lambda:" + (double)load/tablesize + "      Success:" 
                                        + found + "     Items investigated:" + inv);
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////

     

}//end dictrionary class;

