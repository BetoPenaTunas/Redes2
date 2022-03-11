package menu;

import descargas.ServDescargas;
import descargas.carpetas.CDescargaFile;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import subirArchivos.sockets.CEnvia;
import subirCarpetas.CEnviaCarpeta;

public class MainClie {

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        int choice = 1;
        try {

            Cliente cl = new Cliente();

            while (choice != 7) {

                System.out.println("Elige una opción: ");
                System.out.println("1--> Subir Archivos");
                System.out.println("2--> Subir Carpeta");
                System.out.println("3--> Eliminar Archivos");
                System.out.println("4--> Eliminar Carpeta");
                System.out.println("5--> Descargar Archivos");
                System.out.println("6--> Descargar Carpeta");
                System.out.println("7--> Salir ");
                choice = s.nextInt();

                cl.conectaConServer(choice);    //Conectando con el servidor
                cl.cl.close();
                switch (choice) {
                    case 1:
                        CEnvia c1 = new CEnvia();
                        c1.envioDeCliente();//Aquí va la de msj1
                        cl.cl.close();  //Tenemos que cerrar el socket, para invocar otra función 
                        System.out.println();
                        break;

                    case 2:
                        CEnviaCarpeta carpeta = new CEnviaCarpeta();
                        carpeta.envioDeCliente();  //ENVIANDO CARPETAS  //Aquí va la de msj2
                        cl.cl.close();
                        System.out.println();

                        break;

                    case 3:
                        CEnviaCarpeta cEliminaFile = new CEnviaCarpeta();
                        System.out.println(cEliminaFile.getRuta());
                        String ruta = cEliminaFile.getRuta();   //Obteniendo la ruta donde están guardados en este caso están en la carpeta "Nube"        cl.eliminarArchivo(ruta);
                        cEliminaFile.eliminarArchivo(ruta);
                        cl.cl.close();
                        System.out.println();

                        break;

                    case 4:
                        CEnviaCarpeta cEliminaCarpeta = new CEnviaCarpeta();
                        System.out.println(cEliminaCarpeta.getRuta());
                        String ruta2 = cEliminaCarpeta.getRuta();   //Obteniendo la ruta donde están guardados en este caso están en la carpeta "Nube"        cl.eliminarArchivo(ruta);
                        cEliminaCarpeta.eliminaCarpeta(ruta2);
                        cl.cl.close();
                        System.out.println();

                        break;

                    case 5:
                        ServDescargas servDes = new ServDescargas();
                        servDes.envioDeCliente();
                        servDes.cl.close();
                        System.out.println("");
                        break;
                    case 6:
                        CDescargaFile cdCarpeta = new CDescargaFile();
                        cdCarpeta.envioDeCliente();  //ENVIANDO CARPETAS
                        cdCarpeta.cl.close();
                        System.out.println("");
                        break;

                    case 7:
                        cl.cl.close();
                        break;
                    default:

                        System.out.println("Opción incorrecta, intente de nuevo.");

                }
            }

        } catch (IOException ex) {
            Logger.getLogger(MainClie.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
