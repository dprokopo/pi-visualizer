/* 
 * Copyright 2017 Dagmar Prokopova <xproko26@stud.fit.vutbr.cz>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.vutbr.fit.xproko26.pivis.gui.enveditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import cz.vutbr.fit.xproko26.pivis.formater.TextBlob;

/**
 * EnvironmentalEditor represents gui conponet aimed for viewing and modifying process
 * definitions. It contains text area in which formated text representation of 
 * process definitions is being displayed and control buttons for editing, reseting
 * and loading/saving from/into file.
 * @author Dagmar Prokopova
 */
public class EnvironmentEditor extends JPanel {
    
    //reference to EnvironmentEditor listener
    private EnvironmentEditorListener listener;
    
    //text area
    private final JTextPane area;
    
    //original text which was set for text area
    private List<TextBlob> original;
    
    //control buttons
    private JButton editbutt;
    private JButton resetbutt;
    private JButton commitbutt;
    private JButton loadbutt;
    private JButton savebutt;
    
    /**
     * Constructor of environment editor which positions elements and sets 
     * default look of both, the text area and control buttons.
     */
    public EnvironmentEditor() {
        
        original = new ArrayList<>();
        
        area = new JTextPane();
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        area.setFont(new Font("Courier New", Font.PLAIN, 18));
     
        JScrollPane scroll = new JScrollPane(area);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                
        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.EAST);
        
        disableEdit();
    }
    
    /**
     * Clears text area and set disabled-edit state.
     */
    public void init() {
        area.setText("");
        disableEdit();
    }    
    
    /**
     * Adds environment editor listener.
     * @param l listener to be added
     */
    public void addListener(EnvironmentEditorListener l) {
        listener = l;
    }    
    
    /**
     * Creates panel containing control buttons and initializes them with
     * actions to be performed upon user interaction.
     * @return 
     */
    private JPanel createControlPanel() {
                        
        JPanel ctrlPanel = new JPanel();
        ctrlPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints cons = new GridBagConstraints();
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.weightx = 1;
        cons.gridx = 0;
        
        editbutt = new JButton("Edit");
        editbutt.setPreferredSize(new Dimension(150,30));
        editbutt.addActionListener((ActionEvent ae) -> {
            //set edit mode
            enableEdit();
        });
        
        resetbutt = new JButton("Discard changes");
        resetbutt.setPreferredSize(new Dimension(150,30));
        resetbutt.addActionListener((ActionEvent ae) -> {
            //set original text
            setText(original);
        });
        
        commitbutt = new JButton("Commit changes");
        commitbutt.setPreferredSize(new Dimension(150,30));
        commitbutt.addActionListener((ActionEvent ae) -> {
            if (listener != null)
                listener.commitProcDefs(area.getText());
        });
        
        loadbutt = new JButton("Load from file");
        loadbutt.setPreferredSize(new Dimension(150,30));
        loadbutt.addActionListener((ActionEvent ae) -> {
            if (listener != null)
                listener.loadProcDefs();
        });
        
        savebutt = new JButton("Save to file");
        savebutt.setPreferredSize(new Dimension(150,30));
        savebutt.addActionListener((ActionEvent ae) -> {
            if (listener != null)
                listener.saveProcDefs();
        });
        
        ctrlPanel.add(editbutt, cons);
        ctrlPanel.add(resetbutt, cons);
        ctrlPanel.add(commitbutt, cons);
        ctrlPanel.add(loadbutt, cons);
        ctrlPanel.add(savebutt, cons);
        
        //create wrap panel to keep elements on the top
        JPanel wrapPanel = new JPanel();
        wrapPanel.setLayout(new BorderLayout());
        wrapPanel.add(ctrlPanel, BorderLayout.NORTH);        
        return wrapPanel;
    }
    
    /**
     * Sets font size of text area.
     * @param fs 
     */
    public void setFontSize(int fs) {
        area.setFont(new Font("Courier New", Font.PLAIN, fs));
    }
        
    /**
     * Sets color scheme of editable area.
     * @param background background color
     * @param foreground foreground color (text and caret)
     */
    private void setColor(Color background, Color foreground) {
        area.setBackground(background);
        area.setForeground(foreground);
        area.setCaretColor(foreground);        
    }

    /**
     * Sets formated content specified by list of TextBlobs into the
     * editable area.
     * @param list list of TextBlobs
     */
    public void setText(List<TextBlob> list) {
        setText(list, true);
    }
    
    /**
     * Based on the value of second argument sets either formated or unformated
     * text specified by list of TextBlobs into the text area.
     * @param list list of text blobs to be set as content
     * @param formated boolean value indicating whether text should be formated
     */
    private void setText(List<TextBlob> list, boolean formated) {
        
        //save content into original list and clear text
        original = list;
        area.setText("");
        
        StyledDocument doc = area.getStyledDocument();

        Style style = area.addStyle("style", null);

        //for each TextBlob
        for (TextBlob blob : list) {
            
            //select style according to flags of processed TextBlob
            if (blob.isProcId() && formated) {
                StyleConstants.setForeground(style, Color.RED);
                //StyleConstants.setBold(style, true);
            }
            else if (blob.isName() && formated) {
                StyleConstants.setForeground(style, Color.BLUE);
                //StyleConstants.setBold(style, true);
            }            
            else {
                StyleConstants.setForeground(style, Color.BLACK);
                //StyleConstants.setBold(style, false);
            }

            try {            
                //insert styled text
                doc.insertString(doc.getLength(), blob.getText(), style);
            } catch (BadLocationException ex) {
            }
        }
        
        //set uneditable mode
        disableEdit();
    }
    
    /**
     * Sets uneditable mode and reports change to listener. 
     * As for visual effects the background color is grayed and
     * appropriate control buttons are either enabled or disabled.
     */
    private void disableEdit() {
        setColor(new Color(245,245,245), Color.BLACK);
        area.setEditable(false);
        editbutt.setEnabled(true);
        resetbutt.setEnabled(false);
        commitbutt.setEnabled(false);
        savebutt.setEnabled(true);
        loadbutt.setEnabled(true);
        if (listener != null)
            listener.editDisabled();
    }
    
    /**
     * Sets editable mode and reports change to listener.
     * As for visual effects, the background color is changed to white and
     * appropriate control buttons are either enabled or disabled.
     */
    private void enableEdit() {

        //set unformated text
        setText(original, false);
        
        setColor(new Color(255,255,255), Color.BLACK);
        area.setEditable(true);
        editbutt.setEnabled(false);
        resetbutt.setEnabled(true);
        commitbutt.setEnabled(true);
        savebutt.setEnabled(false);
        loadbutt.setEnabled(false);
        if (listener != null)
            listener.editEnabled();
    }    
}
