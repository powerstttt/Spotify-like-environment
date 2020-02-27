/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hw4;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author eseny
 */
public class HashTable<K,V> {
    int SIZE = 10000;
    Object [] table = new Object[SIZE]; //name hash table
    Object [] songTable = new Object[SIZE]; //song hash table
    String [] personNames = new String[SIZE]; //store person names in a String array.
    int personNameIndex = 0; //specifies to personNames index value
    //alphabet index is used to calculate index. For example, a=2, z=27, J=37.
    String [] alphabet = {"."," ","a","b","c","d","e","f","g","h","i","j","k",
        "l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B",
        "C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S",
        "T","U","V","W","X","Y","Z", null};
    /*
    O, takes an input and runs command lines.
    */
    public void O(String FileName){
        try {
            File newFile = new File(FileName);
            Scanner input = new Scanner(newFile);
            //Continue while having input
            while(input.hasNextLine()){
                //decision of what method is going to use from input.
                char type = input.next().charAt(0);
                //Insert name
                if(type == 'I'){
                    String name = input.nextLine().trim();
                    putName(name);
                }
                //Like song
                else if(type == 'L'){
                    String personName = input.next().trim();
                    String songName = input.nextLine().trim();
                    likeSong(personName, songName);
                }
                //Erase song
                else if(type == 'E'){
                    String personName = input.next().trim();
                    String songName = input.nextLine().trim();
                    eraseSong(personName, songName);
                }
                //Delete person
                else if(type == 'D'){
                    String name = input.nextLine().trim();
                    deletePerson(name);
                }
                //Print songs
                else if(type == 'P'){
                    String personName = input.nextLine().trim();
                    printSongs(personName);
                }
                //Matchmaking
                else if(type == 'M'){
                    String personName = input.nextLine().trim();
                    match(personName);
                }
                //Song recommendation
                else if(type == 'R'){
                    String personName = input.nextLine().trim();
                    recommend(personName);
                }
                //take an input
                else if(type == 'O'){
                    String direction = input.nextLine().trim();
                    O(direction);
                }
                //Exit
                else if(type == 'X'){
                    break;
                }
            }
        } catch (FileNotFoundException ex) {
           throw new UnsupportedOperationException("Something went wrong.");
        }
    }
    //Putting name to name hash table
    public void putName(String personName){
        //to find the index name must have minimum 2 character.
        if(personName.length()>1){
            //is name created before?
            if(!isCreated(personName)){
                //Create hashObject to put name and song
                hashObject newHashLine = new hashObject();
                Integer index = (getCharIndex(personName.charAt(0)) * getCharIndex(personName.charAt(1)))%SIZE;
                //If another name is in the list with same index
                //find another index by mod53
                if(table[index] != null){
                    for(int multiply = 0; table[index] != null; multiply++)
                        index = ((multiply*53)+(index%53))%SIZE;
                }
                nameSong newPersonBlock = new nameSong();
                newPersonBlock.nameOfBlock = personName;
                newHashLine.K = index;
                newHashLine.V = newPersonBlock;
                //put name in the name hash table
                table[index] = newHashLine;
                //put the name also personName to access another time
                personNames[personNameIndex] = personName;
                personNameIndex++;
            }
            else{
                System.out.println(personName+" can not be created");
            }
        }
        else
            System.err.println("'"+personName+"' can not be created. The size of "
                    + "name must be larger than one!");
    }
    
