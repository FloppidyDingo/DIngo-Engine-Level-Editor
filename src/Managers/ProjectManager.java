/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Managers;

import Engine.Scene;
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
import objects.Entity;
import objects.Light;
import objects.Node;
import objects.Project;
import objects.Tile;
import objects.Trigger;

/**
 *
 * @author James
 */
public class ProjectManager {
    private final Editor editor;
    
    public ProjectManager(Editor editor){
        this.editor = editor;
    }
    
    public void loadProject(Scene scene, Project project, String file){
        try{
            File input = new File(file);
            BufferedReader reader = new BufferedReader(new FileReader(input));
            
            //perform version check
            String version = reader.readLine();
            if(!editor.version.equals(version)){
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
                strIn = reader.readLine();
            }
            project.setHeader(header);
            
            //read tile dependencies
            strIn = reader.readLine();
            while(!"?data".equals(strIn)){
                editor.getTools().importTile(project.getPath() + strIn, strIn);
                strIn = reader.readLine();
            }
            
            //read object data
            strIn = reader.readLine();
            while(strIn != null){
                switch(strIn){
                    case "?entity":{
                        Entity entity = new Entity();
                        Entity dataEntity = new Entity();
                        
                        entity.setID(reader.readLine());
                        entity.setX(Float.parseFloat(reader.readLine()));
                        entity.setY(Float.parseFloat(reader.readLine()));
                        String skinID = reader.readLine();
                        for (Tile tile : editor.getTiles()) {
                            if(tile.id.equals(skinID)){
                                entity.setSkin(tile.skin);
                            }
                        }
                        entity.useSkin(Integer.parseInt(reader.readLine()));
                        dataEntity.setSolid(Boolean.parseBoolean(reader.readLine()));
                        dataEntity.setMass(Float.parseFloat(reader.readLine()));
                        dataEntity.setVisible(Boolean.parseBoolean(reader.readLine()));
                        dataEntity.setOpacity(Float.parseFloat(reader.readLine()));
                        dataEntity.setCollisionLayer(Integer.parseInt(reader.readLine()));
                        dataEntity.setInvertX(Boolean.parseBoolean(reader.readLine()));
                        dataEntity.setInvertY(Boolean.parseBoolean(reader.readLine()));
                        
                        entity.setUserData(dataEntity);
                        
                        //create XML tags
                        editor.getCodeManager().genTag(entity);
                        
                        //read custom tag
                        String tagInput = reader.readLine();
                        String tag = "";
                        while(!"?end_tag".equals(tagInput)){
                            tag += tagInput;
                            tagInput = reader.readLine();
                        }
                        editor.getCodeManager().setCustomTag(entity, tag);
                        
                        //add entity to scene
                        scene.attachGUIChild(entity);
                        editor.getNodeList().add(entity);
                        break;
                    }
                    case "?light":{
                        Entity node = new Entity(editor.getIconSkin(), null);
                        node.useSkin(0);
                        
                        Light light = new Light();
                        
                        light.setId(reader.readLine());
                        node.setX(Float.parseFloat(reader.readLine()));
                        node.setY(Float.parseFloat(reader.readLine()));
                        light.setRed(Integer.parseInt(reader.readLine()));
                        light.setBlue(Integer.parseInt(reader.readLine()));
                        light.setGreen(Integer.parseInt(reader.readLine()));
                        light.setBrightness(Float.parseFloat(reader.readLine()));
                        light.setRadius(Float.parseFloat(reader.readLine()));
                        light.setAmbient(Boolean.parseBoolean(reader.readLine()));
                        
                        node.setUserData(light);
                        
                        //create XML tags
                        editor.getCodeManager().genTag(node);
                        
                        //read custom tag
                        String tagInput = reader.readLine();
                        String tag = "";
                        while(!"?end_tag".equals(tagInput)){
                            tag += tagInput;
                            tagInput = reader.readLine();
                        }
                        editor.getCodeManager().setCustomTag(node, tag);
                        
                        //add light to scene
                        scene.attachGUIChild(node);
                        editor.getNodeList().add(node);
                        break;
                    }
                    case "?trigger":{
                        Entity node = new Entity(editor.getIconSkin(), null);
                        node.useSkin(1);
                        
                        Trigger trigger = new Trigger(0, 0, 0, 0);
                        
                        trigger.setID(reader.readLine());
                        node.setX(Float.parseFloat(reader.readLine()));
                        node.setY(Float.parseFloat(reader.readLine()));
                        trigger.setWidth(Integer.parseInt(reader.readLine()));
                        trigger.setHeight(Integer.parseInt(reader.readLine()));
                        
                        node.setUserData(trigger);
                        
                        //create XML tags
                        editor.getCodeManager().genTag(node);
                        
                        //read custom tag
                        String tagInput = reader.readLine();
                        String tag = "";
                        while(!"?end_tag".equals(tagInput)){
                            tag += tagInput;
                            tagInput = reader.readLine();
                        }
                        editor.getCodeManager().setCustomTag(node, tag);
                        
                        //add light to scene
                        scene.attachGUIChild(node);
                        editor.getNodeList().add(node);
                        break;
                    }
                }
                strIn = reader.readLine();
            }
        }catch(IOException ex){
            Logger.getLogger(ProjectManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void saveProject(ArrayList<Node> nodeList, ArrayList<Tile> tiles, Project project){
        File mapFile = new File(project.getPath() + project.getName() + ".d4m");
        try {
            if (!mapFile.exists()) {
                mapFile.createNewFile();
            }
            
            BufferedWriter writer = new BufferedWriter(new FileWriter(mapFile));
            
            //write project details (editor version, name, header)
            writer.write(editor.version);
            writer.newLine();
            writer.write(project.getName());
            writer.newLine();
            writer.write(project.getHeader());
            writer.newLine();
            
            //write tile dependencies
            writer.write("?tile_dep");
            writer.newLine();
            for (Tile tile : tiles) {
                writer.write(tile.id);
                writer.newLine();
            }
            
            //dump object data
            writer.write("?data");
            writer.newLine();
            for (Node node : nodeList) {
                //use data node to determine object type
                Object nodeData = node.getUserData();
                
                if(nodeData instanceof Trigger){ //trigger
                    //write trigger data
                    Trigger trigger = (Trigger)nodeData;
                    
                    writer.write("?trigger");
                    writer.newLine();
                    writer.write(trigger.getID());
                    writer.newLine();
                    writer.write(Float.toString(node.getX()));
                    writer.newLine();
                    writer.write(Float.toString(node.getY()));
                    writer.newLine();
                    writer.write(Integer.toString(trigger.getWidth()));
                    writer.newLine();
                    writer.write(Integer.toString(trigger.getHeight()));
                    writer.newLine();
                    writer.write(editor.getCodeManager().getCustomTag(node));
                    writer.newLine();
                    writer.write("?end_tag");
                    writer.newLine();
                }else if(nodeData instanceof Light){ //light
                    Light light = (Light)nodeData;
                    
                    writer.write("?light");
                    writer.newLine();
                    writer.write(light.getId());
                    writer.newLine();
                    writer.write(Float.toString(node.getX()));
                    writer.newLine();
                    writer.write(Float.toString(node.getY()));
                    writer.newLine();
                    writer.write(Integer.toString(light.getRed()));
                    writer.newLine();
                    writer.write(Integer.toString(light.getBlue()));
                    writer.newLine();
                    writer.write(Integer.toString(light.getGreen()));
                    writer.newLine();
                    writer.write(Float.toString(light.getBrightness()));
                    writer.newLine();
                    writer.write(Float.toString(light.getRadius()));
                    writer.newLine();
                    writer.write(Boolean.toString(light.isAmbient()));
                    writer.newLine();
                    writer.write(editor.getCodeManager().getCustomTag(node));
                    writer.newLine();
                    writer.write("?end_tag");
                    writer.newLine();
                }else{ //entity
                    Entity entity = (Entity)nodeData;
                    
                    writer.write("?entity");
                    writer.newLine();
                    writer.write(node.getID());
                    writer.newLine();
                    writer.write(Float.toString(node.getX()));
                    writer.newLine();
                    writer.write(Float.toString(node.getY()));
                    writer.newLine();
                    writer.write(((Entity)node).getSkin().getID());
                    writer.newLine();
                    writer.write(Integer.toString(((Entity)node).getFrame()));
                    writer.newLine();
                    writer.write(Boolean.toString(entity.isSolid()));
                    writer.newLine();
                    writer.write(Float.toString(((Entity)node).getMass()));
                    writer.newLine();
                    writer.write(Boolean.toString(entity.isVisible()));
                    writer.newLine();
                    writer.write(Float.toString(((Entity)node).getOpacity()));
                    writer.newLine();
                    writer.write(Integer.toString(entity.getCollisionLayer()));
                    writer.newLine();
                    writer.write(Boolean.toString(entity.isInvertX()));
                    writer.newLine();
                    writer.write(Boolean.toString(entity.isInvertY()));
                    writer.newLine();
                    writer.write(editor.getCodeManager().getCustomTag(node));
                    writer.newLine();
                    writer.write("?end_tag");
                    writer.newLine();
                }
            }
            
            writer.flush();
            writer.close();
            
        } catch (IOException ex) {
            Logger.getLogger(ProjectManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
