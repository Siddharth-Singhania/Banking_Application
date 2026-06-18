import React, { useState, useEffect } from 'react';
import api from '../api/axios';

const TransferPage = () => {
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  
  const [formData, setFormData] = useState({
    sourceAccountNumber: '',
    destinationAccountNumber: '',
    amount: '',
    description: ''
  });
  
  const [status, setStatus] = useState({ type: '', message: '' }); // type: 'error' or 'success'
  const [isTransferring, setIsTransferring] = useState(false);

  useEffect(() => {
    fetchAccounts();
  }, []);

  const fetchAccounts = async () => {
    try {
      const response = await api.get('/accounts');
      setAccounts(response.data);
      if (response.data.length > 0) {
        setFormData(prev => ({ ...prev, sourceAccountNumber: response.data[0].accountNumber }));
      }
    } catch (err) {
      setStatus({ type: 'error', message: 'Failed to load your accounts.' });
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setStatus({ type: '', message: '' });
    
    if (formData.sourceAccountNumber === formData.destinationAccountNumber) {
      setStatus({ type: 'error', message: 'Source and destination accounts cannot be the same.' });
      return;
    }

    setIsTransferring(true);

    try {
      await api.post('/transactions/transfer', {
        sourceAccountNumber: formData.sourceAccountNumber,
        destinationAccountNumber: formData.destinationAccountNumber,
        amount: parseFloat(formData.amount),
        description: formData.description
      });
      
      setStatus({ type: 'success', message: 'Transfer completed successfully!' });
      setFormData(prev => ({ ...prev, destinationAccountNumber: '', amount: '', description: '' }));
      fetchAccounts(); // Refresh balances
    } catch (err) {
      setStatus({ type: 'error', message: err.response?.data?.message || 'Transfer failed. Please check details and try again.' });
    } finally {
      setIsTransferring(false);
    }
  };

  if (loading) return <div className="loading-spinner">Loading...</div>;

  return (
    <div className="page-container animate-fade-in">
      <h1 className="page-title">Transfer Funds</h1>
      
      <div className="form-container glass-card" style={{ maxWidth: '600px', margin: '0 auto' }}>
        {status.message && (
          <div className={status.type === 'error' ? 'error-alert' : 'success-alert'}>
            {status.message}
          </div>
        )}

        {accounts.length === 0 ? (
          <div className="text-center py-4">
            <p>You need an active account to make transfers.</p>
          </div>
        ) : (
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>From Account</label>
              <div className="form-input" style={{ background: 'var(--bg-secondary)', cursor: 'not-allowed' }}>
                {accounts[0].accountHolderName} ({accounts[0].accountNumber}) - ₹{accounts[0].balance.toFixed(2)}
                {accounts[0].status === 'FROZEN' ? ' [FROZEN]' : ''}
              </div>
            </div>

            <div className="form-group">
              <label>To Account Number</label>
              <input 
                type="text" 
                name="destinationAccountNumber" 
                value={formData.destinationAccountNumber} 
                onChange={handleChange}
                required
                placeholder="Enter destination account number"
                className="form-input"
              />
            </div>

            <div className="form-group">
              <label>Amount (INR)</label>
              <input 
                type="number" 
                name="amount" 
                min="0.01" 
                step="0.01" 
                value={formData.amount} 
                onChange={handleChange}
                required
                placeholder="0.00"
                className="form-input"
              />
            </div>

            <div className="form-group">
              <label>Description (Optional)</label>
              <input 
                type="text" 
                name="description" 
                value={formData.description} 
                onChange={handleChange}
                placeholder="e.g. Rent payment"
                maxLength="255"
                className="form-input"
              />
            </div>

            <button 
              type="submit" 
              className="btn-primary w-100 mt-4" 
              disabled={isTransferring}
            >
              {isTransferring ? (
                <>
                  <span className="loader-spinner"></span> Processing Transfer...
                </>
              ) : 'Complete Transfer'}
            </button>
          </form>
        )}
      </div>
    </div>
  );
};

export default TransferPage;
