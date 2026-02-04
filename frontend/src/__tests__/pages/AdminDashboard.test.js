import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import AdminDashboard from '../../pages/AdminDashboard';
import * as api from '../../services/api';

jest.mock('../../services/api');
const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

const renderAdminDashboard = () => {
  return render(
    <BrowserRouter>
      <AdminDashboard />
    </BrowserRouter>
  );
};

describe('AdminDashboard Page', () => {
  const mockDashboardStats = {
    totalCustomers: 100,
    totalEvents: 50,
    activeEvents: 30,
    totalImages: 5000,
    totalStorageUsedGB: 25.5,
    totalRevenue: 10000.0,
    recentEvents: [
      {
        id: 1,
        name: 'Wedding Event',
        customerName: 'John Customer',
        eventDate: '2026-03-15',
        totalUploads: 100,
      },
    ],
  };

  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.setItem('adminToken', 'fake-admin-token');
  });

  test('renders admin dashboard with loading state', () => {
    api.getAdminDashboard.mockReturnValue(new Promise(() => {}));
    api.getAllEvents.mockReturnValue(new Promise(() => {}));
    
    renderAdminDashboard();
    
    expect(screen.getByText(/loading|admin.*dashboard/i)).toBeInTheDocument();
  });

  test('displays dashboard statistics', async () => {
    api.getAdminDashboard.mockResolvedValue({
      data: { success: true, data: mockDashboardStats },
    });
    api.getAllEvents.mockResolvedValue({
      data: { success: true, data: [] },
    });

    renderAdminDashboard();
    
    await waitFor(() => {
      expect(screen.getByText(/100/)).toBeInTheDocument(); // Total customers
      expect(screen.getByText(/50/)).toBeInTheDocument(); // Total events
      expect(screen.getByText(/5000|5,000/)).toBeInTheDocument(); // Total images
    });
  });

  test('displays recent events', async () => {
    api.getAdminDashboard.mockResolvedValue({
      data: { success: true, data: mockDashboardStats },
    });
    api.getAllEvents.mockResolvedValue({
      data: { success: true, data: mockDashboardStats.recentEvents },
    });

    renderAdminDashboard();
    
    await waitFor(() => {
      expect(screen.getByText('Wedding Event')).toBeInTheDocument();
      expect(screen.getByText(/john customer/i)).toBeInTheDocument();
    });
  });

  test('allows tab navigation between sections', async () => {
    api.getAdminDashboard.mockResolvedValue({
      data: { success: true, data: mockDashboardStats },
    });
    api.getAllEvents.mockResolvedValue({
      data: { success: true, data: [] },
    });
    api.getAllCustomers.mockResolvedValue({
      data: { success: true, data: [] },
    });

    renderAdminDashboard();
    
    await waitFor(() => {
      const customersTab = screen.getByText(/customers/i);
      fireEvent.click(customersTab);
    });

    await waitFor(() => {
      expect(api.getAllCustomers).toHaveBeenCalled();
    });
  });

  test('allows searching events', async () => {
    api.getAdminDashboard.mockResolvedValue({
      data: { success: true, data: mockDashboardStats },
    });
    api.getAllEvents.mockResolvedValue({
      data: { success: true, data: [] },
    });
    api.searchEvents.mockResolvedValue({
      data: { success: true, data: [] },
    });

    renderAdminDashboard();
    
    await waitFor(() => {
      const searchInput = screen.getByPlaceholderText(/search/i);
      fireEvent.change(searchInput, { target: { value: 'wedding' } });
    });

    await waitFor(() => {
      expect(api.searchEvents).toHaveBeenCalledWith('wedding');
    });
  });

  test('handles event deletion', async () => {
    api.getAdminDashboard.mockResolvedValue({
      data: { success: true, data: mockDashboardStats },
    });
    api.getAllEvents.mockResolvedValue({
      data: { success: true, data: mockDashboardStats.recentEvents },
    });
    api.deleteEvent.mockResolvedValue({
      data: { success: true, message: 'Event deleted' },
    });

    renderAdminDashboard();
    
    await waitFor(() => {
      const deleteButton = screen.getByText(/delete/i);
      fireEvent.click(deleteButton);
    });

    // Confirm deletion
    await waitFor(() => {
      expect(api.deleteEvent).toHaveBeenCalledWith(1);
    });
  });

  test('displays error message on API failure', async () => {
    api.getAdminDashboard.mockRejectedValue({
      response: {
        data: { message: 'Unauthorized' },
      },
    });
    api.getAllEvents.mockResolvedValue({
      data: { success: true, data: [] },
    });

    renderAdminDashboard();
    
    await waitFor(() => {
      expect(screen.getByText(/error|failed/i)).toBeInTheDocument();
    });
  });
});
