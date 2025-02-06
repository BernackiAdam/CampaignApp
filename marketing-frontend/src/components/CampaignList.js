import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

export default function CampaignList() {
  const [campaigns, setCampaigns] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Pobranie kampanii z API
  const fetchCampaigns = () => {
    fetch("http://localhost:8080/api/campaigns")
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to fetch campaigns");
        }
        return response.json();
      })
      .then((data) => {
        setCampaigns(data);
        setLoading(false);
      })
      .catch((error) => {
        setError(error.message);
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchCampaigns();
  }, []);

  // Usuwanie kampanii
  const handleDelete = (id) => {
    if (window.confirm("Czy na pewno chcesz usunąć tę kampanię?")) {
      fetch(`http://localhost:8080/api/campaigns/delete/${id}`, {
        method: "DELETE",
      })
        .then((response) => {
          if (!response.ok) {
            throw new Error("Failed to delete campaign");
          }
          fetchCampaigns();
        })
        .catch((error) => {
          setError(error.message);
        });
    }
  };

  if (loading) return <p>Loading campaigns...</p>;
  if (error) return <p>Error: {error}</p>;

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1>Campaigns</h1>
        <Link to="/add" className="btn btn-success">
          Add new Campaign
        </Link>
      </div>

      <table className="table table-striped table-hover">
        <thead className="table-dark">
          <tr>
            <th>ID</th>
            <th>Campaign Name</th>
            <th>Bid Amount</th>
            <th>Funds</th>
            <th>Status</th>
            <th>Town</th>
            <th>Radius (km)</th>
            <th>Keywords</th>
            <th>Actions</th> {/* Kolumna na akcje */}
          </tr>
        </thead>
        <tbody>
          {campaigns.map((campaign) => (
            <tr key={campaign.id}>
              <td>{campaign.id}</td>
              <td>{campaign.campaignName}</td>
              <td>{campaign.bidAmount}</td>
              <td>{campaign.campaignFunds}</td>
              <td>{campaign.status ? "Active" : "Inactive"}</td>
              <td>{campaign.town?.townName || "Brak miasta"}</td>
              <td>{campaign.radius}</td>
              <td>
                {campaign.keywordList && campaign.keywordList.length > 0
                  ? campaign.keywordList.map((k, index) => (
                      <span key={index} className="badge bg-primary me-1">
                        {k.content}
                      </span>
                    ))
                  : "No keywords"}
              </td>
              <td>
                {/* Przycisk Edit */}
                <Link to={`/edit/${campaign.id}`} className="btn btn-warning btn-sm me-2">
                  Edit
                </Link>

                {/* Przycisk Delete */}
                <button className="btn btn-danger btn-sm" onClick={() => handleDelete(campaign.id)}>
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
