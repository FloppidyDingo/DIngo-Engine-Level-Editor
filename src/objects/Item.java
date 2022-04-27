/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

import java.awt.image.BufferedImage;

/**
 *
 * @author James
 */
public class Item {
    public static final int ITEM_ENTITY = 0;
    public static final int ITEM_TRIGGER = 1;
    public static final int ITEM_LIGHT = 2;
    
    private CodeTag tag;
    private int type;
    
    private String ID;
    private int width;
    private int height;
    private boolean ambient;
    private float brightness;
    private float radius;
    private int red;
    private int green;
    private int blue;
    private int frame;
    private TileSheet tile;
    private boolean solid;
    private float mass;
    private boolean visible;
    private float opacity;
    private int collisionLayer;
    private boolean invertX;
    private boolean invertY;
    private int layer;
    private float x;
    private float y;
    
    public BufferedImage getGraphic(){
        return getTile().getFrame(frame);
    }

    public CodeTag getCodeTag() {
        return tag;
    }

    public void setTag(CodeTag tag) {
        this.tag = tag;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public boolean isAmbient() {
        return ambient;
    }

    public void setAmbient(boolean ambient) {
        this.ambient = ambient;
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public int getFrame() {
        return frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public TileSheet getTile() {
        return tile;
    }

    public void setTile(TileSheet tile) {
        this.tile = tile;
    }

    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public int getCollisionLayer() {
        return collisionLayer;
    }

    public void setCollisionLayer(int collisionLayer) {
        this.collisionLayer = collisionLayer;
    }

    public boolean isInvertX() {
        return invertX;
    }

    public void setInvertX(boolean invertX) {
        this.invertX = invertX;
    }

    public boolean isInvertY() {
        return invertY;
    }

    public void setInvertY(boolean invertY) {
        this.invertY = invertY;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
    
}
