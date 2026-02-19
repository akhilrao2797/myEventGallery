import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams, Link } from 'react-router-dom';
import { guestRegister } from '../../api/guest';
import { eventInfo } from '../../api/guest';
import { useAuth } from '../../context/AuthContext';
import './GuestAuth.css';

export default function GuestRegister() {
  const [searchParams] = useSearchParams();
  const eventCodeFromUrl = searchParams.get('eventCode') || '';
  const [eventCode, setEventCode] = useState(eventCodeFromUrl);
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [eventInfoData, setEventInfoData] = useState(null);
  const [error, setError] = useState('');
  const { loginGuest } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (eventCode.trim()) {
      eventInfo(eventCode).then((r) => setEventInfoData(r.data)).catch(() => setEventInfoData(null));
    } else {
      setEventInfoData(null);
    }
  }, [eventCode]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const res = await guestRegister({
        eventCode: eventCode.trim(),
        name,
        email: email || null,
        password: password || null,
        phoneNumber: phoneNumber || null,
      });
      if (res.success && res.data?.token) {
        loginGuest(res);
        navigate(`/guest-app/event/${res.data.eventId}/upload`);
      } else {
        setError(res.message || 'Registration failed');
      }
    } catch (err) {
      setError(err.message || 'Registration failed');
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card card">
        <h1 className="auth-title">Guest Registration</h1>
        {eventInfoData && <p className="auth-subtitle">{eventInfoData.name} · {eventInfoData.eventDate}</p>}
        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <label>Event code</label>
            <input
              type="text"
              value={eventCode}
              onChange={(e) => setEventCode(e.target.value)}
              placeholder="From QR or host"
              required
            />
          </div>
          <div className="input-group">
            <label>Your name</label>
            <input type="text" value={name} onChange={(e) => setName(e.target.value)} placeholder="Name" required />
          </div>
          <div className="input-group">
            <label>Email (optional)</label>
            <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="you@example.com" />
          </div>
          <div className="input-group">
            <label>Password (optional, for later login)</label>
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Min 6 characters" minLength={6} />
          </div>
          <div className="input-group">
            <label>Phone (optional)</label>
            <input type="tel" value={phoneNumber} onChange={(e) => setPhoneNumber(e.target.value)} placeholder="+91 ..." />
          </div>
          {error && <p className="error-msg">{error}</p>}
          <button type="submit" className="btn btn-primary auth-submit">Register</button>
        </form>
        <p className="auth-footer">
          Already registered? <Link to="/guest/login">Log in</Link>
        </p>
      </div>
    </div>
  );
}
