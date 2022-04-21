package scripts;

import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.OptionalDataException;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;


public class kuir {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, ClassNotFoundException, TransformerException{
        String command = args[0];
        String path = args[1];

        if(command.equals("-c")){
            makeCollection mkC = new makeCollection(path);
            mkC.html2xml();
        }
        else if(command.equals("-k")){
            makeKeyword mkK = new makeKeyword(path);
            mkK.makeIndexXml();
        }

        else if (command.equals("-i")) {
            if (path != null) {
                indexer ind = new indexer(path);
                ind.Hashmap();
            }
        }
        else if(command.equals("-s")) {
            if (command.length() > 2 && args[2].equals("-q")) {
                String query = args[3];
                searcher searcher = new searcher(path);
                ArrayList<Double> sim = searcher.CalcSim(query);

                searcher.printTitle(sim, "./collection.xml");

            }
        }
        else if(command.equals("-m")){
            String query = args[3];
            if(args[2].equals("-q")){
                Midterm Mt = new Midterm(path);
                String mtPrint = Mt.showSnippet(query);

                System.out.println(mtPrint);
            }
        }
    }
}
/**
 * args >>>
 * kuir -c ./data/
 * kuir -k ./collection.xml
 * kuir -i ./index.xml   ===>  index.post
 *
 * outputstream : 일단 문서 내에 모든 정보를 직렬화된 HashMap 객체로 파일에 저장
 * inputstream : 역직렬화된 HashMap 객체 읽어오기
 *
 */