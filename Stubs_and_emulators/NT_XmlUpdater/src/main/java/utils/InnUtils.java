package utils;
import org.apache.commons.lang3.RandomStringUtils;
import javax.annotation.Nonnull;

public class InnUtils {
    private final static int[] IP_N2 = {7, 2, 4, 10, 3, 5, 9, 4, 6, 8};
    private final static int[] IP_N1 = {3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8};
    private final static int[] UL_N1 = {2, 4, 10, 3, 5, 9, 4, 6, 8};

    /**
     * Генерирует ИНН индивидуального предпринимателя
     *
     * @return ИНН индивидуального предпринимателя
     */
    @Nonnull
    public static String generateIpINN() {
        String base = getSubjectNum() + getRNSNum() + getRecNum(6);
        base += calcN(base, IP_N2);
        return base + calcN(base, IP_N1);
    }

    /**
     * Генерирует ИНН юридического лица
     *
     * @return ИНН юридического лица
     */
    @Nonnull
    public static String generateUlINN() {
        String base = getSubjectNum() + getRNSNum() + getRecNum(5);
        return base + calcN(base, UL_N1);
    }

    private static String getSubjectNum() {
        String subjectNum;
        do {
            subjectNum = RandomStringUtils.randomNumeric(2);
        }
        while (subjectNum.equals("00"));
        return subjectNum;
    }

    private static String getRNSNum() {
        return RandomStringUtils.randomNumeric(2);
    }

    private static String getRecNum(int count) {
        return RandomStringUtils.randomNumeric(count);
    }

    private static int calcN(@Nonnull String inn, @Nonnull int[] c) {
        int sum = 0;
        for (int i = 0; i < inn.length(); i++)
            sum += (Integer.valueOf(String.valueOf(inn.charAt(i)))) * c[i];

        int n = sum % 11;
        if(n == 10)
            n = 0;

        return n;
    }

}
