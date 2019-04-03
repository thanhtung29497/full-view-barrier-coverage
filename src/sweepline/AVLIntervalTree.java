package sweepline;

import java.util.LinkedList;

public class AVLIntervalTree<Value> {
	public class ITNode{
		public Interval interval;
		Value value;
		ITNode left, right;
		int size;
		Double max;
		public ITNode(Interval i, Value value) {
			this.interval = i;
			this.value = value;
			this.max = i.high;
			this.left = this.right = null;
			this.size = 1;
		}
	}
	public ITNode root;
	
	public boolean contains(Interval interval) {
        return (get(interval) != null);
    }

    // return value associated with the given key
    // if no such value, return null
    public Value get(Interval interval) {
        return get(root, interval);
    }
    
    private Value get(ITNode x, Interval interval) {
        if (x == null)                  return null;
        int cmp = interval.compareTo(x.interval);
        if      (cmp < 0) return get(x.left, interval);
        else if (cmp > 0) return get(x.right, interval);
        else              return x.value;
    }
    
    public void put(Interval interval, Value value) {
        if (contains(interval)) { 
        	System.out.println("duplicate");
        	remove(interval);
        }
        root = randomizedInsert(root, interval, value);
//        System.out.println(root.interval.id + "111111111111111111111111111111111111111");
    }
    
 // make new node the root with uniform probability
    private ITNode randomizedInsert(ITNode x, Interval interval, Value value) {
        if (x == null) return new ITNode(interval, value);
        if (Math.random() * size(x) < 1.0) return rootInsert(x, interval, value);
        int cmp = interval.compareTo(x.interval);
        if (cmp < 0)  x.left  = randomizedInsert(x.left,  interval, value);
        else          x.right = randomizedInsert(x.right, interval, value);
        fix(x);
        return x;
    }
    
    private ITNode rootInsert(ITNode x, Interval interval, Value value) {
        if (x == null) return new ITNode(interval, value);
        int cmp = interval.compareTo(x.interval);
        if (cmp < 0) { x.left  = rootInsert(x.left,  interval, value); x = rotR(x); }
        else         { x.right = rootInsert(x.right, interval, value); x = rotL(x); }
        return x;
    }
    
    private ITNode joinLR(ITNode a, ITNode b) { 
        if (a == null) return b;
        if (b == null) return a;

        if (Math.random() * (size(a) + size(b)) < size(a))  {
            a.right = joinLR(a.right, b);
            fix(a);
            return a;
        }
        else {
            b.left = joinLR(a, b.left);
            fix(b);
            return b;
        }
    }
    
    public Value remove(Interval interval) {
        Value value = get(interval);
        root = remove(root, interval);
        return value;
    }

    private ITNode remove(ITNode h, Interval interval) {
        if (h == null) return null;
        int cmp = interval.compareTo(h.interval);
        if      (cmp < 0) h.left  = remove(h.left,  interval);
        else if (cmp > 0) h.right = remove(h.right, interval);
        else              h = joinLR(h.left, h.right);
        fix(h);
        return h;
    }
    
    public Interval search(Interval interval) {
        return search(root, interval);
    }

    // look in subtree rooted at x
    public Interval search(ITNode x, Interval interval) {
        while (x != null) {
            if (interval.doOverlap(x.interval)) return x.interval;
            else if (x.left == null)             x = x.right;
            else if (x.left.max < interval.low)  x = x.right;
            else                                 x = x.left;
        }
        return null;
    }

    // return *all* intervals in data structure that intersect the given interval
    // running time is proportional to R log N, where R is the number of intersections
    public Iterable<Interval> searchAll(Interval interval) {
//    	if(root != null)
        LinkedList<Interval> list = new LinkedList<Interval>();
        searchAll(root, interval, list);
        return list;
    }
 // look in subtree rooted at x
    public boolean searchAll(ITNode x, Interval interval, LinkedList<Interval> list) {
         boolean found1 = false;
         boolean found2 = false;
         boolean found3 = false;
         if (x == null)
            return false;
        if (interval.doOverlap(x.interval)) {
//        	System.out.print(x.interval.id);
            list.add(x.interval);
            found1 = true;
        }
        if (x.left != null && x.left.max >= interval.low)
            found2 = searchAll(x.left, interval, list);
        if (found2 || x.left == null || x.left.max < interval.low)
            found3 = searchAll(x.right, interval, list);
        return found1 || found2 || found3;
    }
    
 // return number of nodes in subtree rooted at x
    public int size() { 
    	return size(root); 
    }
    
    private int size(ITNode x) { 
        if (x == null) return 0;
        else           return x.size;
    }

    // height of tree (empty tree height = 0)
    public int height() {
    	return height(root);
    }
    private int height(ITNode x) {
        if (x == null) return 0;
        return 1 + Math.max(height(x.left), height(x.right));
    }
    
 // fix auxiliary information (subtree count and max fields)
    private void fix(ITNode x) {
        if (x == null) return;
//        System.out.print(x);
        x.size = 1 + size(x.left) + size(x.right);
        x.max = max3(x.interval.high, max(x.left), max(x.right));
    }

    private Double max(ITNode x) {
        if (x == null) return Double.MIN_VALUE;
        return x.max;
    }

    // precondition: a is not null
    private Double max3(Double a, Double b, Double c) {
        return Math.max(a, Math.max(b, c));
    }

    // right rotate
    private ITNode rotR(ITNode h) {
        ITNode x = h.left;
        h.left = x.right;
        x.right = h;
        fix(h);
        fix(x);
        return x;
    }

    // left rotate
    private ITNode rotL(ITNode h) {
        ITNode x = h.right;
        h.right = x.left;
        x.left = h;
        fix(h);
        fix(x);
        return x;
    }
    
 // check integrity of subtree count fields
    public boolean check() { return checkCount() && checkMax(); }

    // check integrity of count fields
    private boolean checkCount() {
    	return checkCount(root);
    }
    private boolean checkCount(ITNode x) {
        if (x == null) return true;
        return checkCount(x.left) && checkCount(x.right) && (x.size == 1 + size(x.left) + size(x.right));
    }

    private boolean checkMax() {
    	return checkMax(root); 
    }
    
    private boolean checkMax(ITNode x) {
        if (x == null) return true;
        return x.max ==  max3(x.interval.high, max(x.left), max(x.right));
    }
    
//    public static void main(String[] args) {
////        int N = Integer.parseInt(args[0]);
//        AVLIntervalTree<String> st = new AVLIntervalTree<String>();
//        // generate N random intervals and insert into data structure
//        Interval interval0 = new Interval(0, 1, 0);
////        Interval interval1 = new Interval(5, 6, 1);
////        Interval interval2 = new Interval(3, 4, 2);
//        st.put(interval0, "" + 0);
////        st.put(interval1, "" + 1);
////        st.put(interval2, "" + 2);
//        Interval interval = new Interval(1, 3, 1);
//
//        for (Interval x : st.searchAll(interval))
//        	System.out.println(x.id + " ");
//
//    }
}
