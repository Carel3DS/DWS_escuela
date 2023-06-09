package es.dws.escuela.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import es.dws.escuela.entities.Groups;
import es.dws.escuela.entities.Teacher;
import es.dws.escuela.entities.User;
import es.dws.escuela.entities.Views;
import es.dws.escuela.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserRESTController {

    @Autowired
    private UserService service;
    @Autowired
    private PasswordEncoder passwordEncoder;

    //REST USER
    @PostMapping("/user")
    @JsonView(Views.User.class)
    public ResponseEntity<User> post(@RequestBody @Validated(Groups.UserGroup.class) User user){
        user = new User(user.getName(),user.getSurname(),user.getPass(), user.getDescription());
        return new ResponseEntity<>(service.create(user),HttpStatus.CREATED);
    }
    @GetMapping("/user")
    @JsonView(Views.User.class)
    public ResponseEntity<List<User>> getAllUsers(){
        return new ResponseEntity<>(service.readAll(), HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    @JsonView(Views.User.class)
    public ResponseEntity<User> get(@PathVariable String id){
        User user = service.read(id);
        if (user != null){
            return new ResponseEntity<>(user, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/user/{id}")
    @JsonView(Views.User.class)
    public ResponseEntity<User> put(@PathVariable String id, @RequestBody @Validated(Groups.UserOptGroup.class) User user){
        User newUser = new User(user.getName(),user.getSurname(), user.getPass());
        newUser.setDescription(user.getDescription());
        newUser = service.update(id, user);
        if (newUser != null){
            return new ResponseEntity<>(newUser, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/user/{id}")
    @JsonView(Views.User.class)
    public ResponseEntity<User> delete(@PathVariable String id){
        User user = service.delete(id);
        if (user != null){
            return new ResponseEntity<>(user, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
}
