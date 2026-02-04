import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  adminLogin, 
  getAdminDashboard, 
  getAllEvents, 
  searchEvents,
  deleteEvent,
  getAllCustomers,
  deleteCustomer 
} from '../services/api';
import { 
  FiUsers, FiCalendar, FiImage, FiDatabase, FiTrendingUp, 
  FiSearch, FiEdit, FiTrash2, FiLogOut, FiFilter, FiDownload,
  FiEye, FiSettings, FiBarChart2, FiPackage
} from 'react-icons/fi';
import './AdminDashboard.css';

function AdminDashboard() {
  const navigate = useNavigate();
  const [stats, setStats] = useState(null);
  const [events, setEvents] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [activeTab, setActiveTab] = useState('overview'); // overview, events, customers
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState(null);

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      const statsRes = await getAdminDashboard();
      
      if (statsRes.data.success) {
        setStats(statsRes.data.data);
      }
      
      // Load events
      await loadEvents();
    } catch (error) {
      console.error('Error loading dashboard:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadEvents = async () => {
    try {
      const eventsRes = await getAllEvents({ page: 0, size: 20 });
      if (eventsRes.data.success) {
        setEvents(eventsRes.data.data.content || []);
      }
    } catch (error) {
      console.error('Error loading events:', error);
    }
  };

  const loadCustomers = async () => {
    try {
      const customersRes = await getAllCustomers({ page: 0, size: 20 });
      if (customersRes.data.success) {
        setCustomers(customersRes.data.data.content || []);
      }
    } catch (error) {
      console.error('Error loading customers:', error);
    }
  };

  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      loadEvents();
      return;
    }
    
    try {
      const response = await searchEvents(searchQuery);
      if (response.data.success) {
        setEvents(response.data.data.content || []);
      }
    } catch (error) {
      console.error('Search failed:', error);
    }
  };

  const handleDeleteEvent = async (eventId) => {
    if (!window.confirm('Are you sure you want to delete this event? This action cannot be undone.')) {
      return;
    }
    
    try {
      await deleteEvent(eventId);
      alert('Event deleted successfully');
      loadEvents();
      loadDashboard(); // Refresh stats
    } catch (error) {
      alert('Failed to delete event: ' + (error.response?.data?.message || 'Unknown error'));
    }
  };

  const handleDeleteCustomer = async (customerId) => {
    if (!window.confirm('Are you sure you want to delete this customer? All their events will be deleted too!')) {
      return;
    }
    
    try {
      await deleteCustomer(customerId);
      alert('Customer deleted successfully');
      loadCustomers();
      loadDashboard(); // Refresh stats
    } catch (error) {
      alert('Failed to delete customer: ' + (error.response?.data?.message || 'Unknown error'));
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate('/admin/login');
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  const formatDateTime = (dateString) => {
    return new Date(dateString).toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  if (loading) {
    return (
      <div className="loading-screen">
        <div className="loader"></div>
        <p>Loading admin dashboard...</p>
      </div>
    );
  }

  return (
    <div className="admin-dashboard">
      {/* Modern Header with Gradient */}
      <header className="admin-header">
        <div className="header-background"></div>
        <div className="container">
          <div className="header-content">
            <div className="header-info">
              <h1>Admin Dashboard</h1>
              <p>System overview and management</p>
            </div>
            <div className="header-actions">
              <button className="btn btn-ghost" onClick={handleLogout}>
                <FiLogOut /> Logout
              </button>
            </div>
          </div>
        </div>
      </header>

      <div className="container">
        {/* Stats Cards - Modern Design */}
        {stats && (
          <div className="stats-section">
            <div className="stats-grid">
              <div className="stat-card gradient-purple">
                <div className="stat-icon">
                  <FiUsers />
                </div>
                <div className="stat-details">
                  <h3>{stats.totalCustomers || 0}</h3>
                  <p>Total Customers</p>
                </div>
                <div className="stat-trend">
                  <FiTrendingUp size={16} />
                </div>
              </div>

              <div className="stat-card gradient-blue">
                <div className="stat-icon">
                  <FiCalendar />
                </div>
                <div className="stat-details">
                  <h3>{stats.totalEvents || 0}</h3>
                  <p>Total Events</p>
                  <span className="substat">{stats.activeEvents || 0} active</span>
                </div>
                <div className="stat-trend">
                  <FiTrendingUp size={16} />
                </div>
              </div>

              <div className="stat-card gradient-green">
                <div className="stat-icon">
                  <FiUsers />
                </div>
                <div className="stat-details">
                  <h3>{stats.totalGuests || 0}</h3>
                  <p>Total Guests</p>
                </div>
                <div className="stat-trend">
                  <FiTrendingUp size={16} />
                </div>
              </div>

              <div className="stat-card gradient-pink">
                <div className="stat-icon">
                  <FiImage />
                </div>
                <div className="stat-details">
                  <h3>{stats.totalImages || 0}</h3>
                  <p>Total Photos</p>
                </div>
                <div className="stat-trend">
                  <FiTrendingUp size={16} />
                </div>
              </div>

              <div className="stat-card gradient-orange">
                <div className="stat-icon">
                  <FiDatabase />
                </div>
                <div className="stat-details">
                  <h3>{stats.totalStorageGB?.toFixed(2) || 0} GB</h3>
                  <p>Storage Used</p>
                </div>
                <div className="stat-trend">
                  <FiBarChart2 size={16} />
                </div>
              </div>

              <div className="stat-card gradient-teal">
                <div className="stat-icon">
                  <FiPackage />
                </div>
                <div className="stat-details">
                  <h3>{stats.eventsThisMonth || 0}</h3>
                  <p>Events This Month</p>
                </div>
                <div className="stat-trend">
                  <FiTrendingUp size={16} />
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Tabs Navigation */}
        <div className="tabs-nav">
          <button 
            className={`tab-btn ${activeTab === 'overview' ? 'active' : ''}`}
            onClick={() => setActiveTab('overview')}
          >
            <FiBarChart2 /> Overview
          </button>
          <button 
            className={`tab-btn ${activeTab === 'events' ? 'active' : ''}`}
            onClick={() => {
              setActiveTab('events');
              loadEvents();
            }}
          >
            <FiCalendar /> Events Management
          </button>
          <button 
            className={`tab-btn ${activeTab === 'customers' ? 'active' : ''}`}
            onClick={() => {
              setActiveTab('customers');
              loadCustomers();
            }}
          >
            <FiUsers /> Customers
          </button>
        </div>

        {/* Overview Tab */}
        {activeTab === 'overview' && stats && (
          <div className="tab-content">
            <div className="overview-grid">
              <div className="card info-card">
                <h3><FiCalendar /> Event Distribution</h3>
                <div className="event-types">
                  {stats.eventTypeDistribution && Object.entries(stats.eventTypeDistribution).map(([type, count]) => (
                    <div key={type} className="type-row">
                      <span className="type-label">{type.replace('_', ' ')}</span>
                      <span className="type-count">{count}</span>
                    </div>
                  ))}
                </div>
              </div>

              <div className="card info-card">
                <h3><FiTrendingUp /> Recent Activity</h3>
                <div className="activity-list">
                  <div className="activity-item">
                    <span className="activity-icon green"><FiCalendar /></span>
                    <div className="activity-details">
                      <p>{stats.eventsLast7Days || 0} events in last 7 days</p>
                      <small>Event creation</small>
                    </div>
                  </div>
                  <div className="activity-item">
                    <span className="activity-icon blue"><FiImage /></span>
                    <div className="activity-details">
                      <p>{stats.imagesLast7Days || 0} photos uploaded</p>
                      <small>Photo uploads</small>
                    </div>
                  </div>
                  <div className="activity-item">
                    <span className="activity-icon purple"><FiUsers /></span>
                    <div className="activity-details">
                      <p>{stats.guestsLast7Days || 0} new guests</p>
                      <small>Guest registrations</small>
                    </div>
                  </div>
                </div>
              </div>

              <div className="card info-card">
                <h3><FiDatabase /> Storage Breakdown</h3>
                <div className="storage-info">
                  <div className="storage-bar">
                    <div 
                      className="storage-fill" 
                      style={{width: `${Math.min((stats.totalStorageGB / 100) * 100, 100)}%`}}
                    ></div>
                  </div>
                  <p className="storage-text">
                    {stats.totalStorageGB?.toFixed(2)} GB used
                  </p>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Events Management Tab */}
        {activeTab === 'events' && (
          <div className="tab-content">
            <div className="card management-card">
              <div className="card-header">
                <h2><FiCalendar /> Events Management</h2>
                <div className="search-bar">
                  <FiSearch />
                  <input 
                    type="text"
                    placeholder="Search events by name or code..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                  />
                  <button className="btn btn-primary" onClick={handleSearch}>
                    Search
                  </button>
                </div>
              </div>

              <div className="table-container">
                <table className="modern-table">
                  <thead>
                    <tr>
                      <th>Event Code</th>
                      <th>Event Name</th>
                      <th>Customer</th>
                      <th>Date</th>
                      <th>Type</th>
                      <th>Guests</th>
                      <th>Photos</th>
                      <th>Storage</th>
                      <th>Status</th>
                      <th>QR Valid</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {events.length === 0 ? (
                      <tr>
                        <td colSpan="11" className="no-data">
                          <FiCalendar size={48} />
                          <p>No events found</p>
                        </td>
                      </tr>
                    ) : (
                      events.map(event => (
                        <tr key={event.id}>
                          <td>
                            <code className="event-code">{event.eventCode}</code>
                          </td>
                          <td className="event-name">{event.name}</td>
                          <td>{event.customerName}</td>
                          <td>{formatDate(event.eventDate)}</td>
                          <td>
                            <span className="badge badge-type">
                              {event.eventType?.replace('_', ' ')}
                            </span>
                          </td>
                          <td className="centered">{event.guestCount || 0}</td>
                          <td className="centered">{event.totalUploads || 0}</td>
                          <td className="centered">{event.totalSizeMB?.toFixed(2) || 0} MB</td>
                          <td>
                            <span className={`badge ${event.isActive ? 'badge-success' : 'badge-error'}`}>
                              {event.isActive ? 'Active' : 'Inactive'}
                            </span>
                          </td>
                          <td>
                            <span className={`badge ${event.qrCodeValid ? 'badge-success' : 'badge-warning'}`}>
                              {event.qrCodeValid ? 'Valid' : 'Expired'}
                            </span>
                          </td>
                          <td className="actions">
                            <button 
                              className="btn-icon btn-icon-view"
                              title="View Details"
                              onClick={() => navigate(`/events/${event.id}`)}
                            >
                              <FiEye />
                            </button>
                            <button 
                              className="btn-icon btn-icon-delete"
                              title="Delete Event"
                              onClick={() => handleDeleteEvent(event.id)}
                            >
                              <FiTrash2 />
                            </button>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}

        {/* Customers Tab */}
        {activeTab === 'customers' && (
          <div className="tab-content">
            <div className="card management-card">
              <div className="card-header">
                <h2><FiUsers /> Customers Management</h2>
              </div>

              <div className="table-container">
                <table className="modern-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Name</th>
                      <th>Email</th>
                      <th>Phone</th>
                      <th>Events</th>
                      <th>Total Photos</th>
                      <th>Storage</th>
                      <th>Joined</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {customers.length === 0 ? (
                      <tr>
                        <td colSpan="9" className="no-data">
                          <FiUsers size={48} />
                          <p>No customers found</p>
                        </td>
                      </tr>
                    ) : (
                      customers.map(customer => (
                        <tr key={customer.id}>
                          <td>#{customer.id}</td>
                          <td className="customer-name">{customer.fullName}</td>
                          <td>{customer.email}</td>
                          <td>{customer.phoneNumber || 'N/A'}</td>
                          <td className="centered">{customer.eventCount || 0}</td>
                          <td className="centered">{customer.totalImages || 0}</td>
                          <td className="centered">{customer.totalStorageMB?.toFixed(2) || 0} MB</td>
                          <td>{formatDate(customer.createdAt)}</td>
                          <td className="actions">
                            <button 
                              className="btn-icon btn-icon-view"
                              title="View Details"
                              onClick={() => navigate(`/admin/customer/${customer.id}`)}
                            >
                              <FiEye />
                            </button>
                            <button 
                              className="btn-icon btn-icon-delete"
                              title="Delete Customer"
                              onClick={() => handleDeleteCustomer(customer.id)}
                            >
                              <FiTrash2 />
                            </button>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default AdminDashboard;
