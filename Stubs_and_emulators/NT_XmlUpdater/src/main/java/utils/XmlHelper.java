package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


//import tests.integration.steps.AssertExtensionSteps;
import ru.tecforce.xmlupdater.XmlFolderContext;
import utils.enums.Tag;
import utils.helpers.FileHelper;
import utils.xmlObjects.Reorganisation;
import utils.xmlObjects.Message;
//import utils.helpers.FileHelper;
import javax.xml.parsers.*;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.TransformerException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.*;


public class XmlHelper {

    // private final static Logger logger = LoggerFactory.getLogger(XmlHelper.class);

    //public static final String baseFolder = "src/test/resources/integration/xmlFiles/";
    private static String pathToXml;
    private static Document doc;

    public static Document getDocument(){
        return doc;
    }


    public static void setXml(String fileName) throws IOException, SAXException, ParserConfigurationException {
        pathToXml = fileName;
        doc = DocBuilder.getDocument(pathToXml);
    }

    public static Element getNodeByXpath(String xpath) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList)xPath.evaluate(xpath,

                doc.getDocumentElement(), XPathConstants.NODESET);
        Node node = nodes.item(0);
        return (Element)node;
    }
    public static NodeList getNodeListByXpath(String xpath) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) xPath.evaluate(xpath,
                doc.getDocumentElement(), XPathConstants.NODESET);
        return nodes;
    }


    public static String getAttrValue(Tag tagName, int index, String attr) throws ParserConfigurationException, IOException, SAXException {
        Element el = getElement(tagName, index);
        String attrVal = el.getAttribute(attr);
        return attrVal;

    }

    public static void setAttrValue(Tag tagName, int index, String attr, String value) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        Element el = getElement(tagName, index);
        el.setAttribute(attr, value);
        XmlUpdater.saveXml(new File(pathToXml), doc);
    }

    public static void setTextContent(Tag tagName, int index, String text) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        Element el = getElement(tagName, index);
        el.setTextContent(text);
        XmlUpdater.saveXml(new File(pathToXml), doc);
    }

    public static String getTextContent(Tag tagName, int index) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        Element el = getElement(tagName, index);
        return el.getTextContent();
    }

    public static Element getElement(Tag tagName, int index) {
        Node node = doc.getDocumentElement().getElementsByTagName(tagName.toString()).item(index);
        Element el = (Element) node;
        return el;
    }

    public static int getAmountOfElements(Tag tagName) {
        return doc.getDocumentElement().getElementsByTagName(tagName.toString()).getLength();
    }
    public static String getElementText(Tag tagName, int index) {
        Node node = doc.getDocumentElement().getElementsByTagName(tagName.toString()).item(index);
        Element el = (Element) node;
        return el.getTextContent();
    }

    public static List<Reorganisation> getReorganizationList(String filePath) {
        String data = FileHelper.getTextFromFile(filePath);
        StringReader reader = new StringReader(data);
        try {
            JAXBContext context = JAXBContext.newInstance(Message.class);
            Unmarshaller un = context.createUnmarshaller();
            Message message = (Message) un.unmarshal(reader);
            return message.getReorganisationList();
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при парсинге xml данных в java объект");
        }
    }

    public static String getGozNumber() {
        Element node = (Element) doc.getElementsByTagName("MetaInf").item(0);
        String attr = node.getAttribute("GOZUID");
        //  AssertExtensionSteps.println(String.format("Полученный из %s номер ГОЗ = %s", pathToXml, attr));
        return attr;
    }

    public static void setMetainf(String gozNumber, String periodStart, String periodEnd) throws TransformerException {
        Element node = (Element) doc.getElementsByTagName("MetaInf").item(0);
        node.setAttribute("GOZUID", gozNumber);
        node.setAttribute("PeriodStart", periodStart);
        node.setAttribute("PeriodEnd", periodEnd);
        XmlUpdater.saveXml(new File(pathToXml), doc);
    }

    public static String getGozText(int index) {
        Element el = getElement(Tag.GOZUID, index);
        return el.getTextContent();
    }

    public static Document addSpaces(Document document, Tag tagName, int index, String attr) throws TransformerException {
        Element el = (Element) document.getDocumentElement().getElementsByTagName(tagName.toString()).item(index);
        String attrValue = el.getAttribute(attr);
        el.setAttribute(attr, " "+attrValue+" ");
        return document;
    }



    public static Document addSymbols(Document document, Tag tagName, int index, String attr) throws TransformerException {
        Element el = (Element) document.getDocumentElement().getElementsByTagName(tagName.toString()).item(index);
        Random r = new Random();
        char c1 = (char)(r.nextInt(26) + 'a');
        char c2 = (char)(r.nextInt(26) + 'a');
        String attrValue = c1 + el.getAttribute(attr) + c2;
        el.setAttribute(attr, attrValue);
        return document;
    }

    public static String getAttrValue(Tag tagName, String attr) throws IOException, SAXException, ParserConfigurationException {
        Element el = getElement(tagName, 0);
        Objects.requireNonNull(el, String.format("В XML отсутствует тэг %s", tagName));
        String attrVal = el.getAttribute(attr);
        return attrVal;
    }

    /**
     * Convert standalone file to map of properties
     * @param standalone file
     * @return properties map String to String
     */
    public static Map<String, String> parseSystemProperties(InputStream standalone) {
        Map<String, String> props = new HashMap<>();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        DefaultHandler handler = new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) {
                if (qName.equalsIgnoreCase("property"))
                    props.put(attributes.getValue("name"), attributes.getValue("value"));
            }
        };
        try {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(standalone, handler);
        } catch (Exception e) {
            //logger.warn("Unable to load standalone properties. Cause:", e);
            e.printStackTrace();
        }
        return props;
    }

    /**
     * Метод по расчету CRC и Size для filelinks (Ангелина)
     */
    public static Map<String, List<String>> addAttachToXml( String... attachRelativePath) throws XPathExpressionException,
            IOException, SAXException, ParserConfigurationException, TransformerException {

        Map<String, List<String>> listAttach=new HashMap<>();

        for (int i = 0; i < attachRelativePath.length; i++) {
            List<String> values = new ArrayList<>();
            File file = new File(XmlFolderContext.getCurrentFolder() + "/" + attachRelativePath[i]);
            Element element = getNodeByXpath(String.format("//FileLinks[@Name='%s']", file.getName()));

            if (element != null) {
                String crc = XmlUpdater.getCrc32(file.toPath());
                String size = String.valueOf(file.length());
                element.setAttribute("CRC", crc);
                element.setAttribute("Size", size);

                values.add(crc);
                values.add(size);
                listAttach.put((element.getAttribute("Id")),values);
            }
        }
        XmlUpdater.saveXml(new File(pathToXml), doc);
        return listAttach;
    }

    public static void addSpecialAttachToXml( Map<String, List<String>> attachRelativePath ) throws XPathExpressionException,
            IOException, SAXException, ParserConfigurationException, TransformerException {
        for (String key : attachRelativePath.keySet()) {
            Element element = getNodeByXpath(String.format("//FileLinks[@Id='%s']", key));
            if (element != null) {
                element.setAttribute("CRC", attachRelativePath.get(key).get(0));
                element.setAttribute("Size", attachRelativePath.get(key).get(1));
            }
        }
        XmlUpdater.saveXml(new File(pathToXml), doc);

    }

    public static  List<String> getListPdfFileInFileLinks(String pathToDataXml) throws ParserConfigurationException,
            SAXException, IOException, XPathExpressionException {
        List<String> listFileName=new ArrayList<>();
        DocumentBuilder db =DocBuilder.getInstance();
        Document document = db.parse(new File(pathToDataXml));
        NodeList nodeList = document.getElementsByTagName("FileLinks");
        for(int x=0,size= nodeList.getLength(); x<size; x++) {
            if (nodeList.item(x).getAttributes().getNamedItem("Name").getNodeValue().contains(".pdf"))
                listFileName.add(nodeList.item(x).getAttributes().getNamedItem("Name").getNodeValue().toString());

        }
        return  listFileName;
    }
    public static String[] getListFilesName() throws XPathExpressionException {
        List<String> names = new ArrayList<>();
        NodeList nodeList = getNodeListByXpath("//FileLinks[contains(@Name, '.pdf') or contains(@Name, '.jpg')" +
                "or contains(@Name, '.xml')" +
                " or contains(@Name, '.jpeg')]");
        for (int i = 0; i < nodeList.getLength(); i++){
            String attr = ((Element) nodeList.item(i)).getAttribute("Name");
            names.add(attr);
        }
        String[] n = new String[names.size()];
        return names.toArray(n);
    }

}
