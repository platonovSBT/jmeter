package db;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JdbcConnector {

    private String url;
    private String name;
    private String pwd;
    private String countContract;
    private ArrayList<String> arrayResult;
    Connection connection;
    PreparedStatement preparedStatement ;
    ResultSet resultSql;
    String date="2999-12-31 23:59:59";

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
    public String getCountContract() {
        return countContract;
    }
    public void setCountContract(String countContract) {
        this.countContract = countContract;
    }

    public JdbcConnector(String url, String name, String pwd, String countContract) {
        this.url = url;
        this.name = name;
        this.pwd = pwd;
        this.countContract=countContract;
    }

    public ArrayList<String> getArrayContract()
    {
        arrayResult= new ArrayList<String>();

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(getUrl(), getName(), getPwd());
            preparedStatement = connection.prepareStatement(
                    " SELECT  DFO_NUM_REG,DFO_HEAD_INN,DFO_HEAD_KPP, DFO_BIK, DFO_TOTAL_PAY\n" +
                            "FROM  privfastsm.CONTRACT c  \n" +
                            "WHERE c.DFO_HEAD_INN IS NOT NULL  \n" +
                            "AND c.DFO_HEAD_KPP IS NOT NULL\n" +
                            "AND c.DFO_BIK IS NOT NULL\n" +
                            "AND c.STATUS='NOT_CLOSED' " +
                            "AND length(DFO_NUM_REG)>=25 " +
                            "LIMIT ?; ");
            preparedStatement.setInt(1, Integer.valueOf(getCountContract()));

            resultSql = preparedStatement.executeQuery();
            while (resultSql.next())
            {
                arrayResult.add(resultSql.getString(1)+";"+resultSql.getString(2)+";"+resultSql.getString(3)+";"+resultSql.getString(4)+";"+resultSql.getString(5));
            }
            connection.close();
        } catch (Exception ex) {
            Logger.getLogger(JdbcConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return arrayResult;
    }

    public void saveToXml(String path, ArrayList<String> data ) throws ParseException {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(getUrl(), getName(), getPwd());

            preparedStatement = connection.prepareStatement(
                    " SELECT FORM_DATE\n" +
                            "FROM privfastsm.DFO_IMPORT_PROTOCOL \n" +
                            "ORDER BY LOAD_DATE DESC\n" +
                            "LIMIT 1");
            resultSql = preparedStatement.executeQuery();
            while (resultSql.next())
            {
                date=resultSql.getString(1);
            }
            connection.close();
        } catch (Exception ex) {
            Logger.getLogger(JdbcConnector.class.getName()).log(Level.SEVERE, null, ex);

        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(sdf.parse(date));
        c.add(Calendar.DATE, 1);  // number of days to add
        date = sdf.format(c.getTime());  // dt is now the new date

        Date dateNew = sdf.parse(date); //получили дату
        sdf.applyPattern("dd.MM.yyyy hh:mm:ss"); //изменили шаблон
        date = sdf.format(dateNew);     //получили дату в нужном формате



        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"  standalone=\"yes\"?>\n" +
                "<dfo time=\""+date+"\">");
        for (String element: data)
        {
            String[] subElement=element.split(";");
            builder.append("\n\t<contract reg_date=\"20171017\" end_date=\"20281017\" rn=\""+subElement[0]+"\"" +
                    " status=\"Действует\" num=\""+subElement[0]+"\" num_reg=\""+subElement[0]+"\" sum=\""+subElement[4]+"\"" +
                    " head=\"ЗАО НПЦ Фирма &quot;НЕЛК&quot;\" head_inn=\""+subElement[1]+"\" head_kpp=\""+subElement[2]+"\"" +
                    " bik=\""+subElement[3]+"\" debet=\"0.00\" baddebet=\"0.00\" kredit=\"0.00\" total_pay=\""+subElement[4]+"\"" +
                    " accept=\"0.00\" advance=\"0.00\"> " +
                    "\n\t\t<payments> " +
                    "\n\t\t\t<pmt date=\"20170415\" acnt=\"40506810968250952511\" sub=\"(л/с 03951001870) Оплата за поставку ВВТ по ГК N 1616187148772592562015494 от 16.05.2016г. Товарная накладная N 323 от 10.11.2016г.В т.ч. НДС 18% - 5112590,24 руб.\" " +
                    "sum=\""+getRandomNumberInRange(1,Double.parseDouble(subElement[4]))
                    +"\" ndoc=\"GENA2017042807343988808991\"" +
                    " bikp=\"112233445\" av=\"Да\"/> " +

                    "\n\t\t\t<pmt date=\"20160725\" acnt=\"40506810400000000078\" sub=\"(л/с 03951001870)  ГК N 1618187140042452466001569 от 12.05.2016г. Оплата аванса на НИР в сумме 50 000 000,00 рублей.\n" +
                    "НДС не облаг.\" sum=\""+getRandomNumberInRange(1,Double.parseDouble(subElement[4]))
                    +"\" ndoc=\"GENA2017042807343988808991\"" +
                    " bikp=\"112233445\" av=\"Да\"/> " +

                    "\n\t\t\t<pmt date=\"20160609\" acnt=\"40101810500000001901\" sub=\"(л/с 03951001870) Частичная оплата за гсм по ГК N 1616187365712543149000000 от 17.05.2016г. Реестр счетов N 1011228 от 17.10.16г.\n" +
                    "Без НДС.\" sum=\""+getRandomNumberInRange(1,Double.parseDouble(subElement[4]))
                    +"\" ndoc=\"GENA2017042807343988808991\"" +
                    " bikp=\"112233445\" av=\"Да\"/> " +

                    "\n\t\t\t<pmt date=\"20161205\" acnt=\"40101810800000010041\" sub=\"(л/с 03951001870) Уплата НДС по расчетам с Федеральным агентством по государственным резервам. (За гсм по ГК N 1616187365712543149000000 от 17.05.2016г.)\"" +
                    " sum=\""+getRandomNumberInRange(1,Double.parseDouble(subElement[4]))
                    +"\" ndoc=\"GENA2017042807343988808991\"" +
                    " bikp=\"112233445\" av=\"Да\"/> " +

                    "\n\t\t\t<pmt date=\"20171215\" acnt=\"40706810239000000502\" sub=\"(л/с 03951001870) Оплата за поставку ВВТ по ГК № 1617187302312412209001695 от 05.05.2016г. Св. акт №1 от 25.11.16г. Сч.№2811005 от 28.11.16г.Уд. ав. 31096150,85 НДС 18% - 4743480,64\"" +
                    " sum=\""+getRandomNumberInRange(1,Double.parseDouble(subElement[4]))
                    +"\" ndoc=\"GENA2017042807343988808991\"" +
                    " bikp=\"112233445\" av=\"Да\"/> " +

                    "\n\t\t\t<pmt date=\"20160722\" acnt=\"40706810239000000502\" sub=\"(л/с 03951001870) Аванс на поставку ВВТ по ГК № 1617187302312412209001695 от 05.05.2016г.  (37,1%), ранее выдан аванса , срок пог. аванса 4 кв.2017 г., НДС 18% - 8207953,98 руб.\"" +
                    " sum=\""+getRandomNumberInRange(1,Double.parseDouble(subElement[4]))
                    +"\" ndoc=\"GENA2017042807343988808991\"" +
                    " bikp=\"112233445\" av=\"Да\"/> " +

                    "\n\t\t\t<pmt date=\"20161209\" acnt=\"40506810002000000198\" sub=\"(л/с 03951001870) Аванс на поставку ВВТ по ГК № 1617187302312412209001695 от 05.05.2016г.  (37,1%), ранее выдан аванса , срок пог. аванса 4 кв.2017 г., НДС 18% - 8207953,98 руб.\"" +
                    " sum=\""+getRandomNumberInRange(1,Double.parseDouble(subElement[4]))
                    +"\" ndoc=\"GENA2017042807343988808991\"" +
                    " bikp=\"112233445\" av=\"Да\"/> " +

                    "\n\t\t\t<pmt date=\"20161215\" acnt=\"40506810600510000651\" sub=\"(л/с 03951001870) Аванс на поставку ВВТ по ГК № 1617187302312412209001695 от 05.05.2016г.  (37,1%), ранее выдан аванса , срок пог. аванса 4 кв.2017 г., НДС 18% - 8207953,98 руб.\"" +
                    " sum=\""+getRandomNumberInRange(1,Double.parseDouble(subElement[4]))
                    +"\" ndoc=\"GENA2017042807343988808991\"" +
                    " bikp=\"112233445\" av=\"Да\"/> " +

                    "\n\t\t</payments>" +
                    "\n\t</contract>");
        }
        builder.append("\n</dfo>");

        try(OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8))
        {
            writer.write(builder.toString());
        }
        catch(IOException ex) {
            System.out.println(ex.getMessage());
        }

    }
    private static int getRandomNumberInRange(int min, double max) {

        max -= min;
        return (int) (Math.random() * ++max) + min;

    }
}
