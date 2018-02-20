/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import javax.inject.Named;
import javax.enterprise.context.Dependent;

/**
 *
 * @author Mike
 */
@Named(value = "flight")
@Dependent
public class Flight {

    /**
     * Creates a new instance of Flight
     */
    public Flight() {
    }
    
}
