package com.shop.admin.user;

import com.shop.commom.entity.Role;
import com.shop.commom.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTests {

    @Autowired
    private UserRepository repo;
    @Autowired
    private TestEntityManager testEntityManager;


    @Test
    public void testCreateNewUserWithOneRole() {
        Role roleAdmin = testEntityManager.find(Role.class,1);
        User userVanThe= new User("vanthe@gmail.com", "vanthe","the","vanthe");
        userVanThe.addRole(roleAdmin);
        
       User saveUser= repo.save(userVanThe);
       assertThat(saveUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateNewUserWithTwoRole() {

        User userRavi = new User("ravi@gmail.com", "ravi","the","ravi");
        Role roleEditor = new Role(3);
        Role roleAssistant = new Role(5);
        userRavi.addRole(roleEditor);
        userRavi.addRole(roleAssistant);

        User saveUser= repo.save(userRavi);
        assertThat(saveUser.getId()).isGreaterThan(0);
    }


    @Test
    public void testListAllUsers() {
        Iterable<User> listUsers = repo.findAll();
        listUsers.forEach(user -> System.out.println(user));
    }

    @Test
    public void testGetUserById() {
        User user = repo.findById(1).get();
        System.out.println(user);
        assertThat(user).isNotNull();
    }

    @Test
    public void testUpdateUserDetails() {
        User user = repo.findById(1).get();
        user.setEnabled(true);
        user.setEmail("vanthe1@gmail.com");

        repo.save(user);
    }

    @Test
    public void testUpdateUserRoles() {
        User user = repo.findById(2).get();
        Role roleEditor = new Role(3);
        Role roleSalesperson = new Role(2);

        user.getRoles().remove(roleEditor);
        user.addRole(roleSalesperson);

        repo.save(user);
    }


    @Test
    public void deleteUser() {
        Integer userId = 2;
        repo.deleteById(userId);
    }

    

}
