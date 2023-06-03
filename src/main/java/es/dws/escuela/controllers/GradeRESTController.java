package es.dws.escuela.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import es.dws.escuela.entities.Grade;
import es.dws.escuela.entities.Views;
import es.dws.escuela.services.GradeService;
import es.dws.escuela.services.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GradeRESTController {

    @Autowired
    private GradeService service;
    @Autowired
    private TeacherService teacherService;
    //REST GRADE
    @PostMapping("/grade")
    @JsonView(Views.Grade.class)
    public ResponseEntity<Grade> post(@RequestBody Grade grade){
        return new ResponseEntity<>(service.create(grade),HttpStatus.CREATED);
    }
    @GetMapping("/grade")
    @JsonView(Views.Grade.class)
    public ResponseEntity<List<Grade>> getAllGrades(){
        return new ResponseEntity<>(service.readAll(), HttpStatus.OK);
    }

    @GetMapping("/grade/{id}")
    @JsonView(Views.Grade.class)
    public ResponseEntity<Grade> get(@RequestParam Long id){
        Grade grade = service.read(id);
        if (grade != null){
            return new ResponseEntity<>(grade, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/grade/{id}")
    @JsonView(Views.Grade.class)
    public ResponseEntity<Grade> put(@RequestParam Long id, @RequestBody Grade grade){
        Grade newGrade = service.update(id, grade);
        if (newGrade != null){
            return new ResponseEntity<>(newGrade, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/grade/{id}")
    @JsonView(Views.Grade.class)
    public ResponseEntity<Grade> delete(@RequestParam Long id){
        Grade grade = service.delete(id);
        if (grade != null){
            return new ResponseEntity<>(grade, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
