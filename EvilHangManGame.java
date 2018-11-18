import java.util.Stack;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
/**
 * Write a description of class EvilHangMan1 here.
 *
 * @author (Marco Rosales)
 * @version (a version number or a date)
 */
//I like this one more. I just have to figure out how to print the words of that length
public class EvilHangManGame

{ 
    public static void main(String [] args) throws IOException
    {
        //I have my global variables
        
        HashMap<String, ArrayList <String>> wordFamilies = new HashMap<String, ArrayList <String>>();
        ////This arrayList has all the possible words from the dictionary
        ArrayList<String> allPossibleDictionaryWords = new ArrayList<String>();
        ArrayList<String> computersCheatSheet = new ArrayList<String>();
        ArrayList<String> lookForWords = new ArrayList<String>();
        ArrayList<String> theRightWordsToUse = new ArrayList<String>();
        ArrayList<String> arrangement = new ArrayList<String>();
        ArrayList<String> latestWords = new ArrayList<String>();
        ////lets user know what was already guessed
        Stack<Character> previouslyPredicted = new Stack<Character>();
        String hyphensForMissingLetters = "";
        File wordsFile = new File("dictionary.txt");
        Scanner readFile = new Scanner(wordsFile);
        int overallUserGuesses = 0;
        char usedCharacters = 0;
        char accessibleLetters = 0;
        boolean succeeded = false;
        Random  generation = new Random();   
        if(!fileIsThere())
        {
                IOException e = new IOException();
                System.out.println("Something went wrong");
                System.out.println("Caught exception");
                throw e;
        }
        //This while loop is to check which list of words it should use.
        //This is where the computer makes it hard for the user to guess correctly
        while(readFile.hasNext())
        {
            allPossibleDictionaryWords.add(readFile.next());
        }
        System.out.println("You have entered the darkside of Hangman");
        Scanner chosen = new Scanner(System.in);
        System.out.println("How long do you want your word?");
        System.out.println("Enter a number to describe your word length");
        String chosenWords = chosen.next();
        //enterIntegerForWordLengthandGuessLength checks if chosenWords is an integer or not
        //we store that in usingWOrdsThisLong
        int usingWordsThisLong = enterIntegerForWordLengthandGuessLength(chosenWords);
        //scrambledListOfWords accepts the original word list with all its words with
        //different lengths and then takes the users guess anotherTry and returns
        //arraylist lookForWords with that length
        //If there are no words with that length then we go into this while loop
        //so the user can enter a new length
        lookForWords = scrambledListOfWordsToUse(allPossibleDictionaryWords,usingWordsThisLong);
        while(lookForWords.size() == 0)
        {
            System.out.println("The length you entered is invalid");
            System.out.println("Enter a different length for a word");
            int anotherTry = chosen.nextInt();
            lookForWords = scrambledListOfWordsToUse(allPossibleDictionaryWords,anotherTry);
        }
        theRightWordsToUse = lookForWords;
        for(int k = 0; k < theRightWordsToUse.get(0).length();k++)
        {
            //hyphensForMissingLetters is just the hypens that I put to represent the length
            hyphensForMissingLetters += "-";
        }
        System.out.println("How many guesses would you like to be allowed?");
        String amountOfGuessesUserWants = chosen.next();
        int thisAmountOfGuesses = enterIntegerForWordLengthandGuessLength(amountOfGuessesUserWants);
        int usersAllowedGuesses = thisAmountOfGuesses;
        //This is were the main part of the game is played, this will keep 
        //going so long as the user has some guesses left
        while(isGuessValid(thisAmountOfGuesses))
        {
            System.out.println("Do you want to see the total number of words?");
            System.out.println("Press 6 for no, 7 for yes");
            String vision = chosen.next();
            int haveVision = enterIntegerForWordLengthandGuessLength(vision);
            if(haveVision == 7)
            {
                System.out.println("These are the number of words you can guess from " + theRightWordsToUse.size());
            }
            //pickedLetter will be the letter the user guesses
            char pickedLetter = letterUserChose(chosen);
            //our first key to our wordFamilies
            String wordFamiliesKey = hyphensForMissingLetters;
            //this will have the whole arraylist theRightWordsToUse
            wordFamilies.put(wordFamiliesKey, theRightWordsToUse);
            //we push the pickedLetter onto the stack of previouslyPredicted
            previouslyPredicted.push(pickedLetter);
            //computersCheatSheet is the list with more possibilities to win
            computersCheatSheet = cheatingMachine(theRightWordsToUse, pickedLetter);
            Iterator<Character> stackIterationShowLetters = previouslyPredicted.iterator();
            //here we iterate through the stack and show the user what letters
            //they have guessed
            while(stackIterationShowLetters.hasNext())
            {
                System.out.println("You guessed the letter " + stackIterationShowLetters.next() );
            }
            //if the list has the letter, than this will be true
            if(letterIterator(computersCheatSheet.get(0), pickedLetter))
            {
                //if it is true I use my global variables to start the second game loop
                 usedCharacters = pickedLetter;
                 overallUserGuesses = thisAmountOfGuesses;
                 break;
            }
            else
            {
                System.out.println("");
                System.out.println("Sorry that letter is not correct");
                System.out.println("");
                System.out.println("You have " + --thisAmountOfGuesses + " number of guesses");
                //This if statement lets the player know when they have run out of guesses
                if(thisAmountOfGuesses <= 0)
                {
                   System.out.println("No more guesses available");
                   if(isGuessValid(0) == false)
                   {
                      break;
                   }
                }
            }
        }
        System.out.println("Well what do you know. Looks like that one was a valid letter!");
        //computersCheatSheet has all the words the user has guessd
        //So long as the overallUserGuesses is not equal to 0
        if(overallUserGuesses != 0)
        {
            //arrangement array list will only hold word arrangements for words in the
            //computersCheatSheet with the letter. So if the letter guessed was e
            //then the computersCheatSheet will go through all the words with e's
            //in various positions, thats what the first for loop does.
            for(int k = 0; k < computersCheatSheet.size(); k++)
            {
                arrangement.add(verifyWordsWithCorrectCharacterArrangement(computersCheatSheet.get(k), usedCharacters));
            }
            //this will remove any duplicates
            arrangement = duplicateArrangements(arrangement, usedCharacters);
            //This loop is used to go through all the possible arrangements in the list
            //we then assign the values to the arrangement keys
            for(int j = 0; j < arrangement.size(); j++)
            {
                ArrayList<String> reusableList = new ArrayList<String>();
                for(int l = j; l < computersCheatSheet.size(); l++)
                {
                    //for each arrangement, it goes through the computersCheatSheet
                    //it will then use verifyWordsWithCorrectCharacterArrangement
                    //and if the pattern in computersCheatSheet
                    //is equal to the pattern at index
                    //then we add it to the arraylist reusableList
                    //so if computersCheatSheet pattern matches arrangement pattern then
                    //we will grab all those words and put it in arraylist reusableList
                    if(verifyWordsWithCorrectCharacterArrangement(computersCheatSheet.get(l), usedCharacters).equals(arrangement.get(j)))
                    {
                        reusableList.add(computersCheatSheet.get(l));
                    }
                }
                //Once that is done I then add arrangement at specified index as our key
                //so wordFamilies uses words that have that pattern of letters
                //this has our keys and a values with the corresponding word patterns
                wordFamilies.put(arrangement.get(j),reusableList);
            }
            //here I iterate through all the keys in wordFamilies to find
            //the values
            for(ArrayList<String> value: wordFamilies.values())
            {
                latestWords = value;
            }
            String changeArrangement = arrangement.get(0);
            //This loop does the same as our other loop except it chooses
            //a word for the user to try to guess a letter for
            while(isGuessValid(overallUserGuesses))
            {
                System.out.println("Do you want to see the total number of words?");
                System.out.println("Press 6 for no, 7 for yes");
                String vision = chosen.next();
                int haveVision = enterIntegerForWordLengthandGuessLength(vision);
                if(haveVision == 7)
                {
                    System.out.println("These are the number of words you can guess from " + theRightWordsToUse.size());
                }
                char pickedLetter = letterUserChose(chosen);
                previouslyPredicted.push(pickedLetter);
                Iterator<Character> stackIterationShowLetters = previouslyPredicted.iterator();
                while(stackIterationShowLetters.hasNext())
                {
                    System.out.println("You guessed the letter " + stackIterationShowLetters.next() );
                }
                if(letterIterator(latestWords.get(0), pickedLetter))
                {
                    System.out.println("Hey you got a letter right! right on!");
                    System.out.println("So far you have filled : " + wordArrangement(latestWords.get(0), changeArrangement, pickedLetter));
                    changeArrangement = wordArrangement(latestWords.get(0), changeArrangement, pickedLetter);
                    System.out.println("You have " + --thisAmountOfGuesses + " number of guesses");
                    if(thisAmountOfGuesses <= 0)
                    {
                        if(isGuessValid(0) == false)
                        {
                            break;
                        }
                    }
                }
                else
                {
                    System.out.println("");
                    System.out.println("Sorry that letter is not correct");
                    System.out.println("");
                    System.out.println("So far you have filled:  " + changeArrangement);// + wordArrangement(latestWords.get(0), changeArrangement, pickedLetter)); //+ hyphensForMissingLetters
                    System.out.println("You have " + --thisAmountOfGuesses + " number of guesses");
                    if(thisAmountOfGuesses <= 0)
                    {
                        System.out.println("No more guesses available");
                        if(isGuessValid(0) == false)
                        {
                            break;
                        }
                    }
                }
                int keepTrack = 0;
                for(int n = 0; n < changeArrangement.length(); n++)
                {
                    if(changeArrangement.charAt(n) != '-')
                    {
                        keepTrack += 1;
                    }
                }
                if(keepTrack == changeArrangement.length())
                {
                    System.out.println("Wow, you are impressive");
                    System.out.println("You beat the computer! Congratulations!");
                    System.out.println("The word guessed was: " + changeArrangement);
                }
            }
        }
    }
    
