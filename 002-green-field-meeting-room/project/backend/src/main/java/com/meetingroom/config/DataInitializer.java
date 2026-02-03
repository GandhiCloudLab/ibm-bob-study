package com.meetingroom.config;

import com.meetingroom.model.Room;
import com.meetingroom.model.User;
import com.meetingroom.repository.RoomRepository;
import com.meetingroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@meetingroom.com");
            admin.setFullName("Admin User");
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Admin user created: username=admin, password=admin123");
        }

        // Create regular user if not exists
        if (!userRepository.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@meetingroom.com");
            user.setFullName("Regular User");
            user.setRole(User.Role.USER);
            userRepository.save(user);
            System.out.println("Regular user created: username=user, password=user123");
        }

        // Create sample rooms if not exist
        if (roomRepository.count() == 0) {
            Room room1 = new Room();
            room1.setName("Conference Room A");
            room1.setLocation("Floor 1, Building A");
            room1.setCapacity(10);
            room1.setDescription("Main conference room with projector and whiteboard");
            room1.setIsActive(true);
            roomRepository.save(room1);

            Room room2 = new Room();
            room2.setName("Meeting Room B");
            room2.setLocation("Floor 2, Building A");
            room2.setCapacity(6);
            room2.setDescription("Small meeting room for team discussions");
            room2.setIsActive(true);
            roomRepository.save(room2);

            Room room3 = new Room();
            room3.setName("Board Room");
            room3.setLocation("Floor 3, Building A");
            room3.setCapacity(15);
            room3.setDescription("Executive board room with video conferencing");
            room3.setIsActive(true);
            roomRepository.save(room3);

            System.out.println("Sample rooms created");
        }
    }
}

// Made with Bob
