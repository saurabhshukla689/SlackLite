import { useMutation, useQuery } from '@apollo/client/react';
import { LOGIN_MUTATION, GET_ME_QUERY, LOGOUT_MUTATION } from '../graphql/queries';

// Custom hook for login
export const useLogin = () => {
    const [loginMutation, { loading, error }] = useMutation(LOGIN_MUTATION);

    const login = async (username, password) => {
        try {
            const { data } = await loginMutation({
                variables: { username, password }
            });

            // Store token
            localStorage.setItem('token', data.login.token);
            localStorage.setItem('user', JSON.stringify(data.login.user));

            return data.login;
        } catch (error) {
            throw error;
        }
    };

    return { login, loading, error };
};

// Custom hook for token validation
export const useValidateToken = () => {
    const { data, loading, error } = useQuery(GET_ME_QUERY, {
        skip: !localStorage.getItem('token')
    });

    return {
        user: data?.me,
        isValid: !!data?.me,
        loading,
        error
    };
};

// Custom hook for logout
export const useLogout = () => {
    const [logoutMutation] = useMutation(LOGOUT_MUTATION);

    const logout = async () => {
        try {
            await logoutMutation();
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            return true;
        } catch (error) {
            console.error('Logout error:', error);
            return false;
        }
    };

    return { logout };
};