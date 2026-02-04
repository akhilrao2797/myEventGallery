import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    // Smart token selection based on endpoint
    const guestToken = localStorage.getItem('guestToken');
    const customerToken = localStorage.getItem('token');
    const adminToken = localStorage.getItem('adminToken');
    
    let token;
    if (config.url?.includes('/admin/')) {
      token = adminToken; // Admin endpoints
    } else if (config.url?.includes('/guest/')) {
      token = guestToken; // Guest endpoints
    } else {
      token = customerToken; // Customer endpoints
    }
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.clear();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Utility function to construct image URL
export const getImageUrl = (s3Url) => {
  if (!s3Url) return '';
  if (s3Url.startsWith('http')) return s3Url; // S3 URL
  return `http://localhost:8080/api/files/${s3Url}`; // Local URL
};

// Customer Auth APIs
export const customerRegister = (data) => api.post('/auth/register', data);
export const customerLogin = (data) => api.post('/auth/login', data);

// Guest Auth APIs
export const guestLogin = (data) => api.post('/guest/login', data);
export const getGuestDashboard = () => api.get('/guest/dashboard');
export const deleteGuestImage = (imageId) => api.delete(`/guest/image/${imageId}`);

// Admin Auth APIs
export const adminLogin = (data) => api.post('/admin/login', data);
export const getAdminDashboard = () => api.get('/admin/dashboard/stats');
export const getAllEvents = (params) => api.get('/admin/events', { params });
export const searchEvents = (query) => api.get('/admin/events/search', { params: { query } });
export const deleteEvent = (eventId) => api.delete(`/admin/events/${eventId}`);
export const updateEvent = (eventId, updates) => api.put(`/admin/events/${eventId}`, updates);
export const getAllCustomers = () => api.get('/admin/customers');
export const getCustomerDetails = (customerId) => api.get(`/admin/customers/${customerId}`);
export const deleteCustomer = (customerId) => api.delete(`/admin/customers/${customerId}`);

// Event APIs
export const createEvent = (data) => api.post('/events', data);
export const getMyEvents = () => api.get('/events');
export const getEventDetails = (eventId) => api.get(`/events/${eventId}`);
export const getEventByCode = (eventCode) => api.get(`/events/code/${eventCode}`);
export const getQRCode = (eventCode) => api.get(`/events/qr/${eventCode}`, { responseType: 'blob' });

// Image APIs
export const getEventImages = (eventId) => api.get(`/images/event/${eventId}`);
export const getEventImagesGrouped = (eventId) => api.get(`/images/event/${eventId}/grouped`);
export const deleteImage = (imageId) => api.delete(`/images/${imageId}`);
export const downloadImagesAsZip = (imageIds) => 
  api.post('/images/download-zip', { imageIds }, { responseType: 'blob' });

// Guest APIs
export const registerGuest = (data) => api.post('/guest/register', data);
export const uploadImages = (guestId, formData) => 
  api.post(`/guest/${guestId}/upload`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });

// Shared Folder APIs
export const createSharedFolder = (data) => api.post('/shared-folders', data);
export const getMySharedFolders = () => api.get('/shared-folders');
export const getSharedFolder = (shareCode, password) => 
  api.get(`/shared-folders/public/${shareCode}`, { params: { password } });
export const updateFolderImages = (folderId, imageIds) => 
  api.put(`/shared-folders/${folderId}/images`, { imageIds });
export const deleteSharedFolder = (folderId) => api.delete(`/shared-folders/${folderId}`);

// Package APIs
export const getPackages = () => api.get('/packages');

export default api;
