import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getGuestDashboard, deleteGuestImage, getImageUrl } from '../services/api';
import { 
  FiLogOut, FiImage, FiCalendar, FiTrash2, FiAlertCircle, FiCheckCircle 
} from 'react-icons/fi';
import './GuestDashboard.css';

function GuestDashboard() {
  const navigate = useNavigate();
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedImage, setSelectedImage] = useState(null);
  const [deleteLoading, setDeleteLoading] = useState(false);
  const guestName = localStorage.getItem('guestName');

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      const response = await getGuestDashboard();
      if (response.data.success) {
        setDashboard(response.data.data);
      }
    } catch (err) {
      setError('Failed to load dashboard');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate('/guest/login');
  };

  const canDeleteImage = (uploadedAt, eventDate) => {
    const now = new Date();
    const uploadDate = new Date(uploadedAt);
    const eventEnd = new Date(eventDate);
    eventEnd.setDate(eventEnd.getDate() + 1); // Event date + 1 day
    
    // Can delete during event and 1 day after
    return now <= eventEnd;
  };

  const handleDeleteImage = async (imageId, uploadedAt, eventDate) => {
    if (!canDeleteImage(uploadedAt, eventDate)) {
      alert('Deletion window has closed. You can only delete images during the event and 1 day after.');
      return;
    }

    if (!window.confirm('Are you sure you want to delete this image? This action cannot be undone.')) {
      return;
    }

    setDeleteLoading(true);
    try {
      await deleteGuestImage(imageId);
      alert('Image deleted successfully');
      loadDashboard(); // Reload dashboard
      setSelectedImage(null);
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to delete image');
    } finally {
      setDeleteLoading(false);
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  const getDeletionStatus = (uploadedAt, eventDate) => {
    const now = new Date();
    const eventEnd = new Date(eventDate);
    eventEnd.setDate(eventEnd.getDate() + 1);
    
    if (now <= eventEnd) {
      return { canDelete: true, message: 'Can delete until ' + formatDate(eventEnd) };
    } else {
      return { canDelete: false, message: 'Deletion window closed' };
    }
  };

  if (loading) {
    return (
      <div className="loading-screen">
        <div className="loader"></div>
        <p>Loading your photos...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container">
        <div className="error-message">{error}</div>
        <button className="btn btn-primary" onClick={() => navigate('/guest/login')}>
          Back to Login
        </button>
      </div>
    );
  }

  const totalImages = dashboard?.events?.reduce((sum, event) => 
    sum + (event.uploadedImages?.length || 0), 0
  ) || 0;

  return (
    <div className="guest-dashboard">
      <header className="dashboard-header">
        <div className="header-background"></div>
        <div className="container">
          <div className="header-content">
            <div className="header-info">
              <h1>Welcome, {guestName}!</h1>
              <p>View and manage your uploaded photos</p>
            </div>
            <button className="btn btn-ghost" onClick={handleLogout}>
              <FiLogOut /> Logout
            </button>
          </div>

          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-icon blue">
                <FiCalendar />
              </div>
              <div className="stat-content">
                <h3>{dashboard?.events?.length || 0}</h3>
                <p>Events Attended</p>
              </div>
            </div>
            <div className="stat-card">
              <div className="stat-icon purple">
                <FiImage />
              </div>
              <div className="stat-content">
                <h3>{totalImages}</h3>
                <p>Photos Uploaded</p>
              </div>
            </div>
          </div>
        </div>
      </header>

      <div className="container">
        <div className="events-section">
          <h2>My Events & Photos</h2>

          {!dashboard?.events || dashboard.events.length === 0 ? (
            <div className="empty-state card">
              <FiImage size={48} />
              <h3>No Photos Yet</h3>
              <p>You haven't uploaded any photos yet. Scan an event QR code to start uploading!</p>
            </div>
          ) : (
            dashboard.events.map((event) => {
              const deletionStatus = getDeletionStatus(
                event.uploadedImages?.[0]?.uploadedAt,
                event.eventDate
              );

              return (
                <div key={event.eventId} className="event-card card">
                  <div className="event-header">
                    <div>
                      <h3>{event.eventName}</h3>
                      <p className="event-date">
                        <FiCalendar /> {formatDate(event.eventDate)}
                      </p>
                    </div>
                    <div className="event-stats">
                      <span className="image-count">
                        <FiImage /> {event.uploadedImages?.length || 0} photos
                      </span>
                      {deletionStatus.canDelete ? (
                        <span className="delete-status active">
                          <FiCheckCircle /> {deletionStatus.message}
                        </span>
                      ) : (
                        <span className="delete-status inactive">
                          <FiAlertCircle /> {deletionStatus.message}
                        </span>
                      )}
                    </div>
                  </div>

                  {event.uploadedImages && event.uploadedImages.length > 0 && (
                    <div className="gallery-grid">
                      {event.uploadedImages.map((image) => {
                        const imageUrl = getImageUrl(image.s3Url);
                        const canDelete = canDeleteImage(image.uploadedAt, event.eventDate);

                        return (
                          <div 
                            key={image.imageId} 
                            className="gallery-item"
                          >
                            <img 
                              src={imageUrl} 
                              alt={image.fileName}
                              onClick={() => setSelectedImage({ ...image, event })}
                              loading="lazy"
                            />
                            <div className="gallery-item-actions">
                              <button
                                className={`btn-delete ${canDelete ? '' : 'disabled'}`}
                                onClick={() => handleDeleteImage(image.imageId, image.uploadedAt, event.eventDate)}
                                disabled={!canDelete || deleteLoading}
                                title={canDelete ? 'Delete image' : 'Deletion window closed'}
                              >
                                <FiTrash2 />
                              </button>
                            </div>
                            <div className="gallery-item-info">
                              <small>{(image.fileSizeMB || 0).toFixed(2)} MB</small>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  )}
                </div>
              );
            })
          )}
        </div>
      </div>

      {/* Lightbox */}
      {selectedImage && (
        <div className="lightbox" onClick={() => setSelectedImage(null)}>
          <div className="lightbox-content" onClick={(e) => e.stopPropagation()}>
            <button className="lightbox-close" onClick={() => setSelectedImage(null)}>×</button>
            <img src={getImageUrl(selectedImage.s3Url)} alt={selectedImage.fileName} />
            <div className="lightbox-info">
              <p><strong>Event:</strong> {selectedImage.event?.eventName}</p>
              <p><strong>File:</strong> {selectedImage.fileName}</p>
              <p><strong>Size:</strong> {(selectedImage.fileSizeMB || 0).toFixed(2)} MB</p>
              <p><strong>Uploaded:</strong> {new Date(selectedImage.uploadedAt).toLocaleString()}</p>
              
              {canDeleteImage(selectedImage.uploadedAt, selectedImage.event?.eventDate) ? (
                <button
                  className="btn btn-danger"
                  onClick={() => handleDeleteImage(
                    selectedImage.imageId, 
                    selectedImage.uploadedAt, 
                    selectedImage.event?.eventDate
                  )}
                  disabled={deleteLoading}
                >
                  <FiTrash2 /> {deleteLoading ? 'Deleting...' : 'Delete Image'}
                </button>
              ) : (
                <div className="deletion-notice">
                  <FiAlertCircle /> Deletion window closed (event + 1 day)
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default GuestDashboard;
