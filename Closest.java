import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class Closest {
    private int N;

    public static void main(String[] args) {
        Closest c = new Closest();
        Point[] points = c.parse();
        double answer = c.closest(points, c.N);
        System.out.format(Locale.US, "%.6f", answer);
		System.out.println();

    }

    private Point[] parse() {
        Scanner scanner = new Scanner(System.in);
        N = scanner.nextInt();
        Point[] points = new Point[N];
        for (int i = 0; i < N; i++) {
            points[i] = new Point(scanner.nextDouble(), scanner.nextDouble());
        }
        //System.out.println(Arrays.toString(points));
        scanner.close();
        return points;
    }

    private double closest(Point[] a, int n) {
        Point[] byX = a.clone();
        Point[] byY = a.clone();
        Arrays.sort(byX, Point.sortByX);
        Arrays.sort(byY, Point.sortByY);
        return method(byX, byY, n);
    }

    private double method(Point[] byX, Point[] byY, int n) {
        if (n == 2) {
            return space(byX[0], byX[1]);
        }
        else if (n == 3) {
            return basic(byX, n);
        } else {
        //int rightN = Math.round(n/2);
        //int leftN = n/2;
        int rightN = n/2;
		int leftN = n/2;
			if(n % 2 != 0) { 
				rightN = n/2 + 1;
			}

        Point[] xLeft = new Point[leftN];
        Point[] yLeft = new Point[leftN];
        Point[] xRight = new Point[rightN];
        Point[] yRight = new Point[rightN];

        for(int i = 0; i < n; i++) {
            if (i < leftN) {
                xLeft[i] = byX[i];
                byX[i].check = true;
            } else {
                xRight[i-leftN] = byX[i];
                byX[i].check = false;
            }
        }

        double firstX = Math.ceil(byX[n/2].x);

        int leftYstop = 0;
        int rightYstop = 0;
        for(int i = 0; i < n; i++) {
            Point y = byY[i];
            if(y.check) {
                yLeft[leftYstop] = y;
                leftYstop++;
            } else {
                yRight[rightYstop] = y;
                rightYstop++;
            }
        }
        
        double left = method(xLeft, yLeft, leftN);
		double right = method(xRight, yRight, rightN);
        double smallestSoFar = Math.min(left, right);
        double leftShrink = firstX - smallestSoFar/2;
        double rightShrink = firstX + smallestSoFar/2;

        ArrayList<Point> gather = new ArrayList<Point>();
        for (int i = 0; i < n; i++) {
            if (byY[i].x <= rightShrink && byY[i].x >= leftShrink) {
                gather.add(byY[i]);
            }
        }

        for (int i = 0; i < gather.size(); i++) {
            for (int j = i + 1; j < i+15; j++) {
                if (j < gather.size()) {
                    double sp = space(gather.get(i), gather.get(j));
                    if (sp < smallestSoFar) {
                        smallestSoFar = sp;
                    }

                }

            }
        }
        return smallestSoFar;
    }

    }

    private double basic(Point[] points, int n) {
        double smallestSoFar = Double.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            Point a = points[i];
            for (int j = i +1; j < n; j++) {
                Point b = points[j];
                if(space(a, b) < smallestSoFar) {
                    smallestSoFar = space(a, b);
                }
            }
        }
        return smallestSoFar;
    }

    private static Double space(Point a, Point b) {
        double xLed = Math.abs(a.x - b.x);
        double yLed = Math.abs(a.y - b.y);
        return Math.sqrt(xLed*xLed + yLed*yLed);

    }

}
