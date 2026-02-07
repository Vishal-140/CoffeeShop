import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import Menu from './pages/Menu';
import Dashboard from './pages/Dashboard';
import SimulationPage from './pages/SimulationPage';

import Navbar from './components/Navbar';


function App() {
  return (
    <Router>
      <div className="min-h-screen bg-gray-50 text-gray-900 font-sans">
        <div className="min-h-screen bg-gray-50 flex flex-col">
          <Navbar />
          <div className="flex-1">
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/menu" element={<Menu />} />
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/simulation" element={<SimulationPage />} />
            </Routes>
          </div>
        </div>
      </div>
    </Router>
  );
}

export default App;
