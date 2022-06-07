package HuffmanEncoding;
import java.util.*;

/*
Created on 06/01/2022 by Kevin Galdamez
***************************************
This class is an implementation of huffman
encoding built upon a linked list of priority
queues. Each queue is of length 1 or 3, constituting
a subtree. The left path represents a 0, the right
 represents a 1.
 */

@SuppressWarnings("unused")
public class HuffmanTree{
    /**attributes**/
    private ArrayList<MyPQ<Element<Character,Double>>> tree;
    private final Comparator<Element<Character,Double>> ORDER = Comparator.reverseOrder();

    /**constructors**/
    public HuffmanTree(){this(null);}
    public HuffmanTree(MyPQ<Element<Character,Double>> root){
        this.tree = new ArrayList<>();
        if(root != null) tree.add(root);
    }//end constructor

    /**public methods**/
    //merges array list of huffman trees into one large tree
    public void merge(ArrayList<HuffmanTree> input){
        if(isValid(this)) input.add(this);
        while(input.size() > 1){
            HuffmanTree a = findMin(input), b = findMin(input);
            if(a.getSize() > b.getSize()) a.add(b);
            else b.add(a);
            input.add((a.getSize() > 0) ? a : b);
        }//end while loop
        tree.clear();
        this.add(input.remove(0));
    }//end merge
    //adds two huffman trees together
    public void add(HuffmanTree huff){
        if(!isValid(huff)) return;
        if(isValid(this)){
            //grow the tree, storing old tree to the right
            double sum = Math.round((getHead().root().priority()+huff.getHead().root().priority())*1000000.0)/1000000.0;
            huff.getTree().add(new MyPQ<>(new Element<>('\b',sum),ORDER));
            huff.getTree().addAll(tree);
        }//end else statement
        tree = huff.getTree();
    }//end add
    //returns the huffman tree whose root's priority is smallest in a linked list
    public HuffmanTree findMin(ArrayList<HuffmanTree> input){
        HuffmanTree huff = new HuffmanTree(new MyPQ<>(new Element<>('\b',Double.MAX_VALUE)));
        for (HuffmanTree h : input) if (compare(huff,h) >= 0) huff = h;
        return input.remove(huff) ? huff : new HuffmanTree();
    }//end findMin

    //return path from root to Character c where 0 = left, 1 = right
    public String getPath(Character c){
        //recursively search the tree until it holds 1 PQ with 1 element
        int i = indexOf(c),r = rootIndex();
        if(i == r) return getHead().root().data() == c ? "" : (getHead().left(0).data() == c ? "0" : "1");
        return (i < r) ? "0"+leftTree().getPath(c) : "1"+rightTree().getPath(c);
    }//end getPath
    //compare the roots of two Huffman Trees
    public int compare(HuffmanTree first,HuffmanTree second){
        return first.getHead().compareTo(second.getHead());
    }//end compare

    /**private methods**/
    //asserts that there is at least one element in huff, false if tree is empty
    private boolean isValid(HuffmanTree h){
        if(h == null) return false;
        if(h.getTree() == null) return false;
        if(h.getTree().size() == 0) return false;
        return h.getTree().get(0).root() != null;
    }//end isValidate
    //returns the index of the root;
    private int rootIndex(){return tree.indexOf(getHead());}
    //return the Huffman Tree representing the left child of the root
    public HuffmanTree leftTree(){
        HuffmanTree huff = new HuffmanTree();
        for(int i = 0; i < rootIndex(); i++) huff.getTree().add(tree.get(i));
        return huff;
    }//end leftTree
    //return the Huffman Tree representing the right child of the root
    public HuffmanTree rightTree(){
        HuffmanTree huff = new HuffmanTree();
        for(int i = rootIndex()+1; i < tree.size(); i++) huff.getTree().add(tree.get(i));
        return huff;
    }//end leftTree
    //return the index of a given character
    private int indexOf(Character c){
        //search each PQ
        for(int i = 0; i < tree.size(); i++){
            if(tree.get(i).root().data() == c) return i;
            if(tree.get(i).size() > 1 && tree.get(i).left(0).data() == c) return i;
            if(tree.get(i).size() > 2 && tree.get(i).right(0).data() == c) return i;
        }//end for loop
        return -1;
    }//end indexOf

    /**getters & setters**/
    //returns the total size of the tree (size of all PQs in tree)
    public int getSize(){
        int size = 0;
        for (MyPQ<Element<Character, Double>> pq : tree) size += pq.size();
        return size;
    }//end get size
    //returns the array list that represents the tree
    public ArrayList<MyPQ<Element<Character,Double>>> getTree(){return tree;}
    //returns the PQ in the linked list with the largest root
    public MyPQ<Element<Character,Double>> getHead(){
        //find the PQ with the largest root in the tree
        MyPQ<Element<Character,Double>> pq = new MyPQ<>(new Element<>('\b',Double.MIN_VALUE));
        for (MyPQ<Element<Character, Double>> t : tree) if(t.compareTo(pq) > 0) pq = t;
        return pq.root().priority() >= 0 ? pq : null;
    }//end getHead

    /**Object methods**/
    @Override
    public String toString(){return tree.toString();}
    //check if two huffman trees are equal
    @Override
    public boolean equals(final Object o){
        if(this == o) return true;
        if(o == null) return false;
        if(getClass() != o.getClass()) return false;
        return tree.equals(((HuffmanTree) o).getTree());
    }//end equals
}//end HuffmanTree