package es.dws.escuela.controllers;

import es.dws.escuela.entities.Department;
import es.dws.escuela.entities.Grade;
import es.dws.escuela.entities.Teacher;
import es.dws.escuela.services.DepartmentService;
import es.dws.escuela.services.GradeService;
import es.dws.escuela.services.TeacherService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;

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
        Teacher teacher = new Teacher("profesor1","Uno","profesor1@urdj.es","uno","Soy profesor 1",21);
        Teacher teacher2 = new Teacher("profesor2","Dos","profesor2@urdj.es","dos","Soy profesor 2",23);
        Grade grade = new Grade("Ciberseguridad","Clase de Ciberseguridad",2023);
        Department department = new Department("Dpto. Ciberseguridad","Departamental II", "Departamento de Ciberseguridad");
        departmentService.create(department);
        gradeService.create(grade);
        teacherService.create(teacher);
        teacherService.create(teacher2);
    }
    @GetMapping("/teacher")
    public String getTeachers(Model model){
        model.addAttribute("teachers",teacherService.readAll());
        return "teachers";
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
        Teacher teacher = teacherService.read(name);
        if(teacher != null){
            model.addAttribute("teacher",teacher);
            return "profile";
        }else{
            return "../static/index";
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

    //Post
    @PostMapping("/teacher/add")
    public String postTeacher(@RequestParam String name, @RequestParam String surname, @RequestParam String email, @RequestParam String pass, @RequestParam String description, @RequestParam int    age){
        teacherService.create(new Teacher(name,surname,email,pass,description,age));
        return "/teachers";
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
