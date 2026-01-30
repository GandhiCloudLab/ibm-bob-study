import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import '../styles/Navbar.css';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <h2>💰 Wealthcare</h2>
      </div>
      <div className="navbar-menu">
        <Link to="/dashboard" className="nav-link">Dashboard</Link>
        <Link to="/customers" className="nav-link">Customers</Link>
        <Link to="/goals" className="nav-link">Goals</Link>
        <Link to="/investments" className="nav-link">Investments</Link>
      </div>
      <div className="navbar-user">
        <span className="user-name">👤 {user?.username}</span>
        <span className="user-role">({user?.role})</span>
        <button onClick={handleLogout} className="logout-btn">Logout</button>
      </div>
    </nav>
  );
};

export default Navbar;