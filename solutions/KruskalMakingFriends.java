package solutions;

import java.util.PriorityQueue;
import java.util.Scanner;


public class KruskalMakingFriends {
	//Used for answering time questions to the report
	private long spanningTreeTime;
	private long buildTime;
	
	private int N; //number of people (nodes)
	private int M; //number of edges between people
	
	//List of all edges remaining, not in tree, sorted on their edge-cost
	private PriorityQueue<Edge> remainingEdges = new PriorityQueue<Edge>();
	
	//Array of all nodes, for implementing union-find
	//On each index i is stored the Node with index (i) (index 0 is empty).
	private Node[] allNodes;
	
	
	private KruskalMakingFriends(int N, int M) {
		this.N = N;
		this.M = M;
		allNodes = new Node[N+1];
	}
	
	
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		int N = scan.nextInt();
		int M = scan.nextInt();
		
		KruskalMakingFriends mf = new KruskalMakingFriends(N, M);
		
		long start = System.currentTimeMillis();
		//Parse input from standard in and build the tree
	    mf.parse(scan);
	    scan.close();
		long stop = System.currentTimeMillis();
		mf.buildTime = stop - start;
		
		long start2 = System.currentTimeMillis();
		//Finds minimum spanning tree and prints out the minimum cost
	    mf.kruskals();
		long stop2 = System.currentTimeMillis();
		mf.spanningTreeTime = stop2 - start2;
	    
	    mf.printTimeResult();
	}
	
	private void parse(Scanner scan) {
		//Initialize node-list
		for(int i = 1; i < N+1; i++) {
			allNodes[i] = new Node(i);
		}
		
		//first scans node1-index, then node2-index, then the weight
		for(int i = 0; i < M; i++) {
			remainingEdges.add(new Edge(scan.nextInt(), scan.nextInt(), scan.nextInt()));
		}
	}
	
	private void kruskals() {
		int cost = 0; //the cost of all the edge-weights inside MST
	    while(!remainingEdges.isEmpty()) {
	    	//chose a remaining edge with minimal weight (the first in remainingEdges)
			Edge chosenEdge = remainingEdges.poll();
			//check if chosenEdge creates a cycle (i.e. the two connected nodes are in same set)
			if(find(chosenEdge.node1) != find(chosenEdge.node2)) {
				//add chosenEdge to MST
				cost += chosenEdge.cost;
                union(chosenEdge.node1, chosenEdge.node2);
			}
		}
		System.out.println(cost);
	}
	
	
	//Returns the upmost parent index for this node
	private int find(int node) {
		int parentIndex = node;
		while(allNodes[parentIndex].parent != -1) {
			parentIndex = allNodes[parentIndex].parent; //Travel upwards until reached upmost parent
		}
		while(allNodes[node].parent != -1) {
			//fixes so that all parents to node gets the upmost parent as parent
			int nodeAbove = allNodes[node].parent;
			allNodes[node].parent = parentIndex;
			node = nodeAbove;
		}
		return parentIndex;	
	}
	
	//Joins the set holding node1 with the set holding node2
	private void union(int node1, int node2) {
		node1 = find(node1);
		node2 = find(node2);
		if(allNodes[node1].size < allNodes[node2].size) {
			allNodes[node1].parent = node2;
			allNodes[node2].size += allNodes[node1].size;
		} else {
			allNodes[node2].parent = node1;
			allNodes[node1].size += allNodes[node2].size;
		}	
	}
	
	private void printTimeResult() {
		System.out.println("Build tree time (ms): " + buildTime);
		System.out.println("Fix the minimum spanning tree (ms): " + spanningTreeTime);
	}
	
	private static class Edge implements Comparable<Edge> {
		private int node1;
		private int node2;
		private int cost;
		
		private Edge(int node1, int node2, int cost) {
			this.node1 = node1;
			this.node2 = node2;
			this.cost = cost;
		}
		
		@Override
		public int compareTo(Edge e) {
			return this.cost - e.cost;
		}
		
		@Override
		public String toString() {
			return node1 + " - " + node2 + ", cost: " + cost;
		}
	}
	
	private static class Node {
		private int parent;
		private int size;
		
		private Node(int index) {
			this.parent = -1; //initially doesn't have any parent
			this.size = 1; //originally has size 1, because is in a set with itself
		}
	}

}
