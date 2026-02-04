import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';

// Import pages
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import CreateEvent from './pages/CreateEvent';
import EventDetails from './pages/EventDetails';
import GuestRegistration from './pages/GuestRegistration';
import GuestUpload from './pages/GuestUpload';
import GuestLogin from './pages/GuestLogin';
import GuestDashboard from './pages/GuestDashboard';
import AdminLogin from './pages/AdminLogin';
import AdminDashboard from './pages/AdminDashboard';

// Auth helpers
const isAuthenticated = () => {
  return localStorage.getItem('token') !== null;
};

const isGuestAuthenticated = () => {
  return localStorage.getItem('guestToken') !== null;
};

const isAdminAuthenticated = () => {
  return localStorage.getItem('adminToken') !== null;
};

const ProtectedRoute = ({ children }) => {
  return isAuthenticated() ? children : <Navigate to="/login" />;
};

const GuestProtectedRoute = ({ children }) => {
  return isGuestAuthenticated() ? children : <Navigate to="/guest/login" />;
};

const AdminProtectedRoute = ({ children }) => {
  return isAdminAuthenticated() ? children : <Navigate to="/admin/login" />;
};

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<Navigate to="/login" />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          
          {/* Customer Protected Routes */}
          <Route 
            path="/dashboard" 
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/create-event" 
            element={
              <ProtectedRoute>
                <CreateEvent />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/events/:eventId" 
            element={
              <ProtectedRoute>
                <EventDetails />
              </ProtectedRoute>
            } 
          />
          
          {/* Guest Routes */}
          <Route path="/guest/login" element={<GuestLogin />} />
          <Route path="/guest/register" element={<GuestRegistration />} />
          <Route path="/guest/upload/:guestId" element={<GuestUpload />} />
          <Route 
            path="/guest/dashboard" 
            element={
              <GuestProtectedRoute>
                <GuestDashboard />
              </GuestProtectedRoute>
            } 
          />
          
          {/* Admin Routes */}
          <Route path="/admin/login" element={<AdminLogin />} />
          <Route 
            path="/admin/dashboard" 
            element={
              <AdminProtectedRoute>
                <AdminDashboard />
              </AdminProtectedRoute>
            } 
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
