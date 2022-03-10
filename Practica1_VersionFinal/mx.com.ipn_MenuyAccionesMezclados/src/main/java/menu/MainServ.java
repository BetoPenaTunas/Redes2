package menu;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import subirArchivos.sockets.SRecibe;
import subirCarpetas.SRecibeCarpeta;

public class MainServ {

    public static int bandera;

    public static void main(String[] args) throws InterruptedException, IOException {
        Servidor s = new Servidor();
        s.inicializaServerSocket(); //Se enciende el Servidor 

        /* ---> RECIBIMOS LA ELECCIÓN<---*/
        int choice = 0;

        /* 
            --> CICLO WHILE <----
            Se hace este ciclo debido a que en el cliente también hay uno 
            Pero nosotros sólo queremos ejecutar cierto ACCIÓN, por eso tenemos 
            una variable choice.
        
            ***NOTA: CHECAR BIEN EN DÓNDE SE CIERRAN Y ABREN LOS FLUJOS DE SOCKET, OUTPUT, INPUT 
            Y SERVER***
        
            ***NOTA: Para las acciones se cambio el puerto del servidor, se inició en un nuevo servidor, en el 8001
         */
        while (choice != 5) {
            choice = s.aceptaConexionYEleccion();
            System.out.println("Elección: " + choice);
            s.cl.close();   //Cerrando el socket, se volverá a crear después

            if (choice == 1) {
                /* 
                    ---> EJECUTANDO EL ENVIO DE ARCHIVOS <----
                 */
                SRecibe s1 = new SRecibe();
                s1.recibeServidor();    //Aquí va la de msj1 de la prueba 
                SRecibe.s.close();      //Cerramos la conexión para que nos volvamos a conectar
                s.cl.close();
                
                
                
            } else if (choice == 2) {
                SRecibeCarpeta s2 = new SRecibeCarpeta();   // Aquí va la de msj2 de la prueba 
                s2.recibeServidor();  //recibiendo carpetas
                s.cl.close();
                SRecibeCarpeta.s.close();   //Se cierra la conexión
               
            }else if(choice==3){
                SRecibeCarpeta s3 = new SRecibeCarpeta();
                s3.daRuta();
                s.cl.close();
                SRecibeCarpeta.s.close();
            }
            
            else if(choice==4){
                SRecibeCarpeta s4 = new SRecibeCarpeta();
                s4.daRuta();
                s.cl.close();
                SRecibeCarpeta.s.close();
            }
            
            else {
                s.s.close();
                s.cl.close();
            }

        }

    }
}
