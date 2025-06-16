package pl.edu.pw.fizyka.pojava.BitkowskaKysiak;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import pl.edu.pw.fizyka.pojava.BitkowskaKysiak.SimulationPanel.Source;

public class Worm {

    private int x, y, cooldown = 0; 
    private final double speed = 2; 
    private Color color = new Color(210, 125, 45);
    private int targetX = -1, targetY = -1;
    private boolean hasTarget = false;
    private boolean activated = false;
    private boolean reactivated = true; //dodano w celu naprawienia blednaj logoki czerwia
    
    public Worm(int startX, int startY) 
    {
        this.x = startX;
        this.y = startY;
    }

    public int[] setDirection(SimulationPanel panel, int r)
    {
    	if (cooldown > 0) return null;
    	
        double[][] amplitudes = panel.getCurrentField(); 
        int xDim = panel.x_dim;
        int yDim = panel.y_dim;

        int destX = 0, destY = 0;
        double maxIntensity = 0;

        List<Source> sources = panel.getSources(); 

        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                int nx = this.x + dx;
                int ny = this.y + dy;

                if (nx >= 0 && ny >= 0 && nx < xDim && ny < yDim) {

                    for (int i = 0; i < sources.size(); i++) {
                        if (nx == sources.get(i).getX() && ny == sources.get(i).getY()) {
                            sources.remove(i);
                            setCooldown(100);
                            break;
                        }
                    }

                    double amp = Math.abs(amplitudes[nx][ny]);
                    double estimatedIntensity = 0;

                    for (Source s : sources) {
                        double dist = Math.sqrt(Math.pow(s.getX() - nx, 2) + Math.pow(s.getY() - ny, 2));
                        estimatedIntensity += (amp*s.getPow()) / (dist + 1); 
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



    public void moveTowardTarget() {
        if (!hasTarget) return;

        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx*dx + dy*dy);

        if (distance < speed) {
            x = targetX;
            y = targetY;
            hasTarget = false; 
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
    
    public void setCooldown(int ticks) {
        this.cooldown = ticks;
        this.activated = false;
    }

    public void tickCooldown() {
        if (cooldown > 0) cooldown--;
        if (cooldown == 0) reactivated = true;
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