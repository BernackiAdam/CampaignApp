import './App.css';
import React from 'react';
import CampaignList from './components/CampaignList';
import EditCampaign from './components/EditCampaign';
import 'bootstrap/dist/css/bootstrap.min.css';
import { Route,Routes, BrowserRouter } from 'react-router-dom';
import AddCampaign from './components/AddCampaign';


function App() {
  return (
    <BrowserRouter>
      <div className="container mx-auto p-4">
        <h1 className="text-2xl font-bold mb-4">Marketing Campaigns</h1>
        <Routes>
            <Route path="/" element={<CampaignList />} />
            <Route path="/add" element={<AddCampaign />} />
            <Route path="/edit/:id" element={<EditCampaign />} />
          </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;
