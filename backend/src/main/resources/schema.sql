-- ============================================================
-- Mithila Royal Paan - Complete Database Schema
-- Drop tables in reverse FK order, then recreate all
-- ============================================================

DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS contact_messages;
DROP TABLE IF EXISTS blogs;
DROP TABLE IF EXISTS testimonials;
DROP TABLE IF EXISTS gallery;
DROP TABLE IF EXISTS export_enquiries;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS media_files;
DROP TABLE IF EXISTS site_settings;
DROP TABLE IF EXISTS wedding_packages;
DROP TABLE IF EXISTS faqs;
DROP TABLE IF EXISTS team_members;
DROP TABLE IF EXISTS services;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS products;

-- ============================================================
-- 1. Roles Table
-- ============================================================
CREATE TABLE roles (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- ============================================================
-- 2. Products Table
-- ============================================================
CREATE TABLE products (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(100) NOT NULL,
    description       TEXT,
    price             DECIMAL(10, 2) NOT NULL,
    category          VARCHAR(50)  NOT NULL,
    image_url         VARCHAR(255),
    additional_images TEXT,
    availability      BOOLEAN      DEFAULT TRUE,
    featured          BOOLEAN      DEFAULT FALSE,
    created_at        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 3. Users Table
-- ============================================================
CREATE TABLE users (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    email          VARCHAR(100) NOT NULL UNIQUE,
    password       VARCHAR(255),
    phone          VARCHAR(20),
    google_id      VARCHAR(255),
    provider       VARCHAR(20)  DEFAULT 'LOCAL',
    role_id        INT          NOT NULL,
    email_verified BOOLEAN      DEFAULT FALSE,
    created_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- ============================================================
-- 4. Bookings Table (Wedding/Event)
-- ============================================================
CREATE TABLE bookings (
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    customer_id          INT          NOT NULL,
    event_type           VARCHAR(50)  NOT NULL,
    event_date           DATE         NOT NULL,
    location             VARCHAR(255) NOT NULL,
    guest_count          INT          NOT NULL,
    special_requirements TEXT,
    status               VARCHAR(20)  DEFAULT 'PENDING',
    assigned_staff_id    INT,
    created_at           TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id)       REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_staff_id) REFERENCES users(id) ON DELETE SET NULL
);

-- ============================================================
-- 5. Orders Table
-- ============================================================
CREATE TABLE orders (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    customer_id      INT          NOT NULL,
    product_id       INT          NOT NULL,
    quantity         INT          NOT NULL,
    price            DECIMAL(10, 2) NOT NULL,
    shipping_address TEXT         NOT NULL,
    phone            VARCHAR(20)  NOT NULL,
    status           VARCHAR(20)  DEFAULT 'PENDING',
    created_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id)    ON DELETE CASCADE,
    FOREIGN KEY (product_id)  REFERENCES products(id) ON DELETE CASCADE
);

