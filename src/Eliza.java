
//////////////////// ALL ASSIGNMENTS INCLUDE THIS SECTION /////////////////////
//
// Title:           Eliza
// Files:           utf 8
// Course:          200, fall, 2018
//
// Author:          James Thomason
// Email:           jethomason@wisc.edu
// Lecturer's Name: Renault
//
///////////////////////////// CREDIT OUTSIDE HELP /////////////////////////////
//
// Students who get help from sources other than their partner must fully 
// acknowledge and credit those sources of help here.  Instructors and TAs do 
// not need to be credited here, but tutors, friends, relatives, room mates 
// strangers, etc do.  If you received no outside help from either type of 
// source, then please explicitly indicate NONE.
//
// Persons:         (identify each person and describe their help in detail)
// Online Sources:  (identify each URL and describe their assistance in detail)
//
/////////////////////////////// 80 COLUMNS WIDE ///////////////////////////////

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * The Eliza class holds the user input and response formation for a system that
 * collects user input and responds appropriately. Eliza is based off of a
 * computer program written at MIT in the 1960's by Joseph Weizenbaum. Eliza
 * uses keyword matching to respond to users in a way that displays interest in
 * the users and continues the conversation until instructed otherwise.
 */
public class Eliza {

	/*
	 * This method does input and output with the user. It calls supporting methods
	 * to read and write files and process each user input.
	 * 
	 * @param args (unused)
	 */
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);// Reads what the user types in.
		String computerName; // For choosing other personalities if you have them.
		Random randGen = new Random(Config.SEED); // Seeds the chosen response.
		ArrayList<String> dialog = new ArrayList<String>(); // Used to store the conversation between the program and
															// the user.
		boolean error = true; // for try catch loop with reading file.
		String[] preparedInput; // Some variable place holders so that the method is not called too many times.
		String userDialogChoice;
		String userFileName;
		String computerResponse;
		String userInput;

		// For later if multiple personalities are added.
		// System.out.println("Would you like to speak with Eliza, James, or Bucky?");
		// dialog.add("Would you like to speak with Eliza, James, or Bucky?");
		// userInput = sc.nextLine();
		// dialog.add(userInput);

		// if (userInput.trim().equals(null) || userInput.trim().equals("")) {
		computerName = "Eliza";
		// } else {
		// computerName = userInput;
		// }

		ArrayList<ArrayList<String>> responseTable = Eliza.loadResponseTable(computerName);
		System.out.println("Hi I'm " + computerName + " what's your name?");
		dialog.add("Hi I'm " + computerName + " what's your name?");
		String userName = sc.nextLine();
		dialog.add(userName);
		System.out.println("Nice to meet you " + userName + ". What would you like to talk about?");
		dialog.add("Nice to meet you " + userName + ". What would you like to talk about?");

		for (;;) { // Starts conversation loop.
			userInput = sc.nextLine();
			dialog.add(userInput);
			preparedInput = Eliza.prepareInput(userInput);
			if (preparedInput == null) { // Null is returned if a quit word is found
				System.out.println("Goodbye " + userName + ".");
				dialog.add("Goodbye " + userName + ".");
				System.out.println("Would you like a copy of our conversation?");
				userDialogChoice = sc.next().toLowerCase().trim();
				if (userDialogChoice.charAt(0) == 'y') { // Checks for a y to confirm that the user wants to save the
															// dialog.
					System.out.println("What would you like to call the file?");
					dialog.add("What would you like to call the file?");
					userFileName = sc.next().trim();
					dialog.add(userFileName);

					while (error) { // When you load an invalid file or file name, it continually prompts for you to
									// try again.
						try { // Attemps to save the dialog
							Eliza.saveDialog(dialog, userFileName);
						} catch (NullPointerException e) {
							System.out.println("ERROR: Invalid File");
						} catch (IOException e) {
							System.out.println("ERROR: Invalid File");
						}
						error = false;
						System.out.println("Thanks again for talking! Our conversation is saved in:\n" + userFileName);

					}
				}
				break;
			}
			computerResponse = Eliza.prepareResponse(preparedInput, randGen, responseTable);
			System.out.println(computerResponse);
			dialog.add(computerResponse);
		}

	}

	/**
	 * This method processes the user input, returning an ArrayList containing
	 * Strings, where each String is a phrase from the user's input. This is done by
	 * removing leading and trailing whitespace, making the user's input all lower
	 * case, then going through each character of the user's input. When going
	 * through each character this keeps all digits, alphabetic characters and '
	 * (single quote). The characters ? ! , . signal the end of a phrase, and
	 * possibly the beginning of the next phrase, but are not included in the
	 * result. All other characters such as ( ) - " ] etc. should be replaced with a
	 * space. This method makes sure that every phrase has some visible characters
	 * but no leading or trailing whitespace and only a single space between words
	 * of a phrase. If userInput is null then return null, if no characters then
	 * return a 0 length list, otherwise return a list of phrases. Empty phrases and
	 * phrases with just invalid/whitespace characters should NOT be added to the
	 * list.
	 * 
	 * Example userInput: "Hi, I am! a big-fun robot!!!" Example returned: "hi", "i
	 * am", "a big fun robot"
	 * 
	 * @param userInput text the user typed
	 * @return the phrases from the user's input
	 */
	public static ArrayList<String> separatePhrases(String userInput) {
		userInput = userInput.trim().toLowerCase();
		ArrayList<String> phrases = new ArrayList<>();
		String end[] = { "?", "!", ",", "." };
		int arrayCell = 0;
		boolean endChar = false;
		phrases.add("");

		if (userInput == null) {
			return null;
		}

		if (userInput.length() <= 0) {
			return phrases;
		}

		/**
		 * regular loop to loop through string, then do while not end character, add to
		 * index i
		 */
		for (int i = 0; i < userInput.length(); ++i) {
			if (userInput.charAt(i) != '?' && userInput.charAt(i) != '!' && userInput.charAt(i) != ','
					&& userInput.charAt(i) != '.') { // Checks that it isn't a phrase ending char
				if (userInput.charAt(i) == ' ' && userInput.charAt(i + 1) == ' ' && phrases.size() == arrayCell + 1) {
					phrases.set(arrayCell, phrases.get(arrayCell) + ("" + userInput.charAt(i)));
					++i;// gets rid of extra whitespace
				} else if (phrases.size() > 0) { // Concats if there are characters in the array cell already
					if (!Character.isDigit(userInput.charAt(i)) && !Character.isLetter(userInput.charAt(i))
							&& userInput.charAt(i) != '\'') {
						phrases.set(arrayCell, phrases.get(arrayCell) + (" "));
					} else {
						phrases.set(arrayCell, phrases.get(arrayCell) + ("" + userInput.charAt(i)));
					}
				} else { // Creates a new row for new characters.
					phrases.add(arrayCell, "");
					if (!Character.isDigit(userInput.charAt(i)) && !Character.isLetter(userInput.charAt(i))
							&& userInput.charAt(i) != '\'') {
						phrases.set(arrayCell, " ");
					} else {
						phrases.set(arrayCell, ("" + userInput.charAt(i)).trim());
					}
				}
			} else if (!phrases.get(arrayCell).equals(" ") || !phrases.get(arrayCell).equals("") // Adds new row for a
																									// new phrase
					|| !phrases.get(arrayCell).isEmpty()) {
				++arrayCell;
				phrases.add("");
			}
		}

		for (int i = 0; i < phrases.size(); ++i) {
			phrases.set(i, phrases.get(i).trim());
		}

		for (int i = 0; i < phrases.size(); ++i) { // Gets rid of empty Array cells
			if (phrases.get(i).equals(" ") || phrases.get(i).equals("") || phrases.get(i).isEmpty()) {
				phrases.remove(i);
				--i;
			}
		}

		return phrases;
	}

	/**
	 * Checks whether any of the phrases in the parameter match a quit word from
	 * Config.QUIT_WORDS. Note: complete phrases are matched, not individual words
	 * within a phrase.
	 * 
	 * @param phrases List of user phrases
	 * @return true if any phrase matches a quit word, otherwise false
	 */
	public static boolean foundQuitWord(ArrayList<String> phrases) {

		for (int i = 0; i < phrases.size(); ++i) { // Iterates through the phrase list passed in and if a quit word is
													// found then the method returns true.
			for (int j = 0; j < Config.QUIT_WORDS.length; ++j) {
				if (phrases.get(i).equals(Config.QUIT_WORDS[j])) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Iterates through the phrases of the user's input, finding the longest phrase
	 * to which to respond. If two phrases are the same length, returns whichever
	 * has the lower index in the list. If phrases parameter is null or size 0 then
	 * return "" (Update 11/15/18).
	 * 
	 * @param phrases List of user phrases
	 * @return the selected phrase
	 */
	public static String selectPhrase(ArrayList<String> phrases) {
		int longestPhrase = phrases.get(0).length();
		int index = 0;
		String selectedPhrase = "";

		if (phrases.size() == 0 || phrases.isEmpty() || phrases == null) { // Checks that the ArrayList passed in is
																			// valid
			return "";
		}
		for (int i = 0; i < phrases.size(); ++i) {
			if (phrases.get(i).length() > longestPhrase) { // Checks if the phrase is longer than a previous and if it
															// is then longestPhrase gets updated.
				longestPhrase = phrases.get(i).length();
				index = i;
			}
			selectedPhrase = phrases.get(index);
		}

		return selectedPhrase;
	}

	/**
	 * Looks for a replacement word for the word parameter and if found, returns the
	 * replacement word. Otherwise if the word parameter is not found then the word
	 * parameter itself is returned. The wordMap parameter contains rows of match
	 * and replacement strings. On a row, the element at the 0 index is the word to
	 * match and if it matches return the string at index 1 in the same row. Some
	 * example word maps that will be passed in are Config.INPUT_WORD_MAP and
	 * Config.PRONOUN_MAP.
	 * 
	 * If word is null return null. If wordMap is null or wordMap length is 0 simply
	 * return word parameter. For this implementation it is reasonable to assume
	 * that if wordMap length is >= 1 then the number of elements in each row is at
	 * least 2.
	 * 
	 * @param word    The word to look for in the map
	 * @param wordMap The map of words to look in
	 * @return the replacement string if the word parameter is found in the wordMap
	 *         otherwise the word parameter itself.
	 */
	public static String replaceWord(String word, String[][] wordMap) {
		if (word == null) {
			return null;
		}
		if (wordMap == null || wordMap.length <= 0) {
			return word;
		}

		for (int i = 0; i < wordMap.length; ++i) {
			if (wordMap[i][0].equals(word)) {
				word = wordMap[i][1];
				break;
			}
		}
		return word;
	}

	/**
	 * Concatenates the elements in words parameter into a string with a single
	 * space between each array element. Does not change any of the strings in the
	 * words array. There are no leading or trailing spaces in the returned string.
	 * 
	 * @param words a list of words
	 * @return a string containing all the words with a space between each.
	 */
	public static String assemblePhrase(String[] words) {
		String assembledPhrase = "";

		for (int i = 0; i < words.length; ++i) {
			String add = words[i].trim();
			assembledPhrase = assembledPhrase.concat(" ");
			assembledPhrase = assembledPhrase.concat(add);
		}
		return assembledPhrase.trim();
	}

	/**
	 * Replaces words in phrase parameter if matching words are found in the mapWord
	 * parameter. A word at a time from phrase parameter is looked for in wordMap
	 * which may result in more than one word. For example: i'm => i am Uses the
	 * replaceWord and assemblePhrase methods. Example wordMaps are
	 * Config.PRONOUN_MAP and Config.INPUT_WORD_MAP. If wordMap is null then phrase
	 * parameter is returned. Note: there will Not be a case where a mapping will
	 * itself be a key to another entry. In other words, only one pass through
	 * swapWords will ever be necessary.
	 * 
	 * @param phrase  The given phrase which contains words to swap
	 * @param wordMap Pairs of corresponding match & replacement words
	 * @return The reassembled phrase
	 */
	public static String swapWords(String phrase, String[][] wordMap) {

		if (wordMap == null) {
			return phrase;
		}

		Scanner scWordCount = new Scanner(phrase);
		Scanner scFillArray = new Scanner(phrase);
		String phraseScan;
		int numWords = 0;

		for (;;) {
			if (scWordCount.hasNext()) { // Counts how many individual words are the string passed in
				phraseScan = scWordCount.next();
				++numWords;
			} else {
				break;
			}
		}

		String[] wordArray = new String[numWords]; // Creates and array with a size equal to the number of words in the
													// passed in string

		for (int i = 0;; ++i) { // Puts individual words into their own array cell.
			if (scFillArray.hasNext()) {
				wordArray[i] = scFillArray.next();
			} else {
				break;
			}
		}

		for (int i = 0; i < wordArray.length; ++i) { // Uses replace word method to identify and switch words using
														// passed in word map
			wordArray[i] = Eliza.replaceWord(wordArray[i], wordMap);
		}

		return Eliza.assemblePhrase(wordArray);

	}

	/**
	 * This prepares the user input. First, it separates input into phrases (using
	 * separatePhrases). If a phrase is a quit word (foundQuitWord) then return
	 * null. Otherwise, select a phrase (selectPhrase), swap input words (swapWords
	 * with Config.INPUT_WORD_MAP) and return an array with each word its own
	 * element in the array.
	 * 
	 * @param input The input from the user
	 * @return words from the selected phrase
	 */
	public static String[] prepareInput(String input) {
		ArrayList<String> phrases = Eliza.separatePhrases(input);
		String selectedPhrase;
		String swappedPhrase;

		for (int i = 0; i < phrases.size(); ++i) {
			if (Eliza.foundQuitWord(phrases)) {
				return null;
			}
		}

		selectedPhrase = Eliza.selectPhrase(phrases);
		swappedPhrase = Eliza.swapWords(selectedPhrase, Config.INPUT_WORD_MAP);

		Scanner scWordCount = new Scanner(swappedPhrase);
		Scanner scFillArray = new Scanner(swappedPhrase);
		String phraseScan;
		int numWords = 0;

		for (;;) {
			if (scWordCount.hasNext()) { // Counts how many individual words are the string passed in
				phraseScan = scWordCount.next();
				++numWords;
			} else {
				break;
			}
		}

		String[] wordArray = new String[numWords]; // Creates and array with a size equal to the number of words in the
													// passed in string

		for (int i = 0;; ++i) { // Puts individual words into their own array cell.
			if (scFillArray.hasNext()) {
				wordArray[i] = scFillArray.next();
			} else {
				break;
			}
		}

		return wordArray;
	}

	/**
	 * Reads a file that contains keywords and responses. A line contains either a
	 * list of keywords or response, any blank lines are ignored. All leading and
	 * trailing whitespace on a line is ignored. A keyword line begins with
	 * "keywords" with all the following tokens on the line, the keywords. Each line
	 * that follows a keyword line that is not blank is a possible response for the
	 * keywords. For example (the numbers are for our description purposes here and
	 * are not in the file):
	 * 
	 * 1 keywords computer 2 Do computers worry you? 3 Why do you mention computers?
	 * 4 5 keywords i dreamed 6 Really, <3>? 7 Have you ever fantasized <3> while
	 * you were awake? 8 9 Have you ever dreamed <3> before?
	 *
	 * In line 1 is a single keyword "computer" followed by two possible responses
	 * on lines 2 and 3. Line 4 and 8 are ignored since they are blank (contain only
	 * whitespace). Line 5 begins new keywords that are the words "i" and "dreamed".
	 * This keyword list is followed by three possible responses on lines 6, 7 and
	 * 9.
	 * 
	 * The keywords and associated responses are each stored in their own ArrayList.
	 * The response table is an ArrayList of the keyword and responses lists. For
	 * every keywords list there is an associated response list. They are added in
	 * pairs into the list that is returned. There will always be an even number of
	 * items in the returned list.
	 * 
	 * Note that in the event an IOException occurs when trying to read the file
	 * then an error message "Error reading <fileName>", where <fileName> is the
	 * parameter, is printed and a non-null reference is returned, which may or may
	 * not have any elements in it.
	 * 
	 * @param fileName The name of the file to read
	 * @return The response table
	 * @throws FileNotFoundException
	 */
	public static ArrayList<ArrayList<String>> loadResponseTable(String fileName) {
		FileInputStream userFile = null;

		if (!fileName.contains(Config.RESPONSE_FILE_EXTENSION) && !fileName.contains(".txt")) {
			fileName = fileName + Config.RESPONSE_FILE_EXTENSION;
		}

		try { // Tries to load the file but will print out error if not found
			userFile = new FileInputStream(fileName);
		} catch (NullPointerException e) {
			System.out.println("Error reading file name");
		} catch (IOException e) {
			System.out.println("Error reading" + fileName);
		}

		Scanner fileScan = new Scanner(userFile);
		ArrayList<ArrayList<String>> responseTable = new ArrayList<ArrayList<String>>();
		int arrayCell = -1; // For cleaner code further down starts at -1 because index starts at 0.
		boolean firstResponse = true; // So that multiple responses to keywords are in one array.

		while (fileScan.hasNextLine()) {
			if (fileScan.hasNext("keywords")) { // Looks for key words and creates an array to put them in.
				fileScan.next();
				++arrayCell;
				responseTable.add(new ArrayList<String>()); // Creates the Array for the keywords.
				responseTable.get(arrayCell).add(fileScan.nextLine().trim());
				firstResponse = true;
			} else if (fileScan.hasNext("") || fileScan.hasNext(" ")) { // Skips over blank lines.
				fileScan.nextLine();
			} else if (firstResponse) { // Creates an new Array for responses.
				++arrayCell;
				responseTable.add(new ArrayList<String>());
				firstResponse = false;
				responseTable.get(arrayCell).add(fileScan.nextLine().trim());
			} else if (!firstResponse) { // Adds other responses besides the first into the same Array as the first
											// response.
				responseTable.get(arrayCell).add(fileScan.nextLine().trim());
			}
		}

		if (responseTable.size() % 2 != 0) { // Makes Sure size is even
			responseTable.add(new ArrayList<String>());
		}

		return responseTable;

	}

	/**
	 * Checks to see if the keywords match the sentence. In other words, checks to
	 * see that all the words in the keyword list are in the sentence and in the
	 * same order. If all the keywords match then this method returns an array with
	 * the unmatched words before, between and after the keywords. If the keywords
	 * do not match then null is returned.
	 * 
	 * When the phrase contains elements before, between, and after the keywords,
	 * each set of the three is returned in its own element String[] keywords =
	 * {"i", "dreamed"}; String[] phrase = {"do", "you", "know", that", "i", "have",
	 * "dreamed", "of", "being", "an", "astronaut"};
	 * 
	 * toReturn[0] = "do you know that" toReturn[1] = "have" toReturn[2] = "of being
	 * an astronaut"
	 * 
	 * In an example where there is a single keyword, the resulting List's first
	 * element will be the the pre-sequence element and the second element will be
	 * everything after the keyword, in the phrase String[] keywords = {"always"};
	 * String[] phrase = {"I", "always", "knew"};
	 * 
	 * toReturn[0] = "I" toReturn[1] = "knew"
	 * 
	 * In an example where a keyword is not in the phrase in the correct order, null
	 * is returned. String[] keywords = {"computer"}; String[] phrase = {"My","dog",
	 * "is", "lost"};
	 * 
	 * return null
	 * 
	 * @param keywords The words to match, in order, in the sentence.
	 * @param phrase   Each word in the sentence.
	 * @return The unmatched words before, between and after the keywords or null if
	 *         the keywords are not all matched in order in the phrase.
	 */
	public static String[] findKeyWordsInPhrase(ArrayList<String> keywords, String[] phrase) {
		// see the algorithm presentation linked in Eliza.pdf.
		int keyCheck = 0;
		int orderCheck = 0;
		int returnedArraySize = 0;

		for (int i = 0; i < keywords.size(); ++i) { // Checks the phrase for keywords and makes sure they are in order.
			for (int j = 0; j < phrase.length; ++j) {
				if (keywords.get(i).equals(phrase[j])) {
					++keyCheck;
					if (returnedArraySize == 0) {
						returnedArraySize += 2;
					} else {
						++returnedArraySize;
					}
					if (orderCheck == 0) {
						orderCheck = j;
					} else if (orderCheck > j) {
						return null;
					}
				}
			}
		}

		if (keywords.size() != keyCheck) { // Checks that all key words were in the phrase.
			return null;
		}

		String[] unmatchedWords = new String[returnedArraySize]; // Creates an Array to put the unmathced words in
		for (int i = 0; i < unmatchedWords.length; ++i) {
			unmatchedWords[i] = "";
		}
		int arrayCell = 0;
		boolean firstWordAdd = true;

		for (int i = 0; i < phrase.length; ++i) {
			if (!keywords.contains(phrase[i])) {
				if (firstWordAdd) {
					unmatchedWords[arrayCell] = phrase[i];
					firstWordAdd = false;
				} else {
					unmatchedWords[arrayCell] = unmatchedWords[arrayCell] + (" ") + phrase[i];
				}
			} else {
				firstWordAdd = true;
				++arrayCell;
			}
		}

		return unmatchedWords;
	}

	/**
	 * Selects a randomly generated response within the list of possible responses
	 * using the provided random number generator where the number generated
	 * corresponds to the index of the selected response. Use Random nextInt(
	 * responseList.size()) to generate the random number. If responseList is null
	 * or 0 length then return null.
	 * 
	 * @param rand         A random number generator.
	 * @param responseList A list of responses to choose from.
	 * @return A randomly selected response
	 */
	public static String selectResponse(Random rand, ArrayList<String> responseList) {
		String randResponse = "";

		if (responseList == null || responseList.size() == 0) {
			return null;
		}

		int randNum = rand.nextInt(responseList.size());

		randResponse = responseList.get(randNum);

		return randResponse;
	}

	/**
	 * This method takes processed user input and forms a response. This looks
	 * through the response table in order checking to see if each keyword pattern
	 * matches the userWords. The first matching keyword pattern found determines
	 * the list of responses to choose from. A keyword pattern matches the
	 * userWords, if all the keywords are found, in order, but not necessarily
	 * contiguous. This keyword matching is done by findKeyWordsInPhrase method. See
	 * the findKeyWordsInPhrase algorithm in the Eliza.pdf.
	 * 
	 * If no keyword pattern matches then Config.NO_MATCH_RESPONSE is returned.
	 * Otherwise one of possible responses for the matched keywords is selected with
	 * selectResponse method. The response selected is checked for the replacement
	 * symbol <n> where n is 1 to the length of unmatchedWords array returned by
	 * findKeyWordsInPhrase. For each replacement symbol the corresponding unmatched
	 * words element (index 0 for <1>, 1 for <2> etc.) has its pronouns swapped with
	 * swapWords using Config.PRONOUN_MAP and then replaces the replacement symbol
	 * in the response.
	 * 
	 * @param userWords     using input after preparing.
	 * @param rand          A random number generator.
	 * @param responseTable A table containing a list of keywords and response
	 *                      pairs.
	 * @return The generated response
	 */
	public static String prepareResponse(String[] userWords, Random rand, ArrayList<ArrayList<String>> responseTable) {
		String response = null;
		String[] unmatchedWords = null;
		String swap = null; // These three strings are needed to concatenate the response after the number
							// is replaced.
		String beforeSwap = null;
		String afterSwap = null;
		String[] foundKeyWords = null; // For assigning the result of findKeyWords so I don;t have to call the method
										// more than necessary.

		for (int i = 0; i < responseTable.size(); i += 2) { // Uses findKeyWordsInPhrase to prepare phrase.
			foundKeyWords = Eliza.findKeyWordsInPhrase(responseTable.get(i), userWords);
			if (foundKeyWords != null) {
				response = Eliza.selectResponse(rand, responseTable.get(i + 1));
				unmatchedWords = Eliza.findKeyWordsInPhrase(responseTable.get(i), userWords);
				break;
			}
		}

		if (response == null) { // If there is no response
			return Config.NO_MATCH_RESPONSE;
		}

		for (int i = 1; i <= foundKeyWords.length; ++i) { // Creates new response where swap characters are replaced
			if (response.contains("<" + i + ">")) {
				swap = Eliza.swapWords(unmatchedWords[i - 1], Config.PRONOUN_MAP);
				beforeSwap = response.substring(0, response.indexOf("<" + i + ">"));
				afterSwap = response.substring(response.indexOf("<" + i + ">") + 3);
				response = beforeSwap.concat(swap).concat(afterSwap);
			}
		}

		return response;
	}

	/**
	 * Creates a file with the given name, and fills that file line-by-line with the
	 * tracked conversation. Every line ends with a newline. Throws an IOException
	 * if a writing error occurs.
	 * 
	 * @param dialog   the complete conversation
	 * @param fileName The file in which to write the conversation
	 * @throws IOException
	 */
	public static void saveDialog(ArrayList<String> dialog, String fileName) throws IOException {
		if (!fileName.contains(".txt")) {
			fileName = fileName + ".txt";
		}
		FileOutputStream elizaDialog = new FileOutputStream(fileName); // Creates a new file with saved dialog
		PrintWriter outFS = new PrintWriter(elizaDialog);

		for (int i = 0; i < dialog.size(); ++i) {
			outFS.println(dialog.get(i));
		}
		outFS.flush();
		outFS.close();
	}

}