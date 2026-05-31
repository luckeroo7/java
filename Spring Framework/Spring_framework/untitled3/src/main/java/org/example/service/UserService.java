package org.example.service;

import org.example.entity.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository repo;

    public void save(String name, int age) {
        User u = new User();
        u.setName(name);
        u.setAge(age);
        repo.save(u);
    }

    public List<User> findAll() {
        return repo.findAll();
    }

    public List<User> findByName(String name) {
        return repo.findByName(name);
    }
}
