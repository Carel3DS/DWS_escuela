package es.dws.escuela.services;

import es.dws.escuela.entities.Teacher;
import es.dws.escuela.repositories.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepository repository;

    //RELATIONSHIP
    @Autowired
    private GradeService gradeService;
    @Autowired
    private TeacherService teacherService;

    public Teacher create(Teacher teacher){
        return repository.save(teacher);
    }
    public List<Teacher> readAll(){
        return repository.findAll();
    }
    public Teacher read(String id){
        Optional<Teacher> op = repository.findById(id);
        return op.orElse(null);
    }

    public Teacher update(String id, Teacher newTeacher){
        Optional<Teacher> op = repository.findById(id);
        if(op.isPresent()){
            //TODO: make safe update (for each attribute)
            Teacher teacher = op.get();
            teacher.setName(newTeacher.getName());
            teacher.setSurname(newTeacher.getSurname());
            teacher.setDescription(newTeacher.getDescription());
            teacher.setEmail(newTeacher.getEmail());
            //Does this overwrite the teacher identified by id?
            repository.save(teacher);
            return teacher;
        }else{
            return null;
        }
    }
    public Teacher delete(String id){
        Optional<Teacher> op = repository.findById(id);
        if(op.isPresent()){
            repository.deleteById(id);
            return op.get();
        }else{
            return null;
        }
    }
}
