-- Drop tables if they exist to start clean
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS contact_messages;
DROP TABLE IF EXISTS blogs;
DROP TABLE IF EXISTS testimonials;
DROP TABLE IF EXISTS gallery;
DROP TABLE IF EXISTS export_enquiries;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS faqs;
DROP TABLE IF EXISTS services;
DROP TABLE IF EXISTS wedding_packages;
DROP TABLE IF EXISTS team_members;
DROP TABLE IF EXISTS media_files;
DROP TABLE IF EXISTS site_settings;

-- 1. Roles Table
CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 2. Products Table
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    image_url VARCHAR(255),
    additional_images TEXT,
    availability BOOLEAN DEFAULT TRUE,
    featured BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Users Table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255),
    phone VARCHAR(20),
    google_id VARCHAR(255),
    provider VARCHAR(20) DEFAULT 'LOCAL',
    role_id INT NOT NULL,
    email_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- 4. Bookings Table (Wedding/Event)
CREATE TABLE bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_date DATE NOT NULL,
    location VARCHAR(255) NOT NULL,
    guest_count INT NOT NULL,
    special_requirements TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',
    assigned_staff_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_staff_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 5. Orders Table (Bulk/Product)
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    shipping_address TEXT NOT NULL,
    phone VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- 6. Export Enquiries Table
