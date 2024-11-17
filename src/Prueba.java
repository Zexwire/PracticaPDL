public class Prueba {
	public static void main(String[] args) {
		String line = "0,,d13,,d14,d5,d11,,d10,d12,,d7,d6,,,,,d9,,,,,,,,,,,,,,,d4,1,3,,,,,,,,2,,,,8,,,,,,,,,";
		String[] parts = line.split(",", -1);

		System.out.println("parts.length = " + parts.length);
		for (int i = 0; i < parts.length; i++) {
			System.out.println("parts[" + i + "] = \"" + parts[i] + "\"");
		}
	}
}
