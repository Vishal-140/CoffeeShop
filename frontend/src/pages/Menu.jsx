import { useState } from 'react';
import { drinks } from '../data/menu';
import { orderApi } from '../services/api';
import { Coffee, User } from 'lucide-react';

export default function Menu() {
    const [customerType, setCustomerType] = useState('REGULAR');
    const [loading, setLoading] = useState(null);
    const [message, setMessage] = useState(null);

    const placeOrder = async (drink) => {
        setLoading(drink.id);
        setMessage(null);
        try {
            await orderApi.create({
                drinkType: drink.name,
                customerType: customerType,
            });
            setMessage({ type: 'success', text: `Ordered ${drink.name} successfully!` });
        } catch (error) {
            console.error("Order failed", error);
            setMessage({ type: 'error', text: 'Failed to place order. Try again.' });
        } finally {
            setLoading(null);
            setTimeout(() => setMessage(null), 3000);
        }
    };

    const customerTypes = ['REGULAR', 'GOLD', 'VIP', 'PREMIUM'];

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <div className="flex flex-col md:flex-row justify-between items-center mb-8 gap-4">
                <h1 className="text-3xl font-bold text-gray-900">Our Menu</h1>

                <div className="flex items-center gap-4 bg-white p-2 rounded-lg shadow-sm border border-gray-200 overflow-x-auto max-w-full">
                    <span className="flex items-center gap-2 text-gray-600 font-medium whitespace-nowrap">
                        <User className="h-5 w-5" /> Customer Type:
                    </span>
                    <div className="flex gap-2">
                        {customerTypes.map((type) => (
                            <button
                                key={type}
                                onClick={() => setCustomerType(type)}
                                className={`px-3 py-1.5 text-sm rounded-md transition ${customerType === type
                                        ? 'bg-amber-600 text-white'
                                        : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                                    }`}
                            >
                                {type.charAt(0) + type.slice(1).toLowerCase()}
                            </button>
                        ))}
                    </div>
                </div>
            </div>

            {message && (
                <div className={`mb-6 p-4 rounded-md ${message.type === 'success' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                    {message.text}
                </div>
            )}

            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
                {drinks.map((drink) => (
                    <div key={drink.id} className="bg-white rounded-lg shadow-sm overflow-hidden hover:shadow-md transition border border-gray-100">
                        <div className="p-4">
                            <div className="flex justify-between items-start mb-2">
                                <div className="p-2 bg-amber-50 rounded-full text-amber-600">
                                    <Coffee className="h-5 w-5" />
                                </div>
                                <span className="text-lg font-bold text-gray-900">₹{drink.price}</span>
                            </div>
                            <h3 className="text-lg font-bold text-gray-900 mb-1">{drink.name}</h3>
                            <p className="text-gray-500 mb-4 text-xs">Prep time: ~{drink.prepTime} min</p>

                            <button
                                onClick={() => placeOrder(drink)}
                                disabled={loading === drink.id}
                                className="w-full py-2 bg-amber-600 hover:bg-amber-700 text-white text-sm font-semibold rounded-md transition disabled:opacity-50 disabled:cursor-not-allowed flex justify-center items-center gap-2"
                            >
                                {loading === drink.id ? 'Ordering...' : 'Order'}
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}
