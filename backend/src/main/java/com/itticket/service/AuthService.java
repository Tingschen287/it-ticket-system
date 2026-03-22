package com.itticket.service;

import com.itticket.dto.JwtResponse;
import com.itticket.dto.LoginRequest;
import com.itticket.dto.RegisterRequest;
import com.itticket.entity.Department;
import com.itticket.entity.Role;
import com.itticket.entity.User;
import com.itticket.repository.DepartmentRepository;
import com.itticket.repository.RoleRepository;
import com.itticket.repository.UserRepository;
import com.itticket.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setIsActive(true);

        // 设置默认角色
        Role userRole = roleRepository.findByCode("USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("普通用户");
                    role.setCode("USER");
                    role.setDescription("默认用户角色");
                    return roleRepository.save(role);
                });
        user.setRole(userRole);

        // 设置部门
        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElse(null);
            user.setDepartment(dept);
        }

        return userRepository.save(user);
    }

    public JwtResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        if (!user.getIsActive()) {
            throw new RuntimeException("账户已被禁用");
        }

        String token = jwtUtils.generateToken(user.getUsername());
        String roleCode = user.getRole() != null ? user.getRole().getCode() : "USER";

        return new JwtResponse(token, user.getId(), user.getUsername(), user.getEmail(), roleCode);
    }

    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
}
