import { useState, useEffect, useRef } from 'react';
import { orderApi } from '../services/api';
import { Clock, CheckCircle, Loader, Flame, Users, XCircle } from 'lucide-react';
import toast, { Toaster } from 'react-hot-toast';

export default function Dashboard() {
    const [waiting, setWaiting] = useState([]);
    const [inProgress, setInProgress] = useState([]);
    const [done, setDone] = useState([]);
    const [abandoned, setAbandoned] = useState([]);
    const prevWaitingLength = useRef(0);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [waitingData, inProgressData, doneData, abandonedData] = await Promise.all([
                    orderApi.getWaiting(),
                    orderApi.getInProgress(),
                    orderApi.getDone(),
                    orderApi.getAbandoned(),
                ]);

                // Check for new orders
                if (waitingData.data.length > prevWaitingLength.current) {
                    const newCount = waitingData.data.length - prevWaitingLength.current
                    if (prevWaitingLength.current > 0 || (waitingData.data.length > 0 && prevWaitingLength.current === 0)) {
                    }
                    if (waitingData.data.length > 0 && prevWaitingLength.current !== 0) {
                        toast.success(`New Order Received!`, {
                            icon: '☕',
                            style: {
                                borderRadius: '10px',
                                background: '#333',
                                color: '#fff',
                            },
                        });
                    }
                }
                prevWaitingLength.current = waitingData.data.length;

                setWaiting(waitingData.data);
                setInProgress(inProgressData.data);
                setDone(doneData.data);
                setAbandoned(abandonedData.data);
            } catch (error) {
                console.error("Failed to fetch dashboard data", error);
            }
        };

        fetchData();
        const interval = setInterval(fetchData, 2000); // Poll every 2 seconds
        return () => clearInterval(interval);
    }, []);

    const PriorityBadge = ({ score }) => {
        let color = "bg-gray-100 text-gray-800";
        if (score > 40) color = "bg-red-100 text-red-800";
        else if (score > 20) color = "bg-yellow-100 text-yellow-800";
        else if (score > 10) color = "bg-blue-100 text-blue-800";

        return (
            <span className={`px-2 py-1 rounded-full text-xs font-bold ${color}`}>
                {score.toFixed(1)}
            </span>
        );
    };

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <Toaster position="top-right" reverseOrder={false} />
            <h1 className="text-3xl font-bold text-gray-900 mb-8 flex items-center gap-3">
                <Clock className="w-8 h-8 text-amber-600" /> Live Order Queue
            </h1>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">

                {/* Waiting Column */}
                <div className="bg-white rounded-xl shadow-md overflow-hidden border border-gray-100">
                    <div className="bg-red-50 p-3 border-b border-red-100 flex justify-between items-center">
                        <h2 className="text-lg font-bold text-red-800 flex items-center gap-2">
                            <Flame className="w-5 h-5" /> Waiting
                        </h2>
                        <span className="bg-white px-2 py-0.5 rounded text-red-600 font-bold text-sm shadow-sm">
                            {waiting.length}
                        </span>
                    </div>
                    <div className="p-3 space-y-3 max-h-[70vh] overflow-y-auto">
                        {waiting.length === 0 ? (
                            <p className="text-gray-400 text-sm text-center py-4">No orders waiting.</p>
                        ) : (
                            waiting.map((order) => (
                                <div key={order.id} className="p-3 bg-white border border-gray-100 rounded-lg shadow-sm hover:shadow-md transition">
                                    <div className="flex justify-between items-start mb-1">
                                        <h3 className="font-bold text-gray-900 text-sm">#{order.id.toString().slice(-4)}</h3>
                                        <PriorityBadge score={order.priorityScore} />
                                    </div>
                                    <div className="flex justify-between text-xs text-gray-600">
                                        <span>{order.drinkType}</span>
                                        <span className="flex items-center gap-1">
                                            <Users className="w-3 h-3" /> {order.customerType}
                                        </span>
                                    </div>
                                    <div className="mt-1 text-[10px] text-gray-400">
                                        {Math.floor((new Date() - new Date(order.arrivalTime)) / 60000)}m ago
                                    </div>
                                </div>
                            ))
                        )}
                    </div>
                </div>

                {/* In Progress Column */}
                <div className="bg-white rounded-xl shadow-md overflow-hidden border border-gray-100">
                    <div className="bg-blue-50 p-3 border-b border-blue-100 flex justify-between items-center">
                        <h2 className="text-lg font-bold text-blue-800 flex items-center gap-2">
                            <Loader className="w-5 h-5 animate-spin" /> Processing
                        </h2>
                        <span className="bg-white px-2 py-0.5 rounded text-blue-600 font-bold text-sm shadow-sm">
                            {inProgress.length}
                        </span>
                    </div>
                    <div className="p-3 space-y-3 max-h-[70vh] overflow-y-auto">
                        {inProgress.map((order) => (
                            <div key={order.id} className="p-3 bg-blue-50/50 border border-blue-100 rounded-lg shadow-sm">
                                <div className="flex justify-between items-start mb-1">
                                    <h3 className="font-bold text-gray-900 text-sm">#{order.id.toString().slice(-4)}</h3>
                                    <span className="text-[10px] bg-blue-200 text-blue-800 px-1.5 py-0.5 rounded-full">Barista {order.baristaId}</span>
                                </div>
                                <div className="text-xs text-gray-700 font-medium">
                                    {order.drinkType}
                                </div>
                                <div className="mt-2 w-full bg-gray-200 rounded-full h-1">
                                    <div className="bg-blue-600 h-1 rounded-full animate-pulse" style={{ width: '60%' }}></div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Done Column */}
                <div className="bg-white rounded-xl shadow-md overflow-hidden border border-gray-100">
                    <div className="bg-green-50 p-3 border-b border-green-100 flex justify-between items-center">
                        <h2 className="text-lg font-bold text-green-800 flex items-center gap-2">
                            <CheckCircle className="w-5 h-5" /> Ready
                        </h2>
                        <span className="bg-white px-2 py-0.5 rounded text-green-600 font-bold text-sm shadow-sm">
                            {done.length}
                        </span>
                    </div>
                    <div className="p-3 space-y-3 max-h-[70vh] overflow-y-auto">
                        {done.map((order) => (
                            <div key={order.id} className="p-3 bg-green-50/50 border border-green-100 rounded-lg shadow-sm opacity-80">
                                <div className="flex justify-between items-start mb-1">
                                    <h3 className="font-bold text-gray-900 text-sm line-through">#{order.id.toString().slice(-4)}</h3>
                                    <span className="text-[10px] text-green-700 font-bold">DONE</span>
                                </div>
                                <div className="text-xs text-gray-600">
                                    {order.drinkType}
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Abandoned Column */}
                <div className="bg-white rounded-xl shadow-md overflow-hidden border border-gray-100">
                    <div className="bg-gray-50 p-3 border-b border-gray-200 flex justify-between items-center">
                        <h2 className="text-lg font-bold text-gray-600 flex items-center gap-2">
                            <XCircle className="w-5 h-5" /> Abandoned
                        </h2>
                        <span className="bg-white px-2 py-0.5 rounded text-gray-600 font-bold text-sm shadow-sm">
                            {abandoned.length}
                        </span>
                    </div>
                    <div className="p-3 space-y-3 max-h-[70vh] overflow-y-auto">
                        {abandoned.map((order) => (
                            <div key={order.id} className="p-3 bg-gray-50 border border-gray-200 rounded-lg shadow-sm opacity-60">
                                <div className="flex justify-between items-start mb-1">
                                    <h3 className="font-bold text-gray-700 text-sm">#{order.id.toString().slice(-4)}</h3>
                                    <span className="text-[10px] text-red-500 font-bold">LEFT</span>
                                </div>
                                <div className="text-xs text-gray-500">
                                    {order.drinkType} ({Math.floor((new Date() - new Date(order.arrivalTime)) / 60000)}m)
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

            </div>
        </div>
    );
}
