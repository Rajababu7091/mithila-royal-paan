// Administrative Dashboard Operations Controller

let staffList = [];
let bsProductModal, bsBlogModal, bsGalleryModal, bsTestimonialModal;

document.addEventListener('DOMContentLoaded', () => {
    // 1. Secure route - allow SUPER_ADMIN and STAFF
    Auth.checkRouteProtection(['ROLE_SUPER_ADMIN', 'ROLE_STAFF']);

    // 2. Initialize Modals
    bsProductModal = new bootstrap.Modal(document.getElementById('productCrudModal'));
    bsBlogModal = new bootstrap.Modal(document.getElementById('blogCrudModal'));
    bsGalleryModal = new bootstrap.Modal(document.getElementById('galleryCrudModal'));
    bsTestimonialModal = new bootstrap.Modal(document.getElementById('testimonialCrudModal'));
    bsFaqModal = new bootstrap.Modal(document.getElementById('faqCrudModal'));

    // 3. Load stats
    loadAdminStats();

    // 4. Load staff directory first (needed for bookings assignments dropdowns)
    loadStaffMembers().then(() => {
        loadAdminBookings();
    });

    // 5. Load other management logs
    loadAdminOrders();
    loadAdminLeads();
    loadAdminProducts();
    loadAdminBlogs();
    loadAdminGallery();
    loadAdminTestimonials();
    loadAdminFaqs();
    loadCentralMedia();
    loadAdminUsers();

    // 6. Bind form handlers
    document.getElementById('product-crud-form').addEventListener('submit', handleProductSubmit);
    document.getElementById('blog-crud-form').addEventListener('submit', handleBlogSubmit);
    document.getElementById('gallery-crud-form').addEventListener('submit', handleGallerySubmit);
    document.getElementById('testimonial-crud-form').addEventListener('submit', handleTestimonialSubmit);
    document.getElementById('faq-crud-form').addEventListener('submit', handleFaqSubmit);
});

// Load KPI counters
function loadAdminStats() {
    API.get('/dashboard/stats')
        .then(stats => {
            document.getElementById('stat-revenue').textContent = `INR ${stats.totalRevenue.toFixed(2)}`;
            document.getElementById('stat-bookings').textContent = stats.totalBookings;
            document.getElementById('stat-orders').textContent = stats.totalOrders;
            document.getElementById('stat-leads').textContent = stats.totalEnquiries;
            document.getElementById('stat-customers').textContent = stats.totalCustomers;
        })
        .catch(err => console.error('Failed to load dashboard statistics:', err));
}

// Load staff list
async function loadStaffMembers() {
    try {
        staffList = await API.get('/users/admin/staff');
    } catch (err) {
        console.error('Failed to fetch staff members:', err);
    }
}

