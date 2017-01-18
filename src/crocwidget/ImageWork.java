
/**
* Class Imagework
* Data for specifying image to load
* Creation: June, 9, 2009
* @author Ludovic APVRILLE
* @see
*/

package crocwidget;

import java.awt.*;
import java.awt.image.*;



//import java.io.*;

//import myutil.*;

public  class ImageWork {

    public String pathToFile;
    public Image image; // Must be specified only if no load work to do
    public boolean urgent;
    public boolean load;
    public boolean scale;
    public boolean crop;
    public int xC, yC, widthC, heightC; // For cropping
    public int widthS, heightS; // For scaling
    public CrocWidget cw;

    // To be used by loader only
    public Image scaledImage;
    public MediaTracker media;
    public boolean scaleMode;

    // Constructor
    public ImageWork(CrocWidget _cw) {
        cw = _cw;
    }

} // End of class ImageWork

