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
package cz.vutbr.fit.xproko26.pivis.gui.redpanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * ReductionPanel represents gui conponet aimed for viewing and selecting available
 * reductions. It contains table of reductions and two control buttons - one for
 * deselecting reduction and the second for executing reduction.
 * @author Dagmar Prokopova
 */
public class ReductionPanel extends JPanel {
    
    //reference to reduction panel listener
    private ReductionPanelListener listener;
    
    //reduction table
    private JTable table;
    
    /**
     * Constructor of reduction panel which positions elements and sets 
     * default look of both, the reduction table and control buttons.
     */
    public ReductionPanel() {       
        setMinimumSize(new Dimension(200, 360));
        setPreferredSize(new Dimension(200,360));
        setLayout(new BorderLayout());
        add(createTitlePanel(), BorderLayout.NORTH);
        add(new JScrollPane(createTable()));
        add(createButtonPanel(), BorderLayout.SOUTH);
        
        setVisible(false);
    }
    
    /**
     * Creates title panel which contains titla and also a close button.
     * @return created panel
     */
    private JPanel createTitlePanel() {
        
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.setLayout(new BorderLayout());
        
        JLabel title = new JLabel();
        title.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        title.setText("Reduction list");
        
        JButton closebutt = new JButton();
        closebutt.setIcon((Icon) UIManager.getDefaults().get("InternalFrame.closeIcon"));
        closebutt.setPreferredSize(new Dimension(20,20));
        closebutt.addActionListener((ActionEvent ae) -> {
            if (listener != null) {
                listener.closeReductionList();
            }
        });
        
        panel.add(title);
        panel.add(closebutt, BorderLayout.EAST);
        return panel;
    }
    
    /**
     * Creates reduction table and sets its selection listener.
     * @return created reduction table
     */
    private JTable createTable() {                
        
        table = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {                
                return false;               
            }
        };
        table.setModel(new ReductionTableModel(new ArrayList<>()));
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.enableInputMethods(false);
               
        //center content of table cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i=0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        ListSelectionModel cellSelectionModel = table.getSelectionModel();

        cellSelectionModel.addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting())
                return;
            
            if (table.getSelectedRow() >= 0) {
                if (listener != null)
                    listener.reductionSelected(((ReductionTableModel)table.getModel()).getReductionAt(table.getSelectedRow()));
            }
        });
        
        return table;
   
    }

    /**
     * Creates panel containing control buttons and sets their action listeners.
     * @return created panel with buttons
     */
    private JPanel createButtonPanel() {

        JButton clearButton = new JButton("Clear");
        clearButton.setPreferredSize(new Dimension(80,40));
        clearButton.addActionListener((ActionEvent ae) -> {
            if (listener != null) {
                listener.clearButtonClicked();
                if (table != null) {
                    table.getSelectionModel().clearSelection();
                }
            }
        });
        
        JButton reduceButton = new JButton("Reduce");
        reduceButton.setPreferredSize(new Dimension(80,40));
        reduceButton.addActionListener((ActionEvent ae) -> {
            if (listener != null) {
                listener.reduceButtonClicked();
            }
        });
        
        JPanel buttonpanel = new JPanel();
        buttonpanel.setBorder(BorderFactory.createLoweredBevelBorder());
        buttonpanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        buttonpanel.add(clearButton, c);
        buttonpanel.add(reduceButton, c);
        
        return buttonpanel;
    }
    
    /**
     * Adds reduction panel listener.
     * @param l listener to be added
     */
    public void addListener(ReductionPanelListener l) {
        listener = l;
    }
    
    /**
     * Fills the reduction table with data contained in reduction table model.
     * @param model model containing list of reductions
     */
    public void fill(ReductionTableModel model) {
        TableCellRenderer renderer = table.getColumnModel().getColumn(0).getCellRenderer();        
        table.setModel(model);        
        for (int i=0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }    

    /**
     * Sets selected row of the table specified by an argument.
     * @param i indes into the table
     */
    public void setSelection(int i) {
        if (table != null) {
            if (i >= 0) {
                //this is just a hack not to invoke listener methods
                table.getSelectionModel().setValueIsAdjusting(true);
                table.setRowSelectionInterval(i, i);
            }
            else {
                table.getSelectionModel().clearSelection();
            }
        }
    }
    
    /**
     * Initializes reduction table with empty reduction model.
     */
    public void init() {
        fill(new ReductionTableModel(new ArrayList<>()));
    }
    
}
