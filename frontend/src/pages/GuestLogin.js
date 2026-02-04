import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { guestLogin } from '../../services/api';
import { FiUser, FiLock, FiHash } from 'react-icons/fi';
import '../Auth.css';

function GuestLogin() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    eventCode: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await guestLogin(formData);
      if (response.data.success) {
        const { token, id, name, email } = response.data.data;
        localStorage.setItem('guestToken', token);
        localStorage.setItem('guestId', id);
        localStorage.setItem('guestName', name);
        localStorage.setItem('guestEmail', email);
        navigate('/guest/dashboard');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1>Guest Login</h1>
        <p className="auth-subtitle">Access your uploaded photos</p>

        {error && <div className="error-message">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Event Code</label>
            <div className="input-with-icon">
              <FiHash className="input-icon" />
              <input
                type="text"
                value={formData.eventCode}
                onChange={(e) => setFormData({ ...formData, eventCode: e.target.value })}
                required
                placeholder="Enter event code"
              />
            </div>
          </div>

          <div className="form-group">
            <label>Email</label>
            <div className="input-with-icon">
              <FiUser className="input-icon" />
              <input
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                required
                placeholder="your@email.com"
              />
            </div>
          </div>

          <div className="form-group">
            <label>Password</label>
            <div className="input-with-icon">
              <FiLock className="input-icon" />
              <input
                type="password"
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                required
                placeholder="Enter your password"
              />
            </div>
          </div>

          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>

        <div className="auth-link">
          <a href="/login">Customer Login</a> | <a href="/admin/login">Admin Login</a>
        </div>
      </div>
    </div>
  );
}

export default GuestLogin;
