import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;


public class Solution {

    private int groupSize = 0;

    private LinkedList<Man> men = new LinkedList<Man>();
    private Map<String, Woman> womenMap;
    private Map<Woman, Couple> couples;

    public List<String> reader() {
        Scanner scanner = new Scanner(System.in);
        LinkedList<String> llist = new LinkedList<String>();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            llist.add(line);
        }
        scanner.close();

        groupSize = Integer.parseInt(llist.get(0));

        String[][] strings = new String[llist.size()][groupSize+1];
        for (int i = 0; i < llist.size(); i++ ) {
            strings[i] = llist.get(i).split(" ");
        }

        Object[] flat  = Arrays.stream(strings)
                        .flatMap(Arrays::stream)
                        .toArray();
        
        String[] flatstring = Arrays.copyOf(flat, flat.length, String[].class);
        List<String> otherLlist = Arrays.asList(flatstring);
        List<String> finalList = new LinkedList<String>();

        int index = 1;
        finalList.add(llist.get(0));

        while (index < otherLlist.size()){
            finalList.add(String.join(" ", otherLlist.subList(index, groupSize+index+1)).trim());
            index += groupSize+1;
        }

        return finalList;
    }

    public void createFoundation(List<String> rawList) {
        groupSize = Integer.parseInt(rawList.get(0));
        womenMap = new HashMap<String, Woman>(groupSize);

        List<String> firstTime = new ArrayList<String>(groupSize);
        List<String> secondTime = new ArrayList<String>(groupSize);
        List<String> firstTimeNames = new ArrayList<String>(groupSize);

        for(String i: rawList.subList(1, rawList.size())){
            String[] person = i.split(" ");
            if(firstTimeNames.contains(person[0])){
                secondTime.add(i);
            }
            else {
                firstTime.add(i);
                firstTimeNames.add(person[0]);
            }
        }
        createMen(secondTime);
        createWomenForMap(firstTime);
    }
    public void createMen(List<String> list) {
        for(String i: list){
            String name = i.split(" ")[0];
            men.add(new Man(name, i.substring(name.length())));
        }
    }

    public void createWomenForMap(List<String> list) {
        for(String i: list){
            String name = i.split(" ")[0];
            womenMap.put(name, new Woman(name, i.substring(name.length())));
        }
    }

    public void pair() { //Gale-Shapley Algorithm
        couples = new HashMap<Woman, Couple>(groupSize);
        while(!men.isEmpty()){ 
            Man m = men.removeFirst(); //O(1)
            Woman w = womenMap.get(m.nextProposal()); //O(1) O(1)
            if(w.issingle()){
                couples.put(w, new Couple(w, m)); //O(1)
                w.paired(m); //O(1)
            }
            else if (w.doILikeTheNewGuyMore(w.husband(), m)){
                couples.remove(w); //O(1)
                couples.put(w, new Couple(w, m)); //O(1)
                men.addLast(w.husband()); //O(1)
                w.husband().proposed(); //O(1)
                w.paired(m); //O(1)
            }
            else {
                m.proposed(); //O(1)
                men.addLast(m); //O(1)
            }
        }
    }

    public void print(){ //messy because I wanted to use hashMap
        List<Couple> sortableCouples = couples.values()
                                            .stream()
                                            .collect(Collectors.toList());
        Collections.sort(sortableCouples);
        for(Couple i: sortableCouples){
            System.out.println(i.man());
        }
    }

    public void run(){
        //long startTimeParse = System.currentTimeMillis();
        createFoundation(reader());
        //long endTimeParse = System.currentTimeMillis();
        //long startTimeGSA = System.currentTimeMillis();
        pair();
        //long endTimeGSA = System.currentTimeMillis();
        print();
        //long timeToParse = endTimeParse - startTimeParse;
        //long timeToGSA = endTimeGSA - startTimeGSA;
        //System.out.println("Parsing: " + timeToParse + " and time for algorithm is: " + timeToGSA);
    }
}
