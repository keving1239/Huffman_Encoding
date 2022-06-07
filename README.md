# Huffman_Encoding
A personal exploration and implementation in the method of variable length encoding, specifically using a Huffman Tree.

MyPQ is an implmentation of a priority queue using a heap. This class is general, but also tailored for the use of this project.
Element is a general wrapper class for two objects. it is used by MyPQ and HuffmanTree.
HuffmanTree is the implementation of HuffmanTree encoding, implementing a linked list of MyPQ's.
DataCompression is the GUI that accepts user input as text or a .txt file and shows the comparison between Huffman and ASCII.
Compression Driver is the main file which is used to run DataCompression.
EncodingComparison is a data viewing class. It simulates the length of huffman codes for various random inputs. Note that the 
data is not displayed in the fanciest way, and the code is harshly written. This class is not thoroughly written but exists only
for general testing.

Upon implementing and testing this code, I have concluded a few notes:
1. Huffman Encoding is extremely efficient in the case that the number of characters is relatively small, and the size of the text is very large. (i.e. DNA sequencing)
2. Upoon reaching roughly 65 unique characters in a text, Huffman encoding saves roughly 50% memory. More characters results in less memory save and more processing.
3. The processing cost of Huffman Encoding is very high. in my personal opinion, it should only be used in the event of a case 1 data set.

10110101111100100011011110011101011000110010101000
(Kevin Galdamez)
  = 1101
a = 011
d = 1100
e = 010
v = 1111
G = 1110
i = 1001
z = 1000
K = 1011
l = 1010
m = 001
n = 000
