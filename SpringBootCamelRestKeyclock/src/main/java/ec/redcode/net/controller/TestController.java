package ec.redcode.net.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test", description = "API para validar la configuracion de acceso mediante para user y admin")
public class TestController {

    @GetMapping("/hello-admin")
    @PreAuthorize("hasRole('admin_client_role')")
    @Operation(
            summary = "Validar endpoint para Admin",
            description = "Retorna un saludo solo para usuarios administradores"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saludo exitoso para admin"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido o faltante"),
            @ApiResponse(responseCode = "403", description = "Prohibido - No tiene rol de administrador")
    })
    public String helloAdmin() {
        return "Hello Admin - Spring Boot with keycloak";
    }

    @GetMapping("/hello-user")
    @PreAuthorize("hasRole('user_client_role') or hasRole('admin_client_role')")
    @Operation(
            summary = "Validar endpoint para User",
            description = "Retorna un saludo solo para usuarios usuarios / administradores"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saludo exitoso para usuario"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido o faltante"),
            @ApiResponse(responseCode = "403", description = "Prohibido - No tiene rol de usuario o administrador")
    })
    public String helloUser() {
        return "Hello User - Spring Boot with keycloak";
    }

    @GetMapping("/public")
    @Operation(
            summary = "Endpoint Público",
            description = "Endpoint público que no requiere autenticación"
    )
    @ApiResponse(responseCode = "200", description = "Respuesta pública exitosa")
    public String publicEndpoint() {
        return "Public endpoint - No authentication required";
    }

}
