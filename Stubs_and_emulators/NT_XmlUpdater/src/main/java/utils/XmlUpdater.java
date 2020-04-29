package utils;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//import com.hp.dfe.utils.FileUtils;

import utils.DocBuilder;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class XmlUpdater {

    static Map<String, String> reqUidMap;
    static Map<String, String> accNumberMap;
    static Map<String, String> accUidMap;
    static Map<String, String> operationUids;
    static Map<String, String> fileLinkUidMap;
    static Map<String, String> contractorUids;
    static Map<String, String> contractUidMap;
    static Map<String, String> contractNums;
    static Map<String, String> accRepUidMap;
    static Map<String, String> gozMap;
    static Map<String, String> regAccMap;
    static Map<String, String> innMap;
    static Map<String, String> ogrnMap;
    static Map<String, String> DebitCorrInnMap;
    static Map<String, String> CreditPayerInnMap;
    static Map<String, String> reorganisationOpUidMap;
    static Map<String, String> depositUids;
    static Map<String, String> loanUidMap;
    static Map<String, String> minReqBalanceMap;
    static Map<String, String> assigneeUidMap;
    public static Map<String, String> dulSeriesNumberMap;
    public static Map<String, String> kppMap;

    static int innDCount;
    static int orgnDCount;
    static String pathDestination ;


    public XmlUpdater(String pathDestination)
    {

        this.pathDestination=pathDestination;
    }


    public static void initMaps() {
        reqUidMap = new HashMap<>();
        accNumberMap = new HashMap<>();
        accUidMap = new HashMap<>();
        operationUids = new HashMap<>();
        contractorUids = new HashMap<>();
        fileLinkUidMap = new HashMap<>();
        contractUidMap = new HashMap<>();
        contractNums = new HashMap<>();
        accRepUidMap = new HashMap<>();
        gozMap = new HashMap<>();
        regAccMap = new HashMap<>();
        innMap = new HashMap<>();
        ogrnMap = new HashMap<>();
        DebitCorrInnMap = new HashMap<>();
        CreditPayerInnMap = new HashMap<>();
        reorganisationOpUidMap = new HashMap<>();
        depositUids = new HashMap<>();
        loanUidMap = new HashMap<>();
        minReqBalanceMap = new HashMap<>();
        assigneeUidMap = new HashMap<>();

        dulSeriesNumberMap = new HashMap<>();
        kppMap = new HashMap<>();

    }
    public static void deleteAllFIlesInDirectory()
    {
        File dir = new File(pathDestination);
        for (File file: dir.listFiles()) {
            if (!file.isDirectory())
                file.delete();
        }
        dir.delete();
    }

    public static void unZip() throws IOException {
        File dir = new File(pathDestination);
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();

        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        for (File file:dir.listFiles())
        {
            if (file.getName().contains(".jar")   )
            {continue;}
            try {
                fis = new FileInputStream(file.getAbsolutePath());
                ZipInputStream zis = new ZipInputStream(fis);
                ZipEntry ze = zis.getNextEntry();
                while(ze != null){
                    String fileName = ze.getName();
                    File newFile = new File(pathDestination + File.separator + fileName);
                   // System.out.println(newFile.getAbsolutePath());
                    //create directories for sub directories in zip
                    new File(newFile.getParent()).mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                    //close this ZipEntry
                    zis.closeEntry();
                    ze = zis.getNextEntry();
                }
                //close last ZipEntry
                //file.delete();
                zis.closeEntry();
                zis.close();
                fis.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    //    public static String getCrc32(Path zipFilePath) throws IOException {
//        FileInputStream fin = new FileInputStream(zipFilePath.toFile());
//        Checksum sum_control = new CRC32();
//        for (int b = fin.read(); b != -1; b = fin.read()) {
//            sum_control.update(b);
//        }
//        String crc32 = String.valueOf(sum_control.getValue());
//        fin.close();
//        return crc32;
//
//    }

    public static String getCrc32(Path zipFilePath) throws IOException {
        CRC32 sum_control = new CRC32();
        byte[] bytes = Files.readAllBytes(zipFilePath);
        sum_control.update(bytes);
        String crc32 = String.valueOf(sum_control.getValue());
        return crc32;

    }

    public static long checksumBufferedInputStream(String filepath) throws IOException {

        InputStream inputStream = new BufferedInputStream(new FileInputStream(filepath));
        CRC32 crc = new CRC32();
        int cnt;
        while ((cnt = inputStream.read()) != -1) {
            crc.update(cnt);
        }
        return crc.getValue();
    }


    public static void createZipFile(List<String> scrPath, String zipPath) throws Exception {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        Path zipfile = Paths.get(zipPath);

        try (FileSystem fs = FileSystems.newFileSystem(zipfile, null)) {
            for (String path: scrPath) {
                Path externalTxtFile = Paths.get(path);
                Path pathInZipfile = fs.getPath(externalTxtFile.getFileName().toString());
                Files.copy(externalTxtFile, pathInZipfile, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    public static void createZipFile(List<Path> scrPath, Path zipPath) throws Exception {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        final URI uri = URI.create("jar:" + zipPath.toUri());

        try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
            for (Path path : scrPath) {
                Path pathInZipfile = fs.getPath(path.getFileName().toString());
                Files.copy(path, pathInZipfile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void updateAllXmlInFolder(String path) throws Exception {
        updateAllXmlInFolder(path,12);
    }


    public static void updateAllXmlInFolder(String path, int innDigCount) throws Exception {
        innDCount = innDigCount;
        if (innDigCount == 12) {
            orgnDCount = 15;
        } else {
            orgnDCount = 13;
        }


        initMaps();

        MapSetter ms = new MapSetter();
        AttrChanger ac = new AttrChanger();


        File[] xmls = new File(path).listFiles();
        List<File> fileList = Arrays.asList(xmls);
        List<File> filteredList = fileList.stream().filter(file -> file.toString().endsWith(".xml")).collect(Collectors.toList());
        Collections.sort(filteredList);

        for (File xml : filteredList) {
            Document doc = DocBuilder.getInstance().parse(xml);

            changePacketUID(doc);

            visit(doc, 0, ms);
            visit(doc, 0, ac);
            saveXml(xml, doc);


        }
    }

    public static ArrayList<File> collectFiles(String folderPath, boolean isRecursively) throws IOException {
        File[] xmls;
        if (isRecursively) {
            xmls = (File[])Files.walk((new File(folderPath)).toPath()).map(Path::toFile).filter((e) -> {
                return !e.isDirectory();
            }).toArray((x$0) -> {
                return new File[x$0];
            });
        } else {
            xmls = (new File(folderPath)).listFiles();
        }

        ArrayList<File> fileList = new ArrayList(Arrays.asList(xmls));
        Collections.sort(fileList);
        return fileList;
    }

    public static void addZeros(String folder) throws ParserConfigurationException, IOException, SAXException, TransformerException {

        AddZeros az = new AddZeros();
        File[] xmls = new File(folder).listFiles();
        List fileList = Arrays.asList(xmls);
        Collections.sort(fileList);
        for (File xml : xmls) {
            Document doc = DocBuilder.getInstance().parse(xml);
            visit(doc, 0, az);
            saveXml(xml, doc);
        }

    }


    public static void saveXml(File xml, Document doc) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Result output = new StreamResult(new File(xml.getAbsolutePath()).getAbsolutePath());
        Source input = new DOMSource(doc);
        transformer.transform(input, output);
    }

    public static void visit(Node node, int level, IProcessor process) {
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node childNode = list.item(i);
            process.process(childNode, level + 1);
            visit(childNode, level + 1, process);
        }
    }

    /**
     * Возвращает имя атрибута ReqUID
     *
     * @param e исследуемый тег
     * @return имя атрибута или null, если для этого тега не предусмотрен аттрибут ReqUID
     */
    private static String getReqUidAttrName(Element e) {
        switch (e.getTagName()) {
            case "Containers":
            case "MetaInf":
                return "ReqUID";
            default:
                return null;
        }
    }

    /**
     * Запоминает ReqUID и генерирует для него новый. если в этом гете это есть
     *
     * @param e исследуемый тег
     */
    private static void setReqUidMap(Element e) {
        String reqUidAttrName = getReqUidAttrName(e);

        if (reqUidAttrName != null) {
            String reqUid = e.getAttribute(reqUidAttrName);
            if (!StringUtils.isBlank(reqUid)) {
                reqUidMap.putIfAbsent(reqUid, UUID.randomUUID().toString());
            }
        }
    }

    /**
     * Устаналивает новый ReqUID если в этом гете это есть
     *
     * @param e исследуемый тег
     */
    private static void changeReqUid(Element e) {
        String reqUidAttrName = getReqUidAttrName(e);

        if (reqUidAttrName != null) {
            String oldReqUid = e.getAttribute(reqUidAttrName);
            if (!StringUtils.isBlank(oldReqUid)) {
                String newReqUid = reqUidMap.get(oldReqUid);
                e.setAttribute(reqUidAttrName, newReqUid);
            }
        }
    }


    /**
     * Возвращает имя атрибута ОГРН
     *
     * @param e исследуемый тег
     * @return имя атрибута или null, если для этого тега не предусмотрен аттрибут ОГРН
     */
    private static String getOgrnAttrName(Element e) {
        switch (e.getTagName()) {
            case "Contractors":
            case "Owner":
            case "Contractor":
            case "ContractorCorr":
            case "RightsSource":
            case "RightsTarget":
                return "OGRN";
            default:
                return null;
        }
    }

    /**
     * Запоминает ОГРН и генерирует для него новый. если в этом гете это есть
     *
     * @param e исследуемый тег
     */
    private static void setOgrnMap(Element e) {
        String ogrnAttrName = getOgrnAttrName(e);

        if (ogrnAttrName != null) {
            String ogrn = e.getAttribute(ogrnAttrName);
            if (!StringUtils.isBlank(ogrn)) {
                ogrnMap.putIfAbsent(ogrn, getRandomOgrn());
            }
        }
    }

    /**
     * Устаналивает новый ОГРН если в этом гете это есть
     *
     * @param e исследуемый тег
     */
    private static void changeOgrn(Element e) {
        String ogrnAttrName = getOgrnAttrName(e);

        if (ogrnAttrName != null) {
            String oldOgrn = e.getAttribute(ogrnAttrName);
            if (!StringUtils.isBlank(oldOgrn)) {
                String newOgrn = ogrnMap.get(oldOgrn);
                e.setAttribute(ogrnAttrName, newOgrn);
            }
        }
    }

    /**
     * Возвращает имя атрибута ИНН
     *
     * @param e исследуемый тег
     * @return имя атрибута или null, если для этого тега не предусмотрен аттрибут ИНН
     */
    private static String getInnAttrName(Element e) {
        switch (e.getTagName()) {
            case "Contractors":
            case "Owner":
            case "Contractor":
            case "ContractorCorr":
            case "RightsSource":
            case "RightsTarget":
            case "Description":
                return "INN";
            case "AccountFile":
                return "ReceiverINN";
            default:
                return null;
        }
    }

    /**
     * Запоминает ИНН генерирует для него новый. если в этом теге это есть
     *
     * @param e исследуемый тег
     */
    private static void setInnMap(Element e) {
        String innAttrName = getInnAttrName(e);
        String inn = null;

        if (innAttrName != null) {
            inn = e.getAttribute(innAttrName);
        } else if (e.getTagName().equals("INN")) {
            inn = e.getNodeValue();
        }

        if (!StringUtils.isBlank(inn)) {
            innMap.putIfAbsent(inn, getRandomInn());
        }
    }

    /**
     * Устаналивает новый ИНН если в этом гете это есть
     *
     * @param e исследуемый тег
     */
    private static void changeInn(Element e) {
        String innAttrName = getInnAttrName(e);

        if (innAttrName != null) {
            String oldInn = e.getAttribute(innAttrName);
            if (!StringUtils.isBlank(oldInn)) {
                String newInn = innMap.get(oldInn);
                e.setAttribute(innAttrName, newInn);
            }
        } else if (e.getTagName().equals("INN") && !StringUtils.isBlank(e.getNodeValue())) {
            e.setNodeValue(innMap.get(e.getNodeValue()));
        }
    }

    private static void setRegAccMap(Element e) {
        String attr = "";
        if (e.hasAttribute("AccFrom") || e.hasAttribute("AccTo")) {
            attr = !e.getAttribute("AccFrom").trim().isEmpty() ? e.getAttribute("AccFrom") : e.getAttribute("AccTo");
            regAccMap.putIfAbsent(attr, getRandomAccNumber());
        }

    }

    private static void changeRegAcc(Element e) {
        String attrToChange = "";
        if (e.hasAttribute("AccFrom") || e.hasAttribute("AccTo")) {
            attrToChange = e.hasAttribute("AccFrom") ? "AccFrom" : "AccTo";
            if (!e.getAttribute(attrToChange).trim().isEmpty())
                e.setAttribute(attrToChange, regAccMap.get(e.getAttribute(attrToChange)));
        }
    }

    private static void setDebitCorrInnMap(Element e) {
        if (e.getTagName() == "DebitOps" && e.hasAttribute("CorrINN")) {
            DebitCorrInnMap.putIfAbsent(e.getAttribute("CorrINN"), getRandomInn());
        }

    }

    private static void changeDebitCorrInn(Element e) {

        if (e.getTagName() == "DebitOps" && e.hasAttribute("CorrINN")) {

            if (!e.getAttribute("CorrINN").trim().isEmpty())
                e.setAttribute("CorrINN", DebitCorrInnMap.get(e.getAttribute("CorrINN")));
        }
    }

    private static void setCreditPayerInnMap(Element e) {

        if (e.getTagName() == "CreditOps" && e.hasAttribute("PayerINN")) {
            CreditPayerInnMap.putIfAbsent(e.getAttribute("PayerINN"), getRandomInn());
        }

    }

    private static void changeCreditPayerInn(Element e) {

        if (e.getTagName() == "CreditOps" && e.hasAttribute("PayerINN")) {

            if (!e.getAttribute("PayerINN").trim().isEmpty())
                e.setAttribute("PayerINN", CreditPayerInnMap.get(e.getAttribute("PayerINN")));
        }
    }

    private static void setGozMap(Element e) {
        if (e.hasAttribute("GOZUID"))
            gozMap.putIfAbsent(e.getAttribute("GOZUID"), getRandomGozUid());

    }

    private static void changeGoz(Element e) {
        if (e.hasAttribute("GOZUID"))
            e.setAttribute("GOZUID", gozMap.get(e.getAttribute("GOZUID")));
        if (e.getTagName() == "GOZUID") {
            Node n = e;
            n.setTextContent(gozMap.get(e.getTextContent()));
        }
    }

    private static void setAccNumMaps(Element e) {
        if ((e.getTagName() == "ContractorCorrAcc" || e.getTagName() == "Accs" || e.getTagName() == "Acc" || e.getTagName() == "AccFrom" || e.getTagName() == "AccTo")) {
            accNumberMap.putIfAbsent(e.getAttribute("Num"), getRandomAccNumber());
        }
    }

    private static void changeAccsNum(Element e) {
        if (e.getTagName() == "ContractorCorrAcc" || e.getTagName() == "Accs" || e.getTagName() == "Acc" || e.getTagName() == "AccPrev" || e.getTagName() == "AccFrom" || e.getTagName() == "AccTo") {
            e.setAttribute("Num", accNumberMap.get(e.getAttribute("Num")));
        }
    }

    private static void setAccUidMaps(Element e) {
        if ((e.getTagName() == "ContractorCorrAcc" || e.getTagName() == "Accs" || e.getTagName() == "Acc" || e.getTagName() == "AccFrom" || e.getTagName() == "AccTo")) {
            accUidMap.putIfAbsent(e.getAttribute("ID"), UUID.randomUUID().toString());
        }
    }

    private static void changeAccsUid(Element e) {
        if (e.getTagName() == "ContractorCorrAcc" || e.getTagName() == "Accs" || e.getTagName() == "Acc" || e.getTagName() == "AccPrev" || e.getTagName() == "AccFrom" || e.getTagName() == "AccTo") {
            e.setAttribute("ID", accUidMap.get(e.getAttribute("ID")));
        }
    }

    /**
     * Возвращает имя атрибута UID контрагента
     *
     * @param e исследуемый тег
     * @return имя атрибута или null, если для этого тега не предусмотрен аттрибут UID контрагента
     */
    private static String getContractorUidAttrName(Element e) {
        switch (e.getTagName()) {
            case "Contractors":
            case "Owner":
            case "Contractor":
            case "ContractorCorr":
            case "RightsSource":
            case "RightsTarget":
                return "ID";
            case "Description":
                return "Uid";
            case "ForeignContractor":
                return "ContractorUID";

//            case "Assignee":
//                //return ((e.getParentNode().getNodeName().equals("Contractors")) ? "UID" : "AssigneeUID");
//                return (e.getParentNode().getNodeName().equals("Contractors") ? null : "ContractorUID");

            default:
                return null;
        }
    }

    /**
     * Запоминает UID контрагента и генерирует для него новый. если в этом гете это есть
     *
     * @param e исследуемый тег
     */
    private static void collectContractorUids(Element e) {
        String contractorUidAttrName = getContractorUidAttrName(e);

        if (contractorUidAttrName != null) {
            String contractorUID = e.getAttribute(contractorUidAttrName);
            if (!StringUtils.isBlank(contractorUID)) {
                contractorUids.putIfAbsent(contractorUID, UUID.randomUUID().toString());
            }
        }
    }

    /**
     * Устаналивает новый UID контрагента если в этом гете это есть
     *
     * @param e исследуемый тег
     */
    private static void changeContractorUids(Element e) {
        String contractorUidAttrName = getContractorUidAttrName(e);

        if (contractorUidAttrName != null) {
            String oldContractorUID = e.getAttribute(contractorUidAttrName);
            if (!StringUtils.isBlank(oldContractorUID)) {
                String newContractorUID = contractorUids.get(oldContractorUID);
                e.setAttribute(contractorUidAttrName, newContractorUID);
            }
        }
    }

    /**
     * Возвращает имя атрибута Num контракта
     *
     * @param e исследуемый тег
     * @return имя атрибута или null, если для этого тега не предусмотрен аттрибут Num контракта
     */
    private static String getContractNumAttrName(Element e) {
        switch (e.getTagName()) {
            case "Contracts":
                return "Num";
            default:
                return null;
        }
    }

    /**
     * Запоминает Num контракта и генерирует для него новый. если в этом гете это есть
     *
     * @param e исследуемый тег
     */
    private static void collectContractNums(Element e) {
        String contractNumAttrName = getContractNumAttrName(e);

        if (contractNumAttrName != null) {
            String contractNum = e.getAttribute(contractNumAttrName);
            if (!StringUtils.isBlank(contractNum)) {
                contractNums.putIfAbsent(contractNum, getRandomNumber(contractNum.length()));
            }
        }
    }

    /**
     * Устаналивает новый Num контракта если в этом гете это есть
     *
     * @param e исследуемый тег
     */
    private static void changeContractNums(Element e) {
        String contractNumAttrName = getContractNumAttrName(e);

        if (contractNumAttrName != null) {
            String oldContractNum = e.getAttribute(contractNumAttrName);
            if (!StringUtils.isBlank(oldContractNum)) {
                String newContractNum = contractNums.get(oldContractNum);
                e.setAttribute(contractNumAttrName, newContractNum);
            }
        }
    }


    private static void setFileLinkUidMap(Element e) {
        if (e.getTagName().equals("FileLinks") || e.getTagName().equals("FileAttach"))
            fileLinkUidMap.putIfAbsent(e.getAttribute("Id"), UUID.randomUUID().toString());
    }

    private static void changeFileLinkUid(Element e) {
        if (e.getTagName().equals("FileLinks") || e.getTagName().equals("FileAttach")) {
            e.setAttribute("Id", fileLinkUidMap.get(e.getAttribute("Id")));
        }
    }

    private static void setContractUidMap(Element e) {
        if ((e.getTagName() == "Contracts" || e.getTagName() == "Contract"))
            contractUidMap.putIfAbsent(e.getAttribute("ContractUID"), UUID.randomUUID().toString());
    }

    private static void collectDepositUids(Element e) {
        if ((e.getTagName().equals("Deposit")))
            depositUids.putIfAbsent(e.getAttribute("UID"), UUID.randomUUID().toString());
    }

    private static void setLoanUidMap(Element e) {

        if ((e.getTagName().equals("Loan")))
            loanUidMap.putIfAbsent(e.getAttribute("UID"), UUID.randomUUID().toString());
    }

    private static void setAssigneeUidMap(Element e) {
        if ((e.getTagName().equals("Assignee"))) {
            String attrToCollect = e.hasAttribute("UID") ? "UID" : "AssigneeUID";
            assigneeUidMap.putIfAbsent(e.getAttribute(attrToCollect), UUID.randomUUID().toString());
        }
    }

    public void setDulSeriesNumberMap(Element e) {
        if ((e.getTagName().equals("DUL"))) {
            String attrToCollect = "SeriesNumber";
            dulSeriesNumberMap.putIfAbsent(e.getAttribute(attrToCollect), DataGenerator.getRandomNumber(10));
        }
    }


    public void changeDulSeriesNumber(Element e) {

        if ((e.getTagName().equals("DUL"))) {
            String attrToChange = "SeriesNumber";
            e.setAttribute(attrToChange, dulSeriesNumberMap.get(e.getAttribute(attrToChange)));
        }
    }

    private static void setMinReqBalanceUidMap(Element e) {

        if ((e.getTagName().equals("MinReqBalance")))
            minReqBalanceMap.putIfAbsent(e.getAttribute("UID"), UUID.randomUUID().toString());
    }


    private static void changeContractUid(Element e) {
        if (e.getTagName().equals("Contracts") || e.getTagName().equals("Contract")) {
            e.setAttribute("ContractUID", contractUidMap.get(e.getAttribute("ContractUID")));
        }
    }

    private static void changeDepositUid(Element e) {
        if ((e.getTagName().equals("Deposit"))) {
            e.setAttribute("UID", depositUids.get(e.getAttribute("UID")));
        }
    }

    private static void changeLoanUid(Element e) {
        if ((e.getTagName().equals("Loan"))) {
            e.setAttribute("UID", loanUidMap.get(e.getAttribute("UID")));
        }
    }

    private static void changeAssigneeUid(Element e) {

        if ((e.getTagName().equals("Assignee"))) {
            String attrToChange = e.hasAttribute("UID") ? "UID" : "AssigneeUID";
            e.setAttribute(attrToChange, assigneeUidMap.get(e.getAttribute(attrToChange)));
        }
    }

    private static void changeMinReqBalanceUid(Element e) {
        if ((e.getTagName().equals("MinReqBalance"))) {
            e.setAttribute("UID", minReqBalanceMap.get(e.getAttribute("UID")));
        }
    }

    private static void setAccRepUidMap(Element e) {
        if (e.getTagName().equals("AccReps"))
            accRepUidMap.putIfAbsent(e.getAttribute("AccRepUID"), UUID.randomUUID().toString());
    }

    private static void changeAccRepUid(Element e) {
        if (e.getTagName().equals("AccReps") || e.getTagName().equals("AccRep")) {
            e.setAttribute("AccRepUID", accRepUidMap.get(e.getAttribute("AccRepUID")));
        }
    }


    private static void collectOpUids(Element e) {
        String uid = null;
        if (e.hasAttribute("OpUID") && !e.getTagName().equals("Reorganisation")) {
            uid = e.getAttribute("OpUID");
        }
        if (e.getTagName().equals("BalanceTransfer") && e.hasAttribute("UID")) {
            uid = e.getAttribute("UID");
        }
        if (e.getTagName().equals("OrigOpUID") || e.getTagName().equals("Ops")) {
            uid = e.getTextContent();
        }
        if (!StringUtils.isBlank(uid)) {
            operationUids.putIfAbsent(uid, UUID.randomUUID().toString());
        }
    }




    private static void changeOpUids(Element e) {
        if (e.hasAttribute("OpUID")  && !e.getTagName().equals("Reorganisation")){
            e.setAttribute("OpUID", operationUids.get(e.getAttribute("OpUID")));

        } else if (e.getTagName().equals("BalanceTransfer") && e.hasAttribute("UID")){
            e.setAttribute("UID", operationUids.get(e.getAttribute("UID")));

        }  else if (e.getTagName().equals("OrigOpUID") || e.getTagName().equals("Ops")){
            e.setTextContent(operationUids.get(e.getTextContent()));
        }
    }

    private static void changeCorrectedOpUid(Element e) {
        if (e.hasAttribute("CorrectedOpUID")) {
            String oldOpUid = e.getAttribute("CorrectedOpUID");
            String newOpUid = operationUids.get( oldOpUid );

            //Подменяем только если такая операция была в этом пакете.
            //В противном случае это изменение операции уже пролитой давно.
            if ( newOpUid != null ){
                e.setAttribute("CorrectedOpUID", newOpUid);
            }
        }
    }

    /**
     * Устаналивает новый Uid операции реорганизации если в этом теге это есть
     * @param e исследуемый тег
     */
    private static void changeReorgOpUid(Element e) {
        if ( "RightsTransfer".equals( e.getTagName() ) ){
            String oldOperationUid = e.getAttribute("ReorganisationOp");
            e.setAttribute("ReorganisationOp", operationUids.get(oldOperationUid));
        }
    }




    public static String getRandomAccNumber() {

        return getRandomNumber(20);
    }

    public static String getRandomGozUid() {

        return getRandomNumber(25);
    }

    public static String getRandomInn() {

        return innDCount == 12 ? InnUtils.generateIpINN() : InnUtils.generateUlINN();
    }

    public static String getRandomOgrn() {

        return orgnDCount == 13 ? OgrnUtils.generateOGRN() : OgrnUtils.generateOGRNIP();
    }

    /**
     * Меняет атрибут PacketUID в control.xml
     * @param doc XML-документ control.xml
     */
    private static void changePacketUID( Document doc ){
        Element root = doc.getDocumentElement();
        if (root.hasAttribute("PacketUID")){
            root.setAttribute("PacketUID", UUID.randomUUID().toString());
        }
    }


    public static String getRandomNumber(int digCount) {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(digCount);
        for (int i = 0; i < digCount; i++)
            sb.append((char) ('0' + rnd.nextInt(10)));
        return sb.toString();
    }

    public interface IProcessor {
        void process(Node node, int level);
    }

    public static class AddZeros implements IProcessor {
        public void process(Node node, int level) {
            if (node instanceof Element) {
                Element e = (Element) node;
                switch (e.getTagName()) {
                    case "FileAttach":
                        e.setAttribute("Id", e.getAttribute("Id").replaceFirst("^(...)", "000"));
                        break;
                    case "FileLinks":
                        e.setAttribute("Id", e.getAttribute("Id").replaceFirst("^(...)", "000"));
                        break;
                    case "Contractors":
                        e.setAttribute("ID", e.getAttribute("ID").replaceFirst("^(...)", "000"));
                        break;
                    case "Owner":
                        e.setAttribute("ID", e.getAttribute("ID").replaceFirst("^(...)", "000"));
                        break;
                    case "AccCreateOps":
                        e.setAttribute("OpUID", e.getAttribute("OpUID").replaceFirst("^(...)", "000"));
                        break;
                    case "CreditOps":
                        e.setAttribute("OpUID", e.getAttribute("OpUID").replaceFirst("^(...)", "000"));
                        break;
                    case "DebitOps":
                        e.setAttribute("OpUID", e.getAttribute("OpUID").replaceFirst("^(...)", "000"));
                        break;
                    case "Accs":
                        e.setAttribute("ID", e.getAttribute("ID").replaceFirst("^(...)", "000"));
                        break;
                    case "Acc":
                        e.setAttribute("ID", e.getAttribute("ID").replaceFirst("^(...)", "000"));
                        break;
                    case "AccTo":
                        e.setAttribute("ID", e.getAttribute("ID").replaceFirst("^(...)", "000"));
                        break;
                    case "AccFrom":
                        e.setAttribute("ID", e.getAttribute("ID").replaceFirst("^(...)", "000"));
                        break;
                }
            }
        }
    }

    public static class MapSetter implements IProcessor {


        public void process(Node node, int level) {
            if (node instanceof Element) {
                Element e = (Element) node;

                setReqUidMap( e );
                setAccNumMaps(e);
                setAccUidMaps(e);
                collectContractorUids(e);
                setFileLinkUidMap(e);
                setContractUidMap(e);
                collectContractNums( e );
                setAccRepUidMap(e);
                setGozMap(e);
                setRegAccMap(e);
                setInnMap(e);
                setOgrnMap(e);
                setDebitCorrInnMap(e);
                setCreditPayerInnMap(e);
                collectOpUids( e );
                collectDepositUids(e);
                setLoanUidMap(e);
                setMinReqBalanceUidMap(e);
                setAssigneeUidMap(e);
            }
        }
    }

    public static class AttrChanger implements IProcessor {
        public void process(Node node, int level) {
            if (node instanceof Element) {
                Element e = (Element) node;
                changeReqUid( e );
                changeGoz(e);
                changeAccsNum(e);
                changeAccsUid(e);
                changeOpUids(e);
                changeCorrectedOpUid(e);
                changeReorgOpUid( e );
                changeContractorUids(e);
                changeFileLinkUid(e);
                changeContractUid(e);
                changeContractNums( e );
                changeAccRepUid(e);
                changeRegAcc(e);
                changeInn(e);
                changeOgrn(e);
                changeDebitCorrInn(e);
                changeCreditPayerInn(e);
                changeDepositUid(e);
                changeLoanUid(e);
                changeMinReqBalanceUid(e);
                changeAssigneeUid(e);
            }
        }
    }
}
