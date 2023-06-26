package es.dws.escuela.controllers;

import es.dws.escuela.entities.*;
import es.dws.escuela.services.DepartmentService;
import es.dws.escuela.services.GradeService;
import es.dws.escuela.services.TeacherService;
import es.dws.escuela.services.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j //Ad-hoc solution to enable custom logs in the WebController
public class WebController {
    //TODO: make editGrade view using forms/gradeForm template. Same for department
    //TODO: set br.error fields on templates. Add not-logged-error message on login page
    //TODO: solve no-teacher grade API REST method
    // TODO: implement CSRF and deploy on GCP host domain

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
        User user = new User("user","o", passwordEncoder.encode("user"),"Hola mundo","USER");
        User admin = new User("admin","o", passwordEncoder.encode("admin"),"Soy Admin","USER","ADMIN");
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
        //grade.addUser(user);
        gradeService.create(grade);

    }

    // FUNCTIONAL FEATURES CONTROLLERS //
    @GetMapping("/")
    public String index(){
        return "home/index";
    }

    @GetMapping("/login")
    public String login(User user, HttpServletRequest request){
        if(request.getUserPrincipal()==null){
            return "home/login";
        }else {
            return "redirect:/";
        }
    }
    @GetMapping("/signup")
    public String signup(User user, HttpServletRequest request){
        if(request.getUserPrincipal()==null){
            return "forms/signup";
        }else {
            return "redirect:/";
        }
    }

    @PostMapping("/signup")
    public String signup(Model model, HttpServletRequest request, @Valid User user, BindingResult br)
            throws ServletException {
        if(request.getUserPrincipal() == null){
            if(br.hasErrors()){
                return "forms/signup";
            }

            user = new User(user.getName(),user.getSurname(),user.getPass());
            if(!userService.userExists(user.getId())){
                String pass = user.getPass();
                user.setPass(passwordEncoder.encode(user.getPass()));
                userService.create(user);
                try{
                    request.login(user.getId(), pass);
                    return "redirect:/profile";
                }catch (ServletException e){
                    log.error(e.toString());
                    throw e;
                }
            }else {
                ObjectError e = new ObjectError("ExistingTeacherError","User with this name and surname already exists");
                br.addError(e);
                return "forms/signup";
            }
        }else {
            return "redirect:/";
        }
    }

    @GetMapping("/profile")
    public String profile(Model model){
        if(SecurityContextHolder.getContext() != null){
            model.addAttribute("self",true);
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user= userService.read(username);
            if (user == null){
                Teacher teacher = teacherService.read(username);
                if (teacher == null){
                    return "errors/404";
                }
                model.addAttribute("profile",teacher);
                model.addAttribute("isTeacher",true);
                return "entities/profile";
            }
            model.addAttribute("profile",user);
            model.addAttribute("isTeacher",false);
            return "entities/profile";
        }else {
            return "errors/400";
        }
    }
    
    // GET-ALL REQUESTS //
    @GetMapping("/teacher")
    public String getTeachers(Model model, HttpServletRequest request){
        model.addAttribute("teachers",teacherService.readAll());
        model.addAttribute("isAdmin", request.isUserInRole("ROLE_ADMIN"));
        return "entities/teachers";
    }

    //Typed-query requests
    //Note: usually, the same entity route may be used to perform queries
    //however, for this project since I made ad-hoc filters, I made specific routes
    @GetMapping("/teacherByAge")
    public String getTeachersByAge(Model model, @RequestParam(defaultValue = "1") int min, @RequestParam(defaultValue = "100") int max, HttpServletRequest request){
        if(min <= max && min > 0){
            List<Teacher> teachers = teacherService.readByAge(min,max);
            model.addAttribute("teachers", teachers);
            model.addAttribute("isAdmin", request.isUserInRole("ROLE_ADMIN"));
            return "entities/teachers";
        }else{
            return "errors/error";
        }
    }
    @GetMapping("/gradeByName")
    public String getGradeByName(Model model, @RequestParam String name, HttpServletRequest request){
        List<Grade> grades = gradeService.readByName(name);
        model.addAttribute("grades", grades);
        model.addAttribute("isAdmin", request.isUserInRole("ROLE_ADMIN"));
        return "entities/grades";
    }

    @GetMapping("/departmentByName")
    public String getDepartmentByName(Model model, @RequestParam String name, HttpServletRequest request){
        List<Department> departments = departmentService.readByName(name);
        model.addAttribute("departments", departments);
        model.addAttribute("isAdmin", request.isUserInRole("ROLE_ADMIN"));
        return "entities/departments";
    }
    
    @GetMapping("/department")
    public String getDepartments(Model model, HttpServletRequest request){
        List<Department> departments = departmentService.readAll();
        model.addAttribute("departments",departments);
        model.addAttribute("isAdmin", request.isUserInRole("ROLE_ADMIN"));
        return "entities/departments";
    }
    @GetMapping("/grade")
    public String getGrades(Model model, HttpServletRequest request){
        List<Grade> grades = gradeService.readAll();
        model.addAttribute("grades",grades);
        model.addAttribute("isAdmin", request.isUserInRole("ROLE_ADMIN"));
        return "entities/grades";
    }

    // GET REQUESTS //
    @GetMapping("/teacher/{id}")
    public String getTeacherProfile(Model model, @PathVariable String id, HttpServletRequest request){
        Teacher teacher = teacherService.read(id);
        if(teacher != null){
            model.addAttribute("profile",teacher);
            model.addAttribute("isTeacher",true);
            model.addAttribute("isAdmin", request.isUserInRole("ROLE_ADMIN"));
            return "entities/profile";
        }else{
            return "errors/404";
        }
    }
    @GetMapping("/user/{id}")
    public String getUserProfile(Model model,@PathVariable String id) {
        User user = userService.read(id);
        if(user != null){
            model.addAttribute("profile", user);
            model.addAttribute("isTeacher",false);
            model.addAttribute("self",false);
            return "entities/profile";
        }else {
            return "errors/404";
        }
    }
    @GetMapping("/grade/{id}")
    public String getGradeProfile(Model model, @PathVariable Long id, HttpServletRequest request){
        Grade grade = gradeService.read(id);
        if(grade != null){
            //Check if user is logged in and its roles
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            if(request.isUserInRole("ROLE_TEACHER")){
                model.addAttribute("profile",teacherService.read(userId));
            } else if (request.isUserInRole("ROLE_USER")) {
                model.addAttribute("profile",userService.read(userId));
            }
            //Add model attributes
            model.addAttribute("isTeacher", request.isUserInRole("ROLE_TEACHER"));
            model.addAttribute("isAdmin", request.isUserInRole("ROLE_ADMIN"));
            model.addAttribute("grade",grade);
            model.addAttribute("teacher",grade.getTeachers());
            return "entities/gradeProfile";
        }else{
            return "errors/404";
        }
    }
    @GetMapping("/department/{id}")
    public String getDepartmentProfile(Model model, @PathVariable Long id, HttpServletRequest request){
        Department department = departmentService.read(id);
        if(department != null){
            model.addAttribute("department",department);
            model.addAttribute("teacher",department.getTeachers());
            model.addAttribute("isAdmin",request.isUserInRole("ROLE_ADMIN"));
            return "entities/departmentProfile";
        }else{
            return "errors/404";
        }
    }

    // ENTITY FORM REQUESTS //
    //Note: request the entity for binding
    @GetMapping("/teacher/add")
    public String getTeacherForm(Teacher teacher){
        return "forms/teacherForm";
    }
    @GetMapping("/department/add")
    public String getDepartmentForm(Department department){
        return "forms/departmentForm";
    }
    @GetMapping("/grade/add")
    public String getGradeForm(Grade grade){
        return "forms/gradeForm";
    }


    // ENTITY FORM POSTS //
    //Note: User post is signup() method, located at Functional features section
    @PostMapping("/teacher/add")
    public String postTeacher(Model model, HttpServletRequest request, @Valid Teacher teacher, BindingResult br) throws ServletException {
        if(request.getUserPrincipal()==null){
            Integer age = teacher.getAge();
            //Seems Valid is not working for this attribute as expected...
            if(age == null){
                FieldError e = new FieldError("teacher","age","Age is required");
                br.addError(e);
            }
            if(br.hasErrors()){
                return "forms/teacherForm";
            }

            Teacher newteacher = new Teacher(teacher.getName(),teacher.getSurname(),passwordEncoder.encode(teacher.getPass()), teacher.getAge());
            if(!teacherService.teacherExists(newteacher.getId())){
                String id = teacherService.create(newteacher).getId();
                request.login(newteacher.getId(), teacher.getPass());
                return "redirect:/profile";
            }else {
                ObjectError e = new ObjectError("ExistingTeacherError","Teacher with this name and surname already exists");
                br.addError(e);
                return "forms/teacherForm";
            }
        }else{
            return "redirect:/";
        }
    }
    @PostMapping("/department/add")
    public String postDepartment(Model model, @Valid Department department, BindingResult br){
        var roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if(roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            if(br.hasErrors()){
                return "forms/departmentForm";
            }
            department.setTeachers(new ArrayList<>());
            Long id = departmentService.create(department).getId();
            return "redirect:/grade/"+id;
        }else {
            return "errors/403";
        }
    }
    @PostMapping("/grade/add")
    public String postGrade(Model model, @Valid Grade grade,BindingResult br){
        var roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if(roles.contains(new SimpleGrantedAuthority("ROLE_TEACHER"))||roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            if(br.hasErrors()){
                return "forms/gradeForm";
            }
            grade.setTeachers(new ArrayList<>());
            if(roles.contains(new SimpleGrantedAuthority("ROLE_TEACHER"))){
                Teacher teacher = teacherService.read(SecurityContextHolder.getContext().getAuthentication().getName());
                grade.addTeacher(teacher);
            }
            Long id = gradeService.create(grade).getId();
            return "redirect:/grade/"+id;
        }else {
            return "errors/403";
        }
    }


    // EDIT FORM REQUESTS //
    //Teacher
    @GetMapping("/teacher/edit/{id}")
    public String editTeacher(Model model, @PathVariable String id, HttpServletRequest request){
        if(request.isUserInRole("ROLE_ADMIN")){
            Teacher teacher = teacherService.read(id);
            if(teacher != null){
                model.addAttribute("profile",teacher);
                model.addAttribute("isTeacher",true);
                model.addAttribute("isAdmin",true);
                model.addAttribute("departments",departmentService.readAll());
                return "forms/profileTeacherForm";
            }else{
                return "errors/404";
            }
        }else {
            return "errors/403";
        }
    }
    @PostMapping("/teacher/edit/{id}")
    public String updateTeacher(Model model, @PathVariable String id, @Validated(Groups.TeacherGroup.class) Teacher teacher, HttpServletRequest request, BindingResult br){
        if(request.isUserInRole("ROLE_ADMIN")){
            if(br.hasErrors()){
                model.addAttribute("profile", teacher);
                return "forms/profileForm";
            }
            //If validation is correct, check if the teacher with this id exists and update it
            if(teacherService.update(id,teacher) != null){
                return "redirect:/teacher/"+id;
            }else {
                return "errors/404";
            }
        }else {
            return "errors/403";
        }
    }
    
    //User
    @GetMapping("/user/edit/{id}")
    public String editUser(Model model, @PathVariable String id, HttpServletRequest request){
        if(request.isUserInRole("ROLE_ADMIN")){
            User user = userService.read(id);
            if(user != null){
                model.addAttribute("profile",user);
                model.addAttribute("isAdmin",true);
                return "forms/profileForm";
            }else{
                return "errors/404";
            }
        }else {
            return "errors/403";
        }
    }
    @PostMapping("/user/edit/{id}")
    public String updateUser(Model model, @PathVariable String id, @Validated(Groups.UserGroup.class) User user, BindingResult br){
        var roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if(roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            if(br.hasErrors()){
                model.addAttribute("profile", user);
                return "forms/profileForm";
            }
            //If validation is correct, check if the user with this id exists and update it
            if(userService.update(id,user) != null){
                return getUserProfile(model,id);
            }else {
                return "errors/404";
            }
        }else {
            return "errors/403";
        }
    }
    
    //Grade 
    @GetMapping("/grade/edit/{id}")
    public String editGrade(Model model, @PathVariable Long id){
        Grade grade = gradeService.read(id);
        if(grade != null){
            model.addAttribute("grade",grade);
            model.addAttribute("id",id);
            return "forms/gradeForm";
        }else{
            return "errors/404";
        }
    }
    @PostMapping("/grade/edit/{id}")
    public String updateGrade(@PathVariable Long id, Model model, @Validated(Groups.GradeGroup.class) Grade newGrade, BindingResult br){
        if(br.hasErrors()){

            model.addAttribute("grade", newGrade);
            return "forms/gradeForm";
        }
        if(gradeService.update(id,newGrade) != null){
            return "redirect:/grade/"+id;
        }else {
            return "errors/404";
        }
    }

    //Department
    @GetMapping("/department/edit/{id}")
    public String editDepartment(Model model, @PathVariable Long id){
        Department department = departmentService.read(id);
        if(department != null){
            model.addAttribute("department",department);
            return "forms/departmentForm";
        }else{
            return "errors/404";
        }
    }
    @PostMapping("/department/edit/{id}")
    public String updateDepartment(@PathVariable Long id, Model model, @Validated(Groups.DepartmentGroup.class) Department newDept, BindingResult br){
        if(br.hasErrors()){
            model.addAttribute("department", newDept);
            return "forms/profileForm";
        }
        if(departmentService.update(id,newDept) != null){
            return "redirect:/department/"+id;
        }else {
            return "errors/404";
        }
    }
    

    // DELETE-REQUEST CONTROLLERS //
    //Teacher
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/teacher/delete")
    public String deleteTeacher(Model model, @RequestParam String id){
        var roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if(roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            if(teacherService.read(id) != null){
                teacherService.delete(id);
                return "redirect:/teacher";
            }else{
                return "errors/404";
            }
        }else {
            return "errors/403";
        }
    }
    //User
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/user/delete")
    public String deleteUser(Model model, @RequestParam String id){
        var roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if(roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            if(userService.read(id) != null){
                userService.delete(id);
                return "redirect:/";
            }else{
                return "errors/404";
            }
        }else {
            return "errors/403";
        }
    }
    //Department
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/department/delete")
    public String deleteDepartment(Model model, @RequestParam Long id){
        if(departmentService.read(id) != null){
            departmentService.delete(id);
            return "redirect:/department";
        }else{
            return "errors/404";
        }
    }
    //Grade
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/grade/delete")
    public String deleteGrade(Model model, @RequestParam Long id){
        if(gradeService.read(id) != null){
            gradeService.delete(id);
            return "redirect:/grade";
        }else{
            return "errors/404";
        }
    }
    
    
    // SELF-FORM REQUESTS //
    //Edit-form request. Valid for Both for teacher and user type
    @GetMapping("/profile/edit")
    String editProfile(Model model){
        var roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("isTeacher", roles.contains(new SimpleGrantedAuthority("ROLE_TEACHER")));
        if(roles.contains(new SimpleGrantedAuthority("ROLE_TEACHER"))){
            model.addAttribute("teacher",teacherService.read(id));
            model.addAttribute("departments",departmentService.readAll());
            return "forms/profileTeacherForm";
        }else{
            model.addAttribute("user",userService.read(id));
            return "forms/profileForm";
        }


    }
    //Edit-form request. Valid for Both for teacher and user type
    @GetMapping("/profile/delete")
    public String deleteProfile(){
        var roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if(roles.contains(new SimpleGrantedAuthority("ROLE_TEACHER"))){
            String id = SecurityContextHolder.getContext().getAuthentication().getName();
            if(teacherService.read(id) != null){
                //Remove session (logout) and delete teacher
                SecurityContextHolder.clearContext();
                teacherService.delete(id);
                return "redirect:/";
            }
            else{
                return "errors/404";
            }
        }else if(roles.contains(new SimpleGrantedAuthority("ROLE_TEACHER"))){
            String id = SecurityContextHolder.getContext().getAuthentication().getName();
            if(userService.read(id) != null){
                //Remove session (logout) and delete teacher
                SecurityContextHolder.clearContext();
                userService.delete(id);
                return "redirect:/";
            }
            else{
                return "errors/404";
            }
        }else{
            return "errors/403";
        }
    }
    
    
    // SELF-EDIT METHODS //
    //Valid only for Users
    @PostMapping("/user/edit")
    String UpdateUser(Model model, @Validated(Groups.UserGroup.class) User user, BindingResult br){
        var roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if(roles.contains(new SimpleGrantedAuthority("ROLE_USER")) && !roles.contains(new SimpleGrantedAuthority("ROLE_TEACHER"))){
            if(br.hasErrors()){
                model.addAttribute("error",br.getAllErrors());
                return "forms/profileForm";
            }
            String id = SecurityContextHolder.getContext().getAuthentication().getName();
            userService.update(id,user);
            return "redirect:/profile";
        }else {
            return "errors/403";
        }
    }
    //Valid only for Teachers
    @PostMapping("/teacher/edit")
    String UpdateTeacher(Model model, @Validated(Groups.TeacherGroup.class) Teacher teacher, BindingResult br){
        var roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if(roles.contains(new SimpleGrantedAuthority("ROLE_TEACHER"))){
            if(br.hasErrors()){
                model.addAttribute("error",br.getAllErrors());
                model.addAttribute("departments",departmentService.readAll());
                return "forms/profileTeacherForm";
            }
            String id = SecurityContextHolder.getContext().getAuthentication().getName();
            teacherService.update(id,teacher);
            return "redirect:/profile";
        }else {
            return "errors/403";
        }
    }

    

    // ASIGNMENTS //

    //Admin-only management routes
    @GetMapping("/grade/assignTeacher")
    public String assignTeacherToGrade(Model model, @RequestParam Long id, @RequestParam String teacherId){
        if(gradeService.gradeExists(id) && teacherService.teacherExists(teacherId)) {
            if (teacherService.assignGrade(teacherId, id) != null) {
                return "redirect:/profile";
            } else {
                return "errors/error";
            }
        }else {
            return "errors/404";
        }
    }
    @GetMapping("/grade/assignUser")
    public String assignUserToGrade(Model model, @RequestParam Long id, @RequestParam String userId){
       if(gradeService.gradeExists(id) && userService.userExists(userId)) {
           if(userService.assignGrade(userId, id) != null){
               return getUserProfile(model,userId);
           }else{
               return "errors/error";
           }
       }else {
           return "errors/404";
       }
    }

    @GetMapping("/department/assignTeacher")
    public String assignTeacherToDepartment(Model model, @RequestParam Long id, @RequestParam String teacherId){
        if(teacherService.setDepartment(teacherId, id) != null){
            return "redirect:/teacher/"+teacherId;
        }else{
            return "errors/404";
        }
    }
    
    //Remove teacher/user from Grade
    @GetMapping("/grade/removeTeacher")
    public String removeTeacherFromGrade(Model model, @RequestParam Long id, @RequestParam String teacherId){
        if(teacherService.removeGrade(teacherId,id) != null){
            return "redirect:/grade/"+id;
        }else{
            return "errors/404";
        }
    }
    @GetMapping("/grade/removeUser")
    public String removeUserFromGrade(Model model, @RequestParam Long id, @RequestParam String userId){
        if(userService.removeGrade(userId,id) != null){
            return getUserProfile(model,userId);
        }else{
            return "errors/404";
        }
    }
    
    //Remove teacher from department
    @GetMapping("/department/remove")
    public String removeTeacherFromDept(Model model, @RequestParam String teacherId){
        if(teacherService.removeDept(teacherId) != null){
            return "redirect:/teacher/"+teacherId;
        }else{
            return "errors/404";
        }
    }

    // ENROLLING GRADE METHODS //

    //Self-enrolls a user into a grade
    @GetMapping("/grade/enroll")
    public String enroll(@RequestParam Long id){
        var roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Grade grade = gradeService.read(id);
        if(grade != null){
            //User must not be a teacher
            if( roles.contains(new SimpleGrantedAuthority("ROLE_USER")) && !roles.contains(new SimpleGrantedAuthority("ROLE_TEACHER"))){
                userService.assignGrade(userId,id);
                return "redirect:/grade/"+id;
            }else {
                return "errors/403";
            }
        }else {
            return "errors/404";
        }
        
    }

    //Self-departures a user/teacher from a grade.
    @GetMapping("/grade/leave")
    public String leave(Model model, @RequestParam long id){
        var roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if(roles.contains(new SimpleGrantedAuthority("ROLE_TEACHER"))){
            removeTeacherFromGrade(model,id,userId);
            return "redirect:/grade/"+id;
        }else if(roles.contains(new SimpleGrantedAuthority("ROLE_USER"))){
            removeUserFromGrade(model,id,userId);
            return "redirect:/grade/"+id;
        }else {
            return "errors/403";
        }
    }
    
    
    // OLD CONTROLLER METHODS //
    /*
    @PostMapping("/teacher/add")
    public String postTeacher(Model model,
                              @RequestParam String name,
                              @RequestParam String surname,
                              @RequestParam String pass,
                              @RequestParam String description,
                              @RequestParam int age){
        teacherService.create(new Teacher(name,surname.split(" ")[0],pass,description,age));
        return getTeachers(model);
    }
    @PostMapping("/department/add")
    public String postDepartment(Model model,
                                 @RequestParam String name,
                                 @RequestParam String location,
                                 @RequestParam String description){
        departmentService.create(new Department(name,location,description));
        return getDepartments(model);
    }
    @PostMapping("/grade/add")
    public String postGrade(Model model,
                            @RequestParam String name,
                            @RequestParam Integer year,
                            @RequestParam String description){
        gradeService.create(new Grade(name,description,year));
        return getGrades(model);
    }*/


}
