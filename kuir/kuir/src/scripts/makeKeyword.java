package scripts;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordList;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class makeKeyword {
    String filedir;
    public makeKeyword(String path) {
        this.filedir = path;
    }
    public void makeIndexXml() throws ParserConfigurationException, SAXException, IOException {
        try {
            //make doucument(indexed xml)
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.newDocument();
            Element docs = document.createElement("docs");
            document.appendChild(docs);

            //parsing
            String [][] getTags = kkmaChange(parseXML());

            for(int i = 0; i < getTags.length; i++){
                String num = Integer.toString(i);

                String getTitle = getTags[i][0];
                String getBody = getTags[i][1];
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

            //xml indexing
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new FileOutputStream(new File("./index.xml")));

            transformer.transform(source, result);
        } catch(Exception e) {
            System.out.println(e);
        }
    }
    public String [][] parseXML() throws IOException, SAXException, ParserConfigurationException {
        String [][] getTags = new String[5][2];
        try{
            //make documents
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document document = documentBuilder.parse(this.filedir);
            Element doc = document.getDocumentElement();

            NodeList children = doc.getChildNodes();
            int docId = 0;
            //parsing ID,tags
            for(int i = 0; i < children.getLength(); i++){
                Node node = children.item(i);
                NodeList docChild = node.getChildNodes();

                for(int j = 0; j < docChild.getLength(); j++){
                    Node docChildNode = docChild.item(j);
                    if(docChildNode.getNodeType() == Node.ELEMENT_NODE){
                        Element ele = (Element)docChildNode;
                        String nodeName = ele.getNodeName();
                        if(nodeName.equals("title")){
                            getTags[docId][0] = ele.getTextContent();
                        }
                        else if(nodeName.equals("body")){
                            getTags[docId][1] = ele.getTextContent();
                            docId++; //body -> next title
                        }
                        else
                            continue;
                    }
                }
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return getTags;
    }
    public String [][] kkmaChange(String [][] text){
        String [][] kkmaUsedText = new String[text.length][text[0].length];
        for(int i = 0; i < text.length; i++) {
            for(int j = 0; j < text[0].length; j++) {
                if(j == 0)
                    kkmaUsedText[i][j]= text[i][j];
                else
                    kkmaUsedText[i][j] = kkmaCnt(text[i][j]);
            }
        }
        return kkmaUsedText;
    }
    //���ξ� ���� : ���� �ڷ�
    public String kkmaCnt(String text){
        String result = "";
        KeywordExtractor ke = new KeywordExtractor();
        KeywordList kl = ke.extractKeyword(text, true);

        for(int i = 0; i < kl.size(); i++){
            Keyword kwrd = kl.get(i);
            result = result + kwrd.getString() + ":" + kwrd.getCnt() + "#";
        }
        return result;
    }
}
