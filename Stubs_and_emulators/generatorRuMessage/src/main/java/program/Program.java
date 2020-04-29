package program;

import stub.StubFile;
import db.JdbcConnector;
import org.apache.commons.cli.*;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Program {
    public static void main(String[] args)throws ParseException   {
        Options options = new Options();
        Option dataBaseInfo=new Option("db","infoDataBase",true,
                "The address of the database. Enter in the following format: <ip-address>:port");
        dataBaseInfo.setRequired(false);
        options.addOption(dataBaseInfo);

        Option countContract=new Option("c","countBlockContracts",true,"Number of generated contract blocks");
        countContract.setRequired(true);
        options.addOption(countContract);

        Option pathMessageXml=new Option("p","pathToGeneratedMessage",true,"Path to generated message");
        pathMessageXml.setRequired(false);
        options.addOption(pathMessageXml);

        Option infoOrg=new Option("i","infoOrganisation",true,"Info about the organization. " +
                "Enter in the following format: nameOrganisation;Inn;Kpp");
        infoOrg.setRequired(true);
        options.addOption(infoOrg);


        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd=null;
        try {
            cmd = parser.parse(options, args);
        } catch (org.apache.commons.cli.ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }
        String dBI = cmd.getOptionValue("db");
        String cC = cmd.getOptionValue("c");
        String pMx = cmd.getOptionValue("p");
        String iO=cmd.getOptionValue("i");

        if (dBI!=null) {
            JdbcConnector jc = new JdbcConnector("jdbc:postgresql://" + dBI + "/moprivfastdb", "postgres", "postgres", cC);
            ArrayList<String> queryResult = jc.getArrayContract();
            jc.saveToXml(pMx, queryResult);
        }
        else {
            StubFile sf=new StubFile();
            sf.generateMessageXml(cC,pMx,iO);
        }
    }
}
