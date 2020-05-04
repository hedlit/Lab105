package solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/** Q&A:
 * 
 * My question: Why is the "preferred way", the one we except happened, to switch characters?
 * Or have I misunderstood??
 * 
 * Is your solution recursive or iterative?
 * Iterative. Doesn't call opt(i,j) but instead gets value from optMatrix[i,j]
 * 
 * What is the time complexity, and more importantly why?
 * See report.
 * 
 * What would the time complexity of a recursive solution without cache be?
 * ???
 * It takes more memory to do recursive calls, there would be (n+1)*(m+1) calls (stack would have to be that big),
 * because one call per each box in the matrix.
 * 
 * Can you think of any applications of this type of string alignment?
 * Spell checking. Compilers. Looking for RNA-patterns.
 * 
 * What could the costs represent in the applications?
 * For spell checking, the cost (gain) is bigger the more alike the characters are.
 * Could it represent anything else??
 * 
 * */


public class Gorilla {
	private long queriesTime;
	private long parseTime;
	private int delta = -4; //cost of inserting a character
	
	private int Q; //The number of queries
	private int[][] gains; //all the gains (costs) of switching characters
	private int[][] optMatrix; //matrix holding the results from opt(i,j)
	private HashMap<Character, Integer> letterIndices; //The indices in which each character in the alphabet will occur in the gains-matrix

	public static void main(String[] args) {
        Gorilla g = new Gorilla();
		Scanner scan = new Scanner(System.in);
		
		long start = System.currentTimeMillis();
		//Read and store the characters and the cost (gain) for switching them
	    g.parse(scan);
		long stop = System.currentTimeMillis();
		g.parseTime = stop - start;
		
		long start2 = System.currentTimeMillis();
		//For each query, align the strings with maximal gain and print out the result
		for(int q = 0; q < g.Q; q++) {
			g.alignStrings(scan);
		}
		long stop2 = System.currentTimeMillis();
		g.queriesTime = stop2 - start2;
		
		scan.close();
		g.printTimeResult();
	}
	
	private void parse(Scanner scan) {
		//First line in input: the characters in this alphabet, space separated.
		//Index the characters 0..k-1, by the order they are read, and store the indices in letterIndices
		String alphabet = scan.nextLine();
		ArrayList<Character> characters = new ArrayList<>();
		letterIndices = new HashMap<Character, Integer>();
		int index = 0;
		for(char c : alphabet.toCharArray()) {
			if(c != ' ') {
				characters.add(c);
				letterIndices.put(c, index);
				index++;
			}
		}
		int K = characters.size(); //Number of characters in this alphabet
		
		//Next in input comes K lines, which makes up a [k x k] matrix
		//Each integer in the matrix is the GAIN for aligning these characters
		gains = new int[K][K];
		for(int i = 0; i < K; i++) {
			for(int j = 0; j < K; j++) {
				gains[i][j] = scan.nextInt();
			}
		}
		
		//Next in input: One line with the integer Q. This is the number of upcoming queries
		Q = scan.nextInt();
	}
	
	private void alignStrings(Scanner scan) {
		//Search for MAXIMAL gain of aligning the two strings read from the input
		String string1 = scan.next();
		String string2 = scan.next();
		int length1 = string1.length();
		int length2 = string2.length();
		
		//Create a matrix with the values opt(x,y) on the place [x][y]
		optMatrix = new int[length1+1][length2+1];
		
		for(int i = 0; i < length1+1; i++) {
			for(int j = 0; j < length2+1; j++) {
				optMatrix[i][j] = opt(i, j, string1, string2);
			}
		}
		
		//Now the maxgain is stored in optMatrix[length1][length2] (lower right corner of matrix)
		//Need to backtrack to know how we got there.
		//For each character in the strings, starting from right, decide which way we came from in the matrix
		//If we have added one more character to any of the strings, insert * to the string
		printAlignedStrings(string1, string2);
	}
	
	private int opt(int i, int j, String string1, String string2) {	
		if(i == 0) { //if the first index is 0, the first string is emptied
			return j * delta;
		} else if(j == 0) {
			return i * delta;
		} else {
			//Vi vill ha värdet i gains som linkar bokstäverna i string1[i] och string2[j]
			int swapGain = gains[letterIndices.get(string1.charAt(i-1))]
					            [letterIndices.get(string2.charAt(j-1))]
					       + optMatrix[i-1][j-1];
			
			//If this one is used in max-solution, will be inserted star in resultString1 at index i
			int addIGain = delta + optMatrix[i][j-1];
			
			//If this one is used in max-solution, will be inserted star in resultString2 at index j
			int addJGain = delta + optMatrix[i-1][j];
			
		    return Math.max(swapGain, Math.max(addIGain, addJGain));
		}
	}
	
	private void printAlignedStrings(String string1, String string2) {
		StringBuilder resultString1 = new StringBuilder();
		StringBuilder resultString2 = new StringBuilder();
	    int i = string1.length();
	    int j = string2.length();
	    while(i >= 0 || j >= 0) {
	    	int currentValue = optMatrixGet(i, j);
	    	int leftValue = optMatrixGet(i, j-1);
	    	int upValue = optMatrixGet(i-1, j);
	    	int upDiagonalValue = optMatrixGet(i-1, j-1);
	    	
	    	int fromLeft = delta + leftValue;
	    	int fromUp = delta + upValue;
	    	int fromDiagonal = Integer.MIN_VALUE;
	    	if(j > 0 && i > 0) {
	    		fromDiagonal = gains[letterIndices.get(string1.charAt(i-1))]
                                    [letterIndices.get(string2.charAt(j-1))] + upDiagonalValue;
	    	}
	    	
	    	if(currentValue == fromDiagonal) {
	    		//If we came from the diagonal, just keep the characters in both strings as they are
		    	//(((Why is this the preferred way, so that it has to be first among the if-statements!!???)
	    		if(i > 0) {
	    	        resultString1.append(string1.charAt(i-1));
	    		}
	    		if(j > 0) {
	    		    resultString2.append(string2.charAt(j-1));
	    		}
	    		i--;
	    		j--;
	    	} else if(currentValue == fromLeft) {
		    	//If came from left, insert * in string1
	    		resultString1.append('*');
    		    resultString2.append(string2.charAt(j-1));
	    		j--;
	    	} else if(currentValue == fromUp) {
	    		//If came from up, insert * in string2
	    		resultString2.append('*');
    	        resultString1.append(string1.charAt(i-1));
	    		i--;
	    	} else {
	    		//If we now are in the upper left corner (??)
	    		j--;
	    		i--;
	    	}
	    	
	    }
	    System.out.println(resultString1.reverse().toString() + " " + resultString2.reverse().toString());
	}
	
	private int optMatrixGet(int i, int j) {
		if(i < 0 || j < 0) {
			return Integer.MIN_VALUE;
		} else {
			return optMatrix[i][j];
		}
	}
	
	private void printTimeResult() {
		System.out.println("Parse time (ms): " + parseTime);
		System.out.println("Time for all queries (ms): " + queriesTime);
	}
}
