import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import customerService from '../services/customerService';
import '../styles/Customers.css';

const Customers = () => {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadCustomers();
  }, []);

  const loadCustomers = async () => {
    try {
      const data = await customerService.getAllCustomers();
      setCustomers(data);
    } catch (err) {
      setError('Failed to load customers');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div>
        <Navbar />
        <div className="customers-container">
          <p>Loading customers...</p>
        </div>
      </div>
    );
  }

  return (
    <div>
      <Navbar />
      <div className="customers-container">
        <div className="page-header">
          <h1>Customers</h1>
          <button className="add-btn">+ Add Customer</button>
        </div>

        {error && <div className="error-message">{error}</div>}

        <div className="customers-table">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>City</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {customers.length === 0 ? (
                <tr>
                  <td colSpan="6" style={{textAlign: 'center'}}>
                    No customers found. Create one via API first.
                  </td>
                </tr>
              ) : (
                customers.map(customer => (
                  <tr key={customer.id}>
                    <td>{customer.id}</td>
                    <td>{customer.firstName} {customer.lastName}</td>
                    <td>{customer.emailId}</td>
                    <td>{customer.phoneNumber}</td>
                    <td>{customer.city}</td>
                    <td>
                      <button className="btn-small">View</button>
                      <button className="btn-small">Edit</button>
                    </td>
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

export default Customers;