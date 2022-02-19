import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.swing.JOptionPane;

public class DecodeFile {
    private Map<Character, String> prefixCodeTale;
    private List<String> dataList;

    DecodeFile() {
        prefixCodeTale = new HashMap<>();
        dataList = new ArrayList<>();

    }

    public static void main(String[] args) {

        try {
            DecodeFile df = new DecodeFile();
            String input = JOptionPane.showInputDialog("Please input your file for decoding.");
            String filename = "testSamples/" + input;
            String DecodedFile = filename.substring(0, filename.length() - 7);

            FileInputStream fis = new FileInputStream(new File(filename));
            FileOutputStream fos = new FileOutputStream(DecodedFile + "2.txt");

            int read = 0;
            String codes = "";
            while ((read = fis.read()) != -1) {
                codes += (char) read;

                if (codes.contains("*****")) {
                    break;
                }
            }

            String[] datas = codes.split("\n");
            int num = datas.length;

            for (int i = 0; i < num - 1; i++) {
                String str;
                String nl;
                if (datas[i].contains("space") && datas[i].contains(" ")) {
                    str = datas[i].replaceAll(" ", "|");
                    nl = str.replaceAll("space", " ");
                    df.dataList.add(nl);
                } else if (datas[i].contains("newline") && datas[i].contains(" ")) {
                    str = datas[i].replaceAll(" ", "|");
                    nl = str.replaceAll("newline", "\n");
                    df.dataList.add(nl);
                } else if (datas[i].contains("return") && datas[i].contains(" ")) {
                    str = datas[i].replaceAll(" ", "|");
                    nl = str.replaceAll("return", "\r");
                    df.dataList.add(nl);
                } else if (datas[i].contains("tab") && datas[i].contains(" ")) {
                    str = datas[i].replaceAll(" ", "|");
                    nl = str.replaceAll("tab", "\t");
                    df.dataList.add(nl);
                } else {
                    str = datas[i].replaceAll(" ", "|");
                    df.dataList.add(str);
                }
            }

            int size = df.dataList.size();
            for (int i = 0; i < size; i++) {
                String[] codeSet = df.dataList.get(i).split("\\|");
                String value = codeSet[0];
                char key = codeSet[1].charAt(0);
                df.prefixCodeTale.put(key, value);
            }

            List<Integer> rawCodeList = new ArrayList<>();
            List<Integer> codeList = new ArrayList<>();

            String s = "";
            while ((read = fis.read()) != -1) {
                int b = read;
                rawCodeList.add(b);
            }

            int RC_size = rawCodeList.size();

            for (int i = 1; i < RC_size; i++) {
                if (rawCodeList.get(i) == 10) {
                    int startIndex = i + 1;
                    for (int j = startIndex; j < RC_size; j++) {
                        codeList.add(rawCodeList.get(j));
                    }
                    break;
                }
            }

            String codeBuffer = "";
            for (int i = 0; i < codeList.size(); i++) {
                String bin = Integer.toBinaryString(codeList.get(i));
                int numZero = 8 - bin.length();
                String padding = "0".repeat(numZero);
                bin = padding + bin;
                codeBuffer += bin;
            }

            String sentences = df.decoding(codeBuffer);

            fos.write(sentences.getBytes());

            fis.close();

            // System.out.println("Output File Name: "+DecodedFile.substring(7,
            // DecodedFile.length())+"2.txt");
            System.out.println("*** All processes are DONE ***");

        } catch (IOException e) {
            System.out.println(e);
        }

    }

    public String decoding(String data) {

        StringBuilder sb = new StringBuilder();
        String buffer = "";
        for (char c : data.toCharArray()) {
            buffer += c;

            if (prefixCodeTale.containsValue(buffer)) {
                Stream<Character> keyStream = getKeysByValue(prefixCodeTale, buffer);
                char key = keyStream.findFirst().get();
                sb.append(key);
                buffer = "";
            }
        }

        return sb.toString();
    }

    public static <K, V> Stream<K> getKeysByValue(Map<K, V> map, V value) {
        return map.entrySet().stream().filter(entry -> value.equals(entry.getValue())).map(Map.Entry::getKey);
    }

}
