/**
* Class TimeAndDateCrocWidgetJDialog
* Dialog for managing alarms
* Creation: May, 18, 2009
* @author Ludovic APVRILLE
* @see
*/

package crocwidget;

import uicrocbar.*;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;



public class TimeAndDateCrocWidgetJDialog extends javax.swing.JDialog implements ActionListener, ListSelectionListener  {
    //private Vector attributes, attributesPar, forbidden, initValues;
    //private boolean checkKeyword, checkJavaKeyword;

    private final static String pathIconSave = "images/Save24.gif";
    private final static String pathIconStop = "images/Stop24.gif";


    protected ArrayList<String> times, labels, oldtimes, oldlabels;
    protected Vector alarms;

    private JPanel panel1, panel2;

    private Frame frame;

    private String attrib; // "Attributes", "Gates", etc.

    // Panel1
    private JTextField timeText, labelText;
    private JButton addButton;

    //Panel2
    private JList listAlarms;
    private JButton upButton;
    private JButton downButton;
    private JButton removeButton;

    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;

    private CrocWidget cw;

    /** Creates new form  */
    public TimeAndDateCrocWidgetJDialog(CrocWidget _cw, ArrayList<String> _times, ArrayList<String> _labels, Frame f, String title, String attrib, Color _bg, Color _fg) {
        super(f, title, true);
        cw = _cw;
        frame = f;
        times = _times;
        labels = _labels;
        alarms = new Vector();
        this.attrib = attrib;

        alarms = new Vector();

        String tmp;
        for(int i=0; i<times.size(); i++) {
            tmp = (String)(times.get(i)) + " (" + (String)(labels.get(i)) + ")";
            alarms.addElement(tmp);
        }

        oldtimes = new ArrayList<String>();
        oldlabels = new ArrayList<String>();

        for(String t: times) {
            oldtimes.add(t);
        }

        for(String l: labels) {
            oldlabels.add(l);
        }

        setForeground(_fg);
        setBackground(_bg);

        initComponents();
        myInitComponents();
        pack();
    }

    private void myInitComponents() {
        removeButton.setEnabled(false);
        upButton.setEnabled(false);
        downButton.setEnabled(false);
    }