// ------------------- BOOKINGS WORKFLOW -------------------
function loadAdminBookings() {
    const body = document.getElementById('admin-bookings-table-body');
    API.get('/bookings/all')
        .then(list => {
            if (list.length === 0) {
                body.innerHTML = `<tr><td colspan="8" class="text-secondary py-4">No bookings registered.</td></tr>`;
                return;
            }

            body.innerHTML = '';
            list.forEach(b => {
                let statusBadge = `<span class="badge bg-secondary">Pending</span>`;
                if (b.status === 'APPROVED') statusBadge = `<span class="badge bg-success">Approved</span>`;
                if (b.status === 'REJECTED') statusBadge = `<span class="badge bg-danger">Rejected</span>`;
                if (b.status === 'COMPLETED') statusBadge = `<span class="badge bg-info text-dark">Completed</span>`;

                // Build staff dropdown
                let staffOptions = `<option value="" disabled ${!b.assignedStaff ? 'selected' : ''}>Assign Coordinator...</option>`;
                staffList.forEach(staff => {
                    const isSelected = b.assignedStaff && b.assignedStaff.id === staff.id ? 'selected' : '';
                    staffOptions += `<option value="${staff.id}" ${isSelected}>${staff.name}</option>`;
                });

                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="font-monospace text-warning">#W-${b.id}</td>
                    <td>
                        <strong class="text-white">${b.customer.name}</strong><br>
                        <small class="text-secondary">${b.customer.email}</small>
                    </td>
                    <td class="royal-font fw-bold text-white">${b.eventType}</td>
                    <td class="small">${b.eventDate}</td>
                    <td class="font-monospace">${b.guestCount}</td>
                    <td>
                        <select class="form-select form-select-sm form-royal-control d-inline-block w-auto" onchange="assignStaffToBooking(${b.id}, this)">
                            ${staffOptions}
                        </select>
                    </td>
                    <td>${statusBadge}</td>
                    <td>
                        <div class="btn-group btn-group-sm">
                            <button class="btn btn-success" onclick="updateBookingStatus(${b.id}, 'APPROVED')"><i class="bi bi-check-circle"></i></button>
                            <button class="btn btn-danger" onclick="updateBookingStatus(${b.id}, 'REJECTED')"><i class="bi bi-x-circle"></i></button>
                            <button class="btn btn-info text-dark" onclick="updateBookingStatus(${b.id}, 'COMPLETED')"><i class="bi bi-check-all"></i></button>
                        </div>
                    </td>
                `;
                body.appendChild(tr);
            });
        })
        .catch(err => {
            body.innerHTML = `<tr><td colspan="8" class="text-danger py-4">Failed to load bookings console.</td></tr>`;
        });
}

function updateBookingStatus(id, status) {
    API.put(`/bookings/${id}/status`, null, { status })
        .then(() => {
            showGlobalAlert(`Booking #${id} status updated to: ${status}`, 'success', 'admin-alert-container');
            loadAdminBookings();
            loadAdminStats();
        })
        .catch(err => showGlobalAlert(err.message, 'danger', 'admin-alert-container'));
}

function assignStaffToBooking(bookingId, selectEl) {
    const staffId = selectEl.value;
    if (!staffId) return;

    API.put(`/bookings/${bookingId}/assign`, null, { staffId })
        .then(() => {
            showGlobalAlert(`Staff coordinator assigned successfully to booking #${bookingId}`, 'success', 'admin-alert-container');
            loadAdminBookings();
        })
        .catch(err => showGlobalAlert(err.message, 'danger', 'admin-alert-container'));
}

// ------------------- ORDERS WORKFLOW -------------------
function loadAdminOrders() {
    const body = document.getElementById('admin-orders-table-body');
    API.get('/orders/all')
        .then(list => {
            if (list.length === 0) {
                body.innerHTML = `<tr><td colspan="9" class="text-secondary py-4">No product orders placed.</td></tr>`;
                return;
            }

            body.innerHTML = '';
            list.forEach(order => {
                let statusBadge = `<span class="badge bg-secondary">Pending</span>`;
                if (order.status === 'SHIPPED') statusBadge = `<span class="badge bg-warning text-dark">Shipped</span>`;
                if (order.status === 'DELIVERED') statusBadge = `<span class="badge bg-success">Delivered</span>`;
                if (order.status === 'CANCELLED') statusBadge = `<span class="badge bg-danger">Cancelled</span>`;

                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="font-monospace text-warning">#O-${order.id}</td>
                    <td>
                        <strong class="text-white">${order.customer.name}</strong><br>
                        <small class="text-secondary">${order.phone}</small>
                    </td>
                    <td class="text-white font-heading">${order.product.name}</td>
                    <td class="font-monospace">${order.quantity}</td>
                    <td class="text-warning font-monospace fw-bold">INR ${order.price.toFixed(2)}</td>
                    <td class="small" style="max-width: 180px;">${order.shippingAddress}</td>
                    <td class="small">${new Date(order.createdAt || new Date()).toLocaleDateString()}</td>
                    <td>${statusBadge}</td>
                    <td>
                        <div class="btn-group btn-group-sm">
                            <button class="btn btn-warning text-dark" onclick="updateOrderStatus(${order.id}, 'SHIPPED')">Ship</button>
                            <button class="btn btn-success" onclick="updateOrderStatus(${order.id}, 'DELIVERED')">Deliver</button>
                            <button class="btn btn-danger" onclick="updateOrderStatus(${order.id}, 'CANCELLED')">Cancel</button>
                        </div>
                    </td>
                `;
                body.appendChild(tr);
            });
        })
        .catch(err => {
            body.innerHTML = `<tr><td colspan="9" class="text-danger py-4">Failed to load purchase orders.</td></tr>`;
        });
}

function updateOrderStatus(id, status) {
    API.put(`/orders/${id}/status`, null, { status })
        .then(() => {
            showGlobalAlert(`Order #${id} marked as ${status}`, 'success', 'admin-alert-container');
            loadAdminOrders();
            loadAdminStats();
        })
        .catch(err => showGlobalAlert(err.message, 'danger', 'admin-alert-container'));
}

// ------------------- LEADS CENTER -------------------
function loadAdminLeads() {
    const leadsBody = document.getElementById('admin-leads-table-body');
    const contactBody = document.getElementById('admin-contact-table-body');

    // 1. Load export leads
    API.get('/export-enquiries/all')
        .then(list => {
            if (list.length === 0) {
                leadsBody.innerHTML = `<tr><td colspan="9" class="text-secondary py-4">No export leads found.</td></tr>`;
                return;
            }

            leadsBody.innerHTML = '';
            list.forEach(enq => {
                let statusBadge = `<span class="badge bg-secondary">Pending</span>`;
                if (enq.status === 'CONTACTED') statusBadge = `<span class="badge bg-info text-dark">Contacted</span>`;
                if (enq.status === 'CLOSED') statusBadge = `<span class="badge bg-success">Closed</span>`;

                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="font-monospace text-warning">#E-${enq.id}</td>
                    <td>
                        <strong class="text-white">${enq.name}</strong><br>
                        <small class="text-secondary">${enq.company || 'Private Broker'}</small>
                    </td>
                    <td>${enq.country}</td>
                    <td class="font-monospace text-white">${enq.quantityRequirement} kg</td>
                    <td class="badge bg-dark mt-2">${enq.businessType}</td>
                    <td class="small">${enq.email}</td>
                    <td class="small" title="${enq.message}">${enq.message ? enq.message.substring(0, 50) + '...' : ''}</td>
                    <td>${statusBadge}</td>
                    <td>
                        <button class="btn btn-sm btn-gold" onclick="updateEnquiryStatus(${enq.id}, 'CONTACTED')"><i class="bi bi-envelope-check"></i> Contact</button>
                    </td>
                `;
                leadsBody.appendChild(tr);
            });
        })
        .catch(err => {
            leadsBody.innerHTML = `<tr><td colspan="9" class="text-danger py-4">Failed to load leads.</td></tr>`;
        });

    // 2. Load contact messages
    API.get('/contact/admin/all')
        .then(list => {
            if (list.length === 0) {
                contactBody.innerHTML = `<tr><td colspan="8" class="text-secondary py-4">No inbox messages.</td></tr>`;
                return;
            }

            contactBody.innerHTML = '';
            list.forEach(msg => {
                let statusBadge = `<span class="badge bg-secondary">Pending</span>`;
                if (msg.status === 'READ') statusBadge = `<span class="badge bg-success">Read</span>`;

                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="font-monospace text-warning">#C-${msg.id}</td>
                    <td class="text-white">${msg.name}</td>
                    <td class="small">${msg.email}<br>${msg.phone || ''}</td>
                    <td class="text-white font-heading">${msg.subject}</td>
                    <td class="small">${msg.message}</td>
                    <td class="small">${new Date(msg.createdAt || new Date()).toLocaleDateString()}</td>
                    <td>${statusBadge}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-gold" onclick="updateMessageStatus(${msg.id}, 'READ')">Read</button>
                    </td>
                `;
                contactBody.appendChild(tr);
            });
        })
        .catch(err => {
            contactBody.innerHTML = `<tr><td colspan="8" class="text-danger py-4">Failed to load inbox.</td></tr>`;
        });
}

function updateEnquiryStatus(id, status) {
    API.put(`/export-enquiries/${id}/status`, null, { status })
        .then(() => {
            showGlobalAlert(`Export enquiry status updated to ${status}`, 'success', 'admin-alert-container');
            loadAdminLeads();
            loadAdminStats();
        })
        .catch(err => showGlobalAlert(err.message, 'danger', 'admin-alert-container'));
}

function updateMessageStatus(id, status) {
    API.put(`/contact/admin/${id}/status`, null, { status })
        .then(() => {
            showGlobalAlert(`Message #${id} marked as ${status}`, 'success', 'admin-alert-container');
            loadAdminLeads();
        })
        .catch(err => showGlobalAlert(err.message, 'danger', 'admin-alert-container'));
}

// ------------------- CRUD PRODUCTS -------------------
let adminProductsList = [];
function loadAdminProducts() {
    const body = document.getElementById('admin-products-table-body');
    API.get('/products/admin/all')
        .then(list => {
            adminProductsList = list;
            if (list.length === 0) {
                body.innerHTML = `<tr><td colspan="6" class="text-secondary py-4">No products.</td></tr>`;
                return;
            }

            body.innerHTML = '';
            list.forEach(p => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td><img src="${p.imageUrl || 'https://images.unsplash.com/photo-1596040033229-a9821ebd058d?auto=format&fit=crop&w=800&q=80'}" class="rounded" style="width:50px; height:50px; object-fit:cover;"></td>
                    <td class="text-white royal-font fw-bold">${p.name}</td>
                    <td><span class="badge bg-dark">${p.category}</span></td>
                    <td class="font-monospace text-warning fw-bold">INR ${p.price.toFixed(2)}</td>
                    <td>${p.availability ? '<span class="badge bg-success">Active</span>' : '<span class="badge bg-danger">Hidden</span>'}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-warning text-warning me-1" onclick="openEditProductModal(${p.id})">Edit</button>
                        <button class="btn btn-sm btn-danger" onclick="deleteProductItem(${p.id})">Delete</button>
                    </td>
                `;
                body.appendChild(tr);
            });
        })
        .catch(err => {
            body.innerHTML = `<tr><td colspan="6" class="text-danger py-4">Failed to load products.</td></tr>`;
        });
}

function openAddProductModal() {
    document.getElementById('productCrudTitle').textContent = 'Add Product Details';
    document.getElementById('product-crud-form').reset();
    document.getElementById('crud-prod-id').value = '';
    bsProductModal.show();
}

function openEditProductModal(id) {
    const p = adminProductsList.find(item => item.id === id);
    if (!p) return;

    document.getElementById('productCrudTitle').textContent = 'Edit Product Details';
    document.getElementById('crud-prod-id').value = p.id;
    document.getElementById('crud-prod-name').value = p.name;
    document.getElementById('crud-prod-cat').value = p.category;
    document.getElementById('crud-prod-price').value = p.price;
    document.getElementById('crud-prod-avail').value = p.availability.toString();
    document.getElementById('crud-prod-img').value = p.imageUrl || '';
    document.getElementById('crud-prod-desc').value = p.description;

    bsProductModal.show();
}

function handleProductSubmit(e) {
    e.preventDefault();
    const id = document.getElementById('crud-prod-id').value;
    const req = {
        name: document.getElementById('crud-prod-name').value,
        category: document.getElementById('crud-prod-cat').value,
        price: parseFloat(document.getElementById('crud-prod-price').value),
        availability: document.getElementById('crud-prod-avail').value === 'true',
        imageUrl: document.getElementById('crud-prod-img').value,
        description: document.getElementById('crud-prod-desc').value
    };

    let apiCall;
    if (id) {
        apiCall = API.put(`/products/admin/${id}`, req);
    } else {
        apiCall = API.post('/products/admin', req);
    }

    apiCall.then(() => {
        showGlobalAlert('Product catalog entry saved successfully!', 'success', 'admin-alert-container');
        bsProductModal.hide();
        loadAdminProducts();
    }).catch(err => showGlobalAlert(err.message, 'danger', 'admin-alert-container'));
}

function deleteProductItem(id) {
    if (!confirm('Are you sure you want to delete this product catalog entry?')) return;
    API.delete(`/products/admin/${id}`)
        .then(() => {
            showGlobalAlert('Product deleted successfully', 'success', 'admin-alert-container');
            loadAdminProducts();
        })
        .catch(err => showGlobalAlert(err.message, 'danger', 'admin-alert-container'));
}

// ------------------- CRUD BLOGS -------------------
let adminBlogsList = [];
function loadAdminBlogs() {
    const body = document.getElementById('admin-blogs-table-body');
    API.get('/blogs')
        .then(list => {
            adminBlogsList = list;
            if (list.length === 0) {
                body.innerHTML = `<tr><td colspan="5" class="text-secondary py-4">No blogs published.</td></tr>`;
                return;
            }

            body.innerHTML = '';
            list.forEach(b => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="text-white fw-bold royal-font">${b.title}</td>
                    <td>${b.authorName}</td>
                    <td class="small">${new Date(b.createdAt || new Date()).toLocaleDateString()}</td>
                    <td class="small text-secondary" style="max-width: 250px;">${b.content.substring(0, 80)}...</td>
                    <td>
                        <button class="btn btn-sm btn-outline-warning text-warning me-1" onclick="openEditBlogModal(${b.id})">Edit</button>
                        <button class="btn btn-sm btn-danger" onclick="deleteBlogItem(${b.id})">Delete</button>
                    </td>
                `;
                body.appendChild(tr);
            });
        })
        .catch(err => {
            body.innerHTML = `<tr><td colspan="5" class="text-danger py-4">Failed to load blogs.</td></tr>`;
        });
}

function openAddBlogModal() {
    document.getElementById('blogCrudTitle').textContent = 'Publish Blog Article';
    document.getElementById('blog-crud-form').reset();
    document.getElementById('crud-blog-id').value = '';
    bsBlogModal.show();
}

function openEditBlogModal(id) {
    const b = adminBlogsList.find(item => item.id === id);
    if (!b) return;

    document.getElementById('blogCrudTitle').textContent = 'Edit Published Article';
    document.getElementById('crud-blog-id').value = b.id;
    document.getElementById('crud-blog-title').value = b.title;
    document.getElementById('crud-blog-author').value = b.authorName;
    document.getElementById('crud-blog-img').value = b.imageUrl || '';
    document.getElementById('crud-blog-content').value = b.content;

    bsBlogModal.show();
}

function handleBlogSubmit(e) {
    e.preventDefault();
    const id = document.getElementById('crud-blog-id').value;
    const req = {
        title: document.getElementById('crud-blog-title').value,
        authorName: document.getElementById('crud-blog-author').value,
        imageUrl: document.getElementById('crud-blog-img').value,
        content: document.getElementById('crud-blog-content').value
    };

    let apiCall;
    if (id) {
        apiCall = API.put(`/blogs/admin/${id}`, req);
    } else {
        apiCall = API.post('/blogs/admin', req);
    }

    apiCall.then(() => {
        showGlobalAlert('Blog published successfully!', 'success', 'admin-alert-container');
        bsBlogModal.hide();
        loadAdminBlogs();
    }).catch(err => showGlobalAlert(err.message, 'danger', 'admin-alert-container'));
}

function deleteBlogItem(id) {
    if (!confirm('Are you sure you want to delete this blog post?')) return;
    API.delete(`/blogs/admin/${id}`)
        .then(() => {
            showGlobalAlert('Blog post deleted successfully', 'success', 'admin-alert-container');
            loadAdminBlogs();
        })
        .catch(err => showGlobalAlert(err.message, 'danger', 'admin-alert-container'));
}

// ------------------- CRUD GALLERY -------------------
function loadAdminGallery() {
    const body = document.getElementById('admin-gallery-table-body');
    API.get('/gallery')
        .then(list => {
            if (list.length === 0) {
                body.innerHTML = `<tr><td colspan="4" class="text-secondary py-4">No gallery items.</td></tr>`;
                return;
            }

            body.innerHTML = '';
            list.forEach(item => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td><img src="${item.imageUrl || 'https://images.unsplash.com/photo-1511795409834-ef04bbd61622?auto=format&fit=crop&w=400&q=80'}" class="rounded" style="width:50px; height:50px; object-fit:cover;"></td>
                    <td class="text-white fw-bold">${item.title}</td>
                    <td><span class="badge bg-warning text-dark">${item.category}</span></td>
                    <td>
                        <button class="btn btn-sm btn-danger" onclick="deleteGalleryItem(${item.id})"><i class="bi bi-trash"></i></button>
                    </td>
                `;
                body.appendChild(tr);
            });
        })
        .catch(err => {
            body.innerHTML = `<tr><td colspan="4" class="text-danger py-4">Failed to load media.</td></tr>`;
        });
}

function openAddGalleryModal() {
    document.getElementById('gallery-crud-form').reset();
    bsGalleryModal.show();
}

function handleGallerySubmit(e) {
    e.preventDefault();
    const req = {
        title: document.getElementById('crud-gal-title').value,
        category: document.getElementById('crud-gal-cat').value,
        imageUrl: document.getElementById('crud-gal-url').value
    };

    API.post('/gallery/admin', req)
        .then(() => {
            showGlobalAlert('Media added to gallery!', 'success', 'admin-alert-container');
            bsGalleryModal.hide();
            loadAdminGallery();
        })
        .catch(err => showGlobalAlert(err.message, 'danger', 'admin-alert-container'));
}

function deleteGalleryItem(id) {
    if (!confirm('Remove this image from public gallery?')) return;
    API.delete(`/gallery/admin/${id}`)
        .then(() => {
            showGlobalAlert('Gallery item deleted', 'success', 'admin-alert-container');
            loadAdminGallery();
        })
        .catch(err => showGlobalAlert(err.message, 'danger', 'admin-alert-container'));
}

// ------------------- CRUD TESTIMONIALS -------------------
function loadAdminTestimonials() {
    const body = document.getElementById('admin-testimonials-table-body');
    API.get('/testimonials')
        .then(list => {
            if (list.length === 0) {
                body.innerHTML = `<tr><td colspan="5" class="text-secondary py-4">No reviews recorded.</td></tr>`;
                return;
            }

            body.innerHTML = '';
            list.forEach(t => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="text-white royal-font fw-bold">${t.authorName}</td>
                    <td class="font-monospace text-warning">${t.rating} Stars</td>
                    <td class="small text-secondary" style="max-width:300px;">"${t.text}"</td>
                    <td class="small">${t.authorRole || 'Guest'}</td>
                    <td>
                        <button class="btn btn-sm btn-danger" onclick="deleteTestimonialItem(${t.id})"><i class="bi bi-trash"></i></button>
                    </td>
                `;
                body.appendChild(tr);
            });
        })
        .catch(err => {
            body.innerHTML = `<tr><td colspan="5" class="text-danger py-4">Failed to load testimonials list.</td></tr>`;
        });
}

function openAddTestimonialModal() {
    document.getElementById('testimonial-crud-form').reset();
    bsTestimonialModal.show();
}

function handleTestimonialSubmit(e) {
    e.preventDefault();
    const req = {
        authorName: document.getElementById('crud-test-name').value,
        authorRole: document.getElementById('crud-test-role').value,
        rating: parseInt(document.getElementById('crud-test-rating').value),
        text: document.getElementById('crud-test-text').value
    };

    API.post('/testimonials/admin', req)
        .then(() => {
            showGlobalAlert('Testimonial review added!', 'success', 'admin-alert-container');
            bsTestimonialModal.hide();
            loadAdminTestimonials();
        })
        .catch(err => showGlobalAlert(err.message, 'danger', 'admin-alert-container'));
}

function deleteTestimonialItem(id) {
    if (!confirm('Are you sure you want to delete this testimonial?')) return;
    API.delete(`/testimonials/admin/${id}`)
        .then(() => {
            showGlobalAlert('Testimonial deleted', 'success', 'admin-alert-container');
            loadAdminTestimonials();
        })
        .catch(err => showGlobalAlert(err.message, 'danger', 'admin-alert-container'));
}

// ------------------- USER DIRECTORY -------------------
function loadAdminUsers() {
    const body = document.getElementById('admin-users-table-body');
    API.get('/users/admin/all')
        .then(list => {
            if (list.length === 0) {
                body.innerHTML = `<tr><td colspan="7" class="text-secondary py-4">No users.</td></tr>`;
                return;
            }

            body.innerHTML = '';
            list.forEach(u => {
                const roles = ['ROLE_CUSTOMER', 'ROLE_STAFF', 'ROLE_SUPER_ADMIN'];
                let roleOptions = '';
                roles.forEach(r => {
                    const selected = u.role.name === r ? 'selected' : '';
                    roleOptions += `<option value="${r}" ${selected}>${r.replace('ROLE_', '')}</option>`;
                });

                const currentRole = localStorage.getItem('user_role');
                const isSuperAdmin = currentRole === 'ROLE_SUPER_ADMIN';
                const disableSelector = !isSuperAdmin ? 'disabled' : '';

                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="font-monospace text-secondary">#U-${u.id}</td>
                    <td class="text-white fw-bold">${u.name}</td>
                    <td>${u.email}</td>
                    <td class="font-monospace">${u.phone || ''}</td>
                    <td><span class="badge bg-dark text-warning">${u.provider}</span></td>
                    <td><span class="badge bg-warning text-dark">${u.role.name}</span></td>
                    <td>
                        <select class="form-select form-select-sm form-royal-control d-inline-block w-auto me-1" ${disableSelector} onchange="changeUserRole(${u.id}, this.value)">
                            ${roleOptions}
                        </select>
                        <button class="btn btn-sm btn-danger" ${disableSelector} onclick="deleteUserItem(${u.id})"><i class="bi bi-trash"></i></button>
                    </td>
                `;
                body.appendChild(tr);
            });
        })
        .catch(err => {
            body.innerHTML = `<tr><td colspan="7" class="text-danger py-4">Failed to load directory.</td></tr>`;
        });
}

function changeUserRole(userId, newRole) {
    if (!confirm(`Are you sure you want to change user role to ${newRole}?`)) {
        loadAdminUsers();
        return;
    }
    API.put(`/users/admin/${userId}/role?roleName=${newRole}`)
        .then(() => {
            showGlobalAlert(`User role updated to: ${newRole}`, 'success', 'admin-alert-container');
            loadAdminUsers();
        })
        .catch(err => {
            showGlobalAlert(err.message || "Failed to update role. Only Super Admin is allowed.", 'danger', 'admin-alert-container');
            loadAdminUsers();
        });
}

function deleteUserItem(userId) {
    if (!confirm('Are you sure you want to delete this user account?')) return;
    API.delete(`/users/admin/${userId}`)
        .then(() => {
            showGlobalAlert('User account deleted successfully.', 'success', 'admin-alert-container');
            loadAdminUsers();
        })
        .catch(err => {
            showGlobalAlert(err.message || "Failed to delete user. Only Super Admin is allowed.", 'danger', 'admin-alert-container');
            loadAdminUsers();
        });
}

// ------------------- CRUD FAQS -------------------
let adminFaqsList = [];
let bsFaqModal;

function loadAdminFaqs() {
    const body = document.getElementById('admin-faqs-table-body');
    if (!body) return;

    fetch('/api/v1/faqs')
        .then(res => res.json())
        .then(list => {
            adminFaqsList = list;
            if (list.length === 0) {
                body.innerHTML = `<tr><td colspan="4" class="text-secondary py-4">No FAQs published.</td></tr>`;
                return;
            }

            body.innerHTML = '';
            list.forEach(item => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="text-white fw-bold royal-font" style="max-width: 200px;">${item.question}</td>
                    <td class="small text-secondary" style="max-width: 350px;">${item.answer}</td>
                    <td class="font-monospace">${item.displayOrder}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-warning text-warning me-1" onclick="openEditFaqModal(${item.id})">Edit</button>
                        <button class="btn btn-sm btn-danger" onclick="deleteFaqItem(${item.id})">Delete</button>
                    </td>
                `;
                body.appendChild(tr);
            });
        })
        .catch(err => {
            body.innerHTML = `<tr><td colspan="4" class="text-danger py-4">Failed to load FAQs.</td></tr>`;
        });
}

function openAddFaqModal() {
    document.getElementById('faqCrudTitle').textContent = 'Add FAQ Details';
    document.getElementById('faq-crud-form').reset();
    document.getElementById('crud-faq-id').value = '';
    bsFaqModal.show();
}

function openEditFaqModal(id) {
    const item = adminFaqsList.find(i => i.id === id);
    if (!item) return;

    document.getElementById('faqCrudTitle').textContent = 'Edit FAQ Details';
    document.getElementById('crud-faq-id').value = item.id;
    document.getElementById('crud-faq-question').value = item.question;
    document.getElementById('crud-faq-answer').value = item.answer;
    document.getElementById('crud-faq-order').value = item.displayOrder;

    bsFaqModal.show();
}

function handleFaqSubmit(e) {
    e.preventDefault();
    const id = document.getElementById('crud-faq-id').value;
    const req = {
        question: document.getElementById('crud-faq-question').value,
        answer: document.getElementById('crud-faq-answer').value,
        displayOrder: parseInt(document.getElementById('crud-faq-order').value) || 0
    };

    const token = localStorage.getItem('jwt_token');
    const method = id ? 'PUT' : 'POST';
    const url = id ? `/api/v1/faqs/${id}` : '/api/v1/faqs';

    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(req)
    })
    .then(res => res.json())
    .then(() => {
        showGlobalAlert('FAQ entry saved successfully!', 'success', 'admin-alert-container');
        bsFaqModal.hide();
        loadAdminFaqs();
    })
    .catch(err => showGlobalAlert("Error saving FAQ.", 'danger', 'central-media-grid'));
}

