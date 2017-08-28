/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taller2;

/**
 *
 * @author CAMILA
 */
public class Taller2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Ascensor ascensor=Instancia.newAscensor();
        ascensor.subir2Pisos();
        ascensor.subir1Piso();
        ascensor.bajar3Pisos();
        ascensor.subir1Piso();
        ascensor.bajar1Piso();
    }
    
}
