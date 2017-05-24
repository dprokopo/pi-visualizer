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
package cz.vutbr.fit.xproko26.pivis.gui.help;

/**
 * HelpElement is a class which stores pair - help chapter title and help
 * chapter content. It is used by {@link HelpListModel HelpListModel} class.
 * @author Dagmar Prokopova
 */
public class HelpElement {
    
    //title of the help chapter
    private final String title;
    
    //content of the help chapter
    private final String content;
    
    /**
     * Initializes title and content for the specific help chapter.
     * @param t help chapter title
     * @param c help chapteer content
     */
    public HelpElement(String t, String c) {
        title = t;
        content = c;
    }
    
    /**
     * Returns help chapter title.
     * @return help chapter title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Returns help chapter content.
     * @return help chapter content
     */
    public String getContent() {
        return content;
    }
}
