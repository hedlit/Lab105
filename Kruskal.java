import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

public class Kruskal {
    private int N, M;
    private Circle[] circles;
    LinkedList<Line> lines = new LinkedList<Line>();

    private Kruskal (int N, int M) {
        this.N = N;
        this.M = M;
        circles = new Circle[N+1];
    }

    private static class Circle {
        private int above;
        private int size;

        private Circle() {
            this.above = -1;
            this.size = 1;
        }

    }

    private static class Line implements Comparable<Line> {
        private int to;
        private int from;
        private int cost;

        private Line(int from, int to, int cost) {
            this.from = from;
            this.to = to;
            this.cost  = cost;
        }
        @Override
        public int compareTo(Line l) {
            return this.cost - l.cost;
        }
    }

    public void prep(Scanner scanner) {
        for (int i = 1; i < N+1; i++) {
            circles[i] = new Circle();
        }

        for (int i = 0; i < M; i++) {
            lines.add(new Line(scanner.nextInt(), scanner.nextInt(), scanner.nextInt()));
        }
        Collections.sort(lines); //O(nlogn)
    }

    private int findTop(int circle) {
        int topNum = circle;
        while(circles[topNum].above != -1) {
            topNum = circles[topNum].above;
        }
        while (circles[circle].above != -1) {
            int above = circles[circle].above;
            circles[circle].above = topNum;
            circle = above;
        }
        return topNum;
    }

    private void combine(int circlefrom, int circleto) {
        circlefrom = findTop(circlefrom);
        circleto = findTop(circleto);

        if(circles[circlefrom].size < circles[circleto].size) {
            circles[circlefrom].above = circleto;
            circles[circleto].size = circles[circlefrom].size;
        }
        else {
            circles[circleto].above = circlefrom;
            circles[circlefrom].size = circles[circleto].size;
        }
    }

    private void method() {
        int result = 0;
        for (int i = 0; i < M; i++) {
            Line current = lines.poll();
            if (findTop(current.from) != findTop(current.to)){
                result = result + current.cost;
                combine(current.from, current.to);
            }
        }
        System.out.println(result);
    }

    public static void main(String[] args) {
        long startParse = System.currentTimeMillis();
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();
        int M = scanner.nextInt();

        Kruskal k = new Kruskal(N, M);
        k.prep(scanner);
        scanner.close();  
        long stopParse = System.currentTimeMillis();
        long startK = System.currentTimeMillis();
        k.method();
        long stopK = System.currentTimeMillis();
        System.out.println("Parsing time: " + (stopParse - startParse) + " Kruskal time: " +( stopK - startK));
    }
}
