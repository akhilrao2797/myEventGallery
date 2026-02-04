import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import CreateEvent from '../../pages/CreateEvent';
import * as api from '../../services/api';

jest.mock('../../services/api');
const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

const renderCreateEvent = () => {
  return render(
    <BrowserRouter>
      <CreateEvent />
    </BrowserRouter>
  );
};

describe('CreateEvent Page', () => {
  const mockPackages = [
    {
      id: 1,
      packageType: 'BASIC',
      name: 'Basic Package',
      basePrice: 49.99,
      storageGB: 5,
      maxGuests: 50,
      maxImages: 500,
      storageDays: 30,
      description: 'Perfect for small events',
    },
    {
      id: 2,
      packageType: 'PREMIUM',
      name: 'Premium Package',
      basePrice: 99.99,
      storageGB: 20,
      maxGuests: 200,
      maxImages: 2000,
      storageDays: 90,
      description: 'Best for large events',
    },
  ];

  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.setItem('token', 'fake-token');
  });

  test('renders create event form', async () => {
    api.getPackages.mockResolvedValue({
      data: {
        success: true,
        data: mockPackages,
      },
    });

    renderCreateEvent();
    
    await waitFor(() => {
      expect(screen.getByPlaceholderText(/event name/i)).toBeInTheDocument();
      expect(screen.getByPlaceholderText(/venue/i)).toBeInTheDocument();
    });
  });

  test('loads and displays packages', async () => {
    api.getPackages.mockResolvedValue({
      data: {
        success: true,
        data: mockPackages,
      },
    });

    renderCreateEvent();
    
    await waitFor(() => {
      expect(screen.getByText('Basic Package')).toBeInTheDocument();
      expect(screen.getByText('Premium Package')).toBeInTheDocument();
      expect(screen.getByText(/\$49\.99/)).toBeInTheDocument();
      expect(screen.getByText(/\$99\.99/)).toBeInTheDocument();
    });
  });

  test('handles package selection', async () => {
    api.getPackages.mockResolvedValue({
      data: {
        success: true,
        data: mockPackages,
      },
    });

    renderCreateEvent();
    
    await waitFor(() => {
      const basicPackage = screen.getByText('Basic Package').closest('.package-card');
      fireEvent.click(basicPackage);
      expect(basicPackage).toHaveClass('selected');
    });
  });

  test('handles successful event creation', async () => {
    api.getPackages.mockResolvedValue({
      data: {
        success: true,
        data: mockPackages,
      },
    });

    api.createEvent.mockResolvedValue({
      data: {
        success: true,
        data: {
          id: 1,
          eventCode: 'ABC123',
        },
      },
    });

    renderCreateEvent();
    
    await waitFor(() => {
      fireEvent.change(screen.getByPlaceholderText(/event name/i), {
        target: { value: 'My Wedding' },
      });
      fireEvent.change(screen.getByPlaceholderText(/venue/i), {
        target: { value: 'Grand Hotel' },
      });
      
      const basicPackage = screen.getByText('Basic Package').closest('.package-card');
      fireEvent.click(basicPackage);
    });
    
    const submitButton = screen.getByRole('button', { name: /create event|submit/i });
    fireEvent.click(submitButton);
    
    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith('/dashboard');
    });
  });

  test('displays error on event creation failure', async () => {
    api.getPackages.mockResolvedValue({
      data: {
        success: true,
        data: mockPackages,
      },
    });

    api.createEvent.mockRejectedValue({
      response: {
        data: {
          message: 'Event creation failed',
        },
      },
    });

    renderCreateEvent();
    
    await waitFor(() => {
      const submitButton = screen.getByRole('button', { name: /create event|submit/i });
      fireEvent.click(submitButton);
    });
    
    await waitFor(() => {
      expect(screen.getByText(/event creation failed|error/i)).toBeInTheDocument();
    });
  });

  test('validates required fields', async () => {
    api.getPackages.mockResolvedValue({
      data: {
        success: true,
        data: mockPackages,
      },
    });

    renderCreateEvent();
    
    await waitFor(() => {
      const submitButton = screen.getByRole('button', { name: /create event|submit/i });
      fireEvent.click(submitButton);
    });
    
    // Should show validation error or prevent submission
    expect(mockNavigate).not.toHaveBeenCalled();
  });
});
