package solutions;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;


/** QA:
 * 
 * How do you represent the problem as a graph, and how is the graph built?
 * List of "Nodes", each Node with a neighbor list. Uses linked lists,
 * because only changes will be done at head/tail where time complexity is O(1).
 * 
 * If one were to perform backtracking (find the path along which we went), how would that be done?
 * "Node" needs one more attribute "Node predecessor".
 * Recursively, write out predecessor for the nodes to get the path.
 * 
 * What is the time complexity, and more importantly why?
 * Build graph: O(N^2) ???
 * All the queries: O(Q*(N+M)) ???
 * So whichever one of these that are the greatest.
 * 
 * Is it possible to solve the problem with DFS: why or why not?
 * Maybe dfs only works if you do all possible searches and then compare them???
 * Because if dfs follow a path than necessary and finds the stopnode,
 * it cannot say that is the shortest path, it must test all other paths first.
 * 
 * Can you think of any applications of this? Of BFS/DFS in general?
 * Find route for transportation of different kinds. Find connections between people (heritage or stuff like that).
 * 
 * */


public class WordLadders {
	private int N; //number of nodes
	private int Q; //number of queries
	private List<Node> nodes = new LinkedList<Node>(); //the graph on list-form
	
	//Used for answering questions to the report
	private long searchTime;
	private long buildTime;
	

	public static void main(String[] args) {
		WordLadders wl = new WordLadders();
		Scanner scan = new Scanner(System.in);
		
		long start = System.currentTimeMillis();
		//Parse input from standard in and build the graph
	    wl.buildGraph(scan);
		long stop = System.currentTimeMillis();
		wl.buildTime = stop - start;
		
		long start2 = System.currentTimeMillis();
		//Parses each query from standard in, handles them one by one
	    for(int q = 0; q < wl.Q; q++) {
		  	wl.handleQuery(scan);
	    }
		long stop2 = System.currentTimeMillis();
		wl.searchTime = stop2 - start2;
	    
	    scan.close();
	    //wl.printTimeResult();
	}

	
	/* Input is on this form:
	 * First row: Two integers, N (number of nodes) and Q (number of queries).
	 * N lines: Words, to be stored in one node each. Each word has five letters.
	 * Q lines: Each line has one query: Two space separated words.
	 * 
	 * Each word is made into a Node.
	 * 
	 * Time complexity: O(n^2) ????????????????<-----
	 * One for loop with N iterations,
	 * and one nested loop with 0,1,2,3..,(N-1) iteration
	 * */
	private void buildGraph(Scanner scan) {
		N = scan.nextInt();
		Q = scan.nextInt();
		
		for(int i = 0; i < N; i++) { //Creates N nodes
			Node node = new Node(scan.next());
			
			//Check all previously added nodes and create edges where appropriate
			for(Node u : nodes) {
				//Check for edge node -> u.
				checkNeighbor(node, u); // O(1)
				//Check for edge u -> node
				checkNeighbor(u, node);
			}
			nodes.add(node);
		}
	}
	
	
	/* For this query:
	 * 1) Read the query from standard in.
	 * 2) BFS to find shortest path.
	 * 3) Print output (shortest path) on new line, standard out.
	 * 
	 * Time complexity:
	 * O(n) (set all nodes to not visited etc) + O(n+m) (one bfs)
	 * */
	private void handleQuery(Scanner scan) {
		//Read the query:
		String startWord = scan.next();
		String stopWord = scan.next();
		Node startNode = null;
		Node stopNode = null;
		
		//Set all nodes in the list as not visited,
		//except for startNode which is visited
		//O(n)
		for(Node n : nodes) {
			if (n.word.equals(startWord)) {
				startNode = n;
				startNode.visited = true;
				startNode.level = 0;
			} else {
				n.visited = false;
				n.level = -1; //Not needed, just for debugging and clarity
			}
		}
		
		//quick fix, sorry
		if(startWord.equals(stopWord)) {
			stopNode = startNode;
		}
		
		//Find path startWord -> stopWord with BFS:
		
		//Create bfs-queue
		Queue<Node> queue = new LinkedList<Node>(); //FIFO-list
		queue.add(startNode);
		
		//Time complexity O(n+m)
		while(!queue.isEmpty()) { //Max N iterations
			Node n = queue.poll(); //Take out the first node in the queue
			for (Node neighbor : n.neighbors) { //deg(n) iterations, max M iterations
				if(neighbor.visited == false) {
					neighbor.visited = true;
					queue.add(neighbor); //Adds to the end of queue
					neighbor.level = n.level + 1;
					if(neighbor.word.equals(stopWord)) {
						//Found path!
						stopNode = neighbor;
						break;
					}
				}
			}
		}
		
		//Print path length to standard out
		if (stopNode == null) {
			System.out.println("Impossible");
		} else {
			System.out.println(stopNode.level);
		}
		
	}
	
	/* Checks if appropriate to draw edge n->u. If so, draws an edge.
	 * Draws edge IFF: All of the last four letters in n are present in u.
	 * */
	private void checkNeighbor(Node n, Node u) {
		int[] uLetters = u.word.chars().toArray();
		int[] nLetters = n.word.chars().toArray();
		for(int l = 1; l < 5; l++) { //check each of the last 4 letters in n
			int nLetter = nLetters[l];
			boolean checked = false;
			for(int j = 0; j < uLetters.length; j++) { //try to match with any letter in u
				int uLetter = uLetters[j];
				if(uLetter == nLetter && !checked) {
					checked = true;
					uLetters[j] = -1;
				}
			}
			if(!checked) {
				return;
			}
		}
		n.addNeighbor(u);
	}
	
	private void printTimeResult() {
		System.out.println("Build graph time (ms): " + buildTime);
		System.out.println("Answering all the queries (ms): " + searchTime);
	}
	
	/* A node in a directed graph.
	 * */
	private class Node {
		private String word;
		private List<Node> neighbors;
		private boolean visited;
		private int level;
		
		private Node(String word) {
			this.word = word;
			neighbors = new LinkedList<Node>();
			visited = false;
		}
		
		private void addNeighbor(Node n) {
			neighbors.add(n); //Adds neighbor to END of neighbor list
		}
	}	
}