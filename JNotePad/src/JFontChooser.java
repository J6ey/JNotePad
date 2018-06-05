/*
Name:       Tan, Joey
Project:    3
Due:        March 7, 2018
Course:     CS-245-01-w18

Description:
                This program implements the JFontChooser class that behaves like the
                JFileChooser class.
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

public class JFontChooser implements ListSelectionListener, ActionListener{

    private JDialog fontDialog;
    private String[] sizes, styles, fonts;
    private JLabel sampleText;
    private JList fontList, styleList, sizeList;
    private JTextField fontTextField, styleTextField, sizeTextField;
    private String sampleFont;
    private Font previousFont, font;
    private Color previousColor, color;
    private int sampleStyle, sampleSize;
    private boolean isSelected;

    public JFontChooser(){
        fontDialog = new JDialog();
        fontDialog.setModal(true);
        fontDialog.setTitle("Font");
        fontDialog.setSize(434, 260);
        fontDialog.setLayout(new FlowLayout());
        fontDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        fontTextField = new JTextField(12);
        styleTextField = new JTextField(11);
        sizeTextField = new JTextField(5);
        fontTextField.setActionCommand("fontText");
        styleTextField.setActionCommand("styleText");
        sizeTextField.setActionCommand("sizeText");
        JLabel fontLabel = new JLabel("Font:");
        JLabel styleLabel = new JLabel("Font Style:");
        JLabel sizeLabel = new JLabel("Size:");
        fontLabel.setLabelFor(fontTextField);
        styleLabel.setLabelFor(styleTextField);
        sizeLabel.setLabelFor(sizeTextField);
        JButton okButton = new JButton("OK");
        fontDialog.getRootPane().setDefaultButton(okButton);
        JButton cancelButton = new JButton("Cancel");
        JButton colorButton = new JButton("Color");
        colorButton.setMnemonic('c');
        sampleText = new JLabel("AaBbYyZz");
        sampleFont = "Lucida Console";
        sampleStyle = Font.PLAIN;
        sampleSize = 12;
        sampleText.setFont(new Font(sampleFont, sampleStyle, sampleSize));
        fontLabel.setDisplayedMnemonic('f');
        styleLabel.setDisplayedMnemonic('y');
        sizeLabel.setDisplayedMnemonic('s');
        fontList = new JList(getAllFonts());
        styleList = new JList(getAllStyles());
        sizes = Arrays.toString(getAllSizes()).split("[\\[\\]]")[1].split(", ");
        styles = getAllStyles();
        sizeList = new JList(sizes);
        fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        styleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel okCancelButton = new JPanel();
        okCancelButton.setLayout(new BoxLayout(okCancelButton, BoxLayout.Y_AXIS));
        okButton.setMaximumSize(cancelButton.getMaximumSize());
        okCancelButton.add(okButton);
        okCancelButton.add(Box.createRigidArea(new Dimension(0, 5)));
        okCancelButton.add(cancelButton);
        JPanel samplePanel = new JPanel();
        TitledBorder sampleBorder = BorderFactory.createTitledBorder("Sample");
        samplePanel.setBorder(sampleBorder);
        samplePanel.add(sampleText);
        samplePanel.setPreferredSize(new Dimension(140, 48));
        fontList.addListSelectionListener(this);
        styleList.addListSelectionListener(this);
        sizeList.addListSelectionListener(this);
        fontTextField.addActionListener(this);
        styleTextField.addActionListener(this);
        sizeTextField.addActionListener(this);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        colorButton.addActionListener(this);

        fontTextField.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                String typed = fontTextField.getText();
                int index = -1;
                for (int i = 0; i < fonts.length; i++) {
                    if(typed.length() > 0){
                        if(fonts[i].length() >= typed.length()){
                            if(fonts[i].substring(0, typed.length()).equalsIgnoreCase(typed)){
                                index = i;
                                break;
                            }
                        }
                    }
                }
                if(index != -1){
                    fontList.setSelectedIndex(index);
                    fontList.ensureIndexIsVisible(fontList.getSelectedIndex());
                }
            }
        });
        styleTextField.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                String typed = styleTextField.getText();
                int index = -1;
                for (int i = 0; i < styles.length; i++) {
                    if(typed.length() > 0){
                        if(styles[i].length() >= typed.length()){
                            if(styles[i].substring(0, typed.length()).equalsIgnoreCase(typed)){
                                index = i;
                                break;
                            }
                        }
                    }
                }
                if(index != -1){
                    styleList.setSelectedIndex(index);
                    styleList.ensureIndexIsVisible(styleList.getSelectedIndex());
                }
            }
        });
        sizeTextField.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                String typed = sizeTextField.getText();
                int index = -1;
                for (int i = 0; i < sizes.length; i++) {
                    if(typed.length() > 0){
                        if(sizes[i].length() >= typed.length()){
                            if(sizes[i].substring(0, typed.length()).equalsIgnoreCase(typed)){
                                index = i;
                                break;
                            }
                        }
                    }
                }
                if(index != -1){
                    sizeList.setSelectedIndex(index);
                    sizeList.ensureIndexIsVisible(sizeList.getSelectedIndex());
                }
            }
        });

        JPanel fontPanel = new JPanel(new GridBagLayout());
        JPanel stylePanel = new JPanel(new GridBagLayout());
        JPanel sizePanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        fontPanel.add(fontLabel, c);
        stylePanel.add(styleLabel, c);
        sizePanel.add(sizeLabel, c);
        c.gridx = 0;
        c.gridy = 1;
        fontPanel.add(fontTextField, c);
        stylePanel.add(styleTextField, c);
        sizePanel.add(sizeTextField, c);
        c.gridx = 0;
        c.gridy = 2;
        JScrollPane fontScroll = new JScrollPane(fontList);
        JScrollPane styleScroll = new JScrollPane(styleList);
        JScrollPane sizeScroll = new JScrollPane(sizeList);
        styleScroll.setPreferredSize(new Dimension(125, 110));
        sizeScroll.setPreferredSize(new Dimension(59,110));
        fontScroll.setPreferredSize(new Dimension(135,110));
        fontPanel.add(fontScroll, c);
        stylePanel.add(styleScroll, c);
        sizePanel.add(sizeScroll, c);
        fontDialog.add(fontPanel);
        fontDialog.add(stylePanel);
        fontDialog.add(sizePanel);
        fontDialog.add(okCancelButton);
        fontDialog.add(colorButton);
        fontDialog.add(Box.createRigidArea(new Dimension(30, 0)));
        fontDialog.add(samplePanel);
        fontDialog.setResizable(false);
    }

    public void valueChanged(ListSelectionEvent e){
        JList list = (JList) e.getSource();
        if(list.equals(fontList)){
            int index = list.getSelectedIndex();
            if(index != -1){
                sampleFont = fonts[index];
                sampleText.setFont(new Font(sampleFont, sampleStyle, sampleSize));
            }
        } else if(list.equals(styleList)){
            int index = list.getSelectedIndex();
            if(index != -1){
                sampleStyle = styles[index].equals("Regular") ? Font.PLAIN
                        : styles[index].equals("Italic") ? Font.ITALIC
                        : styles[index].equals("Bold") ? Font.BOLD
                        : Font.BOLD + Font.ITALIC;
                sampleText.setFont(new Font(sampleFont, sampleStyle, sampleSize));
            }
        } else {
            int index = list.getSelectedIndex();
            if(index != -1){
                sampleSize = Integer.parseInt(sizes[index]);
                sampleText.setFont(new Font(sampleFont, sampleStyle, sampleSize));
            }
        }
    }

    public void actionPerformed(ActionEvent ae){
        switch (ae.getActionCommand()){
            case "fontText":
                if(!textContains(fonts,  fontTextField.getText())){
                    JOptionPane.showMessageDialog(fontDialog, "There is no font with that name." +
                            "\nChoose a font from the list of fonts.", "Font", JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case "styleText":
                if(!textContains(styles, styleTextField.getText())){
                    JOptionPane.showMessageDialog(fontDialog, "This font is not available in that style." +
                            "\nChoose a style from the list of styles.", "Font", JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case "sizeText":
                try{
                    int input = Integer.parseInt(sizeTextField.getText());
                    sampleText.setFont(new Font(sampleFont, sampleStyle, input));
                } catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(fontDialog, "Size must be a number.",
                            "Font", JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case "OK":
                    fontDialog.setVisible(false);
                    font = sampleText.getFont();
                    color = sampleText.getForeground();
                    previousFont = sampleText.getFont();
                    previousColor = sampleText.getForeground();
                    isSelected = true;
                break;
            case "Cancel":
                fontDialog.setVisible(false);
                sampleText.setFont(previousFont);
                sampleText.setForeground(previousColor);
                break;
            case "Color":
                Color color = JColorChooser.showDialog(fontDialog, "Font", Color.BLACK);
                if(color != null){
                    sampleText.setForeground(color);
                }
                break;
        }
    }

    public boolean showDialog(Component parent){
        fontDialog.setLocationRelativeTo(parent);
        fontDialog.setVisible(true);
        if(isSelected){
            return true;
        }
        return false;
    }

    public boolean textContains(String[] arr, String target){

        for(String s : Arrays.asList(arr)){
            if(s.equalsIgnoreCase(target)){
                return true;
            }
        }
        return false;
    }

    public void setDefault(Font font){
        sampleText.setFont(font);
        this.font = font;
        previousFont = sampleText.getFont();
    }
    public void setDefault(Color color){
        sampleText.setForeground(color);
        this.color = color;
        previousColor = sampleText.getForeground();
    }
    public Font getFont(){

        return font;
    }
    public Color getColor(){

        return color;
    }
    private String[] getAllFonts(){
        fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        return fonts;
    }
    private String[] getAllStyles(){
        String[] styleArray = {"Regular", "Italic", "Bold", "Bold Italic"};
        return styleArray;
    }
    private int[] getAllSizes(){
        int[] sizes = new int[16];
        for (int i=0; i<5 ; i++) {
            sizes[i] = i + 8;
        }
        for(int i=5, j = 9; i<13; i++, j++){
            sizes[i] = i + j;
        }
        sizes[13] = 36;
        sizes[14] = 48;
        sizes[15] = 72;
        return sizes;
    }
}
