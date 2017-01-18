/**
* Class ToDoCrocWidgetJDialog
* Dialog for managing todos
* Creation: June, 3, 2009
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



public class ToDoCrocWidgetJDialog extends javax.swing.JDialog implements ActionListener, ListSelectionListener  {
    //private Vector attributes, attributesPar, forbidden, initValues;
    //private boolean checkKeyword, checkJavaKeyword;

    private final static String pathIconSave = "images/Save24.gif";
    private final static String pathIconStop = "images/Stop24.gif";


    protected ArrayList<String> todos, oldtodos;
    protected Vector list;

    private JPanel panel1, panel2;

    private Frame frame;

    private String attrib; // "Attributes", "Gates", etc.

    // Panel1
    private JTextField todoText;
    private JButton addButton, addLastButton, modifyButton;

    //Panel2
    private JList listToDos;
    private JButton upButton;
    private JButton downButton;
    private JButton removeButton;

    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;

    private CrocWidget cw;

    /** Creates new form  */
    public ToDoCrocWidgetJDialog(CrocWidget _cw, ArrayList<String> _todos, Frame f, String title, String attrib, Color _bg, Color _fg) {
        super(f, title, true);
        cw = _cw;
        frame = f;
        todos = _todos;
        this.attrib = attrib;

        list = new Vector();
        String tmp;
        for(int i=0; i<todos.size(); i++) {
            tmp = todos.get(i);
            list.addElement(tmp);
        }

        oldtodos = new ArrayList<String>();

        for(String t: todos) {
            oldtodos.add(t);
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
        modifyButton.setEnabled(false);
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
        panel1.setPreferredSize(new Dimension(300, 300));

        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Managing " + attrib + "s"));
        panel2.setPreferredSize(new Dimension(300, 300));

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
        panel1.add(new JLabel("To do description:"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        panel1.add(new JLabel(" "), c1);


        // second line panel1
        c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel(" "), c1);
        todoText = new JTextField();
        todoText.setColumns(100);
        todoText.setEditable(true);
        panel1.add(todoText, c1);
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
        addButton = new JButton("Add first " + attrib);
        addButton.addActionListener(this);
        panel1.add(addButton, c1);

        // fiveth line panel2
        c1.gridheight = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        addLastButton = new JButton("Add last " + attrib);
        addLastButton.addActionListener(this);
        panel1.add(addLastButton, c1);

        // sixth line panel2
        c1.gridheight = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        modifyButton = new JButton("Modify " + attrib);
        modifyButton.addActionListener(this);
        panel1.add(modifyButton, c1);

        // 1st line panel2
        listToDos = new JList(list);
        //listAlarms.setFixedCellWidth(150);
        //listAlarms.setFixedCellHeight(20);
        listToDos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listToDos.addListSelectionListener(this);
        JScrollPane scrollPane = new JScrollPane(listToDos);
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
        } else if (command.equals("Add first " + attrib)) {
            addFirstTodo();
        } else if (command.equals("Add last " + attrib)) {
            addLastTodo();
        } else if (command.equals("Modify " + attrib)) {
            modifyTodo();
        } else if (command.equals("Cancel")) {
            cancelDialog();
        } else if (command.equals("Remove " + attrib)) {
            removeTodo();
        } else if (command.equals("Down")) {
            downTodo();
        } else if (command.equals("Up")) {
            upTodo();
        }
    }


    public void addLastTodo() {
        String t = todoText.getText().trim();

        if (t.length()>0) {
            todos.add(t);
            list.add(t);
            listToDos.setListData(list);
        }
    }

    public void addFirstTodo() {
        String t = todoText.getText().trim();

        if (t.length()>0) {
            todos.add(0, t);
            list.add(0, t);
            listToDos.setListData(list);
        }
    }

    public void modifyTodo() {
        int i = listToDos.getSelectedIndex() ;
        if (i == -1) {
            addFirstTodo();
        } else {
            String t = todoText.getText().trim();
            if (t.length()>0) {
                removeTodo();
                todos.add(i, t);
                list.add(i, t);
                listToDos.setListData(list);
            }
        }
    }

    public void removeTodo() {
        int i = listToDos.getSelectedIndex() ;
        if (i!= -1) {
            String s = (String)(list.elementAt(i));
            list.removeElementAt(i);
            listToDos.setListData(list);
            todos.remove(i);
        }
    }

    public void downTodo() {
        int i = listToDos.getSelectedIndex();
        if ((i!= -1) && (i != todos.size() - 1)) {
            Object o = list.elementAt(i);
            String s = todos.get(i);
            todos.remove(i);
            todos.add(i+1, s);
            list.removeElementAt(i);
            list.insertElementAt(o, i+1);
            listToDos.setListData(list);
            listToDos.setSelectedIndex(i+1);
        }
    }

    public void upTodo() {
        int i = listToDos.getSelectedIndex();
        if (i > 0) {
            Object o = list.elementAt(i);
            String s = todos.get(i);
            list.removeElementAt(i);
            list.insertElementAt(o, i-1);
            todos.remove(i);
            todos.add(i-1, s);
            listToDos.setListData(list);
            listToDos.setSelectedIndex(i-1);
        }
    }


    public void closeDialog() {
        dispose();
    }

    public void cancelDialog() {
        todos.clear();

        for(String t: oldtodos) {
            todos.add(t);
        }

        dispose();
    }

    public void valueChanged(ListSelectionEvent e) {
        int i = listToDos.getSelectedIndex() ;
        if (i == -1) {
            removeButton.setEnabled(false);
            upButton.setEnabled(false);
            downButton.setEnabled(false);
            modifyButton.setEnabled(false);
            todoText.setText("");
        } else {
            todoText.setText(todos.get(i));
            modifyButton.setEnabled(true);
            removeButton.setEnabled(true);
            if (i > 0) {
                upButton.setEnabled(true);
            } else {
                upButton.setEnabled(false);
            }
            if (i != todos.size() - 1) {
                downButton.setEnabled(true);
            } else {
                downButton.setEnabled(false);
            }
        }
    }





}
