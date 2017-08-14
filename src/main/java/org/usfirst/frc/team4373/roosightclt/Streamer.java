package org.usfirst.frc.team4373.roosightclt;

import java.util.concurrent.atomic.AtomicBoolean;

public class Streamer {
    public interface StreamImageHandler {
        void handle(byte[] image);
    }

    private AtomicBoolean keepPolling = new AtomicBoolean(true);

    /**
     * I hate JavaDoc.
     * @param ip The IP address of the camera from which to stream.
     * @param handler The handler object whose handle() method will be called
     *                when an image is received.
     */
    public void streamFromCamera(String ip, StreamImageHandler handler) {
        Runnable run = () -> {
            while (keepPolling.get()) {
                // Get the image
                // Call handler.handle(bytes)
            }
        };
    }

    /**
     * Aborts streaming.
     */
    public void abort() {
        this.keepPolling.set(false);
    }
}
