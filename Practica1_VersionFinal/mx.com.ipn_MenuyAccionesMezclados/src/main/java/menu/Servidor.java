package menu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {

    private int pto = 8000;
    public  Socket cl;
    public  ServerSocket s;
    private int conexiones = 0;

    public void inicializaServerSocket() {

        try {
            s = new ServerSocket(pto);
            s.setReuseAddress(true);
            System.out.println("Servidor iniciado...");
            
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int aceptaConexionYEleccion() throws IOException{
        cl=s.accept();
        DataInputStream dis= new DataInputStream(cl.getInputStream());
        int choice=dis.readInt();
        dis.close();
        return choice;
    }
    
    public  void enviaMsg() {
        try {
            cl=s.accept();
            System.out.println(cl);
            DataOutputStream dos = new DataOutputStream(this.cl.getOutputStream());
            dos.writeUTF("Mensaje del server 1");
            dos.flush();
            dos.close();

        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void enviaMsg2() {
        try {
            cl = s.accept();
            System.out.println(cl);
            DataOutputStream dos = new DataOutputStream(this.cl.getOutputStream());
            dos.writeUTF("Mensaje del server 2");
            dos.flush();
            dos.close();
            this.conexiones++;

        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
