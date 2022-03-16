/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dingoengineleveleditor;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import objects.Item;

/**
 *
 * @author James
 */
public class Renderer extends JPanel{
    private Editor editor;
    
    public void setEditor(Editor edit){
        editor = edit;
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        
        //render placed items
        if(editor == null){
            return;
        }
        
        editor.updateCamera();
        float zoom = editor.getZoom();
        
        //determine number of layers
        int maxLayer = 0;
        for(Item item : editor.getItems()){
            if(item.getType() == Item.ITEM_ENTITY && item.getLayer() > maxLayer){
                maxLayer = item.getLayer();
            }
        }
        
        //render all objects
        for(int layer = 0; layer <= maxLayer; layer++){
            for(Item item : editor.getItems()){
                switch(item.getType()){
                    case Item.ITEM_ENTITY:{
                        if(item.getLayer() == layer){
                            g.drawImage(item.getGraphic(), 
                                    (int)(((item.getX() - editor.getCameraX()) * zoom) + (this.getWidth() / 2) - (item.getGraphic().getWidth() * zoom / 2)),
                                    (int)(((item.getY() - editor.getCameraY()) * zoom) + (this.getHeight() / 2) - (item.getGraphic().getHeight() * zoom / 2)),
                                    (int)(item.getGraphic().getWidth() * zoom),
                                    (int)(item.getGraphic().getHeight() * zoom), null);
                        }
                        
                        break;
                    }
                    case Item.ITEM_LIGHT:{
                        if(layer == 0){
                            g.setColor(Color.yellow);
                            g.fillOval((int)(((item.getX() - editor.getCameraX()) * zoom) + (this.getWidth() / 2) - (8 * zoom)),
                                    (int)(((item.getY() - editor.getCameraY()) * zoom) + (this.getHeight() / 2) - (8 * zoom)),
                                    (int)(16 * zoom), (int)(16 * zoom));
                        }
                        
                        break;
                    }
                    case Item.ITEM_TRIGGER:{
                        if(layer == 0){
                            g.setColor(Color.blue);
                            g.drawRect((int)(((item.getX() - editor.getCameraX()) * zoom) + (this.getWidth() / 2) - (item.getWidth() * zoom / 2)), 
                                    (int)(((item.getY() - editor.getCameraY()) * zoom) + (this.getHeight() / 2) - (item.getHeight() * zoom / 2)),
                                    (int)(item.getWidth() * zoom), (int)(item.getHeight() * zoom));
                        }
                        
                        break;
                    }
                }
            }
        }
        
        //render active item
        Item item = editor.getActiveItem();
        if(item != null && (editor.getEditorMode() == Editor.PLACE_ENTITY || editor.getEditorMode() == Editor.PLACE_LIGHT || editor.getEditorMode() == Editor.PLACE_TRIGGER)){
            switch(item.getType()){
                case Item.ITEM_ENTITY:{
                    g.drawImage(item.getGraphic(), 
                            (int)(((item.getX() - editor.getCameraX()) * zoom) + (this.getWidth() / 2) - (item.getGraphic().getWidth() * zoom / 2)),
                            (int)(((item.getY() - editor.getCameraY()) * zoom) + (this.getHeight() / 2) - (item.getGraphic().getHeight() * zoom / 2)),
                            (int)(item.getGraphic().getWidth() * zoom),
                            (int)(item.getGraphic().getHeight() * zoom), null);
                    break;
                }
                case Item.ITEM_LIGHT:{
                    g.setColor(Color.yellow);
                    g.fillOval((int)(((item.getX() - editor.getCameraX()) * zoom) + (this.getWidth() / 2) - (8 * zoom)),
                            (int)(((item.getY() - editor.getCameraY()) * zoom) + (this.getHeight() / 2) - (8 * zoom)),
                            (int)(16 * zoom), (int)(16 * zoom));
                    break;
                }
                case Item.ITEM_TRIGGER:{
                    g.setColor(Color.blue);
                    g.drawRect((int)(((item.getX() - editor.getCameraX()) * zoom) + (this.getWidth() / 2) - (item.getWidth() * zoom / 2)), 
                            (int)(((item.getY() - editor.getCameraY()) * zoom) + (this.getHeight() / 2) - (item.getHeight() * zoom / 2)),
                            (int)(item.getWidth() * zoom), (int)(item.getHeight() * zoom));
                    break;
                }
            }
        }
        
        //render center marker
        g.setColor(Color.red);
        g.drawLine(-editor.getCameraX() + (this.getWidth() / 2), 
                -editor.getCameraY() + (this.getHeight() / 2) + 10, 
                -editor.getCameraX() + (this.getWidth() / 2), 
                -editor.getCameraY() + (this.getHeight() / 2) - 10);
        g.drawLine(-editor.getCameraX() + (this.getWidth() / 2) - 10, 
                -editor.getCameraY() + (this.getHeight() / 2), 
                -editor.getCameraX() + (this.getWidth() / 2) + 10, 
                -editor.getCameraY() + (this.getHeight() / 2));
    }
    
}
