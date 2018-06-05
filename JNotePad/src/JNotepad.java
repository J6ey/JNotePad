/*
Name:       Tan, Joey
Project:    3
Due:        March 12, 2018
Course:     CS-245-01-w18

Description:
                This program implements the JNotepad that behaves like the
                Windows Notepad.
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import javax.swing.undo.*;

public class JNotepad implements ActionListener{
    private JFileChooser fileChooser;
    private UndoManager undo;
    private Document doc;
    private JDialog find, replace, goTo;
    private File file;
    private String fileName, initialText, findTarget, rightHalf, leftHalf;
    private JFrame frame;
    private  JLabel status;
    private JPanel statusBarPanel;
    private JTextArea textArea;
    private JCheckBoxMenuItem wordWrap;
    private JMenuItem statusBar;
    private ActionListener findAction;
    private JFontChooser fontChooser;
    private int line, column;
    private boolean fileChosen, direction, findCase, replaceCase, statusBarOn;

    public JNotepad(){
        frame = new JFrame("Untitled - JNotepad");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(500,400);
        frame.setLocationRelativeTo(null);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if(!initialText.equals(textArea.getText())){ // ask to save
                    int res = saveDialog();
                    if(res == 0){
                        saveFile(false);
                    } else if(res == 1) {
                        System.exit(0);
                    }
                } else {
                    System.exit(0);
                }
            }
        });
        initialText = "";
        fontChooser = new JFontChooser();
        textArea = new JTextArea();
        status = new JLabel("Ln " + line + ", Col " + column);
        undo = new UndoManager(); // For undo
        doc = textArea.getDocument(); // For undo
        JMenuBar menuBar = new JMenuBar();
        JMenu menuBarMenus[] = {new JMenu("File"), new JMenu("Edit"), new JMenu("Format"),
                                new JMenu("View"), new JMenu("Help")};
        JMenuItem[] fileItems = {new JMenuItem("New", 'n'), new JMenuItem("Open..."), new JMenuItem("Save"),
                                new JMenuItem("Save As..."), new JMenuItem("Page Setup...", 'u'),
                                new JMenuItem("Print..."), new JMenuItem("Exit", 'x')};
        for (int i = 0; i < fileItems.length; i++) {
            menuBarMenus[0].add(fileItems[i]);
            fileItems[i].addActionListener(this);
            if(i != 3 && i != 4 && i !=6){
                fileItems[i].setAccelerator(KeyStroke.getKeyStroke(fileItems[i].getText().charAt(0), InputEvent.CTRL_MASK));
            }
            if(i == 3 || i == 5){
                menuBarMenus[0].addSeparator();
            }
            if(i == 4 || i == 5 ){
                fileItems[i].setEnabled(false);
            }
        }
        JMenuItem[] editItems = {new JMenuItem("Undo"), new JMenuItem(new DefaultEditorKit.CutAction()), new JMenuItem(new DefaultEditorKit.CopyAction()),
                                new JMenuItem(new DefaultEditorKit.PasteAction()), new JMenuItem("Delete"), new JMenuItem("Find..."),
                                new JMenuItem("Find Next"), new JMenuItem("Replace..."), new JMenuItem("Go To..."),
                                new JMenuItem("Select All"), new JMenuItem("Time/Date")};
        textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_MASK), "none");
        editItems[0].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK));
        editItems[1].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        editItems[2].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        editItems[3].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        editItems[4].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        editItems[7].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));
        editItems[9].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        editItems[10].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        editItems[1].setText("Cut");
        editItems[2].setText("Copy");
        editItems[3].setText("Paste");
        for (int i = 0; i < editItems.length; i++) {
            menuBarMenus[1].add(editItems[i]);
            editItems[i].addActionListener(this);
            if(i == 5 || i == 8)
                editItems[i].setAccelerator(KeyStroke.getKeyStroke(editItems[i].getText().charAt(0), InputEvent.CTRL_MASK));
            if(i == 0 || i== 4 || i ==8)
                menuBarMenus[1].addSeparator();
            if(i == 0 || i == 1 || i == 2 || i == 4){
                editItems[i].setEnabled(false);
            }
        }
        wordWrap = new JCheckBoxMenuItem("Word Wrap");
        wordWrap.setMnemonic('w');
        JMenuItem font = new JMenuItem("Font...", 'f');
        wordWrap.addActionListener(this);
        font.addActionListener(this);
        menuBarMenus[2].add(wordWrap);
        menuBarMenus[2].add(font);
        statusBar = new JMenuItem("Status Bar", 's');
        statusBar.addActionListener(this);
        menuBarMenus[3].add(statusBar);
        JMenuItem help = new JMenuItem("View Help", 'h');
        help.addActionListener(this);
        JMenuItem about = new JMenuItem("About JNotepad");
        about.addActionListener(this);
        menuBarMenus[4].add(help);
        menuBarMenus[4].addSeparator();
        menuBarMenus[4].add(about);
        for (int i = 0; i < menuBarMenus.length; i++) {
            if(i != 2) {
                menuBarMenus[i].setMnemonic(menuBarMenus[i].getText().charAt(0));
            } else {
                menuBarMenus[i].setMnemonic('o');
            }
            menuBar.add(menuBarMenus[i]);
        }
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(new JMenuItem(new DefaultEditorKit.CutAction())).setText("Cut");
        popupMenu.add(new JMenuItem(new DefaultEditorKit.CopyAction())).setText("Copy");
        popupMenu.add(new JMenuItem(new DefaultEditorKit.PasteAction())).setText("Paste");
        textArea.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if(e.isPopupTrigger()){
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            public void mouseReleased(MouseEvent e){
                if(e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        textArea.addKeyListener(new KeyAdapter() { // For undo
            public void keyTyped(KeyEvent e) {
                if(undo.canUndo()){
                    editItems[0].setEnabled(true);
                } else {
                    editItems[0].setEnabled(false);
                }
            }
        });
        textArea.getDocument().addDocumentListener(new DocumentListener() { // checks if textArea has text
            public void insertUpdate(DocumentEvent e) {
                undoSwitch(editItems[0]);
                areaUpdate(textArea, editItems[5], editItems[6]);
            }
            public void removeUpdate(DocumentEvent e) {
                undoSwitch(editItems[0]);
                areaUpdate(textArea, editItems[5], editItems[6]);
            }
            public void changedUpdate(DocumentEvent e) {
                undoSwitch(editItems[0]);
                areaUpdate(textArea, editItems[5], editItems[6]);
            }
        });
        textArea.addCaretListener(e -> {
            if(textArea.getSelectionEnd() - textArea.getSelectionStart() > 0){
                editItems[1].setEnabled(true);
                editItems[2].setEnabled(true);
                editItems[4].setEnabled(true);
            } else {
                editItems[1].setEnabled(false);
                editItems[2].setEnabled(false);
                editItems[4].setEnabled(false);
            }
            line = 1;
            column = 1;

            try {
                int caretPos =  textArea.getCaretPosition();
                Element map =  textArea.getDocument().getDefaultRootElement();
                line = map.getElementIndex(caretPos);
                Element lineElem = map.getElement(line);
                column = caretPos - lineElem.getStartOffset();
                status.setText("Ln " + line + ", Col " + column);

            } catch (Exception ex){ }
        });

        doc.addUndoableEditListener( ue -> { // For undo
            undo.addEdit(ue.getEdit());
        });
        find(); // creates a find dialog
        replace(); // creates a replace dialog
        goTo(); // creates a Go To dialog

        editItems[6].addActionListener(findAction);
        frame.add(new JScrollPane(textArea));
        frame.add(setStatusBar(), BorderLayout.SOUTH);
        frame.setJMenuBar(menuBar);
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent ae){
        String itemMenu = ae.getActionCommand();
        switch(itemMenu){
            case "New":
                if(textArea.getText().isEmpty() && !fileChosen){
                    setNew();
                } else if(!textArea.getText().isEmpty() && !fileChosen){ // ask to save
                    int res = saveDialog();
                    if(res == 0){
                        saveFile(false);
                    } else if(res == 1) {
                        setNew();
                    }
                } else {
                    if(!initialText.equals(textArea.getText())){ // ask to save
                        int res = saveDialog();
                        if(res == 0){
                            saveFile(false);
                        } else if(res == 1) {
                            setNew();
                        }
                    } else {
                        setNew();
                    }
                }
                break;
            case "Open...":
                if(!initialText.equals(textArea.getText())){
                    int res = saveDialog();
                    if(res == 0){
                        saveFile(false);
                        selectFile();
                    } else if(res == 1) {
                        selectFile();
                    }
                } else {
                    selectFile();
                }
                    break;
            case "Save":
                saveFile(false);
                break;
            case "Save As...":
                saveFile(true);
                break;
            case "Exit":
                if(!initialText.equals(textArea.getText())){
                    int res = saveDialog();
                    if(res == 0){
                        saveFile(false);
                    } else if(res == 1) {
                        System.exit(0);
                    }
                } else {
                    System.exit(0);
                }
                break;
            case "Delete":
                textArea.replaceSelection("");
                break;
            case "Undo":

                try{
                    undo.undo();
                } catch (CannotUndoException e){ }
                break;
            case "Find...":
                find.setVisible(true);
                break;
            case "Up":
                direction = true;
                break;
            case "Down":
                direction = false;
                break;
            case "Replace...":
                replace.setVisible(true);
                break;
            case "Go To...":
                goTo.setVisible(true);
                break;
            case "Select All":
                textArea.selectAll();
                break;
            case "Time/Date":
                String timeStamp = new SimpleDateFormat("H:mm a M/d/yyyy")
                        .format(Calendar.getInstance().getTime());
                textArea.replaceSelection(timeStamp);
                break;
            case "Font...":
                if(fontChooser.showDialog(frame)){
                    textArea.setForeground(fontChooser.getColor());
                    textArea.setFont(fontChooser.getFont());
                }
                break;
            case "Word Wrap":
                if(wordWrap.isSelected()){
                    textArea.setLineWrap(true);
                    textArea.setWrapStyleWord(true);
                    goTo.setEnabled(false);
                    statusBar.setEnabled(false);
                    if(statusBarPanel.isVisible()){
                        statusBarPanel.setVisible(false);
                    }
                } else {
                    textArea.setLineWrap(false);
                    textArea.setWrapStyleWord(false);
                    goTo.setEnabled(true);
                    statusBar.setEnabled(true);
                    if(statusBarOn){
                        statusBarPanel.setVisible(true);
                    }
                }
                break;
            case "Status Bar":
                if(statusBarPanel.isVisible()){
                    statusBarPanel.setVisible(false);
                    statusBarOn = false;
                } else {
                    statusBarPanel.setVisible(true);
                    statusBarOn = true;
                }
                break;
            case "About JNotepad":
                JOptionPane.showMessageDialog(frame,
                        new JLabel("(c) Joey Tan", JLabel.CENTER), "About JNotepad",
                        JOptionPane.PLAIN_MESSAGE);
                break;
            case "View Help":
                if(Desktop.isDesktopSupported()){
                    try {
                        Desktop.getDesktop().browse(
                                new URI("https://answers.microsoft.com/en-us/windows/forum/apps_windows_10"));
                    } catch (Exception e){}
                }
                break;
        }

    }
    public void selectFile(){
        FileFilter filter = new FileNameExtensionFilter(".txt & .java", "txt", "java");
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(filter);
        int res = fileChooser.showOpenDialog(frame);
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                textArea.read(new FileReader(fileChooser.getSelectedFile()), fileChooser.getSelectedFile());
                frame.setTitle(fileChooser.getSelectedFile().getName() + " - JNotepad");
                initialText = textArea.getText();
                fileChosen = true;
            } catch (Exception e) {
                System.out.println("File Not Found.");
            }
        }
    }

    public void undoSwitch(JMenuItem undoItem){ // determines if undoable
        if(undo.canUndo()){
            undoItem.setEnabled(true);
        } else {
            undoItem.setEnabled(false);
        }
    }

    public void areaUpdate(JTextArea field, JMenuItem find, JMenuItem findNext){ // checks if text area contains text
        if(field.getText().isEmpty()){ // sets Find and Find Next menu item to false if empty, true otherwise.
            find.setEnabled(false);
            findNext.setEnabled(false);
        } else {
            find.setEnabled(true);
            findNext.setEnabled(true);
        }
    }

    public void textUpdate(JTextField field, JButton[] button, int function){ // checks if Find textfield contains text
        if(function == 0) { // sets button status for Find
            if (field.getText().isEmpty()) { // sets button to false if empty, true otherwise.
                button[0].setEnabled(false);
            } else {
                button[0].setEnabled(true);
            }
        } else { // sets button statuses for Replace
            if(field.getText().isEmpty()) {
                button[0].setEnabled(false);
                button[1].setEnabled(false);
                button[2].setEnabled(false);
            } else {
                button[0].setEnabled(true);
                button[1].setEnabled(true);
                button[2].setEnabled(true);
            }
        }
    }
    public void find() { // creates the find dialog window
        int function = 0; // tells textUpdate() which function to set
        find = new JDialog(frame, "Find");
        find.setLocationRelativeTo(frame);
        find.setVisible(false);
        find.setLayout(new FlowLayout());
        find.setSize(370, 140);
        find.setResizable(false);
        JLabel findWhat = new JLabel("Find What:   ");
        findWhat.setDisplayedMnemonic('n');
        find.add(findWhat);
        JTextField findField = new JTextField(15);
        findWhat.setLabelFor(findField);
        findField.setActionCommand("findField");
        JButton findNext = new JButton("Find Next");
        JButton[] findNArray = {findNext};
        findNext.setEnabled(false);
        findNext.setMnemonic('f');
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(ae -> {
            find.setVisible(false);
        });
        findField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                textUpdate(findField, findNArray, function);
            }
            public void removeUpdate(DocumentEvent e) {
                textUpdate(findField, findNArray, function);
            }
            public void changedUpdate(DocumentEvent e) {
                textUpdate(findField, findNArray, function);
            }
        });
        JRadioButton up = new JRadioButton("Up");
        JRadioButton down = new JRadioButton("Down", true);
        up.addActionListener(this);
        down.addActionListener(this);
        ButtonGroup group = new ButtonGroup();
        group.add(up);
        group.add(down);
        JPanel panel = new JPanel();
        panel.add(up);
        panel.add(down);
        TitledBorder borderTitle = BorderFactory.createTitledBorder("Direction");
        panel.setBorder(borderTitle);
        up.setMnemonic('u');
        down.setMnemonic('d');
        find.getRootPane().setDefaultButton(findNext);

        JCheckBox matchBox = new JCheckBox("Match case");
        matchBox.setMnemonic('c');
        matchBox.setDisplayedMnemonicIndex(6);
        matchBox.addItemListener(ie -> {
            if(!matchBox.isSelected()){
                findCase = false;
            } else {
                findCase = true;
            }
        });

        findAction = (ae ->{
            findHelper(findField, findNext, findCase, function);
        });
        findNext.addActionListener(findAction);
        find.add(findField);
        find.add(findNext);
        find.add(matchBox);
        find.add(panel);
        find.add(cancel);
    }

    public void findHelper(JTextField findField, JButton findNext, boolean matchCase, int function){
        leftHalf = textArea.getText().substring(0, textArea.getCaretPosition());
        rightHalf = textArea.getText().substring(textArea.getCaretPosition());
        findTarget = findField.getText();
        if(!matchCase){
            leftHalf = leftHalf.toLowerCase();
            rightHalf = rightHalf.toLowerCase();
            findTarget = findTarget.toLowerCase();
        }
        if(direction && function == 0){ // position is up/left of caret & function is Find
            if(leftHalf.contains(findTarget)){
                textArea.requestFocus();
                findNext.requestFocus();
                if((textArea.getCaretPosition() == leftHalf.lastIndexOf(findTarget) + findTarget.length())
                        && textArea.getSelectedText() != null){
                    if(textArea.getSelectedText().equals(findTarget)) {
                        leftHalf = leftHalf.substring(0, leftHalf.lastIndexOf(findTarget));
                    }
                }
                if(leftHalf.contains(findTarget)) {
                    textArea.select(leftHalf.lastIndexOf(findTarget)
                            , leftHalf.lastIndexOf(findTarget) + findTarget.length());
                } else {
                    cannotFind(findTarget);
                }
            } else {
                cannotFind(findTarget);
            }
        } else { // position is down/right of caret
            if (rightHalf.contains(findTarget)){
                textArea.requestFocus();
                findNext.requestFocus();
                textArea.select(rightHalf.indexOf(findTarget) + leftHalf.length()
                        , rightHalf.indexOf(findTarget) + findTarget.length() + leftHalf.length());
            } else {
                cannotFind(findTarget);
            }
        }
    }

    public void replace(){ // creates the replace dialog window
        int function = 1; // tells textUpate() which function to set
        replace = new JDialog(frame, "Replace");
        replace.setLocationRelativeTo(frame);
        replace.setVisible(false);
        replace.setLayout(new FlowLayout());
        replace.setSize(370,170);
        replace.setResizable(false);
        JCheckBox matchBox = new JCheckBox("Match case");
        matchBox.setMnemonic('c');
        matchBox.setDisplayedMnemonicIndex(6);
        matchBox.addItemListener(ie -> {
            if(!matchBox.isSelected()){
                replaceCase = false;
            } else {
                replaceCase = true;
            }
        });
        JLabel findWhat = new JLabel("Find What:   ");
        JLabel replaceWith = new JLabel("Replace with:");
        findWhat.setDisplayedMnemonic('n');
        replaceWith.setDisplayedMnemonic('p');
        JTextField rFindField = new JTextField(40);
        JTextField replaceField = new JTextField(40);
        rFindField.setMaximumSize(rFindField.getPreferredSize());
        replaceField.setMaximumSize(replaceField.getPreferredSize());
        findWhat.setLabelFor(rFindField);
        replaceWith.setLabelFor(replaceField);
        JButton rFindNext =  new JButton("Find Next");
        JButton replaceButton = new JButton("Replace");
        JButton replaceAll = new JButton("Replace All");
        JButton cancel = new JButton("cancel");
        rFindNext.setEnabled(false);
        replaceButton.setEnabled(false);
        replaceAll.setEnabled(false);
        replace.getRootPane().setDefaultButton(rFindNext);
        JButton[] replaceArray = {rFindNext, replaceButton, replaceAll};
        rFindField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                textUpdate(rFindField, replaceArray, function);
            }
            public void removeUpdate(DocumentEvent e) {
                textUpdate(rFindField, replaceArray, function);
            }
            public void changedUpdate(DocumentEvent e) {
                textUpdate(rFindField, replaceArray, function);
            }
        });
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        labelPanel.add(findWhat);
        labelPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        labelPanel.add(replaceWith);
        labelPanel.setPreferredSize(new Dimension(80,100));
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.add(rFindField);
        fieldPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        fieldPanel.add(replaceField);
        fieldPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        fieldPanel.add(matchBox);
        fieldPanel.setPreferredSize(new Dimension(140,110));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(rFindNext);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        buttonPanel.add(replaceButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        buttonPanel.add(replaceAll);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        buttonPanel.add(cancel);
        rFindNext.addActionListener(ae-> {
            findHelper(rFindField, rFindNext, replaceCase, function);
        });
        replaceButton.addActionListener(ae-> {
            if(textArea.getSelectedText() != null){
                if(textArea.getSelectedText().equals(rFindField.getText())){
                    textArea.replaceSelection(replaceField.getText());
                } else {
                    cannotFind(rFindField.getText());
                }
            } else {
                findHelper(rFindField, rFindNext, replaceCase, function);
            }
        });
        replaceAll.addActionListener(ae-> {
            textArea.setText(textArea.getText().replaceAll(rFindField.getText(), replaceField.getText()));
        });
        cancel.addActionListener(ae-> {
            replace.setVisible(false);
        });

        replace.add(labelPanel);
        replace.add(fieldPanel);
        replace.add(buttonPanel);
    }

    public void goTo(){
        goTo = new JDialog(frame, "Go To Line");
        goTo.setLocationRelativeTo(frame);
        goTo.setVisible(false);
        goTo.setLayout(new FlowLayout(FlowLayout.RIGHT));
        goTo.setSize(251, 125);
        goTo.setResizable(false);
        JLabel lineNumber = new JLabel("Line Number:");
        lineNumber.setDisplayedMnemonic('l');
        JTextField goToField = new JTextField(20);
        lineNumber.setLabelFor(goToField);
        JButton goToButton = new JButton("Go To");
        JButton cancel = new JButton("Cancel");
        JPanel labelFieldPanel = new JPanel(new GridLayout(2,1));
        labelFieldPanel.add(lineNumber);
        labelFieldPanel.add(goToField);
        goTo.getRootPane().setDefaultButton(goToButton);
        goToButton.addActionListener(ae-> {
            try {
                int index = Integer.parseInt(goToField.getText());
                textArea.setCaretPosition(textArea.getDocument()
                        .getDefaultRootElement().getElement(index)
                        .getStartOffset());
                goTo.setVisible(false);
            } catch (Exception e){
                JOptionPane.showMessageDialog(frame, "The line number is beyond " +
                        "the total number of lines", "JNotepad - Goto Line", JOptionPane.PLAIN_MESSAGE);
            }
        });
        cancel.addActionListener(ae-> {
            goTo.setVisible(false);
        });
        goTo.add(labelFieldPanel);
        goTo.add(goToButton);
        goTo.add(cancel);
    }

    public JPanel setStatusBar(){
        statusBarPanel = new JPanel();
        statusBarPanel.setVisible(false);
        statusBarPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusBarPanel.setPreferredSize(new Dimension(frame.getWidth(), 22));
        statusBarPanel.setLayout(new BorderLayout());
        status.setHorizontalAlignment(JLabel.RIGHT);
        statusBarPanel.add(status);
        return statusBarPanel;
    }

    public void cannotFind(String target){ // displays the target text is unreachable
        JOptionPane.showMessageDialog(null, "Cannot find \"" + target
                + "\"", "JNotePad", JOptionPane.INFORMATION_MESSAGE);
    }

    public void setNew(){
        initialText = "";
        textArea.setText("");
        frame.setTitle("Untitled - JNotepad");
        fileChosen = false;
    }

    public void saveFile(boolean saveAs){
        if(fileChosen && !saveAs) {
            try(FileWriter writer = new FileWriter(file)){
                writer.write(textArea.getText());
                writer.close();
                initialText = textArea.getText();
            } catch (Exception e){
                System.out.println("File Not Found.");
            }
        } else {
            FileFilter filter = new FileNameExtensionFilter(".txt & .java", "txt", "java");
            fileChooser = new JFileChooser();
            fileChooser.setFileFilter(filter);
            fileChooser.setDialogTitle("Save As");
            int res = fileChooser.showSaveDialog(frame);
            int replaceRes = JOptionPane.YES_OPTION;
            if(res == JFileChooser.APPROVE_OPTION){
                if(fileChooser.getSelectedFile().exists()){
                    replaceRes = JOptionPane.showConfirmDialog(frame, fileChooser.getSelectedFile().getName()
                    + " already exists.\nDo you want to replace it?", "Confirm Save As", JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                }
                if(replaceRes == JOptionPane.YES_OPTION){
                    try(FileWriter writer = new FileWriter((fileChooser.getSelectedFile()))) {
                        file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                        writer.write(textArea.getText());
                        writer.close();
                        initialText = textArea.getText();
                        fileChosen = true;
                        frame.setTitle(fileChooser.getSelectedFile().getName() + " - JNotepad");
                    } catch (Exception e){
                        System.out.println("File Not Found.");
                    }
                }
            }
        }
    }

    public int saveDialog(){
        fileName = fileChosen ? file.getName() : "Untitled";
        String[] options = {"Save", "Don't Save", "Cancel"};
        int res = JOptionPane.showOptionDialog(frame, "Do you want to save changes to " + fileName + "?",
                "JNotepad", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, "Save");
        return res;
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            try{
                UIManager.setLookAndFeel(
                        "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (Exception e){
            }
            new JNotepad();
        });
    }
}
