package ec.redcode.net.camelspringbootrest.service;

import ec.redcode.net.camelspringbootrest.model.Producto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Servicio que gestiona las operaciones CRUD de productos.
 * Utiliza un almacenamiento en memoria para simplicidad.
 *
 * @author Jorge Murillo
 * @version 1.0.0
 */
@Service
public class ProductoService {

    private final Map<Long, Producto> productos = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

    public ProductoService() {
        // Datos de ejemplo
        crearProducto(new Producto(null, "Laptop Dell", "Laptop empresarial 15 pulgadas", 899.99, 10));
        crearProducto(new Producto(null, "Mouse Logitech", "Mouse inalámbrico ergonómico", 29.99, 50));
        crearProducto(new Producto(null, "Teclado Mecánico", "Teclado gaming RGB", 79.99, 25));
    }

    /**
     * Obtiene todos los productos disponibles.
     *
     * @return Lista de todos los productos
     */
    public List<Producto> obtenerTodos() {
        return new ArrayList<>(productos.values());
    }

    /**
     * Busca un producto por su ID.
     *
     * @param id Identificador del producto
     * @return Optional con el producto si existe
     */
    public Optional<Producto> buscarPorId(Long id) {
        return Optional.ofNullable(productos.get(id));
    }

    /**
     * Crea un nuevo producto.
     *
     * @param producto Datos del producto a crear
     * @return Producto creado con ID asignado
     */
    public Producto crearProducto(Producto producto) {
        Long id = idGenerator.incrementAndGet();
        producto.setId(id);
        productos.put(id, producto);
        return producto;
    }

    /**
     * Actualiza un producto existente.
     *
     * @param id Identificador del producto
     * @param producto Nuevos datos del producto
     * @return Optional con el producto actualizado
     */
    public Optional<Producto> actualizarProducto(Long id, Producto producto) {
        if (productos.containsKey(id)) {
            producto.setId(id);
            productos.put(id, producto);
            return Optional.of(producto);
        }
        return Optional.empty();
    }

    /**
     * Elimina un producto por su ID.
     *
     * @param id Identificador del producto
     * @return true si se eliminó, false si no existía
     */
    public boolean eliminarProducto(Long id) {
        return productos.remove(id) != null;
    }
}
