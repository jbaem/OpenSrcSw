package scripts;
import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

//import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
//import java.io.File;
import java.io.FileOutputStream;

public class makeCollection {

    String fileDir;
    public makeCollection(String fileDir){
        this.fileDir = fileDir;
    }

    public File[] fileInFolder(){
        File dir = new File(this.fileDir);
        File [] files = dir.listFiles();

        /*for(int i = 0; i < files.length; i++){
            System.out.println("file : " + files[i]);
        }*/
        return files;
    }

    public String[] getTag(File fileName) throws IOException {

        String [] strArr = new String[2];

        try{
                org.jsoup.nodes.Document doc = Jsoup.parse(fileName, "UTF-8");
                Elements p = doc.select("p");
                Elements title = doc.select("title");
                String strP = p.text();
                String strTitle = title.text();
                strArr[0] = strTitle;
                strArr[1] = strP;

        }catch(Exception e){
            System.out.println(e);
        }
        return strArr;
    }

    public void html2XML() throws ParserConfigurationException {

        //System.out.println("XML파일을 작성합니다.");
        File[] htmls = fileInFolder();
        int numFile = htmls.length;

        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            org.w3c.dom.Document document = db.newDocument();
            Element docs = document.createElement("docs");
            document.appendChild(docs);
            for(int i = 0; i < numFile; i++){
                String num = Integer.toString(i);

                String [] strArray = getTag(htmls[i]);
                String getTitle = strArray[0];
                String getP = strArray[1];

                /*System.out.println("title: " +  getTitle);
                System.out.println("p : "+ getP);*/

                Element doc = document.createElement("doc");
                Element title = document.createElement("title");
                Element body = document.createElement("body");
                docs.appendChild(doc);
                doc.setAttribute("id", num);
                doc.appendChild(title);
                title.appendChild(document.createTextNode(getTitle));
                doc.appendChild(body);
                body.appendChild(document.createTextNode(getP));
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DOMSource src = new DOMSource(document);
            StreamResult result = new StreamResult(new FileOutputStream(new File("./data/collection.xml")));

            transformer.transform(src, result);

            //System.out.println("XML파일 작성을 종료합니다.");

        }catch (Exception e){
            System.out.println(e);
        }

    }

}