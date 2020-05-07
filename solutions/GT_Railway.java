package solutions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;


public class GT_Railway {
	private long algorithmTime;
	private long parseTime;
	
	private int N; //Number of nodes
	private int M; //Number of edges
	private int C; //Number of students to transport per day from start to stop. Final max flow result must be C or bigger!
	private int P; //Number of routes in the plan, i.e. the roots encouraged to be removed. 0 < P < M
	int source;
	int sink;

	//Capacity-matrix. capacaties[u][v] = capacaties[v][u], since undirected graph
	//Different names for those that always stay their initial value and those who change when removing edges
	int[][] changedCapacities;
	int[][] constantCapacities; //When removing routes
	
    //flow matrix. Stores the flow between u -> v and v -> u.
	int[][] flows;
	
	//The capacities in the residual graph.
	//DIRECTED edges, i.e. [u][v] and [v][u] will not always give same value
	int[][] residualCapacities;
	
	//Storing all connections, on the index u has a list of all neighbor-indices to the node u
	LinkedList<Integer>[] neighbors;
	
	//On index i stores the two integers u and v for the node (u<->v) with the index i (appeared as i:th in the input)
	LinkedList<Integer>[] indicedEdges;
	
	//Current heights of all the nodes
	int[] heights;
	
	//Current excess preflow in all the nodes, i.e. flow in minus flow out.
	int[] excessFlows;
	
	Queue<Integer> indicesToRemove = new LinkedList<Integer>();
	
	List<Integer> indicesToRemove2 = new ArrayList<Integer>();
	

	
	//All nodes that currently have excessFlow > 0
	HashSet<Integer> excessNodes;

	
	public static void main(String[] args) {
		GT_Railway gt = new GT_Railway();
		
		long start = System.currentTimeMillis();
	    gt.parse();
		long stop = System.currentTimeMillis();
		
		gt.parseTime = stop - start;
		
		//Computes and prints out "x f" where x = number of routes to remove, f = maxflow after removing these routes
		long start2 = System.currentTimeMillis();
	    //gt.algorithm();
	    gt.binaryAlgorithm();
		long stop2 = System.currentTimeMillis();
		gt.algorithmTime = stop2 - start2;
	    
	    gt.printTimeResult();
	}
	
	private void parse() {
		Scanner scan = new Scanner(System.in);
		N = scan.nextInt();
		M = scan.nextInt();
		C = scan.nextInt();
		P = scan.nextInt();
		
		source = 0;
		sink = N-1;
		changedCapacities = new int[N][N];
		constantCapacities = new int[N][N];
		flows = new int[N][N];
		residualCapacities = new int[N][N];
		neighbors = new LinkedList[N];
		indicedEdges = new LinkedList[M];
		heights = new int[N];
		excessFlows = new int[N];
		
		for(int i = 0; i < N; i++) {
			neighbors[i] = new LinkedList<Integer>();
			heights[i] = 0;
		}
		for(int i = 0; i < M; i++) {
			indicedEdges[i] = new LinkedList<Integer>();
		}

		//M lines with (u, v, c), describing capacity of undirected edge (u <-> v)
		//c is the total capacity for BOTH back and forth on this route (??????)
		int u, v, c;
		for(int i = 0; i < M; i++) {
			u = scan.nextInt();
			v = scan.nextInt();
			c = scan.nextInt();
			
			changedCapacities[u][v] = c;
			changedCapacities[v][u] = c;
			constantCapacities[u][v] = c;
			constantCapacities[v][u] = c;
			neighbors[u].add(v);
			neighbors[v].add(u);
			indicedEdges[i].add(u);
			indicedEdges[i].add(v);
		}
		
		//P lines with one integer each, where the i-th
		//line contains the index of the i-th route to be removed.
		for(int i = 0; i < P; i++) {
			int index = scan.nextInt();
			indicesToRemove.add(index);
			indicesToRemove2.add(index);
		}
		
		scan.close();
	}
	

    private void binaryAlgorithm() {
    	binaryAlgorithm(0, indicesToRemove.size() - 1, Integer.MAX_VALUE, 0); 
    }
    

