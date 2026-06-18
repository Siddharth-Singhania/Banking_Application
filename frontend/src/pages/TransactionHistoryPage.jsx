import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import TransactionTable from '../components/TransactionTable';

const TransactionHistoryPage = () => {
  const [accounts, setAccounts] = useState([]);
  const [selectedAccountId, setSelectedAccountId] = useState('');
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [txLoading, setTxLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchAccounts();
  }, []);

  useEffect(() => {
    if (selectedAccountId) {
      fetchTransactions(selectedAccountId);
    } else {
      setTransactions([]);
    }
  }, [selectedAccountId]);

  const fetchAccounts = async () => {
    try {
      const response = await api.get('/accounts');
      setAccounts(response.data);
      if (response.data.length > 0) {
        setSelectedAccountId(response.data[0].id);
      }
    } catch (err) {
      setError('Failed to load accounts.');
    } finally {
      setLoading(false);
    }
  };

  const fetchTransactions = async (accountId) => {
    setTxLoading(true);
    try {
      const response = await api.get(`/transactions/account/${accountId}`);
      setTransactions(response.data);
    } catch (err) {
      setError('Failed to load transactions.');
    } finally {
      setTxLoading(false);
    }
  };

  const handleExport = async () => {
    if (!selectedAccountId) return;
    
    try {
      const response = await api.get(`/accounts/${selectedAccountId}/export`, {
        responseType: 'blob', // Important for file download
      });
      
      // Create a blob link to download
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      
      // Try to get filename from header or fallback
      const contentDisposition = response.headers['content-disposition'];
      let fileName = 'statement.csv';
      if (contentDisposition && contentDisposition.indexOf('filename=') !== -1) {
        const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
        const matches = filenameRegex.exec(contentDisposition);
        if (matches != null && matches[1]) fileName = matches[1].replace(/['"]/g, '');
      }
      
      link.setAttribute('download', fileName);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      setError('Failed to export statement.');
    }
  };

  if (loading) return <div className="loading-spinner">Loading...</div>;

  return (
    <div className="page-container animate-fade-in">
      <div className="dashboard-header" style={{ marginBottom: '2rem' }}>
        <h1 className="page-title">Transaction History</h1>
        
        {accounts.length > 0 && selectedAccountId && (
          <button className="btn-secondary" onClick={handleExport}>
            ⬇ Export CSV
          </button>
        )}
      </div>

      {error && <div className="error-alert">{error}</div>}

      <div className="glass-card mb-4" style={{ marginBottom: '2rem', padding: '1.5rem' }}>
        <div className="form-group m-0">
          <label>Account</label>
          {accounts.length > 0 ? (
            <div className="form-input" style={{ background: 'var(--bg-secondary)', cursor: 'not-allowed' }}>
              {accounts[0].accountHolderName} ({accounts[0].accountNumber}) - ₹{accounts[0].balance.toFixed(2)}
            </div>
          ) : (
            <div className="form-input" style={{ background: 'var(--bg-secondary)', cursor: 'not-allowed' }}>
              No active accounts found.
            </div>
          )}
        </div>
      </div>

      {txLoading ? (
        <div className="text-center p-4"><div className="loading-spinner"></div></div>
      ) : selectedAccountId ? (
        <TransactionTable transactions={transactions} />
      ) : (
        <div className="empty-state glass-card">
          <p>Please select an account to view its transactions.</p>
        </div>
      )}
    </div>
  );
};

export default TransactionHistoryPage;
