import java.util.Comparator;

public class Point {
    public Double x, y;
    public static final Comparator<Point> sortByX = new compareByX();
    public static final Comparator<Point> sortByY = new compareByY();
    public boolean check;

    public Point (Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    private static class compareByX implements Comparator<Point> {
        public int compare(Point a, Point b) {
            if (a.x < b.x) return -1;
            if (a.x > b.x) return 1;
            return 0;
        }
    }
    private static class compareByY implements Comparator<Point> {
        public int compare(Point a, Point b) {
            if (a.y < b.y) return -1;
            if (a.y > b.y) return 1;
            return 0;
        }
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return x + " " +y;
    }
}
