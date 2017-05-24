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
package cz.vutbr.fit.xproko26.pivis.gui.textline;

import cz.vutbr.fit.xproko26.pivis.formater.TextBlob;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * TextLine represents gui conponet aimed for viewing the textual representation
 * of visualized expression. It displays formated text which highlightes exactly
 * those parts of the expression which correspond to the selected graphical nodes.
 * @author Dagmar Prokopova
 */
public final class TextLine extends JPanel {
    
    //text area
    private final JTextPane area;
    
    /**
     * Constructor which sets the default look of the text line.
     */
    public TextLine() {

        area = new JTextPane();
        area.setEditable(false);
        area.setPreferredSize(new Dimension(1024, 40));
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEtchedBorder(), 
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        setFontSize(18);
        setColor(Color.LIGHT_GRAY, Color.BLACK);
     
        JScrollPane scroll = new JScrollPane(area);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);
    }
    
    /**
     * Initializes text line with empty string.
     */
    public void init() {
        area.setText("");
    }

    /**
     * Sets font size of the text.
     * @param fs font size
     */
    public void setFontSize(int fs) {
        area.setFont(new Font("Courier New", Font.PLAIN, fs));
    }
        
    /**
     * Sets text line color scheme.
     * @param background background color
     * @param foreground foreground color
     */
    private void setColor(Color background, Color foreground) {
        area.setBackground(background);
        area.setForeground(foreground);     
    }
    
    /**
     * Sets formated text specified by the list of TextBlobs into the textline.
     * @param list list of text blobs to be set as a content
     */
    public void setText(List<TextBlob> list) {

        //clear text
        area.setText("");
        
        StyledDocument doc = area.getStyledDocument();
        Style style = area.addStyle("style", null);
        
        //for each TextBlob
        for (TextBlob blob : list) {
            
            //select style according to flags of processed TextBlob
            if (blob.isSelected()) {
                StyleConstants.setForeground(style, Color.BLUE);
                StyleConstants.setBold(style, true);
            }
            else if (blob.isReductionSelected()) {
                StyleConstants.setForeground(style, Color.RED);
                StyleConstants.setBold(style, true);
            }            
            else {
                StyleConstants.setForeground(style, Color.BLACK);
                StyleConstants.setBold(style, false);
            }

            try {            
                //insert styled text
                doc.insertString(doc.getLength(), blob.getText(), style);
            } catch (BadLocationException ex) {
            }
        }
    }
}
