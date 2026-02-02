import React, { useState, useCallback } from 'react';
import { useParams } from 'react-router-dom';
import { useDropzone } from 'react-dropzone';
import { uploadImages } from '../services/api';
import { FiUpload, FiCheck, FiX } from 'react-icons/fi';
import './Guest.css';

function GuestUpload() {
  const { guestId } = useParams();
  const [selectedFiles, setSelectedFiles] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [uploadComplete, setUploadComplete] = useState(false);
  const [error, setError] = useState('');

  const onDrop = useCallback((acceptedFiles) => {
    const imageFiles = acceptedFiles.filter(file => file.type.startsWith('image/'));
    
    if (imageFiles.length !== acceptedFiles.length) {
      setError('Only image files are allowed');
    }
    
    setSelectedFiles(prev => [...prev, ...imageFiles]);
    setError('');
  }, []);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      'image/*': ['.jpeg', '.jpg', '.png', '.gif', '.webp']
    },
    multiple: true
  });

  const removeFile = (index) => {
    setSelectedFiles(files => files.filter((_, i) => i !== index));
  };

  const handleUpload = async () => {
    if (selectedFiles.length === 0) {
      setError('Please select at least one image');
      return;
    }

    setUploading(true);
    setError('');
    setUploadProgress(0);

    try {
      // Simulate progress
      const progressInterval = setInterval(() => {
        setUploadProgress(prev => {
          if (prev >= 90) {
            clearInterval(progressInterval);
            return 90;
          }
          return prev + 10;
        });
      }, 200);

      const response = await uploadImages(guestId, selectedFiles);
      
      clearInterval(progressInterval);
      setUploadProgress(100);
      
      if (response.data.success) {
        setUploadComplete(true);
        setTimeout(() => {
          setSelectedFiles([]);
          setUploadComplete(false);
          setUploadProgress(0);
        }, 3000);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Upload failed. Please try again.');
      setUploadProgress(0);
    } finally {
      setUploading(false);
    }
  };

  const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
  };

  return (
    <div className="guest-container">
      <div className="guest-card upload-card">
        <div className="guest-header">
          <h1>Upload Your Photos</h1>
          <p>Share your favorite moments from the event</p>
        </div>

        {error && <div className="error-message">{error}</div>}
        {uploadComplete && (
          <div className="success-message">
            <FiCheck /> Photos uploaded successfully!
          </div>
        )}

        <div {...getRootProps()} className={`dropzone ${isDragActive ? 'active' : ''}`}>
          <input {...getInputProps()} />
          <FiUpload size={48} />
          {isDragActive ? (
            <p>Drop your images here</p>
          ) : (
            <>
              <p>Drag and drop images here</p>
              <p className="dropzone-hint">or click to browse</p>
            </>
          )}
        </div>

        {selectedFiles.length > 0 && (
          <div className="files-preview">
            <h3>Selected Files ({selectedFiles.length})</h3>
            <div className="files-list">
              {selectedFiles.map((file, index) => (
                <div key={index} className="file-item">
                  <img 
                    src={URL.createObjectURL(file)} 
                    alt={file.name}
                    className="file-thumbnail"
                  />
                  <div className="file-info">
                    <p className="file-name">{file.name}</p>
                    <p className="file-size">{formatFileSize(file.size)}</p>
                  </div>
                  <button 
                    className="file-remove"
                    onClick={() => removeFile(index)}
                    disabled={uploading}
                  >
                    <FiX />
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}

        {uploading && (
          <div className="upload-progress">
            <div className="progress-bar">
              <div 
                className="progress-fill" 
                style={{ width: `${uploadProgress}%` }}
              />
            </div>
            <p>Uploading... {uploadProgress}%</p>
          </div>
        )}

        <button 
          className="btn btn-primary upload-btn"
          onClick={handleUpload}
          disabled={selectedFiles.length === 0 || uploading}
        >
          {uploading ? 'Uploading...' : `Upload ${selectedFiles.length} Photo${selectedFiles.length !== 1 ? 's' : ''}`}
        </button>

        <div className="upload-info">
          <p>Supported formats: JPEG, PNG, GIF, WebP</p>
          <p>You can upload multiple photos at once</p>
        </div>
      </div>
    </div>
  );
}

export default GuestUpload;
