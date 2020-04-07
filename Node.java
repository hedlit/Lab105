import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Node {
    
    public String five;
    public boolean visited;
    List<Node> neighbours;
    public int level;
    
    
    public Node(String five){
        visited = false;
        neighbours = new LinkedList<>();
        level = 0;

        this.five = five;
    }

    public void levelUp(){
        this.level = level +1;
    }

    public void isVisited(){
        this.visited = true;
    }
     public void isNotVisited(){
         this.visited = false;
     }

     public void addNeighbour(Node k){
         neighbours.add(k);
     }





    /*public String five;
    public boolean visited = false;
    LinkedList<Node> neighbours = new LinkedList<Node>();
    char lastFour[];
    public int level = 0;
    public Node(String five){
        lastFour = (five.substring(1).toCharArray());
        Arrays.sort(lastFour);
        this.five = five;
    }

    public char[] lastFourLettersSorted(){
        return lastFour;
    }

	public void addNeighbour(Node k) {
        neighbours.addLast(k);
       // System.out.println(five +" " +neighbours);
    }

    public void levelUp(){
        level++;
    }

    public int level(){
        return level;
    }


    public String five() {
        return five;
    }

    public void isVisited() {
        visited = true;
    }

    public void isNotVisited() {
        visited = false;
    }



    
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if(obj instanceof Node){
            return this.five.equals(((Node) obj).five());
        }
        else return false;

    } */

    @Override
    public String toString() {
        return five;
    }




}