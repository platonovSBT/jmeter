package utils;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class DocBuilder {

    private static DocumentBuilderFactory dbf;
    private static DocumentBuilder db;

    public static DocumentBuilder getInstance() throws ParserConfigurationException {

        if (dbf == null) {
            dbf = DocumentBuilderFactory.newInstance();
        }

        if (db == null) {
            db = dbf.newDocumentBuilder();
        }

        return db;
    }

    public static Document getDocument(String path) throws ParserConfigurationException, IOException, SAXException {
        File file = new File(path);
        return DocBuilder.getInstance().parse(file);
    }

}
