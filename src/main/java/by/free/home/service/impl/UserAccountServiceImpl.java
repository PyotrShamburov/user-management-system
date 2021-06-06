package by.free.home.service.impl;

import by.free.home.entity.Role;
import by.free.home.entity.UserStatus;
import by.free.home.entity.UserAccount;
import by.free.home.entity.UserAccountDTO;
import by.free.home.entity.exception.UserAlreadyExistException;
import by.free.home.entity.exception.UserNotFoundException;
import by.free.home.repository.UserAccountRepository;
import by.free.home.service.UserAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserAccountServiceImpl implements UserAccountService, UserDetailsService {

    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        log.info("Request for User Detail with name ["+s+"].");
        return userAccountRepository.findByUsername(s);
    }

    @Override
    public List<UserAccount> getAllUsers(int pageNumber, String sortStrategy) {
        Pageable paging = (Pageable) getPageableWithSortOrWithout(pageNumber, sortStrategy);
        Page<UserAccount> page = userAccountRepository.findAll(paging);
        List<UserAccount> users = page.getContent();
        log.info("Request for get all users of system!");
        return users;
    }

    @Override
    public List<UserAccount> getAllUsersByRole(int pageNumber, Role role, String sortStrategy) {
        Pageable paging = (Pageable) getPageableWithSortOrWithout(pageNumber, sortStrategy);
        Page<UserAccount> page = userAccountRepository.findAllByRole(paging, role);
        List<UserAccount> usersByRole = (List<UserAccount>) page.getContent();
        log.info("Request for get all users of system by role ["+role+"]!");
        return usersByRole;
    }

    @Override
    public UserAccount getUserById(long id) {
        Optional<UserAccount> byId = (Optional<UserAccount>) userAccountRepository.findById(id);
        if (byId.isPresent()) {
            log.info("User with id ["+id+"] is exists!");
            return byId.get();
        }
        log.error("User with id ["+id+"] not found!");
        throw new UserNotFoundException("User with this id not found!");
    }

    @Override
    public boolean saveNewUserInDatabase(UserAccount userAccount) {
        containsUserWithSameUsername(userAccount);
        encodeUserPassword(userAccount);
        if (userAccount.getCreatedAt() == null) {
            log.info("Time of creation is null! It will be set here!");
            setDateOfCreatingAccount(userAccount);
        }
        log.info("User save in database! User: "+userAccount);
        userAccountRepository.save(userAccount);
        return true;
    }

    @Override
    public boolean editExistingUserById(long id, UserAccountDTO userAccountDTO) {
        UserAccount userAccountFromDTO = (UserAccount) createUserAccountFromDTO(userAccountDTO);
        log.info("User DTO for update existing user: "+userAccountDTO);
        String createdAt = (String) getUserById(id).getCreatedAt();
        userAccountFromDTO.setCreatedAt(createdAt);
        userAccountFromDTO.setId(id);
        return saveNewUserInDatabase(userAccountFromDTO);
    }

    @Override
    public UserAccount createUserAccountFromDTO(UserAccountDTO userAccountDTO) {
        UserAccount newUserAccount = new UserAccount();
        log.info("User DTO for data transfer: "+userAccountDTO);
        String username = (String) userAccountDTO.getUsername();
        String password = (String) userAccountDTO.getPassword();
        String firstName = (String) userAccountDTO.getFirstName();
        String lastName = (String) userAccountDTO.getLastName();
        Role role = (Role) userAccountDTO.getRole();
        UserStatus userStatus = (UserStatus) userAccountDTO.getUserStatus();
        newUserAccount.setUsername(username);
        newUserAccount.setPassword(password);
        newUserAccount.setFirstName(firstName);
        newUserAccount.setLastName(lastName);
        newUserAccount.setRole(role);
        newUserAccount.setUserStatus(userStatus);
        log.info("Ready for save created user from DTO :"+newUserAccount);
        return newUserAccount;
    }

    @Override
    public boolean changeUserStatus(UserAccount userAccount, UserStatus newUserStatus) {
        UserStatus userStatus = (UserStatus) userAccount.getUserStatus();
        log.info("New status for user ["+userStatus+"]. User: "+userAccount);
        if (userStatus.equals(newUserStatus)) {
            log.warn("User already has ["+userStatus+"] status!");
            return false;
        } else {
            log.info("Status ["+userStatus+"] has been changed to ["+newUserStatus+"]. User info updated!");
            userAccount.setUserStatus(newUserStatus);
            userAccountRepository.save(userAccount);
            return true;
        }
    }

    @Override
    public void containsUserWithSameUsername(UserAccount userAccount) {
        String username = (String) userAccount.getUsername();
        log.info("Contains check in database user with username["+username+"]!");
        if (userAccountRepository.existsByUsername(username)) {
            log.error("User with this username ["+username+"] already exists!");
            throw new UserAlreadyExistException("User with this username already exists!");
        }
    }

    @Override
    public void setDateOfCreatingAccount(UserAccount userAccount) {
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedString = localDate.format(formatter);
        log.info("Set to user with Id["+userAccount.getId()+"] date of creation: "+formattedString);
        userAccount.setCreatedAt(formattedString);
    }

    @Override
    public void encodeUserPassword(UserAccount userAccount) {
        String password = (String) userAccount.getPassword();
        String encodedPassword = (String) passwordEncoder.encode(password);
        log.info("Set to user with Id["+userAccount.getId()+"] encoded password : "+encodedPassword);
        userAccount.setPassword(encodedPassword);
    }

    @Override
    public Sort getSortFromStrategy(String sortStrategy) {
        Sort sort;
        if ("asc".equals(sortStrategy)) {
            log.info("Create sort with strategy [ASC]!");
            sort = Sort.by("username").ascending();
        } else {
            log.info("Create sort with strategy [DESC]!");
            sort = Sort.by("username").descending();
        }
        return sort;
    }

    @Override
    public Pageable getPageableWithSortOrWithout(int pageNumber, String sortStrategy) {
        Pageable paging;
        if (sortStrategy == null) {
            log.info("Get paging without sort!");
            paging = PageRequest.of(pageNumber-1, NUMBER_ELEMENTS_ON_PAGE);
        } else {
            log.info("Get paging with sort!");
            Sort sort = (Sort) getSortFromStrategy(sortStrategy);
            paging = PageRequest.of(pageNumber-1, NUMBER_ELEMENTS_ON_PAGE, sort);
        }
        return paging;
    }
}
