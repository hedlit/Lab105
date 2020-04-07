import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class Ugh {
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
        //System.out.println("2");

        int[] from5 = from.five.chars().toArray();
        int[] to5 = to.five.chars().toArray();
       /* Arrays.sort(to5);
        Arrays.sort(from4);
        String toFrontString = String.copyValueOf(to5, 0, 3);
        String toBackString = String.copyValueOf(to5, 1, 4);

        char[] toFront = toFrontString.toCharArray();
        char[] toBack = toBackString.toCharArray();*/

       /* if( Arrays.equals(from4, 0, 3, to5, 0, 3) || Arrays.equals(from4, 0, 3, to5, 1, 4)){
            from.addNeighbour(to);
        } */

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
        toVisit.add(fromNode);
        stop = System.currentTimeMillis();
        extra =stop - start;
        while(!toVisit.isEmpty()){
            Node n = toVisit.poll();
            for(Node u: n.neighbours){
                if(u.visited == false){
                    u.visited = true;
                    toVisit.add(u);
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
        Ugh ugh = new Ugh();
        long startParse = System.currentTimeMillis();
        ugh.read(scanner);
        long stopParse = System.currentTimeMillis();
        long startbfs = System.currentTimeMillis();
        for (int i = 0; i < ugh.Q; i++) {
        ugh.BFS(scanner);
        unnecessary = unnecessary + ugh.extra;
        }
        long stopbfs = System.currentTimeMillis();

        System.out.println("bfs time: " + (stopbfs-startbfs-unnecessary) + " parse: " +(stopParse-startParse));
        scanner.close();
    }


}