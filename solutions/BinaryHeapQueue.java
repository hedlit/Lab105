package solutions;

import java.util.PriorityQueue;

public class BinaryHeapQueue<T extends Comparable> {
	private PriorityQueue<T> queue = new PriorityQueue<T>();
	
	//Should be heapify, now is just inserting all elements!
	public BinaryHeapQueue(T[] initialArray) {
		for(T t : initialArray) {
			queue.add(t);
		}
	}
	
	public BinaryHeapQueue() {
		queue = new PriorityQueue<T>();
	}
	
	public T poll() {
		return queue.poll();
	}
	
	public boolean isEmpty() {
		return queue.isEmpty();
	}
	
	public void add(T t) {
		queue.add(t);
	}
	
	public void remove(T t) {
		queue.remove(t);
	}
	
	//???
	public void changePriority(T element, int newPriority) {
		
	}

}
