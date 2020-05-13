import java.util.HashMap;
import java.util.Scanner;

public class Gorilla {
    private int k, Q;
    private int emptycost = -4;
    HashMap<Character, Integer> map;
    int[][] costs;
    private int[][] matrixOpt;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Gorilla gorilla = new Gorilla();
        gorilla.foundation(scanner);
        for (int q = 0; q< gorilla.Q ; q++) {
            gorilla.findOpt(scanner);
        }
    }

    private void foundation(Scanner scanner) {
        String world = scanner.nextLine().replaceAll(" ", "");
        k = world.length();
        map = new HashMap<Character, Integer>(k);
        for (int i = 0; i < k; i++) {
            map.put(world.charAt(i), i);
        }
        //System.out.println(world + " " + k);
        //System.out.println(map);
        costs = new int[k][k];
        for (int i = 0; i < k; i++){
            for (int j = 0; j < k; j++) {
                costs[i][j] = scanner.nextInt();
            }
        }
        Q = scanner.nextInt();
    }


    private void findOpt(Scanner scanner) {
        String q1 = scanner.next();
        String q2 = scanner.next();

        matrixOpt = new int[q1.length() +1][q2.length() +1];

        for (int i = 0; i < q1.length()+1; i++){
            for(int j = 0; j< q2.length()+1; j++) {
                matrixOpt[i][j] = method(i, j, q1, q2);
            }
        }

        result(q1, q2);
    }





    private void result(String q1, String q2) {
        StringBuilder result1 = new StringBuilder();
		StringBuilder result2 = new StringBuilder();
        int m = q1.length();
        int n = q2.length();

        while (m>=0 && n>=0) {
            int cost = getspecial(m, n);
            int leftcost = getspecial(m, n-1);
            int diagcost = getspecial(m-1, n-1);
            int upcost = getspecial(m-1, n);

            int diffleft = emptycost + leftcost;
            int diffup = emptycost+upcost;
            int diagdiff = Integer.MIN_VALUE;

            if (n>0 && m>0) {
                diagdiff = costs[map.get(q1.charAt(m-1))][map.get(q2.charAt(n-1))] + diagcost;
            }

            if (cost == diagdiff) {
                if (m > 0) {
                    result1.append(q1.charAt(m-1));
                }
                if (n > 0) {
                    result2.append(q2.charAt(n-1));
                }
                m--; n--;
            } else if (cost == diffleft) {
                result1.append('*');
                result2.append(q2.charAt(n-1));
                n--;
            } else if (cost == diffup) {
                result2.append('*');
                result1.append(q1.charAt(m-1));
                m--;
            } else {
                m--; n--;
            }

        } System.out.println(result1.reverse().toString() + " "+ result2.reverse().toString());

    }

    private int getspecial(int m, int n) {
        if (n < 0 || m < 0) {
            return Integer.MIN_VALUE;
        } else return matrixOpt[m][n];
    }

    private int method(int i, int j, String q1, String q2) {
        if (i == 0) {
            return j * emptycost;
        } else if (j == 0) {
            return i * emptycost;
        } else {
            int cost = costs[map.get(q1.charAt(i-1))][map.get(q2.charAt(j-1))] + matrixOpt[i-1][j-1];
            int icost = emptycost + matrixOpt[i][j-1];
            int jcost = emptycost + matrixOpt[i-1][j];
            int maxi = Math.max(cost, Math.max(icost, jcost));
            return maxi;
        }
    }

    private class Pair {
        public char a;
        public int cost;
        public Pair(char a, int cost) {
            this.a = a;
            this.cost = cost;
        }
        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return "character: " + a + " cost: " + cost;
        }
    }


}
