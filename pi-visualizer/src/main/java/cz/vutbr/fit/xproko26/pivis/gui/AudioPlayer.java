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
package cz.vutbr.fit.xproko26.pivis.gui;

import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * AudioPlayer is a singleton class which maintains audio output of the application 
 * by providing methods to load and play specific sounds.
 * @author Dagmar Prokopova
 */
public class AudioPlayer {
    
    //singleton instance of AudioPlayer class
    private static AudioPlayer instance;
    
    //flag which specifies if audio output is enabled
    boolean soundOn;
    
    //Private constructor which initializes sound flag to true, which means
    //that sound output is defaultly enabled.
    private AudioPlayer() {
        soundOn = true;
    }
    
    /**
     * Method for accessing the singleton instance.
     * @return instance of AudioPlayer class
     */
    public static AudioPlayer getInstance() {
        if(instance == null) {
            instance = new AudioPlayer();
        }
        return instance;
    }

    /**
     * Enables or disables audio output.
     * @param option true for enabled output, false otherwise
     */
    public void setSoundOn(boolean option) {
        soundOn = option;
    }
    
    /**
     * Plays short sound similar to coin clink.
     */
    public void coin() {
        play("/sounds/coin.wav");
    }
    
    /**
     * Plays short sound similar to when an object is moved.
     */
    public void woosh() {
        play("/sounds/woosh.wav");
    }
    
    /**
     * Plays very short beep sound.
     */
    public void shortbeep() {
        play("/sounds/shortbeep.wav");
    }
    
    /**
     * Check whether audio output is enabled and plays the specified sound.
     * @param path path to the selected audio file
     */
    private void play(String path) {
        if (!soundOn)
            return;
        
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(this.getClass().getResource(path));
            clip.open(inputStream);
            clip.start();
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            //just pritnt message to error output and ignore
            System.err.println(e.getMessage());
        }
    }
    

    
    
    

}
