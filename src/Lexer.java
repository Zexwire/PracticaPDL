import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

public class Lexer{

    public Lexer(String string) {
        //TODO Auto-generated constructor stub
    }

    public static void guardarEnFichero(String[][] datos, String separadorColumnas, String nombreFichero){
        try {
            FileWriter writer = new FileWriter(nombreFichero);
                for (int i = 0; i < datos.length; i++) {
                String[] fila = datos[i];
                    for (int j = 0; j < fila.length; j++) {
                        writer.append(fila[j]);
                        if(j < (fila.length-1))
                            writer.append(separadorColumnas);
                    }
                    writer.append(System.lineSeparator());
                }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
      
    public static String[][] leerFichero(String separadorColumnas, String nombreFichero){
    List<String[]> list = new ArrayList<>();
    try {
        Scanner scanner = new Scanner(new File(nombreFichero));
        String line = "";
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            String[] array = line.split(separadorColumnas);
            list.add(array);
        }
        scanner.close();
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }
    String[][] res = new String[list.size()][];
    for (int i=0;i<res.length;i++){
        res[i] = list.get(i);
    }
    return res;
    }
}