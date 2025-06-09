package ec.redcode.net.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class TestRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Configuración general de REST

        restConfiguration()
                .component("servlet")
                .host("localhost")
                .port(8080)
                .contextPath("/camel/api/")
                .bindingMode(RestBindingMode.json)
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "API SpringBootCamelRestKeyclock")
                .apiProperty("api.version", "1.0.0")
                .apiProperty("api.description", "API REST para validar integracion de spring boot - camel rest - keycloack")
                .apiProperty("api.contact.name", "Equipo de Desarrollo")
                .apiProperty("api.contact.email", "jorge.murillo@redcode.net.ec")
                .apiHost("localhost:8080")
                .apiProperty("base.path", "/camel/api/")
                .apiProperty("api.specification.contentType", "application/json")
                .apiProperty("securityDefinitions.bearerAuth.type", "http")
                .apiProperty("securityDefinitions.bearerAuth.scheme", "bearer")
                .apiProperty("securityDefinitions.bearerAuth.bearerFormat", "JWT")
                .apiProperty("securityRequirement", "bearerAuth");
        ;

        rest("/productos")
                .description("Operaciones de productos")
                .get()
                .description("Obtener lista de productos")
                .responseMessage().code(200).message("Lista de productos obtenida exitosamente").endResponseMessage()
                .responseMessage().code(401).message("No authorizado").endResponseMessage()
                .security("bearerAuth")
                .to("direct:productos");

        from("direct:productos")
                .log("Procesando solicitud de productos")
                .setHeader("Content-Type", constant("application/json"))
                .setBody(constant("[{'id':1,'nombre':'Producto1','precio':100.00}]"));

    }

}
