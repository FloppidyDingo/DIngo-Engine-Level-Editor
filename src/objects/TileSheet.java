/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author James
 */
public class TileSheet {
    private String name;
    private ArrayList<BufferedImage> frames;
    
    public void generate(String basePath, String defPath){
        frames = new ArrayList<>();
        
        try {
            BufferedImage baseImg = ImageIO.read(new File(basePath));
            BufferedImage img = ImageIO.read(new File(defPath));
            byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
            int fx = 0;
            int fy = 0;
            int state = 0;
            for(int y = 0; y < img.getHeight(); y++){
                for(int x = 0; x < img.getWidth(); x++){
                    switch(state){
                        case 0:{
                            if(pixels[x + (y * img.getWidth())] == (byte)0x00){
                                pixels[x + (y * img.getWidth())] = (byte)0xff;
                                fx = x;
                                fy = y;
                                state = 1;
                            }
                            break;
                        }
                        case 1:{
                            if(pixels[x + (y * img.getWidth())] == (byte)0x00){
                                pixels[x + (y * img.getWidth())] = (byte)0xff;
                                for(int y2 = y; y2 < img.getHeight(); y2++){
                                    if(pixels[x + (y2 * img.getWidth())] == (byte)0x00){
                                        pixels[x + (y2 * img.getWidth())] = (byte)0xff;
                                        frames.add(baseImg.getSubimage(fx, fy, x - fx + 1, y2 - fy + 1));
                                        state = 0;
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public int getFrameCount(){
        return frames.size();
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public BufferedImage getFrame(int frame){
        return frames.get(frame);
    }
}
