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
package cz.vutbr.fit.xproko26.pivis.formater;

/**
 * TextBlob class represets a string unit enhanced by special flags 
 * for the formating purposes.
 * @author Dagmar Prokopova
 */
public class TextBlob {
    
    //string unit
    private String text;
    
    //indicates selection for reduction
    private boolean redselected;
    
    //indicates simple selection
    private boolean selected;
    
    //indicates process identifier
    private boolean procid;
    
    //indicates name
    private boolean name;
    
    /**
     * Constructor which creates neutral text blob with all flags set to false.
     * @param str text unit
     */
    public TextBlob(String str) {
        text = str;
        redselected = false;
        selected = false;
        procid = false;
        name = false;
    }
    
    /**
     * Sets the text.
     * @param t 
     */
    public void setText(String t) {
        text = t;
    }
    
    /**
     * Sets reduction-selection flag
     * @param b boolean value to be set
     */
    public void setReductionSelected(boolean b) {
        redselected = b;
    }
    
    /**
     * Sets selection flag
     * @param b boolean value to be set
     */
    public void setSelected(boolean b) {
        selected = b;
    }
    
    /**
     * Sets process identifier flag
     * @param b boolean value to be set
     */
    public void setProcId(boolean b) {
        procid = b;
    }
    
    /**
     * Sets name flag
     * @param b boolean value to be set
     */
    public void setName(boolean b) {
        name = b;
    }
    
    /**
     * Returns text string.
     * @return text
     */
    public String getText() {
        return text;
    }

    /**
     * Returns true if reduction-selection flag is on
     * @return reduction-selection flag
     */
    public boolean isReductionSelected() {
        return redselected;
    }
    
    /**
     * Returns true if selection flag is on
     * @return selection flag
     */
    public boolean isSelected() {
        return selected;
    }
    
    /**
     * Returns true if process identifier flag is on
     * @return process identifier flag
     */
    public boolean isProcId() {
        return procid;
    }
    
    /**
     * Returns true if name flag is on
     * @return name flag
     */
    public boolean isName() {
        return name;
    }
}
