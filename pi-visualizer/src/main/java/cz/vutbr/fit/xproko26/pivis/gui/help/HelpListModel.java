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

import java.util.List;
import javax.swing.AbstractListModel;

/**
 * HelpListModel represents internal model of help chapter list used by
 * {@link HelpFrame HelpFrame}. It stores list of {@link HelpElement HelpElements}.
 * @author Dagmar Prokopova
 */
public class HelpListModel extends AbstractListModel {

    //list of help elements
    private final List<HelpElement> helplist;

    /**
     * Initializes internal list of help elements.
     * @param list list of help elements
     */
    public HelpListModel(List<HelpElement> list) {
        this.helplist = list;
    }
    
    /**
     * Returns size of the list.
     * @return size of the list
     */
    @Override
    public int getSize() {
        return helplist.size();
    }

    /**
     * Returns help element title at specified index.
     * @param i index
     * @return help element title
     */
    @Override
    public Object getElementAt(int i) {
        return getHelpElementAt(i).getTitle();
    }
    
    /**
     * Returns help element at specified index.
     * @param i index into the list
     * @return help element
     */
    public HelpElement getHelpElementAt(int i) {
        return helplist.get(i);
    }
    
}
