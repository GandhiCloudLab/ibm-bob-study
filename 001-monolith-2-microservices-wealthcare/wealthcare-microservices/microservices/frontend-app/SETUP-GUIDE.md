# Complete Setup Guide for Wealthcare React Frontend

This guide will walk you through setting up the complete React frontend application from scratch.

## Step-by-Step Setup

### Step 1: Create React Application

```bash
cd microservices
npx create-react-app frontend-app
cd frontend-app
```

This creates a new React application with all the necessary build tools and configuration.

### Step 2: Install Dependencies

```bash
npm install axios react-router-dom jwt-decode
```

**What these do:**
- `axios` - HTTP client for making API calls
- `react-router-dom` - Routing library for navigation
- `jwt-decode` - Decode JWT tokens

### Step 3: Create Directory Structure

```bash
mkdir -p src/components src/pages src/services src/context src/styles
```

### Step 4: Create Environment Configuration

Create `.env` file in the root of `frontend-app`:

```env
REACT_APP_USER_SERVICE_URL=http://localhost:8081
REACT_APP_CUSTOMER_SERVICE_URL=http://localhost:8082
REACT_APP_GOAL_SERVICE_URL=http://localhost:8083
REACT_APP_INVESTMENT_SERVICE_URL=http://localhost:8084
```

### Step 5: Create Core Files

I'll provide the complete code for each file. Create these files in your `src` directory:

## Core Application Files

### 1. src/index.js

```javascript
import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
```

### 2. src/App.jsx

```javascript
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Customers from './pages/Customers';
import Goals from './pages/Goals';
import Investments from './pages/Investments';
import PrivateRoute from './components/PrivateRoute';
import './styles/App.css';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/dashboard" element={
              <PrivateRoute>
                <Dashboard />
              </PrivateRoute>
            } />
            <Route path="/customers" element={
              <PrivateRoute>
                <Customers />
              </PrivateRoute>
            } />
            <Route path="/goals" element={
              <PrivateRoute>
                <Goals />
              </PrivateRoute>
            } />
            <Route path="/investments" element={
              <PrivateRoute>
                <Investments />
              </PrivateRoute>
            } />
            <Route path="/" element={<Navigate to="/login" />} />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
```

### 3. src/context/AuthContext.jsx

```javascript
import React, { createContext, useState, useContext, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check if user is logged in on mount
    const storedToken = localStorage.getItem('token');
    const storedUser = localStorage.getItem('user');
    
    if (storedToken && storedUser) {
      try {
        const decoded = jwtDecode(storedToken);
        // Check if token is expired
        if (decoded.exp * 1000 > Date.now()) {
          setToken(storedToken);
          setUser(JSON.parse(storedUser));
        } else {
          // Token expired, clear storage
          localStorage.removeItem('token');
          localStorage.removeItem('user');
        }
      } catch (error) {
        console.error('Invalid token:', error);
        localStorage.removeItem('token');
        localStorage.removeItem('user');
      }
    }
    setLoading(false);
  }, []);

  const login = (token, userData) => {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(userData));
    setToken(token);
    setUser(userData);
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
  };

  const value = {
    user,
    token,
    login,
    logout,
    isAuthenticated: !!token,
    loading
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
```

### 4. src/services/api.js

```javascript
import axios from 'axios';

const USER_SERVICE_URL = process.env.REACT_APP_USER_SERVICE_URL || 'http://localhost:8081';
const CUSTOMER_SERVICE_URL = process.env.REACT_APP_CUSTOMER_SERVICE_URL || 'http://localhost:8082';
const GOAL_SERVICE_URL = process.env.REACT_APP_GOAL_SERVICE_URL || 'http://localhost:8083';
const INVESTMENT_SERVICE_URL = process.env.REACT_APP_INVESTMENT_SERVICE_URL || 'http://localhost:8084';

// Create axios instances for each service
const userApi = axios.create({
  baseURL: USER_SERVICE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

const customerApi = axios.create({
  baseURL: CUSTOMER_SERVICE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

const goalApi = axios.create({
  baseURL: GOAL_SERVICE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

const investmentApi = axios.create({
  baseURL: INVESTMENT_SERVICE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Add token to requests
const addAuthToken = (config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
};

// Add interceptors to all instances
[userApi, customerApi, goalApi, investmentApi].forEach(api => {
  api.interceptors.request.use(addAuthToken);
  
  api.interceptors.response.use(
    response => response,
    error => {
      if (error.response?.status === 401) {
        // Token expired or invalid
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
      }
      return Promise.reject(error);
    }
  );
});

export { userApi, customerApi, goalApi, investmentApi };
```

### 5. src/services/authService.js

```javascript
import { userApi } from './api';

const authService = {
  login: async (username, password) => {
    const response = await userApi.post('/api/auth/login', {
      username,
      password
    });
    return response.data;
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  getCurrentUser: () => {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  },

  getToken: () => {
    return localStorage.getItem('token');
  }
};

export default authService;
```

### 6. src/services/customerService.js

