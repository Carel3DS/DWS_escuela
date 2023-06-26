package es.dws.escuela.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import es.dws.escuela.entities.Department;
import es.dws.escuela.entities.Groups;
import es.dws.escuela.entities.Views;
import es.dws.escuela.services.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DepartmentRESTController {

    @Autowired
    private DepartmentService service;

    //REST DEPARTMENT
    @PostMapping("/department")
    @JsonView(Views.Department.class)
    public ResponseEntity<Department> post(@RequestBody Department department){
        return new ResponseEntity<>(service.create(department),HttpStatus.CREATED);
    }
    @GetMapping("/department")
    @JsonView(Views.Department.class)
    public ResponseEntity<List<Department>> getAllDepartments(){
        return new ResponseEntity<>(service.readAll(), HttpStatus.OK);
    }

    @GetMapping("/department/{id}")
    @JsonView(Views.Department.class)
    public ResponseEntity<Department> get(@PathVariable Long id){
        Department department = service.read(id);
        if (department != null){
            return new ResponseEntity<>(department, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/department/{id}")
    @JsonView(Views.Department.class)
    public ResponseEntity<Department> put(@PathVariable Long id, @RequestBody @Validated(Groups.DepartmentGroup.class) Department newDept){
        Department newDepartment = service.update(id, newDept);
        if (newDepartment != null){
            return new ResponseEntity<>(newDepartment, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/department/{id}")
    @JsonView(Views.Department.class)
    public ResponseEntity<Department> delete(@PathVariable Long id){
        Department department = service.delete(id);
        if (department != null){
            return new ResponseEntity<>(department, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
