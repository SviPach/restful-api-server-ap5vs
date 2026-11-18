package utb.fai.RESTAPIServer;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;

import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {
    private final MyUserRepository userRepository;

    public UserController(MyUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<MyUser> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MyUser> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MyUser> createUser(@RequestBody MyUser user) {
        if (!user.isUserDataValid()) {
            return ResponseEntity.badRequest().build();
        }

        MyUser savedUser = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
}