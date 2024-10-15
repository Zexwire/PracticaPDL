import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Prueba {

	public static void main(String[] args) throws IOException {
		int sL = 0;
		BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\sangu\\eclipse-workspace3º\\PracticaPDL\\src\\prueba.txt"));
		ArrayList<Character> chars = new ArrayList<>();
		int c = reader.read();
		while(c!=-1) {
			chars.add((char)c);
		}
		for(char ch: chars) {
			System.out.print(ch);
		}
	}
	
}
