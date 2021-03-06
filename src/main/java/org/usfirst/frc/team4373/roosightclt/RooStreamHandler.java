package org.usfirst.frc.team4373.roosightclt;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import org.opencv.core.Mat;
import org.usfirst.frc.team4373.roosight.RooBinaryImage;
import org.usfirst.frc.team4373.roosight.RooColorImage;
import org.usfirst.frc.team4373.roosight.RooContour;
import org.usfirst.frc.team4373.roosight.RooProcessor;

import java.io.FileOutputStream;
import java.io.IOException;

public class RooStreamHandler implements Streamer.StreamImageHandler {
    private RooProcessor processor;

    public RooStreamHandler(RooProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void handle(Mat imageMat) {
        try {
            RooColorImage colorImage = new RooColorImage(imageMat);
            byte[] bytes = colorImage.getBytes();
            FileOutputStream out = new FileOutputStream("test-output-file");
            out.write(bytes);
            out.close();
            System.exit(1);
            RooBinaryImage thresh = processor.processImage(colorImage);
            RooContour[] contours = processor.findContours(thresh);
            double newOffset = 4373d;
            if (contours.length > 0) {
                double xPixel = contours[0].getCenter().getX();
                double conversionFactor = 47 / 640;
                newOffset = xPixel * conversionFactor;
            }
            System.out.println("[OFFSET] " + String.valueOf(newOffset));
            // NetworkTable.getTable("org.usfirst.frc.team4373.vision").putNumber("setpoint",
            //         newSetpoint);
        } catch (Exception exception) {
            System.out.println("ERROR: " + exception.getLocalizedMessage());
            System.exit(1);
        }
    }
}
