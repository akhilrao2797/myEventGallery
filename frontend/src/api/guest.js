import api from './axios';

export const guestRegister = (data) =>
  api.post('/api/guest/register', data).then((r) => r.data);

export const guestLogin = (data) =>
  api.post('/api/guest/login', data).then((r) => r.data);

export const eventInfo = (eventCode) =>
  api.get(`/api/guest/event-info/${eventCode}`).then((r) => r.data);

export const guestDashboard = () =>
  api.get('/api/guest/dashboard').then((r) => r.data);

export const guestUpload = (eventId, formData) =>
  api.post(`/api/guest/${eventId}/upload`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }).then((r) => r.data);

export const guestDeleteImage = (imageId) =>
  api.delete(`/api/guest/images/${imageId}`).then((r) => r.data);
