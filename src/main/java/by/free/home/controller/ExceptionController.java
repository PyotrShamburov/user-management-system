package by.free.home.controller;

import by.free.home.entity.exception.UserAlreadyExistException;
import by.free.home.entity.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler(UserNotFoundException.class)
    public String UserNotFound(UserNotFoundException ex, Model model) {
        log.error("Exception handler [UserNotFound] in work! Reason: "+ex.getMessage());
        model.addAttribute("message", ex.getMessage());
        return "errorPage";
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public String UserAlreadyExist(UserAlreadyExistException ex, Model model) {
        log.error("Exception handler [UserAlreadyExist] in work! Reason: "+ex.getMessage());
        model.addAttribute("message", ex.getMessage());
        return "errorPage";
    }
}
