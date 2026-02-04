import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { adminLogin, getAdminDashboard, getAllEvents } from '../../services/api';
import { FiUsers, FiCalendar, FiImage, FiDatabase, FiTrendingUp, FiSearch } from 'react-icons/fi';
import './AdminDashboard.css';

function AdminDashboard() {
  const navigate = useNavigate();
  const [stats, setStats] = useState(null);
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      const [statsRes, eventsRes] = await Promise.all([
        getAdminDashboard(),
        getAllEvents({ page: 0, size: 10 })
      ]);
      
      if (statsRes.data.success) {
        setStats(statsRes.data.data);
      }
      if (eventsRes.data.success) {
        setEvents(eventsRes.data.data.content);
      }
    } catch (error) {
      console.error('Error loading dashboard:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="loading">Loading admin dashboard...</div>;
  }

  return (
    <div className="admin-dashboard">
      <div className="container">
        <header className="admin-header">
          <h1>Admin Dashboard</h1>
          <p>System overview and event management</p>
        </header>

        {stats && (
          <>
            <div className="stats-grid">
              <div className="stat-card admin-card">
                <div className="stat-icon purple">
                  <FiUsers />
                </div>
                <div className="stat-content">
                  <h3>{stats.totalCustomers}</h3>
                  <p>Customers</p>
                </div>
              </div>

              <div className="stat-card admin-card">
                <div className="stat-icon blue">
                  <FiCalendar />
                </div>
                <div className="stat-content">
                  <h3>{stats.totalEvents}</h3>
                  <p>Events</p>
                  <span className="substat">{stats.activeEvents} active</span>
                </div>
              </div>

              <div className="stat-card admin-card">
                <div className="stat-icon green">
                  <FiUsers />
                </div>
                <div className="stat-content">
                  <h3>{stats.totalGuests}</h3>
                  <p>Guests</p>
                </div>
              </div>

              <div className="stat-card admin-card">
                <div className="stat-icon pink">
                  <FiImage />
                </div>
                <div className="stat-content">
                  <h3>{stats.totalImages}</h3>
                  <p>Photos</p>
                </div>
              </div>

              <div className="stat-card admin-card">
                <div className="stat-icon orange">
                  <FiDatabase />
                </div>
                <div className="stat-content">
                  <h3>{stats.totalStorageGB?.toFixed(2)} GB</h3>
                  <p>Storage Used</p>
                </div>
              </div>

              <div className="stat-card admin-card">
                <div className="stat-icon teal">
                  <FiTrendingUp />
                </div>
                <div className="stat-content">
                  <h3>{stats.eventsThisMonth}</h3>
                  <p>Events This Month</p>
                </div>
              </div>
            </div>

            <div className="admin-section">
              <h2>Recent Events</h2>
              <div className="events-table">
                <table>
                  <thead>
                    <tr>
                      <th>Event Code</th>
                      <th>Event Name</th>
                      <th>Customer</th>
                      <th>Date</th>
                      <th>Guests</th>
                      <th>Photos</th>
                      <th>Status</th>
                      <th>QR Valid</th>
                    </tr>
                  </thead>
                  <tbody>
                    {events.map(event => (
                      <tr key={event.id}>
                        <td><code>{event.eventCode}</code></td>
                        <td>{event.name}</td>
                        <td>{event.customerName}</td>
                        <td>{new Date(event.eventDate).toLocaleDateString()}</td>
                        <td>{event.guestCount}</td>
                        <td>{event.totalUploads}</td>
                        <td>
                          <span className={`badge ${event.isActive ? 'badge-success' : 'badge-error'}`}>
                            {event.isActive ? 'Active' : 'Inactive'}
                          </span>
                        </td>
                        <td>
                          <span className={`badge ${event.qrCodeValid ? 'badge-success' : 'badge-error'}`}>
                            {event.qrCodeValid ? 'Valid' : 'Expired'}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </>
        )}
      </div>
    </div>
  );
}

export default AdminDashboard;
