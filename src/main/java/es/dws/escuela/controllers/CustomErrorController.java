package es.dws.escuela.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController implements ErrorController {
    //Request the error page
    @GetMapping("/error")
    public String handleError(HttpServletRequest request) {
        //Handle error code
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            //Get status code
            Integer statusCode = Integer.valueOf(status.toString());
            //If bad request Error
            if(statusCode == HttpStatus.BAD_REQUEST.value()) {
                return "errors/400";
            }
            //Forbidden error
            else if(statusCode == HttpStatus.FORBIDDEN.value()) {
                return "errors/403";
            }
            //If Not Found Error
            else if(statusCode == HttpStatus.NOT_FOUND.value()) {
                return "errors/404";
            }
            //If Method Not Allowed
            else if(statusCode == HttpStatus.METHOD_NOT_ALLOWED.value()) {
                return "errors/405";
            }
            //Internal Server Error
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "errors/500";
            }
            //Anything else
            else{
                return "errors/error";
            }
        }
        //Normal error page (shows if user access in a normal way)
        return "errors/errorPage";
    }
    //Shows 400-Error page (normal way)
    @GetMapping("/400")
    public String error400(){
        return "errors/400";
    }
    //Shows 403-Error page (normal way)
    @GetMapping("/403")
    public String error403(){
        return "errors/403";
    }
    //Shows 404-Error page (normal way)
    @GetMapping("/404")
    public String error404(){
        return "errors/404";
    }
    //Shows 404-Error page (normal way)
    @GetMapping("/405")
    public String error405(){
        return "errors/405";
    }
    //Shows 500-Error page (normal way)
    @GetMapping("/500")
    public String error500(){
        return "errors/500";
    }

    @GetMapping("/teapot")
    public String error418(){
        return "errors/teapot";
    }
}
