import api from './axios';

export const createEvent = (data) =>
  api.post('/api/events', data).then((r) => r.data);

export const myEvents = () =>
  api.get('/api/events/my').then((r) => r.data);

export const getEvent = (eventId) =>
  api.get(`/api/events/${eventId}`).then((r) => r.data);

export const getEventByCode = (eventCode) =>
  api.get(`/api/events/code/${eventCode}`).then((r) => r.data);

export const getImagesGrouped = (eventId) =>
  api.get(`/api/events/${eventId}/grouped`).then((r) => r.data);

export const getQrCodeUrl = (eventCode) =>
  `${process.env.REACT_APP_API_URL || ''}/api/events/qr/${eventCode}`;
