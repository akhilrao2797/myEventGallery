import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { guestLogin } from '../../api/guest';
import { useAuth } from '../../context/AuthContext';
import './GuestAuth.css';

export default function GuestLogin() {
  const [eventCode, setEventCode] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const { loginGuest } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const res = await guestLogin({ eventCode: eventCode.trim(), email, password: password || undefined });
      if (res.success && res.data?.token) {
        loginGuest(res);
        navigate(`/guest-app/event/${res.data.eventId}/upload`);
      } else {
        setError(res.message || 'Login failed');
      }
    } catch (err) {
      setError(err.message || 'Login failed');
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card card">
        <h1 className="auth-title">Guest Login</h1>
        <p className="auth-subtitle">Sign in to upload photos</p>
        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <label>Event code</label>
            <input type="text" value={eventCode} onChange={(e) => setEventCode(e.target.value)} placeholder="Event code" required />
          </div>
          <div className="input-group">
            <label>Email</label>
            <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="you@example.com" required />
          </div>
          <div className="input-group">
            <label>Password (if you set one)</label>
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Optional" />
          </div>
          {error && <p className="error-msg">{error}</p>}
          <button type="submit" className="btn btn-primary auth-submit">Sign in</button>
        </form>
        <p className="auth-footer">
          New guest? <Link to="/guest">Register with event code</Link>
        </p>
      </div>
    </div>
  );
}
