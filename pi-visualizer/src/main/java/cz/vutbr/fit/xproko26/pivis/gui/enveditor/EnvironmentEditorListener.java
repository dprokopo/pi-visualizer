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

/**
 * Interface containing methods for reporting user interaction in environment
 * panel.
 * @author Dagmar Prokopova
 */
public interface EnvironmentEditorListener {
    /**
     * Reports that user modified process definitions and wants to commit changes.
     * @param lines text string containing process definitions separated by newline
     */
    public void commitProcDefs(String lines);
    
    /**
     * Reports user intention to load process definitions from file.
     */
    public void loadProcDefs();
    
    /**
     * Reports user intention to save process definition into the file
     */
    public void saveProcDefs();
    
    /**
     * Reports that edit mode was activated.
     */
    public void editEnabled();
    
    /**
     * Reports that edit mode was disabled.
     */
    public void editDisabled();
}
