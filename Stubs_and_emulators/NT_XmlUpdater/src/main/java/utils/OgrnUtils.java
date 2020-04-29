package utils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.ZoneId;

public class OgrnUtils {
    private final static String[] OGRN_SIG = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private final static String[] OGRNIP_SIG = {"3", "4"};
    private final static int YEAR_START = 1990;

    /**
     * Генерирует ОГРН
     *
     * @return строку с ОГРН
     */
    public static String generateOGRN() {
        String base = getGosRegSig(false) + getYear() + getSubjectNum() + getFnsNum() + getRecNum();
        return base + getChek(base);
    }

    /**
     * Генерирует ОГРНИП
     *
     * @return строку с ОГРНИП
     */
    public static String generateOGRNIP() {
        String base = getGosRegSig(true) + getYear() + getSubjectNum() + getIPRecNum();
        return base + getChek(base);
    }

    /**
     * Признак отнесения государственного регистрационного номера записи
     *
     * @param ogrnip Для ОГРНИП
     * @return строку из 1 символа
     */
    @Nonnull
    private static String getGosRegSig(boolean ogrnip) {
        String[] sig = ogrnip ? OGRNIP_SIG : OGRN_SIG;
        return sig[RandomUtils.nextInt(0, sig.length)];
    }

    /**
     * Две последние цифры года внесения записи в государственный реестр
     *
     * @return строку из 2х символов
     */
    @Nonnull
    private static String getYear() {
        int yearEnd = Instant.now().atZone(ZoneId.systemDefault()).getYear();
        int year = RandomUtils.nextInt(YEAR_START, yearEnd);

        return Integer.toString(year).substring(2, 4);

    }

    /**
     * Порядковый номер субъекта Российской Федерации
     *
     * @return строку из 2х символов
     */
    @Nonnull
    private static String getSubjectNum() {
        return RandomStringUtils.randomNumeric(2);
    }

    /**
     * Код налоговой инспекции
     *
     * @return строку из 2х символов
     */
    @Nonnull
    private static String getFnsNum() {
        return RandomStringUtils.randomNumeric(2);
    }

    /**
     * Номер записи, внесенной в государственный реестр в течение года
     *
     * @return строку из 5 символов
     */
    @Nonnull
    private static String getRecNum() {
        return RandomStringUtils.randomNumeric(5);
    }

    /**
     * Номер записи, внесенной в государственный реестр в течение года
     *
     * @return строку из 9 символов
     */
    @Nonnull
    private static String getIPRecNum() {
        return RandomStringUtils.randomNumeric(9);
    }

    /**
     * Контрольное число
     *
     * @param base ОГРН или ОГРНИП без контрольного числа
     * @return контрольное сичло в виде строки из 1 символа
     */
    @Nonnull
    private static String getChek(@Nonnull String base) {
        int num = base.length() == 12 ? 11 : 13;
        long val = Long.parseLong(base) % num;
        if (val >= 10)
            val = val % 10;
        return Long.toString(val);
    }

}
