package org.usfirst.frc.team4373.roosightclt;

import org.usfirst.frc.team4373.roosight.RooBinaryImage;
import org.usfirst.frc.team4373.roosight.RooColor;
import org.usfirst.frc.team4373.roosight.RooColorImage;
import org.usfirst.frc.team4373.roosight.RooConfig;
import org.usfirst.frc.team4373.roosight.RooContour;
import org.usfirst.frc.team4373.roosight.RooProcessor;

import java.awt.Color;

/**
 * Parses command line arguments.
 * @author aaplmath
 */
public class Parser {
    private String inputFile;
    private String outputFile;
    private String saveLoc;
    private String hsv;
    private String hsl;
    private String rgb;
    private String minWidth;
    private String maxWidth;
    private String minHeight;
    private String maxHeight;
    private String minArea;
    private String maxArea;
    private String color;
    private String blur;
    private String inputCamera;

    /**
     * Sets the String values to parse for the various filter operations.
     * @param hsv A comma-separated list of six min/max values for hue, saturation, and value.
     * @param hsl A comma-separated list of six min/max values for hue, saturation, and luminance.
     * @param rgb A comma-separated list of six min/max values for red, green, and blue.
     */
    public void setFilters(String hsv, String hsl, String rgb) {
        this.hsv = hsv;
        this.hsl = hsl;
        this.rgb = rgb;
    }

    /**
     * Sets the String values to parse for the allowable width range.
     * @param min The minimum allowed width (--min-width).
     * @param max The maximum allowed width (--max-width).
     */
    public void setWidthRange(String min, String max) {
        this.minWidth = min;
        this.maxWidth = max;
    }

    /**
     * Sets the String values to parse for the allowable height range.
     * @param min The minimum allowed height (--min-height).
     * @param max The maximum allowed height (--max-height).
     */
    public void setHeightRange(String min, String max) {
        this.minHeight = min;
        this.maxHeight = max;
    }

    /**
     * Sets the String values to parse for the allowable area range.
     * @param min The minimum allowed area (--min-area).
     * @param max The maximum allowed area (--max-area).
     */
    public void setAreaRange(String min, String max) {
        this.minArea = min;
        this.maxArea = max;
    }

    /**
     * Sets the String value to parse for the input file.
     * If this parameter is null, an exception will be thrown in parse().
     * @param file The String containing the path to the input file.
     */
    public void setInputFile(String file) {
        this.inputFile = file;
    }

    /**
     * Sets the String value for the camera's IP address.
     * @param camera The String containing the camera's IP address.
     */
    public void setInputCamera(String camera) {
        this.inputCamera = camera;
    }

    /**
     * Sets the String value to parse for the output file.
     * @param file The String containing the path to where the output file should be saved.
     */
    public void setOutputFile(String file) {
        this.outputFile = file;
    }

    /**
     * Sets the String value to parse for the file where serialized options will be saved.
     * @param file The String containing the path to where the file should be saved.
     */
    public void setSaveLocation(String file) {
        this.saveLoc = file;
    }

    /**
     * Sets the String values to parse for the contour colors to draw.
     * @param color A three-value, comma-separated String containing
     *              the RGB components of the desired color.
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Sets the String value to parse for the blur amount.
     * @param blur The amount between 0 and 1 to blur.
     */
    public void setBlur(String blur) {
        this.blur = blur;
    }

    /**
     * Executes operations based on the collected parameters. Should only be called once all
     * parameters have been loaded.
     * @throws Exception Thrown if integer parsing fails or if an invalid argument is passed.
     */
    public void parse() throws Exception {

        RooConfig config = new RooConfig();

        // Filters
        int[] hsvParams = parseParams(this.hsv, 6);
        if (hsvParams != null) config.setHSV(hsvParams[0], hsvParams[1], hsvParams[2],
                hsvParams[3], hsvParams[4], hsvParams[5]);
        int[] hslParams = parseParams(this.hsl, 6);
        if (hslParams != null) config.setHSL(hslParams[0], hslParams[1], hslParams[2],
                hslParams[3], hslParams[4], hslParams[5]);
        int[] rgbParams = parseParams(this.rgb, 6);
        if (rgbParams != null) config.setRGB(rgbParams[0], rgbParams[1], rgbParams[2],
                rgbParams[3], rgbParams[4], rgbParams[5]);

        // Width, height, area
        if (this.minWidth != null) config.setMinWidth(Integer.parseInt(this.minWidth));
        if (this.maxWidth != null) config.setMaxWidth(Integer.parseInt(this.maxWidth));
        if (this.minHeight != null) config.setMinHeight(Integer.parseInt(this.minHeight));
        if (this.maxHeight != null) config.setMaxHeight(Integer.parseInt(this.maxHeight));
        if (this.minArea != null) config.setMinArea(Integer.parseInt(this.minArea));
        if (this.maxArea != null) config.setMaxArea(Integer.parseInt(this.maxArea));
        if (this.blur != null) {
            config.setBlur(Integer.parseInt(blur));
        } else {
            config.setBlur(1);
        }

        // Colors
        int[] rgbComps = parseParams(this.color, 3);
        Color contourColor;
        if (rgbComps != null) {
            contourColor = new Color(rgbComps[0], rgbComps[1], rgbComps[2]);
        } else {
            contourColor = Color.GREEN;
        }

        // Serialization options
        if (this.saveLoc != null) config.save(saveLoc);

        RooProcessor processor = new RooProcessor(config);

        // Processing
        if (inputCamera != null) {
             Streamer stream = new Streamer();
             stream.streamFromCamera(inputCamera, image -> {
                 try {
                     RooColorImage colorImage = new RooColorImage(image);
                     RooBinaryImage thresh = processor.processImage(colorImage);
                     RooContour[] contours = processor.findContours(thresh);
                     // Fancy math stuff
                     // Put something to stdout or something
                 } catch (Exception exception) {
                     System.out.println("ERROR: " + exception.getLocalizedMessage());
                     System.exit(1);
                 }
             });
        } else {
            RooColorImage colorImage = new RooColorImage(inputFile);
            RooBinaryImage thresh = processor.processImage(colorImage);
            RooContour[] contours = processor.findContours(thresh);

            colorImage.drawContours(contours, new RooColor(contourColor), 3);
            colorImage.markContours(contours, new RooColor(contourColor), 2);
            String outputLoc;
            if (this.outputFile != null) {
                outputLoc = this.outputFile;
            } else {
                outputLoc = inputFile + ".out.jpg";
            }
            colorImage.writeToFile(outputLoc);
        }
    }

    /**
     * Parses a given set of comma-separated parameters passed to the CLT and converts them to
     * an int array, enforcing length checking.
     * @param params The String containing the parameters to parse.
     * @param expectedLength The expected number of parameters (i.e., the expected length of the
     *                       returned array).
     * @return An integer array containing the parsed values.
     * @throws Exception Throws if the number of parameters is not equal to the expected length.
     */
    private int[] parseParams(String params, int expectedLength) throws Exception {
        if (params != null) {
            String[] strParamArray = params.split(",");
            if (strParamArray.length == expectedLength) {
                int[] intParamArray = new int[expectedLength];
                for (int i = 0; i < expectedLength; ++i) {
                    intParamArray[i] = Integer.parseInt(strParamArray[i]);
                }
                return intParamArray;
            } else {
                throw new Exception(Integer.toString(expectedLength) + " arguments were expected,"
                        + " but the following " + Integer.toString(strParamArray.length)
                        + " arguments were received: " + params);
            }
        }
        return null;
    }

}
