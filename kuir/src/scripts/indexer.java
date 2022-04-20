package scripts;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.StringTokenizer;

public class indexer {
    String filedir;
    public indexer(String path){ this.filedir = path;}
    public void Hashmap() throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = docFactory.newDocumentBuilder();
        Document document = builder.parse(filedir);

        NodeList nodeList = document.getElementsByTagName("doc");
        String[] set;
        HashMap<String, String> hashMap = new HashMap<String, String>();
        ArrayList<String> arrayList = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            set = (node.getLastChild().getTextContent().split("#"));
            //StringTokenizer set = new StringTokenizer(node.getLastChild().getTextContent(),"#");
            /*while(set.hasMoreTokens()){
                System.out.println(set.nextToken());
            }*/

            for (String wordset : set) {
                String[] word = wordset.split(":");
                for (int k = 0; k < word.length-1; k += 2) {
                    arrayList.add(word[k] + ";" + i + ";" + word[k + 1]);
                }
            }
        }
        for (String item : arrayList) {
            String[] temp = item.split(";");
            if (hashMap.containsKey(temp[0])) {
                String origin = hashMap.get(temp[0]).toString();
                String new_value = origin + ";" + temp[1] + " " + temp[2];
                hashMap.put(temp[0], new_value);
            } else {
                hashMap.put(temp[0], temp[1] + " " + temp[2]);
            }
        }
        HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

        for (String key : hashMap.keySet()) {
            String value = hashMap.get(key);
            System.out.println(key + " = " + value);

            String[] valueSet = value.split(";");

            for (String s : valueSet) {
                double id = Double.parseDouble(s.substring(0, 1));
                double tf = Double.parseDouble(s.substring(2));
                double df = (double) valueSet.length;
                double n = 5.0;
                double weight = tf * (Math.log(n / df));
                if (map.containsKey(key)) {
                    String s1 = map.get(key).toString().replace("[", "").replace("]", "");
                    String s2 = s1 + " " + Math.round(id) + " " + String.format("%.2f", weight);
                    ArrayList<String> list = new ArrayList<>();
                    list.add(s2);
                    map.put(key, list);
                } else {
                    String s1 = Math.round(id) + " " + String.format("%.2f", weight);
                    ArrayList<String> list = new ArrayList<>();
                    list.add(s1);
                    map.put(key, list);
                }
            }
        }

        FileOutputStream fileOutputStream = new FileOutputStream("./index.post");

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

        objectOutputStream.writeObject(map);

        objectOutputStream.close();
    }

    public void read() throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("./index.post");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        Object object = objectInputStream.readObject();
        objectInputStream.close();

        HashMap<String, ArrayList<String>> hashMap = (HashMap) object;

        for (String key : hashMap.keySet()) {
            ArrayList<String> value = hashMap.get(key);
            System.out.println(key + " -> " + value);
        }
    }
}