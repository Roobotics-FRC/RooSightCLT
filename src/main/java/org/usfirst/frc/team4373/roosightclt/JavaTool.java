package org.usfirst.frc.team4373.roosightclt;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.opencv.core.Core;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Main Java CLT class.
 * @author aaplmath
 */
public class JavaTool {
    /**
     * Runs RooSight operations on the provided image based on the parameters passed.
     * @param args Command-line arguments to be passed to the CLT.
     */
    public static void main(String[] args) {

        // Automatically add OpenCV path

        String openCVPath = "/usr/local/Cellar/opencv3classic/3.3.0_1/share/OpenCV/java";
        String ntPath = "~/wpilib/java/current/lib/native/lib";
        ntPath = ntPath.replaceFirst("^~",System.getProperty("user.home"));

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            openCVPath = "C:\\Users\\robotics\\Downloads\\opencv\\build\\java\\x64";
        }

        try {
            // Get usr_paths
            final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
            usrPathsField.setAccessible(true);

            // Get array of paths
            final String[] paths = (String[]) usrPathsField.get(null);

            // Check if the path to add is already present
            boolean ocvExists = false;
            boolean ntExists = false;
            for (String path : paths) {
                if (path.equals(openCVPath)) {
                    ocvExists = true;
                } else if (path.equals(ntPath)) {
                    ntExists = true;
                }
            }

            // Add missing paths
            int addend = (!ocvExists ? 1 : 0) + (!ntExists ? 1 : 0);
            if (addend > 0) {
                String[] newPaths = Arrays.copyOf(paths, paths.length + addend);
                switch (addend) {
                    case 1:
                        newPaths[newPaths.length - 1] = !ocvExists ? openCVPath : ntPath;
                        break;
                    case 2:
                        newPaths[newPaths.length - 2] = openCVPath;
                        newPaths[newPaths.length - 1] = ntPath;
                        break;
                    default:
                        break;
                }
                usrPathsField.set(null, newPaths);
            }
        } catch (Exception error) {
            System.out.println("Warning: OpenCV could not be automatically loaded. "
                    + "The following error occurred: " + error.getMessage());
        }

        // Main code

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Options options = new Options();

        Option fileOpt = new Option("i", "input", true,
                "takes the path to the image to be filtered");
        fileOpt.setRequired(false);
        options.addOption(fileOpt);

        Option hsvOpt = new Option("hsv", "hue-saturation-value", true,
                "takes comma separated min/max values in the form hmin,hmax,smin,smax,vmin,vmax");
        hsvOpt.setRequired(false);
        options.addOption(hsvOpt);

        Option hslOpt = new Option("hsl", "hue-saturation-luminance", true,
                "takes comma separated min/max values in the form hmin,hmax,smin,smax,lmin,lmax");
        hslOpt.setRequired(false);
        options.addOption(hslOpt);

        Option rgbOpt = new Option("rgb", "red-green-blue", true,
                "takes comma separated min/max values in the form rmin,rmax,gmin,gmax,bmin,bmax");
        rgbOpt.setRequired(false);
        options.addOption(rgbOpt);

        Option minWidthOpt = new Option("wmin", "min-width", true,
                "takes the min width of the target area");
        minWidthOpt.setRequired(false);
        options.addOption(minWidthOpt);

        Option maxWidthOpt = new Option("wmax", "max-width", true,
                "takes the max width of the target area");
        maxWidthOpt.setRequired(false);
        options.addOption(maxWidthOpt);

        Option minHeightOpt = new Option("hmin", "min-height", true,
                "takes the min height of the target area");
        minHeightOpt.setRequired(false);
        options.addOption(minHeightOpt);

        Option maxHeightOpt = new Option("hmax", "max-height", true,
                "takes the max height of the target area");
        maxHeightOpt.setRequired(false);
        options.addOption(maxHeightOpt);

        Option minAreaOpt = new Option("amin", "min-area", true,
                "takes the min area of the target area");
        minAreaOpt.setRequired(false);
        options.addOption(minAreaOpt);

        Option maxAreaOpt = new Option("amax", "max-area", true,
                "takes the max area of the target area");
        maxAreaOpt.setRequired(false);
        options.addOption(maxAreaOpt);

        Option colorOpt = new Option("c", "color", true,
                "takes the desired color of the drawn contours");
        colorOpt.setRequired(false);
        options.addOption(colorOpt);

        Option outputOpt = new Option("o", "output", true,
                "takes the desired output file location");
        outputOpt.setRequired(false);
        options.addOption(outputOpt);

        Option blurOpt = new Option("b", "blur", true,
                "takes the desired blur amount from 0 to 1");
        blurOpt.setRequired(false);
        options.addOption(blurOpt);

        Option saveConfigOpt = new Option("s", "save-config", true,
                "enables the saving of the serialized RooConfig options to a .viz file at the"
                + " specified location");
        saveConfigOpt.setRequired(false);
        options.addOption(saveConfigOpt);

        Option camStreamOpt = new Option("cam", "camera", true,
                "continuously processes a stream from the camera at the specified IP address,"
                        + " overriding the --input and --output parameters");
        camStreamOpt.setRequired(false);
        options.addOption(camStreamOpt);

        CommandLineParser clParser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = clParser.parse(options, args);
        } catch (ParseException error) {
            System.out.println(error.getMessage());
            formatter.printHelp("roosight", options);
            System.exit(1);
            return;
        }

        String file = cmd.getOptionValue("input");
        String cam = cmd.getOptionValue("camera");
        if (file == null && cam == null) {
            System.out.println("Error: RooSight cannot run without an input source.");
            formatter.printHelp("roosight", options);
            System.exit(1);
            return;
        }

        String hsv = cmd.getOptionValue("hue-saturation-value");
        String hsl = cmd.getOptionValue("hue-saturation-luminance");
        String rgb = cmd.getOptionValue("red-green-blue");

        String minWidth = cmd.getOptionValue("min-width");
        String maxWidth = cmd.getOptionValue("max-width");
        String minHeight = cmd.getOptionValue("min-height");
        String maxHeight = cmd.getOptionValue("max-height");
        String minArea = cmd.getOptionValue("min-area");
        String maxArea = cmd.getOptionValue("max-area");

        String blur = cmd.getOptionValue("blur");
        String color = cmd.getOptionValue("color");

        String output = cmd.getOptionValue("output");
        String saveLoc = cmd.getOptionValue("save-config");

        Parser parser = new Parser();
        parser.setInputFile(file);
        parser.setInputCamera(cam);
        parser.setOutputFile(output);
        parser.setSaveLocation(saveLoc);
        parser.setFilters(hsv, hsl, rgb);
        parser.setWidthRange(minWidth, maxWidth);
        parser.setHeightRange(minHeight, maxHeight);
        parser.setAreaRange(minArea, maxArea);
        parser.setColor(color);
        parser.setBlur(blur);

        try {
            parser.parse();
        } catch (Exception error) {
            System.out.println("Error: Parse failure: " + error.getMessage());
            System.exit(1);
            return;
        }
    }
}