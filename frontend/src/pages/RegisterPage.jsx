import React, { useState } from 'react';
import { Link, Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './AuthPages.css';

const RegisterPage = () => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    aadharNumber: '',
    initialBalance: '',
    password: '',
    confirmPassword: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  
  const { register, isAuthenticated } = useAuth();

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    setIsLoading(true);

    try {
      const response = await register(
        formData.username, 
        formData.email, 
        formData.password, 
        formData.aadharNumber, 
        formData.initialBalance || 0
      );
      setSuccess(`Registration successful! Your Account Number is ${response.accountNumber}. Please wait for admin approval.`);
      setFormData({ username: '', email: '', aadharNumber: '', initialBalance: '', password: '', confirmPassword: '' });
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to register. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-background-orbs">
        <div className="orb orb-1"></div>
        <div className="orb orb-2"></div>
      </div>
      
      <div className="auth-card glass-card">
        <div className="auth-header">
          <h1 className="gradient-text">SecureVault</h1>
          <h2>Create Account</h2>
          <p>Join us today for a better banking experience</p>
        </div>

        {error && <div className="error-alert">{error}</div>}
        {success && <div className="success-alert">{success}</div>}

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              type="text"
              id="username"
              name="username"
              className="form-input"
              value={formData.username}
              onChange={handleChange}
              required
              minLength="3"
              maxLength="50"
              placeholder="Choose a username"
            />
          </div>

          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              name="email"
              className="form-input"
              value={formData.email}
              onChange={handleChange}
              required
              placeholder="you@example.com"
            />
          </div>

          <div className="form-group">
            <label htmlFor="aadharNumber">Aadhar Card Number</label>
            <input
              type="text"
              id="aadharNumber"
              name="aadharNumber"
              className="form-input"
              value={formData.aadharNumber}
              onChange={handleChange}
              required
              placeholder="e.g. 1234 5678 9012"
            />
          </div>

          <div className="form-group">
            <label htmlFor="initialBalance">Initial Balance (Optional)</label>
            <input
              type="number"
              id="initialBalance"
              name="initialBalance"
              className="form-input"
              min="0"
              step="0.01"
              value={formData.initialBalance}
              onChange={handleChange}
              placeholder="0.00"
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              name="password"
              className="form-input"
              value={formData.password}
              onChange={handleChange}
              required
              minLength="6"
              placeholder="•••••••• (Min 6 chars)"
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="confirmPassword">Confirm Password</label>
            <input
              type="password"
              id="confirmPassword"
              name="confirmPassword"
              className="form-input"
              value={formData.confirmPassword}
              onChange={handleChange}
              required
              placeholder="••••••••"
            />
          </div>

          <button type="submit" className="btn-primary auth-submit" disabled={isLoading || success}>
            {isLoading ? 'Submitting...' : 'Register'}
          </button>
        </form>

        <div className="auth-footer">
          <p>Already have an account? <Link to="/login">Sign in here</Link></p>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
