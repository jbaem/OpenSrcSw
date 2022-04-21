package scripts;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

public class Midterm {
    String filedir;
    Midterm(String path){ this.filedir = path;}

    public String showSnippet(String query) throws IOException, ClassNotFoundException, ParserConfigurationException, SAXException {

        ArrayList<String> word = new ArrayList<>();
        String tmp = "";
        for(char ch: query.toCharArray()){
            if(ch==' '){
                word.add(tmp);
                tmp = "";
            }
            else{
                tmp+=ch;
            }
        }
        word.add(tmp);
        String res = "";
        for(int i=0; i<word.size(); i++){
            String x = word.get(0);
        }
        //"-m ./collection.xml -q 밀가루 넣은 반죽"
        return "result";
    }
}