    //Do binary search for how many edges we can remove of the P number of edges given
  	//First, try to remove half of them. If okay, remove next half etc. If not okay, remove only half of the half etc.
	private void binaryAlgorithm(int left, int right, int maxFlow, int numRemovedEdges) {
		int mid = left + (right - left) / 2; 
		
		//Base case
		//If we've already found and removed the maximum amount of edges
		if(right < left) {
			if(maxFlow < C) { //must put back the lastly removed node, the only one left, it shouldn't have been removed
				int toPutBack = indicesToRemove2.get(mid);
				int node = indicedEdges[toPutBack].get(0);
				int node2 = indicedEdges[toPutBack].get(1);
				changedCapacities[node][node2] = constantCapacities[node][node2];
				changedCapacities[node2][node] = constantCapacities[node][node2];
				numRemovedEdges--;
				maxFlow = goldbergTarjanMaxflow(); //will be greater or equal to maxflow
			}
			outputPrint(numRemovedEdges, maxFlow);
			return;
		}   
		
		//Recursive case
		//If the current maxFlow < C, then we must put back half of the previously removed edges (the rightmost of them)
		if(maxFlow < C) {
			int lower = mid;
			int upper = right;
			if(right < indicesToRemove.size()) {
				lower = mid+1;
				upper = right+1;
			}
			for(int i = lower; i <= upper; i++) {
		    	int toPutBack = indicesToRemove2.get(i);
				int node = indicedEdges[toPutBack].get(0);
				int node2 = indicedEdges[toPutBack].get(1);
				changedCapacities[node][node2] = constantCapacities[node][node2];
				changedCapacities[node2][node] = constantCapacities[node][node2];
				numRemovedEdges--;
		    }
		} else { //Otherwise, test to remove the leftmost half of the edges
			for(int i = left; i <= mid; i++) {
		    	int toRemove = indicesToRemove2.get(i);
				int node = indicedEdges[toRemove].get(0);
				int node2 = indicedEdges[toRemove].get(1);
				changedCapacities[node][node2] = 0;
				changedCapacities[node2][node] = 0;
				numRemovedEdges++;
		    }
		}
				
		//Calculate the new sflow
	    int newMaxFlow = goldbergTarjanMaxflow();
	    
	    //If this works, i.e. maxflox >= C, then continue to test on UPPER half
	    if(newMaxFlow >= C) {
	    	binaryAlgorithm(mid+1, right, newMaxFlow, numRemovedEdges);
	    } else { //If it doesn't work, then try again on LOWER half
	    	binaryAlgorithm(left, mid-1, newMaxFlow, numRemovedEdges);
	    }
		
	}
    
    	
	private void outputPrint(int numEdgesRemoved, int maxFlow) {
		System.out.println(numEdgesRemoved + " " + maxFlow);
	}
	
	
	private void algorithm() {
		
		//In the order given, remove one edge after the other.
		//The max flow must still be greater than or equal to C.
		//When no more nodes to remove or max flow gets too small, then terminate
		
		int maxFlow = goldbergTarjanMaxflow();
		int numEdgesRemoved = 0;
		
		while(!indicesToRemove.isEmpty()) {
			// Remove the next edge in P from the graph
			int toRemove = indicesToRemove.poll();
			int node = indicedEdges[toRemove].get(0);
			int node2 = indicedEdges[toRemove].get(1);
			changedCapacities[node][node2] = 0;
			changedCapacities[node2][node] = 0;
			
			// Calculate max flow again
			int newMaxflow = goldbergTarjanMaxflow();
		    
			// If max flow < C, "put back" edge and terminate
			if(newMaxflow >= C) {
				maxFlow = newMaxflow;
				numEdgesRemoved++;
			} else {
				break;
			}
		}
		outputPrint(numEdgesRemoved, maxFlow);
	}
	
	//Calculates maxflow with goldberg tarjan algorithm (preflow push), returns the maxflow
	private int goldbergTarjanMaxflow() {	    
	    //Init
	    excessNodes = new HashSet<>();
	    
	    for(int i = 0; i < N; i++) {
	    	excessFlows[i] = 0;
	    	heights[i] = 0;
	    	for(int j = 0; j < N; j++) {
	    		residualCapacities[i][j] = changedCapacities[i][j];
	    		flows[i][j] = 0;
	    	}
	    }
	    heights[source] = N;
	    
	    //Starts preflow
	    for(int neighbor : neighbors[source]) {
	    	flows[source][neighbor] = changedCapacities[source][neighbor];
	    	flows[neighbor][source] = -changedCapacities[source][neighbor];
	    	excessFlows[neighbor] = changedCapacities[source][neighbor];
	    	residualCapacities[source][neighbor] = 0;
	    	if(neighbor != sink && excessFlows[neighbor] > 0) {
	    	    excessNodes.add(neighbor);
	    	}
	    }
	    
	    
	    while(!excessNodes.isEmpty()) {
	    	int currentNode = excessNodes.iterator().next();
	    	boolean pushed = false;
	    	for(int neighbor : neighbors[currentNode]) {
	    		if(heights[currentNode] > heights[neighbor] && residualCapacities[currentNode][neighbor] > 0) {
	    			push(currentNode, neighbor);
	    			pushed = true;
	    			break;
	    		}
	    	}
	    	if(!pushed) {
	    	    relabel(currentNode);
	    	}	    		    	
	    }
	    //The sink is now the only node with any excessflow left, i.e. the preflow has turned into a real flow    
	    return excessFlows[sink]; //This is the final max flow - all the flow reaching the sink
	}
	
	
	private void push(int from, int to) {
		//Assertions
//		int e = excessFlows[from];
//		if(e <= 0) {
//			System.out.println("push assert excessflows failed");
//		}
//		if(heights[from] <= heights[to])  {
//			System.out.println("push assert neighbor height failed");
//		}
//		int resCap = residualCapacities[from][to];
//		if(resCap <= 0) {
//			System.out.println("push-assert residual capacity failed");
//		}
		
		//Obs, since undirected edges, apparently one can almost handle all edges as forward edges....??
	    int delta = Math.min(excessFlows[from], changedCapacities[from][to] - flows[from][to]);
	    flows[from][to] += delta;
	    flows[to][from] -= delta; //the flows are negatives of each others
	    excessFlows[from] -= delta;
	    excessFlows[to] += delta;
	    
	    if(to != sink && to != source && excessFlows[to] > 0) {
	    	excessNodes.add(to);
	    }
	    if(excessFlows[from] <= 0) {
	    	excessNodes.remove(from);
	    }
		    
		//Setting the remaining capacities
		residualCapacities[from][to] = changedCapacities[from][to] - flows[from][to]; //forward edge
		residualCapacities[to][from] = changedCapacities[to][from] + flows[from][to]; //backwards edge, but also its own edge
	}
	
	private void relabel(int node) {
		//Assertions
//		if(excessFlows[node] <= 0) {
//			System.out.println("relabel-assert failed");
//		}
//		for(int neighbor : neighbors[node]) {
//			if(heights[neighbor] < heights[node] && residualCapacities[node][neighbor] > 0) {
//				System.out.println("relabel-assert failed, neighbor height.");
//			}
//		}
				
		heights[node] += 1;
	}
	
	
	private void printTimeResult() {
		System.out.println("Parse time (ms): " + parseTime);
		System.out.println("Algorithm time (ms): " + algorithmTime);
	}
}