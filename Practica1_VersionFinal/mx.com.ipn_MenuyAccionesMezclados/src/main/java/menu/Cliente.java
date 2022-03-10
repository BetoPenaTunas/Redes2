package menu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente {
    
    public Socket cl;
    
    
    public void conectaConServer(int choice){
        try {
            this.cl=new Socket("localhost",8000);   //Se conectó el Socket
            System.out.println("Conexion con el servidor establecida.");
            System.out.println(cl);
            System.out.println("Enviando elección...");
            DataOutputStream dos= new DataOutputStream(cl.getOutputStream());
            dos.writeInt(choice);
            dos.flush();
            dos.close();
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void recibeMsg(){
        try {
            this.cl=new Socket("localhost",8000);   //Se conectó el Socket
            System.out.println("Conexion con el servidor establecida.");
            DataInputStream dis=new DataInputStream(cl.getInputStream());
            String msg=dis.readUTF();
            System.out.println(msg);
            dis.close();
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void recibeMsg2(){
        try {
            this.cl=new Socket("localhost",8000);   //Se conectó el Socket
            System.out.println("Conexion con el servidor establecida.");
            DataInputStream dis=new DataInputStream(cl.getInputStream());
            String msg=dis.readUTF();
            dis.close();
            System.out.println(msg);
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
