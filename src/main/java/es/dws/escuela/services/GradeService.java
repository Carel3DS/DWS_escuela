package es.dws.escuela.services;

import es.dws.escuela.entities.Grade;
import es.dws.escuela.entities.Grade;
import es.dws.escuela.entities.Grade;
import es.dws.escuela.entities.Teacher;
import es.dws.escuela.repositories.GradeRepository;
import es.dws.escuela.valids.ValidGrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GradeService {
    @Autowired
    private GradeRepository repository;

    public Grade create(Grade grade){
        return repository.save(grade);
    }
    public List<Grade> readAll(){
        return repository.findAll();
    }
    public Grade read(Long id){
        Optional<Grade> op = repository.findById(id);
        return op.orElse(null);
    }

    public Grade update(Long id, ValidGrade newGrade){
        Optional<Grade> op = repository.findById(id);
        if(op.isPresent()){
            //TODO: make safe update (for each attribute)
            Grade grade = op.get();
            if(newGrade.getYear() != null){
                grade.setYear(newGrade.getYear());
            }
            if(newGrade.getName() != null){
                grade.setName(newGrade.getName());
            }
            grade.setDescription(newGrade.getDescription());
            //Does this overwrite the grade identified by id?
            repository.save(grade);
            return grade;
        }else{
            return null;
        }
    }

    public Grade delete(Long id){
        Optional<Grade> op = repository.findById(id);
        if(op.isPresent()){
            Grade g = op.get();
            //Remove reference of the grade for each teacher if there are teachers
            if(!g.getTeachers().isEmpty()){
                for(Teacher t:g.getTeachers()){
                    t.removeGrade(g);
                }
            }
            //Save the changes
            repository.save(g);
            //Clear teachers list
            g.getTeachers().clear();
            //Save again
            repository.save(g);
            //Delete safely by ID
            repository.deleteById(id);
            return g;
        }else{
            return null;
        }
    }

    void addTeacher(Teacher teacher, Long gradeId) {
        Grade grade = this.read(gradeId);
        List<Teacher> teachers = grade.getTeachers();
        teachers.add(teacher);
        grade.setTeachers(teachers);
        repository.save(grade);
    }

    public void removeTeacherFromGrade(Long id, String teacherId) {
        Grade grade = this.read(id);
        List<Teacher> teachers = grade.getTeachers();
        boolean found = false;
        int i = 0;
        //Find the teacher and remove from the list (O(N))
        while (!found){
            Teacher t = teachers.get(i);
            if (t.getId().equals(teacherId)){
                teachers.remove(i);
                found = true;
            }else {
                i++;
            }
        }
        grade.setTeachers(teachers);
        repository.save(grade);
    }
}