    public void likeSong(String personName, String songName){
        //find index personName is in
        Integer index = findIndex(personName);
        if (index > -1){
            Integer songIndex;
            //add to song hash table
            if(!isSongCreated(songName)){ //is hash have song?
                hashObject newHashLine = new hashObject();
                //index calculation
                songIndex = (getCharIndex(songName.charAt(0)) * getCharIndex(songName.charAt(1)))%SIZE;
                //if index is not null try another index
                if(songTable[songIndex] != null){
                    for(int multiply = 0; songTable[songIndex] != null; multiply++)
                        songIndex = ((multiply*53)+(songIndex%53))%SIZE;
                }
                songName newSongBlock = new songName(); //line of song hash
                newSongBlock.nameOfBlock = songName;
                newHashLine.K = songIndex;
                newHashLine.V = newSongBlock;
                //add song to song hash table
                songTable[songIndex] = newHashLine;
            }
            else
                songIndex = findSongIndex(songName);
            //add person to song
            Person likedPerson = new Person(); //new person who liked
            likedPerson.PersonName = personName;
            Person dummyPerson = (Person) getPersonBlock(songIndex);
            songName tempSong = (songName) getSongNameBlock(songIndex);
            //if it is null put it in directly, else find null place
            if(dummyPerson == null)
                tempSong.personBlock = likedPerson;
            else{
                while(dummyPerson.next != null)
                    dummyPerson = dummyPerson.next;
                dummyPerson.next = likedPerson;
            }
            //add new song to name
            Song likedSong = new Song();
            likedSong.SongName = songName;
            Song dummy = (Song) getSongBlock(index);
            nameSong temp = (nameSong) getNameSongBlock(index);
            //find null place to put
            if(dummy == null)
                temp.songBlock = likedSong;
            else{  
                while(dummy.next != null)
                    dummy = dummy.next;
                dummy.next = likedSong;
            }
        }
        else
            System.out.println(personName+" is not created so "+songName+" cannot be liked.");
    }
    //erase song from name, erase name from song
    public void eraseSong(String personName, String songName){
        Integer index = findIndex(personName);
        if(index > -1){
            /* Erase song from name hash table's linked list */
            nameSong nameSongBlock = (nameSong) getNameSongBlock(index);
            Song temp = (Song) getSongBlock(index);
            Song temp2 = null;
            //If it is not empty, it can erased
            if(temp != null){
                //find the song that is going to erase
                while(temp != null && !temp.SongName.equals(songName)){
                    temp2 = temp;
                    temp = temp.next;
                }
                //If song is not in the list
                if(temp == null)
                    System.out.println(personName+" "+songName+" can not be erased.");
                else{ //song found. make linked list connections.
                    if(temp2 != null)
                        temp2.next = temp.next;
                    else
                        nameSongBlock.songBlock = temp.next;
                System.out.println(personName+" doesn't like "+songName);
                }
            }
            else{
                System.out.println(personName+" "+songName+" can not be erased.");
            }
            /* Erase name from song hash table's linked list */
            Integer songIndex = findSongIndex(songName);
            if(songIndex > -1){
                songName songNameBlock = (songName) getSongNameBlock(songIndex);
                Person temp3 = (Person) getPersonBlock(songIndex);
                Person temp4 = null;
                if(temp3 != null){ //is there any person
                    while(temp3 != null && !temp3.PersonName.equals(personName)){//is it that person
                        temp4 = temp3;
                        temp3 = temp3.next;
                    }
                    if(temp3.next == null){//is it last person?
                        if(temp4 != null)
                            temp4.next = null;
                    }
                    else{
                        if(temp4 != null){//middle of the list
                            temp4.next = temp3.next;
                        }
                        else//first person in the list
                            songNameBlock.personBlock = temp3.next;
                    }
                }
            }
        }
        else{
            //If name is not created
            System.out.println(personName+" "+songName+" can not be erased.");
        }
    }
    
    public void deletePerson(String personName){
        Integer index = findIndex(personName);
        if(index > -1){
            if(table[index] != null){//is it exist?
                hashObject emptyHash = new hashObject(); //create empty object
                nameSong emptyBlock = new nameSong();    //to replace the position
                emptyBlock.nameOfBlock = "*empty*";      //purpose: do not break
                emptyHash.K = -1;                        //algorithm
                emptyHash.V = emptyBlock;
                //put empty hash to index, dont remove
                //the algorithm is going to fall if putted to null
                table[index] = emptyHash; 
                System.out.println(personName+" is deleted.");
            }
            else
                System.out.println(personName+" is not in the list.");
        }
        else
            System.out.println(personName+" is not in the list.");
    }
    //print all songs liked in a name
    public void printSongs(String personName){
        Integer index = findIndex(personName);
        if(index > -1){
            Song firstSong = (Song) getSongBlock(index);
            if(firstSong != null){
                System.out.println("\n"+personName+" liked:");
                while(firstSong != null){
                    System.out.println(firstSong.SongName);
                    firstSong = firstSong.next;
                }
            }
            else
                System.out.println(personName+" has no song.");
        }
        else
            System.out.println(personName+" is not in the list.");
    }
    