    public static boolean makeSureItsAinteger(String entered)
    {
        try
        {
            Integer.parseInt(entered);
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }
    
    public static int enterIntegerForWordLengthandGuessLength(String wordLength)
    {
        Scanner findWords = new Scanner(System.in);
        while(!makeSureItsAinteger(wordLength))
        {
            System.out.println("Put in a number, so the computer can fetch words that have the length of that number");
            wordLength = findWords.next();
        }
        return Integer.parseInt(wordLength);
    }
    
    public static boolean fileIsThere()
    {
        try
        {
            File wordsFile = new File("dictionary.txt");
            Scanner readFile = new Scanner(wordsFile);
            return true;
        }
        catch(IOException e)
        {
            return false;
        }
    }
    
    public static ArrayList<String> scrambledListOfWordsToUse(ArrayList<String> dictionaryWords, int userWantsWordsThisLong)
    {
        ArrayList<String> userWantsTheseWords = new ArrayList<String>();
        Random scramble = new Random();
        for(int k = 0; k < dictionaryWords.size(); k++)
        {
            if(dictionaryWords.get(k).length() == userWantsWordsThisLong)
            {
                userWantsTheseWords.add(dictionaryWords.get(k));
            }
        }
        int wordsThisLongToPlayWith = userWantsTheseWords.size();
        ArrayList<String> wordsWeWillPlayWith = new ArrayList<String>();
        for(int j = 0; j < wordsThisLongToPlayWith; j++)
        {
            wordsWeWillPlayWith.add(userWantsTheseWords.get(scramble.nextInt(wordsThisLongToPlayWith)));
        }
        return wordsWeWillPlayWith;
    }
    
    public static boolean previouslyPredicted(Stack<Character> chosenLetters, char letterOfChoice )
    {
        Iterator<Character> chosen = chosenLetters.iterator();
        while(chosen.hasNext())
        {
            if(chosen.next() == letterOfChoice)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        return (Boolean) null;
    }
    
    public static boolean isGuessValid(int theFinalCountDown)
    {
        if(theFinalCountDown == 0)
        {
            System.out.println("Oh no!");
            System.out.println("You have run out of guesses");
            System.out.println("You have been defeated by a computer");
            System.out.println("Don't feel bad, SkyNet will spare your life");
            return false;
        }
        else
        {
            return true;
        }
    }
    
    public static char letterUserChose(Scanner letter) 
    {
        System.out.println("What letter will you use? Enter it please");
        String letterEntered = letter.next().toLowerCase();
        int keepTrack = 1;
        if(keepTrack > 0)
        {
            while(keepTrack > 0)
            {
                try
                {
                    int userEnteredNumber = Integer.parseInt(letterEntered);
                    System.out.println("Hey that's not a letter!");
                    System.out.println("Give me a letter silly");
                    letterEntered = letter.next();
                    keepTrack = 1;
                }
                catch(NumberFormatException e)
                {
                    keepTrack = 0;
                    return letterEntered.charAt(0);
                }
            }
        }
        if(letterEntered.length() > 1 || letterEntered.length() < 1)
        {
            return letterUserChose(letter);
        }
        else
        {
            return letterEntered.charAt(0);
        }
    }
    
    public static boolean userLetterIteration(String wordChoice, char theLetterUserEntered)
    {
        boolean seeIt = false;
        for(int k = 0; k < wordChoice.length(); k++)
        {
            if(wordChoice.charAt(k) == theLetterUserEntered)
            {
                seeIt = true;
            }
        }
        return seeIt;
    }
    
    public static ArrayList<String> withdrawWordIterator(ArrayList<String> seeLettersInWord, char theLetterUserEntered)
    {
        for(int k = 0; k < seeLettersInWord.size(); k++)
        {
            if(userLetterIteration(seeLettersInWord.get(k), theLetterUserEntered))
            {
                seeLettersInWord.remove(k);
            }
        }
        return seeLettersInWord;
    }
    
    public static ArrayList<String> cheatingMachine(ArrayList<String> wordsForComputer, char chosenLetter)
    {
        ArrayList<String> playsWithLetters = new ArrayList<String>();
        for(int k = 0; k < wordsForComputer.size(); k++)
        {
            if(userLetterIteration(wordsForComputer.get(k), chosenLetter))
            {
                playsWithLetters.add(wordsForComputer.get(k));
            }
        }
        for(int j = 0; j < wordsForComputer.size(); j++)
        {
            if(userLetterIteration(wordsForComputer.get(j), chosenLetter))
            {
                withdrawWordIterator(wordsForComputer, chosenLetter);
            }
        }
        if(playsWithLetters.size() > wordsForComputer.size())
        {
            return playsWithLetters;
        }
        else
        {
            return wordsForComputer;
        }
    }
    
    public static boolean letterIterator(String wordChoice, char theLetterUserEntered)
    {
        int keepTrack = 0;
        for(int k = 0; k < wordChoice.length(); k++)
        {
            if(wordChoice.charAt(k) == theLetterUserEntered)
            {
                keepTrack++;
            }
        }
        if(keepTrack > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public static String wordArrangement(String wordChoice, String hyphenOrLetterArrangement, char letterOfChoice)
    {
        int cap = wordChoice.length();
        String arrangement = "";
        if(userLetterIteration(wordChoice,letterOfChoice))
        {
            for(int k = 0; k < cap; k++)
            {
                if(wordChoice.charAt(k) == letterOfChoice)
                {
                    arrangement += wordChoice.charAt(k);
                }
                else
                {
                    arrangement += hyphenOrLetterArrangement.charAt(k);
                }
            }
        }
        return arrangement;
    }
    
    public static String verifyWordsWithCorrectCharacterArrangement(String wordChoice, char letterOfChoice)
    {
        String dashes = "";
        for(int k = 0; k < wordChoice.length(); k++)
        {
            dashes += "-";
        }
        String arrangement;
        arrangement = wordArrangement(wordChoice, dashes, letterOfChoice);
        return arrangement;
    }
    
    public static ArrayList<String> duplicateArrangements(ArrayList<String> wordChoice, char letterOfChoice)
    {
        ArrayList<String> arrangements = new ArrayList<String>();
        for(int k = 0; k < wordChoice.size(); k++)
        {
            arrangements.add(verifyWordsWithCorrectCharacterArrangement(wordChoice.get(k), letterOfChoice));
        }
        Set<String> findDuplicates = new LinkedHashSet<String>(arrangements);
        arrangements.clear();
        arrangements.addAll(findDuplicates);
        return arrangements;
    }
}
