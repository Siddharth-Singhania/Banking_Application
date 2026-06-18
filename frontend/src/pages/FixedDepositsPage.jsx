import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';

const FixedDepositsPage = () => {
  const { user } = useAuth();
  const [accounts, setAccounts] = useState([]);
  const [deposits, setDeposits] = useState([]);
  const [loading, setLoading] = useState(true);
  
  const [amount, setAmount] = useState('');
  const [durationInMonths, setDurationInMonths] = useState('3');
  const [agreedToTerms, setAgreedToTerms] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  
  const [status, setStatus] = useState({ type: '', message: '' });
  const [showBreakModal, setShowBreakModal] = useState(false);
  const [selectedDeposit, setSelectedDeposit] = useState(null);
  const [isBreaking, setIsBreaking] = useState(false);

  useEffect(() => {
    fetchData(true);
    const interval = setInterval(() => {
      fetchData(false);
    }, 5000);
    return () => clearInterval(interval);
  }, []);

  const fetchData = async (showLoading) => {
    try {
      if (showLoading) setLoading(true);
      const accRes = await api.get('/accounts');
      const userAccounts = accRes.data;
      setAccounts(userAccounts);
      
      if (userAccounts.length > 0) {
        const depRes = await api.get(`/fixed-deposits/account/${userAccounts[0].id}`);
        setDeposits(depRes.data);
      }
    } catch (err) {
      if (showLoading) setStatus({ type: 'error', message: 'Failed to load data.' });
    } finally {
      if (showLoading) setLoading(false);
    }
  };

  const handleCreateFD = async (e) => {
    e.preventDefault();
    if (!agreedToTerms) {
      setStatus({ type: 'error', message: 'You must agree to the terms and conditions.' });
      return;
    }
    
    if (accounts.length === 0) {
      setStatus({ type: 'error', message: 'No active account found.' });
      return;
    }

    setIsSubmitting(true);
    setStatus({ type: '', message: '' });

    try {
      await api.post(`/fixed-deposits/account/${accounts[0].id}`, {
        amount: parseFloat(amount),
        durationInMonths: parseInt(durationInMonths),
        agreedToTerms
      });
      
      setStatus({ type: 'success', message: 'Fixed deposit created successfully!' });
      setAmount('');
      setAgreedToTerms(false);
      setDurationInMonths('3');
      fetchData(false);
    } catch (err) {
      setStatus({ type: 'error', message: err.response?.data?.message || 'Failed to create fixed deposit.' });
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleBreakDeposit = async () => {
    setIsBreaking(true);
    setStatus({ type: '', message: '' });

    try {
      await api.post(`/fixed-deposits/${selectedDeposit.id}/break`);
      setStatus({ type: 'success', message: 'Fixed deposit broken successfully!' });
      setShowBreakModal(false);
      fetchData(false);
    } catch (err) {
      setStatus({ type: 'error', message: err.response?.data?.message || 'Failed to break fixed deposit.' });
    } finally {
      setIsBreaking(false);
    }
  };

  const openBreakModal = (deposit) => {
    setSelectedDeposit(deposit);
    setShowBreakModal(true);
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(amount);
  };

  if (loading) return <div className="loading-spinner">Loading...</div>;

  return (
    <div className="page-container animate-fade-in">
      <h1 className="page-title gradient-text">Fixed Deposits</h1>
      
      {status.message && (
        <div className={status.type === 'error' ? 'error-alert' : 'success-alert'}>
          {status.message}
        </div>
      )}

      <div className="form-container glass-card" style={{ maxWidth: '600px', margin: '0 auto 2rem auto' }}>
        <h3>Create New Savings Goal</h3>
        {accounts.length > 0 && (
          <p className="text-secondary mb-3">Available Balance: {formatCurrency(accounts[0].balance)}</p>
        )}
        
        <form onSubmit={handleCreateFD}>
          <div className="form-group">
            <label>Amount (INR)</label>
            <input 
              type="number" 
              className="form-input"
              min="100" 
              step="0.01" 
              value={amount} 
              onChange={e => setAmount(e.target.value)}
              required 
              placeholder="Min ₹100.00"
            />
          </div>
          
          <div className="form-group">
            <label>Duration</label>
            <select 
              className="form-input" 
              value={durationInMonths} 
              onChange={e => setDurationInMonths(e.target.value)}
            >
              <option value="3">3 Months</option>
              <option value="6">6 Months</option>
              <option value="12">1 Year</option>
              <option value="24">2 Years</option>
              <option value="36">3 Years</option>
            </select>
          </div>
          
          <div className="form-group" style={{ display: 'flex', alignItems: 'flex-start', gap: '10px' }}>
            <input 
              type="checkbox" 
              id="terms" 
              checked={agreedToTerms} 
              onChange={e => setAgreedToTerms(e.target.checked)} 
              style={{ marginTop: '4px' }}
            />
            <label htmlFor="terms" className="text-secondary" style={{ fontSize: '0.9rem' }}>
              I agree to the terms: Early withdrawal incurs a 2% penalty fee on the deposited amount.
            </label>
          </div>
          
          <button type="submit" className="btn-primary w-100" disabled={isSubmitting || accounts.length === 0}>
            {isSubmitting ? <><span className="loader-spinner"></span> Processing...</> : 'Create Fixed Deposit'}
          </button>
        </form>
      </div>

      <div className="glass-card table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Amount</th>
              <th>Duration</th>
              <th>Start Date</th>
              <th>Maturity Date</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {deposits.length === 0 ? (
              <tr>
                <td colSpan="6" className="text-center text-secondary">No fixed deposits found.</td>
              </tr>
            ) : (
              deposits.map(deposit => (
                <tr key={deposit.id}>
                  <td className="amount-positive">{formatCurrency(deposit.amount)}</td>
                  <td>{deposit.durationInMonths} Months</td>
                  <td>{deposit.startDate}</td>
                  <td>{deposit.maturityDate}</td>
                  <td>
                    <span className={`badge ${deposit.status === 'ACTIVE' ? 'badge-success' : deposit.status === 'BROKEN' ? 'badge-danger' : 'badge-active'}`}>
                      {deposit.status}
                    </span>
                  </td>
                  <td>
                    {deposit.status === 'ACTIVE' && (
                      <button className="btn-danger btn-sm" onClick={() => openBreakModal(deposit)}>
                        Break Deposit
                      </button>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {showBreakModal && (
        <div className="modal-overlay">
          <div className="modal-content glass-card animate-slide-up">
            <h2>Confirm Early Withdrawal</h2>
            <p className="text-secondary mb-4">
              Are you sure you want to break this fixed deposit early? A 2% penalty will be applied to the principal amount.
            </p>
            <div className="modal-actions">
              <button type="button" className="btn-secondary" onClick={() => setShowBreakModal(false)} disabled={isBreaking}>Cancel</button>
              <button type="button" className="btn-danger" onClick={handleBreakDeposit} disabled={isBreaking}>
                {isBreaking ? <><span className="loader-spinner"></span> Processing...</> : 'Yes, Break Deposit'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default FixedDepositsPage;
