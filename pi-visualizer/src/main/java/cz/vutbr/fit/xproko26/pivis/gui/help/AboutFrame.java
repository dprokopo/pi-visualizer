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
package cz.vutbr.fit.xproko26.pivis.gui.help;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import cz.vutbr.fit.xproko26.pivis.gui.TextResource;

/**
 * AboutFrame is an additional frame component which holds information about
 * the application and a http link from which the source code can be obtained.
 * @author Dagmar Prokopova
 */
public class AboutFrame extends JFrame {

    /**
     * Constructor which creates the frame and sets its look and content.
     */
    public AboutFrame() {
        setMinimumSize(new Dimension(600, 300));
        setPreferredSize(new Dimension(600,300));
        setTitle("About");
        setIconImage(new ImageIcon(getClass().getResource("/images/appicon.png")).getImage());
        setResizable(false);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new BorderLayout());     
        
        setContent();
        
        pack();
        setLocationRelativeTo(null);
    }
    
    /**
     * Creates and styles inner components of the frame.
     */
    private void setContent() {
        
        JPanel contentPanel = new JPanel();
        contentPanel.setPreferredSize(new Dimension(600,300));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setLayout(new BorderLayout());
        
        JLabel content = new JLabel(TextResource.getInstance().getString("about-html"));        
        content.setVerticalAlignment(JLabel.NORTH);
        content.setFont(new Font("Calibri", Font.PLAIN, 16));
        
        JPanel linkwrap = new JPanel();
        JLabel link = new JLabel(TextResource.getInstance().getString("about-link"));
        link.setVerticalAlignment(JLabel.NORTH);
        link.setFont(new Font("Calibri", Font.PLAIN, 16));
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.setForeground(Color.BLUE);
        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    open(new URI(link.getText()));
                } catch (IOException | URISyntaxException ex) {
                    //could not open link, so lets ignore it
                }
            }
        });
        linkwrap.add(link);

        contentPanel.add(content, BorderLayout.NORTH);
        contentPanel.add(linkwrap);
        
        getContentPane().add(contentPanel);
    }
    
    /**
     * Method which tries to open web browser and display the specified URI
     * @param uri web address
     * @throws IOException if process failed
     */
    private void open(URI uri) throws IOException {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(uri);
        }        
    }
}
