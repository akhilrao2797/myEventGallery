import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { adminRegister } from '../services/api';
import { FiUser, FiMail, FiLock, FiShield, FiUserPlus, FiKey } from 'react-icons/fi';
import './Auth.css';

function AdminRegister() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    fullName: '',
    role: 'ADMIN',
    superAdminKey: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await adminRegister(formData);
      
      if (response.data.success) {
        // Store admin token
        localStorage.setItem('adminToken', response.data.data.token);
        localStorage.setItem('adminId', response.data.data.adminId);
        localStorage.setItem('adminName', response.data.data.name);
        localStorage.setItem('adminEmail', response.data.data.email);
        
        // Redirect to admin dashboard
        navigate('/admin/dashboard');
      } else {
        setError(response.data.message || 'Registration failed');
      }
    } catch (err) {
      console.error('Registration error:', err);
      setError(err.response?.data?.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-header">
          <div className="auth-icon">
            <FiShield size={40} />
          </div>
          <h1>Admin Registration</h1>
          <p>Create a new admin account</p>
        </div>

        {error && (
          <div className="error-message">
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label>
              <FiUser />
              <span>Username</span>
            </label>
            <input
              type="text"
              name="username"
              value={formData.username}
              onChange={handleChange}
              placeholder="Enter username"
              required
            />
          </div>

          <div className="form-group">
            <label>
              <FiUserPlus />
              <span>Full Name</span>
            </label>
            <input
              type="text"
              name="fullName"
              value={formData.fullName}
              onChange={handleChange}
              placeholder="Enter full name"
              required
            />
          </div>

          <div className="form-group">
            <label>
              <FiMail />
              <span>Email</span>
            </label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="Enter email"
              required
            />
          </div>

          <div className="form-group">
            <label>
              <FiLock />
              <span>Password</span>
            </label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="Enter password"
              required
              minLength="6"
            />
          </div>

          <div className="form-group">
            <label>
              <FiShield />
              <span>Role</span>
            </label>
            <select
              name="role"
              value={formData.role}
              onChange={handleChange}
              required
            >
              <option value="ADMIN">Admin</option>
              <option value="SUPER_ADMIN">Super Admin</option>
              <option value="SUPPORT">Support</option>
            </select>
          </div>

          <div className="form-group">
            <label>
              <FiKey />
              <span>Super Admin Key</span>
            </label>
            <input
              type="password"
              name="superAdminKey"
              value={formData.superAdminKey}
              onChange={handleChange}
              placeholder="Enter super admin key"
              required
            />
            <small className="form-hint">
              Required for security. Only authorized personnel should have this key.
            </small>
          </div>

          <button 
            type="submit" 
            className="btn-primary"
            disabled={loading}
          >
            {loading ? 'Registering...' : 'Register Admin'}
          </button>
        </form>

        <div className="auth-links">
          <p>
            Already have an admin account?{' '}
            <Link to="/admin/login">Login here</Link>
          </p>
          <div className="auth-divider"></div>
          <div className="other-logins">
            <Link to="/login" className="link-secondary">
              Customer Login
            </Link>
            <Link to="/guest/login" className="link-secondary">
              Guest Login
            </Link>
          </div>
        </div>

        <div className="security-notice">
          <FiShield size={16} />
          <span>Secure admin registration with super admin authorization</span>
        </div>
      </div>
    </div>
  );
}

export default AdminRegister;
