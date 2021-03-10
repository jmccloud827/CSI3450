/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VeMan;

/**
 *
 * @author Rich Frost, Julian Fares, Jacob McCloud
 * 
 * Used to to pass an integer by reference to a procedure
 */
public class IntRef {
    private int value;
    
    IntRef (int update) {
        this.value = update;
    }
    
    public void setValue (int update) {
        this.value = update;
    }
    
    public int getValue () {
        return this.value;
    } 
    
    public String toString() {
        return String.valueOf(this.value);
    }
}
    
