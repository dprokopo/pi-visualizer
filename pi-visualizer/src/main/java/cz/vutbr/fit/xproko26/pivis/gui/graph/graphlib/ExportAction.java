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
package cz.vutbr.fit.xproko26.pivis.gui.graph.graphlib;

import java.io.FileOutputStream;

/**
 * ExportAction is a class used by graph library to store information about
 * supported graph export formats. It holds the name which shall be displayed
 * on corresponding menu item, the file extension, the transparency flag and 
 * {@link export export} method which can be overriden to actually perform
 * export action.
 * @author Dagmar Prokopova
 */
public class ExportAction {    
    
    //name of the export action
    private final String name;
    
    //export file extension
    private final String extension;
    
    //transparency flag
    private final boolean transparent;
    
    /**
     * Creates export action of specified name. The extension is set to be
     * the same as name and transparency flag is set to false.
     * @param n name
     */
    public ExportAction(String n) {
        name = n;
        extension = n;
        transparent = false;
    }
    
    /**
     * Creates export action of specified name and extension. The transparency
     * flag is set to false;
     * @param n name
     * @param e extension
     */
    public ExportAction(String n, String e) {
        name = n;
        extension = e;
        transparent = false;
    }
    
    /**
     * Creates export action of specified name, extension and transparency flag
     * @param n name
     * @param e extenssion
     * @param t transparency flag
     */
    public ExportAction(String n, String e, boolean t) {
        name = n;
        extension = e;
        transparent = t;
    }
    
    /**
     * Method which does initially nothing but shall be overriden.
     * @param os file output stream
     * @throws Exception if export failes
     */
    public void export(FileOutputStream os) throws Exception {
    }
    
    /**
     * Returns name of export action.
     * @return name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns file extension of export action.
     * @return file extension
     */
    public String getExtension() {
        return extension;
    }
    
    /**
     * Returns true if transparency flag is on
     * @return value of transparency flag
     */
    public boolean isTransparent() {
        return transparent;
    }
}
