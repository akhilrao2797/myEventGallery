import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getEvent, getImagesGrouped, getQrCodeUrl } from '../api/events';
import { bulkDelete, downloadZip } from '../api/images';
import { createShareLink } from '../api/shared';
import { generateAlbumPdf } from '../api/album';
import { FiDownload, FiTrash2, FiShare2, FiFileText, FiQrCode } from 'react-icons/fi';
import './EventDetail.css';

export default function EventDetail() {
  const { eventId } = useParams();
  const navigate = useNavigate();
  const [event, setEvent] = useState(null);
  const [grouped, setGrouped] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedIds, setSelectedIds] = useState(new Set());
  const [shareModal, setShareModal] = useState(false);
  const [shareForm, setShareForm] = useState({ imageIds: [], password: '', expiresAt: '', folderName: '' });
  const [shareResult, setShareResult] = useState(null);
  const [albumLoading, setAlbumLoading] = useState(false);

  useEffect(() => {
    Promise.all([
      getEvent(eventId).then((r) => r.data),
      getImagesGrouped(eventId).then((r) => r.data),
    ])
      .then(([evt, grp]) => {
        setEvent(evt);
        setGrouped(grp);
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [eventId]);

  const toggleSelect = (id) => {
    setSelectedIds((prev) => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  };

  const toggleSelectAll = (ids) => {
    if (selectedIds.size === ids.length) setSelectedIds(new Set());
    else setSelectedIds(new Set(ids));
  };

  const handleBulkDelete = async () => {
    if (selectedIds.size === 0) return;
    if (!window.confirm(`Delete ${selectedIds.size} image(s)?`)) return;
    try {
      await bulkDelete(eventId, [...selectedIds]);
      setSelectedIds(new Set());
      const res = await getImagesGrouped(eventId);
      setGrouped(res.data);
    } catch (err) {
      alert(err.message);
    }
  };

  const handleDownloadZip = async () => {
    if (selectedIds.size === 0) return;
    try {
      const blob = await downloadZip(eventId, [...selectedIds]);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `event-${eventId}-photos.zip`;
      a.click();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      alert(err.message);
    }
  };

  const openShareModal = () => {
    const ids = grouped?.guestFolders?.flatMap((f) => f.images?.map((i) => i.id) || []) || [];
    setShareForm({ imageIds: selectedIds.size ? [...selectedIds] : ids.slice(0, 50), password: '', expiresAt: '', folderName: event?.name || '' });
    setShareResult(null);
    setShareModal(true);
  };

  const handleCreateShare = async (e) => {
    e.preventDefault();
    try {
      const res = await createShareLink({
        eventId: Number(eventId),
        imageIds: shareForm.imageIds,
        folderName: shareForm.folderName || null,
        password: shareForm.password || null,
        expiresAt: shareForm.expiresAt || null,
      });
      setShareResult(res.data);
    } catch (err) {
      alert(err.message);
    }
  };

  const handleGenerateAlbum = async () => {
    const ids = selectedIds.size ? [...selectedIds] : (grouped?.guestFolders?.flatMap((f) => f.images?.map((i) => i.id) || []) || []);
    if (ids.length === 0) {
      alert('Select images or ensure event has photos.');
      return;
    }
    setAlbumLoading(true);
    try {
      const blob = await generateAlbumPdf(eventId, ids);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `album-${eventId}.pdf`;
      a.click();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      alert(err.message);
    } finally {
      setAlbumLoading(false);
    }
  };

  if (loading) return <div className="page">Loading...</div>;
  if (error) return <div className="page"><p className="error-msg">{error}</p></div>;
  if (!event || !grouped) return null;

  const allImageIds = grouped.guestFolders?.flatMap((f) => f.images?.map((i) => i.id) || []) || [];

  return (
    <div className="page">
      <div className="event-detail-header">
        <div>
          <h1 className="page-title">{event.name}</h1>
          <p className="event-detail-meta">
            {event.eventDate} {event.venue ? ` · ${event.venue}` : ''} · Code: {event.eventCode}
          </p>
        </div>
        <div className="event-detail-actions">
          <a href={getQrCodeUrl(event.eventCode)} target="_blank" rel="noopener noreferrer" className="btn btn-secondary">
            <FiQrCode /> QR Code
          </a>
          <button type="button" className="btn btn-secondary" onClick={openShareModal}>
            <FiShare2 /> Share link
          </button>
          <button type="button" className="btn btn-secondary" onClick={handleGenerateAlbum} disabled={albumLoading || allImageIds.length === 0}>
            <FiFileText /> Album (PDF)
          </button>
          <button type="button" className="btn btn-secondary" onClick={handleDownloadZip} disabled={selectedIds.size === 0}>
            <FiDownload /> Download ZIP
          </button>
          <button type="button" className="btn btn-secondary" onClick={handleBulkDelete} disabled={selectedIds.size === 0}>
            <FiTrash2 /> Delete
          </button>
        </div>
      </div>

      <div className="event-detail-select-bar">
        <label>
          <input
            type="checkbox"
            checked={allImageIds.length > 0 && selectedIds.size === allImageIds.length}
            onChange={() => toggleSelectAll(allImageIds)}
          />
          Select all ({allImageIds.length})
        </label>
        {selectedIds.size > 0 && <span>{selectedIds.size} selected</span>}
      </div>

      <div className="guest-folders">
        {grouped.guestFolders?.map((folder) => (
          <div key={folder.guestId} className="card guest-folder">
            <h3 className="guest-folder-title">{folder.guestName} ({folder.imageCount})</h3>
            <div className="guest-folder-grid">
              {folder.images?.map((img) => (
                <div key={img.id} className="guest-folder-item">
                  <label className="guest-folder-item-check">
                    <input
                      type="checkbox"
                      checked={selectedIds.has(img.id)}
                      onChange={() => toggleSelect(img.id)}
                    />
                  </label>
                  <img src={img.storageUrl?.startsWith('http') ? img.storageUrl : (process.env.REACT_APP_API_URL || '') + img.storageUrl} alt={img.originalFileName} />
                </div>
              ))}
            </div>
          </div>
        ))}
        {(!grouped.guestFolders || grouped.guestFolders.length === 0) && (
          <div className="card empty-state">
            <p>No photos yet. Share the QR code with guests to start uploading.</p>
          </div>
        )}
      </div>

      {shareModal && (
        <div className="modal-overlay" onClick={() => setShareModal(false)}>
          <div className="modal card" onClick={(e) => e.stopPropagation()}>
            <h3>Create share link</h3>
            {!shareResult ? (
              <form onSubmit={handleCreateShare}>
                <div className="input-group">
                  <label>Folder name</label>
                  <input
                    value={shareForm.folderName}
                    onChange={(e) => setShareForm((f) => ({ ...f, folderName: e.target.value }))}
                    placeholder="Optional"
                  />
                </div>
                <div className="input-group">
                  <label>Password (optional)</label>
                  <input
                    type="password"
                    value={shareForm.password}
                    onChange={(e) => setShareForm((f) => ({ ...f, password: e.target.value }))}
                    placeholder="Leave blank for no password"
                  />
                </div>
                <div className="input-group">
                  <label>Expires at (optional)</label>
                  <input
                    type="datetime-local"
                    value={shareForm.expiresAt}
                    onChange={(e) => setShareForm((f) => ({ ...f, expiresAt: e.target.value }))}
                  />
                </div>
                <p className="create-event-note">{shareForm.imageIds.length} image(s) will be shared.</p>
                <div className="create-event-actions">
                  <button type="button" className="btn btn-secondary" onClick={() => setShareModal(false)}>Cancel</button>
                  <button type="submit" className="btn btn-primary">Create link</button>
                </div>
              </form>
            ) : (
              <div>
                <p className="success-msg">Share link created!</p>
                <p className="create-event-note">URL: {window.location.origin}/shared/{shareResult.shareCode}</p>
                <button type="button" className="btn btn-primary" onClick={() => setShareModal(false)}>Done</button>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
