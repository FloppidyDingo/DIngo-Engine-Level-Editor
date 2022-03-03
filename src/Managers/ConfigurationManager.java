/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author James
 */
public class ConfigurationManager {
    private String lastOpenedLocation;
    
    public void loadConfig(){
        File config = new File("config.ini");
        try {
            if(!config.exists()){
                config.createNewFile();
                lastOpenedLocation = "default";
                saveConfig();
            }else{
                BufferedReader br = new BufferedReader(new FileReader(config));
                lastOpenedLocation = br.readLine();
                br.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(ConfigurationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void saveConfig(){
        File config = new File("config.ini");
        try {
            if(!config.exists()){
                config.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(config));
            bw.write(lastOpenedLocation);
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(ConfigurationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getLastOpenedLocation() {
        return lastOpenedLocation;
    }

    public void setLastOpenedLocation(String lastOpenedLocation) {
        this.lastOpenedLocation = lastOpenedLocation;
    }
    
}