function deleteFaqItem(id) {
    if (!confirm('Are you sure you want to delete this FAQ entry?')) return;
    const token = localStorage.getItem('jwt_token');
    fetch(`/api/v1/faqs/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
    })
    .then(res => {
        if (res.ok) {
            showGlobalAlert('FAQ deleted successfully', 'success', 'admin-alert-container');
            loadAdminFaqs();
        } else {
            alert("Delete failed.");
        }
    })
    .catch(err => showGlobalAlert(err.message, 'danger', 'admin-alert-container'));
}

// ------------------- CENTRAL MEDIA LIBRARY -------------------
function loadCentralMedia() {
    const grid = document.getElementById('central-media-grid');
    if (!grid) return;

    const token = localStorage.getItem('jwt_token');
    fetch('/api/v1/media', {
        headers: { 'Authorization': `Bearer ${token}` }
    })
    .then(res => res.json())
    .then(files => {
        grid.innerHTML = '';
        if (files.length === 0) {
            grid.innerHTML = '<div class="col-12 text-center py-4 text-secondary">No files in media library. Upload one above.</div>';
            return;
        }
        files.forEach(file => {
            const col = document.createElement('div');
            col.className = 'col-md-3 col-sm-6 fade-in-up';
            
            let preview = '';
            if (file.fileType === 'IMAGE') {
                preview = `<img src="${file.fileUrl}" class="card-img-top object-fit-cover" style="height: 150px;">`;
            } else {
                preview = `<div class="d-flex align-items-center justify-content-center bg-dark" style="height: 150px;"><i class="bi bi-file-earmark-play-fill text-warning display-5"></i></div>`;
            }

            col.innerHTML = `
                <div class="card bg-dark text-white h-100 royal-border">
                    ${preview}
                    <div class="card-body p-2 d-flex flex-column justify-content-between">
                        <div>
                            <p class="card-text text-truncate small m-0 fw-bold">${file.filename}</p>
                            <small class="text-secondary font-monospace d-block mb-2">${(file.fileSize / 1024).toFixed(1)} KB</small>
                        </div>
                        <div class="d-flex gap-2">
                            <button class="btn btn-sm btn-outline-warning w-100 px-1" onclick="copyMediaLink('${file.fileUrl}')"><i class="bi bi-link-45deg"></i> Link</button>
                            <button class="btn btn-sm btn-danger px-2" onclick="deleteCentralMediaItem(${file.id})"><i class="bi bi-trash"></i></button>
                        </div>
                    </div>
                </div>
            `;
            grid.appendChild(col);
        });
    })
    .catch(err => {
        grid.innerHTML = '<div class="col-12 text-center py-4 text-danger">Failed to load media assets.</div>';
    });
}

function uploadCentralMedia() {
    const input = document.getElementById('central-media-upload-input');
    if (!input || input.files.length === 0) {
        alert("Please select a file to upload");
        return;
    }

    const formData = new FormData();
    formData.append('file', input.files[0]);

    const token = localStorage.getItem('jwt_token');
    fetch('/api/v1/media/upload', {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` },
        body: formData
    })
    .then(res => res.json())
    .then(data => {
        input.value = '';
        showGlobalAlert('File uploaded successfully!', 'success', 'admin-alert-container');
        loadCentralMedia();
    })
    .catch(err => alert("File upload failed. Max limit is 10MB."));
}

function deleteCentralMediaItem(id) {
    if (!confirm('Are you sure you want to permanently delete this media asset?')) return;
    const token = localStorage.getItem('jwt_token');
    fetch(`/api/v1/media/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
    })
    .then(res => {
        if (res.ok) {
            showGlobalAlert('Asset deleted successfully', 'success', 'admin-alert-container');
            loadCentralMedia();
        } else {
            alert("Delete failed.");
        }
    })
    .catch(err => showGlobalAlert(err.message, 'danger', 'admin-alert-container'));
}

function copyMediaLink(url) {
    const fullUrl = window.location.origin + url;
    navigator.clipboard.writeText(fullUrl)
        .then(() => alert("Copied direct link to clipboard: " + fullUrl))
        .catch(() => alert("Failed to copy link."));
}
