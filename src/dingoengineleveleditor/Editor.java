/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dingoengineleveleditor;

import Controls.Listeners.GameKeyListener;
import Managers.CodeManager;
import Controls.Listeners.GameMouseListener;
import Engine.Engine;
import Engine.Scene;
import Engine.Utils;
import Managers.ProjectManager;
import objects.Light;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import motion.Vector;
import objects.Entity;
import objects.Node;
import objects.Project;
import objects.Skin;
import objects.Tile;
import objects.Trigger;
import org.lwjgl.input.Keyboard;

/**
 *
 * @author James
 */
public class Editor extends Engine{
    private int msx, msy;
    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;
    private final int mseSpeed = 3;
    
    public final String version = "1_alpha";
    
    private CodeManager codeManager;
    private ProjectManager projectManager;
    private ProjectUI projectUI;
    private NewProject newProject;
    private Project activeProject;
    
    private ArrayList<Node> nodeList;
    private ArrayList<Tile> tiles;
    
    private int editingMode;
    private int requestedMode;
    private Tools tools;
    
    private Entity activeEntity;
    private Skin activeSkin;
    private int activeFrame;
    private boolean genEntity;
    private Entity activeLight;
    private Entity activeTrigger;
    private Scene activeScene;
    private Skin iconSkin;
    
    //object creation variables
    private Entity creationEntity;
    private Light creationLight;
    private Trigger creationTrigger;

    @Override
    public void frame() {
        msx = this.getMousePoint().x / 2 + (int)this.getGuiCamPos().getX();
        msy = this.getMousePoint().y / 2 + (int)this.getGuiCamPos().getY();
        
        if(requestedMode > -1 && requestedMode != editingMode){
            //object destruction
            if(requestedMode != 0 && editingMode == 0){
                activeScene.removeGUIItem(activeEntity);
                activeEntity = null;
            }
            if(requestedMode != 4 && editingMode == 4){
                activeScene.removeGUIItem(activeLight);
                activeLight = null;
            }
            if(requestedMode != 5 && editingMode == 5){
                activeScene.removeGUIItem(activeTrigger);
                activeTrigger = null;
            }
            
            //object creation
            if(requestedMode == 0){
                tools.setUIMode(0);
                createEntity();
            }
            if(requestedMode == 4){
                tools.setUIMode(1);
                createLight();
            }
            if(requestedMode == 5){
                tools.setUIMode(2);
                createTrigger();
            }
            
            editingMode = requestedMode;
            requestedMode = -1;
        }
        if(genEntity){
            if(activeEntity == null){
                createEntity();
            }
            activeEntity.setSkin(activeSkin);
            activeEntity.useSkin(activeFrame);
            genEntity = false;
        }
        switch(editingMode){
            case 0:{ //Tile add
                if(activeEntity != null){
                    activeEntity.setX(msx);
                    activeEntity.setY(msy);
                }
                break;
            }
            case 1:{ //select
                if(activeEntity != null){
                    activeEntity.setSkin(activeSkin);
                    activeEntity.useSkin(activeFrame);
                }
                break;
            }
            case 2:{ //move
                
                break;
            }
            case 3:{ //erase
                
                break;
            }
            case 4:{ //add Light
                activeLight.setX(msx);
                activeLight.setY(msy);
                break;
            }
            case 5:{ //add Trigger
                activeTrigger.setX(msx);
                activeTrigger.setY(msy);
                break;
            }
            case 6:{ //move op Tile
                if(activeEntity != null){
                    activeEntity.setX(msx);
                    activeEntity.setY(msy);
                }
                break;
            }
            case 7:{ //move op light
                if(activeLight != null){
                    activeLight.setX(msx);
                    activeLight.setY(msy);
                }
                break;
            }
            case 8:{ //move op trigger
                if(activeTrigger != null){
                    activeTrigger.setX(msx);
                    activeTrigger.setY(msy);
                }
                break;
            }
        }
        
        float dirx, diry;
        if(left){
            dirx = mseSpeed;
        } else if(right){
            dirx = -mseSpeed;
        } else {
            dirx = 0;
        }
        if(up){
            diry = mseSpeed;
        } else if (down){
            diry = -mseSpeed;
        } else {
            diry = 0;
        }
        this.setGuiCamDir(new Vector(dirx, diry));
        
        if(activeProject != null){
            hgpu.setTitle(activeProject.getName() + " | " + msx + ", " + msy);
        }else{
            hgpu.setTitle(msx + ", " + msy);
        }
    }

