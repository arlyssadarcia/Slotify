package com.slotify.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.slotify.model.Resource;
import com.slotify.model.User;
import com.slotify.repository.ResourceRepository;
import com.slotify.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public DataSeeder(ResourceRepository resourceRepository, UserRepository userRepository) {
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // Resources
        if (resourceRepository.count() == 0) {
            Resource lab = new Resource();
            lab.setName("Network Lab 1");
            lab.setType("ROOM");
            lab.setLocation("FCSIT Block A");
            lab.setStatus("ACTIVE");
            lab.setDescription("Computers and networking equipment");
            resourceRepository.save(lab);

            Resource hall = new Resource();
            hall.setName("Seminar Room 2");
            hall.setType("ROOM");
            hall.setLocation("FCSIT Block B");
            hall.setStatus("ACTIVE");
            hall.setDescription("Projector and 40 seats");
            resourceRepository.save(hall);

            Resource cam = new Resource();
            cam.setName("DSLR Camera Kit");
            cam.setType("EQUIPMENT");
            cam.setLocation("Media Store");
            cam.setStatus("ACTIVE");
            cam.setDescription("Camera, tripod and SD card");
            resourceRepository.save(cam);
        }

        // Admin
        if (!userRepository.existsByEmail("admin@slotify.local")) {
            User admin = new User();
            admin.setFullName("Slotify Admin");
            admin.setEmail("admin@slotify.local");
            admin.setPasswordHash(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("admin123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }
    }
}