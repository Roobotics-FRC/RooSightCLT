package org.usfirst.frc.team4373.roosightclt;

import org.usfirst.frc.team4373.roosight.*;

import java.awt.*;

/**
 * Parses command line arguments.
 * @author aaplmath
 */
public class Parser {
    private String inputFile;
    private String outputFile;
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

    public void setFilters(String hsv, String hsl, String rgb) {
        this.hsv = hsv;
        this.hsl = hsl;
        this.rgb = rgb;
    }

    public void setWidthRange(String min, String max) {
        this.minWidth = min;
        this.maxWidth = max;
    }

    public void setHeightRange(String min, String max) {
        this.minHeight = min;
        this.maxHeight = max;
    }

    public void setAreaRange(String min, String max) {
        this.minArea = min;
        this.maxArea = max;
    }

    public void setInputFile(String file) {
        this.inputFile = file;
    }

    public void setOutputFile(String file) {
        this.outputFile = file;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void parse() throws Exception {
        RooColorImage colorImage = new RooColorImage(inputFile);
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

        RooProcessor rooProcessor = new RooProcessor(config);
        RooBinaryImage thresh = rooProcessor.processImage(colorImage);
        thresh.blur(1);
        RooContour[] contours = rooProcessor.findContours(thresh);

        // Colors
        Color contourColor;
        int[] rgbComps = parseParams(this.color, 3);
        if (rgbComps != null) {
            contourColor = new Color(rgbComps[0], rgbComps[1], rgbComps[2]);
        } else {
            contourColor = Color.GREEN;
        }
        colorImage.drawContours(contours, new RooColor(contourColor), 3);
        colorImage.markContours(contours, new RooColor(contourColor), 2);

        // Output
        String outputLoc;
        if (this.outputFile != null) {
            outputLoc = this.outputFile;
        } else {
            outputLoc = inputFile + ".out.jpg";
        }
        colorImage.writeToFile(outputLoc);
    }

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
                throw new Exception(Integer.toString(expectedLength) + " arguments were expected," +
                        " but the following " + Integer.toString(strParamArray.length) +
                        " arguments were received: " + params);
            }
        }
        return null;
    }

}
