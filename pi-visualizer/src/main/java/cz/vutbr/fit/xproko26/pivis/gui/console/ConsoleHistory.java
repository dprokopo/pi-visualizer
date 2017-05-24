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

import java.util.ArrayList;
import cz.vutbr.fit.xproko26.pivis.gui.AudioPlayer;

/**
 * ConsoleHistory stores all commands inserted in the console and allows user
 * to list in them or temprarily modificate them. Upon adding new command
 * it checks whether the previous was not the same and if so, it does not add
 * duplicate.
 * @author Dagmar Prokopova
 */
public class ConsoleHistory {
    
    //list of original commands
    private final ArrayList<String> orig;
    
    //list of possibly modified history commands
    private final ArrayList<String> hist;
    
    //current index into history list
    private int histpoint;

    
    /**
     * Constructor which creates empty history lists and initializes index
     * to first empty item.
     */
    public ConsoleHistory() {

        hist = new ArrayList();
        orig = new ArrayList();
        histpoint = 0;
        hist.add("");
    }

    /**
     * Returns previous saved command (possibly modified) or null if there is none.
     * @return previous command string if present
     */
    public String up() {
        if (histpoint > 0) {
            histpoint -= 1;
            return hist.get(histpoint);
        } else {
            AudioPlayer.getInstance().shortbeep();
            return null;
        }
    }

    /**
     * Returns next saved command (possibly modified) or null if there is none.
     * @return next command string if present
     */
    public String down() {
        if (histpoint < hist.size() - 1) {
            histpoint += 1;
            return hist.get(histpoint);
        } else {
            AudioPlayer.getInstance().shortbeep();
            return null;
        }
    }
    
    /**
     * Returns saved command (possibly modified) at current index.
     * @return command string
     */
    public String get() {
        return hist.get(histpoint);
    }

    /**
     * Sets history index to last item and makes it empty.
     */
    public void init() {
        histpoint = hist.size() - 1;
        hist.set(histpoint, "");
    }

    /**
     * Replaces saved history command at actual index with specified text
     * @param text replacement text
     */
    public void update(String text) {
        hist.set(histpoint, text);
    }

    /**
     * Adds new record into history while checking for duplicity. Also restores
     * used history field to original command and moves history index to the end.
     * @param text new command to be added to history
     */
    public void add(String text) {

        //set used hist field back to its original
        if (histpoint != hist.size() - 1) {
            hist.set(histpoint, orig.get(histpoint));
        }

        //check if command was empty or same as previous
        if (text.equals("") || ((hist.size() > 1) && text.equals(hist.get(hist.size() - 2)))) {
            //only erase last
            hist.set(hist.size() - 1, "");
        } else {
            //create new record
            hist.set(hist.size() - 1, text);
            orig.add(orig.size(), text);
            hist.add("");
        }
        
        //move history index to end
        histpoint = hist.size() - 1;
    }


}