    @Override
    public void init() {
        hgpu.setGlobalScale(2);
        nodeList = new ArrayList<>();
        tiles = new ArrayList<>();
        
        codeManager = new CodeManager();
        projectManager = new ProjectManager(this);
        projectUI = new ProjectUI(this);
        newProject = new NewProject(this);
        
        editingMode = 0;
        requestedMode = -1;
        
        iconSkin = new Skin();
        try {
            iconSkin.setBaseImage("icons/icons.png");
            iconSkin.bufferSkinDef("icons/icons_def.png");
        } catch (IOException ex) {
            Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        creationEntity = new Entity(iconSkin, "entity");
        creationLight = new Light();
        creationTrigger = new Trigger(0, 0, 1, 1);
        
        tools = new Tools(this);
        tools.setVisible(true);
        
        this.setMode(TOP_DOWN);
        
        activeScene = new Scene();
        this.setScene(activeScene);
        
        this.addListener(new GameMouseListener(){
            
            @Override
            public void mousePressed(int button, Point position){
                handleClick(button);
            }
            
        });
        this.addListener(new GameKeyListener(){
            
            @Override
            public void keyPressed(String id){
                handleKeyPress(id);
            }
            
            @Override
            public void keyReleased(String id){
                handleKeyRelease(id);
            }
                
        });
        this.addKey(Keyboard.KEY_LEFT, "left");
        this.addKey(Keyboard.KEY_RIGHT, "right");
        this.addKey(Keyboard.KEY_UP, "up");
        this.addKey(Keyboard.KEY_DOWN, "down");
        
        this.setRunning(true);
    }
    
    private void createEntity(){
        if (activeSkin != null) {
            Entity dataEntity = new Entity(iconSkin, "entity");
            dataEntity.setSolid(creationEntity.isSolid());
            dataEntity.setMass(creationEntity.getMass());
            dataEntity.setVisible(creationEntity.isVisible());
            dataEntity.setOpacity(creationEntity.getOpacity());
            dataEntity.setCollisionLayer(creationEntity.getCollisionLayer());
            
            activeEntity = new Entity(activeSkin, "entDefault" + activeSkin.getID() + activeFrame);
            activeEntity.useSkin(activeFrame);
            activeEntity.setInvertX(creationEntity.isInvertX());
            activeEntity.setInvertY(creationEntity.isInvertY());
            activeEntity.setUserData(dataEntity);
            activeScene.attachGUIChild(activeEntity);
            
            codeManager.genTag(activeEntity);
            
            tools.updateUI(activeEntity, null, null, codeManager, activeEntity);
        }
    }
    
    private void createLight(){
        Light dataLight = new Light();
        dataLight.setAmbient(creationLight.isAmbient());
        dataLight.setRed(creationLight.getRed());
        dataLight.setGreen(creationLight.getGreen());
        dataLight.setBlue(creationLight.getBlue());
        dataLight.setRadius(creationLight.getRadius());
        dataLight.setBrightness(creationLight.getBrightness());
        dataLight.setId("light_default");
        
        activeLight = new Entity(iconSkin, "Light");
        activeLight.setUserData(dataLight);
        activeScene.attachGUIChild(activeLight);
        
        codeManager.genTag(activeLight);
        
        tools.updateUI(null, dataLight, null, codeManager, activeLight);
    }
    
    private void createTrigger(){
        Trigger dataTrigger = new Trigger(0, 0, creationTrigger.getWidth(), creationTrigger.getHeight());
        dataTrigger.setID("trigger_default");
        
        activeTrigger = new Entity(iconSkin, "Trigger");
        activeTrigger.useSkin(1);
        activeTrigger.setUserData(dataTrigger);
        activeScene.attachGUIChild(activeTrigger);
        
        codeManager.genTag(activeTrigger);
        
        tools.updateUI(null, null, dataTrigger, codeManager, activeTrigger);
    }
    
    private Object selectNode(){
        List<Node> clickedNodes = Utils.rayTest(nodeList, new Point(msx, msy));
        if(clickedNodes.isEmpty()){
            return null;
        }
        Object node = clickedNodes.get(clickedNodes.size() - 1);
        System.out.println(node);
        return node;
    }
    
    public void handleKeyPress(String id){
        switch(id){
            case "left":{
                left = true;
                break;
            }
            case "right":{
                right = true;
                break;
            }
            case "up":{
                up = true;
                break;
            }
            case "down":{
                down = true;
                break;
            }
        }
    }
    
    public void handleKeyRelease(String id){
        switch(id){
            case "left":{
                left = false;
                break;
            }
            case "right":{
                right = false;
                break;
            }
            case "up":{
                up = false;
                break;
            }
            case "down":{
                down = false;
                break;
            }
        }
    }
    
    public void handleClick(int button){
        switch(editingMode){
            case 0:{
                nodeList.add(activeEntity);
                createEntity();
                break;
            }
            case 1:{
                Object node = selectNode();
                if(node == null){
                    break;
                }
                if(node instanceof Entity){
                    if(((Entity)node).getUserData() instanceof Light){
                        System.out.println("Light selected");
                        activeLight = (Entity)node;
                        System.out.println(activeLight.getID());
                        tools.setUIMode(1);
                        tools.updateUI(null, (Light)activeLight.getUserData(), null, codeManager, activeLight);
                    }else if(((Entity)node).getUserData() instanceof Trigger){
                        System.out.println("Trigger selected");
                        activeTrigger = (Entity)node;
                        System.out.println(activeTrigger.getID());
                        tools.setUIMode(2);
                        tools.updateUI(null, null, (Trigger)activeTrigger.getUserData(), codeManager, activeTrigger);
                    } else {
                        System.out.println("Entity selected");
                        activeEntity = (Entity)node;
                        System.out.println(activeEntity.getID());
                        tools.setUIMode(0);
                        tools.updateUI(activeEntity, null, null, codeManager, activeEntity);
                    }
                }
                break;
            }
            case 2:{
                Object node = selectNode();
                if(node == null){
                    break;
                }
                if(node instanceof Entity){
                    if(((Entity)node).getUserData() instanceof Light){
                        System.out.println("Light selected");
                        activeLight = (Entity)node;
                        System.out.println(activeLight.getID());
                        setEditingMode(7);
                    }else if(((Entity)node).getUserData() instanceof Trigger){
                        System.out.println("Trigger selected");
                        activeTrigger = (Entity)node;
                        System.out.println(activeTrigger.getID());
                        setEditingMode(8);
                    }else{
                        System.out.println("Entity selected");
                        activeEntity = (Entity)node;
                        System.out.println(activeEntity.getID());
                        setEditingMode(6);
                    }
                }
                break;
            }
            case 3 :{
                Node node = (Node)selectNode();
                activeScene.removeGUIItem(node);
                nodeList.remove(node);
                codeManager.removeTag(node);
                break;
            }
            case 4:{
                nodeList.add(activeLight);
                createLight();
                break;
            }
            case 5:{
                nodeList.add(activeTrigger);
                createTrigger();
                break;
            }
            case 6:{
                activeEntity = null;
                setEditingMode(2);
                break;
            }
            case 7:{
                activeLight = null;
                setEditingMode(2);
                break;
            }
            case 8:{
                activeTrigger = null;
                setEditingMode(2);
                break;
            }
        }
    }
    
    public Tile generateTile(String filePath, String name){
        System.out.println(filePath);
        String defPath = filePath.substring(0, filePath.length() - 4) + "_def.png";
        System.out.println(defPath);
        
        Tile tile = new Tile();
        tile.path = filePath;
        tile.id = name;
        
        File sourceFile = new File(filePath);
        if(!sourceFile.exists()){
            System.out.println("Image file not found");
            return null;
        }
        
        File defFile = new File(defPath);
        if(!defFile.exists()){
            System.out.println("Definition file not found");
            return null;
        }
        
        Skin skin = new Skin();
        try {
            skin.setBaseImage(filePath);
            skin.bufferSkinDef(defPath);
            skin.setID(name);
            tile.skin = skin;
        } catch (IOException ex) {
            Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return tile;
    }
    
    public void compileMap(){
        codeManager.setHeader(activeProject.getHeader());
        codeManager.compile(nodeList, activeProject.getPath() + activeProject.getName() + ".map");
    }
    
    public void saveProject(){
        projectManager.saveProject(nodeList, tiles, activeProject);
    }
    
    public void loadProject(String input){
        codeManager.reset();
        nodeList.clear();
        tiles.clear();
        scene.getGUI().clear();
        activeProject = new Project();
        projectManager.loadProject(scene, activeProject, input);
    }
    
    public void setEditingMode(int mode){
        if(editingMode == mode){
            return;
        }
        requestedMode = mode;
    }
    
    public void setEntitySkin(Skin skin, int frame){
        activeSkin = skin;
        activeFrame = frame;
        if (editingMode == 0) {
            genEntity = true;
        }
    }
    
    public Entity getActiveEntity(){
        return activeEntity;
    }
    
    public Light getActiveDataLight(){
        if(activeLight == null){
            return null;
        }
        return (Light)activeLight.getUserData();
    }
    
    public Trigger getActiveDataTrigger(){
        return (Trigger)activeTrigger.getUserData();
    }
    
    public Entity getActiveLight(){
        return activeLight;
    }
    
    public Entity getActiveTrigger(){
        return activeTrigger;
    }
    
    public void setActiveEntity(Entity entity){
        Entity updateEntity = (Entity)entity.getUserData();
        creationEntity.setSolid(updateEntity.isSolid());
        creationEntity.setMass(updateEntity.getMass());
        creationEntity.setVisible(updateEntity.isVisible());
        creationEntity.setOpacity(updateEntity.getOpacity());
        creationEntity.setCollisionLayer(updateEntity.getCollisionLayer());
        creationEntity.setInvertX(updateEntity.isInvertX());
        creationEntity.setInvertY(updateEntity.isInvertY());
    }
    
    public void setActiveTrigger(Trigger trigger){
        creationTrigger.setID(trigger.getID());
        creationTrigger.setWidth(trigger.getWidth());
        creationTrigger.setHeight(trigger.getHeight());
    }
    
    public void setActiveLight(Light light){
        creationLight.setId(light.getId());
        creationLight.setAmbient(light.isAmbient());
        creationLight.setRed(light.getRed());
        creationLight.setGreen(light.getGreen());
        creationLight.setBlue(light.getBlue());
        creationLight.setRadius(light.getRadius());
        creationLight.setBrightness(light.getBrightness());
        activeLight.setUserData(light);
    }
    
    public CodeManager getCodeManager(){
        return codeManager;
    }

    public ProjectUI getProjectUI() {
        return projectUI;
    }

    public Project getActiveProject() {
        return activeProject;
    }

    public void setActiveProject(Project activeProject) {
        this.activeProject = activeProject;
    }

    public NewProject getNewProject() {
        return newProject;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public Tools getTools() {
        return tools;
    }

    public ArrayList<Node> getNodeList() {
        return nodeList;
    }

    public Skin getIconSkin() {
        return iconSkin;
    }

}
