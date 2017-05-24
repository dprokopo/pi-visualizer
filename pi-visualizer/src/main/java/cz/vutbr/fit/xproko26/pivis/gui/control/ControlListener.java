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

import cz.vutbr.fit.xproko26.pivis.gui.graph.graphlib.ExportAction;

/**
 * ControlListener contains methods for reporting user interaction with
 * menu items or toolbar buttons.
 * @author Dagmar Prokopova
 */
public interface ControlListener {
    
    /**
     * Reports usage of 'new' item.
     */
    public void menuCreateNew();
    
    /**
     * Reports usage of 'open' item.
     */
    public void menuLoadFromFile();
    
    /**
     * Reports usage of 'save' item.
     */
    public void menuSaveToFile();

    /**
     * Reports usage of 'save as' item.
     */
    public void menuSaveAsToFile();

    /**
     * Reports usage of some of the export graph items.
     * @param ea export action specifying which item was used
     */
    public void menuExport(ExportAction ea);
    
    /**
     * Reports usage of 'load environment' item.
     */
    public void menuLoadEnvironment();
    
    /**
     * Reports usage of 'save environment' item.
     */
    public void menuSaveEnvironment();
    
    /**
     * Reports usage of 'exit' item.
     */
    public void menuExit();
    
    /**
     * Reports usage of 'clear console' item.
     */
    public void menuClearConsole();
    
    /**
     * Reports usage of 'visualize expression' item.
     */
    public void menuShow();
    
    /**
     * Reports changed value of 'reduction ist' item.
     * @param b true if item is selected
     */
    public void menuVisibleRedList(boolean b);
    
    /**
     * Reports usage of 'reduce expression' item.
     */
    public void menuReduce();
    
    /**
     * Reports usage of 'simplify expression' item.
     */
    public void menuSimplify();

    /**
     * Reports changed value of 'font size' item.
     * @param fs selected font size option
     */
    public void menuFontSize(Control.FontSize fs);
    
    /**
     * Reports changed value of 'console color' item.
     * @param cs selected solor scheme option
     */
    public void menuColorScheme(Control.ColorScheme cs);
    
    /**
     * Reports changed value of 'graph layout' item.
     * @param es selected expand style option
     */
    public void menuExpStyle(Control.ExpStyle es);
    
    /**
     * Reports changed value of 'sound' item.
     * @param b true if item is selected
     */
    public void menuSoundOn(boolean b);

    /**
     * Reports usage of 'how to' item.
     */
    public void menuHelp();

    /**
     * Reports usage of 'about' item.
     */
    public void menuAbout();

    


}
