package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import pl.edu.pw.fizyka.pojava.BitkowskaKysiak.SimulationPanel.Source;

public class Worm {

    private int x, y; 
    private final double speed = 2; 
    private Color color = Color.BLUE;
    private int targetX = -1, targetY = -1;
    private boolean hasTarget = false;
    private boolean activated = false;
    
    public Worm(int startX, int startY) 
    {
        this.x = startX;
        this.y = startY;
    }

    public int[] setDirection(SimulationPanel panel, int r)
    {
        double[][] amplitudes = panel.getCurrentField(); 
        int xDim = panel.x_dim;
        int yDim = panel.y_dim;

        int destX = 0, destY = 0;
        double maxIntensity = 0;

        List<Source> sources = panel.getSources(); // Cache list once

        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                int nx = this.x + dx;
                int ny = this.y + dy;

                if (nx >= 0 && ny >= 0 && nx < xDim && ny < yDim) {

                    // Check if a source is here → devour it
                    for (int i = 0; i < sources.size(); i++) {
                        if (nx == sources.get(i).getX() && ny == sources.get(i).getY()) {
                            sources.remove(i);
                            break;
                        }
                    }

                    double amp = Math.abs(amplitudes[nx][ny]);
                    double estimatedIntensity = 0;

                    // Sum estimated intensities from each source
                    for (Source s : sources) {
                        double dist = Math.sqrt(Math.pow(s.getX() - nx, 2) + Math.pow(s.getY() - ny, 2));
                        estimatedIntensity += (amp*s.getPow()) / (dist + 1); // 1/(r + 1) decay
                    }

                    if (estimatedIntensity > maxIntensity) {
                        maxIntensity = estimatedIntensity;
                        destX = nx;
                        destY = ny;
                    }
                }
            }
        }

        if (destX != 0 || destY != 0) {
            return new int[]{destX, destY}; 
        } else {
            return null; 
        }
    }

/*    
    public int[] setDirection(SimulationPanel panel, int r)
    {
        double[][] amplitudes = panel.getCurrentField(); 
        int xDim = panel.x_dim;
        int yDim = panel.y_dim;

        int destX = 0, destY = 0;
        double maxAmplitude = 0;

        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                int nx = this.x + dx;
                int ny = this.y + dy;
                
                for (int i = 0; i < panel.getSources().size(); i++) {
                    if (nx == panel.getSources().get(i).getX() && ny == panel.getSources().get(i).getY()) {
                    	panel.getSources().remove(i);
                        break; 
                    }
                }



                if (nx >= 0 && ny >= 0 && nx < xDim && ny < yDim) {
                    double amp = Math.abs(amplitudes[nx][ny]);
                    if (amp > maxAmplitude) {
                        maxAmplitude = amp;
                        destX = nx;
                        destY = ny;
                    }
                }
            }
        }

        if (destX != 0 && destY != 0) {
            return new int[]{destX, destY}; 
        } else {
            return null; 
        }
    }
    */

    public void moveTowardTarget() {
        if (!hasTarget) return;

        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx*dx + dy*dy);

        if (distance < speed) {
            x = targetX;
            y = targetY;
            hasTarget = false; // arrived, ready to pick next
        } else {
            x += (dx / distance) * speed;
            y += (dy / distance) * speed;
        }
    }
    
    public void setTarget(int tx, int ty) {
        this.targetX = tx;
        this.targetY = ty;
        this.hasTarget = true;
    }
    public boolean hasTarget() {
        return hasTarget;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    
    public Color getClr() {return color;}
    public int getX() {return x;}
    public int getY() {return y;}
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

}