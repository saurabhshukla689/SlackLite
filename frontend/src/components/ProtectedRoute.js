import React from 'react';
import { Navigate } from 'react-router-dom';
import { useQuery } from '@apollo/client/react';
import { GET_ME_QUERY } from '../graphql/queries';

const ProtectedRoute = ({ children }) => {
    const token = localStorage.getItem('token');
    const { data, loading, error } = useQuery(GET_ME_QUERY, {
        skip: !token,
        errorPolicy: 'all'
    });

    if (!token) {
        return <Navigate to="/login" />;
    }

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error || !data?.me) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        return <Navigate to="/login" />;
    }

    return children;
};

export default ProtectedRoute;