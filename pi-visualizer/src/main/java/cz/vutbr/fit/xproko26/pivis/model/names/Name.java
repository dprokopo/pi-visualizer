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
package cz.vutbr.fit.xproko26.pivis.model.names;

import java.io.Serializable;

/**
 * Abstract class which represents name of the pi-calculus.
 * @author Dagmar Prokopova
 */
public abstract class Name implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    //flag indicating if the name is private
    private boolean priv;
    
    //flag indicating if the name is process
    private boolean process;
    
    //flag indicating if the name is defined process
    private boolean defproc;
  
    /**
     * Constuctor which initializes private flag and process flag as false.
     */
    public Name() {
        priv = false;
        process = false;
        defproc = false;
    }
    
    /**
     * Returns true if the name is private (i.e. defined by restriction expression).
     * @return boolean private flag value
     */
    public boolean isPrivate() {
        return priv;
    }
    
    /**
     * Sets private flag of the name to true.
     */
    public void setPrivate() {
        priv = true;
    }
    
    /**
     * Returns true if the name is process.
     * @return boolean process flag value
     */
    public boolean isProcess() {
        return process;
    }
    
    /**
     * Sets process flag of the name to true.
     */
    public void setProcess() {
        process = true;
    }
    
    /**
     * Returns true if the name is defined process.
     * @return boolean defined process flag value
     */
    public boolean isDefProcess() {
        return defproc;
    }
    
    /**
     * Sets defined process flag of the name.
     */
    public void setDefProcess(boolean b) {
        defproc = b;
    }
    
    /**
     * Returns simple unformated string as textual representation of the name.
     * @return string
     */
    @Override
    public abstract String toString();
    
    /**
     * Returns simple unformated string for debugging purposes.
     * @return string
     */
    public abstract String toStringDebug();
    
    /**
     * Creates copy of the name.
     * @return copied name
     */
    protected abstract Name copy();
    
}
