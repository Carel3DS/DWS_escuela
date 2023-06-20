package es.dws.escuela.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import es.dws.escuela.entities.User;
import es.dws.escuela.entities.Views;
import es.dws.escuela.services.UserService;
import es.dws.escuela.valids.ValidCreateUser;
import es.dws.escuela.valids.ValidUser;
import jakarta.validation.Valid;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<User> post(@RequestBody @Valid ValidCreateUser vcuser){
        User user = new User(vcuser);
        user.setPass(passwordEncoder.encode(user.getPass()));
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
    public ResponseEntity<User> put(@PathVariable String id, @RequestBody @Valid ValidUser user){
        User newUser = service.update(id, user);
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

    //Self-delete
    @DeleteMapping("/user")
    @JsonView(Views.User.class)
    public ResponseEntity<User> deleteSelf(){
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        var role = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        //Ensure the user exists and it's not a teacher
        if(id != null && role.contains(new SimpleGrantedAuthority("ROLE_USER")) && !role.contains(new SimpleGrantedAuthority("ROLE_TEACHER"))){
            User user = service.delete(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

}
