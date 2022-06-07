package HuffmanEncoding;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

/*
Created on 06/03/2022 by Kevin Galdamez
***************************************
This class is the GUI implementation for
the comparison of Huffman & ASCII (UTF-8)
encoding memory efficiency.
*/

@SuppressWarnings("unused")
public class DataCompression{
    /**attributes**/
    private final JFrame display = new JFrame("Data Compression");
    private final JTextArea inputField = new JTextArea(),huffmanField = new JTextArea(),
            asciiField = new JTextArea(),comparisons = new JTextArea();
    private final JButton convertButton = new JButton("Convert"),fileButton = new JButton("Add File"),
            mapButton = new JButton("Show Code Maps"),addButton = new JButton("Add"),
            browseButton = new JButton("Browse"),backButton = new JButton("Back");
    private final JLabel fileDrop = new JLabel("Drop a text (.txt) file here",JLabel.CENTER);
    private final JTextField filePath = new JTextField();
    private final Color TEXT_COLOR = new Color(5, 15, 65);
    private final Font TEXT_FONT = new Font("Lucida Console", Font.BOLD,12);
    private final HashMap<Character,String> asciiCodes = new HashMap<>();
    private HashMap<Character,String> huffmanCodes = new HashMap<>();

    /**constructor**/
    public DataCompression(){
        setComponents();
        createASCIIMap();
        kickoff();
    }//end constructor

