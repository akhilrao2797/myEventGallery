import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import Dashboard from '../../pages/Dashboard';
import * as api from '../../services/api';

jest.mock('../../services/api');
const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
  Link: ({ children, to }) => <a href={to}>{children}</a>,
}));

const renderDashboard = () => {
  return render(
    <BrowserRouter>
      <Dashboard />
    </BrowserRouter>
  );
};

describe('Dashboard Page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.setItem('token', 'fake-token');
    localStorage.setItem('customerName', 'Test User');
  });

  test('renders dashboard with loading state', () => {
    api.getMyEvents.mockReturnValue(new Promise(() => {})); // Never resolves
    renderDashboard();
    
    expect(screen.getByText(/loading|dashboard/i)).toBeInTheDocument();
  });

  test('displays events when loaded successfully', async () => {
    const mockEvents = [
      {
        id: 1,
        name: 'Wedding Event',
        eventDate: '2026-03-15',
        eventType: 'WEDDING',
        totalUploads: 50,
        guestCount: 10,
      },
      {
        id: 2,
        name: 'Birthday Party',
        eventDate: '2026-04-20',
        eventType: 'BIRTHDAY',
        totalUploads: 30,
        guestCount: 5,
      },
    ];

    api.getMyEvents.mockResolvedValue({
      data: {
        success: true,
        data: mockEvents,
      },
    });

    renderDashboard();
    
    await waitFor(() => {
      expect(screen.getByText('Wedding Event')).toBeInTheDocument();
      expect(screen.getByText('Birthday Party')).toBeInTheDocument();
    });
  });

  test('displays empty state when no events', async () => {
    api.getMyEvents.mockResolvedValue({
      data: {
        success: true,
        data: [],
      },
    });

    renderDashboard();
    
    await waitFor(() => {
      expect(screen.getByText(/no events|create your first event/i)).toBeInTheDocument();
    });
  });

  test('displays error message on API failure', async () => {
    api.getMyEvents.mockRejectedValue({
      response: {
        data: {
          message: 'Failed to fetch events',
        },
      },
    });

    renderDashboard();
    
    await waitFor(() => {
      expect(screen.getByText(/failed|error/i)).toBeInTheDocument();
    });
  });

  test('has create new event button', async () => {
    api.getMyEvents.mockResolvedValue({
      data: {
        success: true,
        data: [],
      },
    });

    renderDashboard();
    
    await waitFor(() => {
      expect(screen.getByText(/create event|new event/i)).toBeInTheDocument();
    });
  });

  test('displays welcome message with customer name', async () => {
    api.getMyEvents.mockResolvedValue({
      data: {
        success: true,
        data: [],
      },
    });

    renderDashboard();
    
    await waitFor(() => {
      expect(screen.getByText(/welcome.*test user|test user/i)).toBeInTheDocument();
    });
  });
});
