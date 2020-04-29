package utils;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class JsonWork {

    JSONObject parentObj;
    public JsonWork() {
        parentObj=new JSONObject();
            JSONArray listContracts =new JSONArray();
                for (int i=0;i<1;i++) {
                    JSONObject valueContractArgs=new JSONObject();
                    valueContractArgs.put("contractSum", "1000000");
                    valueContractArgs.put("contractGozuid", String.valueOf(generateRandomNumber(25)));
                    JSONObject valueContractPeriod = new JSONObject();
                    valueContractPeriod.put("start", "2019-01-01");
                    valueContractPeriod.put("end", "2019-05-01");
                    valueContractArgs.put("contractPeriod", valueContractPeriod);
                    listContracts.add(valueContractArgs);
                }
        parentObj.put("contractArgs", listContracts);
            JSONArray listNoData =new JSONArray();
                for (int i=0;i<5;i++) {
                    JSONObject valueNoData=new JSONObject();
                    valueNoData.put("gozuid", String.valueOf(generateRandomNumber(25)));
                    valueNoData.put("lastImportTime", "2018-12-31 23:59:59");
                    listNoData.add(valueNoData);
                }
        parentObj.put("noData", listNoData);
            JSONObject valuePartyMatrixSizes =new JSONObject();
                JSONObject dataLevels=new JSONObject();
                dataLevels.put("start",3);
                dataLevels.put("end",3);
            valuePartyMatrixSizes.put("levels",dataLevels);
                JSONObject dataСlusters=new JSONObject();
                dataСlusters.put("start",3);
                dataСlusters.put("end",3);
            valuePartyMatrixSizes.put("clusters",dataСlusters);
                JSONObject valueLevelSizeInitial=new JSONObject();
                valueLevelSizeInitial.put("start",3);
                valueLevelSizeInitial.put("end",3);
            valuePartyMatrixSizes.put("levelSizeInitial",valueLevelSizeInitial);
                JSONObject valueLevelSizeGrowthFactor=new JSONObject();
                valueLevelSizeGrowthFactor.put("start",5);
                valueLevelSizeGrowthFactor.put("end",5);
            valuePartyMatrixSizes.put("levelSizeGrowthFactor",valueLevelSizeGrowthFactor);
        parentObj.put("partyMatrixSizes",valuePartyMatrixSizes);
            JSONArray listCounterParties =new JSONArray();
//            for (int i=0;i<1;i++) {
//                JSONObject valueCounterParties=new JSONObject();
//                valueCounterParties.put("opf", "ИП");
//                valueCounterParties.put("name", "Название"+String.valueOf(generateRandomNumber(5)));
//                valueCounterParties.put("inn", String.valueOf(generateRandomNumber(10)));
//                valueCounterParties.put("ogrn", String.valueOf(generateRandomNumber(13)));
//                valueCounterParties.put("kpp", String.valueOf(generateRandomNumber(9)));
//                listCounterParties.add(valueCounterParties);
//            }
        parentObj.put("counterparties",listCounterParties);
            JSONObject valueCounterpartyGeneration =new JSONObject();
            valueCounterpartyGeneration.put("mode","from_file");
            valueCounterpartyGeneration.put("explicitHeadPartyFlag",true);
        parentObj.put("counterpartyGeneration",valueCounterpartyGeneration);
            JSONObject valueMoneyFlowWeights =new JSONObject();
                JSONObject valuedownstreamSameCluster=new JSONObject();
                valuedownstreamSameCluster.put("start",70);
                valuedownstreamSameCluster.put("end",80);
            valueMoneyFlowWeights.put("downstreamSameCluster",valuedownstreamSameCluster);
                JSONObject valuedownstreamOtherClusters=new JSONObject();
                valuedownstreamOtherClusters.put("start",10);
                valuedownstreamOtherClusters.put("end",20);
            valueMoneyFlowWeights.put("downstreamOtherClusters",valuedownstreamOtherClusters);
                JSONObject valueoutOfCooperation=new JSONObject();
                valueoutOfCooperation.put("start",5);
                valueoutOfCooperation.put("end",10);
            valueMoneyFlowWeights.put("outOfCooperation",valueoutOfCooperation);
//            valueMoneyFlowWeights.put("taxes",valuetaxes);
//                JSONObject valuesalary=new JSONObject();
//                valuesalary.put("start",5);
//                valuesalary.put("end",10);
//            valueMoneyFlowWeights.put("salary",valuesalary);
//                JSONObject valuespecial3M=new JSONObject();
//                valuespecial3M.put("start",10);
//                valuespecial3M.put("end",20);
//            valueMoneyFlowWeights.put("special3M",valuespecial3M);
        parentObj.put("moneyFlowWeights",valueMoneyFlowWeights);
            JSONObject valuesubcontracts =new JSONObject();
                JSONObject docNumberLength =new JSONObject();
                    docNumberLength.put("start",32);
                    docNumberLength.put("end",32);
            valuesubcontracts.put("docNumberLength",docNumberLength);
        parentObj.put("subcontracts",valuesubcontracts);
            JSONObject valueaggregatedPaymentSplit =new JSONObject();
            valueaggregatedPaymentSplit.put("maxNumber",20);
            valueaggregatedPaymentSplit.put("avgAmount",1000);
        parentObj.put("aggregatedPaymentSplit",valueaggregatedPaymentSplit);
            JSONObject valuecontainerDataSplit=new JSONObject();
            valuecontainerDataSplit.put("daysInContainer",1);
        parentObj.put("containerDataSplit",valuecontainerDataSplit);
            JSONObject valueacceptanceActs=new JSONObject();
            valueacceptanceActs.put("paymentChance",0.4);
            JSONObject valuedocNumberLength=new JSONObject();
                valuedocNumberLength.put("start",52);
                valuedocNumberLength.put("end",62);
            valueacceptanceActs.put("docNumberLength",valuedocNumberLength);
        parentObj.put("acceptanceActs",valueacceptanceActs);
            JSONObject valueaccountUpdates=new JSONObject();
            valueaccountUpdates.put("versionCountFactor",0.2);
            valueaccountUpdates.put("versionCountBound",4);
            valueaccountUpdates.put("requisitesUpdateChance",0.95);
            valueaccountUpdates.put("fileCountFactor",0.2);
            valueaccountUpdates.put("fileCountBound",2);
            valueaccountUpdates.put("severalPerPeriod",false);
        parentObj.put("accountUpdates",valueaccountUpdates);
            JSONObject valuepaymentCorrections=new JSONObject();
            valuepaymentCorrections.put("correctionChance", 0.02);
            valuepaymentCorrections.put("correctionMode", "edit_source_op");
        parentObj.put("paymentCorrections",valuepaymentCorrections);
            JSONObject valueattachments=new JSONObject();
            valueattachments.put("maxFiles",3460);
            valueattachments.put("maxDocTypes",3460);
            valueattachments.put("accountCreationFactor",0.05);
            valueattachments.put("accountCreationBound", 2);
            valueattachments.put("contractCustomerFactor",0.05);
            valueattachments.put("contractCustomerBound",1);
            valueattachments.put("contractPerformerFactor", 0.05);
            valueattachments.put("contractPerformerBound",1);
            valueattachments.put("paymentPayerFactor",0.05);
            valueattachments.put("paymentPayerBound", 1);
            valueattachments.put("paymentRecipientFactor", 0.05);
            valueattachments.put("paymentRecipientBound",1);
            valueattachments.put("debitPayerFactor",0.05);
            valueattachments.put("debitPayerBound", 1);
            valueattachments.put("acceptanceActCustomerFactor", 0.05);
            valueattachments.put("acceptanceActCustomerBound", 1);
            valueattachments.put("acceptanceActPerformerFactor", 0.05);
            valueattachments.put("acceptanceActPerformerBound", 2);
        parentObj.put("attachments",valueattachments);
            JSONObject valuebankReqs=new JSONObject();
            valuebankReqs.put("id","RC gena_bank");
            valuebankReqs.put("bankName","gena_bank");
            valuebankReqs.put("bankCorrAcc", "11111111111111111111");
            valuebankReqs.put("bankBranchName", "1");
            valuebankReqs.put("bankBIK", "112233445");
            valuebankReqs.put("bankINN", "1234567898");
            valuebankReqs.put("bankKPP", "554433221");
            valuebankReqs.put("cryptoMode", "dummy");
            valuebankReqs.put("cryptoWsUrl", "http://172.17.7.60:8081/mo/integration/soapGateway");
            valuebankReqs.put("cryptoCaId", "1");
            valuebankReqs.put("sessionWsUrl", "http://172.17.7.60:8081/mo/integration/soapGateway");
            valuebankReqs.put("sessionUserSn", "54dab300c0a8a6a847e3ee55d511e25f");
            valuebankReqs.put("ftpUrl", "sftp://172.17.7.60/home/ftpuser/ftp/gena_bank");
            valuebankReqs.put("ftpUser", "gena_bank");
            valuebankReqs.put("ftpPathMode","absolute");
                JSONObject valuezkDatabase=new JSONObject();
                valuezkDatabase.put("jdbcUrl", "jdbc:postgresql://172.17.7.64:5432/moprivfastdb");
                valuezkDatabase.put("username", "postgres");
                valuezkDatabase.put("password", "postgres");
            valuebankReqs.put("zkDatabase",valuezkDatabase);
            valuebankReqs.put("minFtpUploadDelay", "60");
            valuebankReqs.put("maxReplyDelay", "1800");
            valuebankReqs.put("sessionCloseRequest", "false");
        parentObj.put("bankReqs",valuebankReqs);
            JSONObject valueaccounts=new JSONObject();
            valueaccounts.put("randomAccountStat", true);
        parentObj.put("accounts",valueaccounts);
            JSONObject valuereorganisation=new JSONObject();
                JSONObject valuemerge=new JSONObject();
                    valuemerge.put("enabled",true);
                    valuemerge.put("useRandom",true);
                    valuemerge.put("countRandom",2);
                    JSONArray listCP=new JSONArray();
                    valuemerge.put("counterparties",listCP);
            valuereorganisation.put("merge", valuemerge);
            JSONObject valueaccession=new JSONObject();
                valueaccession.put("enabled",false);
                valueaccession.put("sourceRandom",true);
                valueaccession.put("sourceCountRandom",1);
                JSONArray listSourceCP=new JSONArray();
                valueaccession.put("sourceCounterparties",listSourceCP);
                valueaccession.put("targetRandom",true);
                valueaccession.put("targetCounterparty",null);
            valuereorganisation.put("accession",valueaccession);
            JSONObject valueseparation=new JSONObject();
                valueseparation.put("enabled",false);
                valueseparation.put("sourceRandom",true);
                valueseparation.put("sourceCounterparty",null);
                valueseparation.put("targetCount",2);
            valuereorganisation.put("separation",valueseparation);
            JSONObject valueallocation=new JSONObject();
                valueallocation.put("enabled",false);
                valueallocation.put("sourceRandom",true);
                valueallocation.put("sourceCounterparty",null);
                valueallocation.put("targetCount",1);
            valuereorganisation.put("allocation",valueallocation);
            JSONObject valuetransforming=new JSONObject();
                valuetransforming.put("enabled",false);
                valuetransforming.put("useRandom",true);
                valuetransforming.put("countRandom",1);
                    JSONArray listCPS=new JSONArray();
                    valuetransforming.put("counterparties",listCPS);
                valuereorganisation.put("transforming",valuetransforming);
        parentObj.put("reorganisation",valuereorganisation);
         System.out.print(parentObj.toString());
    }

    public String sendJson()
    {
        String result=null;
        try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();) {

            HttpPost request = new HttpPost("http://gendata.corp.tecforce:8082/schema/packets2");
            StringEntity params =new StringEntity(parentObj.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            if (response != null) {
                result = IOUtils.toString(response.getEntity().getContent());
            }

        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return  result;
    }

    public BigInteger generateRandomNumber(int length ) {
        Random random=new Random();
        StringBuilder builder = new StringBuilder(length);
        builder.append(random.nextInt(9) + 1);
        for (int i = 1; i < length; i++) {
            builder.append(random.nextInt(10));
        }
        return new BigInteger(builder.toString());
    }

    public void  generateXml()
    {
        StringBuilder sb=new StringBuilder();
        StringBuilder AccReps=new StringBuilder();
        for (int i=2213;i<=2900;i++)
        {
            String valueId="651de7a2-17bb-4da2-bc36-"+String.valueOf(generateRandomNumber(12));
            sb.append("<FileLinks Id=\""+valueId+"\" Name=\"filetest_"+String.valueOf(i)+".jpg\" Size=\"49333\" CRC=\"3826961580\" Comment=\"Random file\">\n");
            sb.append("\t<DocType>1</DocType>\n");
            sb.append("\t<DocType>2</DocType>\n");
            sb.append("\t<DocType>3</DocType>\n");
            sb.append("\t<DocType>4</DocType>\n");
            sb.append("\t<DocType>5</DocType>\n");
            sb.append("\t<DocType>6</DocType>\n");
            sb.append("\t<DocType>7</DocType>\n");
            sb.append("\t<DocType>8</DocType>\n");
            sb.append("\t<DocType>9</DocType>\n");
            sb.append("\t<DocType>10</DocType>\n");
            sb.append("\t<DocType>11</DocType>\n");
            sb.append("\t<DocType>12</DocType>\n");
            sb.append("\t<DocType>13</DocType>\n");
            sb.append("\t<DocType>14</DocType>\n");
            sb.append("\t<DocType>15</DocType>\n");
            sb.append("</FileLinks>\n");

            AccReps.append("<AccReps AccRepUID=\"c3d8eef6-dd12-4c13-907f-15af2d52d408\" GOZUID=\"3159023189507630121785187\" Num=\"585219261032548287862027359738970137346899464749982824\" Date=\"2019-03-01\" Cost=\"13733\">");
            AccReps.append("\n\t<Contractor ID=\"b8753c4e-1881-4a71-8965-12ec70cce8d9\" OGRN=\"1027802765110\" INN=\"7805152182\" KPP=\"781001001\"/>");
            AccReps.append("\n\t<ContractorCorr ID=\"f291ba23-5a88-4324-af10-29f6933a7ca4\" OGRN=\"1127847453403\" INN=\"7806485258\" KPP=\"780601001\"/>");
            AccReps.append("\n\t<Contract ContractUID=\"5d046932-a4de-4599-9348-b7a2f5a3dd41\" GOZUID=\"3159023189507630121785187\"/>");
            AccReps.append("\n\t<FileAttach Id=\"d6f01c79-416a-4199-a276-"+valueId+"\"/>\n</AccReps>\n");
        }
       System.out.println( sb.toString());

        System.out.println(AccReps.toString());
    }
}

