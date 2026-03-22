CREATE SCHEMA IF NOT EXISTS `courier_tracking` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

INSERT IGNORE INTO store (name, lat, lng) VALUES ('Ataşehir MMM Migros', 40.9923307, 29.1244229);
INSERT IGNORE INTO store (name, lat, lng) VALUES ('Novada MMM Migros', 40.986106, 29.1161293);
INSERT IGNORE INTO store (name, lat, lng) VALUES ('Beylikdüzü 5M Migros', 41.0066851, 28.6552262);
INSERT IGNORE INTO store (name, lat, lng) VALUES ('Ortaköy MMM Migros', 41.055783, 29.0210292);
INSERT IGNORE INTO store (name, lat, lng) VALUES ('Caddebostan MMM Migros', 40.9632463, 29.0630908);

-- Insert a dummy courier (ID: 1) for testing purposes
INSERT IGNORE INTO courier (id, total_distance) VALUES (1, 0.0);