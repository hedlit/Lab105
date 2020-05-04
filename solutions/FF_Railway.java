package solutions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/** FIX:
 * -do Goldberg-Tarjan-version (neat) + write more in report about this
 * -upload everything to git
 * 
 * -make it quicker!!!!!! is it even the right time complexity??
 * -take time and write report better
 * */


/** 
 * NOTE: I had a very hard time understanding the effects of the graph being undirected (and how to use a residual graph anyways).
 * I thought the flow of (from->to) and (to->from) should added together equals the given capacity.
 * So that students could travel both ways at the same edge.
 * But as it seems to work for this program (???) only only way can be used at the same time, either to->from or from->to.
 * Search for "residualCapacities"-matrix and see the confused comments whenever it is used.
 * 
 * Q&A:
 * 
 * What is the time complexity, and more importantly why?
 * O(K * C * m).
 * See report.
 * 
 * Which other (well-known) algorithmic problems can be solved using Network-Flow?
 * Scheduling with bipartite graph matching (for example workers and jobs).
 * 
 * If the capacities of the edges are very large, how can one get a different (better) time complexity?
 * First, only chose edges with capacity > 2^k, where k is the smallest int s.t. 2^k >= C.
 * When no more paths exists where all the edges satisfy this, halve this number and search again. Etc.
 * It's in order to take "larger" steps and not decrease C with one every time (and then get exactly C bfs-searches).
 *
 */

public class FF_Railway {
	private long algorithmTime;
	private long parseTime;
	
	private int N; //Number of nodes
	private int M; //Number of edges
	private int C; //Number of students to transport per day from start to stop. Final max flow result must be C or bigger!
	private int P; //Number of routes in the plan, i.e. the roots encouraged to be removed. 0 < P < M
	int source;
	int sink;
	int minDelta;
	
	int cLower; //x = smallest two-potence that is >= C.

	//Capacity-matrix. capacaties[u][v] = capacaties[v][u], since undirected graph
	int[][] capacities;
	
    //flow matrix. Stores the flow between u -> v
	int[][] flows;
	
	//The capacities in the residual graph.
	//DIRECTED edges, i.e. [u][v] and [v][u] will not always give same value
	int[][] residualCapacities;
	
	//Storing all connections, on the index u has a list of all neighbor-indices to the node u
	LinkedList<Integer>[] neighbors;
	
	//On index i stores the two integers u and v for the node (u<->v) with the index i (appeared as i:th in the input)
	LinkedList<Integer>[] indicedEdges;
	
	
	Queue<Integer> indicesToRemove = new LinkedList<Integer>();

	
	public static void main(String[] args) {
		FF_Railway fr = new FF_Railway();
		
		long start = System.currentTimeMillis();
	    fr.parse();
		long stop = System.currentTimeMillis();
		
		fr.parseTime = stop - start;
		
		//Computes and prints out "x f" where x = number of routes to remove, f = maxflow after removing these routes
		long start2 = System.currentTimeMillis();
	    fr.algorithm();
		long stop2 = System.currentTimeMillis();
		fr.algorithmTime = stop2 - start2;
	    
	    fr.printTimeResult();
	}
	
	private void parse() {
		Scanner scan = new Scanner(System.in);
		N = scan.nextInt();
		M = scan.nextInt();
		C = scan.nextInt();
		P = scan.nextInt();
		
		int k = 0;
		while(cLower < C) {
			cLower = (int) Math.pow(2, k);
			k++;
		}
		
		source = 0;
		sink = N-1;
		capacities = new int[N][N];
		flows = new int[N][N];
		residualCapacities = new int[N][N];
		neighbors = new LinkedList[N];
		indicedEdges = new LinkedList[M];
		
		for(int i = 0; i < N; i++) {
			neighbors[i] = new LinkedList<Integer>();
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
			
			capacities[u][v] = c;
			capacities[v][u] = c;
			neighbors[u].add(v);
			neighbors[v].add(u);
			indicedEdges[i].add(u);
			indicedEdges[i].add(v);
		}
		
		//P lines with one integer each, where the i-th
		//line contains the index of the i-th route to be removed.
		for(int i = 0; i < P; i++) {
			indicesToRemove.add(scan.nextInt());
		}
		
		scan.close();
	}
	
