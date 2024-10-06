import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

public class GestorCinePrincipal{
    //Declaración estática de las matrices responsables de los ficheros y de Scanner para facilitar su uso a lo largo de todo el programa
    static Scanner sc;
    static String[][][] butacas = new String[][][] {leerFichero("\t", "sala1.txt"), leerFichero("\t", "sala2.txt"), leerFichero("\t", "sala3.txt")};
    static String[][] peliculas = leerFichero("\t", "peliculas.txt");
    static String[][] comida = leerFichero("\t", "comida.txt");

    public static void main(String[] args) {
        System.out.println("\nBienvenido a nuestro cine por favor escoga que quiere hacer a continuación");
        sc = new Scanner(System.in);
        menuModo();
        guardadoGeneral();
        sc.close();
        System.out.println("\nVuelva pronto");
    }

    /**
     * Inicialización del programa con un menú para acceder a las tres partes fundamentales del programa
     */
    public static void menuModo() {
        System.out.println("\nSi quiere acceder a COMPRAS pulse 1 y para DEVOLUCIONES pulse 2, siempre que desee volver atrás pulse 0.");
        int eleccion = sc.nextInt();
        switch(eleccion){
            case 1:
                System.out.println("\nUsted ha seleccionado COMPRAS, espere mientras le redirigimos a nuestro catálogo de películas");
                menuComprar();
            break;
            case 2:
                System.out.println("\nUsted ha seleccionado DEVOLUCIONES, ahora mismo le mostraremos el gestor de devoluciones");
                menuDevolucion();
            break;
            case 9999:
                System.out.println("\nBienvenido ADMIN, espere un momento");
                menuAdmin();
            break;
            case 0:
                System.out.println("\nMuchas gracias por visitarnos");
            break;
            default:
                System.out.println("\nPor favor escoga un valor correcto");
                menuModo();
        }
    }

    /**
     * Switch que nos permite escoger que peli queremos ver
     */
    public static void menuComprar() {
        System.out.println("\nPara " + peliculas[0][0].replace("_", " ") + " pulse 1, para " + peliculas[0][1].replace("_", " ") + " pulse 2 y para " + peliculas[0][2].replace("_", " ") + " pulse 3.");
        int eleccion = sc.nextInt();
        switch(eleccion){
            case 1:
                System.out.println("\nUsted ha seleccionado " + peliculas[0][0].replace("_", " "));
                seleccionHora(butacas[0]);
            break;
            case 2:
                System.out.println("\nUsted ha seleccionado " + peliculas[0][1].replace("_", " "));
                seleccionHora(butacas[1]);
            break;
            case 3:
                System.out.println("\nUsted ha seleccionado " + peliculas[0][2].replace("_", " "));
                seleccionHora(butacas[2]);
            break;
            case 0:
            menuModo();
            break;
            default:
                System.out.println("\nPor favor escoga una película válida");
                menuComprar();
        }
    }
    
    /**
     * Switch con el que indicamos a que sesión se quiere acudir, es una función a parte para facilitar la lectura de las funciones
     */
    public static void seleccionHora(String[][] sala) {
        System.out.println("\nLas sesiones disponibles son: (1)16:00, (2)19:00, (3)22:00, cual desea escoger?");
        int eleccion = sc.nextInt();
        switch(eleccion){
            case 1:
                System.out.println("\nHa seleccionado la sesión de las 16:00");
                seleccionCompra(sala, "1");
            break;
            case 2:
                System.out.println("\nHa seleccionado la sesión de las 19:00");
                seleccionCompra(sala, "2");
            break;
            case 3:
                System.out.println("\nHa seleccionado la sesión de las 22:00");
                seleccionCompra(sala, "3");
            break;
            case 0:
            menuComprar();
            break;
            default:
                System.out.println("\nPor favor escoga una sesión válida");
                seleccionHora(sala);
        }
    }

