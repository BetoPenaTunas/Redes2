package menu;

import java.io.IOException;
import java.net.ServerSocket;

public class Servidor8001 {
    public static void main(String[] args) throws IOException {
        ServerSocket s= new ServerSocket(8002);
        s.setReuseAddress(true);
        System.out.println("Sevidor 8002 iniciado");
        for (;;) {
            
        }
        
        
        
    }
}
