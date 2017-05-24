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

import cz.vutbr.fit.xproko26.pivis.model.redmanager.Reduction;
import cz.vutbr.fit.xproko26.pivis.model.redmanager.IOReduction;
import cz.vutbr.fit.xproko26.pivis.model.redmanager.TReduction;

import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * ReductionTableModel represents internal model of reduction table used in
 * {@link ReductionPanel ReductionPanel}. It stores list of {@link Reduction 
 * Reductionss} and provides method to obtain elements from this list. The 
 * reduction table comprises of 3 columns - the first for channel name, the second 
 * for input parameters and the last one for output parameters. In case of 
 * unobservable tau reduction, there is a '?' sign in all fields.
 * @author Dagmar Prokopova
 */
public class ReductionTableModel extends AbstractTableModel {
    
    //internal list of reductions
    private final List<Reduction> redlist;

    /**
     * Initializes internal list of reductions with specified argument.
     * @param reds list of reductions
     */
    public ReductionTableModel(List<Reduction> reds) {
        this.redlist = reds;
    }
    
    /**
     * Returns number of table rows.
     * @return row count
     */
    @Override
    public int getRowCount() {
        return redlist.size();
    }

    /**
     * Returns number of table columns.
     * @return column count
     */
    @Override
    public int getColumnCount() {
        return 3;
    }

    /**
     * Returns value of table cell specified by row and column index.
     * @param row row index
     * @param column column index
     * @return text value
     */
    @Override
    public Object getValueAt(int row, int column) {        

        Reduction red = redlist.get(row);

        if (red instanceof TReduction) {
            return "?";
        } else {
            
            Object value = "";
            switch (column) {
                case 0:
                    value = ((IOReduction) red).getIn().getChannel().toString();
                    break;
                case 1:
                    value = ((IOReduction) red).getIn().getParams().toString();
                    break;
                case 2:
                    value = ((IOReduction) red).getOut().getParams().toString();
                    ;
                    break;
            }

            return value;
        }
    }
    
    /**
     * Returns title of the table column specified by index.
     * @param index index of the column
     * @return title of the column
     */
    @Override
    public String getColumnName(int index) {
        switch (index) {
            case 0:
                return "Channel";
            case 1:
                return "Input";
            case 2:
                return "Output";
            default:
                return "";
        }
    }
    
    /**
     * Returns reduction placed at specified index of reduction list.
     * @param row index into the list
     * @return reduction
     */
    public Reduction getReductionAt(int row) {
        return redlist.get(row);
    }
}
