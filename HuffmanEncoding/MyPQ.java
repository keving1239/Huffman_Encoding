package HuffmanEncoding;
import java.util.*;

/*
Created on 05/27/2022 by Kevin Galdamez
***************************************
This class is a heap implementation of a
priority queue, stored on an array. It is
also a base for the huffman tree implementation.
*/

@SuppressWarnings("unused")
public class MyPQ<T> implements Comparable<MyPQ<T>>{
    /**attributes**/
    private Object[] binHeap;
    //type decides whether the heap/PQ is min or max
    private final Comparator<? super T> type;
    private int size,capacity;
    //thresholds are used to increase/decrease capacity
    private float topThreshold,botThreshold;
    private static final float TOP_THRESHOLD = 0.80f,BOT_THRESHOLD = 0.40f;

    /**constructors**/
    public MyPQ(){this(null,TOP_THRESHOLD,null);}
    public MyPQ(T initial){this(initial,TOP_THRESHOLD,null);}
    public MyPQ(float threshold){this(null,threshold,null);}
    public MyPQ(Comparator<? super T> c){this(null,TOP_THRESHOLD,c);}
    public MyPQ(T initial,float threshold){this(initial,threshold,null);}
    public MyPQ(float threshold,Comparator<? super T> c){this(null,threshold,c);}
    public MyPQ(T initial,Comparator<? super T> c){this(initial,TOP_THRESHOLD,c);}
    public MyPQ(T initial,float threshold,Comparator<? super T> c){
        this.size = 0;
        this.capacity = 3;
        this.topThreshold = checkTopThreshold(threshold);
        this.botThreshold = BOT_THRESHOLD;
        this.binHeap = new Object[capacity];
        this.type = checkComparator(c);
        this.add(initial);
    }//end overridden constructor

    /**public PQ methods**/
    //add a given element
    public void add(T element){
        //add a given element
        if(element == null) return;
        binHeap[size] = element;
        size++;
        buildHeap();
    }//end add
    //remove a given element
    public void remove(T element){
        if(element == null) return;
        int index = getIndexOf(element);
        removeAt(index);
    }//end remove
    //remove the element at a given index
    public void removeAt(int index){
        if(index < 0 || size < 1) return;
        size--;
        //swap last element and removed element
        binHeap[index] = binHeap[size];
        binHeap[size] = null;
        if(index == size) return;
        buildHeap();
    }//end remove
    //return the object at the given index
    @SuppressWarnings("unchecked")
    public T get(int index){
        if(index < 0 || index > size-1) return null;
        return (T) binHeap[index];
    }//end get
    //return index of an element or -1 if not found
    public int getIndexOf(T element){
        if(element == null) return -1;
        for(int i = 0; i < size;i++){
            if(binHeap[i].equals(element)) return i;
        }//end for loop
        return -1;
    }//end getIndexOf()
    //removes and returns largest element in heap
    public T extractRoot(){
        if(size < 1) return null;
        T root = root();
        removeAt(0);
        return root;
    }//end extractRoot
    public boolean contains(T element){return getIndexOf(element) != -1;}
    public boolean isEmpty(){return size == 0;}
    public void clear(){
        capacity = 3;
        size = 0;
        binHeap = new Object[capacity];
    }//end clear
    //merge two priority queues
    public void merge(MyPQ<T> pq){
        if(pq == null) return;
        T[] heapArray = pq.toArray();
        for(T t : heapArray) if(t != null) this.add(t);
    }//end merge
    @SuppressWarnings("unchecked")
    public T[] toArray(){return (T[]) binHeap;}

