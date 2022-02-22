package sockets;

import java.net.*;
import java.io.*;

/**
 *
 * @author axele
 */
public class SRecibe {
    public void recibeServidor(){
      try{
          int pto = 8000;   // App corriendo en este puerto
          ServerSocket s = new ServerSocket(pto);
          s.setReuseAddress(true);  // Si se cierra el socket, generamos uno nuevo
          
          //NOTA: Unicamente se aceptará un archivo por conexión 
          // Debido a que cuando se reciba un archivo y su último byte, se recibirá un fin de flujo o un -1 
          // y se cierra el socket 
          // NOTA: UNA CONEXIÓN POR CADA ARCHIVO QUE NECESITEMOS 
          System.out.println("Servidor iniciado esperando por archivos..");
          
                   
          for(;;){  // Ciclo infinito para aceptar una conexión del CLIENTE cuando se quiera 
              
//              -----> SÓLO RECIBIRÉ UN ARCHIVO A LA VEZ <----
              
              Socket cl = s.accept();
              String ruta_archivos=this.creaDirectorio();
              this.recibeArchivo(cl,ruta_archivos);
              
          }//for
          
      }catch(Exception e){
          e.printStackTrace();
      }  
    }//main
    
    public void recibeArchivo(Socket cl,String ruta_archivos) throws IOException{
        System.out.println("Cliente conectado desde "+cl.getInetAddress()+":"+cl.getPort());
              
//            Flujo de lectura, ASOCIADO al socket del CLIENTE, con el que se recibirá la INFORMACIÓN
//            ---->Primero recibo la METAINFORMACIÓN<----

              DataInputStream dis = new DataInputStream(cl.getInputStream());
              
//            nombre y tam deben estar en orden 
              String nombre = dis.readUTF(); // Esto está conectado con el Flujo de Salida dos, del archivo CEnvia.java
              long tam = dis.readLong();
              System.out.println("Comienza descarga del archivo "+nombre+" de "+tam+" bytes\n\n");
              
              
//            ---> ASOCIANDO EL FLUJO DE ESCRITURA A MI SISTEMA DE ARCHIVOS <----
              DataOutputStream dos = new DataOutputStream(new FileOutputStream(ruta_archivos+nombre));  //especificamos la ruta del archivo que se creo + el nombre del archivo(recibido) que SE CREARÁ
              long recibidos=0; //Sirve como condición de paro para saber cuando recibí el archivo completo 
              int l=0, porcentaje=0;    // l= cuantos segmentos se recibieron por cada TCP 
              while(recibidos<tam){
                  
//                  ---> PREPARÁNDOSE PARA RECIBIR 1500 BYTES<----
//                No importa si mis paquetes se DESORDENAN ya que TCP los ordena
                  byte[] b = new byte[1500];        // Podemos cambiar al tamaño que queramos PERO LAS TRAMAS DE IP miden 1500
                  l = dis.read(b);  // Lectura de bytes del flujo que está asociado al SOCKET, AQUÍ LOS DATOS SE CARGAN EN EL ARREGLO y se retorna la cantidad de BYTES recibidos 
                  System.out.println("leidos: "+l);
                  dos.write(b,0,l); // Escribo el pedazo de ARCHIVO al sistema de archivos LOCAL 
                  dos.flush();  // Hago que los métodos sean escritos con el método FLUSH 
                  recibidos = recibidos + l;    // recibidos INCREMENTA 
                  porcentaje = (int)((recibidos*100)/tam);
                  System.out.print("\rRecibido el "+ porcentaje +" % del archivo");
              }//while
              System.out.println("Archivo recibido..");
              dos.close();
              dis.close();
              cl.close();
    }
    
    public String creaDirectorio(){
        
         // ----> CREANDO UNA CARPETA EN DONDE SE GUARDARÁN LOS ARCHIVOS <-----
          File f = new File("");    // Es una referencia a un PATH, de momento no apunto a nada que a su vez SIGNIFICA LA RAÍZ DEL PROYECTO (Donde está almacenado el proyecto)
          String ruta = f.getAbsolutePath();    // Obtenga el PATH del archivo, que en este momento es LA CARPETA RAÍZ 
          String carpeta="archivos";    // Concatenaremos esta nombre a la ruta 
          String ruta_archivos = ruta+"\\"+carpeta+"\\";    // Estamos dando un PATH o NOMBRE COMPLETAMENTE CALIFICADO con un STRING aún
          System.out.println("ruta:"+ruta_archivos);    //Imprimimos mi ruta de archivos
          File f2 = new File(ruta_archivos);    //Creamos un apuntador al nuevo nombrel del NOMBRE COMPLETAMENTE CALIFICADO o PATH 
          f2.mkdirs();  //Con este método, en el PATH f2 creamos una carpeta 
          f2.setWritable(true); // Permiso de escritura, para que PUEDE MODIFICAR A LA CARPETA 
          return ruta_archivos;
    }
}
