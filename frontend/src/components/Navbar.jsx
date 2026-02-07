import { Link, useLocation } from 'react-router-dom';
import { Coffee, ClipboardList, LayoutDashboard } from 'lucide-react';

export default function Navbar() {
    const location = useLocation();

    const isActive = (path) => {
        return location.pathname === path ? "bg-amber-700 text-white" : "text-amber-100 hover:bg-amber-800 hover:text-white";
    };

    return (
        <nav className="bg-amber-900 text-white shadow-lg">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex items-center justify-between h-16">
                    <div className="flex items-center">
                        <Link to="/" className="flex items-center gap-2 font-bold text-xl">
                            <Coffee className="h-8 w-8 text-amber-300" />
                            <span>Coffee Shop Barista</span>
                        </Link>
                    </div>
                    <div className="hidden md:block">
                        <div className="ml-10 flex items-baseline space-x-4">
                            <Link to="/" className={`px-3 py-2 rounded-md text-sm font-medium transition ${isActive('/')}`}>
                                Home
                            </Link>
                            <Link to="/menu" className={`px-3 py-2 rounded-md text-sm font-medium transition flex items-center gap-2 ${isActive('/menu')}`}>
                                <ClipboardList className="h-4 w-4" />
                                Order
                            </Link>
                            <Link to="/dashboard" className={`px-3 py-2 rounded-md text-sm font-medium transition flex items-center gap-2 ${isActive('/dashboard')}`}>
                                <LayoutDashboard className="h-4 w-4" />
                                Queue
                            </Link>
                        </div>
                    </div>
                </div>
            </div>
        </nav>
    );
}
