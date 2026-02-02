import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { createEvent, getPackages } from '../services/api';
import { FiArrowLeft } from 'react-icons/fi';
import './CreateEvent.css';

function CreateEvent() {
  const navigate = useNavigate();
  const [packages, setPackages] = useState([]);
  const [formData, setFormData] = useState({
    name: '',
    eventType: 'MARRIAGE',
    description: '',
    eventDate: '',
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
      if (response.data.success) {
        setPackages(response.data.data);
      }
    } catch (err) {
      console.error('Failed to load packages', err);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
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
    'MARRIAGE', 'RECEPTION', 'BIRTHDAY', 'ANNIVERSARY', 
    'CORPORATE', 'GRADUATION', 'BABY_SHOWER', 'ENGAGEMENT', 'OTHER'
  ];

  return (
    <div className="create-event-page">
      <div className="container">
        <button className="btn-back" onClick={() => navigate('/dashboard')}>
          <FiArrowLeft /> Back to Dashboard
        </button>

        <div className="card">
          <h1>Create New Event</h1>
          <p className="subtitle">Set up your event and get a unique QR code for photo sharing</p>

          {error && <div className="error-message">{error}</div>}

          <form onSubmit={handleSubmit}>
            <div className="form-row">
              <div className="form-group">
                <label>Event Name *</label>
                <input
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  required
                  placeholder="e.g., Sarah & John's Wedding"
                />
              </div>

              <div className="form-group">
                <label>Event Type *</label>
                <select name="eventType" value={formData.eventType} onChange={handleChange} required>
                  {eventTypes.map((type) => (
                    <option key={type} value={type}>
                      {type.replace('_', ' ')}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Event Date *</label>
                <input
                  type="date"
                  name="eventDate"
                  value={formData.eventDate}
                  onChange={handleChange}
                  required
                  min={new Date().toISOString().split('T')[0]}
                />
              </div>

              <div className="form-group">
                <label>Expected Guests</label>
                <input
                  type="number"
                  name="expectedGuests"
                  value={formData.expectedGuests}
                  onChange={handleChange}
                  placeholder="Number of guests"
                  min="1"
                />
              </div>
            </div>

            <div className="form-group">
              <label>Venue</label>
              <input
                type="text"
                name="venue"
                value={formData.venue}
                onChange={handleChange}
                placeholder="Event location"
              />
            </div>

            <div className="form-group">
              <label>Description</label>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleChange}
                rows="3"
                placeholder="Brief description of your event"
              />
            </div>

            <div className="form-group">
              <label>Select Package *</label>
              <div className="packages-grid">
                {packages.map((pkg) => (
                  <div
                    key={pkg.id}
                    className={`package-card ${formData.packageType === pkg.packageType ? 'selected' : ''}`}
                    onClick={() => setFormData({ ...formData, packageType: pkg.packageType })}
                  >
                    <input
                      type="radio"
                      name="packageType"
                      value={pkg.packageType}
                      checked={formData.packageType === pkg.packageType}
                      onChange={handleChange}
                    />
                    <h3>{pkg.name}</h3>
                    <p className="package-price">${pkg.basePrice}</p>
                    <ul className="package-features">
                      <li>Up to {pkg.maxGuests} guests</li>
                      <li>Up to {pkg.maxImages} images</li>
                      <li>{pkg.storageDays} days storage</li>
                      <li>{pkg.storageGB} GB storage</li>
                    </ul>
                  </div>
                ))}
              </div>
            </div>

            <div className="form-actions">
              <button type="button" className="btn btn-secondary" onClick={() => navigate('/dashboard')}>
                Cancel
              </button>
              <button type="submit" className="btn btn-primary" disabled={loading}>
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
