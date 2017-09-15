package org.usfirst.frc.team4373.roosightclt;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.util.concurrent.atomic.AtomicBoolean;

public class Streamer implements Runnable {

    private String ip;
    private StreamImageHandler handler;

    private AtomicBoolean keepPolling = new AtomicBoolean(true);

    public interface StreamImageHandler {
        void handle(Mat image);
    }

    /**
     * Constructs a new Streamer object.
     * @param ip The IP of the camera from which to stream.
     * @param handler The StreamImageHandler to be called to process new images.
     */
    public Streamer(String ip, StreamImageHandler handler) {
        this.ip = ip;
        this.handler = handler;
    }

    @Override
    public void run() {
        VideoCapture capturer = new VideoCapture();
        capturer.open(ip + "&dummy=param.mjpg"); // TODO: Add logic
        Mat imageMat = new Mat();
        while (keepPolling.get()) {
            boolean didRead = capturer.read(imageMat);
            if (!didRead) System.out.println("[ERROR] Failed to read.");
            handler.handle(imageMat);
        }
    }

    /**
     * Aborts streaming.
     */
    public void abort() {
        this.keepPolling.set(false);
    }
}
