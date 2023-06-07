package es.dws.escuela.services;

import es.dws.escuela.entities.Department;
import es.dws.escuela.entities.Grade;
import es.dws.escuela.entities.Teacher;
import es.dws.escuela.repositories.TeacherRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepository repository;
    @Autowired
    private EntityManager entityManager;

    //RELATIONSHIP
    @Autowired
    private GradeService gradeService;
    @Autowired
    private DepartmentService departmentService;

    public Teacher create(Teacher teacher){
        return repository.save(teacher);
    }

    //Read all the teachers saved in repository
    public List<Teacher> readAll(){
        return repository.findAll();
    }

    //Read by age range
    public List<Teacher> readByAge(int min, int max) {
        TypedQuery<Teacher>  tq = entityManager.createQuery("SELECT t FROM Teacher t where t.age >= ?1 and t.age <= ?2", Teacher.class);
        tq.setParameter(1, min);
        tq.setParameter(2, max);
        return tq.getResultList();
    }
    //Read by known ID
    public Teacher read(String id){
        Optional<Teacher> op = repository.findById(id);
        return op.orElse(null);
    }

    public Teacher update(String id, Teacher newTeacher){
        Optional<Teacher> op = repository.findById(id);
        if(op.isPresent()){
            //TODO: make safe update (for each attribute)
            Teacher teacher = op.get();
            teacher.setDescription(newTeacher.getDescription());
            if(newTeacher.getPass().length() > 0 && !newTeacher.getPass().equals(teacher.getPass())){
                teacher.setPass(newTeacher.getPass());
            }
            if(newTeacher.getAge() <= 100 && newTeacher.getAge() > 0){
                teacher.setAge(newTeacher.getAge());
            }
            repository.save(teacher);
            return teacher;
        }else{
            return null;
        }
    }

    public Teacher delete(String id){
        Optional<Teacher> op = repository.findById(id);
        if(op.isPresent()){
            Teacher t = op.get();
            //Clear grades list and department
            this.removeAllGrades(id);
            this.removeDept(id);
            //Delete safely by ID
            repository.deleteById(id);
            return t;
        }else{
            return null;
        }
    }

    //Non-standard queries

    //Check if teacher exists
    public boolean teacherExists(String id){
        return repository.findById(id).isPresent();
    }
    //Assign teacher to department
    public Teacher assignDept(String teacherId, Long deptId){
        Teacher teacher = this.read(teacherId);
        if (teacher != null){
            Department department = this.departmentService.read(deptId);
            if(department != null){
                Department oldDep = teacher.getDepartment();
                if(oldDep!=null){
                    departmentService.removeTeacherFromDept(oldDep.getId(),teacherId);
                }
                teacher.setDepartment(department);
                repository.save(teacher);
                this.departmentService.addTeacher(teacher,deptId);
                return teacher;
            }else {
                return null;
            }
        }else {
            return null;
        }
    }

    //Assign teacher to grade
    public Teacher assignGrade(String teacherId, Long gradeId){
        Teacher teacher = this.read(teacherId);
        if (teacher != null){
            Grade grade = this.gradeService.read(gradeId);
            if(grade != null){
                teacher.addGrade(grade);
                this.gradeService.addTeacher(teacher,gradeId);
                return teacher;
            }else {
                return null;
            }
        }else {
            return null;
        }
    }

    //remove grade from teacher if assigned. Delete grade if the grade has no teachers
    public Teacher removeGrade(String teacherId, Long gradeId){
        Teacher teacher = this.read(teacherId);
        if (teacher != null){
            Grade grade = this.gradeService.read(gradeId);
            if(grade != null){
                teacher.removeGrade(grade);
                this.gradeService.removeTeacherFromGrade(gradeId,teacherId);
                if(grade.getTeachers().isEmpty()){
                    gradeService.delete(gradeId);
                }
                return teacher;
            }else {
                return null;
            }
        }else {
            return null;
        }
    }

    //Remove all the grades from the grade list of the teacher. Delete grades if the grades have no teachers
    public Teacher removeAllGrades(String teacherId){
        Optional<Teacher> op = repository.findById(teacherId);
        if(op.isPresent()){
            Teacher teacher = op.get();
            if (!teacher.getGrades().isEmpty()){
                for(Grade g:teacher.getGrades()){
                    g.removeTeacher(teacher);
                    if(g.getTeachers().isEmpty()){
                        gradeService.delete(g.getId());
                    }
                }
                teacher.getGrades().clear();
                repository.save(teacher);
                return teacher;
            }else {
                return null;
            }
        }else {
            return null;
        }
    }

    public Teacher removeDept(String teacherId) {
        Teacher teacher = this.read(teacherId);
        if (teacher != null){
            Department department = teacher.getDepartment();
            if(department != null){
                department.removeTeacher(teacher);
                teacher.setDepartment(null);
                repository.save(teacher);
                return teacher;
            }else {
                return null;
            }
        }else {
            return null;
        }
    }
}
