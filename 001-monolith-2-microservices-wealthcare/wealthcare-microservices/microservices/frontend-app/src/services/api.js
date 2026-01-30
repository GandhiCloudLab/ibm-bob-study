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