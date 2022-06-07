package HuffmanEncoding;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

/*
Created on 06/05/2022 by Kevin Galdamez
***************************************
This class test runs 950 huffman trees
using strings of length 1000 with 1-95
unique characters to determine the
efficiency of huffman encoding as opposed
to ASCII (UTF-8) encoding. The data is
represented in a bar graph, and the linear
regression is shown
*/

public class EncodingComparison{
    private final static HashMap<Integer,Integer> map = new HashMap<>();
    private final static Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    public static void main(String[] args) {
        map.put(1,1);
        for(int i = 1; i < 96; i++) map.put(i,0);
        for(int n = 0; n < 10; n++){
            for(int l = 2; l < 96; l++){
                char[] input = inputGen(l);
                HashMap<Character,Integer> characters = new HashMap<>();
                ArrayList<HuffmanTree> entries = new ArrayList<>();
                HuffmanTree huff = new HuffmanTree();
                HashMap<Character,String> huffmanCodes = new HashMap<>();
                for(char c: input){
                    if(characters.containsKey(c)) characters.put(c,characters.get(c)+1);
                    else characters.put(c,1);
                }//end for loop
                for(Character c : characters.keySet()){
                    double probability = Math.round((characters.get(c)/(double)input.length)*1000000.0)/1000000.0;
                    Element<Character,Double> e = new Element<>(c,probability);
                    entries.add( new HuffmanTree(new MyPQ<>(e, Comparator.reverseOrder())));
                }//end while loop
                huff.merge(entries);
                for(Character c : characters.keySet()) huffmanCodes.put(c,huff.getPath(c));
                int size = 0;
                for(Character c : characters.keySet()) size += huffmanCodes.get(c).length();
                map.put(l,size + map.get(l));
            }//end for loop
        }//end for loop
        for(int i = 1; i < 96; i++) map.put(i,(int) (map.get(i)/10.0));
        GraphFrame g = new GraphFrame("data");
        g.getContentPane().setBackground(new Color(165, 180, 190));
        g.setSize(d);
        g.setLocationRelativeTo(null);
        g.setVisible(true);
    }//end main

    public static char[] inputGen(int uniqueChars){
        char[] input = new char[1000];
        Random rand = new Random();
        ArrayList<Character> ch = new ArrayList<>();
        while(ch.size() < uniqueChars){
            char c = (char) rand.nextInt(32,127);
            if(!ch.contains(c)) ch.add(c);
        }//end while loop
        for(int i = 0; i < 1000; i++) input[i] = ch.get(rand.nextInt(uniqueChars));
        return input;
    }//end inputGen

    public static double[] linReg(HashMap<Integer,Integer> data){
        //x1,y1,x2,y2,slope,y-int
        double[] points = new double[6];
        double kAvg = 0,vAvg = 0;
        for(Integer k : data.keySet()) kAvg += k;
        for(Integer v : data.values()) vAvg += v;
        kAvg /= data.size();
        vAvg /= data.size();
        ArrayList<Double> kDev = new ArrayList<>();
        ArrayList<Double> vDev = new ArrayList<>();
        for(Integer k : data.keySet()) kDev.add(k-kAvg);
        for(Integer v : data.values()) vDev.add(v-vAvg);
        double slope = 0;
        for(int i = 0; i < data.size(); i++) slope += kDev.get(i)*vDev.get(i);
        double sum = 0;
        for(int j = 0; j < data.size(); j++) sum += kDev.get(j)*kDev.get(j);
        slope /= sum;
        double yIntercept = vAvg + slope*((kAvg > 0) ? kAvg*-1 : kAvg);
        points[0] = yIntercept/slope*-1;
        points[1] = 0.0;
        points[2] = 96.0;
        points[3] = 96.0*slope + yIntercept;
        points[4] = Math.round(slope*10000.0)/10000.0;
        points[5] = Math.round(yIntercept*10000.0)/10000.0;
        return points;
    }//end linReg

    public static class GraphFrame extends JFrame {
        public GraphFrame(String name){
            super(name);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setResizable(false);
            this.setFocusable(true);
        }//end constructor
        @Override
        public void paint(Graphics g){
            super.paint(g);
            g.setColor(Color.DARK_GRAY);
            g.fillRect(50,(int) d.getHeight()-50,(int) d.getWidth()-95,5);
            g.drawString("input size",(int) d.getWidth()/2,(int) d.getHeight()-10);
            g.drawString("Average Huffman Code Length",(int) d.getWidth()/2,60);
            g.fillRect(50,40,5,(int) d.getHeight()-90);
            g.drawString("c",10,(int) d.getHeight()/2 - 50);
            g.drawString("o",10,(int) d.getHeight()/2 - 40);
            g.drawString("d",10,(int) d.getHeight()/2 - 30);
            g.drawString("e",10,(int) d.getHeight()/2 - 20);
            g.drawString(" ",10,(int) d.getHeight()/2 - 10);
            g.drawString("l",10,(int) d.getHeight()/2);
            g.drawString("e",10,(int) d.getHeight()/2 + 10);
            g.drawString("n",10,(int) d.getHeight()/2 + 20);
            g.drawString("g",10,(int) d.getHeight()/2 + 30);
            g.drawString("t",10,(int) d.getHeight()/2 +40);
            g.drawString("h",10,(int) d.getHeight()/2 + 50);
            for(int i = 0; i < 96; i+=2){g.drawString(i+"",50+(i*15),(int) d.getHeight()-30);}
            for(int i = 0; i < 700; i+=20) g.drawString(i+"",20,((int) d.getHeight()-50)-(i/20)*25);
            for(int i = 1; i < 96; i++){
                int h = (int) (map.get(i)*1.25);
                g.setColor(Color.GRAY);
                g.fillRect(50+(i*15),(int) d.getHeight()-50-h,15,h);
                g.setColor(Color.DARK_GRAY);
                g.drawRect(50+(i*15),(int) d.getHeight()-50-h,15,h);
                char[] n = String.valueOf(map.get(i)).toCharArray();
                for(int j = 0; j < n.length; j++){
                    g.drawString(n[j]+"",50+(i*15),(int) d.getHeight()-50-h-40+(j*10));
                }//end for loop
            }//end for loop
            g.setColor(Color.RED);
            double[] line = linReg(map);
            g.drawLine((int)(50+(line[0])*15),(int)(d.getHeight()-50-(line[1]*1.25)),
                    (int)(50+(line[2])*15),(int)(d.getHeight()-50-(line[3]*1.25)));
            g.fillRect(90,100,10,10);
            g.drawString("Huffman Regression (y = " + line[4] + "x " + line[5] + ")" ,120,110);
            g.setColor(Color.BLUE);
            g.drawLine(50,(int) d.getHeight()-50,
                    50+(96*15),(int )(d.getHeight()-50-(8*96*1.25)));
            g.fillRect(90,140,10,10);
            g.drawString("ASCII Regression (y = 8x)",120,150);
            g.setColor(Color.DARK_GRAY);
            g.drawRect(90,100,10,10);
            g.drawRect(90,140,10,10);
        }//end paint
    }//end GraphFrame
}//end EncodingComparison
