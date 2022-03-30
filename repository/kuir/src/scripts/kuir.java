package scripts;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class kuir {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, ClassNotFoundException {
        String option = args[0];

        if(option.equals("-c")){
            makeCollection mkC = new makeCollection(args[1]);
            mkC.html2XML();
        }else if(option.equals("-k")){
            String xmlRoute = "./data/" + args[1];
            makeKeyword mkK = new makeKeyword(xmlRoute);
            mkK.makeIndexXml();
        }else if(option.equals("-i")){
            String xmlRoute = "./data/" + args[1];
            indexer ix = new indexer(xmlRoute);
            ix.input2HashMap();//颇老 积己
            //ix.printIndex("./data/index.post");//颇老 免仿
        }

    }

}