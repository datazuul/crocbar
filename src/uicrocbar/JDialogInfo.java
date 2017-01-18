

/**
* Class JDialogInfo
* Dialod for displaying an non-editable text
* Creation: May, 06, 2009
* @author Ludovic APVRILLE
* @see
*/

package uicrocbar;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class JDialogInfo extends javax.swing.JDialog implements ActionListener  {

    private String info;

    // Main Panel
    private JButton closeButton;

    public JDialogInfo(Frame f, String title, String _info) {

        super(f, title, true);

        info = _info;

        Container framePanel = getContentPane();
        framePanel.setLayout(new BorderLayout());

        JTextArea jta = new JTextArea(info);
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        Font font = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(font);
        JScrollPane jsp = new JScrollPane(jta);

        framePanel.add(jsp, BorderLayout.CENTER);

        JPanel jp = new JPanel();
        closeButton = new JButton("Close");
        closeButton.addActionListener(this);
        jp.add(closeButton);

        framePanel.add(jp, BorderLayout.SOUTH);

        pack();

    }

    public void	actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (command.equals("Close"))  {
            closeDialog();
        }
    }


    public void closeDialog() {
        dispose();
    }

}
