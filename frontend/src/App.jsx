import React from 'react';
import { BrowserRouter, Routes, Route, Navigate, Outlet } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Navbar from './components/Navbar';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import TransferPage from './pages/TransferPage';
import TransactionHistoryPage from './pages/TransactionHistoryPage';
import FixedDepositsPage from './pages/FixedDepositsPage';
import RecurringPaymentsPage from './pages/RecurringPaymentsPage';
import AdminPage from './pages/AdminPage';

import './App.css';

const Layout = () => (
  <>
    <Navbar />
    <main className="main-content">
      <Outlet />
    </main>
  </>
);

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          
          <Route element={<ProtectedRoute />}>
            <Route element={<Layout />}>
              <Route path="/dashboard" element={<DashboardPage />} />
              <Route path="/transfer" element={<TransferPage />} />
              <Route path="/transactions" element={<TransactionHistoryPage />} />
              <Route path="/fixed-deposits" element={<FixedDepositsPage />} />
              <Route path="/recurring-payments" element={<RecurringPaymentsPage />} />
            </Route>
          </Route>
          
          <Route element={<ProtectedRoute adminOnly />}>
            <Route element={<Layout />}>
              <Route path="/admin" element={<AdminPage />} />
            </Route>
          </Route>
          
          <Route path="*" element={<Navigate to="/login" />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
