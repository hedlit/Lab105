
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;


public class GoldbergTarjan {
    int N, M, C, P, s, t; 
	HashSet<Integer> excess;
    LinkedList<Integer> toremove = new LinkedList<Integer>();
    LinkedList<Integer> toremoveplaceholder = new LinkedList<Integer>();
    LinkedList<Integer>[] nodeNeighbours, bEdges;
    int[] heights, excessflow;
    int[][] givenCapacities, residualCapacities, editedCapacities, flows;

	
	public static void main(String[] args) {
		GoldbergTarjan gt = new GoldbergTarjan();
	    gt.parse();
	    gt.reP_Essie();
	}
	
	private void parse() {
		Scanner scanner = new Scanner(System.in);
		N = scanner.nextInt();
		M = scanner.nextInt();
		C = scanner.nextInt();
		P = scanner.nextInt();
		
		s = 0;
		t = N-1;
		editedCapacities = new int[N][N];
		givenCapacities = new int[N][N];
		flows = new int[N][N];
		residualCapacities = new int[N][N];
		nodeNeighbours = new LinkedList[N];
		bEdges = new LinkedList[M];
		heights = new int[N];
		excessflow = new int[N];
		
		for(int i = 0; i < N; i++) {
			nodeNeighbours[i] = new LinkedList<Integer>();
			heights[i] = 0;
		}
		for(int i = 0; i < M; i++) {
			bEdges[i] = new LinkedList<Integer>();
        }
        
		int ui, vi, ci;
		for(int i = 0; i < M; i++) {
			ui = scanner.nextInt();
			vi = scanner.nextInt();
            ci = scanner.nextInt();
            
            nodeNeighbours[ui].add(vi);
			nodeNeighbours[vi].add(ui);
			bEdges[i].add(ui);
			bEdges[i].add(vi);
			editedCapacities[ui][vi] = ci;
			editedCapacities[vi][ui] = ci;
			givenCapacities[ui][vi] = ci;
			givenCapacities[vi][ui] = ci;
		}
		for(int i = 0; i < P; i++) {
			int index = scanner.nextInt();
			toremove.add(index);
			toremoveplaceholder.add(index);
		}
		
		scanner.close();
	}
	

    private void reP_Essie() {
    	reP_Essie(0, toremove.size() - 1, Integer.MAX_VALUE, 0); 
    }
    
	private void reP_Essie(int i, int j, int maxValue, int k) {
        int center = i + (j - i) / 2;

        if (j < i) {
            if (maxValue < C) {
                int toreturn = toremoveplaceholder.get(center);
                int firstnode = bEdges[toreturn].get(0);
                int secondnode = bEdges[toreturn].get(1);
                editedCapacities[firstnode][secondnode] = givenCapacities[firstnode][secondnode];
                editedCapacities[secondnode][firstnode] = givenCapacities[firstnode][secondnode];
                k--;
                maxValue = preflowpush();
            }
            System.out.println(k + " " + maxValue);
            return;
        }

        if (maxValue < C) {
            int below = center; 
            int above = j;
            if (j < toremove.size()) {
                below = center +1;
                above = j+1;
            }
            for(int m  = below; m <= above; m++) {
                int toreturn = toremoveplaceholder.get(m);
                int firstnode = bEdges[toreturn].get(0);
                int secondnode = bEdges[toreturn].get(1);
                editedCapacities[firstnode][secondnode] = givenCapacities[firstnode][secondnode];
                editedCapacities[secondnode][firstnode] = givenCapacities[firstnode][secondnode];
                k--;
            }
        } else {
            for (int n = i; n <= center; n++) {
                int toerase = toremoveplaceholder.get(n);
                int firstnode = bEdges[toerase].get(0);
                int secondnode = bEdges[toerase].get(1);
                editedCapacities[firstnode][secondnode] = editedCapacities[secondnode][firstnode] = 0;
                k++;

            }
        }
        int newflow = preflowpush();
        if (newflow >= C) {
            reP_Essie(center+1, j, newflow, k);
        } else {
            reP_Essie(i, center-1, newflow, k);
        }
	}

	private int preflowpush() {	    
	    //Init
        excess = new HashSet<>();

        for(int i = 0; i < N; i++){
            excessflow[i] = 0;
            heights[i] = 0;
            for (int j = 0; j < N; j++) {
                residualCapacities[i][j] = editedCapacities[i][j];
                flows[i][j]=0;
            }
        }
        heights[s] = N;

        for(int nextto : nodeNeighbours[s]) {
            flows[s][nextto] = editedCapacities[s][nextto];
            flows[nextto][s] = -editedCapacities[s][nextto];
            excessflow[nextto] = editedCapacities[s][nextto];
            residualCapacities[s][nextto] = 0;
            if(nextto != t && excessflow[nextto] > 0){
                excess.add(nextto);
            }
        }
        //System.out.println(excess.size()); -- interesting pattern


        while(!excess.isEmpty()) {
            //System.out.println("1");
            int current = excess.iterator().next();
            boolean push = false;
            for (int nextto : nodeNeighbours[current]) {
                if (heights[nextto] < heights[current] && 0 < residualCapacities[current][nextto]) {
                    push(current, nextto);
                    push = true;
                    break;
                }
            }
            if (!push) {
                relabel(current);
            }
        }
        return excessflow[t];
	}
	
	
	private void push(int current, int nextto) {
        int smallest = Math.min(excessflow[current], editedCapacities[current][nextto]-flows[current][nextto]);
        flows[current][nextto] += smallest;
        flows[nextto][current] -= smallest;
        excessflow[current] -= smallest;
        excessflow[nextto] += smallest;

        if(nextto != t && nextto != s && excessflow[nextto] > 0) {
            excess.add(nextto);
        }
        if (excessflow[current] <= 0) {
            excess.remove(current);
        }
         residualCapacities[current][nextto] = editedCapacities[current][nextto] - flows[current][nextto];
         residualCapacities[nextto][current] = editedCapacities[nextto][current] + flows[current][nextto];
    }
	private void relabel(int current) {
		heights[current] += 1;
	}
}
