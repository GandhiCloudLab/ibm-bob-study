import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import investmentService from '../services/investmentService';
import '../styles/Investments.css';

const Investments = () => {
  const [investments, setInvestments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadInvestments();
  }, []);

  const loadInvestments = async () => {
    try {
      const data = await investmentService.getAllInvestments();
      setInvestments(data);
    } catch (err) {
      setError('Failed to load investments');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div>
        <Navbar />
        <div className="investments-container">
          <p>Loading investments...</p>
        </div>
      </div>
    );
  }

  return (
    <div>
      <Navbar />
      <div className="investments-container">
        <div className="page-header">
          <h1>Investments</h1>
          <button className="add-btn">+ Add Investment</button>
        </div>

        {error && <div className="error-message">{error}</div>}

        <div className="investments-table">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Type</th>
                <th>Symbol</th>
                <th>Quantity</th>
                <th>Purchase Price</th>
                <th>Current Price</th>
                <th>Gain/Loss</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {investments.length === 0 ? (
                <tr>
                  <td colSpan="9" style={{textAlign: 'center'}}>
                    No investments found. Create one via API first.
                  </td>
                </tr>
              ) : (
                investments.map(investment => (
                  <tr key={investment.id}>
                    <td>{investment.id}</td>
                    <td>{investment.investmentName}</td>
                    <td>{investment.investmentType}</td>
                    <td>{investment.symbol}</td>
                    <td>{investment.quantity}</td>
                    <td>₹{investment.purchasePrice}</td>
                    <td>₹{investment.currentPrice || investment.purchasePrice}</td>
                    <td className={investment.gainLoss >= 0 ? 'positive' : 'negative'}>
                      ₹{investment.gainLoss?.toFixed(2) || '0.00'}
                    </td>
                    <td>{investment.status}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Investments;