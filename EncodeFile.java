import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
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

    private Map<Character, String> prefixCodeTable;
    public int bitwight;
    public FileOutputStream encodedFile;

    EncodeFile() {
        prefixCodeTable = new HashMap<>();
        bitwight = 0;
    }

    public static void main(String[] args) {

        try {
            long start = System.currentTimeMillis();

            EncodeFile ef = new EncodeFile();
            String inputData = JOptionPane.showInputDialog("input your fileName.");
            String filename = "testSamples/" + inputData;
            File file = new File(filename);
            double preFileSize = file.length();
            String txtData = ef.readDoc(file);

            String encodedFileName = filename.substring(12, filename.length() - 4);
            ef.encodedFile = new FileOutputStream("testSamples/" + encodedFileName + ".zipped");

            String code = ef.encoding(txtData);
            String division = "*****" + "\n";
            String bits = String.valueOf(ef.bitwight) + "\n";

            ef.encodedFile.write(division.getBytes());
            ef.encodedFile.write(bits.getBytes());

            int num = code.length() % 8;
            int size = code.length();

            if (num != 0) {
                int numZero = 8 - num;
                String padding = "0".repeat(numZero);
                String original = code.substring(0, size - num);
                String bin = code.substring(size - num, size);

                bin = padding + bin;
                code = original + bin;
            }

            for (int i = 0; i < code.length(); i += 8) {
                String singleByte = code.substring(i, i + 8);
                int value = Integer.parseInt(singleByte, 2);
                ef.encodedFile.write(value);
            }

            ef.encodedFile.close();

            long end = System.currentTimeMillis();

            long processTime = end - start;

            File postFile = new File("testSamples/" + encodedFileName + ".zipped");
            double encodedFileSize = postFile.length();

            System.out.println(encodedFileName + ".zipped created.");
            System.out.println("**********Processe Analysis**********");
            System.out.println("Original File Size: " + preFileSize + " bytes.");
            System.out.println("Encoded File Size: " + encodedFileSize + " bytes.");
            System.out.println("All process takes " + processTime + " sec.");
            System.out.println("*** All processes are DONE ***");

        } catch (IOException e) {
            System.out.println(e);

        }
    }

    public String encoding(String data) throws IOException {

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

    public Node buildTree(PriorityQueue<Node> priQue) {

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

    public void setPrefixCode(Node n, String code) throws IOException {

        if (n == null)
            return;

        if (n.left == null && n.right == null) {
            prefixCodeTable.put(n.data, code);

            if (n.data == '\n') {
                encodedFile.write((code + " newline" + "\n").getBytes());
            } else if (n.data == ' ') {
                encodedFile.write((code + " space" + "\n").getBytes());
            } else if (n.data == '\r') {
                encodedFile.write((code + " return" + "\n").getBytes());
            } else if (n.data == '\t') {
                encodedFile.write((code + " tab" + "\n").getBytes());
            } else {
                encodedFile.write((code + " " + n.data + "\n").getBytes());
            }

            int bit = code.length() * n.freq;
            bitwight += bit;

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
