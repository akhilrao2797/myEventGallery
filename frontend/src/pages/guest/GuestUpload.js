import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useDropzone } from 'react-dropzone';
import { guestDashboard, guestUpload, guestDeleteImage } from '../../api/guest';
import { FiUpload, FiTrash2 } from 'react-icons/fi';
import './GuestUpload.css';

export default function GuestUpload() {
  const { eventId } = useParams();
  const navigate = useNavigate();
  const [eventData, setEventData] = useState(null);
  const [images, setImages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    guestDashboard()
      .then((r) => {
        const data = (r.data || []).find((e) => String(e.eventId) === String(eventId));
        setEventData(data);
        setImages(data?.images || []);
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [eventId]);

  const { getRootProps, getInputProps } = useDropzone({
    accept: { 'image/*': ['.jpeg', '.jpg', '.png', '.gif', '.webp'] },
    maxFiles: 20,
    disabled: uploading || !eventData?.canModify,
    onDrop: async (acceptedFiles) => {
      if (acceptedFiles.length === 0) return;
      setUploading(true);
      setError('');
      const formData = new FormData();
      acceptedFiles.forEach((f) => formData.append('files', f));
      try {
        const res = await guestUpload(eventId, formData);
        setImages((prev) => [...(res.data || []), ...prev]);
      } catch (err) {
        setError(err.message);
      } finally {
        setUploading(false);
      }
    },
  });

  const handleDelete = async (imageId) => {
    if (!window.confirm('Delete this photo?')) return;
    try {
      await guestDeleteImage(imageId);
      setImages((prev) => prev.filter((i) => i.id !== imageId));
    } catch (err) {
      alert(err.message);
    }
  };

  if (loading) return <div className="page">Loading...</div>;
  if (error && !eventData) return <div className="page"><p className="error-msg">{error}</p></div>;
  if (!eventData) return <div className="page"><p>Event not found.</p></div>;

  return (
    <div className="page">
      <div className="guest-upload-header">
        <h1 className="page-title">{eventData.eventName}</h1>
        <button type="button" className="btn btn-ghost" onClick={() => navigate('/guest-app/dashboard')}>
          ← Back to my events
        </button>
      </div>
      {!eventData.canModify && (
        <p className="guest-upload-deadline">{eventData.modifyDeadlineMessage}</p>
      )}
      {eventData.canModify && (
        <div
          {...getRootProps()}
          className={`guest-upload-dropzone card ${uploading ? 'uploading' : ''}`}
        >
          <input {...getInputProps()} />
          <FiUpload className="guest-upload-dropzone-icon" />
          <p>Drag & drop photos here, or click to select</p>
          <p className="guest-upload-dropzone-hint">Max 20 per batch</p>
        </div>
      )}
      {error && <p className="error-msg">{error}</p>}
      <div className="guest-upload-grid">
        {images.map((img) => (
          <div key={img.id} className="guest-upload-item card">
            <img src={img.storageUrl?.startsWith('http') ? img.storageUrl : (process.env.REACT_APP_API_URL || '') + img.storageUrl} alt={img.originalFileName} />
            {eventData.canModify && (
              <button type="button" className="btn btn-ghost guest-upload-delete" onClick={() => handleDelete(img.id)} aria-label="Delete">
                <FiTrash2 />
              </button>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
