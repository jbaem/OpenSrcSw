package scripts;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;

import org.jsoup.Jsoup;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

public class makeCollection {
    //for using class
    String fileDir;
    public makeCollection(String path) throws ParserConfigurationException, IOException, TransformerException{
        this.fileDir = path;
    }
    //make file list from directory
    public File[] makeFileList() {
        File dir = new File(this.fileDir);
        return dir.listFiles();
    }
    // htmls to xml
    public void html2xml() throws ParserConfigurationException{
        try {
            //make document(xml)
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.newDocument();
            Element docs = document.createElement("docs");
            document.appendChild(docs);
            //get tags(html)
            File[] htmls = makeFileList();
            Element[] doc = new Element[htmls.length];
            Element[] title = new Element[htmls.length];
            Element[] body = new Element[htmls.length];
            //tag elements
            for(int i=0; i<htmls.length; i++) {
                org.jsoup.nodes.Document html = Jsoup.parse(htmls[i], "UTF-8");
                String titleData = html.title();
                String bodyData = html.body().text();
                //<doc>
                doc[i] = document.createElement("doc");
                docs.appendChild(doc[i]);
                doc[i].setAttribute("id", String.valueOf(i));
                //<title>
                title[i] = document.createElement("title");
                title[i].appendChild(document.createTextNode(titleData));
                doc[i].appendChild(title[i]);
                //<body>
                body[i] = document.createElement("body");
                body[i].appendChild(document.createTextNode(bodyData));
                doc[i].appendChild(body[i]);
            }
            //html to xml
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new FileOutputStream(new File("./collection.xml")));

            transformer.transform(source, result);
        }catch(Exception e) {
            System.out.println(e);
        }
    }
}
