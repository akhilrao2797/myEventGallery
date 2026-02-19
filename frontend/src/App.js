import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import Layout from './components/Layout';
import CustomerLogin from './pages/CustomerLogin';
import CustomerRegister from './pages/CustomerRegister';
import Dashboard from './pages/Dashboard';
import CreateEvent from './pages/CreateEvent';
import EventDetail from './pages/EventDetail';
import GuestLanding from './pages/guest/GuestLanding';
import GuestRegister from './pages/guest/GuestRegister';
import GuestLogin from './pages/guest/GuestLogin';
import GuestDashboard from './pages/guest/GuestDashboard';
import GuestUpload from './pages/guest/GuestUpload';
import SharedView from './pages/SharedView';
import './App.css';

function PrivateCustomer({ children }) {
  const { isCustomer, loading } = useAuth();
  if (loading) return <div className="app-loading">Loading...</div>;
  if (!isCustomer) return <Navigate to="/login" replace />;
  return children;
}

function PrivateGuest({ children }) {
  const { isGuest, loading } = useAuth();
  if (loading) return <div className="app-loading">Loading...</div>;
  if (!isGuest) return <Navigate to="/guest" replace />;
  return children;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<CustomerLogin />} />
      <Route path="/register" element={<CustomerRegister />} />
      <Route path="/guest" element={<GuestLanding />} />
      <Route path="/guest/register" element={<GuestRegister />} />
      <Route path="/guest/login" element={<GuestLogin />} />
      <Route path="/shared/:shareCode" element={<SharedView />} />

      <Route path="/" element={<Layout />}>
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route
          path="dashboard"
          element={
            <PrivateCustomer>
              <Dashboard />
            </PrivateCustomer>
          }
        />
        <Route
          path="events/new"
          element={
            <PrivateCustomer>
              <CreateEvent />
            </PrivateCustomer>
          }
        />
        <Route
          path="events/:eventId"
          element={
            <PrivateCustomer>
              <EventDetail />
            </PrivateCustomer>
          }
        />
      </Route>

      <Route path="/guest-app" element={<Layout />}>
        <Route
          path="dashboard"
          element={
            <PrivateGuest>
              <GuestDashboard />
            </PrivateGuest>
          }
        />
        <Route
          path="event/:eventId/upload"
          element={
            <PrivateGuest>
              <GuestUpload />
            </PrivateGuest>
          }
        />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
