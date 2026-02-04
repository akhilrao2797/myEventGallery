import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { saveAs } from 'file-saver';
import { FiDownload, FiShare2, FiFolder, FiCheckSquare, FiSquare } from 'react-icons/fi';
import './EventDetailsEnhanced.css';

function EventDetailsEnhanced() {
  const { eventId } = useParams();
  const navigate = useNavigate();
  const [groupedImages, setGroupedImages] = useState(null);
  const [selectedImages, setSelectedImages] = useState(new Set());
  const [loading, setLoading] = useState(true);
  const [showCreateFolder, setShowCreateFolder] = useState(false);
  const [folderName, setFolderName] = useState('');
  const [folderPassword, setFolderPassword] = useState('');
  const baseURL = 'http://localhost:8080';

  useEffect(() => {
    loadGroupedImages();
  }, [eventId]);

  const loadGroupedImages = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get(
        `/api/images/event/${eventId}/grouped`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      if (response.data.success) {
        setGroupedImages(response.data.data);
      }
    } catch (error) {
      console.error('Error loading images:', error);
    } finally {
      setLoading(false);
    }
  };

  const toggleImageSelection = (imageId) => {
    const newSelection = new Set(selectedImages);
    if (newSelection.has(imageId)) {
      newSelection.delete(imageId);
    } else {
      newSelection.add(imageId);
    }
    setSelectedImages(newSelection);
  };

  const selectAllInFolder = (images) => {
    const newSelection = new Set(selectedImages);
    images.forEach(img => newSelection.add(img.id));
    setSelectedImages(newSelection);
  };

  const deselectAllInFolder = (images) => {
    const newSelection = new Set(selectedImages);
    images.forEach(img => newSelection.delete(img.id));
    setSelectedImages(newSelection);
  };

  const downloadSelectedAsZip = async () => {
    if (selectedImages.size === 0) {
      alert('Please select images to download');
      return;
    }

    try {
      const token = localStorage.getItem('token');
      const response = await axios.post(
        '/api/images/download-zip',
        { imageIds: Array.from(selectedImages) },
        {
          headers: { Authorization: `Bearer ${token}` },
          responseType: 'blob'
        }
      );

      const blob = new Blob([response.data], { type: 'application/zip' });
      saveAs(blob, `${groupedImages.eventName}-images.zip`);
      alert('Download started!');
    } catch (error) {
      alert('Error downloading images: ' + error.message);
    }
  };

  const createSharedFolder = async () => {
    if (!folderName || selectedImages.size === 0) {
      alert('Please enter folder name and select images');
      return;
    }

    try {
      const token = localStorage.getItem('token');
      const response = await axios.post(
        '/api/shared-folders',
        {
          folderName,
          eventId: parseInt(eventId),
          imageIds: Array.from(selectedImages),
          accessPassword: folderPassword || null
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );

      if (response.data.success) {
        const folder = response.data.data;
        alert(`Folder created! Share code: ${folder.shareCode}\nShare URL: ${folder.shareUrl}`);
        setShowCreateFolder(false);
        setFolderName('');
        setFolderPassword('');
        setSelectedImages(new Set());
      }
    } catch (error) {
      alert('Error creating folder: ' + error.message);
    }
  };

  const getImageUrl = (image) => {
    if (image.s3Url.startsWith('http')) {
      return image.s3Url; // S3 URL
    }
    return `${baseURL}/api/files/${image.s3Url}`; // Local URL
  };

  if (loading) {
    return <div className="loading">Loading images...</div>;
  }

  if (!groupedImages) {
    return <div className="error">Failed to load images</div>;
  }

  return (
    <div className="event-details-enhanced">
      <div className="container">
        <div className="header-section">
          <div>
            <h1>{groupedImages.eventName}</h1>
            <p className="event-stats">
              {groupedImages.totalImages} photos from {groupedImages.totalGuests} guests
            </p>
          </div>
          <div className="header-actions">
            {selectedImages.size > 0 && (
              <>
                <span className="selection-count">{selectedImages.size} selected</span>
                <button className="btn btn-primary" onClick={downloadSelectedAsZip}>
                  <FiDownload /> Download ZIP
                </button>
                <button className="btn btn-secondary" onClick={() => setShowCreateFolder(true)}>
                  <FiShare2 /> Create Shared Folder
                </button>
              </>
            )}
          </div>
        </div>

        {showCreateFolder && (
          <div className="create-folder-modal">
            <div className="modal-content">
              <h3>Create Shared Folder</h3>
              <input
                type="text"
                placeholder="Folder Name"
                value={folderName}
                onChange={(e) => setFolderName(e.target.value)}
                className="input"
              />
              <input
                type="password"
                placeholder="Password (optional)"
                value={folderPassword}
                onChange={(e) => setFolderPassword(e.target.value)}
                className="input"
              />
              <div className="modal-actions">
                <button className="btn btn-primary" onClick={createSharedFolder}>
                  Create
                </button>
                <button className="btn btn-ghost" onClick={() => setShowCreateFolder(false)}>
                  Cancel
                </button>
              </div>
            </div>
          </div>
        )}

        <div className="guest-folders">
          {Object.entries(groupedImages.guestGroups).map(([guestName, group]) => (
            <div key={group.guestId} className="guest-folder">
              <div className="folder-header">
                <div className="folder-title">
                  <FiFolder />
                  <h2>{guestName}</h2>
                  <span className="image-count">{group.imageCount} photos</span>
                  {group.guestEmail && <span className="guest-email">{group.guestEmail}</span>}
                </div>
                <div className="folder-actions">
                  <button
                    className="btn-small"
                    onClick={() => selectAllInFolder(group.images)}
                  >
                    Select All
                  </button>
                  <button
                    className="btn-small"
                    onClick={() => deselectAllInFolder(group.images)}
                  >
                    Deselect All
                  </button>
                </div>
              </div>

              <div className="images-grid">
                {group.images.map((image) => (
                  <div key={image.id} className="image-card">
                    <div
                      className="image-checkbox"
                      onClick={() => toggleImageSelection(image.id)}
                    >
                      {selectedImages.has(image.id) ? (
                        <FiCheckSquare className="checked" />
                      ) : (
                        <FiSquare />
                      )}
                    </div>
                    <img
                      src={getImageUrl(image)}
                      alt={image.originalFileName}
                      className="image-preview"
                      onClick={() => toggleImageSelection(image.id)}
                    />
                    <div className="image-info">
                      <span className="image-name">{image.originalFileName}</span>
                      <span className="image-size">{image.fileSizeMB?.toFixed(2)} MB</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default EventDetailsEnhanced;
