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
            Department d = op.get();
            for(Teacher t:d.getTeachers()){
                t.setDepartment(null);
            }
            //Save changes
            repository.save(d);
            //Clear teachers list
            d.getTeachers().clear();
            //Save changes again
            repository.save(d);
            //Delete safely by id
            repository.deleteById(id);
            return d;
        }else{
            return null;
        }
    }

    //Non-standard queries

    //Add teacher to department (package view only)
    void addTeacher(Teacher teacher, Long deptId){
        Department department = this.read(deptId);
        department.addTeacher(teacher);
        repository.save(department);
    }

    public void removeTeacherFromDept(Long id, String teacherId) {
        Department department = this.read(id);
        department.removeTeacher(teacherId);
        repository.save(department);
    }
}
