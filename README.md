# RooSight Command-Line Tool

The RooSight CLT offers a command-line interface for interacting with the RooSight library.



### Usage

```
usage: roosight
 -amax,--max-area <arg>                  takes the max area of the target
                                         area
 -amin,--min-area <arg>                  takes the min area of the target
                                         area
 -c,--color <arg>                        takes the desired color of the
                                         drawn contours
 -hmax,--max-height <arg>                takes the max height of the
                                         target area
 -hmin,--min-height <arg>                takes the min height of the
                                         target area
 -hsl,--hue-saturation-luminance <arg>   takes comma separated min/max
                                         values in the form
                                         hmin,hmax,smin,smax,lmin,lmax
 -hsv,--hue-saturation-value <arg>       takes comma separated min/max
                                         values in the form
                                         hmin,hmax,smin,smax,vmin,vmax
 -i,--input <arg>                        takes the path to the image to be
                                         filtered
 -o,--output <arg>                       takes the desired output file
                                         location
 -rgb,--red-green-blue <arg>             takes comma separated min/max
                                         values in the form
                                         rmin,rmax,gmin,gmax,bmin,bmax
 -wmax,--max-width <arg>                 takes the max width of the target
                                         area
 -wmin,--min-width <arg>                 takes the min width of the target
                                         area
```

RooSightCLT is the basis of [RooSightGUI](https://github.com/Roobotics-FRC/RooSightGUI), which offers a graphical interface for interacting with the command-line tool.