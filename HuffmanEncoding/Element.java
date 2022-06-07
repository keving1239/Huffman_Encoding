package HuffmanEncoding;

/*
Created on 05/28/2022 by Kevin Galdamez
***************************************
This class is an object wrapper for the
use of the Huffman Tree and PQ classes.
 */

@SuppressWarnings("unused")
public class Element<T,V> implements Comparable<Element<T,V>>{
    /**attributes**/
    private T data;
    private V priority;

    /**constructors**/
    public Element(){this(null,null);}
    public Element(T data){this(data,null);}
    public Element(T data, V priority){
        this.data = data;
        this.priority = priority;
    }//end overridden constructor

    /**getters & setters**/
    public V priority(){return this.priority;}
    public T data(){return this.data;}
    public void setData(T data){this.data = data;}
    public void setPriority(V priority){this.priority = priority;}

    /**Comparable methods**/
    @SuppressWarnings("unchecked")
    @Override
    public int compareTo(Element o){
        //allows for Elements to be compared to Integers
        V a = this.priority, b = (V) o.priority();
        Comparable<? super V> c = (Comparable<? super V>) a;
        return c.compareTo(b);
    }//end compareTo

    /**Object methods**/
    @Override
    public String toString(){
        return data.toString() + ":" + priority.toString();
    }//end toString
    //check whether two elements are equal
    @Override
    public boolean equals(final Object o){
        if(this == o) return true;
        if(o == null) return false;
        if(getClass() != o.getClass()) return false;
        Element<?,?> e = (Element<?,?>) o;
        return this.data.equals(e.data()) && this.priority.equals(e.priority());
    }//end equals
}//end Element