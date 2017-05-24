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
package cz.vutbr.fit.xproko26.pivis.model.redmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import cz.vutbr.fit.xproko26.pivis.model.names.NRList;

/**
 * Reduction context is an auxiliary structure used by reduction manager 
 * during its serch for available reductions to store information about
 * which concretization expressions with which arguments were already used
 * and from which process (caller). This prevents an infinite search 
 * as well as duplicit reductions to be found. Reduction context also stores 
 * 'enabled' flag which indicates whether reductions should be extracted.
 * @author Dagmar Prokopova
 */
public class ReductionContext {
    
    //the calling index in format: <caller, <called process, arguments>>
    private HashMap<String, HashMap<String, List<NRList>>> called;
    
    //process identifier of the caller
    private String caller;
    
    //flag indicating whether reduction extraction is enabled
    private boolean enabled;
    
    /**
     * Constructor which initializes calling index and enabled flag
     */
    public ReductionContext() {
        called = new HashMap<>();        
        enabled = true;
    }    

    /**
     * Sets calling index.
     * @param c calling index to be set
     */
    private void setCalled(HashMap<String, HashMap<String, List<NRList>>> c) {
        called = c;
    }
    
    /**
     * Returns calling index.
     * @return calling index
     */
    private HashMap<String, HashMap<String, List<NRList>>> getCalled() {
        return new HashMap<>(called);
    }

    /**
     * Returns copy of reduction context.
     * @return copy
     */
    public ReductionContext copy() {
        ReductionContext ret = new ReductionContext();
        ret.setCalled(getCalled());
        ret.caller = caller;
        return ret;
    }
    
    /**
     * Sets enabled flag.
     * @param b boolean value the flag should be set to
     */
    public void setEnabled(boolean b) {
        enabled = b;
    }
    
    /**
     * Returns true if enabled flag is on.
     * @return enabled flag value
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Sets new caller and creates new record for it in calling index if needed.
     * @param id caller process identifier
     */
    private void setCaller(String id) {
        caller = id;
        if (called.get(id) == null) {
            called.put(id, new HashMap<>());
        }
    }
    
    /**
     * Adds new list of arguments used for specified process and sets this
     * process as new caller.
     * @param id process for which new arguments were used
     * @param args new list of arguments
     */
    public void update(String id, NRList args) {
        if (caller != null) {                        
            List<NRList> nrlists = called.get(caller).get(id);
            if (nrlists == null) {
                nrlists = new ArrayList<>();
                nrlists.add(args);
                called.get(caller).put(id, nrlists);
            }
        }
        setCaller(id);
    }
    
    /**
     * Returns true there is no record for the specified process and arguments
     * in the calling index.
     * @param id called process identifier
     * @param args list of arguments
     * @return boolean value indicating whether such concretion was used before
     */
    public boolean notUsed(String id, NRList args) {
        
        if (caller != null) {
        
            List<NRList> nrlists = called.get(caller).get(id);
            if (nrlists != null) { //such process was already called before
                for (NRList list : nrlists) {
                    //check if the arguments have the same source
                    if (list.srcequals(args)) {
                        return false;
                    }
                }
            }
        }        
        return true;
    }
    
}
