import React from 'react';
import './TransactionTable.css';

const TransactionTable = ({ transactions }) => {
  const formatCurrency = (amount, type) => {
    const isPositive = type === 'DEPOSIT' || type === 'TRANSFER_IN';
    const sign = isPositive ? '+' : '-';
    const value = new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
    }).format(amount);
    
    return <span className={isPositive ? 'text-success' : 'text-danger'}>{sign}{value}</span>;
  };

  const formatDate = (timestamp) => {
    return new Date(timestamp).toLocaleString();
  };

  const getTypeBadgeClass = (type) => {
    switch (type) {
      case 'DEPOSIT': return 'badge-success';
      case 'WITHDRAWAL': return 'badge-danger';
      case 'TRANSFER_IN': return 'badge-info';
      case 'TRANSFER_OUT': return 'badge-warning';
      default: return 'badge-secondary';
    }
  };

  if (!transactions || transactions.length === 0) {
    return (
      <div className="empty-state glass-card">
        <div className="empty-icon">📝</div>
        <h3>No Transactions Found</h3>
        <p>This account doesn't have any transaction history yet.</p>
      </div>
    );
  }

  return (
    <div className="table-container glass-card">
      <table className="transaction-table">
        <thead>
          <tr>
            <th>Date & Time</th>
            <th>Type</th>
            <th>Description</th>
            <th className="text-right">Amount</th>
            <th className="text-right">Balance After</th>
          </tr>
        </thead>
        <tbody>
          {transactions.map((tx, index) => (
            <tr key={tx.id} style={{ animationDelay: `${index * 0.05}s` }} className="animate-slide-up">
              <td>{formatDate(tx.timestamp)}</td>
              <td>
                <span className={`status-badge ${getTypeBadgeClass(tx.type)}`}>
                  {tx.type.replace('_', ' ')}
                </span>
              </td>
              <td>{tx.description || '-'}</td>
              <td className="text-right font-mono">{formatCurrency(tx.amount, tx.type)}</td>
              <td className="text-right font-mono text-secondary">
                {new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(tx.balanceAfter)}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default TransactionTable;
