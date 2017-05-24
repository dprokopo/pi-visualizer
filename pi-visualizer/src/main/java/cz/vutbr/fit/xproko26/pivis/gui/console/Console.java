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
package cz.vutbr.fit.xproko26.pivis.gui.console;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.NavigationFilter;
import javax.swing.text.Position;

/**
 * Console is the class which implements the functionallity of standard
 * text console. It allows the user to modify only the last prompted line and
 * maintains command history as well as buffered (multiple line) input.
 * Each complete command (separated with newline) is reporte to console listener.
 * @author Dagmar Prokopova
 */
public final class Console extends JPanel {    

    //default prompt
    private static final String PROMPT = "$> ";
    //newline separator
    private static final String NEWLINE = "\n";
        
    //reference to console listener
    private ConsoleListener listener;   
    
    //writable text area
    private final JTextArea area;
    
    //command history
    private final ConsoleHistory history;
    
    //position in area which separates uneditable and editable part
    private int stop;
    
    //document filter
    private boolean docFilterOn;
    
    //document listener
    private boolean docListenerOn;
    
    
    /**
     * Constructor which initializes the look and behaviour of console by
     * setting focus and key listener, navigation filter, document filter and 
     * document listener. It also initializes command history.
     */
    public Console() {

        area = new JTextArea();
        area.setLineWrap(true);
        area.setBorder(BorderFactory.createCompoundBorder(
            area.getBorder(), 
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        setFontSize(18);
        setColor(Color.BLACK, Color.WHITE);
                
        setFocusListener();        
        setKeyListener();
        setNavigationFilter();        

        AbstractDocument doc = (AbstractDocument) area.getDocument();
        setDocumentFilter(doc);
        setDocumentListener(doc);        
        
        history = new ConsoleHistory();
                
        JScrollPane scroll = new JScrollPane(area);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);        
        
        init();
    }       
    
    /**
     * Creates and sets focus listener which ensures that after gaining focus
     * the carret will be placed at the end of editable text.
     */
    private void setFocusListener() {
        area.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                area.getCaret().setVisible(false);
            }

            @Override
            public void focusGained(FocusEvent e) {
                area.getCaret().setDot(getAreaLen());
            }
        });
    }
    
    /**
     * Creates and sets key listener which processes key strokes for 
     * command history listing.
     */
    private void setKeyListener() {
        area.addKeyListener(new KeyListener() {        
            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    e.consume();
                    showHistory(history.up());
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    e.consume();
                    showHistory(history.down());
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }
    
    /**
     * Creates and sets navigation filter which skips prompt but at the same time
     * allows copying text from console. It also secures that cursor is visible only
     * in editable part.
     */
    private void setNavigationFilter() {
        area.setNavigationFilter(new NavigationFilter() {
            @Override
            public void setDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias) {

                int line = 0;
                int linestart = 0;

                try {
                    line = area.getLineOfOffset(dot);
                    linestart = area.getLineStartOffset(line);
                } catch (BadLocationException ex) {}

                //check if last line
                if (line == area.getLineCount() - 1) {

                    // check caret position in line
                    if ((dot - linestart) < PROMPT.length()) {
                        fb.setDot(linestart + PROMPT.length(), bias);
                    } else {
                        fb.setDot(dot, bias);
                    }

                    if (area.hasFocus()) {
                        area.getCaret().setVisible(true);
                    }
                } else {
                    fb.setDot(dot, bias);
                    area.getCaret().setVisible(false);
                }
            }

            @Override
            public void moveDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias) {
                fb.moveDot(dot, bias);
            }
        });
    }
    
    /**
     * Creates and sets document filter which prevents user editation of uneditable
     * part of console. It can be disabled and enabled again if needed.
     * @param doc abstract document of the console text area
     */
    private void setDocumentFilter(AbstractDocument doc) {
        docFilterOn = true;
        
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(final DocumentFilter.FilterBypass fb, final int offset, final String string, final AttributeSet attr) throws BadLocationException {
                if (offset >= stop || !docFilterOn) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void remove(final DocumentFilter.FilterBypass fb, final int offset, final int length) throws BadLocationException {
                if (offset >= stop || !docFilterOn) {
                    super.remove(fb, offset, length);
                }
            }

            @Override
            public void replace(final DocumentFilter.FilterBypass fb, final int offset, final int length, final String text, final AttributeSet attrs) throws BadLocationException {
                if (offset >= stop || !docFilterOn) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }
    
    /**
     * Creates and sets document listener which reacts to text changes.
     * It checks whether the input contains newline separator and if so,
     * invokes console listener methods to process the input. At the same
     * time it also updates history with new command.
     * @param doc abstract document of the console text area
     */
    private void setDocumentListener(AbstractDocument doc) {
        docListenerOn = true;
        
        doc.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent de) {
                if (docListenerOn) {

                    String text = area.getText().substring(stop);
                    int splitindex = text.indexOf(NEWLINE);
                    if (splitindex >= 0) {
                        String cmd = text.substring(0, splitindex + 1);
                        String buffered = text.substring(splitindex + 1);
                        history.add(text.substring(0, splitindex));
                        
                        
                        SwingUtilities.invokeLater(() -> {
                            replaceWith(cmd + PROMPT);
                            processInput(cmd);
                            area.append(buffered);
                        });
                    } else {
                        history.update(text);
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                if (docListenerOn) {
                    history.update(area.getText().substring(stop, getAreaLen()));
                }
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
            }
        });
    }
    
    /**
     * Returns legth of the text in console.
     * @return text length
     */
    private int getAreaLen() {
        return area.getDocument().getLength();
    }
    
    /**
     * Replaces editable part of the console with specified text and
     * sets the new position of uneditable/editable separator. The text
     * change is however not reflected by document listener.
     * @param cmd text to be set into editable part
     */
    private void replaceWith(String cmd) {
        docListenerOn = false;
        area.replaceRange(cmd, stop, getAreaLen());
        stop = getAreaLen();                                                          
        docListenerOn = true;
    }
    
    /**
     * Reports to the console listener that new text input should be processed.
     * @param cmd 
     */
    private void processInput(String cmd) {
        if (listener != null) {
            listener.consoleInput(cmd);
        }
    }
    
    /**
     * Replaces editable part of the console with history command if it is not
     * null. The text change is not reflected by document listener.
     * @param text 
     */
    private void showHistory(String text) {       
        if (text != null) {
            docListenerOn = false;
            area.replaceRange(text, stop, getAreaLen());
            docListenerOn = true;
        }
    }
    
    /**
     * Adds console listener.
     * @param cl listener to be added
     */
    public void addListener(ConsoleListener cl) {
        this.listener = cl;
    }
    
    /**
     * Clears text area and initializes history.
     */
    public void init() {
        clear();     
        history.init();
    }
    
    /**
     * Sets font size of the console.
     * @param fs font size
     */
    public void setFontSize(int fs) {
        area.setFont(new Font("Courier New", Font.PLAIN, fs));
    }
    
    /**
     * Sets color scheme of the console.
     * @param background background color
     * @param foreground foreground color (text and caret)
     */
    public void setColor(Color background, Color foreground) {
        area.setBackground(background);
        area.setForeground(foreground);
        area.setCaretColor(foreground);        
    }
    
    /**
     * Clears all text from the console and sets new position of uneditable/editable
     * separator. The text change is not reflected by document listener.
     */
    public void clear() {
        
        docFilterOn = false;
        docListenerOn = false;
        
        area.setText("");
        area.append(PROMPT);
        stop = getAreaLen();
        area.setCaretPosition(stop);   
        
        docListenerOn = true;
        docFilterOn = true;
    }

    /**
     * Writes specified text into the uneditable part of console while it keeps 
     * the state of editable part (i.e. unfinished command). The text change
     * is not reflected by document listener.
     * @param text 
     */
    public void write(String text) {
        
        docFilterOn = false;
        docListenerOn = false;
        
        area.replaceRange("", stop-PROMPT.length(), getAreaLen());                
        area.append(text);
        area.append(PROMPT);
        
        stop = getAreaLen();       

        area.append(history.get());
                
        docListenerOn = true;
        docFilterOn = true;
    }
    
    /**
     * Sets focus to text area.
     */
    public void setFocus() {
        area.requestFocusInWindow();
    }
    
}
