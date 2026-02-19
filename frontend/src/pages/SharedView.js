import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { getSharedPublic } from '../api/shared';
import './SharedView.css';

export default function SharedView() {
  const { shareCode } = useParams();
  const [data, setData] = useState(null);
  const [password, setPassword] = useState('');
  const [needPassword, setNeedPassword] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  const load = (pwd) => {
    setError('');
    getSharedPublic(shareCode, pwd || undefined)
      .then((r) => {
        setData(r.data);
        setNeedPassword(false);
      })
      .catch((err) => {
        if (err.message?.toLowerCase().includes('password')) setNeedPassword(true);
        setError(err.message);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, [shareCode]);

  const handlePasswordSubmit = (e) => {
    e.preventDefault();
    setLoading(true);
    load(password);
  };

  if (loading && !needPassword) return <div className="shared-view"><p>Loading...</p></div>;
  if (error && !needPassword) return <div className="shared-view"><p className="error-msg">{error}</p></div>;
  if (needPassword) {
    return (
      <div className="shared-view">
        <div className="card shared-view-card">
          <h2>This link is protected</h2>
          <form onSubmit={handlePasswordSubmit}>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter password"
              className="shared-view-input"
            />
            <button type="submit" className="btn btn-primary">View</button>
          </form>
          {error && <p className="error-msg">{error}</p>}
        </div>
      </div>
    );
  }
  if (!data) return null;

  const urls = data.imageUrls || [];

  return (
    <div className="shared-view">
      <div className="shared-view-header">
        <h1>{data.folderName || 'Shared photos'}</h1>
        {data.expiresAt && <p className="shared-view-expiry">Expires: {new Date(data.expiresAt).toLocaleString()}</p>}
      </div>
      <div className="shared-view-grid">
        {urls.map((url, i) => (
          <div key={i} className="shared-view-item">
            <img src={url.startsWith('http') ? url : (process.env.REACT_APP_API_URL || '') + url} alt={`Photo ${i + 1}`} />
          </div>
        ))}
      </div>
      {urls.length === 0 && <p>No images in this share.</p>}
    </div>
  );
}
