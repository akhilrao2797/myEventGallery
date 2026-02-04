import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter, MemoryRouter } from 'react-router-dom';
import EventDetails from '../../pages/EventDetails';
import * as api from '../../services/api';

jest.mock('../../services/api');
const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
  useParams: () => ({ eventId: '1' }),
}));

const renderEventDetails = () => {
  return render(
    <MemoryRouter initialEntries={['/events/1']}>
      <EventDetails />
    </MemoryRouter>
  );
};

describe('EventDetails Page', () => {
  const mockEvent = {
    id: 1,
    name: 'Wedding Event',
    eventDate: '2026-03-15',
    eventType: 'WEDDING',
    venue: 'Grand Hotel',
    totalUploads: 50,
    guestCount: 10,
    eventCode: 'ABC123',
  };

  const mockGroupedImages = {
    guestFolders: [
      {
        guestId: 1,
        guestName: 'John Doe',
        guestEmail: 'john@example.com',
        imageCount: 5,
        images: [
          { id: 1, imageUrl: 'image1.jpg', uploadedAt: '2026-03-15T10:00:00' },
          { id: 2, imageUrl: 'image2.jpg', uploadedAt: '2026-03-15T10:05:00' },
        ],
      },
      {
        guestId: 2,
        guestName: 'Jane Smith',
        guestEmail: 'jane@example.com',
        imageCount: 3,
        images: [
          { id: 3, imageUrl: 'image3.jpg', uploadedAt: '2026-03-15T11:00:00' },
        ],
      },
    ],
  };

  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.setItem('token', 'fake-token');
  });

  test('renders event details and loading state', () => {
    api.getEventDetails.mockReturnValue(new Promise(() => {}));
    api.getEventImagesGrouped.mockReturnValue(new Promise(() => {}));
    
    renderEventDetails();
    
    expect(screen.getByText(/loading/i)).toBeInTheDocument();
  });

  test('displays event information', async () => {
    api.getEventDetails.mockResolvedValue({
      data: { success: true, data: mockEvent },
    });
    api.getEventImagesGrouped.mockResolvedValue({
      data: { success: true, data: mockGroupedImages },
    });

    renderEventDetails();
    
    await waitFor(() => {
      expect(screen.getByText('Wedding Event')).toBeInTheDocument();
      expect(screen.getByText(/grand hotel/i)).toBeInTheDocument();
    });
  });

  test('displays guest folders with images', async () => {
    api.getEventDetails.mockResolvedValue({
      data: { success: true, data: mockEvent },
    });
    api.getEventImagesGrouped.mockResolvedValue({
      data: { success: true, data: mockGroupedImages },
    });

    renderEventDetails();
    
    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
      expect(screen.getByText('Jane Smith')).toBeInTheDocument();
      expect(screen.getByText(/5.*image/i)).toBeInTheDocument();
      expect(screen.getByText(/3.*image/i)).toBeInTheDocument();
    });
  });

  test('allows folder expansion and collapse', async () => {
    api.getEventDetails.mockResolvedValue({
      data: { success: true, data: mockEvent },
    });
    api.getEventImagesGrouped.mockResolvedValue({
      data: { success: true, data: mockGroupedImages },
    });

    renderEventDetails();
    
    await waitFor(() => {
      const folderHeader = screen.getByText('John Doe').closest('.guest-folder-header');
      fireEvent.click(folderHeader);
      // Folder should collapse/expand
    });
  });

  test('handles bulk image selection', async () => {
    api.getEventDetails.mockResolvedValue({
      data: { success: true, data: mockEvent },
    });
    api.getEventImagesGrouped.mockResolvedValue({
      data: { success: true, data: mockGroupedImages },
    });

    renderEventDetails();
    
    await waitFor(() => {
      const selectButton = screen.getByText(/select|selection mode/i);
      fireEvent.click(selectButton);
      // Selection mode should be activated
    });
  });

  test('handles QR code download', async () => {
    api.getEventDetails.mockResolvedValue({
      data: { success: true, data: mockEvent },
    });
    api.getEventImagesGrouped.mockResolvedValue({
      data: { success: true, data: mockGroupedImages },
    });
    api.getQRCode.mockResolvedValue({
      data: new Blob(['qr-code-data'], { type: 'image/png' }),
    });

    renderEventDetails();
    
    await waitFor(() => {
      const downloadButton = screen.getByText(/download.*qr/i);
      fireEvent.click(downloadButton);
    });

    await waitFor(() => {
      expect(api.getQRCode).toHaveBeenCalledWith('ABC123');
    });
  });

  test('displays error message on load failure', async () => {
    api.getEventDetails.mockRejectedValue({
      response: { data: { message: 'Failed to load event' } },
    });
    api.getEventImagesGrouped.mockRejectedValue({
      response: { data: { message: 'Failed to load images' } },
    });

    renderEventDetails();
    
    await waitFor(() => {
      expect(screen.getByText(/failed|error/i)).toBeInTheDocument();
    });
  });

  test('has back button navigation', async () => {
    api.getEventDetails.mockResolvedValue({
      data: { success: true, data: mockEvent },
    });
    api.getEventImagesGrouped.mockResolvedValue({
      data: { success: true, data: mockGroupedImages },
    });

    renderEventDetails();
    
    await waitFor(() => {
      const backButton = screen.getByText(/back/i);
      fireEvent.click(backButton);
      expect(mockNavigate).toHaveBeenCalledWith('/dashboard');
    });
  });
});
