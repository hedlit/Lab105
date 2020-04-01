package solutions;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Scanner;

/*
 * 
 * Why does your algorithm obtain a stable solution?
 * The slaves must answer yes when bought -> The matching is perfect (everybody gets matched).
 * The masters buy first the slave they like most, and the slave can only say no (or regret afterwards) if they like some other master better.
 * Thus no pair can exist where both the master and the slave want to break to another valid pair - then they would already have broken up.
 * 
 * Could there be other stable solutions? Do you have an example/proof of uniqueness?
 * Yes, could be others. But this is the only one where all the masters get its best "possible" (valid) slave choice.
 * Switch the roles of slaves and masters. If not the same, then not unique.
 * While algorithm proceeds, masters get "worse" and "worse" slaves, and slaves get "better" masters,
 * thus GS-algorithm produces the unique matching where masters get their best choice and slaves their worst (of the valid ones).
 * 
 * Is this (the algorithm) how matching is performed in real life? If not, what flaws does it have?
 * Clearly one part has the final say. Possible for example companies and employees.
 * Also, the slaves have to answer yes to their master, they have no say in getting bought or not.
 * 
 * 
 * */



/**
 * This implementation of the GS-algorithm uses "masters" and "slaves",
 * instead of "men" and "women".
 * The purpose for this is to illustrate which one of the groups preferences
 * are considered most important.
 * Though some consideration is still taken for the preferences of the slaves
 * (not common in slave trade...), so that's nice.
 * 
 */

public class StableMarriage {
	
	//Used for answering questions to the report
	private long algorithmTime;
	private long parseTime;
	
	
	// Hashmap with arrays: the slaves' preferences.
	private HashMap<Integer, Integer[]> slavesPref = new HashMap<>();
	
	// Hashmap with stacks: the masters' preferences.
	private HashMap<Integer, ArrayDeque<Integer>> mastersPref = new HashMap<>();
	
	// Stack with the masters that still have to choose a slave
	private Deque<Integer> masters = new ArrayDeque<>();
	
	//The matched pairs (key=slave, value=master)
	private HashMap<Integer, Integer> matches = new HashMap<>();

	
	public static void main(String[] args) {
		StableMarriage sm = new StableMarriage();
		sm.run();
	}

	
	/**
	 * The algorithm contains one while loop with constant O(1) complexity.
	 * In worst case each master has to try to buy N slaves
	 * => Time complexity O(N^2)
	 * */
	private void run() {
		
		
		long start = System.currentTimeMillis();
		// Parse input from standard in
		parseInputStdin();
		long stop = System.currentTimeMillis();
		parseTime = stop - start;

		
		start = System.currentTimeMillis();
		
		while (!masters.isEmpty()) {
			
			int master = masters.pop();
			
			//the slave the master prefers and has not yet tried to buy:
			int slave = mastersPref.get(master).pop();
			
			//This slaves current match:
			Integer currentMaster = matches.get(slave);
			//This slaves preferences:
			Integer[] slavePref = slavesPref.get(slave);
			
			if(currentMaster == null) {
				matches.put(slave, master);
			} else if(slavePref[master - 1] < slavePref[currentMaster - 1]) {
				matches.put(slave, master); //Change the match
				masters.push(currentMaster); //Earlier master has to buy a new slave
			} else {
				masters.push(master); //Master has to buy another slave
				//Question - does it matter if I add the master to the beginning
				//to the masters queue again?
			}
		}
		
		stop = System.currentTimeMillis();
		algorithmTime = stop - start;

		// Print result to standard out
		printResult();
	}
	
	/*
	 * Input:
	 * First one integer N, number of pairs.
	 * 2:
	 * Preference lists: Total 2N list entries with N + 1 integers per entry
	 * One list entry: First index of current slave/master, then its preferences.
	 * 
	 * This method has one for loop with 2N iterations,
	 * containing one for loop with N iterations
	 * each loop containing only O(1)-operations
	 * --> The time complexity is O(n^2)
	 */
	private void parseInputStdin() {
		Scanner scan = new Scanner(System.in);
		int N = Integer.parseInt(scan.next()); // Number of pairs

		while (masters.size() < N) {
			int prefHolder = Integer.parseInt(scan.next());

			// The slaves come first in the input-lists
			if (!slavesPref.containsKey(prefHolder)) {
				int slave = prefHolder;
				Integer[] slavePref = new Integer[N];

				// Each slaves has a preference list of N entries
				// The preference lists of the slaves are inverted!
				// slavePref[master] = index_of_master_in_preferencelist
				for (int i = 0; i < N; i++) {
					int master = Integer.parseInt(scan.next());
					slavePref[master - 1] = i + 1;
				}
				slavesPref.put(slave, slavePref);

			} else {
				// The masters preferences
				int master = prefHolder;
				ArrayDeque<Integer> masterPref = new ArrayDeque<>();

				// The preference lists of the masters are not inverted!
				// mastersPref[index] = slave_on_this_index
				for (int i = 0; i < N; i++) {
					masterPref.addLast(Integer.parseInt(scan.next()));
				}
				mastersPref.put(master, masterPref);

				// masters = a stack containing integers 1..N
				masters.push(master);
			}
		}
		scan.close();
	}
	
	private void printResult() {
		for(int i = 1; i <= matches.size(); i++) {
			System.out.println(matches.get(i));
		}
		
		
		//System.out.println("Algorithm time: " + algorithmTime + " and parse time: " + parseTime + " (milliseconds)");
	}
}
