/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Managers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import objects.CodeTag;
import objects.Item;

/**
 *
 * @author James
 */
public class CodeManager {
    
    public String genTag(Item item, String customTag){
        if(item == null){
            return "";
        }
        CodeTag tag = new CodeTag();
        tag.setCustomTag(customTag);
        
        switch (item.getType()) {
            case Item.ITEM_TRIGGER:
            { //trigger
                String code = "<trigger id=\"";
                code += item.getID();
                code += "\" width=\"";
                code += item.getWidth();
                code += "\" height=\"";
                code += item.getHeight();
                code += "\"";
                code += tag.getCustomTag();
                code += ">";
                
                tag.setTag(code);
                
                item.setTag(tag);
                return code;
            }
            case Item.ITEM_LIGHT:
            { //light
                String code = "<light id=\"";
                code += item.getID();
                code += "\" ambient=\"";
                code += item.isAmbient();
                code += "\" radius=\"";
                code += item.getRadius();
                code += "\" brightness=\"";
                code += item.getBrightness();
                code += "\" red=\"";
                code += item.getRed();
                code += "\" green=\"";
                code += item.getGreen();
                code += "\" blue=\"";
                code += item.getBlue();
                code += "\"";
                code += tag.getCustomTag();
                code += " >";
                
                tag.setTag(code);
                
                item.setTag(tag);
                return code;
            }
            case Item.ITEM_ENTITY:
            { //entity
                String code = "<tile id=\"";
                code += item.getID();
                code += "\" frame=\"";
                code += item.getFrame();
                code += "\" skin=\"";
                code += item.getTile().getName();
                code += "\" solid=\"";
                code += item.isSolid();
                code += "\" mass=\"";
                code += item.getMass();
                code += "\" visible=\"";
                code += item.isVisible();
                code += "\" opacity=\"";
                code += item.getOpacity();
                code += "\" collision=\"";
                code += item.getCollisionLayer();
                code += "\" invertx=\"";
                code += item.isInvertX();
                code += "\" inverty=\"";
                code += item.isInvertY();
                code += "\" layer=\"";
                code += item.getLayer();
                code += "\"";
                code += tag.getCustomTag();
                code += " >";
                
                tag.setTag(code);
                
                item.setTag(tag);
                return code;
            }
        }
        return "you shouldn't be able to see this";
    }
    
    public void compile(ArrayList<Item> nodeList, String filePath, String header){
        int maxLayer = 0;
        ArrayList<Item> completed = new ArrayList<>();
        
        //create file writer
        try {
            File xmlFile = new File(filePath);
            if(!xmlFile.exists()){
                xmlFile.createNewFile();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(xmlFile));
            
            //write header
            writer.write(header);
            writer.newLine();
            
            //scan list to find maximum number of layers
            for(Item node : nodeList){
                if(node.getType() == Item.ITEM_ENTITY && node.getLayer() > maxLayer){
                    maxLayer = node.getLayer();
                }
            }
            
            //run through each layer
            for(int layer = 0; layer <= maxLayer; layer++){
                //run through each item and compile
                writer.write("<layer layer=\"" + layer + "\">");
                writer.newLine();
                boolean compiled;
                for (Item node : nodeList) {
                    //check if node was compiled already
                    compiled = false;
                    for (Item check : completed) {
                        if(check == node){
                            compiled = true;
                        }
                    }

                    if(!compiled && ((node.getType() == Item.ITEM_ENTITY && layer == node.getLayer()) || (node.getType() != Item.ITEM_ENTITY && layer == 0))){
                        String tag = node.getCodeTag().getTag();
                        writer.newLine();
                        writer.write(tag);
                        writer.newLine();
                        for (Item compileNode : nodeList) {
                            if(compileNode.getCodeTag().getTag().equals(tag)){
                                writer.write(compileNode.getX() + "," + -compileNode.getY() + ";");
                                completed.add(compileNode);
                            }
                        }
                        if(tag.contains("<tile")){
                            writer.newLine();
                            writer.write("</tile>");
                            writer.newLine();
                        }else if(tag.contains("<trigger")){
                            writer.newLine();
                            writer.write("</trigger>");
                            writer.newLine();
                        }else if(tag.contains("<light")){
                            writer.newLine();
                            writer.write("</light>");
                            writer.newLine();
                        }
                    }
                }
                writer.write("</layer>");
                writer.newLine();
            }
            
            //close file
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(CodeManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
