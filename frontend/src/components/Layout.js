import React from 'react';
import { Outlet, Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { FiLogOut, FiGrid, FiPlusCircle, FiCamera } from 'react-icons/fi';
import './Layout.css';

export default function Layout() {
  const { user, guest, isCustomer, isGuest, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    if (isCustomer) navigate('/login');
    else navigate('/guest');
  };

  return (
    <div className="layout">
      <header className="layout-header">
        <Link to={isCustomer ? '/dashboard' : '/guest-app/dashboard'} className="layout-brand">
          <FiCamera className="layout-brand-icon" />
          Event Gallery
        </Link>
        <nav className="layout-nav">
          {isCustomer && (
            <>
              <Link to="/dashboard" className="layout-nav-link">Dashboard</Link>
              <Link to="/events/new" className="layout-nav-link">New Event</Link>
            </>
          )}
          {isGuest && (
            <Link to="/guest-app/dashboard" className="layout-nav-link">My Events</Link>
          )}
          <span className="layout-user">
            {user?.name || guest?.name || user?.email || guest?.email}
          </span>
          <button type="button" className="btn btn-ghost" onClick={handleLogout} aria-label="Logout">
            <FiLogOut /> Logout
          </button>
        </nav>
      </header>
      <main className="layout-main">
        <Outlet />
      </main>
    </div>
  );
}
