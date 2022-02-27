package main;
import java.io.IOException;
import sockets.SRecibe;

public class MainServidor {
    
    public static void main(String[] args) throws IOException {
        
        SRecibe s1=new SRecibe();
//        s1.recibeServidor();  //recibiendo carpetas 
        s1.daRuta();
        
        
    }
    
}
