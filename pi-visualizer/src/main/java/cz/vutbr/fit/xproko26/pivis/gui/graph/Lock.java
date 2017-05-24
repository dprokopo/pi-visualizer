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
package cz.vutbr.fit.xproko26.pivis.gui.graph;

/**
 * Lock is an auxiliary mutex-like structure which guarantees that some
 * action will not be executed more times than necessary. In this application,
 * it is used by {@link GraphManager GraphManager} to execute graph layout 
 * only once after the set of related procedures.
 * @author Dagmar Prokopova
 */
public class Lock {
    
    //boolean flag indicating whether mutex is locked
    private static boolean locked;
    
    //integer counter which counts how many times the locked mutex was visited
    private static int tried;
    
    //singleton instance of lock
    private static Lock instance;
    
    /**
     * Private constructor which initializes lock flag and access counter.
     */
    private Lock() {
        locked = false;
        tried = 0;
    }
    
    /**
     * Method for accessing the singleton instance.
     * @return instance of Lock class
     */
    public static Lock getInstance() {
        if(instance == null) {
            instance = new Lock();
        }
        return instance;
    }

    /**
     * Method which tries to access 'critical section' of mutex.
     * @param mintries minimal number of locked access tries required
     * @param r1 runnable which is always executed regardless of the mutex state
     * @param r2 runnable which is executed only if the mutex was not locked when
     * accessed and if condition of minimum tires is satisfied
     * @return true if mutex was not locked, false otherwise
     */
    public boolean set(int mintries, Runnable r1, Runnable r2) {
        if (locked) {
            tried++;
            r1.run();
            return false;
        }
        else {
            lock();
            r1.run();            
            if (tried >= mintries) {
                r2.run();
            }
            unlock();
            return true;
        }
    }
    
    /**
     * Sets locked flag to true.
     */
    private void lock() {
        locked = true;
    }
    
    /**
     * Sets locked flag to false and resets the access counter.
     */
    private void unlock() {
        locked = false;
        tried = 0;
    }
}