    /**
     * Este método será la convergencia de aquellos que nos permiten tanto introducir tanto la reserva de los asientos en el array correspondiente como redirigir si es necesario al cliente a menuComida
     * @param sala Array al que se le va a hacer el cambio
     * @param sesion Elemento a introducir
     */
    public static void seleccionCompra(String[][] sala, String sesion) {
        System.out.println("\nCuantas butacas quiere?");
        int tickets = sc.nextInt();
        int[][] butacas = seleccionButacas(sala, tickets, sesion);
        int[] fila = butacas[0], numero = butacas[1];
        añadirButacas(sala, fila, numero, sesion);
        System.out.println("Muchas gracias por su compra, el subtotal serían " + tickets*7.25 + " desea algo para picar? Pulse 1, sino pulse cualquier otra tecla.");
        if(sc.nextInt() == 1)
            menuComida();
        else
            System.out.println("Que disfrute de la película");
        menuModo();
    }

    /**
     * Función que nos permite preguntar al usuario donde quiere sus butacas en la sala
     * @param sala Matriz a cambiar
     * @param entradas numero de cambios que se le van a hacer
     * @param sesion cambio que se le va a hacer
     * @return matriz de 2xn numero de butacas escogidas, en la cual se registran la fila y columna de la butaca escogida
     */
    public static int[][] seleccionButacas(String[][] sala, int entradas, String sesion){
        int[][] res = new int [2][entradas];
        mostrarButacas(sala, "|", sesion);
        System.out.println("\nPor favor escoja las butacas escribiendo la fila, pulsando ENTER, y después el numero de butaca.");
        for(int i=0; i<entradas; i++){
            do{
            res[0][i] = sc.nextInt();
            res[1][i] = sc.nextInt();
            String visibilidad = (estaButacasLibre(sala, res[0], res[1],  sesion)) ? "" : ("\nLa butaca " + res[0][i] + "," + res[1][i] + " esta ocupada, por favor escoja otra\n");
            System.out.print(visibilidad);
            }while(!estaButacasLibre(sala, res[0], res[1], sesion));
            if(i < (entradas-1))
            System.out.println("Por favor proceda con la siguiente");
        }
        //FIXME: Recorres el array de filas y columnas de las butacas seleccionadas muchas veces, OPTIMIZE
        return res;
    }

    /**
     * Metodo que nos permite mostrar la disposición de las butacas al usuario, junto con los datos necesarios(fila y columna) para que pueda hacer la reserva
     * @param datos Matriz de butacas a tratar
     * @param separadorColumnas Elemento estético para separar las butacas entre sí
     */
    public static void mostrarButacas(String[][] datos, String separadorColumnas, String sesion){
        for (int i = 0; i < datos.length; i++) {
            String[] fila = datos[i];
                for (int j = 0; j < 5; j++) {
                    String visibilidad = (estaButacasLibre(datos, new int[] {i}, new int[] {j},  sesion)) ? String.format(" %d,%d ", i, j) : "  ,  ";
                    System.out.print(visibilidad);
                    if(j < 4)
                    System.out.print(separadorColumnas);
                }
                System.out.print("  " + separadorColumnas + "  ");
                for (int j = 5; j < fila.length; j++) {
                    String visibilidad = (estaButacasLibre(datos, new int[] {i}, new int[] {j},  sesion)) ? String.format(" %d,%d ", i, j) : "  ,  ";
                    System.out.print(visibilidad);
                    if(j < (fila.length-1))
                    System.out.print(separadorColumnas);
                }
                System.out.println();
        }
    }

    /**
     * Método que agrega las nuevas compras a la matriz de la sala
     * @param datos Matriz de la sala
     * @param fila Filas correspondientes a las butacas a reservar
     * @param columna Columnas correspondientes a las butacas a reservar
     * @param sesion Número que se introducirá en la matriz
     */
    public static void añadirButacas(String[][] datos, int[] fila, int[] columna, String sesion){
        if(estaButacasLibre(datos, fila, columna, sesion)){
            for (int i = 0; i < fila.length; i++) {
                datos[fila[i]][columna[i]] = (datos[fila[i]][columna[i]].contains("0")) ? sesion : datos[fila[i]][columna[i]].concat(sesion);
            }    
        }
        else
        System.out.println("\nPor favor escoga una respuesta válida");
    }

