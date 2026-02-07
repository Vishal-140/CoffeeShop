import { Link } from 'react-router-dom';
import { Coffee, ArrowRight } from 'lucide-react';

export default function Home() {
    return (
        <div className="min-h-[calc(100vh-64px)] flex flex-col items-center justify-center bg-gradient-to-br from-amber-50 to-orange-100 p-4 text-center">

            <div className="bg-white p-8 rounded-2xl shadow-xl max-w-2xl w-full border border-amber-100">
                <div className="flex justify-center mb-6">
                    <div className="p-4 bg-amber-100 rounded-full">
                        <Coffee className="w-16 h-16 text-amber-700" />
                    </div>
                </div>

                <h1 className="text-5xl font-extrabold text-gray-900 mb-6 tracking-tight">
                    Coffee Shop Barista
                </h1>

                <p className="text-xl text-gray-600 mb-8 leading-relaxed">
                    Experience the perfect blend of efficiency and taste.
                    Our smart priority system ensures your coffee is ready exactly when you need it.
                </p>

                <div className="flex flex-col sm:flex-row gap-4 justify-center">
                    <Link
                        to="/menu"
                        className="px-8 py-4 bg-amber-600 text-white text-lg font-bold rounded-xl shadow-lg hover:bg-amber-700 hover:shadow-xl transition transform hover:-translate-y-1 flex items-center justify-center gap-2"
                    >
                        Order Now <ArrowRight className="w-5 h-5" />
                    </Link>

                    <Link
                        to="/dashboard"
                        className="px-8 py-4 bg-white text-amber-800 text-lg font-bold rounded-xl shadow-md border border-amber-200 hover:bg-amber-50 hover:shadow-lg transition transform hover:-translate-y-1"
                    >
                        View Live Queue
                    </Link>
                </div>
            </div>

        </div>
    );
}
