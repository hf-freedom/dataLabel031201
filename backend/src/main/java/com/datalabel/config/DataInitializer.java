package com.datalabel.config;

import com.datalabel.entity.Resource;
import com.datalabel.entity.Role;
import com.datalabel.entity.User;
import com.datalabel.repository.ResourceRepository;
import com.datalabel.repository.RoleRepository;
import com.datalabel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private ResourceRepository resourceRepository;
    
    @Override
    public void run(String... args) throws Exception {
        initResources();
        initRoles();
        initUsers();
    }
    
    private void initResources() {
        Resource menu1 = new Resource();
        menu1.setName("用户管理");
        menu1.setCode("user:menu");
        menu1.setType(Resource.ResourceType.MENU);
        menu1.setPath("/user");
        menu1.setIcon("user");
        menu1.setSort(1);
        menu1.setParentId(null);
        resourceRepository.save(menu1);
        
        Resource menu2 = new Resource();
        menu2.setName("角色管理");
        menu2.setCode("role:menu");
        menu2.setType(Resource.ResourceType.MENU);
        menu2.setPath("/role");
        menu2.setIcon("role");
        menu2.setSort(2);
        menu2.setParentId(null);
        resourceRepository.save(menu2);
        
        Resource menu3 = new Resource();
        menu3.setName("资源管理");
        menu3.setCode("resource:menu");
        menu3.setType(Resource.ResourceType.MENU);
        menu3.setPath("/resource");
        menu3.setIcon("resource");
        menu3.setSort(3);
        menu3.setParentId(null);
        resourceRepository.save(menu3);
        
        Resource btn1 = new Resource();
        btn1.setName("新增用户");
        btn1.setCode("user:add");
        btn1.setType(Resource.ResourceType.BUTTON);
        btn1.setParentId(menu1.getId());
        btn1.setSort(1);
        resourceRepository.save(btn1);
        
        Resource btn2 = new Resource();
        btn2.setName("编辑用户");
        btn2.setCode("user:edit");
        btn2.setType(Resource.ResourceType.BUTTON);
        btn2.setParentId(menu1.getId());
        btn2.setSort(2);
        resourceRepository.save(btn2);
        
        Resource btn3 = new Resource();
        btn3.setName("删除用户");
        btn3.setCode("user:delete");
        btn3.setType(Resource.ResourceType.BUTTON);
        btn3.setParentId(menu1.getId());
        btn3.setSort(3);
        resourceRepository.save(btn3);
        
        Resource api1 = new Resource();
        api1.setName("用户API");
        api1.setCode("user:api");
        api1.setType(Resource.ResourceType.API);
        api1.setApiPath("/api/user");
        api1.setApiMethod("POST");
        resourceRepository.save(api1);
        
        Resource api2 = new Resource();
        api2.setName("角色API");
        api2.setCode("role:api");
        api2.setType(Resource.ResourceType.API);
        api2.setApiPath("/api/role");
        api2.setApiMethod("POST");
        resourceRepository.save(api2);
    }
    
    private void initRoles() {
        Role adminRole = new Role();
        adminRole.setName("管理员");
        adminRole.setCode("admin");
        adminRole.setDescription("系统管理员，拥有所有权限");
        adminRole.setResourceIds(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L));
        roleRepository.save(adminRole);
        
        Role userRole = new Role();
        userRole.setName("普通用户");
        userRole.setCode("user");
        userRole.setDescription("普通用户，只有查看权限");
        userRole.setResourceIds(Arrays.asList(1L, 2L, 3L));
        roleRepository.save(userRole);
    }
    
    private void initUsers() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin123");
        admin.setNickname("管理员");
        admin.setEmail("admin@example.com");
        admin.setPhone("13800138000");
        admin.setRoleIds(Arrays.asList(1L));
        userRepository.save(admin);
        
        User user = new User();
        user.setUsername("user");
        user.setPassword("user123");
        user.setNickname("普通用户");
        user.setEmail("user@example.com");
        user.setPhone("13800138001");
        user.setRoleIds(Arrays.asList(2L));
        userRepository.save(user);
    }
}
