package by.free.home.controller;

import by.free.home.entity.Role;
import by.free.home.entity.UserStatus;
import by.free.home.entity.UserAccount;
import by.free.home.entity.UserAccountDTO;
import by.free.home.service.UserAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path = "/")
@Slf4j
public class UserController {
    @Autowired
    private UserAccountService userAccountService;

    @GetMapping(path = "/login")
    public ModelAndView getLoginPage(ModelAndView modelAndView) {
        modelAndView.setViewName("login");
        log.info("GET authorization page!");
        return modelAndView;
    }

    @GetMapping(path = "/user/new")
    public ModelAndView getNewUserCreationPage(ModelAndView modelAndView) {
        modelAndView.setViewName("new");
        log.info("GET user creation page!");
        modelAndView.addObject("roles", Role.values());
        modelAndView.addObject("userStatuses", UserStatus.values());
        modelAndView.addObject("newUserAccountDTO", new UserAccountDTO());
        return modelAndView;
    }

    @PostMapping(path = "/user/new")
    public ModelAndView createNewUser(@Valid @ModelAttribute("newUserAccountDTO")UserAccountDTO userAccountDTO, BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes, ModelAndView modelAndView) {
        modelAndView.setViewName("new");
        if (bindingResult.hasErrors()) {
            Map<String, String> fieldErrors = new HashMap<>();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            log.warn("Follows validating errors during user creation: "+fieldErrors);
            redirectAttributes.addFlashAttribute("errors", fieldErrors);
        } else {
            UserAccount userAccountFromDTO = (UserAccount) userAccountService.createUserAccountFromDTO(userAccountDTO);
            log.info("User Account from DTO: "+userAccountFromDTO);
            boolean isSaved = (boolean) userAccountService.saveNewUserInDatabase(userAccountFromDTO);
            if (isSaved) {
                redirectAttributes.addFlashAttribute("result", "New user has been saved!");
            }
        }
        modelAndView.setViewName("redirect:/user/new");
        return modelAndView;
    }

    @GetMapping(path = "/user/{id}")
    public ModelAndView getPageWithUserInfo(@PathVariable("id") long id, ModelAndView modelAndView) {
        modelAndView.setViewName("view");
        modelAndView.addObject("userStatuses", UserStatus.values());
        if (id > 0) {
            UserAccount userById = (UserAccount) userAccountService.getUserById(id);
            log.info("User with id ["+id+"] has been found: "+userById);
            modelAndView.addObject("user", userById);
        } else {
            log.error("User not found: id ["+id+"] is not valid!");
        }
        return modelAndView;
    }

    @PostMapping(path = "/user/")
    public ModelAndView changeUserStatus(@RequestParam("id")long id, @RequestParam("userStatus") UserStatus userStatus,
                                         RedirectAttributes redirectAttributes, ModelAndView modelAndView) {
        modelAndView.setViewName("view");
        UserAccount userById = (UserAccount) userAccountService.getUserById(id);
        boolean isChanged = (boolean) userAccountService.changeUserStatus(userById, userStatus);
        log.info("Id for user update status ["+id+"]");
        if (isChanged) {
            log.info("User status has been changed! User id ["+id+"]. New status ["+userStatus+"].");
            redirectAttributes.addFlashAttribute("changed", true);
        } else {
            log.error("User status not changed!");
            redirectAttributes.addFlashAttribute("notChanged", true);
        }
        modelAndView.setViewName("redirect:/user/"+id);
        return modelAndView;
    }

    @GetMapping(path = "/user/{id}/edit")
    public ModelAndView getEditPage(@PathVariable("id")long id, ModelAndView modelAndView) {
        if (id > 0) {
            modelAndView.setViewName("edit");
            log.info("ID for user updating is valid!");
            UserAccount userById = (UserAccount) userAccountService.getUserById(id);
            modelAndView.addObject("username", "username");
            modelAndView.addObject("roles", Role.values());
            modelAndView.addObject("userStatuses", UserStatus.values());
            modelAndView.addObject("currentUserAccount", userById);
            log.info("User for update: "+userById);
            modelAndView.addObject("newUserAccountDTO", new UserAccountDTO());
        } else {
            log.error("Redirect to 404 page! Id is not valid!");
            modelAndView.setViewName("redirect:404");
        }
        return modelAndView;
    }

