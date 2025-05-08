package com.c1se22.publiclaundsmartsystem.configuration;

import com.c1se22.publiclaundsmartsystem.entity.Role;
import com.c1se22.publiclaundsmartsystem.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.List;

@Configuration
@Slf4j
public class ApplicationConfig {
    @Bean
    ApplicationRunner applicationRunner(RoleRepository roleRepository){
        List<String> roles = List.of("ROLE_ADMIN", "ROLE_OWNER", "ROLE_USER");
        return args -> {
            roles.forEach(role -> {
                if( roleRepository.findByName(role).isEmpty()){
                    Role newRole = Role.builder()
                            .name(role)
                            .build();
                    roleRepository.save(newRole);
                    log.info("Role {} has been created as default", role);
                }
            });
        };
    }
}
