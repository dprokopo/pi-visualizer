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
package cz.vutbr.fit.xproko26.pivis.filemanager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import cz.vutbr.fit.xproko26.pivis.model.Data;

/**
 * FileManager is a singleton class which provides methods for saving
 * application data into the file and loading data from file.
 * @author Dagmar Prokopova
 */
public class FileManager {
    
    //file manager instance
    private static FileManager instance;
    
    /**
     * Holds the file which has been used for saving/loading lately, so that user
     * can use "save" instead of "save as".
     */
    private static File savefile;
        
    /**
     * Method for accessing the singleton instance.
     * @return instance of FileManager class
     */
    public static FileManager getInstance() {
        if(instance == null) {
            instance = new FileManager();
        }
        return instance;
    }
    
    /**
     * Initializes cached save file to null.
     */
    public void init() {
        savefile = null;
    }

    /**
     * Saves application data into the lately used file
     * @param data application data
     * @throws Exception 
     */
    public void save(Data data) throws Exception {
        save(data, savefile);
    }
        
    /**
     * Saves application data into the specified file
     * @param data application data
     * @param file specified file
     * @throws Exception 
     */
    public void save(Data data, File file) throws Exception {
        
        FileOutputStream fileOut = null;
        ObjectOutputStream objectOut = null;
        try {
            fileOut = new FileOutputStream(file);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(data);
            savefile = file;
        } catch (IOException ex) {
            throw new Exception("Error: Could not save data into the selected file.");
        } finally {
            if (objectOut != null) {
                try {
                    objectOut.flush();
                    objectOut.close();
                } catch (IOException ex) {}
            }
            if (fileOut != null) {
                try {
                    fileOut.flush();
                    fileOut.close();
                } catch (IOException ex) {}
            }            
        }
    }
        
    /**
     * Loads application data from the specified file
     * @param file specified file
     * @return application data
     * @throws Exception 
     */
    public Data load(File file) throws Exception {

        FileInputStream fileIn = null;
        ObjectInputStream objectIn = null;
        try {
            fileIn = new FileInputStream(file);
            objectIn = new ObjectInputStream(fileIn);
            Data data = (Data) objectIn.readObject();
            savefile = file;
            return data;            
        } catch (IOException|ClassNotFoundException ex ) {
            throw new Exception("Error: Could not read data from the selected file.");
        } finally {
            if (objectIn != null) {
                try {
                    objectIn.close();
                } catch (IOException ex) {}
            }
            if (fileIn != null) {
                try {
                    fileIn.close();
                } catch (IOException ex) {}
            }            
        }
    }
        
    /**
     * Returns true if there is a file cached for saving or loading
     * @return true if default file exists
     */
    public boolean isFileCached() {
        return (savefile != null);
    }
    
    /**
     * Loads string from the specified text file
     * @param file specified text file
     * @return loaded string
     * @throws Exception 
     */
    public String getString(File file) throws Exception {
        
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(file.getPath()));
            return new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new Exception("Error: Could not read data from selected file.");
        }
    }
    
    /**
     * Saves string into specified text file
     * @param str string to be saved
     * @param file specified text file
     * @throws Exception 
     */
    public void saveString(String str, File file) throws Exception {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(str);
        } catch (IOException ex) {
            throw new Exception("Error: Could not save into selected file.");
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }
}
