package sit.int221.sastt3.services;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import sit.int221.sastt3.DTO.MatchPass;
import sit.int221.sastt3.entities.User;
import sit.int221.sastt3.exception.ErrorReport;
import sit.int221.sastt3.exception.InvalidRequestException;
import sit.int221.sastt3.exception.ItemNotFoundException;
import sit.int221.sastt3.repositories.UserRepo;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {
    @Autowired
    public UserRepo repo;

    public List<User> getAllUser(){
        return repo.findAllAndOrderByRoleThenUsernameAsc();
    }

    public User getUserById(Integer id) {
        return repo.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User id "+ id + " does not exist"));
    }
    public User createUser(User newUserDTO){
        try{
            String passwordRegex ="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#_])[A-Za-z\\d@$!%*?&#_]{8,14}$";
//            String emailRegex ="^[a-z0-9._]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            String emailRegex ="([a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9._-]+)";
            Map<String, String> errors = new HashMap<>();
            if (newUserDTO == null ){throw new InvalidRequestException("Cannot create User",errors);}

            //Username Validation
            if(newUserDTO.getUsername() == null){
                errors.put("username","must not be blank");}
            if(newUserDTO.getUsername()!=null){
                if (!checkUnique("username",newUserDTO.getUsername())) {
                    errors.put("username", "does not unique");}
                if (newUserDTO.getUsername().length() > 45 ){
                    errors.put("username","size must be between 1 and 45");}
                else if (newUserDTO.getUsername().trim().isEmpty()){
                    errors.put("username","must not be blank");}
            }
            //Password Validation
            if(newUserDTO.getPassword() == null){
                errors.put("password","must not be blank");}
            if(newUserDTO.getPassword()!=null){
                if (newUserDTO.getPassword().trim().isEmpty()){
                    errors.put("password","must not be blank");}
                 if (newUserDTO.getPassword().length() < 8 || newUserDTO.getPassword().length() > 14){
                    errors.put("password","size must be between 8 and 14");}
                 if (!newUserDTO.getPassword().trim().matches(passwordRegex)) {
                     System.out.println("Password does not match the pattern: " + newUserDTO.getPassword());
                    errors.put("password", "must be 8-14 characters long, at least 1 of uppercase, lowercase, number and special characters");
                }
            }

            //Name Validation
            if(newUserDTO.getName() == null){
                errors.put("name","must not be blank");}
            if(newUserDTO.getName()!=null){
                if (!checkUnique("name",newUserDTO.getName())) {
                    errors.put("name", "does not unique");
                }
                else if (newUserDTO.getName().length() > 100 ){
                    errors.put("name","size must be between 1 and 100");}
                else if(newUserDTO.getName().trim().isEmpty()){
                    errors.put("name","must not be blank");}
            }

            //Email Validation
            if(newUserDTO.getEmail() == null){
                errors.put("email","must not be blank");}
            if(newUserDTO.getEmail()!=null){
                if (!checkUnique("email", newUserDTO.getEmail())) {
                    errors.put("email", "does not unique");}
                if (!newUserDTO.getEmail().matches(emailRegex)) {
                    System.out.println('b');
                    errors.put("email","Email should be valid");}
                if (newUserDTO.getEmail().length() > 150 ){
                    errors.put("email","size must be between 1 and 150");}
               else  if(newUserDTO.getEmail().trim().isEmpty()){
                    errors.put("email","must not be blank");}
            }


            if (!errors.isEmpty()){
                throw new InvalidRequestException("User attributes validation failed!",errors);
            }
            User newUser = new User();
            Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 16, 16);
            String hashedPassword = argon2.hash(3, 4096, 1, newUserDTO.getPassword());
//            if(newUserDTO !=null){
                newUser.setUsername(newUserDTO.getUsername().trim());
                newUser.setName(newUserDTO.getName().trim());
                newUser.setEmail(newUserDTO.getEmail().trim());
                newUser.setRole(newUserDTO.getRole().trim());
                newUser.setPassword(hashedPassword);
//                repo.saveAndFlush(newUser);
//            }
            return repo.saveAndFlush(newUser);
        } catch (ItemNotFoundException exception){throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", exception);}
    }


    public User updateUser(Integer userId,User newUser){
        try {
//            String emailRegex ="^[a-z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            String emailRegex ="([a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9._-]+)";
            Map<String, String> errors = new HashMap<>();
            User ById =getUserById(userId);
            if (userId == null || userId <=0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user id: " + userId);
            }
            if (newUser == null ){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No fields to update");}

            if(newUser.getUsername() == null){
                errors.put("username","must not be blank");}
            if(newUser.getUsername()!=null){
                if(!ById.getUsername().equals(newUser.getUsername())) {
                    //Username Validation
                    if (!checkUnique("username", newUser.getUsername())) {
                        errors.put("username", "does not unique");
                    }
                }
                if (newUser.getUsername().length() > 45 ){
                    errors.put("username","size must be between 1 and 45");}
                else if (newUser.getUsername().trim().isEmpty()){
                    errors.put("username","must not be blank");}
            }

            //Name Validation
            if(newUser.getName() == null){
                errors.put("name","must not be blank");}
            if(newUser.getName()!=null){
                if(!ById.getName().equals(newUser.getName())) {
                    if (!checkUnique("name", newUser.getName())) {
                        errors.put("name", "does not unique");

                    }
                }
                if ( newUser.getName().length() > 100 ){
                    errors.put("name","size must be between 1 and 100");}
                else if(newUser.getName().trim().isEmpty()){
                    errors.put("name","must not be blank");}
            }

            //Email Validation
            if(newUser.getEmail() == null){
                errors.put("email","must not be blank");}
            if(newUser.getEmail()!=null){
                if(!ById.getEmail().equals(newUser.getEmail())) {
                    if (!checkUnique("email", newUser.getEmail())) {
                        errors.put("email", "does not unique");}
                }
                if (!newUser.getEmail().matches(emailRegex)) {
                    errors.put("email","Email should be valid");
                }
                if (newUser.getEmail().length() > 150){
                    errors.put("email","size must be between 1 and 150");}
                else if (newUser.getEmail().trim().isEmpty()){
                    errors.put("email","must not be blank");}
            }


            if (!errors.isEmpty()){
                throw new InvalidRequestException("User attributes validation failed!",errors);
            }
            User userEdit = repo.findById(userId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User id" + userId + "doesn't exist!!!"));
            userEdit.setUsername(newUser.getUsername().trim());
            userEdit.setName(newUser.getName().trim());
            userEdit.setRole(newUser.getRole().trim());
            userEdit.setEmail(newUser.getEmail().trim());
            return repo.saveAndFlush(userEdit);
        }
        catch (ItemNotFoundException exception){throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", exception);}
    }
    public void deleteUser(Integer id) {
        try {
            Map<String, String> errors = new HashMap<>();
            if (id == null || id <= 0) {
                throw new InvalidRequestException("Invalid user id: ", errors);
            }
            User user = getUserById(id);
            repo.delete(user);
        } catch (ItemNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", e);
        } catch (InvalidRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
    public Boolean checkUnique(String field, String data) {
        switch (field) {
            case "username":
                User userByUsername = repo.findByUsername(data);
                return userByUsername == null || userByUsername.getUsername().isEmpty();

            case "name":
                User userByName = repo.findByName(data);
                return userByName == null || userByName.getName().isEmpty();

            case "email":
                User userByEmail = repo.findByEmail(data);
                return userByEmail == null || userByEmail.getEmail().isEmpty();

            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid field name");
        }
    }


    public ResponseStatusException matchPassword(MatchPass data) {

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 16, 16);

        User user = repo.findByUsername(data.getUsername());
        if (user == null) {
            // User not found in the database
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, " Username is not correct");
        }


        String passByDatabase = user.getPassword();
        String pass = data.getPassword();

        // Call the service to check the provided password against the stored hash
        boolean passwordMatches = argon2.verify(passByDatabase,pass.toCharArray());

        if (passwordMatches) {
            // Password is correct, return a success response
            throw new ResponseStatusException(HttpStatus.OK, "User and Password are Valid");
        } else {
            // Password is incorrect, return an error response
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password is not correct");
        }

    }

}
