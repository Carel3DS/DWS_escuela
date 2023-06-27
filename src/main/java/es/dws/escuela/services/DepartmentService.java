package es.dws.escuela.services;

import es.dws.escuela.entities.Department;
import es.dws.escuela.entities.Teacher;
import es.dws.escuela.repositories.DepartmentRepository;
import es.dws.escuela.utils.HTMLPolicy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.owasp.html.PolicyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository repository;
    @Autowired
    private EntityManager entityManager;

    private final static PolicyFactory POLICY = HTMLPolicy.POLICY_DEFINITION;
    
    public Department create(Department department){
        department.setDescription(POLICY.sanitize(department.getDescription()));
        return repository.save(department);
    }
    public List<Department> readAll(){
        return repository.findAll();
    }
    public Department read(Long id){
        Optional<Department> op = repository.findById(id);
        return op.orElse(null);
    }

    public List<Department> readByName(String name) {
        TypedQuery<Department> tq = entityManager.createQuery("SELECT t FROM Department t where t.name like ?1", Department.class);
        tq.setParameter(1, "%"+name+"%");
        return tq.getResultList();
    }

    public Department update(Long id, Department newDepartment){
        Optional<Department> op = repository.findById(id);
        if(op.isPresent()){
            //TODO: make safe update (for each attribute)
            Department department = op.get();
            if(newDepartment.getName() != null){
                department.setName(newDepartment.getName());
            }
            //Location and Description can be Null
            department.setLocation(newDepartment.getLocation());
            department.setDescription(POLICY.sanitize(newDepartment.getDescription()));
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
