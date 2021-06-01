package services.utility;

import lombok.extern.log4j.Log4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.openqa.selenium.WebElement;

import java.io.*;
import java.net.URL;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static services.utility.ReportService.*;

@Log4j
public class FileReaderService {
    private static final String DATA_PROVIDER_FILE_DELIMITER = "-->";
    private static final String TEST_REPORTS_PATH = "target/reports/";
    private static final String FILE_PATH = TEST_REPORTS_PATH + "testng-failed.xml";
    private static String SUCCESS_FILE_PATH = TEST_REPORTS_PATH + "testng-results.xml";
    public static final String PROP_PATH = "src/test/resources/properties/";
    public static final String PROP_PATH_AUT = "properties/";

    public static List<String> listReader(String fileLocation){
        BufferedReader in;
        List<String> myList = new ArrayList<>();
        try {
            in = new BufferedReader(new FileReader(PROP_PATH+fileLocation));
            String str;
            while ((str = in.readLine()) != null) {
                myList.add(str);
            }
            in.close();
        }
        catch (IOException e){
            assertTrue(false, "Catch an exception " + e);
        }

        return myList;
    }

    public static Map<String, String> getMap(String fileLocation) {
        PropertyReader propertyReader = new PropertyReader(fileLocation);
        return new HashMap(propertyReader.getProperties());
    }

    public static String getValueWithReplace(String fileLocation, String key, String text){
        try {
            Map<String, String> map = new HashMap<>();
            BufferedReader in = new BufferedReader( new FileReader(fileLocation));
            String line;
            while ((line = in.readLine()) !=null){
                String parts[] = line.split("=");
                map.put(parts[0],parts[1]);
            }
            in.close();
            return map.get(key).replaceAll("PART", text);
        }
        catch (IOException e){
            assertTrue(false, "Catch an exception " + e);
            return null;
        }

    }

    public static String getLink(WebElement element){
        try {
            if (element.getTagName().equals("a")){
                log.info("Link - "+ element.getAttribute("href"));
                return element.getAttribute("href");
            }
            else {
                assertFalse(true, "link is not on a-tag.");
                return null;
            }
        }
        catch (NoSuchElementException e){
            assertFalse(true, "Not such element "+element);
            return null;
        }
    }

    public static HttpResponse getResponse(String link){
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(),SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new PlainConnectionSocketFactory())
                    .register("https", sslsf)
                    .build();

            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(2000);

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .setConnectionManager(cm)
                    .build();

            HttpResponse response = httpClient.execute(new HttpGet(link));
            return response;
        }
        catch (Exception e){
            assertFalse(true, "Catch "+e);
            return null;
        }
    }

    public static Map<String, String> getMap(String fileLocation, String delimiter){
        try {
            Map<String, String> map = new HashMap<>();
            BufferedReader in = new BufferedReader( new FileReader(fileLocation));
            String line;
            while ((line = in.readLine()) !=null){
                String parts[] = line.split(delimiter);
                map.put(parts[0],parts[1]);
            }
            in.close();
            return map;
        }
        catch (IOException e){
            assertTrue(false, "Catch an exception " + e);
            return null;
        }

    }

    private static void createDir(){
        File dir = new File(FILE_PATH.substring(0, FILE_PATH.lastIndexOf("/")));
        if (!dir.exists()){
            dir.mkdir();
            log.info("Create directory");
        }
    }

    public static Object[][] dataProviderFile(String filePath){
        List<String> emails = listReader(filePath);
        Object[][] objects = new Object[emails.size()][1];
        for (int i = 0; i < emails.size(); i++) {
            objects[i][0] = emails.get(i);
        }
        return objects;
    }

    /**
     * Converts strings from file to TestNG DataProvider applicable data.<br/>
     * Each parameter in file should be delimited with <code>'-->'</code>
     *
     * @param filePath Path to file with content for DataProvider
     * @return Array of objects for TestNG DataProvider
     */
    public static Object[][] readDataProviderFile(String filePath) {
        Object[][] fileContent = dataProviderFile(filePath);
        int dataProviderParametersSize = ((String) fileContent[0][0]).split(DATA_PROVIDER_FILE_DELIMITER).length;
        Object[][] dataProvider = new Object[fileContent.length][dataProviderParametersSize];

        for (int i = 0; i < fileContent.length; i++) {
            String[] currentLine = ((String) fileContent[i][0]).split(DATA_PROVIDER_FILE_DELIMITER);
            System.arraycopy(currentLine, 0, dataProvider[i], 0, dataProviderParametersSize);
        }

        return dataProvider;
    }

    /**
     * Converts strings from file to TestNG DataProvider applicable data.<br/>
     * Each parameter in file should be delimited with <code>'-->'</code>
     *
     * @param path Path to file with content for DataProvider
     * @return Array of objects for TestNG DataProvider
     */
    public static Iterator<Object[]> readFileAsDataProvider(String path) {
        try {
            URL systemResource = ClassLoader.getSystemResource(path);
            try{
                FileSystems.newFileSystem(systemResource.toURI(), Collections.emptyMap());
            }
            catch (FileSystemAlreadyExistsException e){
                FileSystems.getFileSystem(systemResource.toURI());
            }

            return Files.lines(Paths.get(systemResource.toURI()))
                    .parallel()
                    .map(s -> s.split(DATA_PROVIDER_FILE_DELIMITER))
                    .map(Object[].class::cast)
                    .collect(Collectors.toList())
                    .iterator();
        } catch (Exception e) {
            throw new RuntimeException("Cant read file as data provider", e);
        }

    }


    public static String fileReader(File file){
        BufferedReader in;
        String result = "";
        try {
            in = new BufferedReader(new FileReader(file));
            String str;
            while ((str = in.readLine()) != null) {
                result+=str+"\n";
            }
            in.close();
        }
        catch (IOException e){
            assertTrue(false, "Catch an exception " + e);
        }

        return result;
    }

    public static Object[][] dataProviderList(List list){
        Object[][] objects = new Object[list.size()][1];
        for (int i = 0; i < list.size(); i++) {
            objects[i][0] = list.get(i);
        }
        return objects;
    }

    public static void newFile(String pathFile, String bodyFile){
            File json = new File(pathFile);
            try {
                FileWriter writer = new FileWriter(json,true);
                writer.append(bodyFile);
                writer.flush();
                writer.close();
            }
            catch (IOException e){
                catchException(e);
            }
    }
}