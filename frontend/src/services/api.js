import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add auth token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Auth APIs
export const register = (data) => api.post('/auth/register', data);
export const login = (data) => api.post('/auth/login', data);

// Package APIs
export const getPackages = () => api.get('/packages/list');

// Event APIs
export const createEvent = (data) => api.post('/events', data);
export const getMyEvents = () => api.get('/events');
export const getEventById = (eventId) => api.get(`/events/${eventId}`);
export const getEventQRCode = (eventCode) => `${API_BASE_URL}/events/qr/${eventCode}`;

// Guest APIs
export const registerGuest = (data) => api.post('/guest/register', data);
export const uploadImages = (guestId, files) => {
  const formData = new FormData();
  files.forEach((file) => {
    formData.append('files', file);
  });
  
  return axios.post(`${API_BASE_URL}/guest/${guestId}/upload`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

// Image APIs
export const getEventImages = (eventId) => api.get(`/images/event/${eventId}`);
export const getEventImagesPaginated = (eventId, page = 0, size = 20) => 
  api.get(`/images/event/${eventId}/paginated?page=${page}&size=${size}`);
export const deleteImage = (imageId) => api.delete(`/images/${imageId}`);

export default api;
