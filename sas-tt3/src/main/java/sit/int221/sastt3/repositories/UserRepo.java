package sit.int221.sastt3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sit.int221.sastt3.DTO.MatchPass;
import sit.int221.sastt3.entities.Announcement;
import sit.int221.sastt3.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

//public interface UserRepo extends JpaRepository<User,Integer> {
//    @Query("SELECT u FROM User u ORDER BY u.role ASC, u.username ASC")
//    List<User> findAllAndOrderByRoleThenUsernameAsc();
//
//
//
//
//}

public interface UserRepo extends CustomRepository  <User,Integer> {
    @Query("SELECT u FROM User u ORDER BY u.role ASC, u.username ASC")
    List<User> findAllAndOrderByRoleThenUsernameAsc();
//    @Query("SELECT u FROM User u WHERE u.username=:username")
//    User findByUsername(String username);
//    @Query("SELECT u FROM User u WHERE u.name=:name")
//    User findByName(String name);
//    @Query("SELECT u FROM User u WHERE u.email=:email")
//    User findByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.name=:name")
    User findByName(String name);
    @Query("SELECT u FROM User u WHERE u.username=:username")
    User findByUsername(String username);
    @Query("SELECT u FROM User u WHERE u.email=:email")
    User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.password=:pass")
    User findByPassword(String pass);


}

