import React, { useState } from 'react';
import { simulationApi } from '../services/api';
import { BarChart, Clock, AlertTriangle, Coffee } from 'lucide-react';

const SimulationPage = () => {
    const [seed, setSeed] = useState(10);
    const [loading, setLoading] = useState(false);
    const [results, setResults] = useState(null);
    const [error, setError] = useState(null);

    const runSimulation = async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await simulationApi.run(seed);
            setResults(response.data);
        } catch (err) {
            console.error(err);
            setError("Failed to run simulation. Ensure backend is running.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container mx-auto px-4 py-8">
            <h1 className="text-3xl font-bold mb-6 text-gray-800">Coffee Shop Simulation</h1>

            {/* Controls */}
            <div className="bg-white p-6 rounded-lg shadow-md mb-8 flex items-end gap-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        Simulation Seed (Scenario ID)
                    </label>
                    <input
                        type="number"
                        value={seed}
                        onChange={(e) => setSeed(e.target.value)}
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-amber-500"
                    />
                </div>
                <button
                    onClick={runSimulation}
                    disabled={loading}
                    className="px-6 py-2 bg-amber-600 text-white font-bold rounded-md hover:bg-amber-700 transition disabled:opacity-50 flex items-center gap-2"
                >
                    {loading ? 'Running...' : 'Run Simulation'}
                </button>
            </div>

            {error && (
                <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 mb-8" role="alert">
                    <p>{error}</p>
                </div>
            )}

            {results && (
                <div className="space-y-8 animate-fade-in">
                    {/* Summary Metrics */}
                    <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
                        <div className="bg-white p-6 rounded-lg shadow-md border-t-4 border-blue-500">
                            <div className="flex items-center justify-between">
                                <div>
                                    <p className="text-gray-500 text-sm">Total Orders</p>
                                    <h3 className="text-2xl font-bold">{results.totalOrders}</h3>
                                </div>
                                <Coffee className="text-blue-500" />
                            </div>
                        </div>
                        <div className="bg-white p-6 rounded-lg shadow-md border-t-4 border-green-500">
                            <div className="flex items-center justify-between">
                                <div>
                                    <p className="text-gray-500 text-sm">Avg Wait Time</p>
                                    <h3 className="text-2xl font-bold">{results.averageWaitTime}</h3>
                                </div>
                                <Clock className="text-green-500" />
                            </div>
                        </div>
                        <div className="bg-white p-6 rounded-lg shadow-md border-t-4 border-red-500">
                            <div className="flex items-center justify-between">
                                <div>
                                    <p className="text-gray-500 text-sm">Abandoned</p>
                                    <h3 className="text-2xl font-bold">{results.abandonedCount}</h3>
                                </div>
                                <AlertTriangle className="text-red-500" />
                            </div>
                        </div>
                        <div className="bg-white p-6 rounded-lg shadow-md border-t-4 border-orange-500">
                            <div className="flex items-center justify-between">
                                <div>
                                    <p className="text-gray-500 text-sm">Abandon Rate</p>
                                    <h3 className="text-2xl font-bold">{results.abandonedRate}</h3>
                                </div>
                                <AlertTriangle className="text-orange-500" />
                            </div>
                        </div>
                    </div>

                    {/* Barista Workloads */}
                    <div className="bg-white p-6 rounded-lg shadow-md">
                        <h2 className="text-xl font-bold mb-4 flex items-center gap-2">
                            <BarChart className="h-5 w-5" /> Barista Workloads
                        </h2>
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                            {results.baristas.map(b => (
                                <div key={b.id} className="bg-gray-50 p-4 rounded border text-center">
                                    <h4 className="font-bold text-gray-700">Barista {b.id}</h4>
                                    <p className="text-2xl font-bold text-amber-600">{b.workload}</p>
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* Order Details Table */}
                    <div className="bg-white p-6 rounded-lg shadow-md overflow-hidden">
                        <h2 className="text-xl font-bold mb-4">Order Details</h2>
                        <div className="overflow-x-auto max-h-96 overflow-y-auto">
                            <table className="min-w-full divide-y divide-gray-200">
                                <thead className="bg-gray-50 sticky top-0">
                                    <tr>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Time</th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Drink</th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Customer</th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Barista</th>
                                    </tr>
                                </thead>
                                <tbody className="bg-white divide-y divide-gray-200">
                                    {results.orders.map(order => (
                                        <tr key={order.id} className="hover:bg-gray-50">
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{order.id}</td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                                {new Date(order.arrivalTime).toLocaleTimeString()}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{order.drinkType}</td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                                <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full 
                                                    ${order.customerType === 'VIP' ? 'bg-purple-100 text-purple-800' :
                                                        order.customerType === 'GOLD' ? 'bg-yellow-100 text-yellow-800' :
                                                            'bg-gray-100 text-gray-800'}`}>
                                                    {order.customerType}
                                                </span>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm">
                                                <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full 
                                                    ${order.status === 'DONE' ? 'bg-green-100 text-green-800' :
                                                        order.status === 'ABANDONED' ? 'bg-red-100 text-red-800' :
                                                            'bg-yellow-100 text-yellow-800'}`}>
                                                    {order.status}
                                                </span>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                                {order.baristaId ? `Barista ${order.baristaId}` : '-'}
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default SimulationPage;
