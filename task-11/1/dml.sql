INSERT INTO rooms (roomId, number, price, capacity, stars, status, releasedIn)
VALUES 
('r1', 101, 5000.00, 2, 4, 'OCCUPIED', '2026-01-12'),
('r2', 102, 8000.00, 3, 5, 'AVAILABLE', NULL),
('r3', 103, 2000.00, 1, 1, 'OCCUPIED', '2026-01-15');

INSERT INTO guests (guestId, fullName, age, rentRoomId, arriveDate, departureDate, status)
VALUES
('g0', 'Oknov Dima', 23, 'r2', '2025-12-30', '2025-12-31', 'EVICTED'),
('g1', 'Petrov Vasya', 18, 'r1', '2026-01-06', '2026-01-14', 'SETTLED'),
('g2', 'Seledkin Petya', 20, 'r3', '2026-01-05', '2026-01-15', 'SETTLED');

INSERT INTO services (serviceId, name, price, serviceSection)
VALUES
('s1', 'Breakfast', 300.00, 'FOOD');

INSERT INTO guestUsedServices (guestId, serviceId, price, date)
VALUES
('g1', 's1', 300.00, '2026-01-07');

