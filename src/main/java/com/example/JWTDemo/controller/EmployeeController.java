package com.example.JWTDemo.controller;

import com.example.JWTDemo.dto.AuthenticationRequest;
import com.example.JWTDemo.dto.AuthenticationResponse;
import com.example.JWTDemo.entity.Employee;
import com.example.JWTDemo.entity.Members;
import com.example.JWTDemo.repo.MembersRepository;
import com.example.JWTDemo.security.JwtService;
import com.example.JWTDemo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final MembersRepository membersRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public EmployeeController(EmployeeService employeeService,
                              AuthenticationManager authenticationManager,
                              JwtService jwtService,
                              MembersRepository membersRepository,
                              PasswordEncoder passwordEncoder) {
        this.employeeService = employeeService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.membersRepository = membersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Authentication endpoint
    @PostMapping("/authenticate")
    public AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request) throws AuthenticationException {
        // Perform authentication
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserId(), request.getPassword())
        );

        // Load the authenticated member details
        Optional<Members> memberOptional = membersRepository.findByUserId(request.getUserId());
        Members member = memberOptional.orElseThrow(() -> new UsernameNotFoundException("User not found: " + request.getUserId()));


        // Generate JWT token using the username
        String jwt = jwtService.generateToken(member.getUserId());

        return new AuthenticationResponse(jwt);
    }

    // Registration endpoint
    @PostMapping("/register")
    public String register(@RequestBody Members member) {
        // Encrypt the password before saving to the database
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        membersRepository.save(member);
        return "User registered successfully";
    }

    // Existing endpoints for Employee CRUD operations
    @GetMapping("/employees")
    public List<Employee> findAll() {
        return employeeService.findAll();
    }

    @GetMapping("/employees/{employeeId}")
    public Employee getEmployee(@PathVariable int employeeId) {
        Employee theEmployee = employeeService.findById(employeeId);
        if (theEmployee == null) {
            throw new RuntimeException("Employee id not found - " + employeeId);
        }
        return theEmployee;
    }

    @PostMapping("/employees")
    public Employee addEmployee(@RequestBody Employee theEmployee) {
        theEmployee.setId(0); // Ensure we set the ID to 0 for new employees
        return employeeService.save(theEmployee);
    }

    @PutMapping("/employees")
    public Employee updateEmployee(@RequestBody Employee theEmployee) {
        return employeeService.save(theEmployee); // Assume save handles both create and update
    }

    @DeleteMapping("/employees/{employeeId}")
    public String deleteEmployee(@PathVariable int employeeId) {
        Employee tempEmployee = employeeService.findById(employeeId);
        if (tempEmployee == null) {
            throw new RuntimeException("Employee id not found - " + employeeId);
        }
        employeeService.deleteById(employeeId);
        return "Deleted employee id - " + employeeId;
    }
}
