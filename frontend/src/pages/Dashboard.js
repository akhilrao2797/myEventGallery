import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getMyEvents } from '../services/api';
import { FiPlus, FiLogOut, FiCalendar, FiUsers, FiImage } from 'react-icons/fi';
import './Dashboard.css';

function Dashboard() {
  const navigate = useNavigate();
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const customerName = localStorage.getItem('customerName');

  useEffect(() => {
    loadEvents();
  }, []);

  const loadEvents = async () => {
    try {
      const response = await getMyEvents();
      if (response.data.success) {
        setEvents(response.data.data);
      }
    } catch (err) {
      setError('Failed to load events');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate('/login');
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  if (loading) {
    return <div className="loading">Loading your events...</div>;
  }

  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <div className="container">
          <div className="header-content">
            <div>
              <h1>Welcome, {customerName}!</h1>
              <p>Manage your event galleries</p>
            </div>
            <div className="header-actions">
              <button className="btn btn-primary" onClick={() => navigate('/create-event')}>
                <FiPlus /> Create Event
              </button>
              <button className="btn btn-secondary" onClick={handleLogout}>
                <FiLogOut /> Logout
              </button>
            </div>
          </div>
        </div>
      </header>

      <div className="container">
        {error && <div className="error-message">{error}</div>}
        
        {events.length === 0 ? (
          <div className="empty-state">
            <h2>No events yet</h2>
            <p>Create your first event to start collecting photos</p>
            <button className="btn btn-primary" onClick={() => navigate('/create-event')}>
              <FiPlus /> Create Your First Event
            </button>
          </div>
        ) : (
          <div className="events-grid">
            {events.map((event) => (
              <div 
                key={event.id} 
                className="event-card"
                onClick={() => navigate(`/event/${event.id}`)}
              >
                <div className="event-header">
                  <h3>{event.name}</h3>
                  <span className={`badge ${event.isExpired ? 'badge-danger' : 'badge-success'}`}>
                    {event.isExpired ? 'Expired' : 'Active'}
                  </span>
                </div>
                
                <p className="event-type">{event.eventType.replace('_', ' ')}</p>
                
                <div className="event-stats">
                  <div className="stat">
                    <FiCalendar />
                    <span>{formatDate(event.eventDate)}</span>
                  </div>
                  <div className="stat">
                    <FiUsers />
                    <span>{event.guestCount} Guests</span>
                  </div>
                  <div className="stat">
                    <FiImage />
                    <span>{event.totalUploads} Photos</span>
                  </div>
                </div>
                
                {event.venue && <p className="event-venue">{event.venue}</p>}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default Dashboard;
