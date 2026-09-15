import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Genera los archivos planos de prueba que usa la clase main.
 * <p>
 * Crea el archivo de productos, el archivo de vendedores y un archivo
 * de ventas por cada vendedor (algunos vendedores quedan con dos archivos).
 * Todos los archivos se guardan en la carpeta del proyecto.
 *
 * @author Julian David Gutierrez Forero
 * @author Satiago Garcia Castañeda
 * @author Sharol Ochoa
 * @version 1.0
 */
public class GenerateInfoFiles {

    /** Nombres reales para generar los vendedores. */
    static String[] nombres = {"Juan", "Carlos", "Pedro", "Luis", "Santiago",
        "Camila", "Laura", "Ana", "Paula", "Valentina"};

    /** Apellidos reales para generar los vendedores. */
    static String[] apellidos = {"Rodríguez", "Gómez", "González", "Martínez", "García",
        "López", "Hernández", "Pérez", "Díaz", "Moreno"};

    /** Nombres de los productos. */
    static String[] nombresProductos = {"Arroz", "Aceite", "Azúcar", "Café", "Leche",
        "Huevos", "Pan", "Chocolate", "Queso", "Frijol"};

    /** Generador de números aleatorios. */
    static Random random = new Random();

    /** Cantidad de productos generados, para crear ventas con ids que sí existen. */
    static int totalProductos = 0;

    /**
     * Método principal. Genera todos los archivos de prueba.
     *
     * @param args no se usan
     */
    public static void main(String[] args) {
        try {
            borrarVentasAnteriores();
            createProductsFile(10);
            createSalesManInfoFile(8);
            System.out.println("Los archivos se generaron correctamente.");
        } catch (IOException e) {
            System.out.println("Error al generar los archivos: " + e.getMessage());
        }
    }

    /**
     * Crea el archivo productos.txt con información aleatoria.
     * Cada línea tiene el formato IdProducto;NombreProducto;Precio
     *
     * @param productsCount cantidad de productos que se van a crear
     * @throws IOException si hay un problema al escribir el archivo
     */
    public static void createProductsFile(int productsCount) throws IOException {
        BufferedWriter escritor = new BufferedWriter(new FileWriter("productos.txt"));

        for (int i = 1; i <= productsCount; i++) {
            String nombre = nombresProductos[(i - 1) % nombresProductos.length];
            // si se piden más productos que nombres, se le agrega un número
            if (i > nombresProductos.length) {
                nombre = nombre + " " + i;
            }
            int precio = (random.nextInt(50) + 1) * 1000; // entre 1.000 y 50.000

            escritor.write("P" + i + ";" + nombre + ";" + precio);
            escritor.newLine();
        }

        escritor.close();
        totalProductos = productsCount;
    }

    /**
     * Crea el archivo vendedores.txt con información aleatoria y, además,
     * los archivos de ventas de cada vendedor.
     * Cada línea tiene el formato TipoDocumento;NumeroDocumento;Nombres;Apellidos
     *
     * @param salesmanCount cantidad de vendedores que se van a crear
     * @throws IOException si hay un problema al escribir los archivos
     */
    public static void createSalesManInfoFile(int salesmanCount) throws IOException {
        BufferedWriter escritor = new BufferedWriter(new FileWriter("vendedores.txt"));

        for (int i = 0; i < salesmanCount; i++) {
            long documento = 10000000 + random.nextInt(90000000);
            String nombre = nombres[random.nextInt(nombres.length)];
            String apellido = apellidos[random.nextInt(apellidos.length)] + " "
                    + apellidos[random.nextInt(apellidos.length)];

            escritor.write("CC;" + documento + ";" + nombre + ";" + apellido);
            escritor.newLine();

            // cada vendedor tiene al menos un archivo de ventas
            createSalesMenFile(random.nextInt(10) + 5, nombre, documento);

            // algunos vendedores tienen un segundo archivo de ventas
            if (random.nextBoolean()) {
                createSalesMenFile(random.nextInt(10) + 5, nombre, documento);
            }
        }

        escritor.close();
    }

    /**
     * Crea un archivo de ventas aleatorias para un vendedor.
     * La primera línea es TipoDocumento;NumeroDocumento y las demás
     * tienen el formato IdProducto;Cantidad;
     *
     * @param randomSalesCount cantidad de ventas que tendrá el archivo
     * @param name             nombre del vendedor (se usa en el nombre del archivo)
     * @param id               número de documento del vendedor
     * @throws IOException si hay un problema al escribir el archivo
     */
    public static void createSalesMenFile(int randomSalesCount, String name, long id) throws IOException {
        // si el vendedor ya tiene un archivo, el nuevo se numera (_2, _3, ...)
        String nombreBase = "ventas_" + name + "_" + id;
        File archivo = new File(nombreBase + ".txt");
        int numero = 2;
        while (archivo.exists()) {
            archivo = new File(nombreBase + "_" + numero + ".txt");
            numero++;
        }

        BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo));
        escritor.write("CC;" + id);
        escritor.newLine();

        for (int i = 0; i < randomSalesCount; i++) {
            int idProducto = random.nextInt(totalProductos) + 1;
            int cantidad = random.nextInt(10) + 1;
            escritor.write("P" + idProducto + ";" + cantidad + ";");
            escritor.newLine();
        }

        escritor.close();
    }

    /**
     * Borra los archivos de ventas de ejecuciones anteriores para que
     * no se mezclen con los nuevos.
     */
    public static void borrarVentasAnteriores() {
        File carpeta = new File(".");
        File[] archivos = carpeta.listFiles();
        if (archivos == null) {
            return;
        }
        for (File archivo : archivos) {
            if (archivo.getName().startsWith("ventas_") && archivo.getName().endsWith(".txt")) {
                archivo.delete();
            }
        }
    }
}
