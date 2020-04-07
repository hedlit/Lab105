import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class Ladders {
    int N = 0;
    int Q = 0;
    long extra;
    long start;
    long stop;

    List<Node> graphList = new LinkedList<Node>();
    private void read(Scanner scanner){
        N = scanner.nextInt();
        Q = scanner.nextInt(); 
        for(int i = 0; i<N; i++){
            Node n = new Node(scanner.next());
            for(Node u : graphList) {
				connect(n, u); 
				connect(u, n);
            }
            graphList.add(n);
        }       
    }

    private void connect(Node from, Node to){
        int[] from5 = from.five.chars().toArray();
        int[] to5 = to.five.chars().toArray();

        for (int i = 1; i<5; i++){
            int fromc = from5[i];
            boolean used = false;
            for(int j = 0; j < to5.length; j++){
                int toc = to5[j];
                if(fromc == toc && !used){
                    used =true;
                    to5[j] = -30;
                }
            }
            if (!used){
                return;
            }
        }
        from.addNeighbour(to);
    }

    private void BFS(Scanner scanner){
        start = System.currentTimeMillis();
        String fromWord = scanner.next();
        String toWord = scanner.next();

        Node fromNode = null;
        Node toNode = null;

       // System.out.println(graphList + " " +graphList.size());
        
        for(Node n: graphList){
           // System.out.println(n.neighbours);
            if(n.five.equals(fromWord)){
                fromNode = n;
                fromNode.isVisited();
                fromNode.level = 0;
            }
            else {
                n.visited = false;
            }
        }

        if(fromWord.equals(toWord)){
            toNode = fromNode;
        }

        Queue<Node> toVisit = new LinkedList<Node>();
        stop = System.currentTimeMillis();
        toVisit.add(fromNode); //O(1)
        extra =stop - start;

        while(!toVisit.isEmpty()){
            Node n = toVisit.poll(); //O(1)
            for(Node u: n.neighbours){ // O(n)
                if(u.visited == false){
                    u.visited = true;
                    toVisit.add(u); //O(1)
                    u.level = n.level+1;
                    if(u.five.equals(toWord)){
                        toNode = u;
                        break;
                    }
                }
            }
        }
        if (toNode == null) {
            //System.out.println("Impossible");
            ;
		} else {
            //System.out.println(toNode.level);
            ;
        }
        


    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        long unnecessary = 0;
        Ladders ladder = new Ladders();
        long startParse = System.currentTimeMillis();
        ladder.read(scanner);
        long stopParse = System.currentTimeMillis();
        long startbfs = System.currentTimeMillis();
        for (int i = 0; i < ladder.Q; i++) {
        ladder.BFS(scanner);
        unnecessary = unnecessary + ladder.extra;
        }
        long stopbfs = System.currentTimeMillis();

        System.out.println("bfs time: " + (stopbfs-startbfs-unnecessary) + " parse: " +(stopParse-startParse));
        scanner.close();
    }


}