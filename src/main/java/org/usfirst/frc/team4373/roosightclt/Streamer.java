package org.usfirst.frc.team4373.roosightclt;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;

public class Streamer implements Runnable {

    private String ip;
    private StreamImageHandler handler;

    /**
     * Constructs a new Streamer object.
     * @param ip The IP of the camera from which to stream.
     * @param handler The StreamImageHandler to be called to process new images.
     */
    public Streamer(String ip, StreamImageHandler handler) {
        this.ip = ip;
        this.handler = handler;
    }

    private AtomicBoolean keepPolling = new AtomicBoolean(true);
    private static final String USER_AGENT = "Mozilla/5.0";

    public interface StreamImageHandler {
        void handle(byte[] image);
    }

    @Override
    public void run() {
        System.out.println("Init");
        String url = this.ip;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);

            byte[] ib = null;
            ByteArrayInputStream bi = null;
            BufferedImage i;
            int counter = 0;
            System.out.println("hi");
            while (keepPolling.get()) {
                System.out.println("in loop");
                ib = nextIb(con.getInputStream());
                bi = new ByteArrayInputStream(ib);
                i = ImageIO.read(bi);
                ++counter;
                System.out.println("Got frame [" + counter + "] : Height " + i.getHeight()
                        + "; Width: " + i.getWidth());

            }
        } catch (Exception exception) {
            System.out.println(exception.getLocalizedMessage());
            System.exit(1);
        }
    }

    /**
     * Using the urlStream, get the next JPEG image as a byte[]
     *
     * @return byte[] of the JPEG
     * @throws IOException IO error.
     */
    private static byte[] nextIb(InputStream urlStream) throws IOException {
        System.out.println("test ib");
        int currByte = -1;
        StringWriter stringWriter = new StringWriter();
        // go to headers
        while ((currByte = urlStream.read()) > -1) {
            stringWriter.write(currByte);
            System.out.println("str1 - " + stringWriter.toString());
            if (stringWriter.toString().indexOf("myboundary") > 0) break;
        }

        System.out.println("str1 - skipped myboundary");
        // skip Content-Type
        while ((currByte = urlStream.read()) != '\n') {
            System.out.print(currByte);
        }
        System.out.println("\nContent-Type skipped...");
        // fetch Content-Length
        stringWriter.close();
        stringWriter = new StringWriter();
        while ((currByte = urlStream.read()) > 0) {
            stringWriter.write(currByte);
            System.out.println(stringWriter.toString());
            if (stringWriter.toString().equals("Content-Length: ")) break;
        }
        // now we're content length
        stringWriter.close();
        stringWriter = new StringWriter();
        while ((currByte = urlStream.read()) != '\n') {
            stringWriter.write(currByte);
        }
        int length = Integer.parseInt(stringWriter.toString());
        System.out.println(length);
        // read in
        byte[] imageBytes = new byte[length];
        urlStream.read(imageBytes); // TODO: Do we need this? IntelliJ says no…
        return imageBytes;
    }

    /**
     * Aborts streaming.
     */
    public void abort() {
        this.keepPolling.set(false);
    }
}