    /**listeners**/
    public class ConvertListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            if(inputField.getText().isEmpty() || inputField.getForeground() != TEXT_COLOR) return;
            huffmanCompression();
            asciiCompression();
            showComparison();
        }//end actionPerformed
    }//end ConvertListener
    public class FileListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){fileDisplay();}
    }//end FileListener
    public class AddListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            if(filePath.getText().isEmpty()) return;
            parseFile(filePath.getText());
            createDisplay();
        }//end actionPerformed
    }//end AddListener
    public class BrowseListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            //open file explorer
            filePath.setText("");
            try {Process builder = Runtime.getRuntime().exec("cmd /c start C:");}
            catch (IOException i){System.err.println(i.getMessage());}
        }//end actionPerformed
    }//end BrowseListener
    public class BackListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            //return to main display
            createDisplay();
            fileDrop.setBackground(Color.WHITE);
            filePath.setText("");
        }//end actionPerformed
    }//end AddListener
    public class CodeMapListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){showCodeMaps();}
    }//end CodeMapListener
    public class InputListener implements FocusListener{
        @Override
        public void focusGained(FocusEvent e){hideHint();}
        @Override
        public void focusLost(FocusEvent e){showHint();}
    }//end InputListener
    public class DropListener implements DropTargetListener{
        @Override
        public void dragEnter(DropTargetDragEvent e){fileDrop.setBackground(new Color(165, 205, 210));}
        @Override
        public void dragOver(DropTargetDragEvent e){/*do nothing*/}
        @Override
        public void dropActionChanged(DropTargetDragEvent e){/*do nothing*/}
        @Override
        public void dragExit(DropTargetEvent e){fileDrop.setBackground(Color.WHITE);}
        @SuppressWarnings("unchecked")
        @Override
        public void drop(DropTargetDropEvent e){
            e.acceptDrop(DnDConstants.ACTION_COPY);
            Transferable transfer = e.getTransferable();
            DataFlavor[] data = transfer.getTransferDataFlavors();
            List<File> files = new ArrayList<>();
            for(DataFlavor d : data){
                if(!d.isFlavorJavaFileListType()) continue;
                try {files = (List<File>) transfer.getTransferData(d);}
                catch (IOException | UnsupportedFlavorException i){JOptionPane.showMessageDialog(display, i.getMessage());}
            }//end for loop
            if(files.size() > 1){
                JOptionPane.showMessageDialog(display, "Please enter only 1 file");
                fileDrop.setBackground(Color.WHITE);
                return;
            }//end if statement
            String path = files.get(0).getPath();
            if(!path.endsWith(".txt")){
                JOptionPane.showMessageDialog(display, "Please enter a .txt file");
                fileDrop.setBackground(Color.WHITE);
                return;
            }//end if statement
            parseFile(path);
            fileDrop.setBackground(Color.WHITE);
            createDisplay();
        }//end drop
    }//end DropListener

    /**public methods**/
    //start the GUI
    public void kickoff(){SwingUtilities.invokeLater(this::createDisplay);}
    //combine all the components into a single GUI
    public void createDisplay(){
        //display panels
        display.getContentPane().removeAll();
        JPanel p1 = new JPanel(new GridLayout(1,1));
        p1.add(new JScrollPane(inputField));
        JPanel p2 = new JPanel(new GridLayout(1,2));
        p2.add(new JLabel("Huffman Encoding",JLabel.CENTER));
        p2.add(new JLabel("ASCII (UTF-8) Encoding",JLabel.CENTER));
        p2.setOpaque(false);
        JPanel p3 = new JPanel(new GridLayout(1,2));
        p3.add(new JScrollPane(huffmanField));
        p3.add(new JScrollPane(asciiField));
        JPanel p4 = new JPanel(new GridLayout(1,2,20,0));
        p4.add(new JScrollPane(comparisons));
        p4.add(mapButton);
        p4.setOpaque(false);
        JPanel p5 = new JPanel(new GridLayout(1,2,20,0));
        p5.add(fileButton);
        p5.add(convertButton);
        p5.setOpaque(false);
        //layout configurations
        GridBagConstraints c = new GridBagConstraints();
        c.ipadx = 680;
        c.ipady = 300;
        c.gridy = 0;
        display.add(p1,c);
        c.ipadx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 20;
        c.gridy = 1;
        display.add(p2,c);
        c.ipady = 250;
        c.gridy = 2;
        display.add(p3,c);
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(10,1,5,0);
        c.anchor = GridBagConstraints.LAST_LINE_START;
        c.ipadx = 60;
        c.ipady = 20;
        c.gridy = 3;
        c.weightx = 1.0;
        display.add(p4,c);
        c.weightx = 0.5;
        c.insets = new Insets(10,0,5,1);
        c.gridx = 0;
        c.anchor = GridBagConstraints.LAST_LINE_END;
        c.gridy = 3;
        display.add(p5,c);
        //show display
        display.setLocationRelativeTo(null);
        display.validate();
        display.repaint();
        display.setVisible(true);
    }//end create Display
    //adjust component settings
    public void setComponents(){
        //display frame configurations
        display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        display.getContentPane().setBackground(new Color(165, 180, 190));
        display.setResizable(false);
        display.setFocusable(true);
        display.setSize(720,720);
        display.setLayout(new GridBagLayout());
        //text area configurations
        setLookAndFeel(new JComponent[]{inputField,huffmanField,asciiField,comparisons,fileDrop,filePath},true,Color.WHITE,TEXT_COLOR);
        inputField.addFocusListener(new InputListener());
        inputField.setText("Enter text here...");
        inputField.setForeground(Color.gray);
        inputField.setLineWrap(true);
        inputField.setEditable(true);
        inputField.setWrapStyleWord(true);
        //button configurations
        setLookAndFeel(new JComponent[]{convertButton,fileButton,mapButton,addButton,browseButton,backButton},false,null,Color.DARK_GRAY);
        convertButton.addActionListener(new ConvertListener());
        fileButton.addActionListener(new FileListener());
        mapButton.addActionListener(new CodeMapListener());
        addButton.addActionListener(new AddListener());
        browseButton.addActionListener(new BrowseListener());
        backButton.addActionListener(new BackListener());
    }//end setComponents
    //clear display and add file drop components
    public void fileDisplay(){
        display.getContentPane().removeAll();
        new DropTarget(fileDrop,new DropListener());
        //set layout
        GridBagConstraints c = new GridBagConstraints();
        c.ipady = 20;
        c.ipadx = 40;
        c.fill = GridBagConstraints.VERTICAL;
        c.anchor = GridBagConstraints.WEST;
        JPanel p1 = new JPanel(new GridBagLayout());
        p1.setOpaque(false);
        p1.add(new JLabel("File Path: "));
        c.ipadx = 600;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.CENTER;
        p1.add(filePath,c);
        c.ipadx = 30;
        c.weightx = 0;
        c.anchor = GridBagConstraints.EAST;
        p1.add(addButton,c);
        c.ipady = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        display.add(p1,c);
        JPanel p2 = new JPanel(new GridLayout(1,1));
        p2.add(fileDrop);
        c.weighty = 1.0;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        display.add(p2,c);
        JPanel p3 = new JPanel(new GridLayout(1,1));
        p3.setOpaque(false);
        p3.add(backButton);
        JPanel p4 = new JPanel(new GridLayout(1,1));
        p4.setOpaque(false);
        p4.add(browseButton);
        c.ipady = 20;
        c.insets = new Insets(5,1,5,1);
        c.gridy = 2;
        c.weighty = 0;
        c.weightx = 0.1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LAST_LINE_START;
        display.add(p3,c);
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LAST_LINE_END;
        display.add(p4,c);
        //show new display
        display.validate();
        display.repaint();
    }//end fileDisplay

    /**private methods**/
    private void setLookAndFeel(JComponent[] comps,boolean focus,Color bg,Color fg){
        //apply common themes to components
        for(JComponent c : comps){
            if(c.getClass().equals(huffmanField.getClass())){
                ((JTextArea) c).setLineWrap(true);
                ((JTextArea) c).setEditable(false);
            }//end if statement
            c.setFocusable(focus);
            c.setOpaque(true);
            c.setBackground(bg);
            c.setFont(TEXT_FONT);
            c.setForeground(fg);
            c.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY,2));
        }//end for loop
    }//end setLookAndFeel
    //search through specified file
    private void parseFile(String filename){
        File data = new File(filename);
        filePath.setText("");
        if(!data.exists()){
            JOptionPane.showMessageDialog(display,"File " + filename + " does not exist");
            return;
        }//end if statement
        try{
            FileInputStream reader = new FileInputStream(data);
            inputField.setText(new String(reader.readAllBytes()));
        }catch(IOException i){JOptionPane.showMessageDialog(display, i.getMessage());}
        //set the input and calculate codes
        inputField.setForeground(TEXT_COLOR);
        huffmanCompression();
        asciiCompression();
        showComparison();
    }//end parseFile
    //create and write huffman codes
    private void huffmanCompression(){
        char[] input = inputField.getText().toCharArray();
        HashMap<Character,Integer> characters = new HashMap<>();
        ArrayList<HuffmanTree> entries = new ArrayList<>();
        HuffmanTree huff = new HuffmanTree();
        huffmanCodes = new HashMap<>();
        //parse characters and create huffman codes
        for(char c: input){
            if(characters.containsKey(c)) characters.put(c,characters.get(c)+1);
            else characters.put(c,1);
        }//end for loop
        //huffman tree has no path if there is only 1 element
        if(characters.size() == 1){
            huffmanCodes.put(input[0],"0");
            huffmanField.setText("0".repeat(input.length));
            return;
        }//end if statement
        for(Character c : characters.keySet()){
            double probability = Math.round((characters.get(c)/(double)input.length)*1000000.0)/1000000.0;
            Element<Character,Double> e = new Element<>(c,probability);
            entries.add( new HuffmanTree(new MyPQ<>(e, Comparator.reverseOrder())));
        }//end while loop
        //write codes to huffmanField
        huff.merge(entries);
        StringBuilder code = new StringBuilder();
        for(Character c : characters.keySet()) huffmanCodes.put(c,huff.getPath(c));
        for(char c : input) code.append(huffmanCodes.get(c));
        huffmanField.setText(code.toString());
    }//end huffmanCompression
    //create and write ascii codes
    private void asciiCompression(){
        char[] input = inputField.getText().toCharArray();
        StringBuilder code = new StringBuilder();
        //parse input and write codes to asciiField
        for(char c : input) code.append(asciiCodes.get(c));
        asciiField.setText(code.toString());
    }//end asciiCompression
    //convert characters to binary via UTF-8
    private String toBinary(int c){
        StringBuilder binary = new StringBuilder();
        while(c > 0){
            binary.insert(0,c % 2);
            c /= 2;
        }//end while loop
        binary.insert(0,"0".repeat(8-binary.length()));
        return binary.toString();
    }//end toBinary
    //hide the "enter text hint" when user has typed
    private void hideHint(){
        if(inputField.getForeground() == TEXT_COLOR) return;
        inputField.setText("");
        inputField.setForeground(TEXT_COLOR);
    }//end hintText
    //show the "enter text hint" when input is empty
    private void showHint(){
        if(!inputField.getText().isEmpty()) return;
        inputField.setForeground(Color.GRAY);
        inputField.setText("Enter text here...");
    }//end showHint
    private void showComparison(){
        comparisons.setText("Huffman: " + huffmanField.getText().length()
                + "\nASCII:   " + asciiField.getText().length());
    }//end showComparison
    private void showCodeMaps(){
        StringBuilder code = new StringBuilder();
        for(Character c : huffmanCodes.keySet()) code.append(c).append(" = ").append(huffmanCodes.get(c)).append("\n");
        huffmanField.setText(code.toString());
        code = new StringBuilder();
        for(Character c : huffmanCodes.keySet()) code.append(c).append(" = ").append(asciiCodes.get(c)).append("\n");
        asciiField.setText(code.toString());
    }//end showCodeMaps
    //create the asciiCodes hashmap
    private void createASCIIMap(){
        for(int i = 0; i < 128; i++){
            StringBuilder binary = new StringBuilder();
            int c = i;
            while(c > 0){
                binary.insert(0,c % 2);
                c /= 2;
            }//end while loop
            binary.insert(0,"0".repeat(8-binary.length()));
            asciiCodes.put((char) i,binary.toString());
        }//end for loop
    }//end createASCIIMap
}//end Compression Driver