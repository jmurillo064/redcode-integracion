package ec.redcode.net.controler;

import ec.redcode.net.dto.UserDto;
import ec.redcode.net.service.KeycloackService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/v1/keycloack/users")
@PreAuthorize("hasRole('admin_client_role')")
public class KeycloackControler {

    private final KeycloackService keycloackService;

    KeycloackControler(KeycloackService keycloackService) {
        this.keycloackService = keycloackService;
    }

    @GetMapping
    public ResponseEntity<?> findAllUsers() {
        return ResponseEntity.ok(keycloackService.findAllUsers());
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> findAllUsers(@PathVariable String username) {
        return ResponseEntity.ok(keycloackService.searchUserByUsername(username));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto) throws URISyntaxException {
        String result = keycloackService.createUser(userDto);
        return ResponseEntity.created(new URI("/api/v1/keycloack/user")).body(result);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @RequestBody UserDto userDto) {
        keycloackService.updateUser(userId, userDto);
        return ResponseEntity.ok("Successfully updated user");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        keycloackService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

}