CREATE TABLE export_enquiries (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    company VARCHAR(100),
    country VARCHAR(100) NOT NULL,
    quantity_requirement VARCHAR(100) NOT NULL,
    contact_info VARCHAR(255) NOT NULL,
    business_type VARCHAR(100) NOT NULL,
    message TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7. Gallery Table
CREATE TABLE gallery (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 8. Testimonials Table
CREATE TABLE testimonials (
    id INT AUTO_INCREMENT PRIMARY KEY,
    author_name VARCHAR(100) NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    text TEXT NOT NULL,
    author_role VARCHAR(100),
    avatar_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 9. Blogs Table
CREATE TABLE blogs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    image_url VARCHAR(255),
    author_name VARCHAR(100) DEFAULT 'Mithila Editorial',
    meta_title VARCHAR(255),
    meta_description TEXT,
    meta_keywords VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 10. Contact Messages Table
CREATE TABLE contact_messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    subject VARCHAR(150),
    message TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 11. Notifications Table
CREATE TABLE notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 12. Site Settings Table
CREATE TABLE site_settings (
    setting_key VARCHAR(100) PRIMARY KEY,
    setting_value TEXT,
    category VARCHAR(50) NOT NULL
);

-- 13. FAQs Table
CREATE TABLE faqs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    display_order INT DEFAULT 0
);

-- 14. Services Table
CREATE TABLE services (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    price DECIMAL(10, 2),
    features TEXT,
    display_order INT DEFAULT 0
);

-- 15. Wedding Packages Table
CREATE TABLE wedding_packages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    features TEXT,
    display_order INT DEFAULT 0
);

-- 16. Team Members Table
CREATE TABLE team_members (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(100) NOT NULL,
    bio TEXT,
    photo_url VARCHAR(255),
    display_order INT DEFAULT 0
);

-- 17. Media Files Table
CREATE TABLE media_files (
    id INT AUTO_INCREMENT PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_size BIGINT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- ==================== SEED DATA ====================

-- Insert Roles
INSERT INTO roles (id, name) VALUES (1, 'ROLE_SUPER_ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_STAFF');
INSERT INTO roles (id, name) VALUES (3, 'ROLE_CUSTOMER');

-- Insert default users: password is 'Admin@123'
INSERT INTO users (name, email, password, phone, role_id, email_verified)
VALUES ('Super Admin', 'admin@mithilaroyalpaan.com', '$2a$10$8.Z4KqK4SjM4e3z.O0K1G.jQx6t.U6w3Y62fM7o/w97hZ1G.k2.7O', '+919876543210', 1, TRUE);

INSERT INTO users (name, email, password, phone, role_id, email_verified)
VALUES ('Ramesh Kumar', 'staff@mithilaroyalpaan.com', '$2a$10$8.Z4KqK4SjM4e3z.O0K1G.jQx6t.U6w3Y62fM7o/w97hZ1G.k2.7O', '+918765432109', 2, TRUE);

INSERT INTO users (name, email, password, phone, role_id, email_verified)
VALUES ('Raja Babu', 'customer@mithilaroyalpaan.com', '$2a$10$8.Z4KqK4SjM4e3z.O0K1G.jQx6t.U6w3Y62fM7o/w97hZ1G.k2.7O', '+917654321098', 3, TRUE);

-- Insert Products
INSERT INTO products (name, description, price, category, image_url, availability, featured) VALUES
('Fresh Mithila Magahi Paan', 'Premium export-quality Magahi betel leaf known for its melting-in-mouth texture and digestive values.', 120.00, 'Fresh Leaves', '/images/products/magahi.jpg', TRUE, TRUE),
('Royal Sweet Paan', 'Stuffed with sweet gulkand, desiccated coconut, tutty-fruity, cardamom, and premium syrup. Pure refreshing luxury.', 80.00, 'Sweet Paan', '/images/products/sweet.jpg', TRUE, TRUE),
('Premium Dry Fruit Paan', 'Rich amalgamation of almonds, cashews, pistachios, dates, saffron threads, and gulkand wrapped in crisp leaf.', 150.00, 'Dry Fruit Paan', '/images/products/dryfruit.jpg', TRUE, TRUE),
('Gourmet Chocolate Paan', 'Betel leaf loaded with dark chocolate chips, chocolate sauce, sweet sprinkles, and chilled to perfection.', 100.00, 'Chocolate Paan', '/images/products/chocolate.jpg', TRUE, FALSE),
('Mithila Fire Paan', 'The famous theatrical fire paan experience. Flavored with sweet spices and set ablaze for a safe, cold-fire chew.', 200.00, 'Fire Paan', '/images/products/fire.jpg', TRUE, FALSE),
('Royal Wedding Shagun Gift Pack', 'A luxury box containing 12 premium assorted dry-fruit and sweet paans with beautiful traditional packaging.', 1200.00, 'Gift Packs', '/images/products/giftpack.jpg', TRUE, TRUE);

-- Insert Testimonials
INSERT INTO testimonials (author_name, rating, text, author_role, avatar_url) VALUES
('Vikramaditya Mishra', 5, 'We hired Mithila Royal Paan for my daughter\'s wedding. Their royal setup with Madhubani decorations and Fire Paan counter was the highlight of the event! Guests are still talking about it.', 'Bride\'s Father, Patna', '/images/testimonials/avatar1.jpg'),
('Ananya Sen', 5, 'The Magahi Paan is exceptionally soft and completely dissolves in the mouth. Highly recommend their bulk delivery service for authentic festivals.', 'Cultural Event Planner, Kolkata', '/images/testimonials/avatar2.jpg'),
('Rajesh Singhania', 4, 'Excellent service. Very hygienic presentation, which is hard to find with paan counters. The chocolate and dry fruit variations were a hit among kids and adults alike.', 'Corporate Manager, Delhi', '/images/testimonials/avatar3.jpg');

-- Insert Gallery Items
INSERT INTO gallery (title, image_url, category) VALUES
('Traditional Madhubani Wedding Counter Setup', '/images/gallery/setup1.jpg', 'Wedding'),
('Harvesting fresh Magahi betel leaves in Madhubani farm', '/images/gallery/farming1.jpg', 'Farming'),
('Assorted Royal Dry Fruit and Sweet Paan Platter', '/images/gallery/product1.jpg', 'Products'),
('The Live Fire Paan Counter action at a Cultural Fest', '/images/gallery/event1.jpg', 'Events'),
('Happy guests enjoying traditional digestive betel leaves', '/images/gallery/event2.jpg', 'Events'),
('Freshly packaged gift boxes ready for shipment', '/images/gallery/product2.jpg', 'Products');

-- Insert Blogs
INSERT INTO blogs (title, content, image_url, author_name, meta_title, meta_description, meta_keywords) VALUES
('The Cultural Significance of Paan in Mithilanchal', 'In Mithila culture, betel leaf (Paan) and fish (Machh) are symbols of auspiciousness, prosperity, and respect. No religious ceremony, wedding, or guest reception is complete without offering "Paan-Makhana". The Magahi leaf cultivated in Bihar is globally renowned for its quality...', '/images/blog/blog1.jpg', 'Pt. Jha Shastri', 'Paan Culture in Mithilanchal | Mithila Royal Paan', 'Learn about the deep-rooted cultural significance of betel leaves (Paan) in Mithila, Bihar weddings and ceremonies.', 'mithila paan, paan culture, bihar traditions, betel leaf history'),
('Why Magahi Betel Leaf is a GI-Tagged Wonder', 'The unique soil composition, micro-climate, and centuries-old farming techniques of Bihar\'s Magadh and Mithila regions give Magahi Paan its distinct characteristics. It is soft, non-fibrous, and has a mild aromatic flavor. In this article, we dive deep into the cultivation process of this geographical indication (GI) tagged marvel...', '/images/blog/blog2.jpg', 'Dr. S. K. Roy, Agronomist', 'Magahi Paan GI Tag Details | Mithila Royal Paan', 'Discover why the premium Magahi betel leaf holds a Geographical Indication (GI) tag and how it is organically cultivated.', 'magahi paan, gi tag bihar, organic betel leaf, farming'),
('How to Keep Paan Hygienic: The Royal Way', 'One of the biggest concerns with betel leaf consumption in modern times is hygiene. At Mithila Royal Paan, we use triple-filtered water wash, organic farm practices, and custom gloves for our event counters. Read more about our hygiene standards and safety protocols...', '/images/blog/blog3.jpg', 'Ramesh Kumar, Quality Head', 'Hygiene Standards for Wedding Paan | Mithila Royal Paan', 'Explore how we maintain 100% hygiene and quality control during betel leaf sorting, preparation, and live event catering.', 'hygienic paan, wedding catering safety, clean paan, organic farming');

-- Insert FAQs
INSERT INTO faqs (question, answer, display_order) VALUES
('What makes Magahi Paan unique?', 'Magahi betel leaf is highly soft, non-fibrous, and has a melting-in-mouth texture with low pungency. It is a GI-tagged specialty of Bihar.', 1),
('Do you provide custom themed stalls for weddings?', 'Yes, we specialize in luxury wedding counters themed with traditional Mithila Madhubani art and live, costumed servers.', 2),
('How do I book a catering counter for an event?', 'You can register an account, navigate to the Event or Wedding booking page, select your package and date, and submit an enquiry. Our staff will coordinate with you.', 3),
('What are your delivery options for bulk B2B exports?', 'We ship fresh, vacuum-sealed and temperature-controlled betel leaves worldwide via trusted air freight partners.', 4);

-- Insert Services
INSERT INTO services (name, description, image_url, price, features, display_order) VALUES
('Wedding Catering Stalls', 'Full-service premium paan counters styled with traditional Madhubani art, uniform servers, and up to 12 paan variations.', '/images/services/wedding.jpg', 15000.00, 'Live Fire Paan,Assorted Sweet Paan,Madhubani Stall Theme,Gloves and Hygiene certified servers', 1),
('B2B Bulk Betel Leaf Export', 'Supply of premium grade Magahi and fresh betel leaves to distributors and importers worldwide with specialized cooling packs.', '/images/services/export.jpg', 450.00, 'Air Cargo Shipment,Fresh Vacuum Seal,Organic Certified Leaves,Custom Packaging options', 2),
('Corporate & Festival Stalls', 'Hygienic and professional paan counters tailored for corporate fests, product launches, and cultural gatherings.', '/images/services/corporate.jpg', 12000.00, 'Custom branding logo,Mocktail Paan options,Hygienic setup,Interactive servers', 3);

-- Insert Wedding Packages
INSERT INTO wedding_packages (name, description, price, features, display_order) VALUES
('Shahi Bronze Plan', 'Essential premium setup suitable for small events or family gatherings of up to 150 guests.', 9999.00, '3 Paan Varieties,Bronze Theme Stall,2 Hours Live Service,1 Professional Server', 1),
('Mithila Swarna Gold Plan', 'Our most popular setup suitable for grand weddings of up to 400 guests, featuring theatrical counters.', 19999.00, '6 Paan Varieties,Live Fire Paan,Madhubani Art Stall,4 Hours Live Service,2 Custom Uniform Servers', 2),
('Royal Maharaja Diamond Plan', 'Ultimate luxury package for up to 800 guests, featuring all special paan varieties and supreme decoration.', 34999.00, 'All 10+ Paan Varieties,Live Fire & Ice Paan,Grand Royal Maharaja Stall,Unlimited Service Hours,4 Uniform Servers,Gift boxes for special guests', 3);

-- Insert Team Members
INSERT INTO team_members (name, role, bio, photo_url, display_order) VALUES
('Raja Babu', 'Chief Executive & Founder', 'Descendant of traditional farming families in Madhubani, dedicated to modernizing the betel leaf industry.', '/images/team/team1.jpg', 1),
('Ramesh Kumar', 'Operations & Quality Lead', 'Over 10 years of experience managing food supply chains and ensuring 100% organic crop quality.', '/images/team/team2.jpg', 2),
('Sita Devi', 'Lead Madhubani Designer', 'Award-winning local artist who oversees the traditional handicraft styling of our wedding event counters.', '/images/team/team3.jpg', 3);

-- Insert Site Settings
INSERT INTO site_settings (setting_key, setting_value, category) VALUES
('site_name', 'Mithila Royal Paan', 'SYSTEM'),
('site_logo', '/images/logo.png', 'SYSTEM'),
('site_favicon', '/favicon.ico', 'SYSTEM'),
('footer_text', 'Premium betel leaf farming and wedding paan service company from Mithila, Bihar. Fusing organic tradition with modern hygiene standards.', 'SYSTEM'),
('copyright_text', '© 2026 Mithila Royal Paan. All Rights Reserved.', 'SYSTEM'),
('theme_colors', '{"primary":"#0a3622","secondary":"#152e22","accent":"#aa8618","text":"#ffffff"}', 'THEME'),
('seo_meta_title', 'Mithila Royal Paan - Premium Betel Leaf & Event Catering', 'SEO'),
('seo_meta_description', 'Book luxury wedding paan counters, event catering, B2B bulk export, and fresh Magahi betel leaf delivery directly from Madhubani, Bihar.', 'SEO'),
('seo_keywords', 'mithila royal paan, wedding paan stall, betel leaf export, magahi leaf bihar, fire paan wedding', 'SEO'),
('home_hero_title', 'Experience the Royal Taste of Mithila', 'HOME_HERO'),
('home_hero_subtitle', 'Premium organic betel leaf farming and luxury wedding paan catering services direct from Bihar.', 'HOME_HERO'),
('home_hero_btn1_text', 'Book Wedding Counter', 'HOME_HERO'),
('home_hero_btn1_url', '/wedding-paan.html', 'HOME_HERO'),
('home_hero_btn2_text', 'Explore Products', 'HOME_HERO'),
('home_hero_btn2_url', '/products.html', 'HOME_HERO'),
('home_hero_bg_image', '/images/hero-bg.jpg', 'HOME_HERO'),
('home_stats_farmers', '120+', 'HOME_STATS'),
('home_stats_experience', '12+ Years', 'HOME_STATS'),
('home_stats_weddings', '800+', 'HOME_STATS'),
('home_stats_exports', '12+ Countries', 'HOME_STATS'),
('contact_phone', '+91 98765 43210', 'CONTACT'),
('contact_email', 'info@mithilaroyalpaan.com', 'CONTACT'),
('contact_address', 'Royal Farm Estate, Madhubani, Bihar - 847211', 'CONTACT'),
('contact_whatsapp', '919876543210', 'CONTACT'),
('contact_map_url', 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3581.4287311139423!2d86.08481237537637!3d26.312608385627685!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x39edc5c065f49e49%3A0xe54e3d09a06e9fd8!2sMadhubani%2C%20Bihar%20847211!5e0!3m2!1sen!2sin!4v1716584021200!5m2!1sen!2sin', 'CONTACT'),
('contact_facebook', 'https://facebook.com/mithilaroyalpaan', 'CONTACT'),
('contact_instagram', 'https://instagram.com/mithilaroyalpaan', 'CONTACT'),
('contact_youtube', 'https://youtube.com/mithilaroyalpaan', 'CONTACT');
