package sweepline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class NewRectangleSweep {
	public static class Event implements Comparable<Event>{
		Double time;
		int rectangleID;
		int active;
		
		public Event(Double time, int rec, int active) {
			this.time = time;
			this.rectangleID = rec;
			this.active = active;
		}

		@Override
		public int compareTo(Event other) {
//			System.out.println(rectangleID + " " + other.rectangleID);
			if (this.time < other.time + 1.0e-9) return -1;
			else if (this.time > other.time - 1.0e-9) return 1;
			else {
				if(this.active > other.active) return 1;
				else return -1;
			}
		}
	}
	
	 public List<Integer> BFS(int s, int t, ArrayList<ArrayList<Integer>> adjacencyLists) { 
        // Mark all the vertices as not visited(By default 
        // set as false) 
		int N = adjacencyLists.size();
        boolean visited[] = new boolean[N]; 
        int parrent[] =  new int[N];
  
        // Create a queue for BFS 
        LinkedList<Integer> queue = new LinkedList<Integer>(); 
  
        // Mark the current node as visited and enqueue it 
        visited[s]=true; 
        parrent[s]=-1;
        queue.add(s); 
        Boolean hasPath = false;
  
        while (queue.size() != 0) 
        { 
            // Dequeue a vertex from queue and print it 
            int current = queue.poll(); 
            
            Iterator<Integer> i = adjacencyLists.get(current).listIterator();
            while (i.hasNext()) 
            { 
                int n = i.next(); 
                if (!visited[n]) 
                { 
                    visited[n] = true; 
                    parrent[n] = current;
                    if (n == t) {
                    	hasPath = true;
                    	break;
                    }
                    queue.add(n); 
                } 
            } 
        } 
        if (!hasPath) {
        	return null;
        }
        
        int trace = t;
        ArrayList<Integer> shortestPath = new ArrayList<Integer>();
        while(parrent[trace] != -1) {
        	shortestPath.add(trace);	
        	trace = parrent[trace];
        }
        shortestPath.add(s);
        Collections.reverse(shortestPath);
        return shortestPath;
    } 
	
	public List<Integer> solve(List<Rectangle2D> rectangles) {
		int N = rectangles.size();
		PriorityQueue<NewRectangleSweep.Event> pq = new PriorityQueue<NewRectangleSweep.Event>();
		ArrayList<ArrayList<Integer>> adjacencyLists = new ArrayList<ArrayList<Integer>>();
		int[] isProcessed = new int[N];
		for (int i = 0; i < N; i++) {
			ArrayList<Integer> temp = new ArrayList<Integer>();
			temp.add(i);
			adjacencyLists.add(temp);
			isProcessed[i] = 0;
//			System.out.println(rectangles.get(i).topLeft.x + " " + rectangles.get(i).bottomRight.x);
			Event e1 = new Event(rectangles.get(i).topLeft.x, i, 0);
			Event e2 = new Event(rectangles.get(i).bottomRight.x, i, 1);
			pq.add(e1);
			pq.add(e2);
		}
		
		AVLIntervalTree<String> st = new AVLIntervalTree<String>();
		while(!pq.isEmpty()) {
			Event e = pq.poll();
//			int sweep = e.time;
//			System.out.println(e);
			int id = e.rectangleID;
			if(isProcessed[id] == 0) {
//				System.out.println(rectangles.get(id).getInter().id + "->>>>>>>>>>>>>>>>>>>");
				Iterable<Interval> intersections = st.searchAll(rectangles.get(id).getInter());
//				System.out.println();
				for (Interval inter: intersections) {
//					System.out.print(inter.id + " ");
					adjacencyLists.get(rectangles.get(id).getInter().id).add(inter.id);
					adjacencyLists.get(inter.id).add(rectangles.get(id).getInter().id);
				}
				st.put(rectangles.get(id).getInter(), "" + id);
//				System.out.println("");
				isProcessed[id] = 1;
			}else st.remove(rectangles.get(id).getInter());
		}

		return BFS(0, rectangles.size() - 1, adjacencyLists);	
	}
	
	public static void main(String [] args) {
		ArrayList<Rectangle2D> rectangles = new ArrayList<Rectangle2D>();
		Rectangle2D rectangle = new Rectangle2D(new Point2D(-1.0, 2.0), new Point2D(0.0, 0.0), 0);
		Rectangle2D rectangle0 = new Rectangle2D(new Point2D(0.0, 2.0), new Point2D(2.0, 0.0), 1);
		Rectangle2D rectangle1 = new Rectangle2D(new Point2D(2.0, 5.0), new Point2D(4.0, 0.0), 2);
		Rectangle2D rectangle2 = new Rectangle2D(new Point2D(1.0, 4.0), new Point2D(5.0, 1.0), 3);
		Rectangle2D rectangle3 = new Rectangle2D(new Point2D(3.0, 8.0), new Point2D(5.0, 3.0), 4);
		Rectangle2D rectangle4 = new Rectangle2D(new Point2D(5.0, 8.0), new Point2D(6.0, 1.0), 5);
		rectangles.add(rectangle);
		rectangles.add(rectangle0);
		rectangles.add(rectangle1);
		rectangles.add(rectangle2);
		rectangles.add(rectangle3);
		rectangles.add(rectangle4);
		NewRectangleSweep test = new NewRectangleSweep();
		System.out.println(test.solve(rectangles));
	}
}
