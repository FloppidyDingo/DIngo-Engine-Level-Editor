/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dingoengineleveleditor;

import Engine.Settings;

/**
 *
 * @author James
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Settings s = new Settings();
        //s.setDebugMode(true);
        s.setFrameRate(30);
        s.setInternalResX(800);
        s.setInternalResY(600);
        //s.setProfile(true);
        
        Editor editor = new Editor();
        editor.start(s, args);
    }
    
}
