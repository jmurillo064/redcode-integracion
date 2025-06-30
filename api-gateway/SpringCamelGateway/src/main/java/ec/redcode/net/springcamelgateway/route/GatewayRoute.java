package ec.redcode.net.springcamelgateway.route;

import ec.redcode.net.springcamelgateway.processor.HttpErrorProcessor;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GatewayRoute extends RouteBuilder {

    private final HttpErrorProcessor httpErrorProcessor;

    @Override
    public void configure() throws Exception {

        onException(Exception.class)
                .handled(true)
                .log("Error manejado: ${exception.message}")
                .process(httpErrorProcessor)
                .log("Error HTTP manejado - Código: ${header.CamelHttpResponseCode}, Mensaje: ${body}");

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
                .apiProperty("base.path", "/camel/api/");

        rest("/test")
                .description("Rutas de test para servicio gateway")
                .get()
                .to("direct:test");

        rest("/gateway")
                .get("/data")
                .param().name("tipo").type(RestParamType.query).description("Tipo de consulta").dataType("string").endParam()
                .description("Consulta gateway según tipo")
                .to("direct:enrutamiento");

        from("direct:test")
                .log("Procesando solicitud de prueba")
                .setHeader("Content-Type", constant("application/json"))
                .setBody(constant("{'Resultado':'OK','Mensaje':'Prueba satisfactorias'}"));

        from("direct:enrutamiento")
                .routeId("gateway-enrutamiento")
                .log("Nueva solicitud al gateway con tipo: ${header.tipo}")
                .setProperty("startTime", simple("${date:now:yyyy-MM-dd HH:mm:ss.SSS}"))
                .choice()
                .when(header("tipo").isEqualTo("usuarios"))
                    .to("direct:call-usuarios")
                .when(header("tipo").isEqualTo("clientes"))
                    .to("direct:call-clientes")
                .when(header("tipo").isEqualTo("user-camel"))
                    .to("direct:call-user-camel")
                .when(header("tipo").isEqualTo("admin-camel"))
                    .to("direct:call-admin-camel")
                .otherwise()
                    .log("Tipo desconocido: ${header.tipo}")
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                    .setHeader("Content-Type", constant("application/json"))
                    .setBody().constant("{\"error\": \"Tipo no soportado\"}")
                .end()
                .log("Inicio: ${exchangeProperty.startTime}, Fin: ${date:now:yyyy-MM-dd HH:mm:ss.SSS}")
                .log("Respuesta al cliente: ${body}");

        from("direct:call-usuarios")
                .routeId("call-usuarios-service")
                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader("Accept", constant("application/json"))
                .to("http://localhost:8081/api/usuarios?bridgeEndpoint=true")
                .convertBodyTo(String.class)
                .setHeader("Content-Type", constant("application/json"));

        from("direct:call-clientes")
                .routeId("call-clientes-service")
                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader("Accept", constant("application/json"))
                .to("http://localhost:8081/api/clientes?bridgeEndpoint=true")
                .convertBodyTo(String.class)
                .setHeader("Content-Type", constant("application/json"));

        from("direct:call-user-camel")
                .routeId("call-user-camel-service")
                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader("Accept", constant("application/json"))
                .setHeader("Authorization", header("Authorization"))
                .to("http://localhost:8082/camel/api/user-camel?throwExceptionOnFailure=true")
                .convertBodyTo(String.class)
                .setHeader("Content-Type", constant("application/json"));

        from("direct:call-admin-camel")
                .routeId("call-admin-camel-service")
                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader("Accept", constant("application/json"))
                .setHeader("Authorization", header("Authorization"))
                .to("http://localhost:8082/camel/api/admin-camel?throwExceptionOnFailure=true")
                .convertBodyTo(String.class)
                .setHeader("Content-Type", constant("application/json"));

    }
}
