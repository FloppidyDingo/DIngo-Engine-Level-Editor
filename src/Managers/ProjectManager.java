/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Managers;

import dingoengineleveleditor.Editor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import objects.Item;
import objects.Project;
import objects.TileSheet;

/**
 *
 * @author James
 */
public class ProjectManager {
    private final Editor editor;

    public ProjectManager(Editor editor) {
        this.editor = editor;
    }
    
    public void loadProject(Project project, String file){
        try{
            File input = new File(file);
            BufferedReader reader = new BufferedReader(new FileReader(input));
            
            //perform version check
            String version = reader.readLine();
            if(!editor.getVersion().equals(version)){
                System.out.println("version check failed");
                return;
            }
            
            //read project details
            project.setName(reader.readLine());
            project.setPath(input.getParent()+ "\\");
            
            //read header
            String strIn = reader.readLine();
            String header = "";
            while(!"?tile_dep".equals(strIn)){
                header += strIn;
                header += "\n";
                strIn = reader.readLine();
            }
            header = header.substring(0, header.length() - 1);
            project.setHeader(header);
            
            //read tile dependencies
            strIn = reader.readLine();
            while(!"?data".equals(strIn)){
                editor.importTile(project.getPath() + strIn, strIn);
                strIn = reader.readLine();
            }
            
            //read object data
            strIn = reader.readLine();
            while(strIn != null){
                switch(strIn){
                    case "?entity":{
                        //generate entity item
                        Item entity = new Item();
                        entity.setType(Item.ITEM_ENTITY);
                        
                        entity.setID(reader.readLine());
                        entity.setX(Float.parseFloat(reader.readLine()));
                        entity.setY(Float.parseFloat(reader.readLine()));
                        String skinID = reader.readLine();
                        for (TileSheet tile : editor.getTiles()) {
                            if(tile.getName().equals(skinID)){
                                entity.setTile(tile);
                            }
                        }
                        entity.setFrame(Integer.parseInt(reader.readLine()));
                        entity.setSolid(Boolean.parseBoolean(reader.readLine()));
                        entity.setMass(Float.parseFloat(reader.readLine()));
                        entity.setVisible(Boolean.parseBoolean(reader.readLine()));
                        entity.setOpacity(Float.parseFloat(reader.readLine()));
                        entity.setCollisionLayer(Integer.parseInt(reader.readLine()));
                        entity.setInvertX(Boolean.parseBoolean(reader.readLine()));
                        entity.setInvertY(Boolean.parseBoolean(reader.readLine()));
                        entity.setLayer(Integer.parseInt(reader.readLine()));
                        
                        //read custom tag
                        String tagInput = reader.readLine();
                        String tag = "";
                        while(!"?end_tag".equals(tagInput)){
                            tag += tagInput;
                            tagInput = reader.readLine();
                        }
                        
                        //create XML tags
                        editor.getCodeManager().genTag(entity, tag);
                        
                        //add entity to scene
                        editor.getItems().add(entity);
                        break;
                    }
                    case "?light":{
                        Item light = new Item();
                        light.setType(Item.ITEM_LIGHT);
                        
                        light.setID(reader.readLine());
                        light.setX(Float.parseFloat(reader.readLine()));
                        light.setY(Float.parseFloat(reader.readLine()));
                        light.setRed(Integer.parseInt(reader.readLine()));
                        light.setBlue(Integer.parseInt(reader.readLine()));
                        light.setGreen(Integer.parseInt(reader.readLine()));
                        light.setBrightness(Float.parseFloat(reader.readLine()));
                        light.setRadius(Float.parseFloat(reader.readLine()));
                        light.setAmbient(Boolean.parseBoolean(reader.readLine()));
                        
                        //read custom tag
                        String tagInput = reader.readLine();
                        String tag = "";
                        while(!"?end_tag".equals(tagInput)){
                            tag += tagInput;
                            tagInput = reader.readLine();
                        }
                        
                        //create XML tags
                        editor.getCodeManager().genTag(light, tag);
                        
                        //add light to scene
                        editor.getItems().add(light);
                        break;
                    }
                    case "?trigger":{
                        Item trigger = new Item();
                        trigger.setType(Item.ITEM_TRIGGER);
                        
                        trigger.setID(reader.readLine());
                        trigger.setX(Float.parseFloat(reader.readLine()));
                        trigger.setY(Float.parseFloat(reader.readLine()));
                        trigger.setWidth(Float.parseFloat(reader.readLine()));
                        trigger.setHeight(Float.parseFloat(reader.readLine()));
                        
                        //read custom tag
                        String tagInput = reader.readLine();
                        String tag = "";
                        while(!"?end_tag".equals(tagInput)){
                            tag += tagInput;
                            tagInput = reader.readLine();
                        }
                        
                        //create XML tags
                        editor.getCodeManager().genTag(trigger, tag);
                        
                        //add light to scene
                        editor.getItems().add(trigger);
                        break;
                    }
                }
                strIn = reader.readLine();
            }
        }catch(IOException ex){
            Logger.getLogger(ProjectManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void saveProject(ArrayList<Item> nodeList, ArrayList<TileSheet> tiles, Project project){
        File mapFile = new File(project.getPath() + project.getName() + ".d4m");
        try {
            if (!mapFile.exists()) {
                mapFile.createNewFile();
            }
            
            BufferedWriter writer = new BufferedWriter(new FileWriter(mapFile));
            
            //write project details (editor version, name, header)
            writer.write(editor.getVersion());
            writer.newLine();
            writer.write(project.getName());
            writer.newLine();
            writer.write(project.getHeader());
            writer.newLine();
            
            //write tile dependencies
            writer.write("?tile_dep");
            writer.newLine();
            for (TileSheet tile : tiles) {
                writer.write(tile.getName());
                writer.newLine();
            }
            
            //dump object data
            writer.write("?data");
            writer.newLine();
            for (Item node : nodeList) {
                switch (node.getType()) {
                    case Item.ITEM_TRIGGER:
                        //trigger
                        //write trigger data
                        writer.write("?trigger");
                        writer.newLine();
                        writer.write(node.getID());
                        writer.newLine();
                        writer.write(Float.toString(node.getX()));
                        writer.newLine();
                        writer.write(Float.toString(node.getY()));
                        writer.newLine();
                        writer.write(Float.toString(node.getWidth()));
                        writer.newLine();
                        writer.write(Float.toString(node.getHeight()));
                        writer.newLine();
                        writer.write(node.getCodeTag().getCustomTag());
                        writer.newLine();
                        writer.write("?end_tag");
                        writer.newLine();
                        break;
                    case Item.ITEM_LIGHT:
                        //light
                        writer.write("?light");
                        writer.newLine();
                        writer.write(node.getID());
                        writer.newLine();
                        writer.write(Float.toString(node.getX()));
                        writer.newLine();
                        writer.write(Float.toString(node.getY()));
                        writer.newLine();
                        writer.write(Integer.toString(node.getRed()));
                        writer.newLine();
                        writer.write(Integer.toString(node.getBlue()));
                        writer.newLine();
                        writer.write(Integer.toString(node.getGreen()));
                        writer.newLine();
                        writer.write(Float.toString(node.getBrightness()));
                        writer.newLine();
                        writer.write(Float.toString(node.getRadius()));
                        writer.newLine();
                        writer.write(Boolean.toString(node.isAmbient()));
                        writer.newLine();
                        writer.write(node.getCodeTag().getCustomTag());
                        writer.newLine();
                        writer.write("?end_tag");
                        writer.newLine();
                        break;
                    case Item.ITEM_ENTITY:
                        //entity
                        writer.write("?entity");
                        writer.newLine();
                        writer.write(node.getID());
                        writer.newLine();
                        writer.write(Float.toString(node.getX()));
                        writer.newLine();
                        writer.write(Float.toString(node.getY()));
                        writer.newLine();
                        writer.write(node.getTile().getName());
                        writer.newLine();
                        writer.write(Integer.toString(node.getFrame()));
                        writer.newLine();
                        writer.write(Boolean.toString(node.isSolid()));
                        writer.newLine();
                        writer.write(Float.toString(node.getMass()));
                        writer.newLine();
                        writer.write(Boolean.toString(node.isVisible()));
                        writer.newLine();
                        writer.write(Float.toString(node.getOpacity()));
                        writer.newLine();
                        writer.write(Integer.toString(node.getCollisionLayer()));
                        writer.newLine();
                        writer.write(Boolean.toString(node.isInvertX()));
                        writer.newLine();
                        writer.write(Boolean.toString(node.isInvertY()));
                        writer.newLine();
                        writer.write(Integer.toString(node.getLayer()));
                        writer.newLine();
                        writer.write(node.getCodeTag().getCustomTag());
                        writer.newLine();
                        writer.write("?end_tag");
                        writer.newLine();
                        break;
                    default:
                        break;
                }
            }
            
            writer.flush();
            writer.close();
            
        } catch (IOException ex) {
            Logger.getLogger(ProjectManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
