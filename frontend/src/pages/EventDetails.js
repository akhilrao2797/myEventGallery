import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getEventDetails, getEventImages, getQRCode } from '../services/api';
import { FiArrowLeft, FiDownload, FiUsers, FiImage, FiCalendar } from 'react-icons/fi';
import './EventDetails.css';

function EventDetails() {
  const { eventId } = useParams();
  const navigate = useNavigate();
  const [event, setEvent] = useState(null);
  const [images, setImages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedImage, setSelectedImage] = useState(null);

  useEffect(() => {
    loadEventDetails();
    loadImages();
  }, [eventId]);

  const loadEventDetails = async () => {
    try {
      const response = await getEventDetails(eventId);
      if (response.data.success) {
        setEvent(response.data.data);
      }
    } catch (err) {
      setError('Failed to load event details');
    } finally {
      setLoading(false);
    }
  };

  const loadImages = async () => {
    try {
      const response = await getEventImages(eventId);
      if (response.data.success) {
        setImages(response.data.data);
      }
    } catch (err) {
      console.error('Failed to load images', err);
    }
  };

  const downloadQRCode = async () => {
    try {
      const response = await getQRCode(event.eventCode);
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.download = `${event.name}-QRCode.png`;
      link.click();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      console.error('Failed to download QR code', err);
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  if (loading) {
    return <div className="loading">Loading event details...</div>;
  }

  if (error || !event) {
    return (
      <div className="container">
        <div className="error-message">{error || 'Event not found'}</div>
      </div>
    );
  }

  return (
    <div className="event-details-page">
      <div className="container">
        <button className="btn-back" onClick={() => navigate('/dashboard')}>
          <FiArrowLeft /> Back to Dashboard
        </button>

        <div className="event-header-card card">
          <div className="event-info">
            <h1>{event.name}</h1>
            <p className="event-type">{event.eventType.replace('_', ' ')}</p>
            
            <div className="event-meta">
              <div className="meta-item">
                <FiCalendar />
                <span>{formatDate(event.eventDate)}</span>
              </div>
              {event.venue && (
                <div className="meta-item">
                  <span>{event.venue}</span>
                </div>
              )}
            </div>

            <div className="event-stats-row">
              <div className="stat-box">
                <FiUsers />
                <div>
                  <h3>{event.guestCount}</h3>
                  <p>Guests</p>
                </div>
              </div>
              <div className="stat-box">
                <FiImage />
                <div>
                  <h3>{event.totalUploads}</h3>
                  <p>Photos</p>
                </div>
              </div>
              <div className="stat-box">
                <div>
                  <h3>{event.totalSizeMB?.toFixed(2)} MB</h3>
                  <p>Storage Used</p>
                </div>
              </div>
            </div>
          </div>

          <div className="qr-section">
            <h3>Event QR Code</h3>
            <div className="qr-code-container">
              <img src={`http://localhost:8080/api/events/qr/${event.eventCode}`} alt="Event QR Code" />
            </div>
            <button className="btn btn-primary" onClick={downloadQRCode}>
              <FiDownload /> Download QR Code
            </button>
            <p className="qr-info">Share this QR code with guests to upload photos</p>
          </div>
        </div>

        <div className="card">
          <h2>Event Gallery ({images.length} photos)</h2>
          
          {images.length === 0 ? (
            <div className="empty-gallery">
              <p>No photos uploaded yet. Share the QR code with your guests to start collecting photos!</p>
            </div>
          ) : (
            <div className="gallery-grid">
              {images.map((image) => (
                <div 
                  key={image.id} 
                  className="gallery-item"
                  onClick={() => setSelectedImage(image)}
                >
                  <img src={image.s3Url} alt={image.originalFileName} />
                  <div className="gallery-item-overlay">
                    <p>{image.guestName}</p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {selectedImage && (
        <div className="lightbox" onClick={() => setSelectedImage(null)}>
          <div className="lightbox-content" onClick={(e) => e.stopPropagation()}>
            <button className="lightbox-close" onClick={() => setSelectedImage(null)}>×</button>
            <img src={selectedImage.s3Url} alt={selectedImage.originalFileName} />
            <div className="lightbox-info">
              <p><strong>Uploaded by:</strong> {selectedImage.guestName}</p>
              <p><strong>Size:</strong> {selectedImage.fileSizeMB?.toFixed(2)} MB</p>
              <p><strong>Date:</strong> {new Date(selectedImage.uploadedAt).toLocaleString()}</p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default EventDetails;
