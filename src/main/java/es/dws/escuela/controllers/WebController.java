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
        //E-Mail are generated automatically
        Teacher teacher = new Teacher("Profesor","Uno","uno","Soy profesor 1",21);
        Teacher teacher2 = new Teacher("Profesor","Dos","dos","Soy profesor 2",23);
        Grade grade = new Grade("Ciberseguridad","Clase de Ciberseguridad",2023);
        Department department = new Department("Dpto. Ciberseguridad","Departamental II", "Departamento de Ciberseguridad");
        //Create the department
        departmentService.create(department);
        //Associate teachers with the department
        teacher.setDepartment(department);
        teacher2.setDepartment(department);
        //Create the instances into the database
        gradeService.create(grade);
        teacherService.create(teacher);
        teacherService.create(teacher2);
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
    @GetMapping("/profile/{name}")
    public String getProfile(Model model, @PathVariable String name){
        Teacher teacher = teacherService.read(name.replace(" ",""));
        if(teacher != null){
            model.addAttribute("teacher",teacher);
            return "profile";
        }else{
            return "/";
        }
    }
    @GetMapping("/teacher/add")
    public String getTeacherForm(Model model){
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
}
