package by.free.home.service.impl;

import by.free.home.entity.Role;
import by.free.home.entity.UserAccount;
import by.free.home.entity.UserAccountDTO;
import by.free.home.entity.UserStatus;
import by.free.home.entity.exception.UserAlreadyExistException;
import by.free.home.repository.UserAccountRepository;
import by.free.home.service.UserAccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserAccountServiceImplTest {

    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private UserAccountService userAccountService;

    @Test
    void getAllUsers() {
        long expectedCount = UserAccountService.NUMBER_ELEMENTS_ON_PAGE;
        String sortStrategy = "asc";
        int pageNumber = 1;
        List<UserAccount> allUsers = (List<UserAccount>) userAccountService.getAllUsers(pageNumber, sortStrategy);
        long actualCount = (long) allUsers.size();
        assertEquals(expectedCount, actualCount);
    }

    @Test
    void getAllUsersByRole() {
        long expectedCount = UserAccountService.NUMBER_ELEMENTS_ON_PAGE;
        String sortStrategy = "asc";
        int pageNumber = 1;
        Role expectedRole = Role.USER;
        List<UserAccount> allUsersByRole = (List<UserAccount>) userAccountService.getAllUsersByRole(pageNumber, expectedRole, sortStrategy);
        int roleCount = 0;
        for (UserAccount userAccount : allUsersByRole) {
            if (userAccount.getRole().equals(expectedRole)) {
                ++roleCount;
            }
        }
        assertEquals(expectedCount, roleCount);
    }

    @Test
    void getUserById() {
        long idOfUser = 1;
        Optional<UserAccount> byId = (Optional<UserAccount>) userAccountRepository.findById(idOfUser);
        UserAccount expectedAccount = null;
        if (byId.isPresent()) {
            expectedAccount = (UserAccount) byId.get();
        }
        UserAccount actualUserAccount = (UserAccount) userAccountService.getUserById(idOfUser);
        assertEquals(expectedAccount, actualUserAccount);
    }

    @Test
    void saveNewUserInDatabase() {
        long idOfUser = 1;
        Optional<UserAccount> byId = (Optional<UserAccount>) userAccountRepository.findById(idOfUser);
        UserAccount expectedAccount = byId.get();
        Throwable thrown = assertThrows(UserAlreadyExistException.class,
                ()->userAccountService.saveNewUserInDatabase(expectedAccount));
        assertNotNull(thrown.getMessage());
    }

    @Test
    void editExistingUserById() {
        long idOfUser = 1;
        Optional<UserAccount> byId = (Optional<UserAccount>) userAccountRepository.findById(idOfUser);
        UserAccount expectedAccount = byId.get();
        expectedAccount.setFirstName("test");
        expectedAccount.setLastName("test");
        expectedAccount.setRole(Role.USER);
        Throwable thrown = assertThrows(UserAlreadyExistException.class,
                ()->userAccountService.saveNewUserInDatabase(expectedAccount));
        assertNotNull(thrown.getMessage());
    }

    @Test
    void createUserAccountFromDTO() {
        UserAccountDTO userAccountDTO = new UserAccountDTO("test","test", "test", "test", Role.USER, UserStatus.INACTIVE,"01-01-1970");
        UserAccount expectedUserAccount = new UserAccount(1,"test", "test", "test", "test", Role.USER, UserStatus.INACTIVE,"01-01-1970");
        UserAccount actualUserAccount = (UserAccount) userAccountService.createUserAccountFromDTO(userAccountDTO);
        assertEquals(expectedUserAccount, actualUserAccount);
    }

    @Test
    void changeUserStatus() {
        UserAccount userById = (UserAccount) userAccountService.getUserById(1);
        UserStatus expectedStatus = userById.getUserStatus();
        boolean isChanged = (boolean) userAccountService.changeUserStatus(userById, expectedStatus);
        assertFalse(isChanged);
    }

    @Test
    void containsUserWithSameUsername() {
        long idOfUser = 1;
        Optional<UserAccount> byId = (Optional<UserAccount>) userAccountRepository.findById(idOfUser);
        UserAccount expectedAccount = byId.get();
        Throwable thrown = assertThrows( UserAlreadyExistException.class, ()->
                userAccountService.containsUserWithSameUsername(expectedAccount));
        assertNotNull(thrown.getMessage());


    }

    @Test
    void setDateOfCreatingAccount() {
        UserAccount expectedUserAccount = new UserAccount(1,"test", "test", "test", "test", Role.USER, UserStatus.INACTIVE,"01-01-1970");
        userAccountService.setDateOfCreatingAccount(expectedUserAccount);
        Pattern datePattern = Pattern.compile("^[0-9]{2}-[0-9]{2}-[0-9]{4}$");
        String createdAt = (String) expectedUserAccount.getCreatedAt();
        boolean isMatches = (boolean) datePattern.matcher(createdAt).matches();
        assertTrue(isMatches);
    }

    @Test
    void encodeUserPassword() {
        UserAccount expectedUserAccount = new UserAccount(1,"test", "test", "test", "test", Role.USER, UserStatus.INACTIVE,"01-01-1970");
        String oldPassword = expectedUserAccount.getPassword();
        userAccountService.encodeUserPassword(expectedUserAccount);
        String newPassword = expectedUserAccount.getPassword();
        assertNotEquals(oldPassword, newPassword);
    }

    @Test
    void getSortFromStrategy() {
        String sortStrategy = "asc";
        Sort expectedSort = Sort.by("username").ascending();
        Sort actualSort = (Sort) userAccountService.getSortFromStrategy(sortStrategy);
        assertEquals(expectedSort, actualSort);
    }

    @Test
    void getPageableWithSortOrWithout() {
        int pageNumber = 1;
        String sortStrategy = "asc";
        Pageable paging = (Pageable) userAccountService.getPageableWithSortOrWithout(pageNumber, sortStrategy);
        boolean isSorted = (boolean) paging.getSort().isSorted();
        assertTrue(isSorted);
    }
}