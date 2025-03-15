package com.shop.admin.user;

import com.shop.commom.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private  UserRepository userRepo;

    public List<User> listAll(){
        return (List<User>) userRepo.findAll();
    }
}
