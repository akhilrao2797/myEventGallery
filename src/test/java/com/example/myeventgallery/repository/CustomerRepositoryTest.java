package com.example.myeventgallery.repository;

import com.example.myeventgallery.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testFindByEmail() {
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setPassword("password");
        customer.setIsActive(true);

        entityManager.persist(customer);
        entityManager.flush();

        Optional<Customer> found = customerRepository.findByEmail("john@example.com");

        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getName());
    }

    @Test
    void testExistsByEmail() {
        Customer customer = new Customer();
        customer.setName("Jane Doe");
        customer.setEmail("jane@example.com");
        customer.setPassword("password");
        customer.setIsActive(true);

        entityManager.persist(customer);
        entityManager.flush();

        assertTrue(customerRepository.existsByEmail("jane@example.com"));
        assertFalse(customerRepository.existsByEmail("notfound@example.com"));
    }
}
