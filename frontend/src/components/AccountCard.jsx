import React from 'react';
import './AccountCard.css';

const AccountCard = ({ account, onDeposit, onWithdraw, onSetLimit }) => {
  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
    }).format(amount);
  };

  const formatAccountNumber = (number) => {
    return number.replace(/(.{4})/g, '$1 ').trim();
  };

  const isFrozen = account.status === 'FROZEN';

  return (
    <div className={`account-card glass-card ${isFrozen ? 'frozen' : ''}`}>
      <div className="card-header">
        <h3 className="account-name">{account.accountHolderName}</h3>
        <span className={`status-badge ${isFrozen ? 'badge-danger' : 'badge-success'}`}>
          {account.status}
        </span>
      </div>
      
      <div className="card-body">
        <p className="account-number">{formatAccountNumber(account.accountNumber)}</p>
        
        <div className="balance-section">
          <p className="balance-label">Available Balance</p>
          <h2 className="balance-amount">{formatCurrency(account.balance)}</h2>
          <p className="overdraft-limit">Daily Transaction Limit: {formatCurrency(account.dailyTransactionLimit)}</p>
        </div>
      </div>
      
      <div className="card-footer">
        <button 
          className="btn-primary" 
          onClick={() => onDeposit(account)}
          disabled={isFrozen}
        >
          Deposit
        </button>
        <button 
          className="btn-secondary" 
          onClick={() => onWithdraw(account)}
          disabled={isFrozen}
        >
          Withdraw
        </button>
        <button 
          className="btn-secondary" 
          onClick={() => onSetLimit(account)}
          disabled={isFrozen}
        >
          Set Limit
        </button>
      </div>
    </div>
  );
};

export default AccountCard;
