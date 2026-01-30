import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import goalService from '../services/goalService';
import '../styles/Goals.css';

const Goals = () => {
  const [goals, setGoals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadGoals();
  }, []);

  const loadGoals = async () => {
    try {
      const data = await goalService.getAllGoals();
      setGoals(data);
    } catch (err) {
      setError('Failed to load goals');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div>
        <Navbar />
        <div className="goals-container">
          <p>Loading goals...</p>
        </div>
      </div>
    );
  }

  return (
    <div>
      <Navbar />
      <div className="goals-container">
        <div className="page-header">
          <h1>Financial Goals</h1>
          <button className="add-btn">+ Add Goal</button>
        </div>

        {error && <div className="error-message">{error}</div>}

        <div className="goals-grid">
          {goals.length === 0 ? (
            <p>No goals found. Create one via API first.</p>
          ) : (
            goals.map(goal => (
              <div key={goal.id} className="goal-card">
                <h3>{goal.goalName}</h3>
                <p className="goal-type">{goal.goalType}</p>
                <div className="goal-progress">
                  <div className="progress-bar">
                    <div 
                      className="progress-fill" 
                      style={{width: `${(goal.currentAmount / goal.targetAmount) * 100}%`}}
                    ></div>
                  </div>
                  <p>{goal.currentAmount} / {goal.targetAmount}</p>
                </div>
                <p className="goal-status">{goal.status}</p>
                <p className="goal-date">Target: {goal.targetDate}</p>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default Goals;