import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081/api/auth';

const authService = {
    login: async (username, password) => {
        try {
            const response = await axios.post(`${API_BASE_URL}/login`, {
                username,
                password
            });

            console.log(".."+response.data)
            return response.data;
        } catch (error) {
            throw error.response?.data || 'Login failed';
        }
    },

    validateToken: async (token) => {
        try {
            const response = await axios.get(`${API_BASE_URL}/verify`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            return response.data;
        } catch (error) {
            throw error.response?.data || 'Token validation failed';
        }
    }
};

export default authService;