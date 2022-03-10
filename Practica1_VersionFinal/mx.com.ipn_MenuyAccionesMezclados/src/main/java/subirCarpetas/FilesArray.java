package subirCarpetas;

import java.io.File;
import java.io.Serializable;
public class FilesArray implements Serializable {
    private File files[];
    private int arraySize;
    private String pathDeGuardoDeArchivos;


    public FilesArray() {
    }
    
    public FilesArray(File[] files,int arraySize) {
        this.files = files;
        this.arraySize=arraySize;
    }

    public int getArraySize() {
        return arraySize;
    }

    public void setArraySize(int arraySize) {
        this.arraySize = arraySize;
    }
    
    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    public String getPathDeGuardoDeArchivos() {
        return pathDeGuardoDeArchivos;
    }

    public void setPathDeGuardoDeArchivos(String pathDeGuardoDeArchivos) {
        this.pathDeGuardoDeArchivos = pathDeGuardoDeArchivos;
    }

   
}
