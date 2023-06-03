package es.dws.escuela.controllers;

import es.dws.escuela.entities.Department;
import es.dws.escuela.entities.Grade;
import es.dws.escuela.entities.Teacher;
import es.dws.escuela.services.DepartmentService;
import es.dws.escuela.services.GradeService;
import es.dws.escuela.services.TeacherService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class WebController {

    @Autowired
    GradeService gradeService;
    @Autowired
    TeacherService teacherService;
    @Autowired
    DepartmentService departmentService;

    @PostConstruct
    public void init(){
        //Create some instances
        //E-Mail are generated automatically
        Teacher teacher = new Teacher("Profesor","Uno","uno","Soy profesor 1",21);
        Teacher teacher2 = new Teacher("Profesor","Dos","dos","Soy profesor 2",23);
        Grade grade = new Grade("Ciberseguridad","Clase de Ciberseguridad",2023);
        Department department = new Department("Dpto. Ciberseguridad","Departamental II", "Departamento de Ciberseguridad");
        //Create the department into the database
        department = departmentService.create(department);
        //Associate teachers with the department and create the teachers
        teacher.setDepartment(department);
        teacher2.setDepartment(department);
        teacherService.create(teacher);
        teacherService.create(teacher2);
        //Associate the grade to the teachers and create the grade
        grade.addTeacher(teacher);
        grade.addTeacher(teacher2);
        gradeService.create(grade);

    }
    //Get all the teachers. There is always at least one teacher, so teacherexists=1
    @GetMapping("/teacher")
    public String getTeachers(Model model){
        model.addAttribute("teachers",teacherService.readAll());
        model.addAttribute("teacherexists",1);
        return "teachers";
    }
    @GetMapping("/teacherByAge")
    public String getTeachersByAge(Model model, @RequestParam(defaultValue = "1") int min, @RequestParam(defaultValue = "100") int max){
        if(min <= max && min > 0){
            List<Teacher> teachers = teacherService.readByAge(min,max);
            model.addAttribute("teachers", teachers);
            model.addAttribute("teacherexists",!teachers.isEmpty());
            return "teachers";
        }else{
            return "/";
        }
    }
    @GetMapping("/department")
    public String getDepartments(Model model){
        model.addAttribute("department",departmentService.readAll());
        return "departments";
    }
    @GetMapping("/grade")
    public String getGrades(Model model){
        model.addAttribute("grade",gradeService.readAll());
        return "grades";
    }
    @GetMapping("/profile/{id}")
    public String getProfile(Model model, @PathVariable String id){
        Teacher teacher = teacherService.read(id);
        if(teacher != null){
            model.addAttribute("teacher",teacher);
            return "profile";
        }else{
            return "/";
        }
    }

    @GetMapping("/teacher/add")
    public String getTeacherForm(Model model){
        model.addAttribute("ignore",1);
        return "teacherForm";
    }
    @GetMapping("/department/add")
    public String getDepartmentForm(Model model){
        return "departmentForm";
    }
    @GetMapping("/grade/add")
    public String getGradeForm(Model model){
        return "gradeForm";
    }

    //Post controllers
    @PostMapping("/teacher/add")
    public String postTeacher(Model model, @RequestParam String name, @RequestParam String surname, @RequestParam String pass, @RequestParam String description, @RequestParam int    age){
        //Only takes the first surname
        teacherService.create(new Teacher(name,surname.split(" ")[0],pass,description,age));
        return getTeachers(model);
    }
    @PostMapping("/department/add")
    public String postDepartment(Model model){
        return "departments";
    }
    @PostMapping("/grade/add")
    public String postGrade(Model model){
        return "grades";
    }

    //Edit controllers
    @GetMapping("/profile/edit")
    public String editTeacher(Model model, @RequestParam String id){
        Teacher teacher = teacherService.read(id);
        if(teacher != null){
            model.addAttribute("teacher",teacher);
            return "teacherForm";
        }else{
            return "/";
        }
    }
    @PostMapping("/profile/edit")
    public String putEditTeacher(Model model, @RequestParam String id, @RequestParam String pass, @RequestParam String description, @RequestParam int age){
        if(teacherService.update(id,new Teacher("","",pass,description,age)) != null){
            return getProfile(model,id);
        }else {
            return "/";
        }
    }
    //TODO: (DONE) implement edit controllers for Grade and Department
    //Grade edit
    @GetMapping("/grade/edit")
    public String editGrade(Model model, @RequestParam Long id){
        Grade grade = gradeService.read(id);
        if(grade != null){
            model.addAttribute("grade",grade);
            return "gradeForm";
        }else{
            return "/";
        }
    }
    @PostMapping("/grade/edit")
    public String putEditGrade(Model model, @RequestParam Long id, @RequestParam String name, @RequestParam String description, @RequestParam int year){
        if(gradeService.update(id,new Grade("",description,year)) != null){
            return getGrades(model);
        }else {
            return "/";
        }
    }

    //Department edit
    @GetMapping("/department/edit")
    public String editDepartment(Model model, @RequestParam Long id){
        Department department = departmentService.read(id);
        if(department != null){
            model.addAttribute("department",department);
            return "departmentForm";
        }else{
            return "/";
        }
    }
    @PostMapping("/department/edit")
    public String putEditDepartment(Model model, @RequestParam Long id, @RequestParam String name, @RequestParam String location, @RequestParam String description){
        if(departmentService.update(id,new Department(name,location,description)) != null){
            return getDepartments(model);
        }else {
            return "/";
        }
    }

    //Delete controllers
    @GetMapping("/profile/delete")
    public String deleteTeacher(Model model, @RequestParam String id){
        if(teacherService.read(id) != null){
            teacherService.delete(id);
            return getTeachers(model);
        }else{
            return "/";
        }
    }
    @GetMapping("/department/delete")
    public String deleteDepartment(Model model, @RequestParam Long id){
        if(departmentService.read(id) != null){
            departmentService.delete(id);
            return getDepartments(model);
        }else{
            return "/";
        }
    }
    @GetMapping("/grade/delete")
    public String deleteGrade(Model model, @RequestParam Long id){
        if(gradeService.read(id) != null){
            gradeService.delete(id);
            return getGrades(model);
        }else{
            return "/";
        }
    }

    //Remove teacher from Grade/Department
    @GetMapping("/grade/remove")
    public String removeTeacherFromGrade(Model model, @RequestParam Long id, @RequestParam String teacherId){
        if(teacherService.removeGrade(teacherId,id) != null){
            return getProfile(model,teacherId);
        }else{
            return "/";
        }
    }

    @GetMapping("/department/remove")
    public String removeTeacherFromDept(Model model, @RequestParam String teacherId){
        if(teacherService.removeDept(teacherId) != null){
            return getProfile(model,teacherId);
        }else{
            return "/";
        }
    }

}
