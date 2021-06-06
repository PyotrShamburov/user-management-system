package by.free.home.repository;

import by.free.home.entity.Role;
import by.free.home.entity.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;


public interface UserAccountRepository extends CrudRepository<UserAccount, Long> {
    boolean existsByUsername(String username);
    UserAccount findByUsername(String username);
    Page<UserAccount> findAll(Pageable pageable);
    Page<UserAccount> findAllByRole(Pageable pageable, Role role);
}
