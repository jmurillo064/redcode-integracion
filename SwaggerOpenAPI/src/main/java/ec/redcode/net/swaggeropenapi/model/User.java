package ec.redcode.net.swaggeropenapi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Modelo de datos para Usuario.
 *
 * @author Jorge Murillo
 * @version 1.0
 */
@Schema(description = "Modelo de datos del Usuario")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Schema(description = "Identificador único del usuario",
            example = "1",
            required = false)
    private Long id;

    @Schema(description = "Nombre completo del usuario",
            example = "Juan Pérez",
            required = true,
            minLength = 2,
            maxLength = 100)
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100)
    private String name;

    @Schema(description = "Correo electrónico del usuario",
            example = "juan.perez@email.com",
            required = true)
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @Schema(description = "Edad del usuario",
            example = "25",
            minimum = "18",
            maximum = "120")
    private Integer age;

    @Schema(description = "Estado del usuario",
            example = "ACTIVE",
            allowableValues = {"ACTIVE", "INACTIVE", "SUSPENDED"})
    private String status;

    /**
     * Constructor con parámetros
     *
     * @param name nombre del usuario
     * @param email email del usuario
     * @param age edad del usuario
     */
    public User(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.status = "ACTIVE";
    }
}
