package utils.jpgObjects;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JpgBuilder {
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

    public static void generateJpg(String path) {
        try {
            int width = 250;
            int height = 250;
            // Constructs a BufferedImage of one of the predefined image types.
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            // Create a graphics which can be used to draw into the buffered image
            Graphics2D g2d = bufferedImage.createGraphics();
            paintRand(g2d);

            // Disposes of this graphics context and releases any system resources that it is using.
            g2d.dispose();

            // Save as JPG
            File file = new File(path);
            ImageIO.write(bufferedImage, "jpg", file);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (OutOfMemoryError mem) {
            System.err.println("OUT OF MEMORY!!!!!!! " + printMem());
            mem.printStackTrace();
        }
    }

    public static void generateJpeg(String path) {
        try {
            int width = 250;
            int height = 250;
            // Constructs a BufferedImage of one of the predefined image types.
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            // Create a graphics which can be used to draw into the buffered image
            Graphics2D g2d = bufferedImage.createGraphics();
            paintRand(g2d);

            // Disposes of this graphics context and releases any system resources that it is using.
            g2d.dispose();

            // Save as JPEG
            File file = new File(path);
            ImageIO.write(bufferedImage, "jpeg", file);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (OutOfMemoryError mem) {
            System.err.println("OUT OF MEMORY!!!!!!! " + printMem());
            mem.printStackTrace();
        }
    }

    private static void paintRand(Graphics2D g2) {
        int count = rand.nextInt(25);
        for (int i = 0; i < count; i++) {
            g2.setColor(colors.get(rand.nextInt(colors.size())));
            g2.fillOval(rand.nextInt(210), rand.nextInt(210), rand.nextInt(55), rand.nextInt(55));
        }
    }
    public static String generateRandomPhrase() {
        int count = rand.nextInt(25);
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
