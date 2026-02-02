import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { registerGuest } from '../services/api';
import './Guest.css';

function GuestRegistration() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const eventCode = searchParams.get('eventCode');
  
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phoneNumber: '',
    eventCode: eventCode || '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!eventCode) {
      setError('Invalid event link. Please scan the QR code again.');
    }
  }, [eventCode]);

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
      const response = await registerGuest(formData);
      if (response.data.success) {
        const guestId = response.data.data.id;
        navigate(`/guest/upload/${guestId}`);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="guest-container">
      <div className="guest-card">
        <div className="guest-header">
          <h1>Welcome!</h1>
          <p>Register to upload your photos from the event</p>
        </div>

        {error && <div className="error-message">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Your Name *</label>
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
              placeholder="Enter your full name"
            />
          </div>

          <div className="form-group">
            <label>Email (Optional)</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="your.email@example.com"
            />
          </div>

          <div className="form-group">
            <label>Phone Number (Optional)</label>
            <input
              type="tel"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleChange}
              placeholder="+1 (555) 000-0000"
            />
          </div>

          <button 
            type="submit" 
            className="btn btn-primary" 
            disabled={loading || !eventCode}
          >
            {loading ? 'Registering...' : 'Continue to Upload'}
          </button>
        </form>

        <div className="guest-info">
          <p>Your photos will be shared with the event organizer</p>
        </div>
      </div>
    </div>
  );
}

export default GuestRegistration;
