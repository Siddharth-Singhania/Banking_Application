import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';

const RecurringPaymentsPage = () => {
  const { user } = useAuth();
  const [accounts, setAccounts] = useState([]);
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  
  const [destinationAccountNumber, setDestinationAccountNumber] = useState('');
  const [amount, setAmount] = useState('');
  const [dayOfMonth, setDayOfMonth] = useState('1');
  const [description, setDescription] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  
  const [status, setStatus] = useState({ type: '', message: '' });
  const [cancellingId, setCancellingId] = useState(null);

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
        const payRes = await api.get(`/recurring-payments/account/${userAccounts[0].id}`);
        setPayments(payRes.data);
      }
    } catch (err) {
      if (showLoading) setStatus({ type: 'error', message: 'Failed to load data.' });
    } finally {
      if (showLoading) setLoading(false);
    }
  };

  const handleCreatePayment = async (e) => {
    e.preventDefault();
    if (accounts.length === 0) {
      setStatus({ type: 'error', message: 'No active account found.' });
      return;
    }

    setIsSubmitting(true);
    setStatus({ type: '', message: '' });

    try {
      await api.post(`/recurring-payments/account/${accounts[0].id}`, {
        destinationAccountNumber,
        amount: parseFloat(amount),
        dayOfMonth: parseInt(dayOfMonth),
        description
      });
      
      setStatus({ type: 'success', message: 'Recurring payment created successfully!' });
      setDestinationAccountNumber('');
      setAmount('');
      setDayOfMonth('1');
      setDescription('');
      fetchData(false);
    } catch (err) {
      setStatus({ type: 'error', message: err.response?.data?.message || 'Failed to create recurring payment.' });
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCancelPayment = async (id) => {
    setCancellingId(id);
    setStatus({ type: '', message: '' });

    try {
      await api.put(`/recurring-payments/${id}/cancel`);
      setStatus({ type: 'success', message: 'Recurring payment cancelled.' });
      fetchData(false);
    } catch (err) {
      setStatus({ type: 'error', message: err.response?.data?.message || 'Failed to cancel recurring payment.' });
    } finally {
      setCancellingId(null);
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(amount);
  };

  if (loading) return <div className="loading-spinner">Loading...</div>;

  return (
    <div className="page-container animate-fade-in">
      <h1 className="page-title gradient-text">Recurring Payments</h1>
      
      {status.message && (
        <div className={status.type === 'error' ? 'error-alert' : 'success-alert'}>
          {status.message}
        </div>
      )}

      <div className="form-container glass-card" style={{ maxWidth: '600px', margin: '0 auto 2rem auto' }}>
        <h3>Set Up Autopay</h3>
        
        <form onSubmit={handleCreatePayment}>
          <div className="form-group">
            <label>Destination Account Number</label>
            <input 
              type="text" 
              className="form-input"
              value={destinationAccountNumber} 
              onChange={e => setDestinationAccountNumber(e.target.value)}
              required 
              placeholder="Account Number"
            />
          </div>

          <div className="form-group">
            <label>Amount (INR)</label>
            <input 
              type="number" 
              className="form-input"
              min="1" 
              step="0.01" 
              value={amount} 
              onChange={e => setAmount(e.target.value)}
              required 
              placeholder="0.00"
            />
          </div>
          
          <div className="form-group">
            <label>Day of Month</label>
            <select 
              className="form-input" 
              value={dayOfMonth} 
              onChange={e => setDayOfMonth(e.target.value)}
            >
              {Array.from({ length: 28 }, (_, i) => i + 1).map(day => (
                <option key={day} value={day}>{day}</option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>Description (Optional)</label>
            <input 
              type="text" 
              className="form-input"
              value={description} 
              onChange={e => setDescription(e.target.value)}
              placeholder="e.g. Monthly Rent"
              maxLength="255"
            />
          </div>
          
          <button type="submit" className="btn-primary w-100 mt-2" disabled={isSubmitting || accounts.length === 0}>
            {isSubmitting ? <><span className="loader-spinner"></span> Processing...</> : 'Create Recurring Payment'}
          </button>
        </form>
      </div>

      <div className="glass-card table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Destination</th>
              <th>Amount</th>
              <th>Day of Month</th>
              <th>Description</th>
              <th>Status</th>
              <th>Last Executed</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {payments.length === 0 ? (
              <tr>
                <td colSpan="7" className="text-center text-secondary">No recurring payments found.</td>
              </tr>
            ) : (
              payments.map(payment => (
                <tr key={payment.id}>
                  <td>{payment.destinationAccountNumber}</td>
                  <td className="amount-negative">{formatCurrency(payment.amount)}</td>
                  <td>{payment.dayOfMonth}</td>
                  <td>{payment.description || '-'}</td>
                  <td>
                    <span className={`badge ${payment.status === 'ACTIVE' ? 'badge-success' : 'badge-danger'}`}>
                      {payment.status}
                    </span>
                  </td>
                  <td>{payment.lastExecutedAt ? new Date(payment.lastExecutedAt).toLocaleDateString() : 'Never'}</td>
                  <td>
                    {payment.status === 'ACTIVE' && (
                      <button 
                        className="btn-danger btn-sm" 
                        onClick={() => handleCancelPayment(payment.id)}
                        disabled={cancellingId === payment.id}
                      >
                        {cancellingId === payment.id ? <><span className="loader-spinner"></span></> : 'Cancel'}
                      </button>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default RecurringPaymentsPage;
