import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createEvent } from '../api/events';
import { FiCalendar, FiMapPin, FiType } from 'react-icons/fi';
import './CreateEvent.css';

const EVENT_TYPES = [
  'MARRIAGE', 'BIRTHDAY', 'ENGAGEMENT', 'FAREWELL', 'BABY_SHOWER',
  'ANNIVERSARY', 'CORPORATE', 'OTHER'
];

export default function CreateEvent() {
  const navigate = useNavigate();
  const [step, setStep] = useState(1);
  const [form, setForm] = useState({
    name: '',
    eventType: 'MARRIAGE',
    description: '',
    eventDate: '',
    eventStartTime: '',
    eventEndTime: '',
    venue: '',
    expectedGuests: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const update = (field, value) => setForm((f) => ({ ...f, [field]: value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (step < 3) {
      setStep(step + 1);
      return;
    }
    setError('');
    setLoading(true);
    try {
      const payload = {
        name: form.name,
        eventType: form.eventType,
        description: form.description || null,
        eventDate: form.eventDate,
        eventStartTime: form.eventStartTime || null,
        eventEndTime: form.eventEndTime || null,
        venue: form.venue || null,
        expectedGuests: form.expectedGuests ? parseInt(form.expectedGuests, 10) : null,
      };
      const res = await createEvent(payload);
      if (res.success && res.data?.id) {
        navigate(`/events/${res.data.id}`);
      } else {
        setError(res.message || 'Failed to create event');
      }
    } catch (err) {
      setError(err.message || 'Failed to create event');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page">
      <h1 className="page-title">Create Event</h1>
      <div className="create-event-steps">
        <span className={step >= 1 ? 'active' : ''}>1. Basics</span>
        <span className={step >= 2 ? 'active' : ''}>2. Date & Venue</span>
        <span className={step >= 3 ? 'active' : ''}>3. Review</span>
      </div>
      <form onSubmit={handleSubmit} className="card create-event-form">
        {step === 1 && (
          <>
            <div className="input-group">
              <label>Event name</label>
              <input
                type="text"
                value={form.name}
                onChange={(e) => update('name', e.target.value)}
                placeholder="e.g. Sarah & John Wedding"
                required
              />
            </div>
            <div className="input-group">
              <label>Event type</label>
              <select
                value={form.eventType}
                onChange={(e) => update('eventType', e.target.value)}
              >
                {EVENT_TYPES.map((t) => (
                  <option key={t} value={t}>{t.replace('_', ' ')}</option>
                ))}
              </select>
            </div>
            <div className="input-group">
              <label>Description (optional)</label>
              <textarea
                value={form.description}
                onChange={(e) => update('description', e.target.value)}
                placeholder="Brief description"
                rows={3}
              />
            </div>
          </>
        )}
        {step === 2 && (
          <>
            <div className="input-group">
              <label>Event date</label>
              <input
                type="date"
                value={form.eventDate}
                onChange={(e) => update('eventDate', e.target.value)}
                required
              />
            </div>
            <div className="input-group">
              <label>Start time (optional)</label>
              <input
                type="time"
                value={form.eventStartTime}
                onChange={(e) => update('eventStartTime', e.target.value)}
              />
            </div>
            <div className="input-group">
              <label>End time (optional)</label>
              <input
                type="time"
                value={form.eventEndTime}
                onChange={(e) => update('eventEndTime', e.target.value)}
              />
            </div>
            <div className="input-group">
              <label>Venue</label>
              <input
                type="text"
                value={form.venue}
                onChange={(e) => update('venue', e.target.value)}
                placeholder="Venue name or address"
              />
            </div>
            <div className="input-group">
              <label>Expected guests (optional)</label>
              <input
                type="number"
                min="1"
                value={form.expectedGuests}
                onChange={(e) => update('expectedGuests', e.target.value)}
                placeholder="e.g. 100"
              />
            </div>
          </>
        )}
        {step === 3 && (
          <div className="create-event-review">
            <p><strong>Name:</strong> {form.name}</p>
            <p><strong>Type:</strong> {form.eventType}</p>
            <p><strong>Date:</strong> {form.eventDate}</p>
            <p><strong>Venue:</strong> {form.venue || '—'}</p>
            <p className="create-event-note">After creating, you can share the event QR code with guests for photo uploads.</p>
          </div>
        )}
        {error && <p className="error-msg">{error}</p>}
        <div className="create-event-actions">
          {step > 1 && (
            <button type="button" className="btn btn-secondary" onClick={() => setStep(step - 1)}>
              Back
            </button>
          )}
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {step < 3 ? 'Next' : 'Create Event'}
          </button>
        </div>
      </form>
    </div>
  );
}
