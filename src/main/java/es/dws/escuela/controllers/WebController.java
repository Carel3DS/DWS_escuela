package es.dws.escuela.controllers;

import es.dws.escuela.entities.*;
import es.dws.escuela.services.DepartmentService;
import es.dws.escuela.services.GradeService;
import es.dws.escuela.services.TeacherService;
import es.dws.escuela.services.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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


    @PostConstruct
    public void init(){
        //Create some instances
        //E-Mail are generated automatically
        //Passwords are encoded in the services layer
        Teacher teacher1 = new Teacher("Profesor","Uno","profesor1","Soy profesor 1",21);
        Teacher teacher2 = new Teacher("Profesor","Dos","profesor2","Soy profesor 2",23);
        User user = new User("user","o", "user","Hola mundo","USER");
        Teacher admin = new Teacher("admin","o", "admin",18,"Soy Admin","USER","TEACHER","ADMIN");
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
        teacherService.create(admin);
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
    public String login(User user, HttpServletRequest request, BindingResult br){
        if(request.getUserPrincipal()==null){
            return "home/login";
        }else {
            return "redirect:/";
        }
    }
    /*@PostMapping("/login")
    public String postLogin(User user, HttpServletRequest request, BindingResult br) throws ServletException {
        if(request.getUserPrincipal()==null){
            if(br.hasErrors()){
                return "home/login";
            }
            try {
                request.login(user.getId(), user.getPass());
                return "redirect:/profile";
            } catch (ServletException e){
                //Whatever it throws, for user it will be credentials error
                br.addError(new FieldError("user","credentials",e.getMessage()));
                return "home/login";
            }
        }else {
            return "redirect:/";
        }
    }*/
    @GetMapping("/signup")
    public String signup(User user, HttpServletRequest request){
        if(request.getUserPrincipal()==null){
            return "forms/signup";
        }else {
            return "redirect:/";
        }
    }

    @PostMapping("/signup")
    public String signup(Model model, HttpServletRequest request, @Validated(Groups.UserGroup.class) User user, BindingResult br)
            throws ServletException {
        if(request.getUserPrincipal() == null){
            if(br.hasErrors()){
                return "forms/signup";
            }

            user = new User(user.getName(),user.getSurname(),user.getPass());
            if(!userService.userExists(user.getId())){
                String pass = user.getPass();
                userService.create(user);
                try{
                    request.login(user.getId(), pass);
                    return "redirect:/profile";
                }catch (ServletException e){
                    log.error(e.toString());
                    throw e;
                }
            }else {
                return "errors/userExistsError";
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
            if(request.isUserInRole("ROLE_ADMIN")){
                model.addAttribute("teachers",teacherService.readAll());
            }
            return "entities/gradeProfile";
        }else{
            return "errors/404";
        }
    }
    @GetMapping("/department/{id}")
    public String getDepartmentProfile(Model model, @PathVariable Long id, HttpServletRequest request){
        Department department = departmentService.read(id);
        if(department != null){
            //Add the attributes into the model
            model.addAttribute("department",department);
            model.addAttribute("teacher",department.getTeachers());
            model.addAttribute("isAdmin",request.isUserInRole("ROLE_ADMIN"));
            model.addAttribute("belongs",request.getUserPrincipal() != null && teacherService.teacherExists(request.getUserPrincipal().getName()) && teacherService.read(request.getUserPrincipal().getName()).getDepartment()!= null && Objects.equals(teacherService.read(request.getUserPrincipal().getName()).getDepartment().getId(), id));
            if(request.isUserInRole("ROLE_ADMIN")){
                model.addAttribute("teachers",teacherService.readAll());
            }
            return "entities/departmentProfile";
        }else{
            return "errors/404";
        }
    }

    // ENTITY FORM REQUESTS //
    //Note: request the entity for binding
    @GetMapping("/teacher/add")
    public String getTeacherForm(Teacher teacher, HttpServletRequest request){
        if (request.getUserPrincipal() == null){
            return "forms/teacherSignup";
        }else {
            return "redirect:/";
        }
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/department/add")
    public String getDepartmentForm(Department department){
        return "forms/departmentForm";
    }
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping("/grade/add")
    public String getGradeForm(Grade grade){
        return "forms/gradeForm";
    }


    // ENTITY FORM POSTS //
    //Note: User post is signup() method, located at Functional features section
    @PostMapping("/teacher/add")
    public String postTeacher(Model model, HttpServletRequest request, @Validated(Groups.TeacherGroup.class) Teacher teacher, BindingResult br) throws ServletException {
        if(request.getUserPrincipal()==null){
            if(br.hasErrors()){
                return "forms/teacherSignup";
            }
            teacher = new Teacher(teacher.getName(),teacher.getSurname(),teacher.getPass(),teacher.getAge());
            if(!teacherService.teacherExists(teacher.getId())){
                String pass = teacher.getPass();
                teacherService.create(teacher);
                request.login(teacher.getId(), pass);
                return "redirect:/profile";
            }else {
                // Return error page if teacher exists
                return "errors/teacherExistsError";
            }
        }else{
            return "redirect:/";
        }
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/department/add")
    public String postDepartment(Model model, @Validated(Groups.DepartmentGroup.class) Department department, BindingResult br){
        var roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if(roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            if(br.hasErrors()){
                return "forms/departmentForm";
            }
            department.setTeachers(new ArrayList<>());
            Long id = departmentService.create(department).getId();
            return "redirect:/department/"+id;
        }else {
            return "errors/403";
        }
    }
    //Prevent inconsistent grades (grades with no teacher) by checking if they are teachers
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @PostMapping("/grade/add")
    public String postGrade(Model model, @Validated(Groups.GradeGroup.class) Grade grade, BindingResult br){
        String teacherId = SecurityContextHolder.getContext().getAuthentication().getName();
        if(teacherService.teacherExists(teacherId)){
            if(br.hasErrors()){
                return "forms/gradeForm";
            }
            /*//ad-hoc Not-null validation
            if(grade.getYear() == null){
                br.addError(new FieldError("grade","year","Age is required"));
                return "forms/gradeForm";
            }*/
            grade.setTeachers(new ArrayList<>());
            Teacher teacher = teacherService.read(teacherId);
            grade.addTeacher(teacher);

            Long id = gradeService.create(grade).getId();
            return "redirect:/grade/"+id;
        }else {
            //Only teachers can create grades
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
                model.addAttribute("teacher",teacher);
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
    public String updateTeacher(Model model, @PathVariable String id, @Validated(Groups.TeacherOptGroup.class) Teacher teacher, HttpServletRequest request, BindingResult br){
        if(request.isUserInRole("ROLE_ADMIN")){
            if(br.hasErrors()){
                model.addAttribute("profile", teacher);
                return "profileUserForm";
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
                model.addAttribute("user",user);
                model.addAttribute("isAdmin",true);
                return "profileUserForm";
            }else{
                return "errors/404";
            }
        }else {
            return "errors/403";
        }
    }
    @PostMapping("/user/edit/{id}")
    public String updateUser(Model model, @PathVariable String id, @Validated(Groups.UserOptGroup.class) User user, BindingResult br){
        var roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if(roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            if(br.hasErrors()){
                model.addAttribute("profile", user);
                return "profileUserForm";
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
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping("/grade/edit/{id}")
    public String editGrade(Model model, @PathVariable Long id, HttpServletRequest request){
        //Check if grade exists and belongs to the teacher he's requesting it or if it is admin
        if(gradeService.gradeExists(id) && (gradeService.read(id).getTeachers().contains(teacherService.read(request.getUserPrincipal().getName())) || request.isUserInRole("ROLE_ADMIN"))){
            Grade grade = gradeService.read(id);
            model.addAttribute("grade",grade);
            model.addAttribute("grade",grade);
            model.addAttribute("id",id);
            return "forms/gradeForm";
        }else{
            if(!gradeService.gradeExists(id)){
                return "errors/404";
            }else {
                return "errors/403";
            }
        }
    }
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @PostMapping("/grade/edit/{id}")
    public String updateGrade(@PathVariable Long id, Model model, @Validated(Groups.GradeOptGroup.class) Grade newGrade, HttpServletRequest request, BindingResult br){
        if(br.hasErrors()){
            model.addAttribute("grade", newGrade);
            return "forms/gradeForm";
        }
        //Check if grade exists and belongs to the teacher he's requesting it or if it is admin
        if(gradeService.read(id).getTeachers().contains(teacherService.read(request.getUserPrincipal().getName())) || request.isUserInRole("ROLE_ADMIN")){
            if(gradeService.update(id,newGrade) != null){
                return "redirect:/grade/"+id;
            }else {
                return "errors/404";
            }
        }else {
            return "errors/403";
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
    public String updateDepartment(@PathVariable Long id, Model model, @Validated(Groups.DepartmentOptGroup.class) Department newDept, BindingResult br){
        if(br.hasErrors()){
            model.addAttribute("department", newDept);
            return "profileUserForm";
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
    @GetMapping("/teacher/delete/{id}")
    public String deleteTeacher(Model model, @PathVariable String id, HttpServletRequest request){
        var roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if(roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            if(teacherService.read(id) != null && !request.getUserPrincipal().getName().equals(id)){
                Teacher teacher = teacherService.delete(id);
                model.addAttribute("confirm","Usuario eliminado");
                model.addAttribute("text","Se ha eliminado al profesor: "+teacher.getName()+" "+teacher.getSurname());
                return "home/confirm";
            }else{
                if(request.getUserPrincipal().getName().equals(id)){
                    return "errors/403";
                }else{
                    return "errors/404";
                }
            }
        }else {
            if(!teacherService.teacherExists(id)){
                return "errors/404";
            }else {
                //Prevent self delete (admins cannot be deleted)
                return "errors/403";
            }
        }
    }
    //User
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/user/delete/{id}")
    public String deleteUser(Model model, @PathVariable String id, HttpServletRequest request){
        var roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if(roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            if(userService.userExists(id) && !request.getUserPrincipal().getName().equals(id)){
                User user = userService.delete(id);
                model.addAttribute("confirm","Usuario eliminado");
                model.addAttribute("text","Se ha eliminado al usuario: "+user.getName()+" "+user.getSurname());
                return "home/confirm";
            }else{
                if(!userService.userExists(id)){
                    return "errors/404";
                }else {
                    //Prevent self delete (admins cannot be deleted)
                    return "errors/403";
                }
            }
        }else {
            return "errors/403";
        }
    }
    //Department
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/department/delete/{id}")
    public String deleteDepartment(Model model, @PathVariable Long id){
        if(departmentService.read(id) != null){
            departmentService.delete(id);
            return "redirect:/department";
        }else{
            return "errors/404";
        }
    }
    //Grade
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/grade/delete/{id}")
    public String deleteGrade(Model model, @PathVariable Long id, HttpServletRequest request){
        if(gradeService.gradeExists(id) && (gradeService.read(id).getTeachers().contains(teacherService.read(request.getUserPrincipal().getName())) || request.isUserInRole("ROLE_ADMIN"))){
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
            return "forms/profileUserForm";
        }


    }
    //Delete request. Valid for Both for teacher and user type, but not for admins
    @GetMapping("/profile/delete")
    public String deleteProfile(HttpSession session){
        var roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if(roles.contains(new SimpleGrantedAuthority("ROLE_TEACHER"))){
            String id = SecurityContextHolder.getContext().getAuthentication().getName();
            if(teacherService.teacherExists(id) && !roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
                //Delete teacher and logout
                session.invalidate();
                teacherService.delete(id);
                return "redirect:/";
            } else{
                if(roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
                    return "errors/403";
                }else{
                    return "errors/404";
                }
            }
        }else if(roles.contains(new SimpleGrantedAuthority("ROLE_USER")) && !roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            String id = SecurityContextHolder.getContext().getAuthentication().getName();
            if(userService.read(id) != null){
                //Remove session (logout) and delete teacher
                session.invalidate();
                userService.delete(id);
                return "redirect:/";
            }
            else{
                if(roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
                    return "errors/403";
                }else{
                    return "errors/404";
                }
            }
        }else{
            return "errors/403";
        }
    }
    
    
    // SELF-EDIT METHODS //
    //Valid only for Users
    @PostMapping("/user/edit")
    String updateUser(Model model, @Validated(Groups.UserOptGroup.class) User user, BindingResult br){
        var roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if(roles.contains(new SimpleGrantedAuthority("ROLE_USER")) && !roles.contains(new SimpleGrantedAuthority("ROLE_TEACHER"))){
            if(br.hasErrors()){
                model.addAttribute("error",br.getAllErrors());
                return "profileUserForm";
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
    String updateTeacher(Model model, @Validated(Groups.TeacherOptGroup.class) Teacher teacher, BindingResult br){
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
    //TODO: check if it works
    //TODO: make admin page
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

    @GetMapping("/department/assignTeachers")
    public String assignTeacherToDepartment(Model model, @RequestParam Long id, @RequestParam String teacherId){
        if(teacherService.setDepartment(teacherId, id) != null){
            return "redirect:/teacher/"+teacherId;
        }else{
            return "errors/404";
        }
    }
    
    //Modify teachers list of a Grade
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/grade/assignTeachers")
    public String assignTeachersToGrade(Model model, @RequestParam Long id, String ... teachers){
        if (teachers != null){
            List<String> teacherList = new ArrayList<>(List.of(teachers));
            if(gradeService.gradeExists(id)){
                //Create a new list to prevent reading by reference
                List<Teacher> oldTeachers = new ArrayList<>(gradeService.read(id).getTeachers());
                //Add the new teachers from the teachers list
                for (String teacherID: teacherList){
                    if(teacherService.teacherExists(teacherID) && !oldTeachers.contains(teacherService.read(teacherID))){
                        teacherService.assignGrade(teacherID, id);
                    }
                }
                //Remove the teachers that are not in the list. Check if the teacher really is in the grade
                for (Teacher t: oldTeachers){
                    if(!teacherList.contains(t.getName()) && t.getGrades().contains(gradeService.read(id))){
                        gradeService.removeTeacherFromGrade(id,t.getId());
                    }
                }
                return "redirect:/grade/"+id;
            }else {
                return "errors/404";
            }
        }else {
            return "redirect:/grade/"+id;
        }
    }
    //Modify teachers list of a Department
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/department/assignTeachers")
    public String assignTeachersToDept(Model model, @RequestParam Long id, String ... teachers){
        if(teachers != null){
            List<String> teacherList = new ArrayList<>(List.of(teachers));
            if(departmentService.departmentExists(id)){
                //Create a new list to prevent reading by reference
                List<Teacher> oldTeachers = new ArrayList<>(departmentService.read(id).getTeachers());
                //Add the new teachers from the teachers list
                for (String teacherID: teacherList){
                    if(teacherService.teacherExists(teacherID) && !oldTeachers.contains(teacherService.read(teacherID))){
                        teacherService.assignDept(teacherID, id);
                    }
                }
                //Remove the teachers that are not in the list. Check if the teacher really is in the department
                if(!oldTeachers.isEmpty()){
                    for (Teacher t: oldTeachers){
                        if(!teacherList.contains(t.getName()) && t.getDepartment().equals(departmentService.read(id))){
                            departmentService.removeTeacherFromDept(id,t);
                        }
                    }
                }
                return "redirect:/department/"+id;
            }else {
                return "errors/404";
            }
        }else {
            //Remove all teachers from the department
            if(departmentService.departmentExists(id)){
                departmentService.removeAllTeachersFromDept(id);
                return "redirect:/department/"+id;
            }else {
                return "errors/404";
            }
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
    //Note: Teachers cannot join to a grade (admins have to assign the grade to the teacher)
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
            gradeService.removeTeacherFromGrade(id,userId);
            return "redirect:/grade/"+id;
        }else if(roles.contains(new SimpleGrantedAuthority("ROLE_USER"))){
            gradeService.removeUserFromGrade(id,userId);
            return "redirect:/grade/"+id;
        }else {
            return "errors/403";
        }
    }
    //Departments (teachers only)
    @GetMapping("/department/join")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public String joinDept(@RequestParam Long id){
        var roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        String teacherId = SecurityContextHolder.getContext().getAuthentication().getName();
        Department department = departmentService.read(id);
        if(department != null){
            //User must not be a teacher
            if(roles.contains(new SimpleGrantedAuthority("ROLE_TEACHER"))){
                teacherService.assignDept(teacherId, id);
                return "redirect:/department/"+id;
            }else {
                return "errors/403";
            }
        }else {
            return "errors/404";
        }
    }
    @GetMapping("/department/leave")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public String leaveDept(){
        var roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        String teacherId = SecurityContextHolder.getContext().getAuthentication().getName();
        Department department = teacherService.read(teacherId).getDepartment();
        if(department != null){
            //User must not be a teacher
            if(roles.contains(new SimpleGrantedAuthority("ROLE_TEACHER"))){
                teacherService.removeDept(teacherId);
                return "redirect:"+department.getId();
            }else {
                return "errors/403";
            }
        }else {
            return "errors/404";
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
