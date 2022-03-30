package scripts;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class makeKeyword {
    String fileRoute;

    makeKeyword(String fileRoute){
        this.fileRoute = fileRoute;
    }

    public String [][] parseXML() throws IOException, SAXException, ParserConfigurationException {
        String [][] getTags = new String[5][2];

        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document document = documentBuilder.parse(this.fileRoute);

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
                        //System.out.println("node name : " + nodeName);
                        if(nodeName.equals("title")){
                            getTags[docId][0] = ele.getTextContent();
                        }else if(nodeName.equals("body")){
                            getTags[docId][1] = ele.getTextContent();
                            docId++;
                        }else{
                            continue;
                        }
                    }
                }
            }

            /*for(int i = 0; i < getTags.length; i++){
                for(int j = 0; j < getTags[0].length; j++){
                    System.out.print(getTags[i][j] + " ");
                }
                System.out.println();
            }*/

        }catch(Exception e){
            System.out.println(e);
        }
        return getTags;
    }

    public void makeIndexXml() throws ParserConfigurationException, SAXException, IOException {

        String [][] getTags = changeWithKkma(parseXML());

        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            org.w3c.dom.Document document = db.newDocument();
            Element docs = document.createElement("docs");
            document.appendChild(docs);
            for(int i = 0; i < getTags.length; i++){
                String num = Integer.toString(i);

                String getTitle = getTags[i][0];
                String getBody = getTags[i][1];

                //System.out.println("title: " +  getTitle);
                //System.out.println("p : "+ getBody);

                Element doc = document.createElement("doc");
                Element title = document.createElement("title");
                Element body = document.createElement("body");
                docs.appendChild(doc);
                doc.setAttribute("id", num);
                doc.appendChild(title);
                title.appendChild(document.createTextNode(getTitle));
                doc.appendChild(body);
                body.appendChild(document.createTextNode(getBody));
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DOMSource src = new DOMSource(document);
            StreamResult result = new StreamResult(new FileOutputStream(new File("./data/index.xml")));

            transformer.transform(src, result);

            //System.out.println("XML파일 작성을 종료합니다.");

        }catch (Exception e){
            System.out.println(e);
        }
    }
    public String [][] changeWithKkma(String [][] text){
        String [][] kkmaUsedText = new String[text.length][text[0].length];
        for(int i = 0; i < text.length; i++){
            for(int j = 0; j < text[0].length; j++){
                if(j == text[0].length - 1){
                    kkmaUsedText[i][j] = useKkma(text[i][j]);
                }else{
                    kkmaUsedText[i][j]= text[i][j];
                }
            }
        }
        return kkmaUsedText;
    }

    public String useKkma(String text){

        String changedBody = "";

        KeywordExtractor ke = new KeywordExtractor();
        KeywordList kl = ke.extractKeyword(text, true);

        for(int i = 0; i < kl.size(); i++){
            Keyword kwrd = kl.get(i);
            if (i != kl.size() - 1){
                changedBody = changedBody + kwrd.getString() + ":" + kwrd.getCnt() + "#";
            }else{
                changedBody = changedBody + kwrd.getString() +":" + kwrd.getCnt();
            }
        }
        return changedBody;
    }
}