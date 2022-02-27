package main;

import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import sockets.CEnvia;

public class MainCliente {

    public static void main(String[] args) throws IOException {
        CEnvia cl = new CEnvia();
//        cl.envioDeCliente();  //ENVIANDO CARPETAS
        
        System.out.println(cl.getRuta());
        String ruta=cl.getRuta();
        cl.eliminarArchivo(ruta);
        

    }


}