    /**
     * Función booleana que nos permite determinar si las reservas que nos han hecho se pueden hacer en verdad
     * @param datos Matriz de la sala
     * @param fila Filas correspondientes a las butacas a comprobar
     * @param columna Columnas correspondientes a las butacas a comprobar
     * @param sesion Número que se comprobará en la matriz
     * @return true si están todas libres, false si no
     */
    public static boolean estaButacasLibre(String[][] datos, int[] fila, int[] columna, String sesion){
        boolean res = true;
        for (int i = 0; i < fila.length; i++) {
            if(datos[fila[i]][columna[i]].contains(sesion))
            res = false;
        } 
        return res;   
    }
    
    /**
     * Switch que nos permite comprar lo que el usuario desee de comida gracias al bucle do{}while()
     */
    public static void menuComida() {
        System.out.println("\nNuestras opciones de comida son: (1)refresco por 2€, (2) palomitas por 3€, (3) nachos por 5€, (4) combo palomitas y refresco por 4€ y (5) combo nachos y refresco por 6€");
        int eleccion = 0;
        int cantidad;
        boolean seguirComprando = false;
        do{
            eleccion = sc.nextInt();
            switch(eleccion){
                case 1:
                    System.out.println("\nUsted ha seleccionado refresco, cuantos quiere?");
                    cantidad = sc.nextInt();
                    añadirComida(comida, "1", cantidad);
                break;
                case 2:
                    System.out.println("\nUsted ha seleccionado palomitas, cuantos quiere?");
                    cantidad = sc.nextInt();
                    añadirComida(comida, "2", cantidad);
                break;
                case 3:
                    System.out.println("\nUsted ha seleccionado nachos, cuantos quiere?");
                    cantidad = sc.nextInt();
                    añadirComida(comida, "3", cantidad);
                break;
                case 4:
                    System.out.println("\nUsted ha seleccionado combo palomitas, cuantos quiere?");
                    cantidad = sc.nextInt();
                    añadirComida(comida, "4", cantidad);
                break;
                case 5:
                    System.out.println("\nUsted ha seleccionado combo nachos, cuantos quiere?");
                    cantidad = sc.nextInt();
                    añadirComida(comida, "5", cantidad);
                break;
                case 0:
                System.out.println("Que disfrute de la película");
                break;
                default:
                    System.out.println("\nPor favor escoga un combo válida");
            }
            System.out.println("\nSi quiere seguir comprando comida pulse 1");
            seguirComprando = (sc.nextInt() == 1) ? true : false;
        }while(seguirComprando);
        System.out.println("Que disfrute de la película");
        menuModo();
    }

    /**
     *  Método que agrega las nuevas compras al fichero de la comida
     * @param datos Matriz de la comida
     * @param producto Elemento a agregar al fichero
     * @param cantidad Cuantas repeticiones del elemento queremos agregar
     */
    public static void añadirComida(String[][] datos, String producto, int cantidad) {
        String[][] res = datos;
        for(int i = 0; i<cantidad; i++){
            res[0][0] = res[0][0].concat(producto);
        }
    }

    /**
     * Switch que nos permite acceder a las distintas opciones de devolución, pidiendo valores para los casos que necesiten 
     */
    public static void menuDevolucion() {
        System.out.println("\nPor favor especifique que desea devolver, para entradas pulse 1, para comida pulse 2.");
        int eleccion = 0;
        String[][] sala = butacas[0];
        int tickets;
        String sesion = "";
        boolean seguirDevolviendo = false;
        do{
            eleccion = sc.nextInt();
            switch(eleccion){
                case 1:
                    System.out.println("\nUsted ha seleccionado entradas, por favor especifique la sala, sesión y cantidad en orden de las mismas.");
                    sala = butacas[sc.nextInt()-1];
                    switch(sc.next()){
                        case "16:00":
                        case "1":
                            sesion = "1";
                        break;
                        case "19:00":
                        case "2":
                            sesion = "2";;
                        break;
                        case "22:00":
                        case "3":
                            sesion = "3";
                        break;
                        default:
                        System.out.println("\nPor favor escoga una sesión válida");
                        menuDevolucion();
                    }
                    tickets = sc.nextInt();
                    int[][] butacas = seleccionButacasDevolver(sala, tickets, sesion);
                    int[] fila = butacas[0], numero = butacas[1];
                    devolverButacas(sala, fila, numero, sesion);
                break;
                case 2:
                    System.out.println("\nUsted ha seleccionado comida, por favor especifique el numero producto a devolver y la cantidad del mismo");
                    String producto = sc.next();
                    int cantidad = sc.nextInt();
                    devolverComida(comida, producto, cantidad);
                break;
                case 0:
                    menuModo();
                break;
                default:
                    System.out.println("\nPor favor escoga una acción válida");
                    menuDevolucion();
            }
            System.out.println("\nSi necesita devolver más cosas pulse 1");
                seguirDevolviendo = (sc.nextInt() == 1) ? true : false;
        }while(seguirDevolviendo);
        System.out.println("\nMuchas gracias por confiar en nosotros");
        menuModo();
    }

