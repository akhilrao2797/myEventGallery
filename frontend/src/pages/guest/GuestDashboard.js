import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { guestDashboard } from '../../api/guest';
import { FiCalendar, FiUpload } from 'react-icons/fi';
import './GuestDashboard.css';

export default function GuestDashboard() {
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    guestDashboard()
      .then((r) => setList(r.data || []))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="page"><p>Loading...</p></div>;
  if (error) return <div className="page"><p className="error-msg">{error}</p></div>;

  return (
    <div className="page">
      <h1 className="page-title">My Events</h1>
      <p className="guest-dashboard-subtitle">Events where you&apos;ve uploaded photos</p>
      <div className="event-grid">
        {list.length === 0 ? (
          <div className="card empty-state">
            <p>You haven&apos;t uploaded to any event yet. Scan an event QR code to get started.</p>
          </div>
        ) : (
          list.map((evt) => (
            <div key={evt.eventId} className="event-card card">
              <div className="event-card-icon"><FiCalendar /></div>
              <h3 className="event-card-title">{evt.eventName}</h3>
              <p className="event-card-meta">{evt.eventInfo?.eventDate} · {evt.imageCount} photo(s)</p>
              {evt.canModify && (
                <Link to={`/guest-app/event/${evt.eventId}/upload`} className="btn btn-primary guest-dashboard-upload">
                  <FiUpload /> Upload / Manage
                </Link>
              )}
              {!evt.canModify && <p className="guest-dashboard-deadline">{evt.modifyDeadlineMessage}</p>}
            </div>
          ))
        )}
      </div>
    </div>
  );
}
