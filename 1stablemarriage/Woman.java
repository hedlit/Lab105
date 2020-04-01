import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Woman {
    private String name;
    private Man husband;
    private List<Integer> preferences;
    private boolean issingle = true;
    public Woman(String name, String initialPreferences){
        this.name = name;
        this.preferences = Arrays.asList(initialPreferences.trim().split(" "))
                .stream()
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        Integer [] adjustedArr = new Integer [preferences.size()*2+1];

        for(int i=0; i < preferences.size(); i++){
            adjustedArr[preferences.get(i)] = i;
        }
        preferences = Arrays.asList(adjustedArr)
                        .stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

    }
    public String name(){
        return name;
    }

    public boolean doILikeTheNewGuyMore(Man currentMan, Man newSuitor){
        if(preferences.get(Integer.parseInt(newSuitor.name())-1) < preferences.get(Integer.parseInt(currentMan.name())-1)){
            return true;
        }
        else return false;
    }

    public void paired(Man m){
        issingle = false;
        husband = m;
    }

    public Man husband(){
        return husband;
    }

    public boolean issingle(){
        return issingle;
    }

    public void preflist(){
        System.out.println("womans preferences " + preferences);
    }

    @Override
    public String toString() {
        return name;
    }

}
