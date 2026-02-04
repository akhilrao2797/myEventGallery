import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import GuestLogin from '../../pages/GuestLogin';
import * as api from '../../services/api';

jest.mock('../../services/api');
const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

const renderGuestLogin = () => {
  return render(
    <BrowserRouter>
      <GuestLogin />
    </BrowserRouter>
  );
};

describe('GuestLogin Page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  test('renders guest login form', () => {
    renderGuestLogin();
    
    expect(screen.getByPlaceholderText(/email/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/event code/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /login|sign in/i })).toBeInTheDocument();
  });

  test('handles successful guest login', async () => {
    api.guestLogin.mockResolvedValue({
      data: {
        success: true,
        data: {
          token: 'guest-token',
          guestId: 1,
          guestName: 'John Guest',
          guestEmail: 'guest@example.com',
        },
      },
    });

    renderGuestLogin();
    
    fireEvent.change(screen.getByPlaceholderText(/email/i), {
      target: { value: 'guest@example.com' },
    });
    fireEvent.change(screen.getByPlaceholderText(/event code/i), {
      target: { value: 'ABC123' },
    });
    
    fireEvent.click(screen.getByRole('button', { name: /login|sign in/i }));
    
    await waitFor(() => {
      expect(localStorage.setItem).toHaveBeenCalledWith('guestToken', 'guest-token');
      expect(localStorage.setItem).toHaveBeenCalledWith('guestId', '1');
      expect(mockNavigate).toHaveBeenCalledWith('/guest/dashboard');
    });
  });

  test('handles login failure with invalid event code', async () => {
    api.guestLogin.mockRejectedValue({
      response: {
        data: {
          message: 'Invalid event code',
        },
      },
    });

    renderGuestLogin();
    
    fireEvent.change(screen.getByPlaceholderText(/email/i), {
      target: { value: 'guest@example.com' },
    });
    fireEvent.change(screen.getByPlaceholderText(/event code/i), {
      target: { value: 'INVALID' },
    });
    
    fireEvent.click(screen.getByRole('button', { name: /login|sign in/i }));
    
    await waitFor(() => {
      expect(screen.getByText(/invalid.*event code/i)).toBeInTheDocument();
    });
  });

  test('has link to guest registration', () => {
    renderGuestLogin();
    const registerLink = screen.getByText(/don't have an account|register/i);
    expect(registerLink).toBeInTheDocument();
  });
});
