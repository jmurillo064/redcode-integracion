package ec.redcode.net.camelspringbootrest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un producto en el sistema.
 *
 * @author Jorge Murillo
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    /** Identificador único del producto */
    private Long id;

    /** Nombre del producto */
    private String nombre;

    /** Descripción detallada del producto */
    private String descripcion;

    /** Precio del producto en euros */
    private Double precio;

    /** Cantidad disponible en stock */
    private Integer stock;
}