    /**private PQ methods**/
    //return usable threshold (between 0 and 1 exclusive)
    private float checkTopThreshold(float threshold){
        if(threshold <= 0 || threshold >= 1.0f) return TOP_THRESHOLD;
        return threshold;
    }//end checkThreshold
    //return usable comparator, only allow natural or reverse
    private Comparator<? super T> checkComparator(Comparator<? super T> c){
        if(c == null) return null;
        if(c.equals(Comparator.naturalOrder())) return c;
        if(c.equals(Comparator.reverseOrder())) return c;
        else return null;
    }//end checkComparator
    //increase capacity in line with: capacity = 2^h - 1 (h is height)
    private void incrementHeight(){
        capacity <<= 1;
        capacity |= 1;
        //create new, larger array
        binHeap = Arrays.copyOf(binHeap,capacity);
    }//end incrementHeight
    //decrease capacity in line with: capacity = 2^h - 1 (h is height)
    private void decrementHeight(){
        if(capacity <= 3) return;
        capacity >>= 1;
        binHeap = Arrays.copyOf(binHeap,capacity);
    }//end decrementHeight
    //set the bottom threshold to 2^(n-1)-1/2^n-1
    private void smartBotThreshold(){
        if(capacity <= 3) return;
        float temp = ((capacity>>1)/(float) capacity);
        botThreshold = (Math.round(temp*100.0f)/100.0f) - 0.01f;
    }//end smartBotThreshold
    //returns 1 if first is greater than second, 0 if equal, -1 if less than second
    @SuppressWarnings("unchecked")
    private int compare(T first,T second){
        Comparable<? super T> c = (Comparable<? super T>) first;
        return c.compareTo(second);
    }//end compare
    //heapify all entered elements
    @SuppressWarnings("unchecked")
    private void heapify(int index){
        int p = index,r = 2*index+2,l = 2*index+1;
        //if comparator is null use min heap, else use max heap
        if(type == Comparator.reverseOrder()){
            if(l < size && compare((T) binHeap[l],(T) binHeap[p]) > 0) p = l;
            if(r < size && compare((T) binHeap[r],(T) binHeap[p]) > 0) p = r;
        }//end if statement
        else{
            if(l < size && compare((T) binHeap[l],(T) binHeap[p]) < 0) p = l;
            if(r < size && compare((T) binHeap[r],(T) binHeap[p]) < 0) p = r;
        }//end else statement
        if(p != index){
            T temp = (T) binHeap[index];
            binHeap[index] = binHeap[p];
            binHeap[p] = temp;
            heapify(p);
        }//end if statement
    }//end heapify
    //construct the heap from given elements
    private void buildHeap(){
        if(((float)(size))/capacity >= topThreshold) incrementHeight();
        else if(((float)(size))/capacity <= botThreshold) decrementHeight();
        smartBotThreshold();
        int begin = (size)/2-1;
        for(int i = begin; i >= 0; i--) heapify(i);
    }//end buildHeap

    /**getters & setters**/
    public int size(){return size;}
    public int capacity(){return capacity;}
    public float topThreshold(){return topThreshold;}
    public float botThreshold(){return botThreshold;}
    @SuppressWarnings("unchecked")
    public T parent(int index){return (T) binHeap[(index-1)/2];}
    @SuppressWarnings("unchecked")
    public T right(int index){return (T) binHeap[2*index + 2];}
    @SuppressWarnings("unchecked")
    public T left(int index){return (T) binHeap[2*index + 1];}
    @SuppressWarnings("unchecked")
    public T root(){return (T) binHeap[0];}
    //return height of tree (ceiling of log2(capacity))
    public int height(){
        int h = (int) Math.ceil(Math.log10(capacity)/Math.log10(2));
        if(size/(float) capacity <= 0.50f) h--;
        return h;
    }//end getHeight
    public void setTopThreshold(float topThreshold){
        this.topThreshold = checkTopThreshold(topThreshold);
    }//end setThreshold

    /**Object methods**/
    @SuppressWarnings("unchecked")
    @Override
    public String toString(){
        StringBuilder display = new StringBuilder();
        int h = height();
        //loop through each  layer
        outer:for(int i = 1; i <= h; i++){
            int order = (int) Math.pow(2,i-1),tier = (int) Math.pow(2,h-i);
            display.append("\t".repeat(tier-1));
            //loop through each number at each height
            for(int j = 0; j < order; j++){
                String e;
                T element = (T) binHeap[order+j-1];
                if(element != null) e = String.valueOf(element);
                else break outer;
                display.append(String.format("%-"+ 8*tier +"s",e));
            }//end for loop
            //if next element is null, entire heap is already displayed
            if(binHeap[2*order-1] == null) break;
            display.append("\n");
        }//end for loop
        return display.toString();
    }//end toString
    //check whether Object o is equal to this MyPQ
    @Override
    public boolean equals(final Object o){
        if(this == o) return true;
        if(o == null) return false;
        if(getClass() != o.getClass()) return false;
        return Arrays.equals(this.binHeap,((MyPQ<?>) o).toArray());
    }//end equals

    /**Comparable Methods**/
    @Override
    public int compareTo(MyPQ<T> o){
        //we will only compare the root
        T a = this.root(), b = o.root();
        return compare(a,b);
    }//end compareTo
}//end MyPQ