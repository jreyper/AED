package ies.puerto;
import org.apache.commons.csv.CSVFormat;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class LectorRecursos extends FicheroImpl{
    static void main() {
        Path path = Path.of("resources", "csv.txt");
        String ruta = path.toAbsolutePath().toString();
        System.out.println("Ruta del fichero: "+ruta);

        //Los dos verifican si existe el fichero (file y url)
        File file = new File(ruta);
        if (file.exists()){
            System.out.println("El fichero existe");
        }else{
            System.out.println("El fichero no existe");
        }
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String linea = reader.readLine();
            String[] valores = linea.split(",");
            System.out.println(valores[1]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Reader in = null;
        try {
            Reader in = new FileReader(path.toAbsolutePath().toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .setSkipHeaderRecord(true)
                .build();
        Iterable<CSVRecord> records = csvFormat.parse(in);

//        //La otra forma
//        URL url = LectorRecursos.class.getClassLoader().getResource("archivo.txt");
//        System.out.println("Path dentro de resource: "+ url.getPath().toString());
    }
}
