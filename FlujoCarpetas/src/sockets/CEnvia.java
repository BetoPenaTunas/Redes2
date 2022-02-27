package sockets;

import java.net.*;
import java.io.*;
import javax.swing.JFileChooser;    // Clase que me permite ver una CAJA DE DIALOGO(COMO VENTANA) con archivos 

//----> ESTE CLIENTE ENVÍA O SUBE ARCHIVOS A UN SERVIDOR, COMO SI ESTE ÚLTIMO FUERA LA NUBE <---
// Ocuparemos flujos orientados a bytes para no cargarle basura(cosa que ocurre si enviaramos por caractéres)
// STREAM == FLUJO
public class CEnvia {

    public void envioDeCliente() {
        try {
            int pto = 8000;
            String dir = "localhost";   // Podemos poner local HOST DIR==IP PUBLICA  
            Socket cl = null;    // Se intentará conectar al servidor que está en el PUERTO 8000
            cl = this.conectaSocket();
            System.out.println("Conexion con servidor establecida.. lanzando FileChooser..");
            JFileChooser jf = new JFileChooser();   // Creando mi instancia del FileChooser, por default podemos ELEGIR SÓLO UN ARCHIVO
            jf.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); //Seleccionando directorio            
            // showOpen retorna una CONSTANTE con base al botón que se apretó, APROVE_OPTION==OPEN
            int r = jf.showOpenDialog(null);    // Lanza la caja de dialogo, null== Indica que yo no estoy lanzando una VENTANA PADRE 1:02 
            jf.setRequestFocusEnabled(true);    // Cuando se despliegue la VENTANA o CAJA DE DIALOGO, se tiene que enfocar, en PRIMER PLANO RESPECTO A OTRAS APLICACIONES 

            if (r == JFileChooser.APPROVE_OPTION) {

                //File tiene el path del directorio 
                File f = jf.getSelectedFile();  // Este método me retorna una referencia del archivo que seleccioné 
                String carpetName = f.getName();  //Nombre de mi nueva carpeta
                System.out.println(carpetName);

                /* 
                    Rescatando los archivos del DIRECTORIO
                 */
                File archivos[] = f.listFiles();  //Recuperandolos en un arreglo
                for (int i = 0; i < archivos.length; i++) {
                    System.out.println(archivos[i].getAbsoluteFile());  //Imprimiendo el path de cada ARCHIVO DEL DIRECTORIO
                    this.mandaArchivo(archivos[i], cl, carpetName);

                    //Esto es para que no se abra una conexión extra CUANDO ya se llegue al final del archivo
                    if (!(i == archivos.length - 1)) {
                        cl = this.conectaSocket();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }//catch
    }//main

    public void envioDeSubcarpeta(Socket cl, File f1) {
        try {

            //File tiene el path del directorio 
            File f = f1;  // Este método me retorna una referencia del archivo que seleccioné 
            String carpetName = f.getName();  //Nombre de mi nueva carpeta
            System.out.println(carpetName);

            /* 
                    Rescatando los archivos del DIRECTORIO
             */
            File archivos[] = f.listFiles();  //Recuperandolos en un arreglo
            for (int i = 0; i < archivos.length; i++) {
                System.out.println(archivos[i].getAbsoluteFile());  //Imprimiendo el path de cada ARCHIVO DEL DIRECTORIO
                this.mandaArchivo(archivos[i], cl, carpetName);

                //Esto es para que no se abra una conexión extra CUANDO ya se llegue al final del archivo
                if (!(i == archivos.length - 1)) {
                    cl = this.conectaSocket();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }//catch
    }//main

    public void mandaArchivo(File f, Socket cl, String carpetName) throws IOException {

        if (!f.isDirectory()) {
            //                -----> EMPEZAMOS A OBTENER LA METAINFORMACIÓN DEL ARCHIVO QUE MANDARÉ<----
            String nombre = f.getName();        // Nombre del archivo con la referencia f
            String path = f.getAbsolutePath();  // Sé dónde está ubicado el archivo 
            long tam = f.length();              // Tamaño del archivo    

//              Le notifico al servidor que enviaré el archivo 
            System.out.println("Preparandose pare enviar archivo " + path + " de " + tam + " bytes\n\n");

//              Obtengo el flujo de escritura de BYTES, (LO QUE SALE)
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
            cl.shutdownInput();
//              Flujo de lectura DE BYTES para leer desde el SISTEMA DE ARCHIVOS, habrá uno del lado del SRecibe.java
            DataInputStream dis = new DataInputStream(new FileInputStream(path)); //FileInputStream(orientado a bytes) se leen bytes desde el sistema de archivos y está asociado A LA RUTA DEL ARCHIVO QUE SELECCIONE
            // UNDERLYING --> SUBYACENTE

//                ----> MANDANDO METAINFORMACIÓN<----
            dos.writeUTF(carpetName);
            dos.writeUTF(nombre);   // Mandando el nombre al buffer
            dos.flush();            // Mandando al servidor al FLUJO "dis" 
            dos.writeLong(tam);
            dos.flush();

//                -----> MANDANDO EL CONTENIDO DEL ARCHIVO <-----
            long enviados = 0;      // Acumulador que, en cada iteración, incrementará en relación al número de BYTES que se mandaron en dicha iteración. Es LONG por que el tamaño del archivo también lo es
            int l = 0, porcentaje = 0;   // l== saber cuantos BYTES se pudieron leer desde un archivo DE UN JALÓN, porcentaje== para saber que porcentaje de envio se lleva 
            while (enviados < tam) {    // Cuando el acumulador enviados==tam se paran de enviar los archivos 

//              ----> SE ENVÍA POR PARTES<---
//              Debido a que si mandamos archivos grandes, no tenemos tanta memoria 
                // Si pongo un tamaño más grande de 1500 las tramas se van a segmentar y habrá RETARDOS 
                // Podemos cambiar al tamaño que queramos PERO LAS TRAMAS DE ETHERNET miden 1500 bytes 
                byte[] b = new byte[1500];  //CARGAREMOS CADA PEDAZO DEL ARCHIVO en este arreglo que se genera en cada iteración 

                // ---> COMO ES READ se espera hasta que se escriba WRITE (línea 74)
                l = dis.read(b);  // READ == RECIBIR, hago la lectura de BYTES DESDE MI ARCHIVO y los CARGO en el arreglo byte "b"
                //l retorna cuántos bytes se pudieron leer de un jalón, en ocasiones retorna -1 si ya llegamos al final del archivo

                System.out.println("enviados: " + l);
                dos.write(b, 0, l);   // b == escribo este arreglo, 0 == desplazamiento, quieres iniciar desde el cero(0) o iniciar desde los primeros n BYTES 
                dos.flush();
                enviados = enviados + l;
                porcentaje = (int) ((enviados * 100) / tam);
                System.out.print("\rEnviado el " + porcentaje + " % del archivo");
            }//while
            System.out.println("\nArchivo enviado..");
            dis.close();
            dos.close();
            cl.close();
        } else {
            System.out.println("No se puede enviar la SUBCARPETA...");
            this.envioDeSubcarpeta(cl, f);

        }
    }



    public Socket conectaSocket() throws IOException {

        int pto = 8000;
        String dir = "localhost";   // Podemos poner local HOST DIR==IP PUBLICA  
        Socket c = new Socket(dir, pto);    // Se intentará conectar al servidor que está en el PUERTO 8000
        return c;
    }

    public void eliminarArchivo(String ruta) {

        File filesToDelete[];
        JFileChooser jf = new JFileChooser(ruta);
        jf.setMultiSelectionEnabled(true);
        int r = jf.showOpenDialog(null);
        jf.setRequestFocusEnabled(true);
        
        if (r == JFileChooser.APPROVE_OPTION) {
            filesToDelete = jf.getSelectedFiles();
            for (int i = 0; i < filesToDelete.length; i++) {
                System.out.println("Se ha eliminado el archivo: "+filesToDelete[i].getName());
                filesToDelete[i].delete();
                
            }
            
        }
    }
    
    
    public String getRuta() throws IOException{
        String ruta="";
        Socket cl=new Socket();
        cl=this.conectaSocket();
        DataInputStream dis= new DataInputStream(cl.getInputStream());
        ruta=dis.readUTF();
        dis.close();
        cl.close();
        return ruta;
    }
    

    

}
