package utils;
import java.util.Random;

public class DataGenerator {
    public static String getRandomAccNumber() {

        String[] firstPartValues = {"40606", "40506", "40706", "40825"};
        String firstPart = firstPartValues[Integer.valueOf(
                getRandomNumberInRange(0, firstPartValues.length - 1))];

        return firstPart + getRandomNumber(15);
    }

    public static String getRandomGozUid() {
        String yearOfContractStart =  getRandomNumberInRange(1980, 2030);
        String dig_1_2 = yearOfContractStart.substring(2);

        String dig_3_4 = String.valueOf(Integer.valueOf(getRandomNumberInRange(0, 14)) + Integer.valueOf(yearOfContractStart))
                .substring(2);

        String dig_5_6_7 = "187";
        String dig_8 = getRandomNumberInRange(1, 9);
        String dig_9_10_11_12 = getRandomNumber(4);
        String dig_13 = getRandomNumberInRange(1, 3);
        String dig_14_15_16_17_18_19_20_21_22_23_24_25 = getRandomNumber(12);

        String result = dig_1_2 + dig_3_4 + dig_5_6_7 + dig_8 + dig_9_10_11_12 + dig_13 + dig_14_15_16_17_18_19_20_21_22_23_24_25;


        return result;
    }

    public static String getRandomInn(String oldINN) {

        return getRandomInn(oldINN.length());
    }

    public static String getRandomInn(int digCount) {

        return digCount == 12 ? InnUtils.generateIpINN() : InnUtils.generateUlINN();
    }

//    public static String getRandomInn() {
//
//        return getRandomInn(12);
//    }

    public static String getRandomOgrn(String oldOGRN) {

        return oldOGRN.length() == 13 ? OgrnUtils.generateOGRN() : OgrnUtils.generateOGRNIP();
    }

    public static String getRandomKPP(String oldKPP){
        String middle = oldKPP.substring(4,6);
        String firstPart = getRandomNumber(4);
        String secondPart = getRandomNumber(3);
        String result = firstPart.concat(middle).concat(secondPart);
        return result;
    }


    public static String getRandomNumber(int digCount) {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(digCount);
        for (int i = 0; i < digCount; i++)
            sb.append((char) ('0' + rnd.nextInt(10)));
        return sb.toString();
    }

    public static String getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return String.valueOf(r.nextInt((max - min) + 1) + min);
    }

}
