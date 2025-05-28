package ec.redcode.net.camelspringbootrest.route;

import ec.redcode.net.camelspringbootrest.model.Producto;
import ec.redcode.net.camelspringbootrest.service.ProductoService;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.apache.camel.model.rest.RestParamType.path;

/**
 * Configuración de las rutas REST usando Apache Camel.
 * Define los endpoints de la API y su documentación Swagger.
 *
 * @author Tu nombre
 * @version 1.0.0
 */
@Component
public class ProductoRestRoute extends RouteBuilder {

    @Autowired
    private ProductoService productoService;

    @Override
    public void configure() throws Exception {
// Configuración general de REST
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .enableCORS(true)

                // Configuración de Swagger
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "API de Gestión de Productos")
                .apiProperty("api.version", "1.0.0")
                .apiProperty("api.description", "API REST para gestionar productos usando Apache Camel")
                .apiProperty("api.contact.name", "Equipo de Desarrollo")
                .apiProperty("api.contact.email", "desarrollo@ejemplo.com");

        // Definición de endpoints REST
        rest("/productos")
                .description("Servicios para gestión de productos")
                .consumes("application/json")
                .produces("application/json")

                // GET /productos - Obtener todos los productos
                .get()
                .description("Obtiene la lista completa de productos")
                .responseMessage()
                .code(200)
                .message("Lista de productos obtenida exitosamente")
                .endResponseMessage()
                .to("direct:obtenerProductos")

                // GET /productos/{id} - Obtener producto por ID
                .get("/{id}")
                .description("Obtiene un producto específico por su ID")
                .param()
                .name("id")
                .type(path)
                .description("ID del producto")
                .dataType("long")
                .required(true)
                .endParam()
                .responseMessage()
                .code(200)
                .message("Producto encontrado")
                .endResponseMessage()
                .responseMessage()
                .code(404)
                .message("Producto no encontrado")
                .endResponseMessage()
                .to("direct:obtenerProductoPorId")

                // POST /productos - Crear nuevo producto
                .post()
                .description("Crea un nuevo producto")
                .type(Producto.class)
                .responseMessage()
                .code(201)
                .message("Producto creado exitosamente")
                .endResponseMessage()
                .responseMessage()
                .code(400)
                .message("Datos inválidos")
                .endResponseMessage()
                .to("direct:crearProducto")

                // PUT /productos/{id} - Actualizar producto
                .put("/{id}")
                .description("Actualiza un producto existente")
                .param()
                .name("id")
                .type(path)
                .description("ID del producto a actualizar")
                .dataType("long")
                .required(true)
                .endParam()
                .type(Producto.class)
                .responseMessage()
                .code(200)
                .message("Producto actualizado exitosamente")
                .endResponseMessage()
                .responseMessage()
                .code(404)
                .message("Producto no encontrado")
                .endResponseMessage()
                .to("direct:actualizarProducto")

                // DELETE /productos/{id} - Eliminar producto
                .delete("/{id}")
                .description("Elimina un producto")
                .param()
                .name("id")
                .type(path)
                .description("ID del producto a eliminar")
                .dataType("long")
                .required(true)
                .endParam()
                .responseMessage()
                .code(204)
                .message("Producto eliminado exitosamente")
                .endResponseMessage()
                .responseMessage()
                .code(404)
                .message("Producto no encontrado")
                .endResponseMessage()
                .to("direct:eliminarProducto");

        // Implementación de las rutas

        from("direct:obtenerProductos")
                .routeId("obtener-todos-productos")
                .log("Obteniendo todos los productos")
                .bean(productoService, "obtenerTodos");

        from("direct:obtenerProductoPorId")
                .routeId("obtener-producto-por-id")
                .log("Buscando producto con ID: ${header.id}")
                .bean(productoService, "buscarPorId(${header.id})")
                .choice()
                .when(body().isNull())
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
                .setBody(constant("{\"error\": \"Producto no encontrado\"}"))
                .otherwise()
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));

        from("direct:crearProducto")
                .routeId("crear-producto")
                .log("Creando nuevo producto: ${body}")
                .bean(productoService, "crearProducto")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201));

        from("direct:actualizarProducto")
                .routeId("actualizar-producto")
                .log("Actualizando producto con ID: ${header.id}")
                .bean(productoService, "actualizarProducto(${header.id}, ${body})")
                .choice()
                .when(body().isNull())
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
                .setBody(constant("{\"error\": \"Producto no encontrado\"}"))
                .otherwise()
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));

        from("direct:eliminarProducto")
                .routeId("eliminar-producto")
                .log("Eliminando producto con ID: ${header.id}")
                .bean(productoService, "eliminarProducto(${header.id})")
                .choice()
                .when(body().isEqualTo(true))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(204))
                .setBody(constant(""))
                .otherwise()
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
                .setBody(constant("{\"error\": \"Producto no encontrado\"}"));
    }
}
