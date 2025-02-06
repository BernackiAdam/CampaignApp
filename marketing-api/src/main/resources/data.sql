-- Dodajemy miasta
INSERT INTO town (town_name) VALUES
('Warszawa'),
('Kraków'),
('Gdańsk');

INSERT INTO campaign (campaign_name, bid_amount, campaign_funds, status, town_id, radius) VALUES
('Letnia Wyprzedaż', 2.50, 5000.00, true, 1, 10.0),
('Promocja Noworoczna', 3.00, 10000.00, true, 2, 15.0),
('Zniżki na Elektronikę', 1.80, 7000.00, false, 3, 8.0);


INSERT INTO keyword (content) VALUES
('promocja'),
('wyprzedaż'),
('elektronika'),
('nowy rok'),
('smartfony');

INSERT INTO campaign_keywords (campaign_id, keyword_id) VALUES
(1, 1),
(1, 2),
(2, 1),
(2, 4),
(3, 3),
(3, 5);
