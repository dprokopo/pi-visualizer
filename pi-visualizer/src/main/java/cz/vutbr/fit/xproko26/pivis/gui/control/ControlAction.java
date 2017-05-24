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
package cz.vutbr.fit.xproko26.pivis.gui.control;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

/**
 * ControlAction defines action performed upon user interaction with menu or 
 * toolbar item. Apart from action it stores also title and icon of the control 
 * element (button or menu item) and keyboard shortcut.
 * @author Dagmar Prokopova
 */
public class ControlAction extends AbstractAction {
    
    /**
     * Creates control action with text title, icon and keyboard shortcut specified.
     * @param text title of action
     * @param icon icon of action
     * @param ks keyboard shortcut for action
     */
    public ControlAction(String text, ImageIcon icon, KeyStroke ks) {
        super(text, icon);
        putValue(Action.ACCELERATOR_KEY, ks);
    }
    
    /**
     * Creates control action with text title, icon ad keyboard shortcut specified.
     * Apart from that it also sets default state (selected or deselected).
     * @param text title of action
     * @param icon icon of action
     * @param ks keyboard shortcut for action
     * @param selected 
     */
    public ControlAction(String text, ImageIcon icon, KeyStroke ks, boolean selected) {
        super(text, icon);
        putValue(Action.ACCELERATOR_KEY, ks);
        putValue(Action.SELECTED_KEY, selected);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        
    }
    
}
