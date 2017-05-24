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

import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * TextResource is a singleton resource class which stores important text strings
 * used in the application, for example help content.
 * @author Dagmar Prokopova
 */
public class TextResource extends ResourceBundle {

    //singleton instance of TextResource
    private static TextResource instance;
    
    /**
     * Method for accessing the singleton instance.
     * @return instance of TextResource class
     */
    public static TextResource getInstance() {
        if(instance == null) {
            instance = new TextResource();
        }
        return instance;
    }
    
    @Override
    protected Object handleGetObject(String key) {
                
        switch(key) {
            case "consolehelp":
                return  "Use one of the following commands to execute the specified action. \n"
                        + "More details on how to use this application can be found in help menu.\n"
                        + "----------------------------------------------------------------\n"
                        + "command\t\t\t action\n"
                        + "----------------------------------------------------------------\n"
                        + "agent <definition>\t define process\n"
                        + "show <expression>\t visualize process expression\n"
                        + "clear\t\t\t clear console\n"
                        + "redlist \t\t show reduction list\n"
                        + "reduce\t\t\t execute reduction step\n"
                        + "simplify\t\t simplify visualized expression\n"
                        + "env <process>\t\t print process definition\n"
                        + "env\t\t\t print all process definitions\n"
                        + "reset\t\t\t reset application context\n"
                        + "quit|exit\t\t exit application\n"
                        + "help\t\t\t show this text\n";
                
            case "intro-title":
                return "Introduction";
            case "intro-html":             
                return "<html>"
                    + "<h1>Introduction</h1>"
                    + "<p>The aim of this application is to aid understanding and analyzing the pi-calculus expressions by providing a possibility to view and interact with its graphical representation. It is, however, recommended that the users of this applications have at least basic theoretical knowledge of pi-calculus.</p>"
                    + "<br>"                       
                    + "<p>The core functionallity of the application is described in individual chapters of this help with reference to the following components provided by the application interface:</p>"
                    + "<ul>"
                    + "<li><b>MenuBar</b> comprised of 4 main menus - File, Control, Settings, Help</li>"
                    + "<li><b>ToolBar</b> for faster access to key menu items</li>"
                    + "<li><b>Console</b> for text input and control (first tab)</li>"
                    + "<li><b>Environment panel/editor</b> (second tab)</li>"
                    + "<li><b>Graph canvas</b> showing graphical representation of the processed expression (at the center)</li>"
                    + "<li><b>Text line</b> showing textual representation of the processed expression (at the bottom)</li>"
                    + "<li><b>Reduction panel/list</b> (initially hidden)</li>"
                    + "</ul>"
                    + "</html>";
                
            case "procspec-title":
                return "Process specification";
            case "procspec-html":                
                return "<html>"
                    + "<h1>Process specification</h1>"
                    + "<p>Any process can be defined via the console as </p>"
                    + "<br>"    
                    + "<p>&nbsp <b>agent</b> <FONT COLOR=RED>Id</FONT>(<FONT COLOR=BLUE>free names</FONT>) = <FONT COLOR=GREEN>expression</FONT></p>"
                    + "<br>"
                    + "<p>Every process <FONT COLOR=RED>identifier</FONT> starts with an uppercase letter, while every <FONT COLOR=BLUE>name</FONT> starts with a lowercase letter. Multiple names are separated by comma. The following grammar describes the syntax of an <FONT COLOR=GREEN>expression</FONT>:</p>"
                    + "<br>"
                    + "<table>"
                        
                    + "<tr>"
                    + "<td>&nbsp 0</td>"
                    + "<td>&nbsp Nil expression</td>"
                    + "</tr>"
                        
                    + "<tr>"
                    + "<td>&nbsp <FONT COLOR=RED>Id</FONT> &lt<FONT COLOR=BLUE>names</FONT>&gt</td>"
                    + "<td>&nbsp Process concretization</td>"
                    + "</tr>"
                        
                    + "<tr>"
                    + "<td>&nbsp t.<FONT COLOR=GREEN>expression</FONT></td>"
                    + "<td>&nbsp Tau prefix</td>"
                    + "</tr>"
                        
                    + "<tr>"
                    + "<td>&nbsp <FONT COLOR=BLUE>name</FONT> (<FONT COLOR=BLUE>names</FONT>).<FONT COLOR=GREEN>expression</FONT></td>"
                    + "<td>&nbsp Input prefix</td>"
                    + "</tr>"
                        
                    + "<tr>"
                    + "<td>&nbsp '<FONT COLOR=BLUE>name</FONT> &lt<FONT COLOR=BLUE>names</FONT>&gt<FONT>.</FONT><FONT COLOR=GREEN>expression</FONT></td>"
                    + "<td>&nbsp Output prefix</td>"
                    + "</tr>"
                        
                    + "<tr>"
                    + "<td>&nbsp [<FONT COLOR=BLUE>name</FONT> = <FONT COLOR=BLUE>name</FONT>]<FONT COLOR=GREEN>expression</FONT></td>"
                    + "<td>&nbsp Match prefix</td>"
                    + "</tr>"
                        
                    + "<tr>"
                    + "<td>&nbsp <FONT COLOR=GREEN>expression</FONT> + <FONT COLOR=GREEN>expression</FONT></td>"
                    + "<td>&nbsp Summation</td>"
                    + "</tr>"
                        
                    + "<tr>"
                    + "<td>&nbsp <FONT COLOR=GREEN>expression</FONT> | <FONT COLOR=GREEN>expression</FONT></td>"
                    + "<td>&nbsp Parallel composition</td>"
                    + "</tr>"
                        
                    + "<tr>"
                    + "<td>&nbsp !<FONT COLOR=GREEN>expression</FONT></td>"
                    + "<td>&nbsp Replication</td>"
                    + "</tr>"
                        
                    + "<tr>"
                    + "<td>&nbsp (^<FONT COLOR=BLUE>names</FONT>)<FONT COLOR=GREEN>expression</FONT></td>"
                    + "<td>&nbsp Restriction of names</td>"
                    + "</tr>"
                        
                    + "</table>"
                    + "<br>"
                    + "<p>Parentheses can be used in expressions in order to resolve ambiguity or change the order of evaluation.</p>"
                    + "<br>"
                    + "<p>Once the process is specified, its definition can be printed out to the console using the command <b>env <FONT COLOR=RED>Id</FONT></b>. In case that the key word <b>env</b> is used without process identifier, all process definitions are printed.</p>"
                    + "<br>"
                    + "<p>Another way to add, modify or remove process definitions is to use the <b>Environment editor</b>. The syntax of process definition used in the editor is the same as in console. Remember that all changes made in the Environment editor must be commited in order to apply.</p>"
                    + "<br>"    
                    + "<p>Process definitions can be also imported from a text file by selecting a <b>Import environment</b> item from File menu or using the appropriate button in the Environment panel.</p>"
                    + "<br>"
                    + "<p>IMPORTANT: In case that the environment (process definitions) is modified while there is a visualized expression, the visualized expression might still contain some of the old process specifications. The expression needs to be revisualized after the change in order to keep it up to date.</p>";
            case "procvis-title":
                return "Process visualization";
            case "procvis-html":                
                return "<html>"
                    + "<h1>Process visualization</h1>"
                    + "<p>Any process expression can be visualized via the console as </p>"
                    + "<br>"    
                    + "<p>&nbsp <b>show</b> <FONT COLOR=GREEN>expression</FONT></p>"
                    + "<br>"
                    + "<p>Syntax of expression is described in chapter 'Process specification'.</p>"
                    + "<br>"
                    + "<p>Another way to visualize expression is via the dialog window which can be opened from Control menu by selecting item <b>Visualize expression</b> or using keyboard shortcut <b>F4</b>.</p>"
                    + "<br>"    
                    + "<p>In case that the specified expression contains concretization of process which was not defined in advance, warning will be shown in the console. However, it is possible to define the process later.</p>"
                    + "<br>"    
                    + "<p>The expression is visualized in a graphical form on the graph canvas as well as textual form on the bottom text line.</p>"
                    + "</html>";
                
            case "layout-title":
                return "Graph manipulation";
            case "layout-html":                
                return "<html>"
                    + "<h1>Graph manipulation</h1>"
                    + "<p>Once the expression is visualized, it is possible to style its graphical representation and interact with it.<p>"
                    + "<br>"
                    + "<p>The simplest exmple of interaction is selection of graph node with left-mouse-button click. Upon selection the node and all its edges are highlighted blue and so is the corresponding subexpression on the bottom text line.</p>"
                    + "<br>"    
                    + "<p>Each process concretization is initially visualized as a single graph node, however it can be expanded using the + button and collapsed using the - button in its upper left corner. The style the nodes are organized after the expansion can be changed either from Settings menu (item <b>Graph style</b>) or by using the keybort shortcut <b>Ctrl-H</b> for hierarchical style or <b>Ctrl-L</b> for linear style.</p>"
                    + "<br>"
                    + "<p>Another way how one can interact with the graph is to create new replication branches. This can be achieved by right-clicking the replication node (!) and selecting <b>replicate</b> from the context menu.</p>"
                    + "<br>"
                    + "<p>By using right-mouse-button click on a reducible prefix node (tau, input, output) the opened context menu provides an option to <b>select/deselect node for reduction</b>. Nodes selected for reduction are filled with red color. If single input/output node is selected, complementary nodes are filled with pink color to indicate suggestion for the subsequent selection.</p>"
                    + "</html>";
                
            case "reduction-title":
                return "Reduction";
            case "reduction-html":                
                return "<html>"
                    + "<h1>Reduction</h1>"
                    + "<p>In order to perform reduction on the visualized expression, one must first select appropriate nodes for reduction. This can be done either manually by selecting nodes in the graph as described in chapter 'Graph manipulation' or by selecting reduction from the Reduction list. Reduction list can be made visible or hidden from the Settings menu (<b>Reduction list</b> item).<p>"
                    + "<br>"
                    + "<p>The reduction itself can be then executed using the <b>reduce</b> command in the console or by selecting <b>Reduce expression</b> item from Control menu or by using keyboard shortcut <b>F5</b>.</p>"
                    + "<br>"
                    + "<p>The selected nodes are removed during the reduction process and respective names are substitued. As a result some of them might gain an unique <i>#NUM</i> suffix.</p>"
                    + "</html>";
                
            case "simplification-title":
                return "Simplification";
            case "simplification-html":                
                return "<html>"
                    + "<h1>Simplification</h1>"
                    + "<p>After performing several reductions or creating multiple replication branches, the visualized expression might contains duplicities or unnecessary nodes. For this case it might be useful to simplify the expression by writing either <b>simplify</b> command into the console or by selecting <b>Simplify expression</b> from Control menu or by using keyboard shortcut <b>F6</b>.<p>"
                    + "<br>"
                    + "<p>The simplification has following effects: </p>"
                    + "<ul>"
                    + "<li>The non-active nodes followed by Nil expressions are removed</li>"
                    + "<li>The Nil branches of summations and parallel compositions are removed</li>"
                    + "<li>Every match prefix which evaluates as TRUE is removed</li>"
                    + "<li>All replication duplicates are removed</li>"
                    + "<li>The adjacent nodes of the same kind are merged (i.e. one summation followed by another)</li>"
                    + "</ul>"
                    + "</html>";
                
            case "files-title":
                return "File management";
            case "files-html":                
                return "<html>"
                    + "<h1>File management</h1>"
                    + "<p>There are several options for generating output from the application.<p>"
                    + "<p>The whole data model of the application (including all process definitions, visualized expression and selections) can be saved at once using the <b>Save</b> or <b>Save as</b> item from the File menu or by using corresponding keyboard shortcuts <b>Ctrl-S</b> or <b>Ctrl-Sift-S</b>. This action will generate a PVS file (*.pvs) which can be later used to load the saved state of the application by using <b>Open</b> item in File menu or keyboard shortcut <b>Ctrl-O</b>.</p>"
                    + "<br>"    
                    + "<p>Another option is to export only the environment (i.e. process definitions) by using item <b>Export environment</b> in the File menu. This will generate a human-readable text file containing all process definitions. Such file can be easily modified or even created from scratch in any text editor and used later to import all process definitions back into the application with the usage of <b>Import environment</b> item in the File menu.</p>"
                    + "<br>"
                    + "<p>The last (but not the least useful) way how to generate output from the application is to export the graphical representation of the expression into one of the raster or vector formats offered by the <b>Export graph as</b> submenu in the File menu. The whole graph is always exported regardless of whether it is fully visible on the canvas. The exported image size can be adjusted by zooming in or out with the mouse wheel.<p/>"
                    + "</html>"; 
                
            case "about-html":
                return "<html>"
                    + "<h1>Pi-Visualizer</h1>"
                    + "<p>&nbsp <i>version 1.0</i></p>"
                    + "<br>"
                    + "<p>This software was developed as Master thesis project at Brno University of Technology, Czech Republic by Dagmar Prokopova (xproko26@stud.fit.vutbr.cz).</p>"
                    + "<br>"    
                    + "<p>It is available under Apache 2.0 open source license:</p>"
                    + "<br>"
                    + "</html>";
                
            case "about-link":
                return "https://github.com/dprokopo/pi-visualizer";
            default:
                return null;
        }
    }

    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(keySet());
    }        
    
}
