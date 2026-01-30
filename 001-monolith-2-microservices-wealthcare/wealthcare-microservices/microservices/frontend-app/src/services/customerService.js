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