package utils.pdfObjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public class PdfBuilder {
    private static Random rand = new Random();
    private static List<Color> colors = new ArrayList<Color>()
    {
        {
            add(new Color(19, 15, 166));
            add(new Color(90, 183, 39));
            add(new Color(180, 19, 12));
            add(new Color(186, 20, 164));
        }
    };

    public static void generatePdf(String path) {
        try {
            Rectangle rectPage = new Rectangle(0, 0, 210, 297);
            FileOutputStream fos = new FileOutputStream(new File(path));
            Document d = new Document(rectPage, 0, 0, 0, 0);
            PdfWriter writer = PdfWriter.getInstance(d, fos);
            int pageNo =1;
            d.open();
            for (int i = 0; i < pageNo; i++) {
                d.newPage();

                PdfContentByte cb;
                PdfTemplate tp;
                Graphics2D g2;

                cb = writer.getDirectContent();

                tp = cb.createTemplate(rectPage.getWidth(),
                        rectPage.getHeight());
                g2 = tp.createGraphicsShapes(rectPage.getWidth(),
                        rectPage.getHeight());

                paintRand(g2);

                g2.dispose();
                cb.addTemplate(tp, 0, 0);

                writer.flush();


            }
            d.close();
//            d.open();
//            int pageNo =1;
//            d.newPage();
//            d.add(new Paragraph(generateRandomPhrase()));
//            writer.flush();
//            d.close();

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (OutOfMemoryError mem) {
            System.err.println("OUT OF MEMORY!!!!!!! " + printMem());
            mem.printStackTrace();
        }
    }

    private static void paintRand(Graphics2D g2) {
        int count = rand.nextInt(10);
        for (int i = 0; i < count; i++) {
            g2.setColor(colors.get(rand.nextInt(colors.size())));
            g2.fillOval(rand.nextInt(210), rand.nextInt(210), rand.nextInt(55), rand.nextInt(55));
        }
    }
    public static String generateRandomPhrase() {
        int count = rand.nextInt(50);
        StringBuilder builder = new StringBuilder();

        for (int i = 1; i < count; i++) {
            builder.append("Valar morghulis! Valar dohaeris!\n");
        }
        return builder.toString();
    }



    public static Integer generateRandomNumber(int length ) {
        Random random=new Random();
        StringBuilder builder = new StringBuilder(length);
        builder.append(random.nextInt(9) + 1);
        for (int i = 1; i < length; i++) {
            builder.append(random.nextInt(10));
        }
        return new Integer(builder.toString());
    }

    private static String printMem() {
        final long usedMem = Runtime.getRuntime().totalMemory()
                - Runtime.getRuntime().freeMemory();
        String ram = "RAM: " + (usedMem / 1024 / 1024) + "MB / "
                + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + "MB";
        if (Runtime.getRuntime().maxMemory() != Long.MAX_VALUE) {
            ram += " (max limit: " + Runtime.getRuntime().maxMemory() / 1024
                    / 1024 + "MB)";
        } else {
            ram += " (no max limit)";
        }

        return ram;
    }
}
