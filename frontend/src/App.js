import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ApolloProvider } from '@apollo/client/react';
import client from './apollo/client';
import Login from './components/Login';
import Dashboard from './components/Dashboard';
import ProtectedRoute from './components/ProtectedRoute';import {InMemoryCache, createHttpLink } from '@apollo/client/react';
import { setContext } from '@apollo/client/link/context';


function App() {
    return (
        <ApolloProvider client={client}>
            <Router>
                <div className="App">
                    <Routes>
                        {/* Public Routes */}
                        <Route path="/login" element={<Login />} />

                        {/* Protected Routes */}
                        <Route
                            path="/dashboard"
                            element={
                                <ProtectedRoute>
                                    <Dashboard />
                                </ProtectedRoute>
                            }
                        />

                        {/* Default redirect */}
                        <Route path="/" element={<Navigate to="/login" />} />
                    </Routes>
                </div>
            </Router>
        </ApolloProvider>
    );
}

export default App;