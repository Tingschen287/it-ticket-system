package com.itticket.config;

import com.itticket.entity.Department;
import com.itticket.entity.Role;
import com.itticket.entity.Sla;
import com.itticket.entity.User;
import com.itticket.repository.DepartmentRepository;
import com.itticket.repository.RoleRepository;
import com.itticket.repository.SlaRepository;
import com.itticket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final SlaRepository slaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("开始初始化数据...");

        initRoles();
        initDepartments();
        initAdminUser();
        initSlas();

        log.info("数据初始化完成！");
    }

    private void initRoles() {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setName("管理员");
            adminRole.setCode("ADMIN");
            adminRole.setDescription("系统管理员，拥有所有权限");
            adminRole.setPermissions("*");
            roleRepository.save(adminRole);

            Role pmRole = new Role();
            pmRole.setName("产品经理");
            pmRole.setCode("PM");
            pmRole.setDescription("产品经理，可以管理工单和查看统计");
            pmRole.setPermissions("ticket:read,ticket:write,stats:read");
            roleRepository.save(pmRole);

            Role devRole = new Role();
            devRole.setName("程序员");
            devRole.setCode("DEVELOPER");
            devRole.setDescription("开发人员，可以处理分配的工单");
            devRole.setPermissions("ticket:read,ticket:write");
            roleRepository.save(devRole);

            Role userRole = new Role();
            userRole.setName("普通用户");
            userRole.setCode("USER");
            userRole.setDescription("普通用户，可以提交和查看自己的工单");
            userRole.setPermissions("ticket:read");
            roleRepository.save(userRole);

            log.info("已初始化角色数据");
        }
    }

    private void initDepartments() {
        if (departmentRepository.count() == 0) {
            String[][] departments = {
                    {"IT", "信息技术部", "负责公司IT基础设施和系统开发"},
                    {"产品", "产品部", "负责产品规划和需求管理"},
                    {"运营", "运营部", "负责日常运营和用户支持"},
                    {"测试", "测试部", "负责系统测试和质量保证"}
            };

            for (String[] dept : departments) {
                Department department = new Department();
                department.setCode(dept[0]);
                department.setName(dept[1]);
                department.setDescription(dept[2]);
                departmentRepository.save(department);
            }

            log.info("已初始化部门数据");
        }
    }

    private void initAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            Role adminRole = roleRepository.findByCode("ADMIN")
                    .orElseThrow(() -> new RuntimeException("管理员角色不存在"));

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@company.com");
            admin.setFullName("系统管理员");
            admin.setRole(adminRole);
            admin.setIsActive(true);
            userRepository.save(admin);

            log.info("已创建管理员账户: admin / admin123");
        }
    }

    private void initSlas() {
        if (slaRepository.count() == 0) {
            Sla sla1 = new Sla();
            sla1.setName("紧急");
            sla1.setDescription("紧急问题，需要立即响应");
            sla1.setResponseHours(1);
            sla1.setResolutionHours(4);
            sla1.setIsActive(true);
            slaRepository.save(sla1);

            Sla sla2 = new Sla();
            sla2.setName("高优先级");
            sla2.setDescription("高优先级问题");
            sla2.setResponseHours(4);
            sla2.setResolutionHours(24);
            sla2.setIsActive(true);
            slaRepository.save(sla2);

            Sla sla3 = new Sla();
            sla3.setName("普通");
            sla3.setDescription("普通问题");
            sla3.setResponseHours(24);
            sla3.setResolutionHours(72);
            sla3.setIsActive(true);
            slaRepository.save(sla3);

            Sla sla4 = new Sla();
            sla4.setName("低优先级");
            sla4.setDescription("低优先级问题");
            sla4.setResponseHours(48);
            sla4.setResolutionHours(168);
            sla4.setIsActive(true);
            slaRepository.save(sla4);

            log.info("已初始化SLA数据");
        }
    }
}
