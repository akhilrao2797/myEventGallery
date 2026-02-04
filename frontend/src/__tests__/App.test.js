import React from 'react';
import { render, screen } from '@testing-library/react';
import App from '../App';

// Mock all page components to avoid deep rendering
jest.mock('../pages/Login', () => () => <div>Login Page</div>);
jest.mock('../pages/Register', () => () => <div>Register Page</div>);
jest.mock('../pages/Dashboard', () => () => <div>Dashboard Page</div>);
jest.mock('../pages/CreateEvent', () => () => <div>CreateEvent Page</div>);
jest.mock('../pages/EventDetails', () => () => <div>EventDetails Page</div>);
jest.mock('../pages/GuestLogin', () => () => <div>GuestLogin Page</div>);
jest.mock('../pages/GuestRegistration', () => () => <div>GuestRegistration Page</div>);
jest.mock('../pages/GuestUpload', () => () => <div>GuestUpload Page</div>);
jest.mock('../pages/GuestDashboard', () => () => <div>GuestDashboard Page</div>);
jest.mock('../pages/AdminLogin', () => () => <div>AdminLogin Page</div>);
jest.mock('../pages/AdminDashboard', () => () => <div>AdminDashboard Page</div>);

describe('App Component', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  test('redirects to login page by default', () => {
    render(<App />);
    expect(screen.getByText('Login Page')).toBeInTheDocument();
  });

  test('renders login page for unauthenticated users', () => {
    window.history.pushState({}, 'Login', '/login');
    render(<App />);
    expect(screen.getByText('Login Page')).toBeInTheDocument();
  });

  test('protects customer dashboard route', () => {
    window.history.pushState({}, 'Dashboard', '/dashboard');
    render(<App />);
    // Should redirect to login when not authenticated
    expect(screen.getByText('Login Page')).toBeInTheDocument();
  });

  test('allows access to dashboard when authenticated', () => {
    localStorage.setItem('token', 'fake-token');
    window.history.pushState({}, 'Dashboard', '/dashboard');
    render(<App />);
    expect(screen.getByText('Dashboard Page')).toBeInTheDocument();
  });

  test('protects guest dashboard route', () => {
    window.history.pushState({}, 'Guest Dashboard', '/guest/dashboard');
    render(<App />);
    expect(screen.getByText('GuestLogin Page')).toBeInTheDocument();
  });

  test('allows access to guest dashboard when guest authenticated', () => {
    localStorage.setItem('guestToken', 'fake-guest-token');
    window.history.pushState({}, 'Guest Dashboard', '/guest/dashboard');
    render(<App />);
    expect(screen.getByText('GuestDashboard Page')).toBeInTheDocument();
  });

  test('protects admin dashboard route', () => {
    window.history.pushState({}, 'Admin Dashboard', '/admin/dashboard');
    render(<App />);
    expect(screen.getByText('AdminLogin Page')).toBeInTheDocument();
  });

  test('allows access to admin dashboard when admin authenticated', () => {
    localStorage.setItem('adminToken', 'fake-admin-token');
    window.history.pushState({}, 'Admin Dashboard', '/admin/dashboard');
    render(<App />);
    expect(screen.getByText('AdminDashboard Page')).toBeInTheDocument();
  });
});
