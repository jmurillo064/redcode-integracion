package ec.redcode.net.springcamelgateway.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class HttpErrorProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        String message = cause.getMessage();
        int statusCode = 500;
        String errorMessage = "Error interno";
        String description = "Error desconocido";
        if (message.contains("statusCode: 403")) {
            statusCode = 403;
            errorMessage = "Acceso denegado";
            description = "No tienes permisos para acceder a este recurso";
        } else if (message.contains("statusCode: 401")) {
            statusCode = 401;
            errorMessage = "No autorizado";
            description = "Token de autenticación inválido o expirado";
        } else if (message.contains("statusCode: 404")) {
            statusCode = 404;
            errorMessage = "Recurso no encontrado";
            description = "El servicio solicitado no existe";
        } else if (message.contains("statusCode: 500")) {
            statusCode = 502;
            errorMessage = "Error del servicio remoto";
            description = "El servicio destino está experimentando problemas";
        }

        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, statusCode);
        exchange.getIn().setHeader("Content-Type", "application/json");

        String jsonResponse = String.format(
                "{\"error\": \"%s\", \"codigo\": %d, \"mensaje\": \"%s\", \"timestamp\": \"%s\"}",
                errorMessage, statusCode, description, java.time.LocalDateTime.now()
        );
        exchange.getIn().setBody(jsonResponse);
    }
}
