package by.free.home.service;

import by.free.home.entity.Role;
import by.free.home.entity.UserStatus;
import by.free.home.entity.UserAccount;
import by.free.home.entity.UserAccountDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserAccountService {
    int NUMBER_ELEMENTS_ON_PAGE = 5;
    List<UserAccount> getAllUsers(int pageNumber, String sortStrategy);
    List<UserAccount> getAllUsersByRole(int pageNumber, Role role, String sortStrategy);
    UserAccount getUserById(long id);
    boolean saveNewUserInDatabase(UserAccount userAccount);
    boolean editExistingUserById(long id, UserAccountDTO userAccountDTO);
    UserAccount createUserAccountFromDTO(UserAccountDTO userAccountDTO);
    boolean changeUserStatus(UserAccount userAccount, UserStatus newUserStatus);
    void containsUserWithSameUsername(UserAccount userAccount);
    void setDateOfCreatingAccount(UserAccount userAccount);
    void encodeUserPassword(UserAccount userAccount);
    Sort getSortFromStrategy(String sortStrategy);
    Pageable getPageableWithSortOrWithout(int pageNumber, String sortStrategy);

}
