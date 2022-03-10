package subirCarpetas;

import java.net.*;
import java.io.*;
import java.nio.file.Files;
import javax.swing.JFileChooser;
import static subirArchivos.sockets.SRecibe.s;

/**
 *
 * @author axele
 */
public class SRecibeCarpeta {

    public static boolean band = true;
    public static ServerSocket s;
    public void recibeServidor() {
        try {
            int pto = 8002;   // App corriendo en este puerto
            this.s = new ServerSocket(pto);
            s.setReuseAddress(true);  // Si se cierra el socket, generamos uno nuevo

            System.out.println("Servidor iniciado esperando por archivos..");
            for (int x = 0; x < 100; x++) {  // Ciclo infinito para aceptar una conexión del CLIENTE cuando se quiera 

//              -----> SÓLO RECIBIRÉ UN ARCHIVO A LA VEZ <----
                s.setSoTimeout(1000); // ***EXTRA, esto es para que expire en un tiempo determinado la conexión
                                      // y para que la conexión de Cliente servidor se destruya   
                Socket cl = s.accept();
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                String ruta_archivos = this.creaDirectorio(dis);
                this.recibeArchivo(cl, ruta_archivos, dis);
                cl.close();

            }//for
//            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//main

    public void recibeArchivo(Socket cl, String ruta_archivos, DataInputStream dis) throws IOException {
        System.out.println("Cliente conectado desde " + cl.getInetAddress() + ":" + cl.getPort());

        String nombre = dis.readUTF();
        long tam = dis.readLong();
        System.out.println("Comienza descarga del archivo " + nombre + " de " + tam + " bytes\n\n");

//            ---> ASOCIANDO EL FLUJO DE ESCRITURA A MI SISTEMA DE ARCHIVOS <----
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(ruta_archivos + nombre));  //especificamos la ruta del archivo que se creo + el nombre del archivo(recibido) que SE CREARÁ
        long recibidos = 0; //Sirve como condición de paro para saber cuando recibí el archivo completo 
        int l = 0, porcentaje = 0;    // l= cuantos segmentos se recibieron por cada TCP 
        while (recibidos < tam) {

//                  ---> PREPARÁNDOSE PARA RECIBIR 1500 BYTES<----
//                No importa si mis paquetes se DESORDENAN ya que TCP los ordena
            byte[] b = new byte[1500];        // Podemos cambiar al tamaño que queramos PERO LAS TRAMAS DE IP miden 1500
            l = dis.read(b);  // Lectura de bytes del flujo que está asociado al SOCKET, AQUÍ LOS DATOS SE CARGAN EN EL ARREGLO y se retorna la cantidad de BYTES recibidos 
            System.out.println("leidos: " + l);
            dos.write(b, 0, l); // Escribo el pedazo de ARCHIVO al sistema de archivos LOCAL 
            dos.flush();  // Hago que los métodos sean escritos con el método FLUSH 
            recibidos = recibidos + l;    // recibidos INCREMENTA 
            porcentaje = (int) ((recibidos * 100) / tam);
            System.out.print("\rRecibido el " + porcentaje + " % del archivo");
        }//while
        System.out.println("Archivo recibido..");
        dos.close();
        dis.close();

    }

    public String creaDirectorio(DataInputStream dis) throws IOException {

        // ----> CREANDO UNA CARPETA EN DONDE SE GUARDARÁN LOS ARCHIVOS <-----
        File f = new File("");    // Es una referencia a un PATH, de momento no apunto a nada que a su vez SIGNIFICA LA RAÍZ DEL PROYECTO (Donde está almacenado el proyecto)
        String carpetName = dis.readUTF();
        String ruta = f.getAbsolutePath() + "\\Nube";    // Obtenga el PATH del archivo, que en este momento es LA CARPETA RAÍZ 
        String carpeta = carpetName;    // Daremos el nombre que recibimos del lado del cliente
        String ruta_archivos = ruta + "\\" + carpeta + "\\";    // Estamos dando un PATH o NOMBRE COMPLETAMENTE CALIFICADO con un STRING aún
        System.out.println("ruta:" + ruta_archivos);    //Imprimimos mi ruta de archivos
        File f2 = new File(ruta_archivos);    //Creamos un apuntador al nuevo nombrel del NOMBRE COMPLETAMENTE CALIFICADO o PATH 
        f2.mkdirs();  //Con este método, en el PATH f2 creamos una carpeta 
        f2.setWritable(true); // Permiso de escritura, para que PUEDE MODIFICAR A LA CARPETA 
        return ruta_archivos;
    }

    public void daRuta() {
        try {
            File carpetaRaiz = new File("");
            int pto = 8002;   // App corriendo en este puerto
            s = new ServerSocket(pto);
            s.setReuseAddress(true);  // Si se cierra el socket, generamos uno nuevo

            //NOTA: Unicamente se aceptará un archivo por conexión 
            // Debido a que cuando se reciba un archivo y su último byte, se recibirá un fin de flujo o un -1 
            // y se cierra el socket 
            // NOTA: UNA CONEXIÓN POR CADA ARCHIVO QUE NECESITEMOS 
            System.out.println("Servidor iniciado dando ruta...");

            for (;;) {  // Ciclo infinito para aceptar una conexión del CLIENTE cuando se quiera 

//              -----> SÓLO RECIBIRÉ UN ARCHIVO A LA VEZ <----
                s.setSoTimeout(1000);
                Socket cl = s.accept();
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());

                dos.writeUTF(carpetaRaiz.getAbsolutePath() + "\\Nube");
                dos.flush();
                dos.close();
            }//for

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File[] getArchivosADescargar() {
        File carpetaRaiz = new File("");
        FilesArray filesToDowloaded;
        File[] archivosSeleccionados = null;
        int numberOfFilesToDownloaded = 0;
        try {
            int pto = 8002;   // App corriendo en este puerto
            this.s = new ServerSocket(pto);
            s.setReuseAddress(true);  // Si se cierra el socket, generamos uno nuevo

            System.out.println("Servidor iniciado esperando por archivos a descargar..");

            for (;;) {  // Ciclo infinito para aceptar una conexión del CLIENTE cuando se quiera 

//              -----> SÓLO RECIBIRÉ UN ARCHIVO A LA VEZ <----
                Socket cl = s.accept();
                System.out.println(cl);
//                DataOutputStream dos= new DataOutputStream(cl.getOutputStream());
//                
//                // ---> ENVIANDO LA CARPETA RAÍZ AL CLIENTE, DE DONDE SE PUEDEN DESCARGAR LAS COSAS <----
//                dos.writeUTF(carpetaRaiz.getAbsolutePath()+"\\Nube");
//                dos.flush();
//                dos.close();

//              ---> ENVIANDO LA RUTA DE LA CARPETA DE DONDE SE PUEDEN DESCARGAR COSAS <---
//                ObjectOutputStream oos=new ObjectOutputStream(cl.getOutputStream());
//                Path pathCarpetFiles=new Path();
//                pathCarpetFiles.setPath(carpetaRaiz.getAbsolutePath()+"\\Nube");
//                oos.writeObject(pathCarpetFiles);
//                oos.flush();
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());

                // ---> ENVIANDO LA CARPETA RAÍZ AL CLIENTE, DE DONDE SE PUEDEN DESCARGAR LOS ARCHIVOS <----
                dos.writeUTF(carpetaRaiz.getAbsolutePath() + "\\Nube");
                dos.flush();

//              -----> LEYENDO LOS ARCHIVOS QUE QUIERE DESCARGAR EL USUARIO <---
                ObjectInputStream ois = new ObjectInputStream(cl.getInputStream());
                filesToDowloaded = (FilesArray) ois.readObject();  //Recibiendo el objeto 
                numberOfFilesToDownloaded = filesToDowloaded.getArraySize();  //Obteniendo el tamaño del array de archivos
                archivosSeleccionados = filesToDowloaded.getFiles();

//                for (File archivosSeleccionado : archivosSeleccionados) {
//                    System.out.println(archivosSeleccionado.getAbsolutePath());
//                }
//                
                ois.close();
                dos.close();
                cl.close();
                s.close();
                break;    //Aquí no estoy diseñando muchas conexiones entrantes 
            }//for

        } catch (Exception e) {
            e.printStackTrace();
        }
        return archivosSeleccionados;
    }

    public void mandaArchivosAlClienteDescarga(File files[]) {
        try {

            Socket cl = this.conectaServidor();
            System.out.println("Enviando archivos por descargar, al cliente...");

            // ---> PRUEBA, MANDANDO UN ARCHIVO <----
//                this.mandaArchivo(files[0], cl);
            for (int i = 0; i < files.length; i++) {
                this.mandaArchivo(files[i], cl);
                if (!(i == files.length - 1)) {
                    cl = this.conectaServidor();    //Restringiendo para que no se abra una conexión en la última iteración 
                }
            }

            cl.close();
        } catch (Exception e) {
            e.printStackTrace();
        }//catch
    }


    public Socket conectaServidor() throws IOException {

        int pto = 8002;
        this.s = new ServerSocket(pto);
        s.setReuseAddress(true);  // Si se cierra el socket, generamos uno nuevo
        Socket cl = s.accept();
        return cl;
    }

    public void mandaArchivo(File f, Socket cl) throws IOException {
        //                -----> EMPEZAMOS A OBTENER LA METAINFORMACIÓN DEL ARCHIVO QUE MANDARÉ<----
        String nombre = f.getName();        // Nombre del archivo con la referencia f
        String path = f.getAbsolutePath();  // Sé dónde está ubicado el archivo 
        long tam = f.length();              // Tamaño del archivo    

//              Le notifico al servidor que enviaré el archivo 
        System.out.println("Preparandose pare enviar archivo " + path + " de " + tam + " bytes\n\n");

//              Obtengo el flujo de escritura de BYTES, (LO QUE SALE)
        DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
        cl.shutdownInput();
//              Flujo de lectura DE BYTES para leer desde el SISTEMA DE ARCHIVOS, habrá uno del lado del SRecibeCarpeta.java
        DataInputStream dis = new DataInputStream(new FileInputStream(path)); //FileInputStream(orientado a bytes) se leen bytes desde el sistema de archivos y está asociado A LA RUTA DEL ARCHIVO QUE SELECCIONE
        // UNDERLYING --> SUBYACENTE

//                ----> MANDANDO METAINFORMACIÓN AL CLIENTE <----
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
        this.s.close();

    }

}
