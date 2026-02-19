import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { myEvents } from '../api/events';
import { FiCalendar, FiImage, FiUsers, FiPlusCircle } from 'react-icons/fi';
import './Dashboard.css';

export default function Dashboard() {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    myEvents()
      .then((res) => setEvents(res.data || []))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="page"><p>Loading events...</p></div>;
  if (error) return <div className="page"><p className="error-msg">{error}</p></div>;

  return (
    <div className="page">
      <div className="dashboard-header">
        <h1 className="page-title">My Events</h1>
        <Link to="/events/new" className="btn btn-primary">
          <FiPlusCircle /> New Event
        </Link>
      </div>
      <div className="event-grid">
        {events.length === 0 ? (
          <div className="card empty-state">
            <p>No events yet. Create your first event to get started.</p>
            <Link to="/events/new" className="btn btn-primary">Create Event</Link>
          </div>
        ) : (
          events.map((evt) => (
            <Link
              key={evt.id}
              to={`/events/${evt.id}`}
              className="event-card card"
            >
              <div className="event-card-icon">
                <FiCalendar />
              </div>
              <h3 className="event-card-title">{evt.name}</h3>
              <p className="event-card-meta">
                {evt.eventDate} {evt.venue ? ` · ${evt.venue}` : ''}
              </p>
              <div className="event-card-stats">
                <span><FiUsers /> {evt.guestCount ?? 0} guests</span>
                <span><FiImage /> {evt.totalImages ?? 0} photos</span>
              </div>
              <span className="event-card-code">Code: {evt.eventCode}</span>
            </Link>
          ))
        )}
      </div>
    </div>
  );
}