    /**
     * Función que nos permite preguntar al usuario donde quiere sus butacas en la sala
     * @param sala Matriz a cambiar
     * @param entradas numero de cambios que se le van a hacer
     * @param sesion cambio que se le va a hacer
     * @return matriz de 2xn numero de butacas escogidas, en la cual se registran la fila y columna de la butaca escogida
     */
    public static int[][] seleccionButacasDevolver(String[][] sala, int entradas, String sesion){
        int[][] res = new int [2][entradas];
        mostrarButacas(sala, "|", sesion);
        System.out.println("\nPor favor escoja las butacas escribiendo la fila, pulsando ENTER, y después el numero de butaca.");
        for(int i=0; i<entradas; i++){
            do{
            res[0][i] = sc.nextInt();
            res[1][i] = sc.nextInt();
            String visibilidad = (!estaButacasLibre(sala, res[0], res[1],  sesion)) ? "" : ("\nLa butaca " + res[0][i] + "," + res[1][i] + " no esta ocupada, por favor escoja su butaca\n");
            System.out.print(visibilidad);
            }while(estaButacasLibre(sala, res[0], res[1], sesion));
            if(i < (entradas-1))
            System.out.println("Por favor proceda con la siguiente");
        }
        return res;
    }

    /**
     * Método el cuál elimina la reserva de una de las butacas de la sesion definida
     * @param datos Matriz de la sala
     * @param fila Filas correspondientes a las butacas a comprobar
     * @param columna Columnas correspondientes a las butacas a comprobar
     * @param sesion Número que se eliminará de la matriz
     */
    public static void devolverButacas(String[][] datos, int[] fila, int[] columna, String sesion){
        if(!estaButacasLibre(datos, fila, columna, sesion)){
            for (int i = 0; i < fila.length; i++) {
                    datos[fila[i]][columna[i]] = (datos[fila[i]][columna[i]].length() == 1) ? datos[fila[i]][columna[i]].replace(sesion, "0") : datos[fila[i]][columna[i]].replace(sesion, "");
            }    
        }
        else{
        System.out.println("\nPor favor escoga una respuesta válida");
        menuDevolucion();
        }
    }

    /**
     * Método
     * @param datos Matriz de la comida
     * @param producto Elemento a eliminar del fichero
     * @param cantidad Cuantas repeticiones del elemento queremos eliminar
     */
    public static void devolverComida(String[][] datos, String producto, int cantidad) {
        for(int i = 0; i<cantidad; i++){
            if(datos[0][0].contains(producto))
            datos[0][0] = datos[0][0].replaceFirst(producto,"");
            else
            System.out.println("\nNo hay más de " + (i+1) + " compras de este producto, por favor especifíque correctamente su compra");
        }
    }

    /**
     * Switch que nos permite acceder a las distintas opciones que tiene el administrador, definiendo tambien a lgunos datos auxiliares para ello
     */
    public static void menuAdmin() {
        int[] precios = new int[] {2,3,5,4,6};
        int res = 0;
        int eleccion = 0;
        boolean mantenerIniciado = false;
        do{
            System.out.println("\nPara comprobar el recuento del día pulse 1, para resetear las ventas de una sala pulse 2, para cambiar la cartelera pulse 3 y para forzar un guardado pulse 4");
            eleccion = sc.nextInt();
            switch(eleccion){
                case 1:
                    res = contadorTotal(precios, res);
                break;
                case 2:
                    System.out.println("\nEscoja la sala a resetear");
                    int sala = (sc.nextInt()-1);
                    for (int i = 0; i < butacas[sala].length; i++) {
                        for (int j = 0; j < butacas[sala][i].length; j++) {
                            butacas[sala][i][j] = (butacas[sala][i][j].contains("0")) ? butacas[sala][i][j] : "0";
                        }
                    }
                break;
                case 3:
                    for(int i=0; i < peliculas[0].length ;i++){
                        System.out.println("\nQue pelicula desea poner para la sala " + (i+1) + ", recuerde que los espacios deben ser sustituidos por _");
                        peliculas[0][i] = sc.next();
                    }
                    System.out.println("\nSe ha actualizado la cartelera correctamente");
                break;
                case 4:
                    guardadoGeneral();
                    System.out.println("\nSe ha guardado correctamente");
                break;
                default:
                    System.out.println("\nPor favor escoga una acción válida");
            }
            System.out.println("\nSi quiere mantener la sesión pulse 1");
            mantenerIniciado = (sc.nextInt() == 1) ? true : false;
        }while(mantenerIniciado);
    }

