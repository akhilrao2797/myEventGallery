import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import Register from '../../pages/Register';
import * as api from '../../services/api';

jest.mock('../../services/api');
const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

const renderRegister = () => {
  return render(
    <BrowserRouter>
      <Register />
    </BrowserRouter>
  );
};

describe('Register Page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders registration form with all fields', () => {
    renderRegister();
    
    expect(screen.getByPlaceholderText(/full name|name/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/email/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/password/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/phone/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /register|sign up/i })).toBeInTheDocument();
  });

  test('handles successful registration', async () => {
    api.customerRegister.mockResolvedValue({
      data: {
        success: true,
        message: 'Registration successful',
      },
    });

    renderRegister();
    
    fireEvent.change(screen.getByPlaceholderText(/full name|name/i), {
      target: { value: 'Test User' },
    });
    fireEvent.change(screen.getByPlaceholderText(/email/i), {
      target: { value: 'test@example.com' },
    });
    fireEvent.change(screen.getByPlaceholderText(/password/i), {
      target: { value: 'password123' },
    });
    fireEvent.change(screen.getByPlaceholderText(/phone/i), {
      target: { value: '1234567890' },
    });
    
    fireEvent.click(screen.getByRole('button', { name: /register|sign up/i }));
    
    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith('/login');
    });
  });

  test('handles registration failure', async () => {
    api.customerRegister.mockRejectedValue({
      response: {
        data: {
          message: 'Email already exists',
        },
      },
    });

    renderRegister();
    
    fireEvent.change(screen.getByPlaceholderText(/full name|name/i), {
      target: { value: 'Test User' },
    });
    fireEvent.change(screen.getByPlaceholderText(/email/i), {
      target: { value: 'existing@example.com' },
    });
    fireEvent.change(screen.getByPlaceholderText(/password/i), {
      target: { value: 'password123' },
    });
    fireEvent.change(screen.getByPlaceholderText(/phone/i), {
      target: { value: '1234567890' },
    });
    
    fireEvent.click(screen.getByRole('button', { name: /register|sign up/i }));
    
    await waitFor(() => {
      expect(screen.getByText(/email already exists/i)).toBeInTheDocument();
    });
  });

  test('validates password strength', async () => {
    renderRegister();
    
    fireEvent.change(screen.getByPlaceholderText(/password/i), {
      target: { value: '123' },
    });
    
    // Check if validation message appears (might be immediate or on submit)
    fireEvent.click(screen.getByRole('button', { name: /register|sign up/i }));
    
    await waitFor(() => {
      expect(screen.queryByText(/password.*too short|password.*at least/i)).toBeTruthy();
    }, { timeout: 1000 });
  });

  test('has link to login page', () => {
    renderRegister();
    const loginLink = screen.getByText(/already have an account|login/i);
    expect(loginLink).toBeInTheDocument();
  });
});
