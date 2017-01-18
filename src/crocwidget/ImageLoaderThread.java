
/**
* Class ImageLoaderThread
* Thread to load images one after the other
* Creation: June, 9, 2009
* @author Ludovic APVRILLE
* @see
*/

package crocwidget;

import uicrocbar.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;

import org.w3c.dom.*;


//import java.io.*;

//import myutil.*;

public  class ImageLoaderThread  extends Thread   {

    protected ArrayList <ImageWork> urgentImages;
    protected ArrayList <ImageWork> normalImages;
    protected ArrayList <ImageWork> toloadImages;
    protected ArrayList <ImageWork> loadedImages;

    protected MediaTracker media;
    protected int currentTrackID;


    // Constructor
    public ImageLoaderThread() {
        urgentImages = new ArrayList<ImageWork>();
        normalImages = new ArrayList<ImageWork>();
        toloadImages = new ArrayList<ImageWork>();
        loadedImages = new ArrayList<ImageWork>();
    }

    public synchronized void addImageWork(ImageWork iw) {
        //System.out.println("Image added: " + iw.pathToFile);
        if (iw.urgent) {
            urgentImages.add(iw);
        } else {
            normalImages.add(iw);
        }
        notify();
    }

    private boolean availableImages() {
        return (urgentImages.size() > 0) || (normalImages.size() > 0);
    }

    public synchronized void waitForImage() {
        if (toloadImages.size() > 0) {
            try {
                wait(250);
            } catch (InterruptedException ie) {
            }
        } else {
            while (!availableImages()) {
                try {
                    wait();
                } catch (InterruptedException ie) {
                }
            }
        }
    }

    private void sendResponse(ImageWork iw, Image img) {
        //System.out.println("Sending response for " + iw.pathToFile);
        if (iw.cw != null) {
            iw.cw.imageWorkDone(img);
        }
    }

    private void analyzeNewImages(boolean urgent) {
        ImageWork iw;
        ArrayList <ImageWork> list;

        if (urgent) {
            list = urgentImages;
        } else {
            list = normalImages;
        }

        //System.out.println("analyzing list size= " + list.size());

        while (list.size() > 0) {
            iw = list.get(0);
            //System.out.println("analyzing list size= " + list.size() + " iw=" + iw.pathToFile);
            if (iw.cw != null) {
                //System.out.println("Found an image");
                if (!iw.load && (iw.image == null)) {
                    sendResponse(iw, null);
                } else if (iw.load) {
                    if (iw.pathToFile != null) {
                        toloadImages.add(iw);
                        iw.media = new MediaTracker(iw.cw);
                        iw.image = iw.cw.getImage(iw.pathToFile);

                        if (iw.image == null) {
                            System.err.println("Error when loading image " + iw.pathToFile);
                            sendResponse(iw, null);
                        } else {
                            iw.scaleMode = false;
                            iw.media.addImage(iw.image, 0);
                            iw.media.statusID(0, true);
                            //System.out.println("Ready to load the image");
                        }
                    }
                } else {
                    loadedImages.add(iw);
                }
            }
            list.remove(0);
        }
    }

    public void run() {
        ArrayList <ImageWork> list;

        while(true) {
            if (!availableImages()) {
                waitForImage();
            }

            //System.out.println("Working on images");

            // Analyze added images
            if (urgentImages.size() > 0) {
                analyzeNewImages(true);
            } else if (normalImages.size() > 0) {
                analyzeNewImages(false);
            }

            // Are images loaded?
            if (toloadImages.size() > 0) {
                list = new ArrayList <ImageWork>();
                for(ImageWork iw1: toloadImages) {
                    //System.out.println("Image loaded? " + iw1.pathToFile + "status=" + iw1.media.statusID(0, true));

                    if (iw1.media.isErrorAny() || iw1.media.checkAll()) {
                        //System.out.println("Image loaded");
                        list.add(iw1);
                        if((iw1.crop == false) && (iw1.scale == false)) {
                            if (iw1.scaleMode) {
                                sendResponse(iw1, iw1.scaledImage);
                            } else {
                                sendResponse(iw1, iw1.image);
                            }
                        } else {
                            loadedImages.add(iw1);
                        }
                    } else {
                        if (iw1.crop) {
                            //System.out.println("Image not loaded crop to do");
                        } else {
                            //System.out.println("Cropping");
                        }
                    }
                }

                for(ImageWork iw2: list) {
                    toloadImages.remove(iw2);
                }

                list = null;
            }

            // Images to crop and rescale?
            if (loadedImages.size() >0) {
                for(ImageWork iw3: loadedImages) {
                    makeImage(iw3);
                }
                loadedImages.clear();
            }
        }
    }

    private void makeImage(ImageWork iw) {
        if (iw.image == null) {
            return;
        }

        // Must crop?
        if (iw.crop) {
            iw.media = new MediaTracker(iw.cw);
            iw.image = iw.cw.createImage(new FilteredImageSource(iw.image.getSource(), new CropImageFilter(iw.xC, iw.yC, iw.widthC, iw.heightC)));
            iw.media.addImage(iw.image, 0);
            iw.media.statusID(0, true);
            toloadImages.add(iw);
            iw.crop = false;
            iw.scaleMode = false;
            return;
        }

        if (iw.scale) {
            //iw.scaledImage = iw.cw.toBufferedImage(iw.image);
            //iw.scaledImage = iw.cw.getScaledInstance(iw.scaledImage, iw.widthS, iw.heightS, RenderingHints.VALUE_INTERPOLATION_BICUBIC, false);
            //iw.scaledImage = iw.image.getScaledInstance(iw.widthS, iw.heightS, Image.SCALE_SMOOTH);
            //sendResponse(iw, iw.scaledImage);

            iw.scaledImage = iw.image.getScaledInstance(iw.widthS, iw.heightS, Image.SCALE_SMOOTH);
            iw.media = new MediaTracker(iw.cw);
            iw.media.addImage(iw.scaledImage, 0);
            iw.media.statusID(0, true);
            toloadImages.add(iw);
            iw.scale = false;
            iw.scaleMode = true;
            return;
        }

        sendResponse(iw, iw.image);
    }







} // End of class ImageLoaderThread