    private void initComponents() {
        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        GridBagConstraints c1 = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();

        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panel1 = new JPanel();
        panel1.setLayout(gridbag1);
        panel1.setBorder(new javax.swing.border.TitledBorder("Adding " + attrib + "s"));
        panel1.setPreferredSize(new Dimension(200, 200));

        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Managing " + attrib + "s"));
        panel2.setPreferredSize(new Dimension(200, 200));

        // first line panel1
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 3;
        panel1.add(new JLabel(" "), c1);

        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel(""), c1);
        panel1.add(new JLabel("Alarm time"), c1);
        panel1.add(new JLabel(" "), c1);
        panel1.add(new JLabel("label to display"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        panel1.add(new JLabel(" "), c1);


        // second line panel1
        c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel(" "), c1);
        timeText = new JTextField();
        timeText.setColumns(20);
        timeText.setEditable(true);
        panel1.add(timeText, c1);
        panel1.add(new JLabel(" "), c1);

        labelText = new JTextField();
        labelText.setColumns(20);
        labelText.setEditable(true);
        panel1.add(labelText, c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        panel1.add(new JLabel(" "), c1);


        // third line panel1
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 3;
        panel1.add(new JLabel(" "), c1);

        // fourth line panel2
        c1.gridheight = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        addButton = new JButton("Add " + attrib);
        addButton.addActionListener(this);
        panel1.add(addButton, c1);

        // 1st line panel2
        listAlarms = new JList(alarms);
        //listAlarms.setFixedCellWidth(150);
        //listAlarms.setFixedCellHeight(20);
        listAlarms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listAlarms.addListSelectionListener(this);
        JScrollPane scrollPane = new JScrollPane(listAlarms);
        scrollPane.setSize(150, 150);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 5;
        c2.weighty = 10.0;
        c2.weightx = 10.0;
        panel2.add(scrollPane, c2);

        // 2nd line panel2
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 1;
        panel2.add(new JLabel(""), c2);

        // third line panel2
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.HORIZONTAL;
        upButton = new JButton("Up");
        upButton.addActionListener(this);
        panel2.add(upButton, c2);

        downButton = new JButton("Down");
        downButton.addActionListener(this);
        panel2.add(downButton, c2);

        removeButton = new JButton("Remove " + attrib);
        removeButton.addActionListener(this);
        panel2.add(removeButton, c2);

        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;

        c.add(panel1, c0);
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.add(panel2, c0);

        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;
        closeButton = new JButton("Save and Close", cw.getIcon(pathIconSave));
        //closeButton.setPreferredSize(new Dimension(600, 50));
        closeButton.addActionListener(this);
        c.add(closeButton, c0);
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        cancelButton = new JButton("Cancel", cw.getIcon(pathIconStop));
        cancelButton.addActionListener(this);
        c.add(cancelButton, c0);
    }

    public void	actionPerformed(ActionEvent evt)  {


        String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (command.equals("Save and Close"))  {
            closeDialog();
        } else if (command.equals("Add " + attrib)) {
            addAlarm();
        } else if (command.equals("Cancel")) {
            cancelDialog();
        } else if (command.equals("Remove " + attrib)) {
            removeAlarm();
        } else if (command.equals("Down")) {
            downAlarm();
        } else if (command.equals("Up")) {
            upAttribute();
        }
    }




    public void addAlarm() {
        String t = timeText.getText().trim();
        String l = labelText.getText().trim();

        if (t.length()>0) {
            times.add(t);
            labels.add(l);
            alarms.add(t + " (" + l + ")");
            listAlarms.setListData(alarms);
        }
    }

    public void removeAlarm() {
        int i = listAlarms.getSelectedIndex() ;
        if (i!= -1) {
            //String s = (String)(alarms.elementAt(i));
            alarms.removeElementAt(i);
            times.remove(i);
            labels.remove(i);
            listAlarms.setListData(alarms);
        }
    }

    public void downAlarm() {
        int i = listAlarms.getSelectedIndex();
        if ((i!= -1) && (i != alarms.size() - 1)) {
            Object o = alarms.elementAt(i);
            alarms.removeElementAt(i);
            alarms.insertElementAt(o, i+1);
            listAlarms.setListData(alarms);
            listAlarms.setSelectedIndex(i+1);
            String s = times.get(i);
            times.remove(i);
            times.add(i+1, s);
            s = labels.get(i);
            labels.remove(i);
            labels.add(i+1, s);
        }
    }

    public void upAttribute() {
        int i = listAlarms.getSelectedIndex();
        if (i > 0) {
            Object o = alarms.elementAt(i);
            alarms.removeElementAt(i);
            alarms.insertElementAt(o, i-1);
            listAlarms.setListData(alarms);
            listAlarms.setSelectedIndex(i-1);
            String s = times.get(i);
            times.remove(i);
            times.add(i-1, s);
            s = labels.get(i);
            labels.remove(i);
            labels.add(i-1, s);
        }
    }


    public void closeDialog() {
        dispose();
    }

    public void cancelDialog() {
        times.clear();
        labels.clear();

        for(String t: oldtimes) {
            times.add(t);
        }

        for(String l: oldlabels) {
            labels.add(l);
        }
        dispose();
    }

    public void valueChanged(ListSelectionEvent e) {
        int i = listAlarms.getSelectedIndex() ;
        if (i == -1) {
            removeButton.setEnabled(false);
            upButton.setEnabled(false);
            downButton.setEnabled(false);
            timeText.setText("");
            labelText.setText("");
        } else {
            timeText.setText(times.get(i));
            labelText.setText(labels.get(i));

            removeButton.setEnabled(true);
            if (i > 0) {
                upButton.setEnabled(true);
            } else {
                upButton.setEnabled(false);
            }
            if (i != alarms.size() - 1) {
                downButton.setEnabled(true);
            } else {
                downButton.setEnabled(false);
            }
        }
    }



}
