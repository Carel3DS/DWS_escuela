package es.dws.escuela.services;

import es.dws.escuela.entities.Department;
import es.dws.escuela.entities.Teacher;
import es.dws.escuela.repositories.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository repository;

    public Department create(Department department){
        return repository.save(department);
    }
    public List<Department> readAll(){
        return repository.findAll();
    }
    public Department read(Long id){
        Optional<Department> op = repository.findById(id);
        return op.orElse(null);
    }

    public Department update(Long id, Department newDepartment){
        Optional<Department> op = repository.findById(id);
        if(op.isPresent()){
            //TODO: make safe update (for each attribute)
            Department department = op.get();
            department.setLocation(newDepartment.getLocation());
            department.setName(newDepartment.getName());
            department.setDescription(newDepartment.getDescription());
            //Does this overwrite the department identified by id?
            repository.save(department);
            return department;
        }else{
            return null;
        }
    }
    public Department delete(Long id){
        Optional<Department> op = repository.findById(id);
        if(op.isPresent()){
            repository.deleteById(id);
            return op.get();
        }else{
            return null;
        }
    }

    //Non-standard queries

    //Add teacher to department (package view only)
    void addTeacher(Teacher teacher, Long deptId){
        Department department = this.read(deptId);
        List<Teacher> teachers = department.getTeachers();
        teachers.add(teacher);
        department.setTeachers(teachers);
        repository.save(department);
    }

    public void removeTeacherFromDept(Long id, String teacherId) {
        Department department = this.read(id);
        List<Teacher> teachers = department.getTeachers();
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
        department.setTeachers(teachers);
        repository.save(department);
    }
}
