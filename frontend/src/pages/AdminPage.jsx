import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import './AdminPage.css';

const AdminPage = () => {
  const [activeTab, setActiveTab] = useState('pendingUsers'); // 'pendingUsers' or 'allAccounts'
  
  const [pendingUsers, setPendingUsers] = useState([]);
  const [accounts, setAccounts] = useState([]);
  
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    fetchData(true);
    const interval = setInterval(() => {
      fetchData(false);
    }, 3000);
    return () => clearInterval(interval);
  }, [activeTab]);

  const fetchData = async (showLoading = false) => {
    if (showLoading) setLoading(true);
    setError('');
    setSuccess('');
    
    try {
      if (activeTab === 'pendingUsers') {
        const response = await api.get('/admin/users/pending');
        setPendingUsers(response.data);
      } else {
        const response = await api.get('/admin/accounts');
        setAccounts(response.data);
      }
    } catch (err) {
      setError(`Failed to fetch ${activeTab === 'pendingUsers' ? 'users' : 'accounts'}.`);
    } finally {
      setLoading(false);
    }
  };

  const handleUserAction = async (userId, action) => {
    setActionLoading(true);
    setError('');
    try {
      await api.post(`/admin/users/${userId}/${action}`);
      setSuccess(`User successfully ${action}d.`);
      fetchData(); // Refresh list
    } catch (err) {
      setError(`Failed to ${action} user.`);
    } finally {
      setActionLoading(false);
    }
  };

  const handleAccountAction = async (accountId, action) => {
    setActionLoading(true);
    setError('');
    try {
      await api.post(`/admin/accounts/${accountId}/${action}`);
      setSuccess(`Account successfully ${action}d.`);
      fetchData(); // Refresh list
    } catch (err) {
      setError(`Failed to ${action} account.`);
    } finally {
      setActionLoading(false);
    }
  };

  return (
    <div className="page-container animate-fade-in admin-theme">
      <div className="dashboard-header" style={{ marginBottom: '2rem' }}>
        <div>
          <h1 className="page-title admin-title">Admin Dashboard</h1>
          <p className="text-secondary">Manage users and accounts</p>
        </div>
      </div>

      <div className="admin-tabs">
        <button 
          className={`admin-tab ${activeTab === 'pendingUsers' ? 'active' : ''}`}
          onClick={() => setActiveTab('pendingUsers')}
        >
          Pending Users
        </button>
        <button 
          className={`admin-tab ${activeTab === 'allAccounts' ? 'active' : ''}`}
          onClick={() => setActiveTab('allAccounts')}
        >
          All Accounts
        </button>
      </div>

      {error && <div className="error-alert">{error}</div>}
      {success && <div className="success-alert">{success}</div>}

      <div className="admin-content glass-card">
        {loading ? (
          <div className="text-center p-4"><div className="loading-spinner"></div></div>
        ) : activeTab === 'pendingUsers' ? (
          /* PENDING USERS TABLE */
          <div className="table-container">
            {pendingUsers.length === 0 ? (
              <div className="text-center p-4 text-secondary">No pending users awaiting approval.</div>
            ) : (
              <table className="transaction-table">
                <thead>
                  <tr>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Registration Date</th>
                    <th className="text-center">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {pendingUsers.map(user => (
                    <tr key={user.id}>
                      <td>{user.username}</td>
                      <td>{user.email}</td>
                      <td>{new Date(user.createdAt).toLocaleDateString()}</td>
                      <td className="text-center">
                        <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'center' }}>
                          <button 
                            className="btn-success btn-sm" 
                            onClick={() => handleUserAction(user.id, 'approve')}
                            disabled={actionLoading}
                            style={{ padding: '0.25rem 0.5rem', fontSize: '0.8rem' }}
                          >
                            Approve
                          </button>
                          <button 
                            className="btn-danger btn-sm" 
                            onClick={() => handleUserAction(user.id, 'reject')}
                            disabled={actionLoading}
                            style={{ padding: '0.25rem 0.5rem', fontSize: '0.8rem' }}
                          >
                            Reject
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        ) : (
          /* ALL ACCOUNTS TABLE */
          <div className="table-container">
            {accounts.length === 0 ? (
              <div className="text-center p-4 text-secondary">No accounts found in the system.</div>
            ) : (
              <table className="transaction-table">
                <thead>
                  <tr>
                    <th>Account No.</th>
                    <th>Holder</th>
                    <th className="text-right">Balance</th>
                    <th className="text-center">Status</th>
                    <th className="text-center">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {accounts.map(account => (
                    <tr key={account.id}>
                      <td className="font-mono">{account.accountNumber}</td>
                      <td>{account.accountHolderName}</td>
                      <td className="text-right font-mono">
                        ₹{account.balance.toFixed(2)}
                      </td>
                      <td className="text-center">
                        <span className={`status-badge ${account.status === 'ACTIVE' ? 'badge-success' : 'badge-danger'}`}>
                          {account.status}
                        </span>
                      </td>
                      <td className="text-center">
                        {account.status === 'ACTIVE' ? (
                          <button 
                            className="btn-danger btn-sm" 
                            onClick={() => handleAccountAction(account.id, 'freeze')}
                            disabled={actionLoading}
                            style={{ padding: '0.25rem 0.5rem', fontSize: '0.8rem' }}
                          >
                            Freeze
                          </button>
                        ) : (
                          <button 
                            className="btn-success btn-sm" 
                            onClick={() => handleAccountAction(account.id, 'unfreeze')}
                            disabled={actionLoading}
                            style={{ padding: '0.25rem 0.5rem', fontSize: '0.8rem' }}
                          >
                            Unfreeze
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminPage;
