import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { 
  getEventDetails, 
  getEventImagesGrouped, 
  getQRCode,
  downloadImagesAsZip,
  createSharedFolder,
  getImageUrl 
} from '../services/api';
import { 
  FiArrowLeft, FiDownload, FiUsers, FiImage, FiCalendar, 
  FiFolder, FiCheckSquare, FiSquare, FiShare2, FiTrash2 
} from 'react-icons/fi';
import './EventDetails.css';

function EventDetails() {
  const { eventId } = useParams();
  const navigate = useNavigate();
  const [event, setEvent] = useState(null);
  const [groupedImages, setGroupedImages] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedImage, setSelectedImage] = useState(null);
  const [selectedImages, setSelectedImages] = useState(new Set());
  const [selectMode, setSelectMode] = useState(false);
  const [expandedFolders, setExpandedFolders] = useState(new Set());
  const [showShareModal, setShowShareModal] = useState(false);
  const [shareLoading, setShareLoading] = useState(false);
  const [sharedLink, setSharedLink] = useState('');

  useEffect(() => {
    loadEventDetails();
    loadGroupedImages();
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

  const loadGroupedImages = async () => {
    try {
      const response = await getEventImagesGrouped(eventId);
      if (response.data.success) {
        setGroupedImages(response.data.data);
        // Expand all folders by default
        const guestIds = response.data.data.guestFolders?.map(f => f.guestId) || [];
        setExpandedFolders(new Set(guestIds));
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
      alert('Failed to download QR code');
    }
  };

  const toggleFolder = (guestId) => {
    const newExpanded = new Set(expandedFolders);
    if (newExpanded.has(guestId)) {
      newExpanded.delete(guestId);
    } else {
      newExpanded.add(guestId);
    }
    setExpandedFolders(newExpanded);
  };

  const toggleImageSelection = (imageId) => {
    const newSelected = new Set(selectedImages);
    if (newSelected.has(imageId)) {
      newSelected.delete(imageId);
    } else {
      newSelected.add(imageId);
    }
    setSelectedImages(newSelected);
  };

  const toggleFolderSelection = (folder) => {
    const folderImageIds = folder.images.map(img => img.imageId);
    const allSelected = folderImageIds.every(id => selectedImages.has(id));
    
    const newSelected = new Set(selectedImages);
    if (allSelected) {
      folderImageIds.forEach(id => newSelected.delete(id));
    } else {
      folderImageIds.forEach(id => newSelected.add(id));
    }
    setSelectedImages(newSelected);
  };

  const selectAllImages = () => {
    if (!groupedImages) return;
    const allImageIds = groupedImages.guestFolders?.flatMap(f => 
      f.images.map(img => img.imageId)
    ) || [];
    setSelectedImages(new Set(allImageIds));
  };

  const deselectAll = () => {
    setSelectedImages(new Set());
  };

  const handleBulkDownload = async () => {
    if (selectedImages.size === 0) {
      alert('Please select images to download');
      return;
    }

    try {
      const imageIds = Array.from(selectedImages);
      const response = await downloadImagesAsZip(imageIds);
      
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.download = `${event.name}-photos.zip`;
      link.click();
      window.URL.revokeObjectURL(url);
      
      alert(`Downloaded ${imageIds.length} images successfully!`);
    } catch (err) {
      console.error('Download failed', err);
      alert('Failed to download images. Please try again.');
    }
  };

  const handleCreateSharedFolder = async (e) => {
    e.preventDefault();
    if (selectedImages.size === 0) {
      alert('Please select images to share');
      return;
    }

    const folderName = e.target.folderName.value;
    const password = e.target.password.value;

    setShareLoading(true);
    try {
      const response = await createSharedFolder({
        folderName,
        eventId: parseInt(eventId),
        imageIds: Array.from(selectedImages),
        accessPassword: password || null
      });

      if (response.data.success) {
        const shareCode = response.data.data.shareCode;
        const link = `${window.location.origin}/shared/${shareCode}`;
        setSharedLink(link);
      }
    } catch (err) {
      console.error('Failed to create shared folder', err);
      alert('Failed to create shared folder');
    } finally {
      setShareLoading(false);
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

  const totalImages = groupedImages?.totalImages || 0;

  return (
    <div className="event-details-page">
      <div className="container">
        <button className="btn-back" onClick={() => navigate('/dashboard')}>
          <FiArrowLeft /> Back to Dashboard
        </button>

        <div className="event-header-card card">
          <div className="event-info">
            <h1>{event.name}</h1>
            <p className="event-type">{event.eventType?.replace('_', ' ')}</p>
            
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
                  <h3>{groupedImages?.guestFolders?.length || 0}</h3>
                  <p>Guests</p>
                </div>
              </div>
              <div className="stat-box">
                <FiImage />
                <div>
                  <h3>{totalImages}</h3>
                  <p>Photos</p>
                </div>
              </div>
              <div className="stat-box">
                <FiFolder />
                <div>
                  <h3>{selectedImages.size}</h3>
                  <p>Selected</p>
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

        {/* Selection and Action Bar */}
        <div className="action-bar card">
          <div className="action-bar-left">
            <button 
              className={`btn ${selectMode ? 'btn-primary' : 'btn-secondary'}`}
              onClick={() => {
                setSelectMode(!selectMode);
                if (selectMode) deselectAll();
              }}
            >
              <FiCheckSquare /> {selectMode ? 'Cancel Selection' : 'Select Images'}
            </button>
            
            {selectMode && (
              <>
                <button className="btn btn-secondary" onClick={selectAllImages}>
                  Select All ({totalImages})
                </button>
                <button className="btn btn-secondary" onClick={deselectAll}>
                  Deselect All
                </button>
                <span className="selection-count">
                  {selectedImages.size} selected
                </span>
              </>
            )}
          </div>

          {selectMode && selectedImages.size > 0 && (
            <div className="action-bar-right">
              <button className="btn btn-success" onClick={handleBulkDownload}>
                <FiDownload /> Download Selected ({selectedImages.size})
              </button>
              <button className="btn btn-primary" onClick={() => setShowShareModal(true)}>
                <FiShare2 /> Create Shared Folder
              </button>
            </div>
          )}
        </div>

        {/* Guest Folders */}
        <div className="card">
          <h2>
            <FiFolder /> Guest Folders ({groupedImages?.guestFolders?.length || 0})
          </h2>
          
          {!groupedImages || groupedImages.guestFolders?.length === 0 ? (
            <div className="empty-gallery">
              <FiImage size={48} />
              <p>No photos uploaded yet. Share the QR code with your guests!</p>
            </div>
          ) : (
            <div className="guest-folders">
              {groupedImages.guestFolders.map((folder) => {
                const isExpanded = expandedFolders.has(folder.guestId);
                const folderImageIds = folder.images.map(img => img.imageId);
                const allSelected = folderImageIds.every(id => selectedImages.has(id));
                const someSelected = folderImageIds.some(id => selectedImages.has(id)) && !allSelected;

                return (
                  <div key={folder.guestId} className="guest-folder">
                    <div className="folder-header" onClick={() => toggleFolder(folder.guestId)}>
                      <div className="folder-info">
                        {selectMode && (
                          <button 
                            className="folder-checkbox"
                            onClick={(e) => {
                              e.stopPropagation();
                              toggleFolderSelection(folder);
                            }}
                          >
                            {allSelected ? <FiCheckSquare /> : someSelected ? <FiCheckSquare style={{opacity: 0.5}} /> : <FiSquare />}
                          </button>
                        )}
                        <FiFolder className={isExpanded ? 'folder-open' : ''} />
                        <div>
                          <h3>{folder.guestName}</h3>
                          <p>{folder.imageCount} {folder.imageCount === 1 ? 'photo' : 'photos'}</p>
                        </div>
                      </div>
                      <span className="folder-toggle">{isExpanded ? '▼' : '▶'}</span>
                    </div>

                    {isExpanded && (
                      <div className="folder-content">
                        <div className="gallery-grid">
                          {folder.images.map((image) => {
                            const isSelected = selectedImages.has(image.imageId);
                            const imageUrl = getImageUrl(image.s3Url);

                            return (
                              <div 
                                key={image.imageId} 
                                className={`gallery-item ${isSelected ? 'selected' : ''}`}
                                onClick={() => {
                                  if (selectMode) {
                                    toggleImageSelection(image.imageId);
                                  } else {
                                    setSelectedImage(image);
                                  }
                                }}
                              >
                                {selectMode && (
                                  <div className="selection-overlay">
                                    {isSelected ? <FiCheckSquare /> : <FiSquare />}
                                  </div>
                                )}
                                <img src={imageUrl} alt={image.fileName} loading="lazy" />
                                <div className="gallery-item-overlay">
                                  <p>{image.fileName}</p>
                                  <small>{(image.fileSizeMB || 0).toFixed(2)} MB</small>
                                </div>
                              </div>
                            );
                          })}
                        </div>
                      </div>
                    )}
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>

      {/* Lightbox for image preview */}
      {selectedImage && !selectMode && (
        <div className="lightbox" onClick={() => setSelectedImage(null)}>
          <div className="lightbox-content" onClick={(e) => e.stopPropagation()}>
            <button className="lightbox-close" onClick={() => setSelectedImage(null)}>×</button>
            <img src={getImageUrl(selectedImage.s3Url)} alt={selectedImage.fileName} />
            <div className="lightbox-info">
              <p><strong>File:</strong> {selectedImage.fileName}</p>
              <p><strong>Size:</strong> {(selectedImage.fileSizeMB || 0).toFixed(2)} MB</p>
              <p><strong>Uploaded:</strong> {new Date(selectedImage.uploadedAt).toLocaleString()}</p>
            </div>
          </div>
        </div>
      )}

      {/* Share Folder Modal */}
      {showShareModal && (
        <div className="modal-overlay" onClick={() => setShowShareModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h2>Create Shared Folder</h2>
            <p>Create a secure link to share {selectedImages.size} selected images</p>
            
            {!sharedLink ? (
              <form onSubmit={handleCreateSharedFolder}>
                <div className="form-group">
                  <label>Folder Name *</label>
                  <input 
                    type="text" 
                    name="folderName" 
                    placeholder="e.g., Wedding Highlights"
                    required 
                  />
                </div>
                
                <div className="form-group">
                  <label>Password (Optional)</label>
                  <input 
                    type="password" 
                    name="password" 
                    placeholder="Leave empty for no password"
                  />
                  <small>Add a password to restrict access</small>
                </div>

                <div className="modal-actions">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowShareModal(false)}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-primary" disabled={shareLoading}>
                    {shareLoading ? 'Creating...' : 'Create Shared Folder'}
                  </button>
                </div>
              </form>
            ) : (
              <div className="share-success">
                <p className="success-message">✓ Shared folder created successfully!</p>
                <div className="share-link-box">
                  <input 
                    type="text" 
                    value={sharedLink} 
                    readOnly 
                    onClick={(e) => e.target.select()}
                  />
                  <button 
                    className="btn btn-primary"
                    onClick={() => {
                      navigator.clipboard.writeText(sharedLink);
                      alert('Link copied to clipboard!');
                    }}
                  >
                    Copy Link
                  </button>
                </div>
                <button 
                  className="btn btn-secondary"
                  onClick={() => {
                    setShowShareModal(false);
                    setSharedLink('');
                    setSelectedImages(new Set());
                    setSelectMode(false);
                  }}
                >
                  Done
                </button>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}

export default EventDetails;
