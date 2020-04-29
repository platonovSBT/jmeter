package utils.postgreObjects;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgreDbBuilder {
    private String url;
    private String name;
    private String pwd;
    Connection connection;
    PreparedStatement preparedStatement ;
    boolean flag=false;
    int EVENT_TYPE_ID;
    String EVENT_TIME;
    ResultSet result;
    double timeWork;

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPwd() {
        return pwd;
    }
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    public PostgreDbBuilder(String url, String name, String pwd) {
        this.url = url;
        this.name = name;
        this.pwd = pwd;
    }

    public Map<String, List<String>> getListIgk()
    {
        Map<String, List<String>> listAttach=new HashMap<>();
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://"+getUrl()+"/moprivfastdb", getName(), getPwd());
            preparedStatement = connection.prepareStatement(
                    "SELECT IGK,IMPORTED_PERIOD_END,IMPORTED_PERIOD_START " +
                            "FROM privfastsm.contract_bank_period \n" +
                            "WHERE igk IN \n" +
                            "(SELECT REGISTER_NUMBER\n" +
                            "FROM privfastsm.contract\n" +
                            "WHERE BANK_ID=41\n" +
                            "LIMIT 1500)");
            result = preparedStatement.executeQuery();
                while (result.next())
                {
                    List<String> values = new ArrayList<>();
                    if (result.getString("IGK")==null || result.getString("IGK").isEmpty() )
                    {
                        continue;
                    }
                    if (result.getTime("IMPORTED_PERIOD_END")==null || result.getTime("IMPORTED_PERIOD_START")==null)
                    {
                        values.add("2019-01-02T00:00:00");
                        values.add("2019-01-01T00:00:00");
                        listAttach.put(result.getString("IGK"),values);
                    }
                    else
                    {
                        String endDate=(result.getString("IMPORTED_PERIOD_END")).toString();
                        String startTime=endDate.replace(" ","T");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Calendar c = Calendar.getInstance();
                        c.setTime(sdf.parse(endDate));
                        c.add(Calendar.DATE, 1);  // number of days to add
                        endDate = (sdf.format(c.getTime())).replace(" ","T");  // dt is now the new date

                        values.add(endDate);
                        values.add(startTime);
                        listAttach.put(result.getString("IGK"),values);
                    }
                }
            connection.close();
        } catch (Exception ex) {
            Logger.getLogger(PostgreDbBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listAttach;
    }
}
