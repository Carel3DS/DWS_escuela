package es.dws.escuela.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import es.dws.escuela.entities.Teacher;
import es.dws.escuela.entities.Views;
import es.dws.escuela.services.DepartmentService;
import es.dws.escuela.services.GradeService;
import es.dws.escuela.services.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TeacherRESTController {

    @Autowired
    private TeacherService service;
    @Autowired
    private GradeService gradeService;
    @Autowired
    private DepartmentService departmentService;
    //REST TEACHER
    @PostMapping("/teacher")
    @JsonView(Views.Teacher.class)
    public ResponseEntity<Teacher> post(@RequestBody Teacher teacher){
        return new ResponseEntity<>(service.create(teacher),HttpStatus.CREATED);
    }
    @GetMapping("/teacher")
    @JsonView(Views.Teacher.class)
    public ResponseEntity<List<Teacher>> getAllTeachers(){
        return new ResponseEntity<>(service.readAll(), HttpStatus.OK);
    }

    @GetMapping("/teacher/{id}")
    @JsonView(Views.Teacher.class)
    public ResponseEntity<Teacher> get(@RequestParam String id){
        Teacher teacher = service.read(id);
        if (teacher != null){
            return new ResponseEntity<>(teacher, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/teacher/{id}")
    @JsonView(Views.Teacher.class)
    public ResponseEntity<Teacher> put(@RequestParam String id, @RequestBody Teacher teacher){
        Teacher newTeacher = service.update(id, teacher);
        if (newTeacher != null){
            return new ResponseEntity<>(newTeacher, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/teacher/{id}")
    @JsonView(Views.Teacher.class)
    public ResponseEntity<Teacher> delete(@RequestParam String id){
        Teacher teacher = service.delete(id);
        if (teacher != null){
            return new ResponseEntity<>(teacher, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