    public void match(String personName){
        Integer index = findIndex(personName);
        Integer tempIndex = null;
        int count[] = new int[SIZE];    //stores number of matched songs for each name
        int total[] = new int[SIZE];    //stores number of total songs for each name
        Integer percent[] = new Integer[SIZE];  //stores percentage of matched songs for each name
        if(index > -1){
            nameSong thePerson = (nameSong) getNameSongBlock(index);
            Song thePersonSongs;
            System.out.println("\nPossible friend of " + personName);
            for(int i = 0; personNames[i] != null; i++){ //reach every name of hash
                if(!personNames[i].equals(personName)){ //dont match with himself/herself
                    count[i] = 0; 
                    total[i] = 0;
                    tempIndex = findIndex(personNames[i]);
                    if(tempIndex > -1){
                        nameSong temp = (nameSong) getNameSongBlock(tempIndex); //person who matched
                        Song dummytemp = temp.songBlock;
                        thePersonSongs = thePerson.songBlock;
                        Song dummyPerson = thePerson.songBlock;
                        //look for song of new person
                        while(temp.songBlock != null){
                            //look for song of personName
                            while(thePersonSongs != null){
                                if(temp.songBlock.SongName.equals(thePersonSongs.SongName)){
                                    count[i]++; //increase matched songs
                                    break;
                                }
                                thePersonSongs = thePersonSongs.next;//go to next song(the person)
                            }
                            total[i]++; //increase total songs
                            temp.songBlock = temp.songBlock.next; //go to next song(matched person)
                            thePersonSongs = thePerson.songBlock;
                        }
                        temp.songBlock = dummytemp;         //turn back the initial pointer
                        thePerson.songBlock = dummyPerson;  //turn back the initial pointer
                        if(total[i] > 0)
                            percent[i] = (count[i]*100)/total[i]; //percentage calculation
                        else
                            percent[i] = 0;
                        System.out.println(personNames[i] + " " + percent[i] + 
                                "% match  (" + count[i] + " song out of " + total[i] + ")");
                    }
                }
            }
        }
        else{
            System.out.println("There is no person in the list with that name.");
        }
        
    }
    
    public void recommend(String personName){
        /* Starting copy of match function. */
        
        /* To giving a recommendation to personName,
        Matched songs information is used. The person who has the most 
        percentage of matched songs is the source of recommendation.
        */
        Integer index = findIndex(personName);
        Integer tempIndex = null;
        int count[] = new int[SIZE];    //stores number of matched songs for each name
        int total[] = new int[SIZE];    //stores number of total songs for each name
        Integer percent[] = new Integer[SIZE];  //stores percentage of matched songs for each name
        if(index > -1){
            nameSong thePerson = (nameSong) getNameSongBlock(index);
            Song thePersonSongs;
            for(int i = 0; personNames[i] != null; i++){ //reach every name of hash
                if(!personNames[i].equals(personName)){ //dont match with himself/herself
                    count[i] = 0; 
                    total[i] = 0;
                    tempIndex = findIndex(personNames[i]);
                    if(tempIndex > -1){
                        nameSong temp = (nameSong) getNameSongBlock(tempIndex); //person who matched
                        Song dummytemp = temp.songBlock;
                        thePersonSongs = thePerson.songBlock;
                        Song dummyPerson = thePerson.songBlock;
                        while(temp.songBlock != null){
                            while(thePersonSongs != null){
                                if(temp.songBlock.SongName.equals(thePersonSongs.SongName)){
                                    count[i]++;
                                    break;
                                }
                                thePersonSongs = thePersonSongs.next;//go to next song(the person)
                            }
                            total[i]++;
                            temp.songBlock = temp.songBlock.next; //go to next song(matched person)
                            thePersonSongs = thePerson.songBlock;
                        }
                        temp.songBlock = dummytemp;
                        thePerson.songBlock = dummyPerson;
                        if(total[i] > 0)
                            percent[i] = (count[i]*100)/total[i];
                        else
                            percent[i] = 0;
                    }
                }
                else
                    percent[i] = 0;
            }
            /* Ending copy of match function */
            
            /* Recommending processes */
            Integer max = 0;
            int maxIndex[] = new int[5];
            for(int i = 0; percent[i] != null; i++){
                if(percent[i].compareTo(max) >= 0){
                    max = percent[i];
                    /*
                    There is a possibility to have deficient songs to write
                    recommended songs when looking the most matched persons.
                    The worst possibility is every person has liked a one song.
                    To avoid that problem maxIndex defines the most matched persons
                    in an array to access them to get data.
                    If the most matched person enters this function first,
                    There will be a problem to write 5 songs on output.
                    */
                    maxIndex[4] = maxIndex[3];
                    maxIndex[3] = maxIndex[2];
                    maxIndex[2] = maxIndex[1];
                    maxIndex[1] = maxIndex[0];
                    maxIndex[0] = i;
                }
            }
            int printedSongs=0;
            System.out.println("\nRecommended songs for " + personName+":");
            String recommended = "";
            
            for(int k = 0; printedSongs<5 && k<5; k++){ //do that for 5 songs or end of maxIndex
                Integer blockIndex = findIndex(personNames[maxIndex[k]]);
                Song mostMatchedSongs = (Song) getSongBlock(blockIndex); //get songs of the most matched person
                Song dummyMost = mostMatchedSongs;
                Song personSongs = (Song) getSongBlock(index);    //get songs of himself/herself
                Song dummyPerson = personSongs;

                int flag = -1;   
                while(mostMatchedSongs != null){
                    flag = -1;
                    while(personSongs != null){
                        flag = 0;
                        if(mostMatchedSongs.SongName.equals(personSongs.SongName)){
                            flag = 1; //dont write this song
                            break;
                        }
                        personSongs = personSongs.next;
                    }
                    personSongs = dummyPerson; //return head of the songs
                    if(flag == 0){
                        if(!recommended.contains(mostMatchedSongs.SongName)){
                            System.out.println(mostMatchedSongs.SongName);
                            recommended += mostMatchedSongs.SongName;
                            printedSongs++;
                        }
                        if(printedSongs > 4)
                            break;
                    }
                    mostMatchedSongs = mostMatchedSongs.next;
                }
                mostMatchedSongs = dummyMost;
            }
            
        }
        else{
            System.out.println("There is no person in the list with that name. "
                    + "No recommendation.");
        } 
        
        
    }
    
