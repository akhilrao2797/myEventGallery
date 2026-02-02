package com.example.myeventgallery.service;

import com.example.myeventgallery.dto.AuthResponse;
import com.example.myeventgallery.dto.CustomerLoginRequest;
import com.example.myeventgallery.dto.CustomerRegisterRequest;
import com.example.myeventgallery.model.Customer;
import com.example.myeventgallery.repository.CustomerRepository;
import com.example.myeventgallery.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private CustomerService customerService;

    private CustomerRegisterRequest registerRequest;
    private CustomerLoginRequest loginRequest;
    private Customer customer;

    @BeforeEach
    void setUp() {
        registerRequest = new CustomerRegisterRequest();
        registerRequest.setName("John Doe");
        registerRequest.setEmail("john@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setPhoneNumber("+1234567890");

        loginRequest = new CustomerLoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password123");

        customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setPassword("encodedPassword");
        customer.setIsActive(true);
    }

    @Test
    void testRegisterSuccess() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(jwtUtil.generateToken(anyString(), any(Long.class))).thenReturn("token123");

        AuthResponse response = customerService.register(registerRequest);

        assertNotNull(response);
        assertEquals("token123", response.getToken());
        assertEquals(1L, response.getCustomerId());
        assertEquals("John Doe", response.getName());
        assertEquals("john@example.com", response.getEmail());

        verify(customerRepository).existsByEmail("john@example.com");
        verify(customerRepository).save(any(Customer.class));
        verify(jwtUtil).generateToken("john@example.com", 1L);
    }

    @Test
    void testRegisterEmailAlreadyExists() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerService.register(registerRequest);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testLoginSuccess() {
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), any(Long.class))).thenReturn("token123");

        AuthResponse response = customerService.login(loginRequest);

        assertNotNull(response);
        assertEquals("token123", response.getToken());
        assertEquals(1L, response.getCustomerId());
        verify(customerRepository).findByEmail("john@example.com");
    }

    @Test
    void testLoginInvalidEmail() {
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerService.login(loginRequest);
        });

        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    void testLoginInvalidPassword() {
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerService.login(loginRequest);
        });

        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    void testLoginInactiveAccount() {
        customer.setIsActive(false);
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerService.login(loginRequest);
        });

        assertEquals("Account is inactive", exception.getMessage());
    }

    @Test
    void testGetCustomerById() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
    }

    @Test
    void testGetCustomerByIdNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerService.getCustomerById(1L);
        });

        assertEquals("Customer not found", exception.getMessage());
    }
}
