package com.springvuegradle.team6.startup;

import com.springvuegradle.team6.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

/**
 * SetupDataLoader class to check for default admin account. If there isn't one, a default admin account will be created
 */

@Component
public class SetupDataLoader implements
        ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailRepository emailRepository;

    @Value("#{environment.ADMIN_EMAIL}")
    private String adminEmail;

    @Value("#{environment.ADMIN_PASSWORD}")
    private String adminPassword;

    /**
     * Create a default admin account if it is not found in the database
     *
     * @param event ContextRefreshedEvent
     */
    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (isAlreadySetup())
            return;
        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_USER");
        createRoleIfNotFound("ROLE_USER_ADMIN");

        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        Profile user = new Profile();
        profileRepository.save(user);
        user.setPassword(adminPassword);
        Email email = new Email((adminEmail));
        emailRepository.save(email);
        user.setPrimaryEmail(email);
        Collection<Role> roles = new ArrayList<>(Arrays.asList(adminRole));
        user.setRoles(roles);
    }

    /**
     * Check if default admin account is already setup
     *
     * @return True if found or else False
     */
    private boolean isAlreadySetup() {
        Optional<Email> email = emailRepository.findByAddress(adminEmail);
        return email.isPresent();
    }


    /**
     * Create an object of Role class and save to role repository if not found
     *
     * @param name role name
     */
    @Transactional
    public void createRoleIfNotFound(String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            roleRepository.save(role);
        }
    }
}
