import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Lee los archivos generados por GenerateInfoFiles y crea los reportes:
 * <ul>
 * <li>reporte_vendedores.csv: vendedores ordenados por dinero recaudado.</li>
 * <li>reporte_productos.csv: productos ordenados por cantidad vendida.</li>
 * </ul>
 * Si un vendedor tiene varios archivos de ventas, se suman todos.
 * Las ventas con productos que no existen o cantidades negativas se ignoran.
 *
 * @author Julian David Gutierrez Forero
 * @author Santiago Garcia Castañeda
 * @author Sharol Ochoa
 * @version 1.0
 */
public class main {

    /** Nombre de cada producto, la llave es el id. */
    static HashMap<String, String> nombreProducto = new HashMap<>();

    /** Precio de cada producto, la llave es el id. */
    static HashMap<String, Double> precioProducto = new HashMap<>();

    /** Nombre completo de cada vendedor, la llave es "TipoDocumento;Numero". */
    static HashMap<String, String> nombreVendedor = new HashMap<>();

    /** Dinero recaudado por cada vendedor. */
    static HashMap<String, Double> dineroVendedor = new HashMap<>();

    /** Cantidad vendida de cada producto. */
    static HashMap<String, Integer> cantidadVendida = new HashMap<>();

    /**
     * Método principal. Lee los archivos y genera los reportes.
     *
     * @param args no se usan
     */
    public static void main(String[] args) {
        try {
            leerProductos();
            leerVendedores();
            leerVentas();
            crearReporteVendedores();
            crearReporteProductos();
            System.out.println("Los reportes se generaron correctamente.");
        } catch (Exception e) {
            System.out.println("Error al generar los reportes: " + e.getMessage());
        }
    }

    /**
     * Lee el archivo productos.txt y guarda el nombre y precio de cada producto.
     *
     * @throws IOException si no se puede leer el archivo
     */
    public static void leerProductos() throws IOException {
        BufferedReader lector = new BufferedReader(new FileReader("productos.txt"));
        String linea;

        while ((linea = lector.readLine()) != null) {
            String[] datos = linea.split(";");
            if (datos.length != 3) {
                System.out.println("Advertencia: línea mal escrita en productos.txt: " + linea);
                continue;
            }
            double precio = Double.parseDouble(datos[2]);
            if (precio < 0) {
                System.out.println("Advertencia: precio negativo en el producto " + datos[0]);
                continue;
            }
            nombreProducto.put(datos[0], datos[1]);
            precioProducto.put(datos[0], precio);
            cantidadVendida.put(datos[0], 0);
        }

        lector.close();
    }

    /**
     * Lee el archivo vendedores.txt y guarda el nombre completo de cada vendedor.
     *
     * @throws IOException si no se puede leer el archivo
     */
    public static void leerVendedores() throws IOException {
        BufferedReader lector = new BufferedReader(new FileReader("vendedores.txt"));
        String linea;

        while ((linea = lector.readLine()) != null) {
            String[] datos = linea.split(";");
            if (datos.length != 4) {
                System.out.println("Advertencia: línea mal escrita en vendedores.txt: " + linea);
                continue;
            }
            String llave = datos[0] + ";" + datos[1];
            nombreVendedor.put(llave, datos[2] + " " + datos[3]);
            dineroVendedor.put(llave, 0.0);
        }

        lector.close();
    }

    /**
     * Busca todos los archivos de ventas en la carpeta del proyecto
     * y suma el dinero y las cantidades vendidas.
     *
     * @throws IOException si no se puede leer algún archivo
     */
    public static void leerVentas() throws IOException {
        File carpeta = new File(".");
        File[] archivos = carpeta.listFiles();
        if (archivos == null) {
            throw new IOException("No se pudo leer la carpeta del proyecto.");
        }

        for (File archivo : archivos) {
            if (archivo.getName().startsWith("ventas_") && archivo.getName().endsWith(".txt")) {
                leerArchivoVentas(archivo);
            }
        }
    }

    /**
     * Lee un archivo de ventas de un vendedor.
     *
     * @param archivo archivo de ventas
     * @throws IOException si no se puede leer el archivo
     */
    public static void leerArchivoVentas(File archivo) throws IOException {
        BufferedReader lector = new BufferedReader(new FileReader(archivo));
        String vendedor = lector.readLine(); // primera línea: TipoDocumento;Numero

        if (vendedor == null || !nombreVendedor.containsKey(vendedor)) {
            System.out.println("Advertencia: el archivo " + archivo.getName()
                    + " es de un vendedor que no existe.");
            lector.close();
            return;
        }

        String linea;
        while ((linea = lector.readLine()) != null) {
            String[] datos = linea.split(";");
            if (datos.length != 2) {
                System.out.println("Advertencia: línea mal escrita en " + archivo.getName() + ": " + linea);
                continue;
            }

            String idProducto = datos[0];
            if (!precioProducto.containsKey(idProducto)) {
                System.out.println("Advertencia: el producto " + idProducto + " no existe ("
                        + archivo.getName() + ")");
                continue;
            }

            int cantidad;
            try {
                cantidad = Integer.parseInt(datos[1]);
            } catch (NumberFormatException e) {
                System.out.println("Advertencia: cantidad no válida en " + archivo.getName() + ": " + linea);
                continue;
            }
            if (cantidad <= 0) {
                System.out.println("Advertencia: cantidad negativa o cero en " + archivo.getName());
                continue;
            }

            double valorVenta = precioProducto.get(idProducto) * cantidad;
            dineroVendedor.put(vendedor, dineroVendedor.get(vendedor) + valorVenta);
            cantidadVendida.put(idProducto, cantidadVendida.get(idProducto) + cantidad);
        }

        lector.close();
    }

    /**
     * Crea el archivo reporte_vendedores.csv con el formato NombreVendedor;Dinero,
     * ordenado de mayor a menor dinero recaudado.
     *
     * @throws IOException si no se puede escribir el archivo
     */
    public static void crearReporteVendedores() throws IOException {
        ArrayList<String> vendedores = new ArrayList<>(dineroVendedor.keySet());
        vendedores.sort((a, b) -> Double.compare(dineroVendedor.get(b), dineroVendedor.get(a)));

        BufferedWriter escritor = new BufferedWriter(new FileWriter("reporte_vendedores.csv"));
        for (String vendedor : vendedores) {
            String dinero = String.format("%.0f", dineroVendedor.get(vendedor));
            escritor.write(nombreVendedor.get(vendedor) + ";" + dinero);
            escritor.newLine();
        }
        escritor.close();
    }

    /**
     * Crea el archivo reporte_productos.csv con el formato NombreProducto;Precio;Cantidad,
     * ordenado de mayor a menor cantidad vendida. Solo incluye productos vendidos.
     *
     * @throws IOException si no se puede escribir el archivo
     */
    public static void crearReporteProductos() throws IOException {
        ArrayList<String> productos = new ArrayList<>(cantidadVendida.keySet());
        productos.sort((a, b) -> cantidadVendida.get(b) - cantidadVendida.get(a));

        BufferedWriter escritor = new BufferedWriter(new FileWriter("reporte_productos.csv"));
        for (String id : productos) {
            if (cantidadVendida.get(id) > 0) {
                String precio = String.format("%.0f", precioProducto.get(id));
                escritor.write(nombreProducto.get(id) + ";" + precio + ";" + cantidadVendida.get(id));
                escritor.newLine();
            }
        }
        escritor.close();
    }
}
