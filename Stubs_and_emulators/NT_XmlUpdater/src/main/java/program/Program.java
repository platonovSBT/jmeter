package program;


import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;


import org.apache.commons.io.FileUtils;
import ru.tecforce.qa.dfm.core.xmlupdater.XmlFileManager;
import utils.XmlHelper;
import utils.jpgObjects.JpgBuilder;
import ru.tecforce.xmlupdater.*;
import utils.XmlUpdater;
import utils.enums.Tag;
import utils.helpers.FileHelper;
import utils.pdfObjects.PdfBuilder;
import utils.postgreObjects.PostgreDbBuilder;


public class Program {

    public static void main(String[] args) throws ParserConfigurationException {
        long m = System.currentTimeMillis();
        Options options = new Options();

        Option unZipPackage=new Option("u","unZipPackage",true,"Unziping package.Enter YES, if you want. NO - default");
        unZipPackage.setRequired(false);
        options.addOption(unZipPackage);

        Option pathToPackage=new Option("p","pathToZipPackage",true,"Enter path to directory folder");
        pathToPackage.setRequired(true);
        options.addOption(pathToPackage);

        Option genUniqueAttach=new Option("g","generateUniquePdfFile",true,"Generate unique PDF-files.Enter YES, if you want. NO - default");
        genUniqueAttach.setRequired(false);
        options.addOption(genUniqueAttach);

        Option delFiles=new Option("d","deleteFiles",true,"Clear directory after unzipping.Enter YES, if you want. NO - default");
        delFiles.setRequired(false);
        options.addOption(delFiles);

        Option noDataGen=new Option("n","generateNoDataFiles",true,"Generate NoData-files.Enter information about database ZK in format like <host>:<port>. NO - default");
        noDataGen.setRequired(false);
        options.addOption(noDataGen);

        Option cryptoPath=new Option("cp","cryptoPath",true,"Cryptopro.Enter path to directory with cryptcp. NO - default");
        cryptoPath.setRequired(false);
        options.addOption(cryptoPath);

        Option cryptoKey=new Option("ck","cryptoKey",true,"Cryptopro.Enter the fingerprint of the key to sign. NO - default");
        cryptoKey.setRequired(false);
        options.addOption(cryptoKey);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd=null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        String valuepath = cmd.getOptionValue("p");
        String unzip = cmd.getOptionValue("u");
        String genAttch=cmd.getOptionValue("g");
        String deleteFiles=cmd.getOptionValue("d");
        String noDataGenFlag=cmd.getOptionValue("n");
        String pathCrpt=cmd.getOptionValue("cp");
        String keyCrpt=cmd.getOptionValue("ck");
        List<File> listAbsolutePathToZipWithExt = new ArrayList<>();
        Set<String> listAbsolutePathToZip =  new HashSet<>();
        Set<String> listAbsolutePathWith2package =  new HashSet<>();

        XmlUpdater xml=new XmlUpdater(valuepath);
        try {
            listAbsolutePathToZipWithExt = xml.collectFiles(valuepath, true);
            for (File f:listAbsolutePathToZipWithExt)
            {
                if (f.getName().contains("zip")) {
                    Path path = Paths.get(f.getAbsolutePath());

                    String directory = path.getParent().toString();
                    listAbsolutePathToZip.add(directory);
                    //На 2уровня вверх ждя рекурсивного обновления файлов
                    listAbsolutePathWith2package.add(path.getParent().getParent().toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> sortedListAbsolutePathToZip = new ArrayList<>(listAbsolutePathToZip);
        Collections.sort(sortedListAbsolutePathToZip);

        if (unzip!=null) {
            System.out.println("================Unzipping================");
            for (String f:sortedListAbsolutePathToZip)
            {
                XmlUpdater unZip=new XmlUpdater(f);
                try {
                    unZip.unZip();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        //Для создания общего control.zip
        StringBuilder controlDataFileCreate=new StringBuilder();
        if (sortedListAbsolutePathToZip.size()>1)
        {
            controlDataFileCreate.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<ns2:Message xmlns:ns2=\"http://smb.mil.ru/integration/control\" PacketUID=\""+UUID.randomUUID().toString()+"\">\n");
        }

        StringBuilder controlDataFileEdit=new StringBuilder();
        if (sortedListAbsolutePathToZip.size()>1)
        {
            controlDataFileEdit.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<ns2:Message xmlns:ns2=\"http://smb.mil.ru/integration/control\" PacketUID=\""+UUID.randomUUID().toString()+"\">\n");
        }

      //  XmlFolderContext.setCurrentFolder(valuepath);


        try {
            System.out.println("================Updating XML================");
            for (String f: listAbsolutePathWith2package)
            {
              //  ru.tecforce.xmlupdater.XmlUpdater.updateAllXmlInFolder(f,true,false);
                XmlFileManager  xfm=new XmlFileManager();
                xfm.setCurrentFolder(f);
                xfm.updateAllXmlInFolder(f,true,false);
            }
            int i=0;
            int j=0;
            for (String f: sortedListAbsolutePathToZip) {
                try{
                    XmlFolderContext.setCurrentFolder(f);
                    XmlHelper.setXml(f+File.separator+"data.xml");
                    String[] namesAttachFiles =  XmlHelper.getListFilesName();
                    if (namesAttachFiles.length>0)
                    {
                        if (genAttch!=null) {
                            System.out.println("================Generating Unique Attachments================");
                            for (int k=0;k<namesAttachFiles.length;k++) {
                                if (namesAttachFiles[k].contains("pdf")) {
                                    PdfBuilder.generatePdf(f+File.separator+namesAttachFiles[k]);
                                }
                                if (namesAttachFiles[k].contains("jpg")) {
                                    JpgBuilder.generateJpg(f+File.separator+namesAttachFiles[k]);
                                }
                                if (namesAttachFiles[k].contains("jpeg")) {
                                    JpgBuilder.generateJpg(f+File.separator+namesAttachFiles[k]);
                                }
                            }
                        }
                        XmlHelper.addAttachToXml( namesAttachFiles);
                        XmlFolderContext.setCurrentFolder(valuepath);
                    }
                    else  XmlFolderContext.setCurrentFolder(valuepath);

                }
                catch (XPathExpressionException e)
                {
                    System.out.println("В data.xml нет XML-аттачей");
                    XmlFolderContext.setCurrentFolder(valuepath);
                }
                List<Path> filesInFolder = new ArrayList<>();
                filesInFolder.add(new File(f +File.separator+ "data.xml").toPath());
                filesInFolder.addAll(FileHelper.getPathsOfFilesWithExtension(f, ".jpeg"));
                filesInFolder.addAll(FileHelper.getPathsOfFilesWithExtension(f, ".jpg"));
                filesInFolder.addAll(FileHelper.getPathsOfFilesWithExtension(f, ".pdf"));
                filesInFolder.addAll(FileHelper.getPathsOfFilesWithExtensionWithoutControlXml(f, ".xml"));
                if (pathCrpt != null) {

                    File dataSign=new File(f + File.separator + "data.sign");
                    if (dataSign.exists())
                    {
                        dataSign.delete();
                    }
                    File pathSign=new File (pathCrpt);
                    File signNew=new File (f +File.separator+pathSign.getName());
                    FileUtils.copyFile(pathSign, signNew);

                    ProcessBuilder  processBuilder  = new ProcessBuilder();
                    if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
                        processBuilder.command("cmd.exe", "/c");
                        processBuilder.directory(new File(signNew.getParent()));
                        processBuilder.command(signNew.getAbsolutePath(), "-signf", "-thumbprint", keyCrpt, "-q20",
                                "-cert", new File(f + File.separator + "data.xml").getAbsolutePath());
                        Process process =processBuilder.start();
                        InputStream is = process.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader br = new BufferedReader(isr);
                        String line;
                        while ((line = br.readLine()) != null) {

                        }
                        process.destroy();
                    }
                    else {
                        processBuilder.command("/bin/bash", "-c");
                        processBuilder.directory(new File(signNew.getParent()));
                        processBuilder.command(signNew.getAbsolutePath(), "-signf", "-thumbprint", keyCrpt, "-q20",
                                "-cert", new File(f + File.separator + "data.xml").getAbsolutePath());
                        Process process =processBuilder.start();
                        InputStream is = process.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader br = new BufferedReader(isr);
                        String line;
                        while ((line = br.readLine()) != null) {

                        }
                        process.destroy();
                       }
                    File olderFile=new File(f+File.separator+ "data.xml.sgn");
                    File newFile= new File(f +File.separator+ "data.sign");
                    boolean success = olderFile.renameTo(newFile);
                    if (success)
                    {
                        olderFile.delete();
                    }
                    signNew.delete();
                    filesInFolder.add(newFile.toPath());
                }
                else {
                    filesInFolder.add(new File(f + File.separator + "data.sign").toPath());
                }
                if (f.contains("create"))
                {

                    String pathEndFile=valuepath+File.separator+"create";
                    new File(pathEndFile).mkdir();
                    Path zipfile = Paths.get(pathEndFile + File.separator+"data_"+i+".zip");
                    String reqUIDValue= XmlHelper.getAttrValue(Tag.MetaInf,"ReqUID");
                    xml.createZipFile(filesInFolder, zipfile);

                    File file = new File(zipfile.toUri());
                    String size = String.valueOf(file.length());
                    // String crc32 = xml.getCrc32(zipfile.toAbsolutePath());
//        StringBuffer sb =new StringBuffer();
//        try
//        {
//            Runtime rt = Runtime.getRuntime();
//            Process proc = rt.exec("cmd /c "+valuepath+ "\\crc32.exe "+"\""+zipfile.toString()+"\"");
//            InputStream is=proc.getInputStream();
//            int i=0;
//
//            while ((i=is.read())!=-1)
//                sb.append((char)i);
//
//
//        } catch (Exception t)
//        {
//            t.printStackTrace();
//        }
                    // System.out.println("sb "+sb.toString());
                    // String sum32 = sb.toString().split(" ")[0].split("x")[1];
                    //    System.out.println("sum32 "+sum32);
                    //     System.out.println("path zip: "+zipfile.toString());
                    String crc32 = String.valueOf(xml.checksumBufferedInputStream(zipfile.toAbsolutePath().toString()));
                    XmlHelper.setXml(f + File.separator+"control.xml");
                    XmlHelper.setAttrValue(Tag.Containers, 0, "size", size);
                    XmlHelper.setAttrValue(Tag.Containers, 0, "CRC", crc32);
                    //Для создания общего control.zip
                    controlDataFileCreate.append("<Containers ReqUID=\""+reqUIDValue+"\" name=\"data_"+i+".zip\" size=\""+size+"\" CRC=\""+crc32+"\"/>\n");
                    i++;
                }

                if (f.contains("edit"))
                {
                    String pathEndFile=valuepath+File.separator+"edit";
                    new File(pathEndFile).mkdir();
                    Path zipfile = Paths.get(pathEndFile + File.separator+"data_"+j+".zip");
                    String reqUIDValue= XmlHelper.getAttrValue(Tag.MetaInf,"ReqUID");
                    xml.createZipFile(filesInFolder, zipfile);

                    File file = new File(zipfile.toUri());
                    String size = String.valueOf(file.length());
                    // String crc32 = xml.getCrc32(zipfile.toAbsolutePath());
//        StringBuffer sb =new StringBuffer();
//        try
//        {
//            Runtime rt = Runtime.getRuntime();
//            Process proc = rt.exec("cmd /c "+valuepath+ "\\crc32.exe "+"\""+zipfile.toString()+"\"");
//            InputStream is=proc.getInputStream();
//            int i=0;
//
//            while ((i=is.read())!=-1)
//                sb.append((char)i);
//
//
//        } catch (Exception t)
//        {
//            t.printStackTrace();
//        }
                    // System.out.println("sb "+sb.toString());
                    // String sum32 = sb.toString().split(" ")[0].split("x")[1];
                    //    System.out.println("sum32 "+sum32);
                    //     System.out.println("path zip: "+zipfile.toString());
                    String crc32 = String.valueOf(xml.checksumBufferedInputStream(zipfile.toAbsolutePath().toString()));
                    XmlHelper.setXml(f + File.separator+"control.xml");
                    XmlHelper.setAttrValue(Tag.Containers, 0, "size", size);
                    XmlHelper.setAttrValue(Tag.Containers, 0, "CRC", crc32);

                    //Для создания общего control.zip
                    controlDataFileEdit.append("<Containers ReqUID=\""+reqUIDValue+"\" name=\"data_"+j+".zip\" size=\""+size+"\" CRC=\""+crc32+"\"/>\n");
                    j++;
                }
//                Path zipfile2 = Paths.get(f + "/control.zip");
//                List<Path> filesInFolder2 = new ArrayList<>();
//                filesInFolder2.add(new File(f + "/control.xml").toPath());
//                filesInFolder2.add(new File(f + "/control.sign").toPath());
//                xml.createZipFile(filesInFolder2, zipfile2);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (noDataGenFlag!=null)
        {
            System.out.println("================Generating NoData================");
            //Для создания NO_DATA control.zip
            Map<String, List<String>> igkForNoData=new HashMap<>();
            PostgreDbBuilder db=new PostgreDbBuilder(noDataGenFlag,"postgres","postgres");
            igkForNoData=db.getListIgk();
            StringBuilder controlNoDataFile=new StringBuilder();
            controlNoDataFile.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<con:Message xmlns:con=\"http://smb.mil.ru/integration/control\" PacketUID=\""+UUID.randomUUID().toString()+"\">\n");
            for (String key : igkForNoData.keySet())
            {
                controlNoDataFile.append("\t<Nodata GOZUID=\""+key+"\" PeriodEnd=\""+igkForNoData.get(key).get(0)+"\" PeriodStart=\""+igkForNoData.get(key).get(1)+"\"/>\n");
            }
            controlNoDataFile.append("</con:Message>");
            try {
                File pathToNoData=new File(valuepath+File.separator+"NoData");
                pathToNoData.mkdir();
                writeToFile(controlNoDataFile.toString(), pathToNoData.getAbsolutePath() + File.separator+"control.xml");
                writeToFile("dummy", pathToNoData.getAbsolutePath() + File.separator+"control.sign");

                Path pathUnionControl = Paths.get(pathToNoData.getAbsolutePath() + File.separator+"control.zip");
                List<Path> filesInUnionControlFolder = new ArrayList<>();
                filesInUnionControlFolder.add(new File(pathToNoData.getAbsolutePath() + File.separator+"control.xml").toPath());
                filesInUnionControlFolder.add(new File(pathToNoData.getAbsolutePath() + File.separator+ "control.sign").toPath());
                xml.createZipFile(filesInUnionControlFolder, pathUnionControl);
            }
            catch (Exception ex) {
                Logger.getLogger(Program.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (pathCrpt==null)
        {
            try {
                String pathEndFileControlCreate=valuepath+File.separator+"create";
                writeToFile("dummy",pathEndFileControlCreate+File.separator+"control.sign");
                controlDataFileCreate.append("</ns2:Message>");
                writeToFile(controlDataFileCreate.toString(),pathEndFileControlCreate+File.separator+"control.xml");
                Path pathUnionControl = Paths.get(pathEndFileControlCreate+File.separator+"control.zip");
                List<Path> filesInUnionControlFolder = new ArrayList<>();
                filesInUnionControlFolder.add(new File(pathEndFileControlCreate+File.separator+"control.xml").toPath());
                filesInUnionControlFolder.add(new File(pathEndFileControlCreate+File.separator+"control.sign").toPath());
                xml.createZipFile(filesInUnionControlFolder,  pathUnionControl);
                new File(pathEndFileControlCreate+File.separator+"control.sign").delete();
                new File(pathEndFileControlCreate+File.separator+"control.xml").delete();

                String pathEndFileControlEdit=valuepath+File.separator+"edit";
                writeToFile("dummy",pathEndFileControlEdit+File.separator+"control.sign");
                controlDataFileEdit.append("</ns2:Message>");
                writeToFile(controlDataFileEdit.toString(),pathEndFileControlEdit+File.separator+"control.xml");
                Path pathUnionControlEdit = Paths.get(pathEndFileControlEdit+File.separator+"control.zip");
                List<Path>  filesInUnionControlFolderEdit = new ArrayList<>();
                filesInUnionControlFolderEdit.add(new File(pathEndFileControlEdit+File.separator+"control.xml").toPath());
                filesInUnionControlFolderEdit.add(new File(pathEndFileControlEdit+File.separator+"control.sign").toPath());
                xml.createZipFile(filesInUnionControlFolderEdit,  pathUnionControlEdit);
                new File(pathEndFileControlEdit+File.separator+"control.sign").delete();
                new File(pathEndFileControlEdit+File.separator+"control.xml").delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                String pathEndFileControlCreate=valuepath+File.separator+"create";
                controlDataFileCreate.append("</ns2:Message>");
                writeToFile(controlDataFileCreate.toString(),pathEndFileControlCreate+File.separator+"control.xml");
                File pathSignC=new File (pathCrpt);
                File signNewC=new File (pathEndFileControlCreate +File.separator+pathSignC.getName());
                FileUtils.copyFile(pathSignC, signNewC);
                ProcessBuilder  processBuilder  = new ProcessBuilder();
                if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
                    processBuilder.command("cmd.exe", "/c");
                    processBuilder.directory(new File(signNewC.getParent()));
                    processBuilder.command(signNewC.getAbsolutePath(),
                            "-signf", "-thumbprint", keyCrpt, "-q20", "-cert", new File(pathEndFileControlCreate +
                            File.separator + "control.xml").getAbsolutePath());
                    Process process =processBuilder.start();
                    InputStream is = process.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    while ((br.readLine()) != null) {
                    }
                    process.destroy();
                }
                else {
                    processBuilder.command("/bin/bash", "-c");
                    processBuilder.directory(new File(signNewC.getParent()));
                    processBuilder.command("-signf", "-thumbprint", keyCrpt, "-q20", "-cert", new File(pathEndFileControlCreate + File.separator + "data.xml").getAbsolutePath());
                    Process process =processBuilder.start();
                    InputStream is = process.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    while (( br.readLine()) != null) {
                    }
                    process.destroy();
                }
                File olderFileC=new File(pathEndFileControlCreate +File.separator+ "control.xml.sgn");
                File newFileC=new File(pathEndFileControlCreate +File.separator+ "control.sign");
                boolean success =olderFileC.renameTo(newFileC);
                if (success)
                {
                    olderFileC.delete();
                }
                Thread.sleep(2000);
                signNewC.delete();

                Path pathUnionControl = Paths.get(pathEndFileControlCreate+File.separator+"control.zip");
                List<Path> filesInUnionControlFolder = new ArrayList<>();
                filesInUnionControlFolder.add(new File(pathEndFileControlCreate+File.separator+"control.xml").toPath());
                filesInUnionControlFolder.add(new File(pathEndFileControlCreate+File.separator+"control.sign").toPath());
                xml.createZipFile(filesInUnionControlFolder,  pathUnionControl);
                new File(pathEndFileControlCreate+File.separator+"control.sign").delete();
                new File(pathEndFileControlCreate+File.separator+"control.xml").delete();

                String pathEndFileControlEdit=valuepath+File.separator+"edit";
                File dataSignEdit=new File(pathEndFileControlEdit + File.separator + "control.sign");
                if (dataSignEdit.exists())
                {
                    dataSignEdit.delete();
                }
                controlDataFileEdit.append("</ns2:Message>");
                writeToFile(controlDataFileEdit.toString(),pathEndFileControlEdit+File.separator+"control.xml");
                File pathSignEdit=new File (pathCrpt);
                File signNewEdit=new File (pathEndFileControlEdit +File.separator+pathSignEdit.getName());
                FileUtils.copyFile(pathSignEdit, signNewEdit);
                ProcessBuilder  processBuilderEdit  = new ProcessBuilder();
                if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
                    processBuilderEdit.command("cmd.exe", "/c");
                    processBuilderEdit.directory(new File(signNewEdit.getParent()));
                    processBuilderEdit.command(signNewEdit.getAbsolutePath(),
                            "-signf", "-thumbprint", keyCrpt, "-q20", "-cert", new File(pathEndFileControlEdit +
                                    File.separator + "control.xml").getAbsolutePath());
                    Process process =processBuilderEdit.start();
                    InputStream is = process.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    while ((br.readLine()) != null) {
                    }
                    process.destroy();
                   }
                else {
                    processBuilderEdit.command("/bin/bash", "-c");
                    processBuilderEdit.directory(new File(signNewEdit.getParent()));
                    processBuilderEdit.command(signNewEdit.getAbsolutePath(),
                            "-signf", "-thumbprint", keyCrpt, "-q20", "-cert", new File(pathEndFileControlEdit +
                                    File.separator + "control.xml").getAbsolutePath());
                    Process process =processBuilderEdit.start();
                    InputStream is = process.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    while ((br.readLine()) != null) {
                    }
                    process.destroy();
                   }
                File olderFileEdit=new File(pathEndFileControlEdit +File.separator+ "control.xml.sgn");
                File newFileEdit = new File(pathEndFileControlEdit +File.separator+ "control.sign");
                boolean successEd=olderFileEdit.renameTo(newFileEdit);
                if (successEd)
                {
                    olderFileEdit.delete();
                }
                Thread.sleep(2000);
                signNewEdit.delete();
                Path pathUnionControlEdit = Paths.get(pathEndFileControlEdit+File.separator+"control.zip");
                List<Path>  filesInUnionControlFolderEdit = new ArrayList<>();
                filesInUnionControlFolderEdit.add(new File(pathEndFileControlEdit+File.separator+"control.xml").toPath());
                filesInUnionControlFolderEdit.add(new File(pathEndFileControlEdit+File.separator+"control.sign").toPath());
                xml.createZipFile(filesInUnionControlFolderEdit,  pathUnionControlEdit);
                new File(pathEndFileControlEdit+File.separator+"control.sign").delete();
                new File(pathEndFileControlEdit+File.separator+"control.xml").delete();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (deleteFiles!=null) {
            for (String f: sortedListAbsolutePathToZip) {
                try {
                    FileHelper.deleteAllFilesWithoutFileWithExtension(f, "zip");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println((double) (System.currentTimeMillis() - m));
    }




    public static int hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val;
    }
    public static void writeToFile(String text , String nameFile) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(nameFile));
        writer.write(text);
        writer.close();
    }
}