-- ============================================================
-- 6. Export Enquiries Table
-- ============================================================
CREATE TABLE export_enquiries (
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    name                 VARCHAR(100) NOT NULL,
    email                VARCHAR(100) NOT NULL,
    company              VARCHAR(100),
    country              VARCHAR(100) NOT NULL,
    quantity_requirement VARCHAR(100) NOT NULL,
    contact_info         VARCHAR(255) NOT NULL,
    business_type        VARCHAR(100) NOT NULL,
    message              TEXT,
    status               VARCHAR(20)  DEFAULT 'PENDING',
    created_at           TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 7. Gallery Table
-- ============================================================
CREATE TABLE gallery (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(100) NOT NULL,
    image_url  VARCHAR(255) NOT NULL,
    category   VARCHAR(50)  NOT NULL,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 8. Testimonials Table
-- ============================================================
CREATE TABLE testimonials (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    author_name VARCHAR(100) NOT NULL,
    rating      INT          NOT NULL CHECK (rating >= 1 AND rating <= 5),
    text        TEXT         NOT NULL,
    author_role VARCHAR(100),
    avatar_url  VARCHAR(255),
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 9. Blogs Table  (includes SEO meta columns)
-- ============================================================
CREATE TABLE blogs (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(200) NOT NULL,
    content          TEXT         NOT NULL,
    image_url        VARCHAR(255),
    author_name      VARCHAR(100) DEFAULT 'Mithila Editorial',
    meta_title       VARCHAR(255),
    meta_description TEXT,
    meta_keywords    VARCHAR(255),
    created_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 10. Contact Messages Table
-- ============================================================
CREATE TABLE contact_messages (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(100) NOT NULL,
    phone      VARCHAR(20),
    subject    VARCHAR(150),
    message    TEXT         NOT NULL,
    status     VARCHAR(20)  DEFAULT 'PENDING',
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 11. Notifications Table
-- ============================================================
CREATE TABLE notifications (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT     NOT NULL,
    message    TEXT    NOT NULL,
    is_read    BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================
-- 12. Services Table
-- ============================================================
CREATE TABLE services (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)   NOT NULL,
    description   TEXT,
    image_url     VARCHAR(255),
    price         DECIMAL(10, 2),
    features      TEXT,
    display_order INT            DEFAULT 0
);

-- ============================================================
-- 13. FAQs Table
-- ============================================================
CREATE TABLE faqs (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    question      TEXT NOT NULL,
    answer        TEXT NOT NULL,
    display_order INT  DEFAULT 0
);

-- ============================================================
-- 14. Team Members Table
-- ============================================================
CREATE TABLE team_members (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    role          VARCHAR(100) NOT NULL,
    bio           TEXT,
    photo_url     VARCHAR(255),
    display_order INT          DEFAULT 0
);

-- ============================================================
-- 15. Wedding Packages Table
-- ============================================================
CREATE TABLE wedding_packages (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)   NOT NULL,
    description   TEXT,
    price         DECIMAL(10, 2) NOT NULL,
    features      TEXT,
    display_order INT            DEFAULT 0
);

-- ============================================================
-- 16. Site Settings Table  (key-value CMS settings)
-- ============================================================
CREATE TABLE site_settings (
    setting_key   VARCHAR(100) NOT NULL PRIMARY KEY,
    setting_value TEXT,
    category      VARCHAR(50)  NOT NULL
);

-- ============================================================
-- 17. Media Files Table
-- ============================================================
CREATE TABLE media_files (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    filename    VARCHAR(255) NOT NULL,
    file_url    VARCHAR(255) NOT NULL,
    file_type   VARCHAR(50)  NOT NULL,
    file_size   BIGINT,
    uploaded_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- SEED DATA
-- ============================================================

-- Roles
INSERT INTO roles (id, name) VALUES (1, 'ROLE_SUPER_ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_STAFF');
INSERT INTO roles (id, name) VALUES (3, 'ROLE_CUSTOMER');

-- Users
-- admin@mithilaroyalpaan.com / Admin@123
INSERT INTO users (name, email, password, phone, role_id, email_verified)
VALUES ('Super Admin', 'admin@mithilaroyalpaan.com',
        '$2a$10$NKLWhYZDyJovTbAJmqf63Or/QUhX7k7XWeGUHNM2X.JVqOhCsRsjG', '+919876543210', 1, TRUE);

-- staff@mithilaroyalpaan.com / Staff@123
INSERT INTO users (name, email, password, phone, role_id, email_verified)
VALUES ('Ramesh Kumar', 'staff@mithilaroyalpaan.com',
        '$2a$10$5t/JwSoy.WsV1HeVfXxuKuFv72Jtgk42uzJ3XObTfK6/DPJsufAAW', '+918765432109', 2, TRUE);

-- customer@mithilaroyalpaan.com / Customer@123
INSERT INTO users (name, email, password, phone, role_id, email_verified)
VALUES ('Raja Babu', 'customer@mithilaroyalpaan.com',
        '$2a$10$g7qNcgmxTLfAy1ykiDgEi.hJ4K0lGVmvuVh4k8MNwgNppFdQ897RW', '+917654321098', 3, TRUE);

-- Products
INSERT INTO products (name, description, price, category, image_url, availability, featured) VALUES
('Fresh Mithila Magahi Paan',
 'Premium export-quality Magahi betel leaf known for its melting-in-mouth texture and digestive values.',
 120.00, 'Fresh Leaves', '/images/products/magahi.jpg', TRUE, TRUE),

('Royal Sweet Paan',
 'Stuffed with sweet gulkand, desiccated coconut, tutty-fruity, cardamom, and premium syrup. Pure refreshing luxury.',
 80.00, 'Sweet Paan', '/images/products/sweet.jpg', TRUE, TRUE),

('Premium Dry Fruit Paan',
 'Rich amalgamation of almonds, cashews, pistachios, dates, saffron threads, and gulkand wrapped in crisp leaf.',
 150.00, 'Dry Fruit Paan', '/images/products/dryfruit.jpg', TRUE, FALSE),

('Gourmet Chocolate Paan',
 'Betel leaf loaded with dark chocolate chips, chocolate sauce, sweet sprinkles, and chilled to perfection.',
 100.00, 'Chocolate Paan', '/images/products/chocolate.jpg', TRUE, FALSE),

('Mithila Fire Paan',
 'The famous theatrical fire paan experience. Flavored with sweet spices and set ablaze for a safe, cold-fire chew.',
 200.00, 'Fire Paan', '/images/products/fire.jpg', TRUE, TRUE),

('Royal Wedding Shagun Gift Pack',
 'A luxury box containing 12 premium assorted dry-fruit and sweet paans with beautiful traditional packaging.',
 1200.00, 'Gift Packs', '/images/products/giftpack.jpg', TRUE, FALSE);

-- Testimonials
INSERT INTO testimonials (author_name, rating, text, author_role, avatar_url) VALUES
('Vikramaditya Mishra', 5,
 'We hired Mithila Royal Paan for my daughter\'s wedding. Their royal setup with Madhubani decorations and Fire Paan counter was the highlight of the event! Guests are still talking about it.',
 'Bride\'s Father, Patna', '/images/testimonials/avatar1.jpg'),

('Ananya Sen', 5,
 'The Magahi Paan is exceptionally soft and completely dissolves in the mouth. Highly recommend their bulk delivery service for authentic festivals.',
 'Cultural Event Planner, Kolkata', '/images/testimonials/avatar2.jpg'),

('Rajesh Singhania', 4,
 'Excellent service. Very hygienic presentation, which is hard to find with paan counters. The chocolate and dry fruit variations were a hit among kids and adults alike.',
 'Corporate Manager, Delhi', '/images/testimonials/avatar3.jpg');

-- Gallery
INSERT INTO gallery (title, image_url, category) VALUES
('Traditional Madhubani Wedding Counter Setup', '/images/gallery/setup1.jpg', 'Wedding'),
('Harvesting fresh Magahi betel leaves in Madhubani farm', '/images/gallery/farming1.jpg', 'Farming'),
('Assorted Royal Dry Fruit and Sweet Paan Platter', '/images/gallery/product1.jpg', 'Products'),
('The Live Fire Paan Counter action at a Cultural Fest', '/images/gallery/event1.jpg', 'Events'),
('Happy guests enjoying traditional digestive betel leaves', '/images/gallery/event2.jpg', 'Events'),
('Freshly packaged gift boxes ready for shipment', '/images/gallery/product2.jpg', 'Products');

-- Blogs (with SEO columns)
INSERT INTO blogs (title, content, image_url, author_name, meta_title, meta_description, meta_keywords) VALUES
('The Cultural Significance of Paan in Mithilanchal',
 'In Mithila culture, betel leaf (Paan) and fish (Machh) are symbols of auspiciousness, prosperity, and respect. No religious ceremony, wedding, or guest reception is complete without offering "Paan-Makhana". The Magahi leaf cultivated in Bihar is globally renowned for its quality...',
 '/images/blog/blog1.jpg', 'Pt. Jha Shastri',
 'Paan in Mithilanchal Culture | Mithila Royal Paan',
 'Explore the deep cultural roots of Paan in Mithila traditions, weddings, and ceremonies.',
 'paan, mithila, culture, betel leaf'),

('Why Magahi Betel Leaf is a GI-Tagged Wonder',
 'The unique soil composition, micro-climate, and centuries-old farming techniques of Bihar\'s Magadh and Mithila regions give Magahi Paan its distinct characteristics. It is soft, non-fibrous, and has a mild aromatic flavor. In this article, we dive deep into the cultivation process of this geographical indication (GI) tagged marvel...',
 '/images/blog/blog2.jpg', 'Dr. S. K. Roy, Agronomist',
 'Magahi Betel Leaf GI Tag | Mithila Royal Paan',
 'Learn why Magahi Paan holds a Geographical Indication tag and what makes it unique.',
 'magahi paan, GI tag, betel leaf, Bihar'),

('How to Keep Paan Hygienic: The Royal Way',
 'One of the biggest concerns with betel leaf consumption in modern times is hygiene. At Mithila Royal Paan, we use triple-filtered water wash, organic farm practices, and custom gloves for our event counters. Read more about our hygiene standards and safety protocols...',
 '/images/blog/blog3.jpg', 'Ramesh Kumar, Quality Head',
 'Hygienic Paan Standards | Mithila Royal Paan',
 'Discover the hygiene protocols and safety standards followed at Mithila Royal Paan.',
 'paan hygiene, betel leaf safety, organic paan');

-- Services
INSERT INTO services (name, description, image_url, price, features, display_order) VALUES
('Wedding Paan Counter',
 'A beautifully decorated traditional paan counter for your wedding. Includes our expert paan maker and premium ingredient selection.',
 '/images/services/wedding.jpg', 15000.00,
 'Madhubani decorated counter|Expert paan maker|5 types of paan|3-hour service|Traditional setup',
 1),

('Bulk Paan Export',
 'Fresh Magahi and Mithila betel leaves exported in bulk with proper moisture packaging for freshness.',
 '/images/services/export.jpg', NULL,
 'GI-tagged Magahi leaves|Custom packaging|Pan-India delivery|International shipping|Hygiene certified',
 2),

('Corporate Events',
 'Professional paan counter service for corporate events, product launches, and office parties.',
 '/images/services/corporate.jpg', 8000.00,
 'Branded counter setup|2 paan makers|4 varieties|2-hour service|Hygienic serving',
 3),

('Gift Packaging',
 'Custom paan gift boxes for weddings, festivals, and corporate gifting with traditional Madhubani art packaging.',
 '/images/services/gifts.jpg', NULL,
 'Custom Madhubani boxes|Minimum 12 pieces|Pan-India shipping|Festival specials|Corporate branding',
 4);

-- FAQs
INSERT INTO faqs (question, answer, display_order) VALUES
('What is Magahi Paan?',
 'Magahi Paan is a GI-tagged variety of betel leaf grown in the Magadh region of Bihar. It is known for its soft texture, mild taste, and quick digestibility. It is considered one of the best varieties of betel leaf in India.',
 1),
('Do you provide paan counter services outside Bihar?',
 'Yes, we provide our wedding and event paan counter services across India. Travel and logistics charges may apply for distant locations. Please contact us for a customized quote.',
 2),
('How can I place a bulk order for export?',
 'You can fill out our Export Enquiry form on the website, or contact us directly via phone or email. Our export team will get back to you within 24 hours with pricing and shipping details.',
 3),
('Are your paan products hygienic and safe?',
 'Absolutely. We follow strict hygiene standards including triple-filtered water washing, organic farming practices, and food-grade gloved preparation at all our counters and packaging units.',
 4),
('What types of paan do you offer?',
 'We offer Fresh Magahi Paan, Sweet Paan, Dry Fruit Paan, Chocolate Paan, Fire Paan, and Special Wedding Paan. We also offer custom varieties for events and bulk orders.',
 5);

-- Team Members
INSERT INTO team_members (name, role, bio, photo_url, display_order) VALUES
('Rajababu Jha', 'Founder & CEO',
 'Third-generation paan cultivator from Madhubani with over 20 years of experience in Mithila betel leaf farming and paan craftsmanship. Rajababu founded Mithila Royal Paan to bring authentic Mithilanchal tradition to the world.',
 '/images/team/founder.jpg', 1),

('Ramesh Kumar', 'Head of Quality & Operations',
 'Ramesh oversees all quality control processes, from leaf procurement and storage to final event setup. He is a food safety certified professional with 10 years in the food industry.',
 '/images/team/ramesh.jpg', 2),

('Sunita Devi', 'Master Paan Artisan',
 'Sunita is our lead paan maker with over 15 years of experience crafting royal paan recipes. Her expertise in traditional Mithila recipes and new-age flavors makes her indispensable to our team.',
 '/images/team/sunita.jpg', 3);

-- Wedding Packages
INSERT INTO wedding_packages (name, description, price, features, display_order) VALUES
('Silver Paan Package',
 'Perfect for intimate weddings and small family gatherings.',
 8000.00,
 '3 types of paan|100 servings|1 paan maker|2-hour service|Basic decorated counter',
 1),

('Gold Paan Package',
 'Our most popular wedding package for mid-size celebrations.',
 15000.00,
 '5 types of paan|250 servings|2 paan makers|4-hour service|Madhubani decorated counter|Branded menu card',
 2),

('Royal Platinum Package',
 'The ultimate luxury paan experience for royal weddings.',
 30000.00,
 '8 types of paan including Fire Paan|Unlimited servings|3 paan makers|Full-day service|Premium Madhubani counter|Custom gift boxes|Photography assistance',
 3);

-- Site Settings (CMS defaults)
INSERT INTO site_settings (setting_key, setting_value, category) VALUES
('site_name',           'Mithila Royal Paan',                              'general'),
('site_tagline',        'Bihar\'s Finest Paan, Crafted with Royal Heritage', 'general'),
('contact_phone',       '+91 98765 43210',                                  'contact'),
('contact_email',       'info@mithilaroyalpaan.com',                        'contact'),
('contact_address',     'Madhubani, Bihar, India - 847211',                 'contact'),
('hero_title',          'Experience the Royal Taste of Mithila Paan',       'home'),
('hero_subtitle',       'Authentic Magahi Betel Leaves | Traditional Craftsmanship | Pan-India Service', 'home'),
('hero_cta_primary',    'Order Now',                                        'home'),
('hero_cta_secondary',  'Explore Products',                                 'home'),
('about_story',         'Mithila Royal Paan was founded in 2010 by Rajababu Jha, a third-generation paan cultivator from Madhubani, Bihar. We are dedicated to preserving and promoting the rich tradition of Mithilanchal paan culture.', 'about'),
('about_mission',       'To bring authentic, hygienic, and premium quality Mithila paan to every celebration across India and the world.',  'about'),
('about_vision',        'To be the most trusted name in traditional Indian paan culture, connecting heritage with modern lifestyles.',       'about'),
('footer_description',  'Premium Mithila paan for weddings, events, and bulk export. Rooted in Bihar\'s rich tradition.',                   'footer'),
('facebook_url',        'https://facebook.com/mithilaroyalpaan',            'social'),
('instagram_url',       'https://instagram.com/mithilaroyalpaan',           'social'),
('whatsapp_number',     '+919876543210',                                    'social');