    @PostMapping(path = "/user/edit")
    public ModelAndView editExistingUser(@Valid @ModelAttribute("newUserAccountDTO") UserAccountDTO userAccountDTO, BindingResult bindingResult,
                                         @RequestParam("id")long id, RedirectAttributes redirectAttributes, ModelAndView modelAndView) {
        modelAndView.setViewName("edit");
        log.info("New UserAccountDTO for data transfer: "+userAccountDTO);
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            log.warn("Follows validating errors: "+errors);
            redirectAttributes.addFlashAttribute("errors", errors);
            log.error("Validation errors during user editing!");
        } else {
            boolean isUpdated = (boolean) userAccountService.editExistingUserById(id, userAccountDTO);
            if (isUpdated) {
                log.info("User with Id ["+id+"] has been updated!");
                redirectAttributes.addFlashAttribute("result", "User has been updated!");
            }
        }
        modelAndView.setViewName("redirect:/user/"+id+"/edit");
        return modelAndView;
    }

    @GetMapping(path = "/users/{pageNumber}")
    public ModelAndView getPageWithAllUsersAccount(@PathVariable("pageNumber")int pageNumber,HttpSession httpSession,
                                                   ModelAndView modelAndView) {
        modelAndView.setViewName("list");
        Role role = (Role) httpSession.getAttribute("roleSort");
        String sortStrategy = (String) httpSession.getAttribute("sortStrategy");
        log.info("Filter parameters: role=["+role+"], sort=["+sortStrategy+"].");
        if (role!=null) {
            log.info("Filtering by role ["+role+"].");
            List<UserAccount> allUsersByRole = (List<UserAccount>) userAccountService.getAllUsersByRole(pageNumber, role, sortStrategy);
            modelAndView.addObject("allUsers", allUsersByRole);
        } else {
            log.info("Show users without role filter!");
            List<UserAccount> allUsers = (List<UserAccount>) userAccountService.getAllUsers(pageNumber, sortStrategy);
            modelAndView.addObject("allUsers", allUsers);
        }
        modelAndView.addObject("roles", Role.values());
        modelAndView.addObject("sizeOfPage", UserAccountService.NUMBER_ELEMENTS_ON_PAGE);
        return modelAndView;
    }

    @PostMapping(path = "/users/{pageNumber}")
    public ModelAndView filterUsersByRole(@PathVariable("pageNumber")int pageNumber, @RequestParam("role")Role role,
                                          HttpSession httpSession, ModelAndView modelAndView) {
        modelAndView.setViewName("list");
        log.info("POST method set role flag ["+role+"] for filtering!");
        httpSession.setAttribute("roleSort", role);
        modelAndView.setViewName("redirect:/users/"+pageNumber);
        return modelAndView;
    }

    @PostMapping(path = "/users/sort/{pageNumber}")
    public ModelAndView sortByUsername(@PathVariable("pageNumber")int pageNumber, @RequestParam("sortStrategy")String sortStrategy,
                                       HttpSession httpSession, ModelAndView modelAndView) {
        modelAndView.setViewName("list");
        log.info("POST method set strategy of sort flag ["+sortStrategy+"] for filtering!");
        httpSession.setAttribute("sortStrategy", sortStrategy);
        modelAndView.setViewName("redirect:/users/"+pageNumber);
        return modelAndView;
    }

    @PostMapping(path = "/users/break")
    public ModelAndView deleteAllFilters(HttpSession httpSession, ModelAndView modelAndView) {
        String sortFlag = "sortStrategy";
        String roleFlag = "roleSort";
        if (httpSession.getAttribute(sortFlag) != null) {
            log.info("Delete sort strategy filter flag!");
            httpSession.removeAttribute(sortFlag);
        }
        if (httpSession.getAttribute(roleFlag) != null) {
            log.info("Delete role filter flag!");
            httpSession.removeAttribute(roleFlag);
        }
        modelAndView.setViewName("redirect:/users/1");
        return modelAndView;
    }
}
