import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import { useAuth } from '../context/AuthContext';
import customerService from '../services/customerService';
import goalService from '../services/goalService';
import investmentService from '../services/investmentService';
import '../styles/Dashboard.css';

const Dashboard = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState({
    customers: 0,
    goals: 0,
    investments: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      const [customers, goals, investments] = await Promise.all([
        customerService.getAllCustomers(),
        goalService.getAllGoals(),
        investmentService.getAllInvestments()
      ]);

      setStats({
        customers: customers.length,
        goals: goals.length,
        investments: investments.length
      });
    } catch (error) {
      console.error('Error loading dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div>
        <Navbar />
        <div className="dashboard-container">
          <p>Loading...</p>
        </div>
      </div>
    );
  }

  return (
    <div>
      <Navbar />
      <div className="dashboard-container">
        <h1>Dashboard</h1>
        <p>Welcome, {user?.username}!</p>
        
        <div className="stats-grid">
          <div className="stat-card">
            <h3>👥 Customers</h3>
            <p className="stat-number">{stats.customers}</p>
          </div>
          <div className="stat-card">
            <h3>🎯 Goals</h3>
            <p className="stat-number">{stats.goals}</p>
          </div>
          <div className="stat-card">
            <h3>💰 Investments</h3>
            <p className="stat-number">{stats.investments}</p>
          </div>
        </div>

        <div className="quick-actions">
          <h2>Quick Actions</h2>
          <div className="action-buttons">
            <button className="action-btn">Add Customer</button>
            <button className="action-btn">Create Goal</button>
            <button className="action-btn">Add Investment</button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;