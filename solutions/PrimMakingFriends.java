package solutions;

import java.util.HashMap;
import java.util.Scanner;

/** FIX:
 * - Read through my report and try to understand the time complexity.
 * */


/** Implementation using Prim's algorithm,
 * Finding a minimal spanning tree, i.e. a subset of edges so that ALL nodes are connected,
 * with minimal total cost of edges.
 * 
 * 
 * QA:
 * 
 * Why does the algorithm you have implemented produce a minimal spanning tree?
 * They only choose so called "safe edges", i.e. edges that together with the already chosen edges,
 * are guaranteed to be able to make up a MST.
 * Safe edge: (u,v) where u is inside the beginning to a MST and v is not, (u,v) has minimal weight of all these edges.
 * 
 * What is the time complexity, and more importantly why?
 * O(mlogn). See report.
 * 
 * 
 * What happens if one of the edges you have chosen to include collapses? Might there be any problems with that in real applications?
 * One or more nodes are "cut off" completely from the tree,
 * while if we had included all the edges (not MST) then maybe there would be other ways to reach the nodes.
 * May happen for example with electrical wires.
 * 
 * Can you think of any real applications of MST? What would the requirements
 * of a problem need to be in order for us to want MST as a solution?
 * Electrical wires. Trains. Lots more.
 * Cannot be directed edges? Also connections with "middle hands" must work just as well as a direct connection.
 * 
 * */
public class PrimMakingFriends {
	//Used for answering time questions to the report
	private long spanningTreeTime;
	private long parseTime;
	
	private int N; //number of people (nodes)
	private int M; //number of edges between people
	
	private PrimMakingFriends(int N, int M) {
		this.N = N;
		this.M = M;
	}
	
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		int N = scan.nextInt();
		int M = scan.nextInt();
		
		PrimMakingFriends mf = new PrimMakingFriends(N, M);
		
		long start = System.currentTimeMillis();
	    PrimNode[] initialArray = mf.parseToArray(scan);
		long stop = System.currentTimeMillis();
		
	    scan.close();
		mf.parseTime = stop - start;
		
		long start2 = System.currentTimeMillis();
		//Finds minimum spanning tree and prints out the minimum cost
	    mf.prims(initialArray[0]);
		long stop2 = System.currentTimeMillis();
		mf.spanningTreeTime = stop2 - start2;
	    
	    //mf.printTimeResult();
	}
	
	
	//Reads all the input lines, turns them into PrimNodes with neighbors,
	//and stores all the nodes in an initial array for easy handling
	private PrimNode[] parseToArray(Scanner scan) {
		//Tillfällig vektor för att enkelt hantera noderna
		PrimNode[] allNodes = new PrimNode[N];
		
		//Go through and add nodes to all places
		//I tested to do this "on the fly" in the next for loop, but this is for some reason not slower
		for(int i = 0; i < N; i++) {
			allNodes[i] = new PrimNode(i + 1);
		}
		
		int personIndex1, personIndex2, weight;
		PrimNode node1, node2;
		for(int i = 0; i < M; i++) {
			personIndex1 = scan.nextInt();
			personIndex2 = scan.nextInt();
			weight = scan.nextInt();
			
			//Kanten är inte riktad, uppdatera alltså båda nodernas grannar
			//Noden med index i finns i allNodes på plats (i - 1)
			node1 = allNodes[personIndex1 - 1];
			node2 = allNodes[personIndex2 - 1];		
			
			node1.addConnection(node2, weight);
			node2.addConnection(node1, weight);
			
		}
		return(allNodes);
	}
	
	private void prims(PrimNode root) {
		//Kostnaden hittills, den sammanlagda vikten av alla edges i MST
		int cost = 0;
		
		root.insideMST = true;
		
		//Prioritetskö Q = nodesToChoseFrom. Innehåller alla noder att välja mellan att inkludera näst i MST.
		//Lägg från början bara till de med någon koppling till root
		BinaryHeapQueue<PrimNode> nodesToChoseFrom = new BinaryHeapQueue<PrimNode>();
		for(PrimNode rootNeighbor : root.neighbors.keySet()) {
			rootNeighbor.changeKey(rootNeighbor.neighbors.get(root));
			nodesToChoseFrom.add(rootNeighbor);
		}
		
		//N iterations (one for each node)
		while (!nodesToChoseFrom.isEmpty()) {
			
			PrimNode nextNodeToInclude = nodesToChoseFrom.poll();
			nextNodeToInclude.insideMST = true;			
			cost += nextNodeToInclude.key;
			
			//Eftersom en nod (nextNodeToInclude) nu har bytt plats,
			//måste varje granne till nextNodeToInclude (som inte redan är i MST),
			//kolla om dess avstånd till nextNodeToInclude är mindre än dess nyckel.
			//Om så är fallet ska nyckeln ändras till avståndet till nextNodeToInclude
			//Alla grannar till nextNodeToInclude (som ej är inom MST) måste också adderas till kön
			for(PrimNode neighbor : nextNodeToInclude.neighbors.keySet()) {
				int distanceToMovedNode = neighbor.neighbors.get(nextNodeToInclude);
				if(!neighbor.insideMST && neighbor.key >= distanceToMovedNode) {
					//If key-updating is needed. This will also be true if the neighbor is not yet in queue,
				    //because then the key will be Integer.MAX_VALUE					
					
				    nodesToChoseFrom.remove(neighbor);
					neighbor.changeKey(distanceToMovedNode);
					nodesToChoseFrom.add(neighbor);
					
					//This should optimally be "decreaseKey"!!:
					//nodesToChoseFrom.decreaseKey(neighbor, neighbor.key);
					
				}
			}
		}
		
		System.out.println(cost);
	}
	
	private void printTimeResult() {
		System.out.println("Parse time (ms): " + parseTime);
		System.out.println("Build the minimum spanning tree (ms): " + spanningTreeTime);
	}
	
	
	//För en PrimNode:
	  //Nycklarna för noden är avståndet till noder inuti MST,
	  //från början har alla noder nyckeln Integer.MAX_KEY
	  //En hashMap som lagrar alla grannar, neighbors. I HashMap:en är nycklarna grannnoderna,
	       //värdena är kostnaden på kanten till denna granne.
	private static class PrimNode implements Comparable<PrimNode> {
		private int key;
		private int index;
		private HashMap<PrimNode, Integer> neighbors;
		private boolean insideMST;
		
		private PrimNode(int index) {
			this.index = index;
			key = Integer.MAX_VALUE;
			insideMST = false;
			neighbors = new HashMap<PrimNode, Integer>();
		}
		
		private void addConnection(PrimNode neighbor, int edgeWeight) {
			neighbors.put(neighbor, edgeWeight);
		}
		
		//The key should be the minimum distance to nodes inside the MST
		private void changeKey(int newKey) {
			this.key = newKey;
		}
		
		@Override
		public int compareTo(PrimNode p) {
			return this.key - p.key;
		}
		
	}
}
