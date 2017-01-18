/**
* Class JDialogPassword
* Dialog for entering password
* Creation: November, 22, 2009
* @author Ludovic APVRILLE
* @see
*/

package crocwidget;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import javax.swing.event.*;
//import java.util.*;


public class JDialogPassword extends javax.swing.JDialog implements ActionListener  {

    private boolean regularClose;

    private JPanel panel2;
    private Frame frame;
    private String password;


    // Panel1
    protected JPasswordField pass;

    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;

    /** Creates new form  */
    public JDialogPassword(Frame _frame, String _title) {
        super(_frame, _title, true);
        frame = _frame;

        initComponents();
        myInitComponents();
        pack();
    }

    private void myInitComponents() {
    }

    private void initComponents() {
        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
        //GridBagLayout gridbag1 = new GridBagLayout();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        //GridBagConstraints c1 = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();

        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Entering password"));
        //panel2.setPreferredSize(new Dimension(200, 100));

        c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        pass = new JPasswordField(10);
        pass.setEditable(true);
        pass.setFont(new Font("times", Font.PLAIN, 12));
        pass.setEchoChar('*');
        panel2.add(pass, c2);


        // main panel;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.add(panel2, c0);

        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;
        closeButton = new JButton("Save and Close", null);
        //closeButton.setPreferredSize(new Dimension(600, 50));
        closeButton.addActionListener(this);
        c.add(closeButton, c0);
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        cancelButton = new JButton("Cancel", null);
        cancelButton.addActionListener(this);
        c.add(cancelButton, c0);
    }

    public void	actionPerformed(ActionEvent evt)  {

        // Compare the action command to the known actions.
        if (evt.getSource() == closeButton)  {
            closeDialog();
        } else if (evt.getSource() == cancelButton) {
            cancelDialog();
        }
    }

    public void closeDialog() {
        regularClose = true;
        dispose();
    }

    public void cancelDialog() {
        dispose();
    }

    public boolean isRegularClose() {
        return regularClose;
    }

    public String getPassword() {
        return pass.getText();
    }


}
