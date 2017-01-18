/**
* Class JDialogTweet
* Dialog to enter a tweet message
* Creation: February 28, 2011
* @author Axelle Apvrille
* @see
*/

package crocwidget;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class JDialogTweet extends javax.swing.JDialog implements ActionListener  {
    private Frame frame;
    private JPanel panel;
    private JButton cancelButton, tweetButton;
    private JTextField tweetField;
    public String tweetMsg = "";

    public JDialogTweet(Frame _frame, String _title) {
        super(_frame, _title, true);
        frame = _frame;

        initComponents();
        pack();
    }

    private void initComponents() {
        Container c = getContentPane();
        /*GridBagLayout gridbag0 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagConstraints c1 = new GridBagConstraints();

               setFont(new Font("Helvetica", Font.PLAIN, 14));
               c.setLayout(gridbag0);

               setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


               panel = new JPanel();
               panel.setLayout(gridbag1);
               panel.setBorder(new javax.swing.border.TitledBorder("Write your tweet"));
               //panel.setPreferredSize(new Dimension(200, 100));

        c1.gridwidth = 1;
               c1.gridheight = 1;
               c1.weighty = 1.0;
               c1.weightx = 1.0;
               c1.fill = GridBagConstraints.HORIZONTAL;
        tweetField = new JTextField(30);
        tweetField.setEditable(true);
        tweetField.setFont(new Font("times", Font.PLAIN, 12));
        panel.add(tweetField, c1);


               // main panel;
               c0.gridheight = 10;
               c0.weighty = 1.0;
               c0.weightx = 1.0;
               c0.gridwidth = GridBagConstraints.REMAINDER; //end row
               c.add(panel, c0);

               c0.gridwidth = 1;
               c0.gridheight = 1;
               c0.fill = GridBagConstraints.HORIZONTAL;
               tweetButton = new JButton("Tweet", null);
               //closeButton.setPreferredSize(new Dimension(600, 50));
               tweetButton.addActionListener(this);
               c.add(tweetButton, c0);
               c0.gridwidth = GridBagConstraints.REMAINDER; //end row
               cancelButton = new JButton("Cancel", null);
               cancelButton.addActionListener(this);
               c.add(cancelButton, c0);*/


        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());

        // Tweet panel
        JPanel panelTweet = new JPanel();
        panelTweet.setLayout(new BorderLayout());
        panelTweet.setBorder(new javax.swing.border.TitledBorder("Write your tweet"));
        tweetField = new JTextField(30);
        tweetField.setEditable(true);
        tweetField.setFont(new Font("times", Font.PLAIN, 12));
        panelTweet.add(tweetField);
        c.add(panelTweet, BorderLayout.CENTER);

        JPanel panelButton = new JPanel();
        tweetButton = new JButton("Tweet", null);
        tweetButton.addActionListener(this);
        panelButton.add(tweetButton);
        cancelButton = new JButton("Cancel", null);
        cancelButton.addActionListener(this);
        panelButton.add(cancelButton);

        c.add(panelButton, BorderLayout.SOUTH);


    }

    public void actionPerformed(ActionEvent evt) {

        if (evt.getSource() == tweetButton) {
            tweetDialog();
        } else if (evt.getSource() == cancelButton) {
            cancelDialog();
        }
    }

    public void cancelDialog() {
        dispose();
    }

    public void tweetDialog() {
        tweetMsg = tweetField.getText();
        System.out.println("TweetMsg: "+tweetMsg);
        dispose();
    }

}

