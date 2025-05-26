package com.microservices.daos;

import com.microservices.dtos.CreateUserDTO;
import com.microservices.dtos.UserDTO;
import com.microservices.models.User;

import java.util.List;

public interface IUserDAO {
    User save(CreateUserDTO createUserDTO) throws Exception;
    UserDTO authenticate(String userEmail, String userPassword) throws Exception;
    User getUserById(int userId) throws Exception;
    List<User> getAllUsers() throws Exception;
    User updateUser(User user) throws Exception;
}