```javascript
import { customerApi } from './api';

const customerService = {
  getAllCustomers: async () => {
    const response = await customerApi.get('/api/customers');
    return response.data;
  },

  getCustomerById: async (id) => {
    const response = await customerApi.get(`/api/customers/${id}`);
    return response.data;
  },

  createCustomer: async (customerData) => {
    const response = await customerApi.post('/api/customers', customerData);
    return response.data;
  },

  updateCustomer: async (id, customerData) => {
    const response = await customerApi.put(`/api/customers/${id}`, customerData);
    return response.data;
  },

  deleteCustomer: async (id) => {
    await customerApi.delete(`/api/customers/${id}`);
  }
};

export default customerService;
```

### 7. src/services/goalService.js

```javascript
import { goalApi } from './api';

const goalService = {
  getAllGoals: async () => {
    const response = await goalApi.get('/api/goals');
    return response.data;
  },

  getGoalById: async (id) => {
    const response = await goalApi.get(`/api/goals/${id}`);
    return response.data;
  },

  getGoalsByCustomerId: async (customerId) => {
    const response = await goalApi.get(`/api/goals/customer/${customerId}`);
    return response.data;
  },

  createGoal: async (goalData) => {
    const response = await goalApi.post('/api/goals', goalData);
    return response.data;
  },

  updateGoal: async (id, goalData) => {
    const response = await goalApi.put(`/api/goals/${id}`, goalData);
    return response.data;
  },

  deleteGoal: async (id) => {
    await goalApi.delete(`/api/goals/${id}`);
  },

  getGoalProgress: async (id) => {
    const response = await goalApi.get(`/api/goals/${id}/progress`);
    return response.data;
  }
};

export default goalService;
```

### 8. src/services/investmentService.js

```javascript
import { investmentApi } from './api';

const investmentService = {
  getAllInvestments: async () => {
    const response = await investmentApi.get('/api/investments');
    return response.data;
  },

  getInvestmentById: async (id) => {
    const response = await investmentApi.get(`/api/investments/${id}`);
    return response.data;
  },

  getInvestmentsByCustomerId: async (customerId) => {
    const response = await investmentApi.get(`/api/investments/customer/${customerId}`);
    return response.data;
  },

  getPortfolioSummary: async (customerId) => {
    const response = await investmentApi.get(`/api/investments/customer/${customerId}/portfolio-summary`);
    return response.data;
  },

  createInvestment: async (investmentData) => {
    const response = await investmentApi.post('/api/investments', investmentData);
    return response.data;
  },

  updateInvestment: async (id, investmentData) => {
    const response = await investmentApi.put(`/api/investments/${id}`, investmentData);
    return response.data;
  },

  deleteInvestment: async (id) => {
    await investmentApi.delete(`/api/investments/${id}`);
  }
};

export default investmentService;
```

### 9. src/components/PrivateRoute.jsx

```javascript
import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const PrivateRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return <div>Loading...</div>;
  }

  return isAuthenticated ? children : <Navigate to="/login" />;
};

export default PrivateRoute;
```

### 10. src/components/Navbar.jsx

```javascript
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
```

## Page Components

### 11. src/pages/Login.jsx

```javascript
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import authService from '../services/authService';
import '../styles/Login.css';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await authService.login(username, password);
      login(response.token, response.user);
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid username or password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <div className="login-header">
          <h1>💰 Wealthcare</h1>
          <p>Wealth Management Platform</p>
        </div>
        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <label>Username</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Enter username"
              required
              disabled={loading}
            />
          </div>
          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter password"
              required
              disabled={loading}
            />
          </div>
          {error && <div className="error-message">{error}</div>}
          <button type="submit" className="login-btn" disabled={loading}>
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>
        <div className="login-footer">
          <p>Demo Credentials:</p>
          <p><small>Create a user via API first</small></p>
        </div>
      </div>
    </div>
  );
};

export default Login;
```

### 12. src/pages/Dashboard.jsx

```javascript
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
```

### 13. src/pages/Customers.jsx

```javascript
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
```

### 14. src/pages/Goals.jsx

```javascript
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
```

### 15. src/pages/Investments.jsx

```javascript
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
```

## CSS Files

Due to length constraints, I'll provide a link to download all CSS files or you can create basic styles. Here's a starter CSS:

### src/styles/App.css

```css
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen',
    'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue',
    sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background-color: #f5f5f5;
}

.App {
  min-height: 100vh;
}

button {
  cursor: pointer;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  font-size: 14px;
  transition: all 0.3s;
}

button:hover {
  opacity: 0.8;
}

.error-message {
  background-color: #fee;
  color: #c00;
  padding: 10px;
  border-radius: 4px;
  margin: 10px 0;
}
```

## Next Steps

1. **Copy all the code above** into the respective files
2. **Create the CSS files** (I can provide complete CSS if needed)
3. **Enable CORS in backend** (add WebConfig.java to each service)
4. **Run the application**: `npm start`
5. **Create a test user** via backend API
6. **Login and test** the application

## Running the Complete Application

```bash
# Terminal 1 - Start backend services
cd microservices/user-service && mvn spring-boot:run

# Terminal 2
cd microservices/customer-service && mvn spring-boot:run

# Terminal 3
cd microservices/goal-service && mvn spring-boot:run

# Terminal 4
cd microservices/investment-service && mvn spring-boot:run

# Terminal 5 - Start frontend
cd microservices/frontend-app && npm start
```

The application will open at **http://localhost:3000** with a login page!

Would you like me to provide the complete CSS files or help with any specific component?