
/**
 * Class TimerTaskCrocWidget
 * Task for managing timer in crocwidgets
 * Creation: June, 7, 2009
 * @author Ludovic APVRILLE
 * @see
 */

package crocwidget;

import uicrocbar.*;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import javax.imageio.*;




//import java.io.*;

//import myutil.*;

public  class TimerTaskCrocWidget extends TimerTask {


    protected CrocWidget cw;
    protected boolean periodic;


    // Constructor
    public TimerTaskCrocWidget(CrocWidget _cw) {
        cw = _cw;
        periodic = false;
    }

    public TimerTaskCrocWidget(CrocWidget _cw, boolean _periodic) {
        cw = _cw;
        periodic = _periodic;
    }

    public void run() {
        if (periodic) {
            cw.newPeriod();
        } else {
            cw.timerExpired();
        }
    }


} // End of class
