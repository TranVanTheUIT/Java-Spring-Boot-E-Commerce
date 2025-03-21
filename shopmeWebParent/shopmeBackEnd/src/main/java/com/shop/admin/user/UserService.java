package com.shop.admin.user;

import com.shop.commom.entity.Role;
import com.shop.commom.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private  UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> listAll(){
        return (List<User>) userRepo.findAll();
    }

    public List<Role> listRoles(){
        return (List<Role>) roleRepo.findAll();
    }

    public void save(User user) {

        boolean isUpdatingUser = (user.getId() != null);
        if (isUpdatingUser) {
            User existingUser = userRepo.findById(user.getId()).get();

            if (user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            }else {
                encodePassword(user);
            }

        }else {
            encodePassword(user);
        }

        encodePassword(user);
        userRepo.save(user);
    }

    private void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    public boolean isEmailUnique(Integer id, String email) {

        User userByEmail = userRepo.getUserByEmail(email);

        if (userByEmail == null) return true;
        boolean isCreatingNew = (id == null);
        if(isCreatingNew){
            if(userByEmail != null) return false;
        }else{
            if(userByEmail.getId() != id){
                return false;
            }
        }
        return true;
    }

    public User get(Integer id) throws UserNotFoundException {
        try {
            return userRepo.findById(id).get();
        } catch (Exception e) {
            throw new UserNotFoundException("Could not find any user with ID " + id);
        }

    }

    public void delete(Integer id) throws UserNotFoundException {
        Long countById = userRepo.countById(id);
        if(countById == null || countById == 0){
            throw new UserNotFoundException("Could not find any user with ID " + id);
        }
        userRepo.deleteById(id);
    }
}
