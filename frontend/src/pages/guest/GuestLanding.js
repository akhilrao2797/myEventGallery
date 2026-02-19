import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { FiCamera } from 'react-icons/fi';
import './GuestLanding.css';

export default function GuestLanding() {
  const [eventCode, setEventCode] = useState('');
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();
    const code = eventCode.trim();
    if (!code) return;
    navigate(`/guest/register?eventCode=${encodeURIComponent(code)}`);
  };

  return (
    <div className="guest-landing">
      <div className="guest-landing-card card">
        <FiCamera className="guest-landing-icon" />
        <h1>Guest Photo Upload</h1>
        <p>Enter the event code from the QR code or your host</p>
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            value={eventCode}
            onChange={(e) => setEventCode(e.target.value)}
            placeholder="Event code"
            className="guest-landing-input"
            required
          />
          <button type="submit" className="btn btn-primary guest-landing-btn">
            Continue
          </button>
        </form>
        <p className="guest-landing-footer">
          Already registered? <Link to="/guest/login">Log in</Link>
        </p>
      </div>
    </div>
  );
}