	private void algorithm() {
		
		//In the order given, remove one edge after the other.
		//The max flow must still be greater than or equal to C.
		//When no more nodes to remove or max flow gets too small, then terminate
		
		int maxFlow = fordFulkersonMaxFlow();
		int numEdgesRemoved = 0;
		
		while(!indicesToRemove.isEmpty()) {
			// Remove the next edge in P from the graph
			int toRemove = indicesToRemove.poll();
			int node = indicedEdges[toRemove].get(0);
			int node2 = indicedEdges[toRemove].get(1);
			capacities[node][node2] = 0;
			capacities[node2][node] = 0;
			
			// Calculate max flow again with Ford Fulkerson
			int newMaxflow = fordFulkersonMaxFlow();
			
			// If max flow < C, "put back" edge and terminate
			if(newMaxflow >= C) {
				maxFlow = newMaxflow;
				numEdgesRemoved++;
			} else {
				break;
			}
		}
		System.out.println(numEdgesRemoved + " " + maxFlow);
		
	}
	
	//Calculates maxflow with ford fulkerson algorithm, returns the maxflow
	private int fordFulkersonMaxFlow() {
	    int maxFlow = 0;
	    
	    //Initiates residual graph - since undirected puts all capacities to c from beginning...??
	    for(int i = 0; i < N; i++) {
	    	for(int j = 0; j < N; j++) {
	    		residualCapacities[i][j] = capacities[i][j];
	    		flows[i][j] = 0;
	    	}
	    }
		
		while (true) {
			//Do bfs search to find a s-t path where it is possible to increase the flow on every edge
			ArrayList<Integer> path = bfs(source,sink);
			
			//If no such path exists
			if(path.isEmpty()) {
				if(cLower == 1) {
					break;
				} else {
					cLower /= 2;
				}
			}
			
			//Along the path, increase the flow with minDelta on each edge
			//Observe that the path is given in reverse order, (t->s)
			//Also update the residualCapacities!!!! - WHY IS THIS OK
			for(int i = 0; i < path.size() - 1; i++) {
				int to = path.get(i);
				int from = path.get(i+1);
				flows[from][to] += minDelta; //why doesn't it matter what happens with flows[to][from] ???
				
				//The residual graph is directed, even though the original graph is not
				//WHY NO USE OF BACKWARD EDGES????
				residualCapacities[from][to] = capacities[from][to] - flows[from][to]; //forward edge
				residualCapacities[to][from] = capacities[from][to] - flows[from][to]; //forward edge
				// THIS IS WHAT I THOUGHT SHOULD BE USED (it works to use that too, but not needed): residualCapacities[to][from] = flows[from][to]; //backwards edge
			}
			
			maxFlow += minDelta;
		}
		return maxFlow;
	}
	
	//Returns a path (to -> from) along the residual graph with no cycles.
	//All edges on the path has flow < capacity in the direction (from -> to)
	//I.e. all must have residual capacities > 0
	//(((Sets "delta" on all the edges on the path as (capacity-flow))))
	//Returns empty list if no path exists.
	//Also calculates and updates "minDelta", storing the minimum (capacity-flow)
	private ArrayList<Integer> bfs(int from, int to) {
		ArrayList<Integer> path = new ArrayList<>();
		
		boolean[] visited = new boolean[N];
		int[] pred = new int[N]; //predecessors
		int[] deltas = new int[N]; //delta for all visited nodes
		
		Queue<Integer> queue = new LinkedList<Integer>();
		queue.add(from);
		visited[from] = true;
		deltas[from] = Integer.MAX_VALUE;
		
		while(!queue.isEmpty()) {
			int currentNode = queue.poll();
			for(int neighbor : neighbors[currentNode]) {
				//Only chose paths that have residualCapacity >= cLower
				if(!visited[neighbor] && residualCapacities[currentNode][neighbor] >= cLower) { //also checks if it is a real neighbor in the RESIDUAL graph!!
					visited[neighbor] = true;
					queue.add(neighbor);
					pred[neighbor] = currentNode;
					deltas[neighbor] = Math.min(deltas[currentNode], residualCapacities[currentNode][neighbor]);
					if(neighbor == to) {
						//Found a path!
						path.add(to);
						int c = to;
						while(c != from) {
							path.add(pred[c]);
							c = pred[c];
						}
						minDelta = deltas[to];
						return path;
					}
				}
			}			
		}
		
		//No path found, the list will be empty
		return path;
	}
	
	
	private void printTimeResult() {
		System.out.println("Parse time (ms): " + parseTime);
		System.out.println("Algorithm time (ms): " + algorithmTime);
	}	
	
}
