package utb.fai.RESTAPIServer;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/search")
    public List<MyUser> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber) {

        return userRepository.findAll().stream()
                .filter(user -> name == null || user.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(user -> email == null || user.getEmail().toLowerCase().contains(email.toLowerCase()))
                .filter(user -> phoneNumber == null || user.getPhoneNumber().contains(phoneNumber))
                .toList();
    }

    @PostMapping
    public ResponseEntity<MyUser> createUser(@RequestBody MyUser user) {
        if (!user.isUserDataValid()) {
            return ResponseEntity.badRequest().build();
        }

        MyUser savedUser = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MyUser> updateUser(@PathVariable Long id, @RequestBody MyUser updatedUser) {
        if (!updatedUser.isUserDataValid()) {
            return ResponseEntity.badRequest().build();
        }

        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setName(updatedUser.getName());
                    existingUser.setEmail(updatedUser.getEmail());
                    existingUser.setPhoneNumber(updatedUser.getPhoneNumber());

                    MyUser savedUser = userRepository.save(existingUser);
                    return ResponseEntity.ok(savedUser);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MyUser> deleteUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public ResponseEntity<List<MyUser>> deleteAllUsers() {
        userRepository.deleteAll();
        return ResponseEntity.ok(List.of());
    }
}