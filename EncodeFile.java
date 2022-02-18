import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JOptionPane;

class Node implements Comparable<Node> {
    char data;
    int freq;
    Node left, right;

    Node() {
    }

    Node(char data, int frequency) {
        this.data = data;
        this.freq = frequency;
    }

    @Override
    public int compareTo(Node node) {
        return freq - node.freq;
    }
}

public class EncodeFile {

    private static Scanner scan = new Scanner(System.in);
    private static Map<Character, String> prefixCodeTable = new HashMap<>();
    public static int bitwight = 0;
    public static FileOutputStream fos;
    public static String outPutFileName;

    EncodeFile(String filename) throws IOException {

        try {
            File file = new File(filename);
            String data = readDoc(file);

            System.out.println(data);

            outPutFileName = filename.substring(0, filename.length() - 4);
            fos = new FileOutputStream(outPutFileName + ".zip301");

            String code = encoding(data);
            String division = "*****" + "\n";
            String bits = String.valueOf(bitwight) + "\n";

            fos.write(division.getBytes());
            fos.write(bits.getBytes());
            for (int i = 0; i < code.length(); i += 8) {
                String singleByte = code.substring(i, i + 8);
                int value = Integer.parseInt(singleByte, 2);
                fos.write(value);
            }
            fos.close();

        } catch (IOException e) {

        } catch (NumberFormatException ef) {

        } catch (StringIndexOutOfBoundsException str) {

        }

        System.out.println("*** Huffman Zip - TEAM REFERENCE IMPLEMENTATION ***");
        System.out.println("Wrote output to: " + outPutFileName.substring(7, outPutFileName.length()) + ".zip301");
        System.out.println("*** ALL PROCESSES ARE DONE ***");

    }

    public static void main(String[] args) throws IOException {
        String filename = JOptionPane.showInputDialog("input your fileName.");
        new EncodeFile("testSamples/" + filename);
    }

    public static String encoding(String data) throws IOException {

        Map<Character, Integer> charFreq = new HashMap<>();
        for (char c : data.toCharArray()) {
            if (!charFreq.containsKey(c)) {
                charFreq.put(c, 1);
            } else {
                int no = charFreq.get(c);
                charFreq.put(c, ++no);
            }
        }

        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
        Set<Character> ketSet = charFreq.keySet();
        for (char c : ketSet) {
            Node node = new Node(c, charFreq.get(c));
            priorityQueue.offer(node);
        }
        Node rootNode = buildTree(priorityQueue);
        setPrefixCode(rootNode, "");

        StringBuilder sb = new StringBuilder();
        for (char c : data.toCharArray()) {
            sb.append(prefixCodeTable.get(c));
        }

        return sb.toString();
    }

    public static Node buildTree(PriorityQueue<Node> priQue) {

        if (priQue.size() == 1) {
            return priQue.poll();
        } else {
            Node leftNode = priQue.poll();
            Node rightNode = priQue.poll();
            Node sumNode = new Node();

            if (leftNode != null && rightNode != null) {
                sumNode.freq = leftNode.freq + rightNode.freq;
                sumNode.left = leftNode;
                sumNode.right = rightNode;
            }
            priQue.offer(sumNode);

            return buildTree(priQue);
        }
    }

    public static void setPrefixCode(Node n, String code) throws IOException {

        if (n == null)
            return;

        if (n.left == null && n.right == null) {
            prefixCodeTable.put(n.data, code);

            if (n.data == '\n') {
                fos.write((code + " newline" + "\n").getBytes());
            } else if (n.data == ' ') {
                fos.write((code + " space" + "\n").getBytes());
            } else if (n.data == '\r') {
                fos.write((code + " return" + "\n").getBytes());
            } else if (n.data == '\t') {
                fos.write((code + " tab" + "\n").getBytes());
            } else {
                fos.write((code + " " + n.data + "\n").getBytes());
            }

            int bit = code.length() * n.freq;

        } else {
            setPrefixCode(n.left, code + '0');
            setPrefixCode(n.right, code + '1');
        }
    }

    public String readDoc(File f) {
        String text = "";
        int read, N = 1024 * 1024;
        char[] buffer = new char[N];

        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            while (true) {
                read = br.read(buffer, 0, N);
                text += new String(buffer, 0, read);

                if (read < N) {
                    break;
                }
            }

        } catch (Exception e) {
        }

        return text;
    }
}
