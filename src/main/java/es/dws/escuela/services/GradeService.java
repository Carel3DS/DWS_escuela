package es.dws.escuela.services;

import es.dws.escuela.entities.Grade;
import es.dws.escuela.repositories.GradeRepository;
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

    public Grade update(Long id, Grade newGrade){
        Optional<Grade> op = repository.findById(id);
        if(op.isPresent()){
            //TODO: make safe update (for each attribute)
            Grade grade = op.get();
            grade.setYear(newGrade.getYear());
            grade.setName(newGrade.getName());
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
            repository.deleteById(id);
            return op.get();
        }else{
            return null;
        }
    }
}
