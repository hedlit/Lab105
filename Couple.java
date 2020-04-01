//Kommentar

public class Couple implements Comparable<Couple> {
    public Man man;
    public Woman woman;
    public Couple (Woman woman, Man man){
        this.woman = woman;
        this.man = man;
    }
    public Integer compWoman(){
        return Integer.parseInt(woman.name());
    }
    public Woman woman(){
        return woman;
    }

    public Man man (){
        return man;
    }

    @Override
    public int compareTo(Couple o) {
        return this.compWoman().compareTo(o.compWoman());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        if (!(obj instanceof Couple)) { 
            return false; 
        }
        Couple c = (Couple) obj;
        return (woman.name().equals(c.woman.name()) && man.name().equals(c.man.name()));

    }
    @Override
    public String toString() {
        return (man.name() + " " + woman.name());
    }

}