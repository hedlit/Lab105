package solutions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

/** Q&A:
 * 
 * What is the time complexity, and more importantly why?
 * See report.
 * 
 * Why is it sufficient to check a few points along the mid line?
 * Only interested in those that can be closer than min, i.e. check those inside a min-interval.
 * Inside small squares of size min/2, there can only be one dot each because diagonal is smaller than min.
 * Actually only have to check the other side, 8 dots, but easier to check both sides.
 * Also, for the points in the "outer" squares, not all 8 squares actually need to be checked (only 6).
 * 
 * Draw a picture and show/describe when each distance is checked in your solution!
 * 
 * When do you break the recursion and use brute force?
 * Should be done when n < 40. But can be done only with base cases n=3 and n=2.
 * Makes no noticeable difference for 6huger.in.
 * 
 * */

public class ClosestPair {
	private long algorithmTime;
	private long parseTime;
	
	private int N; //The number of points
	
	public static void main(String[] args) {
		ClosestPair cp = new ClosestPair();
		
		long start = System.currentTimeMillis();
	    Point[] allPoints = cp.parse();
		long stop = System.currentTimeMillis();
		cp.parseTime = stop - start;
		
		long start2 = System.currentTimeMillis();
	    double smallestDistance = cp.closest(allPoints, cp.N);
		long stop2 = System.currentTimeMillis();
		cp.algorithmTime = stop2 - start2;
	    
		System.out.format(Locale.US, "%.6f", smallestDistance);
		System.out.println();
		//cp.printTimeResult();
	}
	
	private Point[] parse() {
		Scanner scan = new Scanner(System.in);
		N = scan.nextInt();
		
		//N lines with the x and y coordinates for the i-th point
		int x, y;
		Point[] allPoints = new Point[N];
		for(int i = 0; i < N; i++) {
			x = scan.nextInt();
			y = scan.nextInt();
			allPoints[i] = new Point(x, y);
		}
		return allPoints;
	}
	
	
	public double closest(Point[] allPoints, int n) {
		//Creates two sorted point-arrays.
		Point[] xSortedPoints = allPoints.clone();
		Point[] ySortedPoints = allPoints.clone();
		Arrays.sort(xSortedPoints, (p1, p2) -> p1.x - p2.x);
		Arrays.sort(ySortedPoints, (p1, p2) -> p1.y - p2.y);
		
		//Starts recursion
		return closest(xSortedPoints, ySortedPoints, n);
	}
	
	//n is the number of points, i.e. the length of the vectors
	private double closest(Point[] xSortedPoints, Point[] ySortedPoints, int n) {		
				
		//Base cases
		if(n == 2) {
		    return xSortedPoints[0].distanceTo(xSortedPoints[1]);
		} else if (n < 40) { //Could also be (n==3)
			return closestBruteForce(xSortedPoints, n);
		} else { //recursive case
			
			//divide xSortedPoints into two arrays, split in the middle
			//divide ySortedPoints into two, so that leftY and rightY matches with the points in the x-arrays
			
			int rightLength = n/2;
			int leftLength = n/2;
			if(n % 2 != 0) { //if necessary, make the right array one bigger than the left
				rightLength = n/2 + 1;
			}
			
			Point[] leftXPoints = new Point[leftLength];
			Point[] rightXPoints = new Point[rightLength];
			Point[] leftYPoints = new Point[leftLength];
			Point[] rightYPoints = new Point[rightLength];
			int xDivider = xSortedPoints[n/2].x; //the first x in the right array; the x to divide on
			
			//I tried to combine this and the division of ySortedPoints, but that was asking for trouble
			for(int i = 0; i < n; i++) {
				if (i < leftLength) {
					leftXPoints[i] = xSortedPoints[i];
					xSortedPoints[i].left = true;
				} else {
					rightXPoints[i-leftLength] = xSortedPoints[i];
					xSortedPoints[i].left = false;
				}
			}
			
			int leftYIndex = 0;
			int rightYIndex = 0;
			for(int i = 0; i < n; i++) {
				Point yPoint = ySortedPoints[i];
				if(yPoint.left) {
					leftYPoints[leftYIndex] = yPoint;
					leftYIndex++;
				} else {
					rightYPoints[rightYIndex] = yPoint;
					rightYIndex++;
				}
			}
			
			//Solve the two subproblems and compute the minimum result of these
			double leftMin = closest(leftXPoints, leftYPoints, leftLength);
			double rightMin = closest(rightXPoints, rightYPoints, rightLength);
			double min = Math.min(leftMin, rightMin);
			
			//Create a smaller list from ySortedPoints,
			//containing the points close to the partition,
			ArrayList<Point> linePoints = new ArrayList<>();
			for(int i = 0; i < n; i++) {
				Point yPoint = ySortedPoints[i];
				if(yPoint.x <= xDivider + min/2 && yPoint.x >= xDivider - min/2) {
					linePoints.add(yPoint);
				}
			}
			
			//Check each point in linePoints, to see if any 15 points are closer than min
		    //If so, update min
			for(int i = 0; i < linePoints.size(); i++) {
				for(int j = i+1; j < i+15; j++) {
					if(j < linePoints.size()) {
						double distance = linePoints.get(i).distanceTo(linePoints.get(j));
						if(distance < min) {
							min = distance;
						}
					}
				}
			}
			return min;
		}
	}
	
	
	private double closestBruteForce(Point[] points, int n) {
		double minDistance = Double.MAX_VALUE;
		for(int i = 0; i < n; i++) {
			Point p1 = points[i];
			for(int j = i + 1; j < n; j++) {
				Point p2 = points[j];
				if(p1.distanceTo(p2) < minDistance) {
					minDistance = p1.distanceTo(p2);
				}
			}
		}
		return minDistance;
	}
	
	private void printTimeResult() {
		System.out.println("Parse time (ms): " + parseTime);
		System.out.println("Algorithm time (ms): " + algorithmTime);
	}
	
	private static class Point {
		private int x;
		private int y;
		private boolean left;
		
		private Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		private double distanceTo(Point p) {
			double xDist = Math.abs(this.x - p.x);
			double yDist = Math.abs(this.y - p.y);
			return Math.sqrt(xDist * xDist + yDist * yDist);
		}
		
		@Override
		public String toString() {
			return "(" + x + "," + y + ")";
		}
	}	
}
