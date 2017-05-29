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
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import cz.vutbr.fit.xproko26.pivis.gui.TextResource;

/**
 * HelpFrame is an additional frame component which holds instructions about
 * how to use the application. The instructions are separated into thematic 
 * chapters. The frame comprises of list of chapters and text area.
 * @author Dagmar Prokopova
 */
public class HelpFrame extends JFrame {

    /**
     * Constructor which creates the frame and sets its look and content.
     */
    public HelpFrame() {

        setMinimumSize(new Dimension(800, 720));
        setPreferredSize(new Dimension(800,720));
        setTitle("Help");
        setIconImage(new ImageIcon(getClass().getResource("/images/appicon.png")).getImage());
        setResizable(true);
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

        JLabel content = new JLabel();        
        content.setVerticalAlignment(JLabel.NORTH);
        content.setFont(new Font("Calibri", Font.PLAIN, 16));

        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 50));
        contentPanel.setPreferredSize(new Dimension(600,0));
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(content, BorderLayout.NORTH);
        
        JScrollPane pane = new JScrollPane(contentPanel);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JList list = new JList();        
        list.setBorder(BorderFactory.createEmptyBorder(10,10, 10, 10));
        list.setPreferredSize(new Dimension(200, 0));
        list.setFont(new Font("Calibri", Font.PLAIN, 18));
        list.setModel(createHelpListModel());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.enableInputMethods(false);
        
        ListSelectionModel cellSelectionModel = list.getSelectionModel();
        cellSelectionModel.addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting())
                return;
            
            HelpListModel model = (HelpListModel)list.getModel();
            content.setText((String) model.getHelpElementAt(list.getSelectedIndex()).getContent());
            revalidate();
            contentPanel.setPreferredSize(new Dimension(600, (int) content.getSize().getHeight()+350));
            contentPanel.revalidate();
            revalidate();
        });
        list.setSelectedIndex(0);
        
        getContentPane().add(list, BorderLayout.WEST);
        getContentPane().add(pane);
    }

    /**
     * Creates list model which stores {@link HelpElement HelpElement} objects
     * containing chapter title and html-formated string which should be presented
     * in the text area after chapter selection
     * @return 
     */
    private HelpListModel createHelpListModel() {
        
        List<HelpElement> hlist = new ArrayList<>();
        
        hlist.add(new HelpElement(
            TextResource.getInstance().getString("intro-title"),
            TextResource.getInstance().getString("intro-html")
        ));        
        
        hlist.add(new HelpElement(
            TextResource.getInstance().getString("procspec-title"),
            TextResource.getInstance().getString("procspec-html")
        ));
        
        hlist.add(new HelpElement(
            TextResource.getInstance().getString("procvis-title"),
            TextResource.getInstance().getString("procvis-html")
        ));
        
        hlist.add(new HelpElement(
            TextResource.getInstance().getString("layout-title"),
            TextResource.getInstance().getString("layout-html")
        ));
        
        hlist.add(new HelpElement(
            TextResource.getInstance().getString("reduction-title"),
            TextResource.getInstance().getString("reduction-html")
        ));
        
        hlist.add(new HelpElement(
            TextResource.getInstance().getString("simplification-title"),
            TextResource.getInstance().getString("simplification-html")
        ));
        
        hlist.add(new HelpElement(
            TextResource.getInstance().getString("files-title"),
            TextResource.getInstance().getString("files-html")
        ));
        
        return new HelpListModel(hlist);
    }
}
