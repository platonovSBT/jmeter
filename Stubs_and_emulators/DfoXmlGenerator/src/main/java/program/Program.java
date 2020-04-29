package program;

import db.JdbcConnector;

import java.text.ParseException;
import java.util.ArrayList;

public class Program
{
    public static void main(String[] args) throws ParseException {
        String path = args[0];
        String dbaConnect = args[1];
        String countContract = args[2];

        JdbcConnector jc=new JdbcConnector("jdbc:postgresql://"+dbaConnect+"/moprivfastdb","postgres","postgres",countContract);
        ArrayList<String> queryResult=jc.getArrayContract();
        jc.saveToXml(path,queryResult);
    }
}
