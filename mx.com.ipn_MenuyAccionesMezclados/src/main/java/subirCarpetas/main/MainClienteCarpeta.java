package subirCarpetas.main;

import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import subirCarpetas.CEnviaCarpeta;


public class MainClienteCarpeta {

    public static void main(String[] args) throws IOException, InterruptedException {
        CEnviaCarpeta cl = new CEnviaCarpeta();
        cl.envioDeCliente();  //ENVIANDO CARPETAS
        

        //Eliminando archivos 
//        System.out.println(cl.getRuta());
//        String ruta=cl.getRuta();   //Obteniendo la ruta donde están guardados en este caso están en la carpeta "Nube"        
//        cl.eliminarArchivo(ruta);


// ----> ELIMINANDO CARPETAS <----
//        cl.eliminaCarpeta(ruta);
//        

        //Descargando Archivos
//        cl.seleccionarFilesADescargar();
//        cl.recibeArchivosDelServidor();
        
    }


}