    /**
     * Método que muestra por pantalla las ventas generadas tanto en catidad como en dinero recaudado
     * @param precios Array de precios de la comida
     * @param res Variable inicializada previamente encargada de permitir representar el dinero de las comidas por pantalla
     */
    public static int contadorTotal(int[] precios, int res){
        boolean mantenerIniciado = false;
        do{
            System.out.println("\nPara ver el recuento de entradas pulse 1, para el recuento de comida pulse 2 y para el total pulse 3");
            switch(sc.nextInt()){
                case 1:
                    System.out.println("\nEl total de entradas vendidas fueron " + contadorEntradas() + ", sumando en total " + (contadorEntradas()*7.25) + " euros");
                break;
                case 2:
                    int[] contador = contadorComida(comida);
                    res = 0;
                    for(int i=0; i<contador.length; i++){
                        System.out.println("\nEl total de (" + (i+1) + ") vendidos fueron " + contador[i] + ", sumando " + contador[i]*precios[i] + " euros");
                        res += contador[i]*precios[i];
                    }
                    System.out.println("\nSumando un total de " + res + " euros");
                break;
                case 3:
                    System.out.println("\nLa caja del día de hoy ha sido " + (res+(contadorEntradas()*7.25)) + " euros");
                break;
                case 0:
                    menuAdmin();
                break;
                default:
                    System.out.println("\nEscoja una opcion válida");
                    contadorTotal(precios, res);
            }
            System.out.println("\nSi quiere seguir en el recuento pulse 1");
            mantenerIniciado = (sc.nextInt() == 1) ? true : false;
        }while(mantenerIniciado);
        return res;
    }

    /**
     * Función auxiliar encargada de contar el numero de reservas entodas las salas y sesiones
     * @return numero de reservas
     */
    public static int contadorEntradas() {
        int res = 0;
        for (int i = 0; i < butacas.length; i++) {
            for (int j = 0; j < butacas[i].length; j++) {
                for (int k = 0; k < butacas[i][j].length; k++) {
                    res = (butacas[i][j][k].charAt(0) != '0') ? butacas[i][j][k].length() + res : res;
                }
            }
        }
        return res;
    }

    /**
     * Función auxiliar encargada de recoger cuantos combos hay de cada tipo
     * @return Array ordenado de la cantidad de cada producto
     */
    public static int[] contadorComida(String[][] comida) {
        int[] res = new int [5];
        for (int i = 0; i < comida[0][0].length(); i++) {
            switch(comida[0][0].charAt(i)){
                case '1':
                    res[0] ++;
                break;
                case '2':
                    res[1] ++;
                break;
                case '3':
                    res[2] ++;
                break;
                case '4':
                    res[3] ++;
                break;
                case '5':
                    res[4] ++;
                break;
                default:
                    System.out.println("\nEl fichero de comida está corrupto, reviselo y vuelva a intentarlo");
                    menuAdmin();
            }
        }
        return res;
    }

    /**
     * Método que llama a guardarEnFichero para todas nuestras matrices asociadas a ficheros
     */
    public static void guardadoGeneral(){
        String[] salas = new String[] {"sala1.txt", "sala2.txt", "sala3.txt"};
        for(int i=0; i<butacas.length ;i++){
        guardarEnFichero(butacas[i], "\t", salas[i]);
        }
        guardarEnFichero(comida, "\t", "comida.txt");
        guardarEnFichero(peliculas, "\t", "peliculas.txt");
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