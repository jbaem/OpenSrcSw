package scripts;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;

public class indexer {

    String xmlRoute;
    private ArrayList <ArrayList<String>> keywordAndTF = new ArrayList(); // 키워드 & 가중치 데이터
    private ArrayList <ArrayList<String>> outerData = new ArrayList(); // 이름, 빈도 수 데이터

    public indexer(String xmlRoute){
        this.xmlRoute = xmlRoute;
        cutBody();
    }

    public String[][] getTagsFromXml(){
        String [][] idAndTags = new String[5][2];
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document document = documentBuilder.parse(this.xmlRoute);

            Element doc = document.getDocumentElement();

            NodeList children = doc.getChildNodes();
            int docId = 0;
            for(int i = 0; i < children.getLength(); i++){
                Node node = children.item(i);
                NodeList docChild = node.getChildNodes();

                for(int j = 0; j < docChild.getLength(); j++){
                    Node docChildNode = docChild.item(j);
                    if(docChildNode.getNodeType() == Node.ELEMENT_NODE){
                        Element ele = (Element)docChildNode;
                        String nodeName = ele.getNodeName();
                        if(nodeName.equals("body")){
                            idAndTags[docId][0] = Integer.toString(docId);
                            idAndTags[docId][1] = ele.getTextContent();
                            docId++;
                        }else{
                            continue;
                        }
                    }
                }
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return idAndTags;
    }

    public void cutBody(){
        String [][] matrix = getTagsFromXml();
        String [][] cutStr = new String[5][];

        String [] dataSave = new String[2];

        for(int i = 0; i < matrix.length; i++){
                cutStr[i] = matrix[i][1].split("#");
        }

        for(int i = 0; i < cutStr.length; i++){
            ArrayList innerData = new ArrayList<String>();
            for(int j = 0; j < cutStr[i].length; j++){
                dataSave = cutStr[i][j].split(":");
                innerData.add(dataSave[0]);
                innerData.add(dataSave[1]);
            }
            this.outerData.add(innerData);
        }

        for(int i = 0; i < outerData.size(); i++){
            for(int j = 0; j < outerData.get(i).size(); j++){
                if(j % 2 == 0){
                    if(!keywordAndTF.contains(outerData.get(i).get(j))){
                        this.keywordAndTF.add(calculate4HashMap(outerData.get(i).get(j)));
                    }
                }else{
                    continue;
                }
            }
        }
    }

    public ArrayList<String> calculate4HashMap(String keyword){
        ArrayList <String> calTf = new ArrayList<>();
        calTf.add(keyword);
        int docNum = 0;
        int  [] often = new int[5];
        double result;
        String strResult;

        for(int i = 0; i < this.outerData.size(); i++){
            if (this.outerData.get(i).contains(keyword)){
                for(int j = 0; j < this.outerData.get(i).size(); j++){
                    if(j % 2 == 0){
                        if (this.outerData.get(i).get(j).equals(keyword)){
                            docNum++;
                            often[i] = Integer.parseInt(this.outerData.get(i).get(j+1));
                        }
                    }else{
                        continue;
                    }
                }
            }else{
                continue;
            }
        }
        for(int i = 0; i < often.length; i++){
            if(docNum != 0){
                result = Math.round(often[i] * Math.log((double)5/(double)docNum) * 100) / 100.0;
                strResult = i + ", "+ result;
                calTf.add(strResult);
            }
        }
        return calTf;
    }

    public void input2HashMap() throws IOException {
        HashSet<ArrayList<String>> erase = new HashSet<>(this.keywordAndTF);
        ArrayList <ArrayList<String>> finalKeywordAndTF = new ArrayList(erase);

        FileOutputStream fileStream = new FileOutputStream("./data/index.post");

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileStream);

        HashMap index = new HashMap();
        for(int i = 0; i < finalKeywordAndTF.size(); i++){
            ArrayList <String> keySet = new ArrayList<>();
            for(int j = 0; j < 5; j++){
                    keySet.add(finalKeywordAndTF.get(i).get(j+1));
            }
            index.put(finalKeywordAndTF.get(i).get(0), keySet);
        }

        objectOutputStream.writeObject(index);
        objectOutputStream.close();
    }

    public void printIndex(String route) throws IOException, ClassNotFoundException {
        FileInputStream fileStream = new FileInputStream(route);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileStream);

        Object object = objectInputStream.readObject();
        objectInputStream.close();

        HashMap hashMap = (HashMap)object;
        Iterator<String> it = hashMap.keySet().iterator();

        while(it.hasNext()){
            String key = it.next();
            ArrayList value = (ArrayList) hashMap.get(key);
            System.out.println(key + " → " + value);
        }
    }

}