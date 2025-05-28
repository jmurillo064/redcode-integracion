package ec.redcode.net;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.dsl.yaml.YamlRoutesBuilderLoader;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.spi.Resource;
import org.apache.camel.spi.RestConfiguration;
import org.apache.camel.spi.RoutesBuilderLoader;
import org.apache.camel.support.ResourceHelper;

/**
 * Hello world!
 */
@Slf4j
public class App {
    public static void main(String[] args) {
        try (CamelContext context = new DefaultCamelContext()) {
            // Configurar REST con netty-http
            RestConfiguration restConfig = new RestConfiguration();
            restConfig.setComponent("netty-http");
            restConfig.setHost("localhost");
            restConfig.setPort(8080);
            restConfig.setBindingMode(String.valueOf(RestBindingMode.json));
            context.setRestConfiguration(restConfig);

            // Registrar loader YAML
            try (RoutesBuilderLoader yamlLoader = new YamlRoutesBuilderLoader()) {
                yamlLoader.setCamelContext(context);
                yamlLoader.start(); // Muy importante para inicializar el loader

                // Cargar rutas desde archivo YAML
                Resource yamlResource = ResourceHelper.resolveResource(context, "route/RouteOne.yaml");
                context.addRoutes(yamlLoader.loadRoutesBuilder(yamlResource));
            } catch (Exception e) {
                log.error("Error al cargar rutas desde archivo YAML", e);
            }
            context.start();
            log.info("Camel context iniciado.");
            Thread.sleep(Long.MAX_VALUE); // Mantener la app viva
        }
        catch (Exception e) {
            log.error("Error al iniciar el contexto de Apache Camel", e);
        }
    }
}
