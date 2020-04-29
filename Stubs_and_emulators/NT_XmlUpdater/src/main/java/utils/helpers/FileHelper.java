package utils.helpers;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.springframework.core.CollectionFactory;
//import tests.ui.features.UITests;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FileHelper {

//    public static String getTextFromResource(String fileName) {
//        String result = "";
//        ClassLoader classLoader = UITests.class.getClassLoader();
//        try {
//            result = IOUtils.toString(classLoader.getResourceAsStream(fileName), "UTF-8");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return result;
//    }
//
//    public static URI getResourceUri(String fileName) {
//
//        ClassLoader classLoader = UITests.class.getClassLoader();
//        URI uri = null;
//        try {
//            uri = classLoader.getResource(fileName).toURI();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//
//        return uri;
//    }
//
//    public static File createRandomXmlFile(int bytes) {
//        try {
//            File tempFile = File.createTempFile(GenerateDataHelper.getUniqueId(), ".xml");
//            RandomAccessFile f = new RandomAccessFile(tempFile, "rw");
//            f.setLength(bytes);
//            f.close();
//            return tempFile;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public static String getTextFromFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при доступе к файлу");
        }
    }

    public static void saveStringToFile(String data, String filePath) {
        try {
            Files.write(Paths.get(filePath), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при доступе к файлу");
        }
    }

    public static boolean isFileExist(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    public static int getNumberOfFilesInFolder(String folder) {
        try (Stream<Path> files = Files.list(Paths.get(folder))) {
            return (int) files.count();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при подсчете файлов");
        }
    }

    public static List<Path> getPathsOfFilesWithExtension(String folderPath, String extension) throws InterruptedException, TimeoutException {
        File[] files = new File(folderPath).listFiles();
        List<Path> paths = Arrays.asList(files).stream().filter(e->e.getAbsolutePath().toLowerCase().endsWith(extension)).map(e -> e.toPath()).collect(Collectors.toList());
        return paths;


    }

    public static List<Path> getPathsOfFilesWithExtensionWithoutControlXml(String folderPath, String extension) throws InterruptedException, TimeoutException {
        File[] files = new File(folderPath).listFiles();
        List<Path> paths = Arrays.asList(files).stream().filter(e->e.getAbsolutePath().toLowerCase().endsWith(extension)).map(e -> e.toPath()).collect(Collectors.toList());
        Iterator<Path> pathIterator = paths.iterator();//создаем итератор
        while(pathIterator.hasNext()) {//до тех пор, пока в списке есть элементы
            Path nextPath = pathIterator.next();//получаем следующий элемент
            if (nextPath.getFileName().toString().equals("control.xml")) {
                pathIterator.remove();
            }
        }

        return paths;
    }

    // Function to get the List
    public static <T> List<T>
    getListFromIterator(Iterator<T> iterator)
    {

        // Convert iterator to iterable
        Iterable<T> iterable = () -> iterator;

        // Create a List from the Iterable
        List<T> list = StreamSupport
                .stream(iterable.spliterator(), false)
                .collect(Collectors.toList());

        // Return the List
        System.out.println("list " + list.toString());
        return list;
    }

    public static void deleteAllFilesWithoutFileWithExtension(String f, String zip) {
        File folder = new File(f);
        File fList[] = folder.listFiles();
// Searchs .lck
        for (int i = 0; i < fList.length; i++) {
            File pes = fList[i];
            if (!pes.getName().contains(zip)) {
                // and deletes
                pes.delete() ;
            }
        }
    }
}

