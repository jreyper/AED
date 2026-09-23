package ies.puerto;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class FicheroImpl implements IFicheros{
    @Override
    public boolean existe(Path path) {
        if (path == null){
            return false;
        }

        File file = new File(path.toFile().getAbsolutePath());
        if (file.exists()){
            return true;
        }
        return false;

        // Otra forma

//        if (Files.exists(path)){
//            return true;
//        }
//        return false;
    }
}
