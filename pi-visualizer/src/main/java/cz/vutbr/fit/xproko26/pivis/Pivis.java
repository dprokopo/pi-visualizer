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
package cz.vutbr.fit.xproko26.pivis;

import javax.swing.SwingUtilities;

/**
 * Pivis class containing the main method as an entry point of the application
 * @author Dagmar Prokopova
 */
public class Pivis {

    /**
     * Entry point of the application
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //start application logic
        SwingUtilities.invokeLater(() -> {
            Controller.getInstance().run(); 
        });                               
    }

}
