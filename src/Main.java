import java.util.ArrayList;
import javafx.util.Pair;

public class Main {
    public static void main(String[] args){
        //Para la primera entrega pediremos desde aquí los tokens
        //al lexer y los pasaremos a un archivo de texto
        ArrayList<Token> tokens = new ArrayList<Token>();

        if (args.length > 0) {
            Lexer lexer = new Lexer(args[0]);
        } else {
            System.out.println("No se ha pasado un archivo a compilar.");
        }
    }   
}
