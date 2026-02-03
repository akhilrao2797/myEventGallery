import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getMyEvents } from '../services/api';
import { FiPlus, FiLogOut, FiCalendar, FiUsers, FiImage, FiTrendingUp } from 'react-icons/fi';
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

  const getEventStats = () => {
    const totalGuests = events.reduce((sum, event) => sum + (event.guestCount || 0), 0);
    const totalPhotos = events.reduce((sum, event) => sum + (event.totalUploads || 0), 0);
    return { totalEvents: events.length, totalGuests, totalPhotos };
  };

  const stats = getEventStats();

  if (loading) {
    return (
      <div className="loading-screen">
        <div className="loader"></div>
        <p>Loading your events...</p>
      </div>
    );
  }

  return (
    <div className="dashboard">
      {/* Modern Header */}
      <header className="dashboard-header">
        <div className="header-background"></div>
        <div className="container">
          <div className="header-content">
            <div className="header-info">
              <h1 className="header-title">Welcome back, {customerName}!</h1>
              <p className="header-subtitle">Manage your event galleries and track engagement</p>
            </div>
            <div className="header-actions">
              <button className="btn btn-primary btn-create" onClick={() => navigate('/create-event')}>
                <FiPlus /> New Event
              </button>
              <button className="btn btn-ghost" onClick={handleLogout}>
                <FiLogOut /> Logout
              </button>
            </div>
          </div>

          {/* Stats Cards */}
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-icon purple">
                <FiCalendar />
              </div>
              <div className="stat-content">
                <h3>{stats.totalEvents}</h3>
                <p>Total Events</p>
              </div>
              <div className="stat-trend positive">
                <FiTrendingUp size={16} />
              </div>
            </div>
            <div className="stat-card">
              <div className="stat-icon blue">
                <FiUsers />
              </div>
              <div className="stat-content">
                <h3>{stats.totalGuests}</h3>
                <p>Total Guests</p>
              </div>
              <div className="stat-trend positive">
                <FiTrendingUp size={16} />
              </div>
            </div>
            <div className="stat-card">
              <div className="stat-icon pink">
                <FiImage />
              </div>
              <div className="stat-content">
                <h3>{stats.totalPhotos}</h3>
                <p>Photos Collected</p>
              </div>
              <div className="stat-trend positive">
                <FiTrendingUp size={16} />
              </div>
            </div>
          </div>
        </div>
      </header>

      <div className="container">
        {error && <div className="alert alert-error">{error}</div>}
        
        {events.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon">
              <FiCalendar size={64} />
            </div>
            <h2>No events yet</h2>
            <p>Create your first event to start collecting beautiful memories from your guests</p>
            <button className="btn btn-primary btn-large" onClick={() => navigate('/create-event')}>
              <FiPlus /> Create Your First Event
            </button>
          </div>
        ) : (
          <>
            <div className="section-header">
              <h2>Your Events</h2>
              <p>Click on any event to view details and manage photos</p>
            </div>
            <div className="events-grid">
              {events.map((event) => (
                <div 
                  key={event.id} 
                  className="event-card modern-card"
                  onClick={() => navigate(`/event/${event.id}`)}
                >
                  <div className="card-header">
                    <div className="event-badge-container">
                      <span className={`badge badge-${event.isExpired ? 'error' : 'success'}`}>
                        {event.isExpired ? 'Expired' : 'Active'}
                      </span>
                      <span className="badge badge-outline">{event.eventType.replace('_', ' ')}</span>
                    </div>
                  </div>
                  
                  <div className="card-body">
                    <h3 className="event-title">{event.name}</h3>
                    
                    <div className="event-meta">
                      <div className="meta-item">
                        <FiCalendar size={18} />
                        <span>{formatDate(event.eventDate)}</span>
                      </div>
                      {event.venue && (
                        <div className="meta-item venue">
                          <span>{event.venue}</span>
                        </div>
                      )}
                    </div>
                    
                    <div className="event-stats-mini">
                      <div className="stat-mini">
                        <FiUsers size={20} />
                        <div>
                          <strong>{event.guestCount}</strong>
                          <span>Guests</span>
                        </div>
                      </div>
                      <div className="stat-mini">
                        <FiImage size={20} />
                        <div>
                          <strong>{event.totalUploads}</strong>
                          <span>Photos</span>
                        </div>
                      </div>
                    </div>
                  </div>
                  
                  <div className="card-footer">
                    <div className="storage-info">
                      <div className="storage-bar">
                        <div 
                          className="storage-fill" 
                          style={{ width: `${Math.min((event.totalSizeMB / 100) * 100, 100)}%` }}
                        ></div>
                      </div>
                      <span className="storage-text">{event.totalSizeMB?.toFixed(1) || 0} MB used</span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </>
        )}
      </div>
    </div>
  );
}

export default Dashboard;
