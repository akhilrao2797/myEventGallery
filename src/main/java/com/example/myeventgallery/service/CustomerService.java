package com.example.myeventgallery.service;

import com.example.myeventgallery.dto.AuthResponse;
import com.example.myeventgallery.dto.CustomerLoginRequest;
import com.example.myeventgallery.dto.CustomerRegisterRequest;
import com.example.myeventgallery.model.Customer;
import com.example.myeventgallery.repository.CustomerRepository;
import com.example.myeventgallery.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Transactional
    public AuthResponse register(CustomerRegisterRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setIsActive(true);
        
        customer = customerRepository.save(customer);
        
        String token = jwtUtil.generateToken(customer.getEmail(), customer.getId());
        
        return new AuthResponse(token, customer.getId(), customer.getName(), customer.getEmail());
    }
    
    public AuthResponse login(CustomerLoginRequest request) {
        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        if (!customer.getIsActive()) {
            throw new RuntimeException("Account is inactive");
        }
        
        String token = jwtUtil.generateToken(customer.getEmail(), customer.getId());
        
        return new AuthResponse(token, customer.getId(), customer.getName(), customer.getEmail());
    }
    
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }
    
    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }
}
