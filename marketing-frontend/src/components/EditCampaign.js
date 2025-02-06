import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Typeahead } from "react-bootstrap-typeahead";
import "react-bootstrap-typeahead/css/Typeahead.css";

export default function EditCampaign() {
  const navigate = useNavigate();
  const { id } = useParams();

  const [formData, setFormData] = useState({
    campaignName: "",
    bidAmount: "",
    status: false,
    radius: "",
  });
  const [availableKeywords, setAvailableKeywords] = useState([]);
  const [selectedKeywords, setSelectedKeywords] = useState([]);
  const [availableTowns, setAvailableTowns] = useState([]);
  const [selectedTown, setSelectedTown] = useState(null);
  const [error, setError] = useState(null);
  const [validationErrors, setValidationErrors] = useState({});

  useEffect(() => {
    fetch("http://localhost:8080/api/keywords")
      .then(response => response.json())
      .then(setAvailableKeywords)
      .catch(err => console.error("Error fetching keywords:", err));
  }, []);

  useEffect(() => {
    fetch("http://localhost:8080/api/towns")
      .then(response => response.json())
      .then(setAvailableTowns)
      .catch(err => console.error("Error fetching towns:", err));
  }, []);

  useEffect(() => {
    fetch(`http://localhost:8080/api/campaigns/${id}`)
      .then(response => response.json())
      .then(data => {
        setFormData({
          campaignName: data.campaignName,
          bidAmount: data.bidAmount,
          status: data.status,
          radius: data.radius,
        });
        setSelectedTown(data.town);
        setSelectedKeywords(data.keywordList || []);
      })
      .catch(err => console.error("Error fetching campaign:", err));
  }, [id]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleTownChange = (e) => {
    const townId = parseInt(e.target.value, 10);
    const town = availableTowns.find(t => t.id === townId) || null;
    setSelectedTown(town);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setError(null);
    setValidationErrors({});

    if (!selectedTown) {
      setValidationErrors(prev => ({ ...prev, town: "Wybierz miasto" }));
      return;
    }

    const updatedCampaign = {
      campaignName: formData.campaignName,
      bidAmount: parseFloat(formData.bidAmount),
      status: formData.status,
      radius: parseFloat(formData.radius),
      town: selectedTown, // Backend oczekuje obiektu town
      keywordList: selectedKeywords, // Backend oczekuje listy obiektów Keyword
    };

    fetch(`http://localhost:8080/api/campaigns/edit/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(updatedCampaign),
    })
      .then(response => {
        if (!response.ok) {
          return response.json().then(errData => {
            throw new Error(JSON.stringify(errData.errors));
          });
        }
        navigate("/");
      })
      .catch(err => {
        setError("Failed to update campaign");
        setValidationErrors(JSON.parse(err.message) || {});
      });
  };

  return (
    <div>
      <h1 className="mb-4">Edit Campaign</h1>
      {error && <div className="alert alert-danger">{error}</div>}
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label htmlFor="campaignName" className="form-label">Campaign Name</label>
          <input type="text" className={`form-control ${validationErrors.campaignName ? "is-invalid" : ""}`} id="campaignName" name="campaignName" value={formData.campaignName} onChange={handleChange} required />
          {validationErrors.campaignName && <div className="invalid-feedback">{validationErrors.campaignName}</div>}
        </div>

        <div className="mb-3">
          <label htmlFor="bidAmount" className="form-label">Bid Amount</label>
          <input type="number" step="0.01" className={`form-control ${validationErrors.bidAmount ? "is-invalid" : ""}`} id="bidAmount" name="bidAmount" value={formData.bidAmount} onChange={handleChange} required />
          {validationErrors.bidAmount && <div className="invalid-feedback">{validationErrors.bidAmount}</div>}
        </div>

        <div className="form-check mb-3">
          <input type="checkbox" className="form-check-input" id="status" name="status" checked={formData.status} onChange={handleChange} />
          <label htmlFor="status" className="form-check-label">Active</label>
        </div>

        <div className="mb-3">
          <label htmlFor="town" className="form-label">Town</label>
          <select id="town" name="town" className={`form-select ${validationErrors.town ? "is-invalid" : ""}`} value={selectedTown ? String(selectedTown.id) : ""} onChange={handleTownChange} required>
            <option value="">Wybierz miasto</option>
            {availableTowns.map(town => <option key={town.id} value={String(town.id)}>{town.townName}</option>)}
          </select>
          {validationErrors.town && <div className="invalid-feedback">{validationErrors.town}</div>}
        </div>

        <div className="mb-3">
          <label className="form-label">Keywords</label>
          <Typeahead
            id="keywords"
            multiple
            onChange={setSelectedKeywords}
            options={availableKeywords}
            selected={selectedKeywords}
            labelKey="content"
            placeholder="Wybierz słowa kluczowe..."
          />
        </div>

        <button type="submit" className="btn btn-primary">Update Campaign</button>
      </form>
    </div>
  );
}
