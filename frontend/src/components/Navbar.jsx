import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Navbar.css';

const Navbar = () => {
  const { user, isAdmin, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar glass-card">
      <div className="navbar-container">
        <div className="navbar-left">
          <NavLink to="/dashboard" className="navbar-brand">
            <span className="gradient-text">SecureVault</span>
          </NavLink>
        </div>
        <div className="navbar-center">
          <NavLink to="/dashboard" className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>Dashboard</NavLink>
          <NavLink to="/transfer" className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>Transfer</NavLink>
          <NavLink to="/transactions" className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>History</NavLink>
          <NavLink to="/fixed-deposits" className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>Savings</NavLink>
          <NavLink to="/recurring-payments" className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>Autopay</NavLink>
          {isAdmin && (
            <NavLink to="/admin" className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>Admin Panel</NavLink>
          )}
        </div>
        <div className="navbar-right">
          {user && (
            <div className="user-info">
              <span className="greeting">Hello, {user.username}</span>
              <span className={`status-badge ${isAdmin ? 'admin-badge' : 'customer-badge'}`}>
                {user.role}
              </span>
              <button className="btn-secondary logout-btn" onClick={handleLogout}>Logout</button>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
