    package com.example.taskmanagement_backend.controllers;
    import com.example.taskmanagement_backend.dtos.UserDto.CreateUserRequestDto;
    import com.example.taskmanagement_backend.dtos.UserDto.UpdateUserRequestDto;
    import com.example.taskmanagement_backend.dtos.UserDto.UserResponseDto;
    import com.example.taskmanagement_backend.entities.User;
    import com.example.taskmanagement_backend.services.UserService;
    import jakarta.validation.Valid;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/api/users")
    public class UserController {

        @Autowired
        private UserService userService;

        @Autowired
        public UserController(UserService userService) {
            this.userService = userService;
        }
        @PostMapping
        public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody CreateUserRequestDto dto) {
            return ResponseEntity.ok(userService.createUser(dto));
        }

        @PutMapping("/{id}")
        public ResponseEntity<UserResponseDto> updateUser(@PathVariable Integer id, @RequestBody UpdateUserRequestDto dto) {
            return ResponseEntity.ok(userService.updateUser(id, dto));
        }
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
            userService.deleteUser(Math.toIntExact(id));
            return ResponseEntity.noContent().build();
        }
        @GetMapping("/{id}")
        public ResponseEntity<UserResponseDto> getUserById(@PathVariable Integer id) {
            return ResponseEntity.ok(userService.getUserById(id));
        }
    //    @PutMapping("/{id}")
    //    public User updateUser(@PathVariable Integer id, @Valid @RequestBody UpdateUserRequestDto dto) {
    //        return userService.updateUser(id, dto);
    //    }

//        @GetMapping("/{id}")
//        public UserResponseDto getUserById(@PathVariable Integer id) {
//            return userService.getUserById(id);
//        }

        @GetMapping("/by-email")
        public ResponseEntity<UserResponseDto> getUserByEmail(@RequestParam("email") String email) {
            return ResponseEntity.ok(userService.getUserByEmail(email));
        }
        @GetMapping
        public ResponseEntity<List<UserResponseDto>> getAllUsers() {
            return ResponseEntity.ok(userService.getAllUsers());
        }
    }
