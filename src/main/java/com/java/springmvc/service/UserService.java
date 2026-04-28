package com.java.springmvc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.java.springmvc.domain.Role;
import com.java.springmvc.domain.User;
import com.java.springmvc.domain.dto.RegisterDTO;
import com.java.springmvc.repository.RoleRepository;
import com.java.springmvc.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UploadFileService uploadFileService;

    public UserService(UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            UploadFileService uploadFileService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.uploadFileService = uploadFileService;
    }

    public String Program() {
        return "hello service";
    }

    public User handleCreateUser(User user) {
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        user.setRole(this.handleGetRoleByName(user.getRole().getName()));
        return this.userRepository.save(user);
    }

    public List<User> handleGetAllUsers() {
        return this.userRepository.findAll();
    }

    public User handleGetUserById(Long id) {
        Optional<User> getUserById = this.userRepository.findById(id);

        if (getUserById.isPresent()) {
            return getUserById.get();
        }
        return null;
    }

    public User handleUpdateUserById(Long id, User user, MultipartFile file) {
        User updateUserById = this.handleGetUserById(id);
        updateUserById.setFullName(user.getFullName());
        updateUserById.setAddress(user.getAddress());
        updateUserById.setPhone(user.getPhone());

        Role updateRoleName = this.handleGetRoleByName(user.getRole().getName());
        updateUserById.setRole(updateRoleName);

        String fileName = file.getOriginalFilename();
        if (fileName != null && !fileName.equals("")) {
            String updateFileName = this.uploadFileService.handleStorefile(file, "avatar");
            if (!updateFileName.equals("")) {
                updateUserById.setAvatar(updateFileName);
            }
        }
        return this.userRepository.save(updateUserById);
    }

    public void handleDeleteUserById(Long id) {
        User deleteUserById = this.handleGetUserById(id);
        if (deleteUserById != null) {
            this.userRepository.deleteById(id);
        }
    }

    public Role handleGetRoleByName(String roleName) {
        return this.roleRepository.findByName(roleName);
    }

    public User regiterDTOtoUser(RegisterDTO registerDTO) {
        User user = new User();
        user.setFullName(registerDTO.getFirstName() + " " + registerDTO.getLastName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        return user;
    }

    public boolean checkEmailExists(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public User getUserByUserEmail(String email) {
        return this.userRepository.findByEmail(email);
    }
}