    /*Gives hashObject type address*/
    public Object get(Integer index){
        return table[index];
    }
    
    public Object getNameSongBlock(Integer index){
        hashObject temp = (hashObject) table[index];
        return temp.V;
    }
    public Object getSongNameBlock(Integer songIndex){
        hashObject temp = (hashObject) songTable[songIndex];
        return temp.V;
    }
    public String getSongName(Integer songIndex){
    hashObject temp = (hashObject) songTable[songIndex];
    songName temp2 = (songName) temp.V;
    return temp2.nameOfBlock;
    }
    
    public String getPersonName(Integer index){
        hashObject temp = (hashObject) table[index];
        nameSong temp2 = (nameSong) temp.V;
        return temp2.nameOfBlock;
    }
    
    public Object getSongBlock(Integer index){
        hashObject temp = (hashObject) table[index];
        nameSong temp2 = (nameSong) temp.V;
        return temp2.songBlock;
    }
    
    public Object getPersonBlock(Integer songIndex){
        hashObject temp = (hashObject) songTable[songIndex];
        songName temp2 = (songName) temp.V;
        return temp2.personBlock;
    }
    

    public boolean isCreated(String personName) {
        Integer index = (getCharIndex(personName.charAt(0)) * getCharIndex(personName.charAt(1)))%SIZE;
        int flag = 0;
        for(int multiply = 0; table[index] != null; multiply++){
            if(table[index] == personName)
                flag = 1;
            //index = (multiply*29) + (index%29);
            index = ((multiply*53)+(index%53))%SIZE;
        }
        return flag == 1;
    }
    
    public boolean isSongCreated(String songName) {
        Integer songIndex = (getCharIndex(songName.charAt(0)) * getCharIndex(songName.charAt(1)))%SIZE;
        int flag = 0;
        for(int multiply = 0; songTable[songIndex] != null; multiply++){
            hashObject temp = (hashObject) songTable[songIndex];
            songName temp2 = (songName) temp.V;
            String temp3 = temp2.nameOfBlock;
            if(temp3.equals(songName)){
                flag = 1;
                break;
            }
            songIndex = ((multiply*53)+(songIndex%53))%SIZE;
        }
        return flag == 1;
    }
    
    public Integer findIndex(String personName){
        Integer index = (getCharIndex(personName.charAt(0)) * getCharIndex(personName.charAt(1)))%SIZE;
        Integer found = -1;
        for(int multiply = 1; table[index] != null; multiply++){
            //if(getPersonName(index) == personName)
            if(getPersonName(index).equals(personName)){
                found = index;
                break;
            }
            //index = (multiply*29) + (index%29);
            index = ((multiply*53)+(index%53))%SIZE;
        }
        if(found == -1){
            //System.err.println("findIndex was not find an index in this string.");
        }
        return found;
    }
    
    public Integer findSongIndex(String songName){
        Integer songIndex = (getCharIndex(songName.charAt(0)) * getCharIndex(songName.charAt(1)))%SIZE;
        Integer found = -1;
        for(int multiply = 1; songTable[songIndex] != null; multiply++){
            //if(getPersonName(index) == personName)
            if(getSongName(songIndex).equals(songName)){
                found = songIndex;
                break;
            }
            //index = (multiply*29) + (index%29);
            songIndex = ((multiply*53)+(songIndex%53))%SIZE;
        }
        if(found == -1){
            //System.err.println("findIndex was not find an index in this string.(Song)");
        }
        return found;
    }
    
    public Integer getCharIndex(char a){
        Integer index = 0;
        for(int i = 0; (alphabet[i] != null); i++){
            if(alphabet[i].charAt(0) == a){
                index = i;
                break;
            }
        }
        return index;
    }
}
