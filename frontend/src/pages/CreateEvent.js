import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { createEvent, getPackages } from '../services/api';
import { FiArrowLeft, FiCalendar, FiMapPin, FiFileText, FiPackage, FiUsers } from 'react-icons/fi';
import './CreateEvent.css';

function CreateEvent() {
  const navigate = useNavigate();
  const [packages, setPackages] = useState([]);
  const [formData, setFormData] = useState({
    name: '',
    eventType: 'MARRIAGE',
    description: '',
    eventDate: '',
    eventStartTime: '',
    eventEndTime: '',
    venue: '',
    expectedGuests: '',
    packageType: 'BASIC',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadPackages();
  }, []);

  const loadPackages = async () => {
    try {
      const response = await getPackages();
      console.log('Packages response:', response.data);
      if (response.data.success) {
        setPackages(response.data.data || []);
      } else {
        console.error('Package loading failed:', response.data.message);
      }
    } catch (err) {
      console.error('Failed to load packages:', err);
      console.error('Error details:', err.response?.data);
      // Set empty array to avoid errors
      setPackages([]);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handlePackageSelect = (packageType) => {
    setFormData({
      ...formData,
      packageType: packageType,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await createEvent(formData);
      if (response.data.success) {
        navigate('/dashboard');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create event. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const eventTypes = [
    { value: 'MARRIAGE', label: 'Marriage' },
    { value: 'RECEPTION', label: 'Reception' },
    { value: 'BIRTHDAY', label: 'Birthday Party' },
    { value: 'ANNIVERSARY', label: 'Anniversary' },
    { value: 'CORPORATE', label: 'Corporate Event' },
    { value: 'GRADUATION', label: 'Graduation' },
    { value: 'BABY_SHOWER', label: 'Baby Shower' },
    { value: 'ENGAGEMENT', label: 'Engagement' },
    { value: 'OTHER', label: 'Other' },
  ];

  return (
    <div className="create-event-page">
      <div className="container">
        <button className="btn-back" onClick={() => navigate('/dashboard')}>
          <FiArrowLeft /> Back to Dashboard
        </button>

        <div className="card">
          <h1>
            <FiCalendar /> Create New Event
          </h1>
          <p className="subtitle">Set up your event and get a unique QR code for photo sharing</p>

          {error && <div className="error-message">{error}</div>}

          <form onSubmit={handleSubmit}>
            {/* Event Details */}
            <h3 className="section-title">
              <FiFileText /> Event Details
            </h3>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="name">Event Name *</label>
                <input
                  type="text"
                  id="name"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  required
                  placeholder="e.g., Sarah & John's Wedding"
                />
              </div>

              <div className="form-group">
                <label htmlFor="eventType">Event Type *</label>
                <select 
                  id="eventType"
                  name="eventType" 
                  value={formData.eventType} 
                  onChange={handleChange} 
                  required
                >
                  {eventTypes.map((type) => (
                    <option key={type.value} value={type.value}>
                      {type.label}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="description">Description (Optional)</label>
              <textarea
                id="description"
                name="description"
                value={formData.description}
                onChange={handleChange}
                placeholder="Add a brief description of your event..."
                rows="3"
              />
            </div>

            {/* Date & Time */}
            <h3 className="section-title">
              <FiCalendar /> Date & Time
            </h3>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="eventDate">Event Date *</label>
                <input
                  type="date"
                  id="eventDate"
                  name="eventDate"
                  value={formData.eventDate}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="eventStartTime">Start Time</label>
                <input
                  type="time"
                  id="eventStartTime"
                  name="eventStartTime"
                  value={formData.eventStartTime}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label htmlFor="eventEndTime">End Time</label>
                <input
                  type="time"
                  id="eventEndTime"
                  name="eventEndTime"
                  value={formData.eventEndTime}
                  onChange={handleChange}
                />
              </div>
            </div>

            {/* Venue & Guests */}
            <h3 className="section-title">
              <FiMapPin /> Venue & Guests
            </h3>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="venue">Venue</label>
                <input
                  type="text"
                  id="venue"
                  name="venue"
                  value={formData.venue}
                  onChange={handleChange}
                  placeholder="e.g., Grand Hotel Ballroom"
                />
              </div>

              <div className="form-group">
                <label htmlFor="expectedGuests">
                  <FiUsers /> Expected Guests *
                </label>
                <input
                  type="number"
                  id="expectedGuests"
                  name="expectedGuests"
                  value={formData.expectedGuests}
                  onChange={handleChange}
                  required
                  min="1"
                  placeholder="Number of expected guests"
                />
                <span className="form-helper-text">
                  Approximate number of guests attending
                </span>
              </div>
            </div>

            {/* Package Selection */}
            <h3 className="section-title">
              <FiPackage /> Choose Your Package
            </h3>

            <div className="packages-grid">
              {packages.length === 0 ? (
                <div style={{padding: '20px', textAlign: 'center', color: '#718096'}}>
                  Loading packages...
                </div>
              ) : (
                packages.map((pkg) => (
                  <div
                    key={pkg.packageType}
                    className={`package-card ${formData.packageType === pkg.packageType ? 'selected' : ''}`}
                    onClick={() => handlePackageSelect(pkg.packageType)}
                  >
                    <div className="package-name">{pkg.name || pkg.packageType}</div>
                    <div className="package-price">
                      ${pkg.basePrice}
                      <span>/event</span>
                    </div>
                    <div className="package-description">
                      {pkg.description || 'Perfect for your event needs'}
                    </div>
                    <ul className="package-features">
                      <li>{pkg.storageGB} GB Storage</li>
                      <li>Up to {pkg.maxGuests} Guests</li>
                      <li>Up to {pkg.maxImages} Images</li>
                      <li>{pkg.storageDays} Days Access</li>
                      <li>QR Code Generation</li>
                      {pkg.packageType === 'PREMIUM' && (
                        <>
                          <li>Priority Support</li>
                          <li>Advanced Analytics</li>
                        </>
                      )}
                    </ul>
                  </div>
                ))
              )}
            </div>

            {/* Actions */}
            <div className="form-actions">
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => navigate('/dashboard')}
              >
                Cancel
              </button>
              <button 
                type="submit" 
                className="btn btn-primary"
                disabled={loading}
              >
                {loading ? 'Creating Event...' : 'Create Event'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default CreateEvent;
