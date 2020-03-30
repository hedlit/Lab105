import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Man {
    private String name;
    private LinkedList<String> preferences;
    public Man(String name, String preferences){
        this.name = name;
        this.preferences = Arrays.asList(preferences.trim().split(" "))
                .stream()
                .map(String::trim)
                .collect(Collectors.toCollection(LinkedList::new));

    }
    public String name(){
        return name;
    }
    public String nextProposal(){
        return preferences.get(0);
    }
    public void printpref(){
        System.out.println(preferences);
    }
    public void proposed(){
        preferences.removeFirst();
    }
    @Override
    public String toString() {
        return name;
    }
}