package ec.redcode.net.service.impl;

import ec.redcode.net.dto.UserDto;
import ec.redcode.net.service.KeycloackService;
import ec.redcode.net.util.KeycloackProviderUtil;
import jakarta.ws.rs.core.Response;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class KeycloackServiceImpl implements KeycloackService {

    /***
     * Metodo para listar todos los usuarios de keycloack.
     *
     * @return List<UserRepresentation>
     */
    @Override
    public List<UserRepresentation> findAllUsers() {
        return KeycloackProviderUtil
                .getRealmResource()
                .users()
                .list();
    }

    /***
     * Metodo para buscar un usuario por username en keycloack.
     * Aunque sea un usuario, keycloack lo retorna como lista.
     *
     * @return List<UserRepresentation>
     */
    @Override
    public List<UserRepresentation> searchUserByUsername(String username) {
        return KeycloackProviderUtil
                .getRealmResource()
                .users()
                .searchByUsername(username, true);
    }

    /***
     * Metodo para crear un usuario nuevo para keycloack.
     *
     * @return List<UserRepresentation>
     */
    @Override
    public String createUser(@NonNull UserDto userDto) {
        int status = 0;
        UsersResource usersResource = KeycloackProviderUtil.getUserResource();
        UserRepresentation newUser = new UserRepresentation();
        newUser.setFirstName(userDto.firstName());
        newUser.setLastName(userDto.lastName());
        newUser.setEmail(userDto.email());
        newUser.setUsername(userDto.username());
        newUser.setEmailVerified(true);
        newUser.setEnabled(true);
        Response response = usersResource.create(newUser);
        status = response.getStatus();

        if (201 == status) {
            String path = response.getLocation().getPath();
            String userId = path.substring(path.lastIndexOf('/') + 1);
            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setTemporary(false);
            credentialRepresentation.setType(OAuth2Constants.PASSWORD);
            credentialRepresentation.setValue(userDto.password());
            usersResource.get(userId).resetPassword(credentialRepresentation);

            RealmResource realmResource = KeycloackProviderUtil.getRealmResource();
            List<RoleRepresentation> roles = null;

            if (null == userDto.roles() || userDto.roles().isEmpty()) {
                roles = List.of(realmResource.roles().get("user").toRepresentation());
            } else {
                roles = realmResource.roles()
                        .list()
                        .stream()
                        .filter(role ->
                                userDto.roles()
                                        .stream()
                                        .anyMatch(roleName ->
                                                roleName.equalsIgnoreCase(role.getName()))
                        ).toList();
            }

            realmResource.users().get(userId).roles().realmLevel().add(roles);

        } else if (409 == status) {
            log.warn("User {} already exists", userDto.username());
            return "User ".concat(userDto.username()).concat(" already exists");
        } else {
            log.warn("Error creating user {}", userDto.username());
            return "User ".concat(userDto.username()).concat(" no created, please contact the administrator");
        }

        return "User created successfully";
    }

    /***
     * Metodo para eliminar un usuario en keycloack.
     *
     */
    @Override
    public void deleteUser(String userId) {
        KeycloackProviderUtil.getUserResource()
                .get(userId)
                .remove();

    }

    /***
     * Metodo para actualizar un usuario en keycloack.
     *
     */
    @Override
    public void updateUser(String userId, @NonNull UserDto userDto) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(OAuth2Constants.PASSWORD);
        credentialRepresentation.setValue(userDto.password());

        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername(userDto.username());
        newUser.setFirstName(userDto.firstName());
        newUser.setLastName(userDto.lastName());
        newUser.setEmail(userDto.email());
        newUser.setEmailVerified(true);
        newUser.setEnabled(true);
        newUser.setCredentials(Collections.singletonList(credentialRepresentation));

        UserResource usersResource = KeycloackProviderUtil.getUserResource().get(userId);
        usersResource.update(newUser);

    }
}
