package subirCarpetas;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;    // Clase que me permite ver una CAJA DE DIALOGO(COMO VENTANA) con archivos 

//----> ESTE CLIENTE ENVÍA O SUBE ARCHIVOS A UN SERVIDOR, COMO SI ESTE ÚLTIMO FUERA LA NUBE <---
// Ocuparemos flujos orientados a bytes para no cargarle basura(cosa que ocurre si enviaramos por caractéres)
// STREAM == FLUJO
public class CEnviaCarpeta {

    public void envioDeCliente() {
        try {
            int pto = 8002;
            String dir = "localhost";   // Podemos poner local HOST DIR==IP PUBLICA  
            Socket cl = null;    // Se intentará conectar al servidor que está en el PUERTO 8002
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
            cl.close();
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

        int pto = 8002;
        String dir = "localhost";   // Podemos poner local HOST DIR==IP PUBLICA  
        Socket c = new Socket(dir, pto);    // Se intentará conectar al servidor que está en el PUERTO 8002
        return c;
    }

    public void eliminarArchivo(String ruta) {

        File filesToDelete[];
        JFileChooser jf = new JFileChooser(ruta);
        jf.setMultiSelectionEnabled(true);
        int r = jf.showOpenDialog(null);
        jf.setRequestFocusEnabled(true);
        String fileEliminado;

        if (r == JFileChooser.APPROVE_OPTION) {
            filesToDelete = jf.getSelectedFiles();
            for (int i = 0; i < filesToDelete.length; i++) {
                fileEliminado = filesToDelete[i].getName();   //Nombre del archivo más extensión
                filesToDelete[i].delete();
                System.out.println("Se ha eliminado el archivo: " + fileEliminado);
            }

        }
    }

    public void eliminaCarpeta(String ruta) {

        /* 
            NOTA: Para que una carpeta se elimine, debe de estar vacía
         */
        File filesToDelete[];
        JFileChooser jf = new JFileChooser(ruta);   // Abre el FileChooser desde la ruta que está como argumento
        jf.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int r = jf.showOpenDialog(null);
        jf.setRequestFocusEnabled(true);
        File carpetaAEliminar = jf.getSelectedFile(); //Esto trae una carpeta

        String fileEliminado;
        if (r == JFileChooser.APPROVE_OPTION) {
            filesToDelete = carpetaAEliminar.listFiles();
            for (int i = 0; i < filesToDelete.length; i++) {
                fileEliminado = filesToDelete[i].getName();   //Nombre del archivo más extensión
                filesToDelete[i].delete();
                System.out.println("Se ha eliminado el archivo: " + fileEliminado);
            }
        }

        String auxCarpEliminada = carpetaAEliminar.getName();
        System.out.println("Se ha eliminado la carpeta: " + auxCarpEliminada);
        carpetaAEliminar.delete();  //Cuando se vacían los archivos de la carpeta, YA SE PUEDE ELIMINAR LA CARPETA 

    }

    public String getRuta() throws IOException {
        String ruta = "";
        Socket cl = new Socket();
        cl = this.conectaSocket();
        DataInputStream dis = new DataInputStream(cl.getInputStream());
        ruta = dis.readUTF();
        dis.close();
        cl.close();
        return ruta;
    }

    public void seleccionarFilesADescargar() {
        File[] archivosSeleccionados;  //Arreglo que tiene los archivos seleccionados
        FilesArray files;   //Objecto que manda los archivos seleccionados, clase serializable 
        Path pathCarpetaADescargar;
        try {
            int pto = 8002;
            String dir = "localhost";   // Podemos poner local HOST DIR==IP PUBLICA  
            Socket cl = null;    // Se intentará conectar al servidor que está en el PUERTO 8002
            cl = this.conectaSocket();  // Si se logra la conexión, se continua con el programa 
            System.out.println(cl);

//          --->RECIBIENDO DATOS DEL SERVIDOR <----
//            ObjectInputStream dis= new ObjectInputStream(cl.getInputStream());
//            pathCarpetaADescargar=(Path)dis.readObject();
//            System.out.println(pathCarpetaADescargar.getPath());
//            
            DataInputStream dis = new DataInputStream(cl.getInputStream());
            String pathCarpeta = dis.readUTF();
            System.out.println(pathCarpeta);

            System.out.println("Abriendo ruta del servidor para descargar archivos...");
            JFileChooser jf = new JFileChooser(pathCarpeta);   // Creando mi instancia del FileChooser, por default podemos ELEGIR SÓLO UN ARCHIVO
            jf.setMultiSelectionEnabled(true);
            // showOpen retorna una CONSTANTE con base al botón que se apretó, APROVE_OPTION==OPEN
            int r = jf.showOpenDialog(null);    // Lanza la caja de dialogo, null== Indica que yo no estoy lanzando una VENTANA PADRE 1:02 
            jf.setRequestFocusEnabled(true);    // Cuando se despliegue la VENTANA o CAJA DE DIALOGO, se tiene que enfocar, en PRIMER PLANO RESPECTO A OTRAS APLICACIONES 
            System.out.println("Seleccione el o los archivos a descargar...");

            if (r == JFileChooser.APPROVE_OPTION) {

                //--->Mandando un objeto que tiene un Arreglo de Files<---
                ObjectOutputStream oos = new ObjectOutputStream(cl.getOutputStream());
                archivosSeleccionados = jf.getSelectedFiles();
                files = new FilesArray(archivosSeleccionados, archivosSeleccionados.length); //Objeto que se mandará
                oos.writeObject(files);
                oos.flush();
                oos.close();

                dis.close();
                cl.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }//catch
    }//main

    public void descargaArchivos() { //Se recibirán los archivos del servidor 
        try {
            int pto = 8002;
            String dir = "localhost";   // Podemos poner local HOST DIR==IP PUBLICA
            Socket cl = null;    // Se intentará conectar al servidor que está en el PUERTO 8002
            cl = this.conectaSocket();  // Si se logra la conexión, se continua con el programa 

        } catch (IOException ex) {
            Logger.getLogger(CEnviaCarpeta.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    // ---> ESTE MÉTODO SE ENLAZARÁ CON MandaArchivosAlCliente de la clase SRecibe.java<----
    public void recibeArchivosDelServidor() { //Se recibirán los archivos del servidor 
        while (true) {      //Recibiremos varias veces un archivo, simula un SERVIDOR QUE ESPERA VARIAS COLECCIONES

            try {
                File ruta = new File("");
                File ruta_archivos = new File(ruta.getAbsoluteFile() + "\\Descargas\\");    //Creando una carpeta de descargas del cliente
                ruta_archivos.mkdirs();
                ruta_archivos.setWritable(true);
                System.out.println(ruta_archivos);
                Socket cl = this.conectaSocket();  // Si se logra la conexión, se continua con el programa 
                System.out.println("Cliente conectado desde " + cl.getInetAddress() + ":" + cl.getPort());

//            Flujo de lectura, ASOCIADO al socket del CLIENTE, con el que se recibirá la INFORMACIÓN
//            ---->Primero recibo la METAINFORMACIÓN<----
                DataInputStream dis = new DataInputStream(cl.getInputStream());

//            nombre y tam deben estar en orden 
                String nombre = dis.readUTF(); // Esto está conectado con el Flujo de Salida dos, del archivo CEnviaCarpeta.java
                long tam = dis.readLong();
                System.out.println("Comienza descarga del archivo " + nombre + " de " + tam + " bytes\n\n");

//            ---> ASOCIANDO EL FLUJO DE ESCRITURA A MI SISTEMA DE ARCHIVOS <----
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(ruta_archivos.getAbsolutePath() + "\\" + nombre));  //especificamos la ruta del archivo que se creo + el nombre del archivo(recibido) que SE CREARÁ
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
                cl.close();
            } catch (IOException ex) {
                Logger.getLogger(CEnviaCarpeta.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }
}
