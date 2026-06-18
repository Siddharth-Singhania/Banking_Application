import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import AccountCard from '../components/AccountCard';
import './DashboardPage.css';

const DashboardPage = () => {
  const { user } = useAuth();
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  const [showTransactionModal, setShowTransactionModal] = useState(false);
  const [transactionType, setTransactionType] = useState(''); // 'deposit' or 'withdraw'
  const [selectedAccount, setSelectedAccount] = useState(null);
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [transactionError, setTransactionError] = useState('');
  const [transactionLoading, setTransactionLoading] = useState(false);

  const [showLimitModal, setShowLimitModal] = useState(false);
  const [limitAmount, setLimitAmount] = useState('');
  const [limitError, setLimitError] = useState('');
  const [limitLoading, setLimitLoading] = useState(false);

  useEffect(() => {
    fetchAccounts(true);
    const interval = setInterval(() => {
      fetchAccounts(false);
    }, 3000);
    return () => clearInterval(interval);
  }, []);

  const fetchAccounts = async (showLoading = false) => {
    try {
      if (showLoading) setLoading(true);
      const response = await api.get('/accounts');
      setAccounts(response.data);
    } catch (err) {
      setError('Failed to load accounts. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  const openTransactionModal = (account, type) => {
    setSelectedAccount(account);
    setTransactionType(type);
    setAmount('');
    setDescription('');
    setTransactionError('');
    setShowTransactionModal(true);
  };

  const handleTransaction = async (e) => {
    e.preventDefault();
    setTransactionError('');
    setTransactionLoading(true);

    try {
      await api.post(`/accounts/${selectedAccount.id}/${transactionType}`, {
        amount: parseFloat(amount),
        description
      });
      setShowTransactionModal(false);
      fetchAccounts();
    } catch (err) {
      setTransactionError(err.response?.data?.message || `Failed to ${transactionType}`);
    } finally {
      setTransactionLoading(false);
    }
  };

  const openLimitModal = (account) => {
    setSelectedAccount(account);
    setLimitAmount(account.dailyTransactionLimit || '');
    setLimitError('');
    setShowLimitModal(true);
  };

  const handleLimitUpdate = async (e) => {
    e.preventDefault();
    setLimitError('');
    setLimitLoading(true);

    try {
      await api.put(`/accounts/${selectedAccount.id}/limit`, {
        limit: parseFloat(limitAmount)
      });
      setShowLimitModal(false);
      fetchAccounts();
    } catch (err) {
      setLimitError(err.response?.data?.message || 'Failed to update limit');
    } finally {
      setLimitLoading(false);
    }
  };

  const totalBalance = accounts.reduce((sum, acc) => sum + acc.balance, 0);

  if (loading) return <div className="loading-spinner">Loading...</div>;

  return (
    <div className="page-container animate-fade-in">
      <div className="dashboard-header">
        <div>
          <h1 className="page-title">Dashboard</h1>
          <p className="text-secondary">Welcome back, {user?.username}!</p>
        </div>
      </div>

      <div className="stats-container glass-card">
        <div className="stat-item">
          <p className="stat-label">Total Balance</p>
          <h2 className="stat-value gradient-text">
            {new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(totalBalance)}
          </h2>
        </div>
      </div>

      {error && <div className="error-alert">{error}</div>}

      <div className="accounts-grid">
        {accounts.length === 0 ? (
          <div className="empty-state glass-card" style={{ gridColumn: '1 / -1' }}>
            <div className="empty-icon">💳</div>
            <h3>No Accounts Yet</h3>
            <p>Your account is being set up. Please wait for admin approval.</p>
          </div>
        ) : (
          accounts.map(account => (
            <AccountCard 
              key={account.id} 
              account={account} 
              onDeposit={(acc) => openTransactionModal(acc, 'deposit')}
              onWithdraw={(acc) => openTransactionModal(acc, 'withdraw')}
              onSetLimit={openLimitModal}
            />
          ))
        )}
      </div>

      {/* Transaction Modal */}
      {showTransactionModal && (
        <div className="modal-overlay">
          <div className="modal-content glass-card animate-slide-up">
            <h2>{transactionType === 'deposit' ? 'Deposit Funds' : 'Withdraw Funds'}</h2>
            <p className="text-secondary mb-4">Account: {selectedAccount?.accountNumber}</p>
            
            {transactionError && <div className="error-alert">{transactionError}</div>}
            
            <form onSubmit={handleTransaction}>
              <div className="form-group">
                <label>Amount (INR)</label>
                <input 
                  type="number" 
                  className="form-input"
                  min="0.01" 
                  step="0.01" 
                  value={amount} 
                  onChange={e => setAmount(e.target.value)}
                  required 
                  placeholder="0.00"
                />
              </div>
              <div className="form-group">
                <label>Description (Optional)</label>
                <input 
                  type="text" 
                  className="form-input"
                  value={description} 
                  onChange={e => setDescription(e.target.value)}
                  placeholder="e.g. Salary, Rent, etc."
                />
              </div>
              <div className="modal-actions">
                <button type="button" className="btn-secondary" onClick={() => setShowTransactionModal(false)} disabled={transactionLoading}>Cancel</button>
                <button type="submit" className="btn-primary" disabled={transactionLoading}>
                  {transactionLoading ? (
                    <>
                      <span className="loader-spinner"></span> Processing...
                    </>
                  ) : 'Confirm'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Limit Modal */}
      {showLimitModal && (
        <div className="modal-overlay">
          <div className="modal-content glass-card animate-slide-up">
            <h2>Set Daily Transaction Limit</h2>
            <p className="text-secondary mb-4">Account: {selectedAccount?.accountNumber}</p>
            
            {limitError && <div className="error-alert">{limitError}</div>}
            
            <form onSubmit={handleLimitUpdate}>
              <div className="form-group">
                <label>New Limit (Max: 1,00,000)</label>
                <input 
                  type="number" 
                  className="form-input"
                  min="0.01" 
                  max="100000"
                  step="0.01" 
                  value={limitAmount} 
                  onChange={e => setLimitAmount(e.target.value)}
                  required 
                  placeholder="e.g. 50000.00"
                />
              </div>
              <div className="modal-actions">
                <button type="button" className="btn-secondary" onClick={() => setShowLimitModal(false)} disabled={limitLoading}>Cancel</button>
                <button type="submit" className="btn-primary" disabled={limitLoading}>
                  {limitLoading ? (
                    <>
                      <span className="loader-spinner"></span> Updating...
                    </>
                  ) : 'Save Limit'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default DashboardPage;
