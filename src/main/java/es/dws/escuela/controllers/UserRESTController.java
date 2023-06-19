package es.dws.escuela.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import es.dws.escuela.entities.User;
import es.dws.escuela.entities.Views;
import es.dws.escuela.services.DepartmentService;
import es.dws.escuela.services.GradeService;
import es.dws.escuela.services.UserService;
import es.dws.escuela.valids.ValidUser;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserRESTController {

    @Autowired
    private UserService service;
    @Autowired
    private GradeService gradeService;

    //REST USER
    @PostMapping("/user")
    @JsonView(Views.User.class)
    public ResponseEntity<User> post(@RequestBody User user){
        user.setId(user.getName().toLowerCase().replace(" ","")+"."+user.getSurname().toLowerCase());
        user.setEmail(user.getId()+"@urdj.es");
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

}
