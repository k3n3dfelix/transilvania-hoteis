package grupo1.services;

import grupo1.configs.auth.AuthRequest;
import grupo1.configs.auth.AuthService;
import grupo1.configs.auth.AuthenticationResponse;
import grupo1.configs.auth.RegisterRequest;
import grupo1.configs.jwt.JwtService;
import grupo1.entities.Role;
import grupo1.entities.User;
import grupo1.repositories.IRoleRepository;
import grupo1.repositories.IUserRepository;
import grupo1.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserServiceImpl userService;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IUserRepository userRepository;
    @Mock
    private IRoleRepository roleRepository;

    private User existingUser = new User();
    private Role existingRole = new Role();

    @BeforeEach
    void setUp() {
        existingRole.setNome("ROLE");
        roleRepository.save(existingRole);

        existingUser.setNome("USER NOME");
        existingUser.setSobrenome("USER SOBRENOME");
        existingUser.setEmail("user@gmail.com");
        existingUser.setSenha("brasil@123");
        existingUser.addRole(existingRole);
        userRepository.save(existingUser);
    }

    @Test
    void testRegisterUser() {
        Set<String> roles = new HashSet<>();
        roles.add(existingRole.getNome());

        RegisterRequest request = new RegisterRequest();
        request.setNome("Jane");
        request.setSobrenome("Doe");
        request.setEmail("jane.doe@example.com");
        request.setSenha("password");
        request.setFuncoes(roles);

        when(userRepository.findByEmail(existingUser.getEmail())).thenReturn(Optional.of(existingUser));
        AuthenticationResponse response = authService.register(request);
        assertTrue(response.getToken().isEmpty());
    }

    @Test
    void testAuthUser() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("user@gmail.com");
        authRequest.setSenha("brasil@123");

        AuthenticationResponse response = authService.auth(authRequest);
        assertTrue(response.getToken().isEmpty());
    }

}
