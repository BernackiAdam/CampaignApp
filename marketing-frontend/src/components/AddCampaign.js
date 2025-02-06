import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Typeahead } from "react-bootstrap-typeahead";
import "react-bootstrap-typeahead/css/Typeahead.css";

export default function AddCampaign() {
  const navigate = useNavigate();

  // Stan pól formularza (bez pola dla town – obsługiwane oddzielnie)
  const [formData, setFormData] = useState({
    campaignName: "",
    bidAmount: "",
    status: false,
    radius: "",
  });

  // Stan dla dostępnych słów kluczowych pobranych z API
  const [availableKeywords, setAvailableKeywords] = useState([]);
  // Stan dla wybranych słów kluczowych (tablica obiektów, np. { id, content })
  const [selectedKeywords, setSelectedKeywords] = useState([]);

  // Stan dla dostępnych miast pobranych z API
  const [availableTowns, setAvailableTowns] = useState([]);
  // Stan dla wybranego miasta – obiekt (np. { id: 2, townName: "Kraków" })
  const [selectedTown, setSelectedTown] = useState(null);

  // Błędy – ogólne i walidacyjne
  const [error, setError] = useState(null);
  const [validationErrors, setValidationErrors] = useState({});

  // Pobieramy listę słów kluczowych z API
  useEffect(() => {
    fetch("http://localhost:8080/api/keywords")
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to fetch keywords");
        }
        return response.json();
      })
      .then((data) => {
        console.log("Fetched keywords:", data);
        setAvailableKeywords(data);
      })
      .catch((err) => {
        console.error("Error fetching keywords:", err);
      });
  }, []);

  // Pobieramy listę miast z API
  useEffect(() => {
    fetch("http://localhost:8080/api/towns")
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to fetch towns");
        }
        return response.json();
      })
      .then((data) => {
        console.log("Fetched towns:", data);
        setAvailableTowns(data);
      })
      .catch((err) => {
        console.error("Error fetching towns:", err);
      });
  }, []);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  // Obsługa zmiany w selektorze miast
  const handleTownChange = (e) => {
    const townId = parseInt(e.target.value, 10);
    const town = availableTowns.find((t) => t.id === townId) || null;
    setSelectedTown(town);
  };
  

  const handleSubmit = (e) => {
    e.preventDefault();
    setError(null);
    setValidationErrors({});

    // Walidacja – upewnij się, że wybrano miasto
    if (!selectedTown) {
      setValidationErrors((prev) => ({
        ...prev,
        town: "Wybierz miasto",
      }));
      return;
    }

    // Przygotowanie obiektu kampanii zgodnie z nowym formatem API
    const campaignToAdd = {
      campaignName: formData.campaignName,
      bidAmount: parseFloat(formData.bidAmount),
      status: formData.status,
      radius: parseFloat(formData.radius),
      // Format dla miasta: obiekt z polem "townName"
      town: { townName: selectedTown.townName },
      // Używamy wybranych słów kluczowych – zakładamy, że każdy obiekt zawiera "content"
      keywordList: selectedKeywords.map((keyword) => ({ content: keyword.content })),
    };


    fetch("http://localhost:8080/api/campaigns/add", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(campaignToAdd),
    })
      .then((response) => {
        if (response.status === 409) {
          const errorObj = new Error("Campaign name already exists");
          errorObj.errors = { campaignName: "Campaign name already exists" };
          throw errorObj;
        }
        if (!response.ok) {
          return response.json().then((errData) => {
            const errorObj = new Error("Validation error");
            errorObj.errors = errData.errors;
            throw errorObj;
          });
        }
        return response.text().then((text) => (text ? JSON.parse(text) : {}));
      })
      .then(() => {
        navigate("/");
      })
      .catch((err) => {
        if (err.errors) {
          setValidationErrors(err.errors);
        } else {
          setError(err.message || "Failed to add campaign");
        }
      });
  };

  return (
    <div>
      <h1 className="mb-4">Add New Campaign</h1>
      {error && <div className="alert alert-danger">{error}</div>}
      <form onSubmit={handleSubmit}>
        {/* Campaign Name */}
        <div className="mb-3">
          <label htmlFor="campaignName" className="form-label">
            Campaign Name
          </label>
          <input
            type="text"
            className={`form-control ${validationErrors.campaignName ? "is-invalid" : ""}`}
            id="campaignName"
            name="campaignName"
            value={formData.campaignName}
            onChange={handleChange}
            required
          />
          {validationErrors.campaignName && (
            <div className="invalid-feedback">{validationErrors.campaignName}</div>
          )}
        </div>

        {/* Bid Amount */}
        <div className="mb-3">
          <label htmlFor="bidAmount" className="form-label">
            Bid Amount
          </label>
          <input
            type="number"
            step="0.01"
            className={`form-control ${validationErrors.bidAmount ? "is-invalid" : ""}`}
            id="bidAmount"
            name="bidAmount"
            value={formData.bidAmount}
            onChange={handleChange}
            required
          />
          {validationErrors.bidAmount && (
            <div className="invalid-feedback">{validationErrors.bidAmount}</div>
          )}
        </div>

        {/* Status */}
        <div className="form-check mb-3">
          <input
            type="checkbox"
            className="form-check-input"
            id="status"
            name="status"
            checked={formData.status}
            onChange={handleChange}
          />
          <label htmlFor="status" className="form-check-label">
            Active
          </label>
        </div>

        {/* Town – pole SELECT */}
        <div className="mb-3">
          <label htmlFor="town" className="form-label">
            Town
          </label>
          <select
            id="town"
            name="town"
            className={`form-select ${validationErrors.town ? "is-invalid" : ""}`}
            // Konwertujemy wartość na string
            value={selectedTown ? String(selectedTown.id) : ""}
            onChange={handleTownChange}
            required
          >
            <option value="">Wybierz miasto</option>
            {availableTowns.map((town) => (
              // Również wartość opcji ustawiamy jako string
              <option key={town.id} value={String(town.id)}>
                {town.townName}
              </option>
            ))}
          </select>
          {validationErrors.town && (
            <div className="invalid-feedback">{validationErrors.town}</div>
          )}
        </div>


        {/* Radius */}
        <div className="mb-3">
          <label htmlFor="radius" className="form-label">
            Radius (km)
          </label>
          <input
            type="number"
            step="0.1"
            className={`form-control ${validationErrors.radius ? "is-invalid" : ""}`}
            id="radius"
            name="radius"
            value={formData.radius}
            onChange={handleChange}
            required
          />
          {validationErrors.radius && (
            <div className="invalid-feedback">{validationErrors.radius}</div>
          )}
        </div>

        {/* Keywords – komponent Typeahead */}
        <div className="mb-3">
          <label htmlFor="keywords" className="form-label">
            Keywords
          </label>
          <Typeahead
            id="keywords"
            multiple
            allowNew
            newSelectionPrefix="Add a new keyword: "
            labelKey="content"
            options={availableKeywords}
            placeholder="Wybierz słowa kluczowe..."
            onChange={setSelectedKeywords}
            selected={selectedKeywords}
            minLength={1}
            filterBy={(option, props) =>
              option.content.toLowerCase().includes(props.text.toLowerCase())
            }
          />
          {validationErrors.keywordList && (
            <div className="invalid-feedback d-block">{validationErrors.keywordList}</div>
          )}
        </div>

        <button type="submit" className="btn btn-success">
          Add Campaign
        </button>
      </form>
    </div>
  );
}
