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