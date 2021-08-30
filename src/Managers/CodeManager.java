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
import objects.Entity;
import objects.Light;
import objects.Node;
import objects.Trigger;

/**
 *
 * @author James
 */
public class CodeManager {
    private String header = "";
    private final ArrayList<CodeTag> tags;

    public CodeManager() {
        tags = new ArrayList<>();
    }
    
    public String genTag(Node node){
        CodeTag tag = new CodeTag();
        Object nodeData = node.getUserData();
        
        tag.setNode(node);
        
        if(nodeData instanceof Trigger){ //trigger
            Trigger trigger = (Trigger)nodeData;
            tag.setType(0);
            
            String code = "<trigger id=\"";
            code += trigger.getID();
            code += "\" width=\"";
            code += trigger.getWidth();
            code += "\" height=\"";
            code += trigger.getHeight();
            code += "\"";
            code += tag.getCustomTag();
            code += " >";
            
            tag.setTag(code);
            
            tags.add(tag);
            
            return code;
        }else if (nodeData instanceof Light){ //light
            Light light = (Light)nodeData;
            tag.setType(1);
            
            String code = "<light id=\"";
            code += light.getId();
            code += "\" ambient=\"";
            code += light.isAmbient();
            code += "\" brightness=\"";
            code += light.getBrightness();
            code += "\" red=\"";
            code += light.getRed();
            code += "\" green=\"";
            code += light.getGreen();
            code += "\" blue=\"";
            code += light.getBlue();
            code += "\"";
            code += tag.getCustomTag();
            code += " >";
            
            tag.setTag(code);
            
            tags.add(tag);
            
            return code;
        } else { //entity
            Entity entity = (Entity)nodeData;
            tag.setType(2);
            
            String code = "<tile id=\"";
            code += node.getID();
            code += "\" frame=\"";
            code += ((Entity)node).getFrame();
            code += "\" skin=\"";
            code += ((Entity)node).getSkin().getID();
            code += "\" solid=\"";
            code += entity.isSolid();
            code += "\" mass=\"";
            code += entity.getMass();
            code += "\" visible=\"";
            code += entity.isVisible();
            code += "\" opacity=\"";
            code += entity.getOpacity();
            code += "\" collision=\"";
            code += entity.getCollisionLayer();
            code += "\" invertx=\"";
            code += entity.isInvertX();
            code += "\" inverty=\"";
            code += entity.isInvertY();
            code += "\"";
            code += tag.getCustomTag();
            code += " >";
            
            tag.setTag(code);
            
            tags.add(tag);
            
            return code;
        }
    }
    
    public String updateTag(Node node){
        CodeTag tag = null;
        
        for (CodeTag tag1 : tags) {
            if(tag1.getNode() == node){
                tag = tag1;
            }
        }
        
        if(tag == null){
            return "";
        }
        
        Object nodeData = node.getUserData();
        
        tag.setNode(node);
        
        if(nodeData instanceof Trigger){ //trigger
            Trigger trigger = (Trigger)nodeData;
            tag.setType(0);
            
            String code = "<trigger id=\"";
            code += trigger.getID();
            code += "\" width=\"";
            code += trigger.getWidth();
            code += "\" height=\"";
            code += trigger.getHeight();
            code += "\" ";
            code += tag.getCustomTag();
            code += " >";
            
            tag.setTag(code);
            
            return code;
        }else if (nodeData instanceof Light){ //light
            Light light = (Light)nodeData;
            tag.setType(1);
            
            String code = "<light id=\"";
            code += light.getId();
            code += "\" ambient=\"";
            code += light.isAmbient();
            code += "\" brightness=\"";
            code += light.getBrightness();
            code += "\" red=\"";
            code += light.getRed();
            code += "\" green=\"";
            code += light.getGreen();
            code += "\" blue=\"";
            code += light.getBlue();
            code += "\" radius=\"";
            code += light.getRadius();
            code += "\" ";
            code += tag.getCustomTag();
            code += " >";
            
            tag.setTag(code);
            
            return code;
        } else { //entity
            Entity entity = (Entity)nodeData;
            tag.setType(2);
            
            String code = "<tile id=\"";
            code += node.getID();
            code += "\" frame=\"";
            code += ((Entity)node).getFrame();
            code += "\" skin=\"";
            code += ((Entity)node).getSkin().getID();
            code += "\" solid=\"";
            code += entity.isSolid();
            code += "\" mass=\"";
            code += entity.getMass();
            code += "\" visible=\"";
            code += entity.isVisible();
            code += "\" opacity=\"";
            code += entity.getOpacity();
            code += "\" collision=\"";
            code += entity.getCollisionLayer();
            code += "\" invertx=\"";
            code += entity.isInvertX();
            code += "\" inverty=\"";
            code += entity.isInvertY();
            code += "\" ";
            code += tag.getCustomTag();
            code += " >";
            
            tag.setTag(code);
            
            return code;
        }
    }
    
    public void removeTag(Node node){
        ArrayList<CodeTag> remove = new ArrayList<>();
        for (CodeTag tag : tags) {
            if(tag.getNode() == node){
                remove.add(tag);
                
            }
        }
        for (CodeTag tag : remove) {
            tags.remove(tag);
        }
    }
    
    public String getTag(Node node){
        for (CodeTag tag : tags) {
            if(tag.getNode() == node){
                return tag.getTag();
            }
        }
        return "no code assigned";
    }
    
    public void setCustomTag(Node node, String code){
        for (CodeTag tag : tags) {
            if(tag.getNode() == node){
                tag.setCustomTag(code);
            }
        }
        updateTag(node);
    }
    
    public String getCustomTag(Node node){
        for (CodeTag tag : tags) {
            if(tag.getNode() == node){
                return tag.getCustomTag();
            }
        }
        return "no code assigned";
    }
    
    public void compile(ArrayList<Node> nodeList, String filePath){
        ArrayList<Node> tempList = new ArrayList<>();
        ArrayList<Node> compileList = new ArrayList<>();
        
        //create compile list by adding to temp list then add to compile list (so order is the same)
        for (Node node : nodeList) {
            tempList.add(node);
        }
        for (Node node : tempList) {
            compileList.add(node);
        }
        tempList.clear();
        
        //create file writer
        try {
            File xmlFile = new File(filePath);
            if(!xmlFile.exists()){
                xmlFile.createNewFile();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(xmlFile));
            
            //write header
            writer.write(header);
            
            //run through each item and compile
            boolean compiled;
            for (Node node : compileList) {
                //check if node was compiled already
                compiled = false;
                for (Node check : tempList) {
                    if(check == node){
                        compiled = true;
                    }
                }
                
                if(!compiled){
                    String tag = getTag(node);
                    writer.newLine();
                    writer.write(tag);
                    writer.newLine();
                    for (Node compileNode : compileList) {
                        if(getTag(compileNode).equals(tag)){
                            writer.write(compileNode.getX() + "," + -compileNode.getY() + ";");
                            tempList.add(compileNode);
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
            
            //close file
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(CodeManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
        if(header == null){
           this.header = ""; 
        }
    }

    public void reset() {
        header = "";
        tags.clear();
    }
    
    private class CodeTag{
        private int type; //0 = trigger, 1 == light, 2 == entity
        private Node node;
        private String tag;
        private String customTag = "";

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public Node getNode() {
            return node;
        }

        public void setNode(Node node) {
            this.node = node;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getCustomTag() {
            return customTag;
        }

        public void setCustomTag(String customTag) {
            this.customTag = customTag;
        }
        
    }
}
