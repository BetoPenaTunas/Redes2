package subirCarpetas.main;
import java.io.File;
import java.io.IOException;
import subirCarpetas.SRecibeCarpeta;


public class MainServidorCarpeta {
    
    public static void main(String[] args) throws IOException {
        
        SRecibeCarpeta s1=new SRecibeCarpeta();
       s1.recibeServidor();  //recibiendo carpetas 

//       s1.daRuta();    //Sirve para eliminar archivos 


//      --->Sirve para descargar archivos <---
//        File[] archivosSeleccionados=s1.getArchivosADescargar();
//        for (File archivosSeleccionado : archivosSeleccionados) {
//            System.out.println(archivosSeleccionado.getAbsolutePath());
//        }
        
        /* 
            archivosSeleccionados[0]=new File("C:\\Users\\L450\\Pictures\\Screenshots\\1.jpeg");
            archivosSeleccionados[1]=new File("C:\\Users\\L450\\Pictures\\Screenshots\\2.jpeg");
            archivosSeleccionados[2]=new File("C:\\Users\\L450\\Pictures\\Screenshots\\3.jpeg");
        
        */
//        s1.mandaArchivosAlClienteDescarga(archivosSeleccionados);
//        s1.s.close();
        
    }
    
}
