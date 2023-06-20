package es.dws.escuela.controllers;

import es.dws.escuela.entities.Department;
import es.dws.escuela.entities.Grade;
import es.dws.escuela.entities.Teacher;
import es.dws.escuela.entities.User;
import es.dws.escuela.services.DepartmentService;
import es.dws.escuela.services.GradeService;
import es.dws.escuela.services.TeacherService;
import es.dws.escuela.services.UserService;
import es.dws.escuela.valids.ValidDept;
import es.dws.escuela.valids.ValidGrade;
import es.dws.escuela.valids.ValidTeacher;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
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
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init(){
        //Create some instances
        //E-Mail are generated automatically
        Teacher teacher1 = new Teacher("Profesor","Uno",passwordEncoder.encode("profesor1"),"Soy profesor 1",21);
        Teacher teacher2 = new Teacher("Profesor","Dos",passwordEncoder.encode("profesor2"),"Soy profesor 2",23);
        User user = new User("user","o", passwordEncoder.encode("user"),"Hola mundo");
        User admin = new User("admin","o", passwordEncoder.encode("user"),"Hola mundo","USER","ADMIN");
        Grade grade = new Grade("Ciberseguridad","Clase de Ciberseguridad",2023);
        Department department = new Department("Dpto. Ciberseguridad","Departamental II", "Departamento de Ciberseguridad");
        //Create the department into the database
        department = departmentService.create(department);
        //Associate teachers with the department and create the teachers
        teacher1.setDepartment(department);
        teacher2.setDepartment(department);
        teacherService.create(teacher1);
        teacherService.create(teacher2);
        userService.create(user);
        userService.create(admin);
        //Associate the grade to the teachers and the user and create the grade
        grade.addTeacher(teacher1);
        grade.addTeacher(teacher2);
        grade.addUser(user);
        gradeService.create(grade);

    }

    @GetMapping("/")
    public String index(){
        return "home/index";
    }

    @GetMapping("/login")
    public String login(){
        return "home/login";
    }
    @GetMapping("/signup")
    public String signup(){
        return "forms/signup";
    }
    //Get all the teachers. There is always at least one teacher, so teacherexists=1
    @GetMapping("/teacher")
    public String getTeachers(Model model){
        model.addAttribute("teachers",teacherService.readAll());
        return "entities/teachers";
    }
    @GetMapping("/teacherByAge")
    public String getTeachersByAge(Model model, @RequestParam(defaultValue = "1") int min, @RequestParam(defaultValue = "100") int max){
        if(min <= max && min > 0){
            List<Teacher> teachers = teacherService.readByAge(min,max);
            model.addAttribute("teachers", teachers);
            return "entities/teachers";
        }else{
            return "errors/error";
        }
    }
    @GetMapping("/department")
    public String getDepartments(Model model){
        List<Department> departments = departmentService.readAll();
        model.addAttribute("departments",departments);
        return "entities/departments";
    }
    @GetMapping("/grade")
    public String getGrades(Model model){
        List<Grade> grades = gradeService.readAll();
        model.addAttribute("grades",grades);
        return "entities/grades";
    }
    @GetMapping("/teacher/{id}")
    public String getTeacherProfile(Model model, @PathVariable String id){
        Teacher teacher = teacherService.read(id);
        if(teacher != null){
            model.addAttribute("profile",teacher);
            return "entities/profile";
        }else{
            return "errors/404";
        }
    }

    @GetMapping("/grade/{id}")
    public String getGradeProfile(Model model, @PathVariable Long id){
        Grade grade = gradeService.read(id);
        if(grade != null){
            model.addAttribute("grade",grade);
            model.addAttribute("teacher",grade.getTeachers());
            return "entities/gradeProfile";
        }else{
            return "errors/404";
        }
    }
    @GetMapping("/department/{id}")
    public String getDepartmentProfile(Model model, @PathVariable Long id){
        Department department = departmentService.read(id);
        if(department != null){
            model.addAttribute("department",department);
            model.addAttribute("teacher",department.getTeachers());
            return "entities/departmentProfile";
        }else{
            return "errors/404";
        }
    }

    //Entity forms
    @GetMapping("/teacher/add")
    public String getTeacherForm(Model model){
        model.addAttribute("ignore",1);
        return "forms/teacherForm";
    }
    @GetMapping("/department/add")
    public String getDepartmentForm(Model model){
        model.addAttribute("ignore",1);
        return "forms/departmentForm";
    }
    @GetMapping("/grade/add")
    public String getGradeForm(Model model){
        model.addAttribute("ignore",1);
        return "forms/gradeForm";
    }

    //Post controllers
    /*@PostMapping("/teacher/add")
    public String postTeacher(Model model,
                              @RequestParam String name,
                              @RequestParam String surname,
                              @RequestParam String pass,
                              @RequestParam String description,
                              @RequestParam int age){
        teacherService.create(new Teacher(name,surname.split(" ")[0],pass,description,age));
        return getTeachers(model);
    }*/
    /*@PostMapping("/department/add")
    public String postDepartment(Model model,
                                 @RequestParam String name,
                                 @RequestParam String location,
                                 @RequestParam String description){
        departmentService.create(new Department(name,location,description));
        return getDepartments(model);
    }*/
    /*@PostMapping("/grade/add")
    public String postGrade(Model model,
                            @RequestParam String name,
                            @RequestParam Integer year,
                            @RequestParam String description){
        gradeService.create(new Grade(name,description,year));
        return getGrades(model);
    }*/

    //MODIFICATION: implements validation and body
    @PostMapping("/teacher/add")
    public String postTeacher(Model model, @Valid Teacher teacher, BindingResult br){
        if(br.hasErrors()){
            for (FieldError e:br.getFieldErrors()){
                model.addAttribute(e.getField(),e.getDefaultMessage());
            }
            model.addAttribute("ignore",1);
            model.addAttribute("formerror",1);
            return "forms/teacherForm";
        }
        if(!teacherService.teacherExists(teacher.getId())){
            teacherService.create(teacher);
            return getTeachers(model);
        }else {
            ObjectError e = new ObjectError("ExistingTeacherError","Teacher with this name and surname already exists");
            model.addAttribute("error",e);
            return "errors/error";
        }
    }
    @PostMapping("/department/add")
    public String postDepartment(Model model, @Valid Department department, BindingResult br){
        if(br.hasErrors()){
            model.addAttribute("error",br.getAllErrors());
            return "forms/departmentForm";
        }
        departmentService.create(department);
        return getDepartments(model);
    }
    @PostMapping("/grade/add")
    public String postGrade(Model model, @Valid Grade grade, BindingResult br){
        if(br.hasErrors()){
            model.addAttribute("error",br.getAllErrors());
            return "forms/gradeForm";
        }
        gradeService.create(grade);
        return getGrades(model);
    }


    //Edit controllers
    @GetMapping("/teacher/edit")
    public String editTeacher(Model model, @RequestParam String id){
        Teacher teacher = teacherService.read(id);
        if(teacher != null){
            model.addAttribute("teacher",teacher);
            return "forms/teacherForm";
        }else{
            return "errors/404";
        }
    }
    @PostMapping("/teacher/edit")
    public String putEditTeacher(Model model, @RequestParam String id, @Valid ValidTeacher newTeacher){
        if(teacherService.update(id,newTeacher) != null){
            return getTeacherProfile(model,id);
        }else {
            return "errors/404";
        }
    }
    //TODO: (DONE) implement edit controllers for Grade and Department
    //Grade edit
    @GetMapping("/grade/edit")
    public String editGrade(Model model, @RequestParam Long id){
        Grade grade = gradeService.read(id);
        if(grade != null){
            model.addAttribute("grade",grade);
            return "forms/gradeForm";
        }else{
            return "errors/404";
        }
    }
    @PostMapping("/grade/edit")
    public String putEditGrade(Model model, @RequestParam Long id, @Valid ValidGrade newGrade){
        if(gradeService.update(id,newGrade) != null){
            return getGradeProfile(model,id);
        }else {
            return "errors/404";
        }
    }

    //Department edit
    @GetMapping("/department/edit")
    public String editDepartment(Model model, @RequestParam Long id){
        Department department = departmentService.read(id);
        if(department != null){
            model.addAttribute("department",department);
            return "forms/departmentForm";
        }else{
            return "errors/404";
        }
    }
    @PostMapping("/department/edit")
    public String putEditDepartment(Model model, @RequestParam Long id, @Valid ValidDept newDept){
        if(departmentService.update(id,newDept) != null){
            return getDepartmentProfile(model,id);
        }else {
            return "errors/404";
        }
    }

    //Delete controllers
    @GetMapping("/teacher/delete")
    public String deleteTeacher(Model model, @RequestParam String id){
        if(teacherService.read(id) != null){
            teacherService.delete(id);
            return getTeachers(model);
        }else{
            return "errors/404";
        }
    }
    @GetMapping("/department/delete")
    public String deleteDepartment(Model model, @RequestParam Long id){
        if(departmentService.read(id) != null){
            departmentService.delete(id);
            return getDepartments(model);
        }else{
            return "errors/404";
        }
    }
    @GetMapping("/grade/delete")
    public String deleteGrade(Model model, @RequestParam Long id){
        if(gradeService.read(id) != null){
            gradeService.delete(id);
            return getGrades(model);
        }else{
            return "errors/404";
        }
    }

    //Remove teacher from Grade/Department
    @GetMapping("/grade/remove")
    public String removeTeacherFromGrade(Model model, @RequestParam Long id, @RequestParam String teacherId){
        if(teacherService.removeGrade(teacherId,id) != null){
            return getTeacherProfile(model,teacherId);
        }else{
            return "errors/404";
        }
    }

    @GetMapping("/department/remove")
    public String removeTeacherFromDept(Model model, @RequestParam String teacherId){
        if(teacherService.removeDept(teacherId) != null){
            return getTeacherProfile(model,teacherId);
        }else{
            return "errors/404";
        }
    }

}
