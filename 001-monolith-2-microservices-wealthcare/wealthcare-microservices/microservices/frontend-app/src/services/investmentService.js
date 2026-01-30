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