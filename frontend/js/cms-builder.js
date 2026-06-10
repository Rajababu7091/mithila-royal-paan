// Mithila Royal Paan - Visual Page Builder & CMS Core
(function () {
    let settings = {};
    let isEditMode = false;
    let isAdmin = false;

    // Initialize on DOM load
    document.addEventListener('DOMContentLoaded', () => {
        checkAdminStatus();
        loadDynamicContent();
    });

    // Check if user is authenticated as Admin or Staff
    function checkAdminStatus() {
        const token = localStorage.getItem('jwt_token');
        const role = localStorage.getItem('user_role');
        if (token && (role === 'ROLE_SUPER_ADMIN' || role === 'ROLE_STAFF')) {
            isAdmin = true;
            injectAdminCSS();
            injectAdminToolbar();
            injectAdminModals();
        }
    }

    // Fetch site settings and map to data-cms-key elements
    function loadDynamicContent() {
        fetch('/api/v1/settings?_t=' + new Date().getTime())
            .then(res => res.json())
            .then(data => {
                settings = data;
                applySettings();
                if (isAdmin) {
                    initVisualEditor();
                }
            })
            .catch(err => console.error("Error loading CMS settings:", err));
    }

    // Replace placeholders with database values
    function applySettings() {
        // 1. Apply all data-cms-key elements on the page
        document.querySelectorAll('[data-cms-key]').forEach(el => {
            const key = el.getAttribute('data-cms-key');
            if (settings[key] !== undefined) {
                if (el.tagName === 'IMG') {
                    el.src = settings[key];
                    // Ensure image stays inside its container no matter what size the uploaded image is
                    if (!el.style.width) el.style.width = '100%';
                    if (!el.style.objectFit) el.style.objectFit = 'cover';
                    el.style.maxWidth = '100%';
                    el.removeAttribute('width');
                    el.removeAttribute('height');
                } else if (el.tagName === 'A' && el.hasAttribute('data-cms-link')) {
                    const btnTextKey = key + "_text";
                    const btnUrlKey = key + "_url";
                    if (settings[btnTextKey]) el.textContent = settings[btnTextKey];
                    if (settings[btnUrlKey]) el.href = settings[btnUrlKey];
                } else if (el.hasAttribute('data-cms-bg')) {
                    el.style.backgroundImage = `linear-gradient(rgba(0,0,0,0.7), rgba(0,0,0,0.7)), url('${settings[key]}')`;
                } else {
                    el.textContent = settings[key];
                }
            }
        });

        // Remove loading class to fade in elements smoothly without flashing
        document.body.classList.remove('cms-loading');

        // 2. SEO meta title
        if (settings['seo_meta_title']) document.title = settings['seo_meta_title'];

        // 3. Dynamic favicon
        if (settings['site_favicon']) {
            let link = document.querySelector("link[rel~='icon']");
            if (!link) { link = document.createElement('link'); link.rel = 'icon'; document.head.appendChild(link); }
            link.href = settings['site_favicon'];
        }

        // 4. Site name in header
        const siteTitleText = document.getElementById('header-site-title');
        if (siteTitleText && settings['site_name']) siteTitleText.textContent = settings['site_name'];

        // 5. Site logo
        const headerLogo = document.getElementById('header-logo-img');
        if (headerLogo && settings['site_logo']) {
            headerLogo.src = settings['site_logo'];
            headerLogo.style.display = 'inline-block';
        }

        // 6. Footer description (key in DB is 'footer_text')
        const footerText = document.getElementById('footer-about-text');
        if (footerText && settings['footer_text']) footerText.textContent = settings['footer_text'];

        // 7. Footer brand name
        const footerBrand = document.getElementById('footer-brand-name');
        if (footerBrand && settings['site_name']) footerBrand.textContent = settings['site_name'];

        // 8. Copyright text
        const copyright = document.getElementById('footer-copyright');
        if (copyright && settings['copyright_text']) copyright.textContent = settings['copyright_text'];

        // 9. Contact details (shown in footer column 3)
        const footerAddress = document.getElementById('footer-address');
        if (footerAddress && settings['contact_address']) footerAddress.textContent = settings['contact_address'];

        const footerEmail = document.getElementById('footer-email');
        if (footerEmail && settings['contact_email']) footerEmail.textContent = settings['contact_email'];

        const footerPhone = document.getElementById('footer-phone');
        if (footerPhone && settings['contact_phone']) footerPhone.textContent = settings['contact_phone'];

        // 10. Contact details in contact page
        const contactPagePhone = document.getElementById('contact-phone');
        if (contactPagePhone && settings['contact_phone']) contactPagePhone.textContent = settings['contact_phone'];

        const contactPageEmail = document.getElementById('contact-email');
        if (contactPageEmail && settings['contact_email']) contactPageEmail.textContent = settings['contact_email'];

        const contactPageAddress = document.getElementById('contact-address');
        if (contactPageAddress && settings['contact_address']) contactPageAddress.textContent = settings['contact_address'];

        // 11. Social media links
        const fbLink = document.getElementById('footer-facebook');
        if (fbLink && settings['contact_facebook']) fbLink.href = settings['contact_facebook'];

        const igLink = document.getElementById('footer-instagram');
        if (igLink && settings['contact_instagram']) igLink.href = settings['contact_instagram'];

        const waLink = document.getElementById('footer-whatsapp');
        if (waLink && settings['contact_whatsapp']) waLink.href = 'https://wa.me/' + settings['contact_whatsapp'].replace(/[^0-9]/g, '');

        // 12. Apply theme colors
        if (settings['theme_colors']) {
            try {
                const colors = JSON.parse(settings['theme_colors']);
                const root = document.documentElement;
                if (colors.primary) root.style.setProperty('--bg-primary', colors.primary);
                if (colors.secondary) root.style.setProperty('--bg-secondary', colors.secondary);
                if (colors.accent) root.style.setProperty('--color-warning', colors.accent);
            } catch (e) { console.error('Theme parse error:', e); }
        }
    }

    // visual builder hover and click listeners
    function initVisualEditor() {
        document.querySelectorAll('[data-cms-key]').forEach(el => {
            el.classList.add('cms-editable-zone');
            
            // Add click listener to trigger modal edit
            el.addEventListener('click', (e) => {
                if (!isEditMode) return;
                e.preventDefault();
                e.stopPropagation();
                openVisualEditModal(el);
            });
        });
    }

    // Injects stylesheet for CMS editor
    function injectAdminCSS() {
        const css = `
            .cms-admin-bar {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 55px;
                background: rgba(15, 46, 34, 0.95);
                backdrop-filter: blur(10px);
                border-bottom: 2px solid #aa8618;
                z-index: 10000;
                display: flex;
                align-items: center;
                justify-content: space-between;
                padding: 0 20px;
                box-shadow: 0 4px 20px rgba(0,0,0,0.5);
                color: #fff;
                font-family: 'Outfit', sans-serif;
            }
            body {
                padding-top: 55px !important;
            }
            .cms-editable-zone {
                position: relative;
                cursor: default;
                transition: all 0.3s ease;
            }
            body.cms-edit-active .cms-editable-zone {
                cursor: pointer !important;
            }
            body.cms-edit-active .cms-editable-zone:hover {
                outline: 2px dashed #aa8618 !important;
                outline-offset: 4px;
                background-color: rgba(170, 134, 24, 0.1) !important;
            }
            .cms-badge-edit {
                position: absolute;
                top: -10px;
                right: -10px;
                background: #aa8618;
                color: #fff;
                padding: 2px 6px;
                font-size: 10px;
                border-radius: 4px;
                display: none;
                z-index: 999;
                pointer-events: none;
            }
            body.cms-edit-active .cms-editable-zone:hover .cms-badge-edit {
                display: block;
            }
            .media-item-card {
                cursor: pointer;
                border: 1px solid rgba(255,255,255,0.1);
                transition: all 0.2s ease;
            }
            .media-item-card:hover {
                border-color: #aa8618;
                transform: scale(1.03);
            }
        `;
        const style = document.createElement('style');
        style.innerHTML = css;
        document.head.appendChild(style);
    }

    // Injects floating visual toolbar
    function injectAdminToolbar() {
        const toolbar = document.createElement('div');
        toolbar.className = 'cms-admin-bar';
        toolbar.innerHTML = `
            <div class="d-flex align-items-center">
                <span class="fw-bold text-warning me-3" style="font-size: 16px;"><i class="bi bi-shield-lock-fill me-1"></i>Mithila CMS Builder</span>
                <div class="form-check form-switch m-0">
                    <input class="form-check-input" type="checkbox" id="cms-editor-switch" style="cursor:pointer;">
                    <label class="form-check-label text-white small" for="cms-editor-switch" style="cursor:pointer;">Visual Edit Mode</label>
                </div>
            </div>
            <div class="d-flex align-items-center gap-2">
                <button class="btn btn-sm btn-outline-warning" onclick="window.CmsBuilder.openMediaLibrary()"><i class="bi bi-images me-1"></i>Media Library</button>
                <button class="btn btn-sm btn-outline-warning" onclick="window.CmsBuilder.openSeoModal()"><i class="bi bi-search me-1"></i>SEO</button>
                <button class="btn btn-sm btn-outline-warning" onclick="window.CmsBuilder.openThemeModal()"><i class="bi bi-palette me-1"></i>Theme</button>
                <a href="/admin.html" class="btn btn-sm btn-gold"><i class="bi bi-speedometer2 me-1"></i>Dashboard</a>
            </div>
        `;
        document.body.appendChild(toolbar);

        // Edit Mode Switch Listener
        document.getElementById('cms-editor-switch').addEventListener('change', (e) => {
            isEditMode = e.target.checked;
            if (isEditMode) {
                document.body.classList.add('cms-edit-active');
            } else {
                document.body.classList.remove('cms-edit-active');
            }
        });
    }

    // Inject builder modals dynamically
    function injectAdminModals() {
        const modalContainer = document.createElement('div');
        modalContainer.id = 'cms-modal-wrapper';
        modalContainer.innerHTML = `
            <!-- Edit Content Modal -->
            <div class="modal fade" id="cmsEditModal" tabindex="-1" aria-hidden="true" style="z-index: 10500;">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content bg-secondary text-white border-warning">
                        <div class="modal-header border-bottom border-warning">
                            <h5 class="modal-title royal-font text-warning" id="cmsEditModalTitle">Edit Block Content</h5>
                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form id="cms-edit-form">
                                <input type="hidden" id="cms-edit-key">
                                <input type="hidden" id="cms-edit-type">
                                
                                <!-- Text input block -->
                                <div class="mb-3" id="cms-block-text-input">
                                    <label class="form-label text-warning small" id="cms-label-content">Text Content</label>
                                    <textarea class="form-control bg-transparent text-white border-warning" id="cms-content-textarea" rows="4"></textarea>
                                </div>

                                <!-- Link inputs block -->
                                <div id="cms-block-link-inputs" class="d-none">
                                    <div class="mb-3">
                                        <label class="form-label text-warning small">Button Text</label>
                                        <input type="text" class="form-control bg-transparent text-white border-warning" id="cms-link-text">
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label text-warning small">Redirect URL</label>
                                        <input type="text" class="form-control bg-transparent text-white border-warning" id="cms-link-url">
                                    </div>
                                </div>

                                <!-- Image input block -->
                                <div id="cms-block-image-input" class="d-none">
                                    <label class="form-label text-warning small">Image Source</label>
                                    <div class="input-group">
                                        <input type="text" class="form-control bg-transparent text-white border-warning" id="cms-image-url" readonly>
                                        <button class="btn btn-gold" type="button" onclick="window.CmsBuilder.selectMediaForImage()">Choose File</button>
                                    </div>
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer border-top border-warning">
                            <button type="button" class="btn btn-outline-light" data-bs-dismiss="modal">Cancel</button>
                            <button type="button" class="btn btn-gold" onclick="window.CmsBuilder.saveVisualEdit()">Save Changes</button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- SEO Modal -->
            <div class="modal fade" id="cmsSeoModal" tabindex="-1" aria-hidden="true" style="z-index: 10500;">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content bg-secondary text-white border-warning">
                        <div class="modal-header border-bottom border-warning">
                            <h5 class="modal-title royal-font text-warning"><i class="bi bi-search me-1"></i>Site SEO Configuration</h5>
                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form id="cms-seo-form">
                                <div class="mb-3">
                                    <label class="form-label text-warning small">Meta Title</label>
                                    <input type="text" class="form-control bg-transparent text-white border-warning" id="seo-title">
                                </div>
                                <div class="mb-3">
                                    <label class="form-label text-warning small">Meta Description</label>
                                    <textarea class="form-control bg-transparent text-white border-warning" id="seo-desc" rows="3"></textarea>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label text-warning small">Keywords (comma-separated)</label>
                                    <input type="text" class="form-control bg-transparent text-white border-warning" id="seo-keys">
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer border-top border-warning justify-content-between">
                            <button type="button" class="btn btn-outline-warning" onclick="window.CmsBuilder.triggerSitemap()">Generate Sitemap</button>
                            <div class="d-flex gap-2">
                                <button type="button" class="btn btn-outline-light" data-bs-dismiss="modal">Cancel</button>
                                <button type="button" class="btn btn-gold" onclick="window.CmsBuilder.saveSeoSettings()">Save</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Theme Customization Modal -->
            <div class="modal fade" id="cmsThemeModal" tabindex="-1" aria-hidden="true" style="z-index: 10500;">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content bg-secondary text-white border-warning">
                        <div class="modal-header border-bottom border-warning">
                            <h5 class="modal-title royal-font text-warning"><i class="bi bi-palette me-1"></i>Branding & Settings</h5>
                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form id="cms-theme-form">
                                <div class="mb-3">
                                    <label class="form-label text-warning small">Website Name</label>
                                    <input type="text" class="form-control bg-transparent text-white border-warning" id="theme-site-name">
                                </div>
                                <div class="row mb-3">
                                    <div class="col">
                                        <label class="form-label text-warning small">Primary Color</label>
                                        <input type="color" class="form-control form-control-color bg-transparent border-0" id="theme-color-primary" style="width: 100%; height:40px;">
                                    </div>
                                    <div class="col">
                                        <label class="form-label text-warning small">Secondary Color</label>
                                        <input type="color" class="form-control form-control-color bg-transparent border-0" id="theme-color-secondary" style="width: 100%; height:40px;">
                                    </div>
                                    <div class="col">
                                        <label class="form-label text-warning small">Accent Color</label>
                                        <input type="color" class="form-control form-control-color bg-transparent border-0" id="theme-color-accent" style="width: 100%; height:40px;">
                                    </div>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label text-warning small">Copyright Footer Text</label>
                                    <input type="text" class="form-control bg-transparent text-white border-warning" id="theme-copyright">
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer border-top border-warning">
                            <button type="button" class="btn btn-outline-light" data-bs-dismiss="modal">Cancel</button>
                            <button type="button" class="btn btn-gold" onclick="window.CmsBuilder.saveThemeSettings()">Save</button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Media Library Picker Modal -->
            <div class="modal fade" id="cmsMediaLibraryModal" tabindex="-1" aria-hidden="true" style="z-index: 10600;">
                <div class="modal-dialog modal-lg modal-dialog-centered">
                    <div class="modal-content bg-secondary text-white border-warning">
                        <div class="modal-header border-bottom border-warning">
                            <h5 class="modal-title royal-font text-warning"><i class="bi bi-images me-2"></i>Central Media Library</h5>
                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <div class="mb-3 border border-warning border-dashed p-3 text-center" style="border-style: dashed !important; border-radius: 8px;">
                                <label class="form-label text-warning small royal-font d-block mb-2">Upload New Asset</label>
                                <input type="file" class="form-control bg-transparent text-white border-warning w-50 d-inline-block" id="media-library-upload-input">
                                <button type="button" class="btn btn-gold ms-2" onclick="window.CmsBuilder.uploadFileToLibrary()">Upload</button>
                            </div>
                            <div class="row g-2 overflow-y-scroll" id="media-library-grid" style="max-height: 350px;">
                                <div class="col-12 text-center py-4">Loading library items...</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
        document.body.appendChild(modalContainer);
    }

    // Opens inline content editor modal
    function openVisualEditModal(el) {
        const key = el.getAttribute('data-cms-key');
        
        document.getElementById('cms-edit-key').value = key;
        
        // Check tag and attributes to see what input fields to show
        if (el.tagName === 'IMG') {
            document.getElementById('cms-edit-type').value = 'IMAGE';
            document.getElementById('cms-block-text-input').classList.add('d-none');
            document.getElementById('cms-block-link-inputs').classList.add('d-none');
            document.getElementById('cms-block-image-input').classList.remove('d-none');
            
            document.getElementById('cms-image-url').value = el.src ? el.src.replace(window.location.origin, '') : '';
            document.getElementById('cmsEditModalTitle').textContent = `Edit Block: ${key}`;
        }
        else if (el.tagName === 'A' && el.hasAttribute('data-cms-link')) {
            document.getElementById('cms-edit-type').value = 'LINK';
            document.getElementById('cms-block-text-input').classList.add('d-none');
            document.getElementById('cms-block-link-inputs').classList.remove('d-none');
            document.getElementById('cms-block-image-input').classList.add('d-none');
            
            const btnTextKey = key + "_text";
            const btnUrlKey = key + "_url";
            document.getElementById('cms-link-text').value = settings[btnTextKey] || el.textContent;
            document.getElementById('cms-link-url').value = settings[btnUrlKey] || el.getAttribute('href');
            document.getElementById('cmsEditModalTitle').textContent = `Edit Link Button: ${key}`;
        }
        else if (el.hasAttribute('data-cms-bg')) {
            document.getElementById('cms-edit-type').value = 'BG_IMAGE';
            document.getElementById('cms-block-text-input').classList.add('d-none');
            document.getElementById('cms-block-link-inputs').classList.add('d-none');
            document.getElementById('cms-block-image-input').classList.remove('d-none');
            
            document.getElementById('cms-image-url').value = settings[key] || '';
            document.getElementById('cmsEditModalTitle').textContent = `Edit Background: ${key}`;
        }
        else {
            document.getElementById('cms-edit-type').value = 'TEXT';
            document.getElementById('cms-block-text-input').classList.remove('d-none');
            document.getElementById('cms-block-link-inputs').classList.add('d-none');
            document.getElementById('cms-block-image-input').classList.add('d-none');
            
            document.getElementById('cms-content-textarea').value = settings[key] || el.textContent;
            document.getElementById('cmsEditModalTitle').textContent = `Edit Content: ${key}`;
        }

        const editModal = new bootstrap.Modal(document.getElementById('cmsEditModal'));
        editModal.show();
    }

    // Export central controllers helper
    window.CmsBuilder = {
        // Saves inline edited value
        saveVisualEdit: function () {
            const key = document.getElementById('cms-edit-key').value;
            const type = document.getElementById('cms-edit-type').value;
            let payload = {};

            if (type === 'TEXT') {
                const val = document.getElementById('cms-content-textarea').value;
                payload[key] = val;
            } else if (type === 'IMAGE' || type === 'BG_IMAGE') {
                const val = document.getElementById('cms-image-url').value;
                payload[key] = val;
            } else if (type === 'LINK') {
                const btnTextKey = key + "_text";
                const btnUrlKey = key + "_url";
                payload[btnTextKey] = document.getElementById('cms-link-text').value;
                payload[btnUrlKey] = document.getElementById('cms-link-url').value;
            }

            // Save via REST batch
            const token = localStorage.getItem('jwt_token');
            fetch('/api/v1/settings/batch', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            })
            .then(res => res.json())
            .then(data => {
                // Instantly sync & update DOM
                Object.assign(settings, payload);
                applySettings();
                bootstrap.Modal.getInstance(document.getElementById('cmsEditModal')).hide();
            })
            .catch(err => alert("Failed to save changes. Session might be expired."));
        },

        // SEO Configuration Modal
        openSeoModal: function () {
            document.getElementById('seo-title').value = settings['seo_meta_title'] || '';
            document.getElementById('seo-desc').value = settings['seo_meta_description'] || '';
            document.getElementById('seo-keys').value = settings['seo_keywords'] || '';
            
            new bootstrap.Modal(document.getElementById('cmsSeoModal')).show();
        },

        saveSeoSettings: function () {
            const payload = {
                'seo_meta_title': document.getElementById('seo-title').value,
                'seo_meta_description': document.getElementById('seo-desc').value,
                'seo_keywords': document.getElementById('seo-keys').value
            };
            
            const token = localStorage.getItem('jwt_token');
            fetch('/api/v1/settings/batch', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            })
            .then(res => res.json())
            .then(data => {
                Object.assign(settings, payload);
                applySettings();
                bootstrap.Modal.getInstance(document.getElementById('cmsSeoModal')).hide();
            })
            .catch(err => alert("Error saving SEO."));
        },

        triggerSitemap: function () {
            const token = localStorage.getItem('jwt_token');
            fetch('/api/v1/settings/sitemap', {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${token}` }
            })
            .then(res => {
                if (res.status === 200) {
                    alert("Sitemap XML generated successfully and registered to search consoles!");
                } else {
                    alert("Verification error.");
                }
            });
        },

        // Theme Customizer Modal
        openThemeModal: function () {
            document.getElementById('theme-site-name').value = settings['site_name'] || 'Mithila Royal Paan';
            document.getElementById('theme-copyright').value = settings['copyright_text'] || '';
            
            let primary = "#0a3622", secondary = "#152e22", accent = "#aa8618";
            if (settings['theme_colors']) {
                try {
                    const c = JSON.parse(settings['theme_colors']);
                    if (c.primary) primary = c.primary;
                    if (c.secondary) secondary = c.secondary;
                    if (c.accent) accent = c.accent;
                } catch(e) {}
            }
            document.getElementById('theme-color-primary').value = primary;
            document.getElementById('theme-color-secondary').value = secondary;
            document.getElementById('theme-color-accent').value = accent;

            new bootstrap.Modal(document.getElementById('cmsThemeModal')).show();
        },

        saveThemeSettings: function () {
            const colorsObj = {
                primary: document.getElementById('theme-color-primary').value,
                secondary: document.getElementById('theme-color-secondary').value,
                accent: document.getElementById('theme-color-accent').value
            };
            const payload = {
                'site_name': document.getElementById('theme-site-name').value,
                'copyright_text': document.getElementById('theme-copyright').value,
                'theme_colors': JSON.stringify(colorsObj)
            };

            const token = localStorage.getItem('jwt_token');
            fetch('/api/v1/settings/batch', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            })
            .then(res => res.json())
            .then(data => {
                Object.assign(settings, payload);
                applySettings();
                bootstrap.Modal.getInstance(document.getElementById('cmsThemeModal')).hide();
            })
            .catch(err => alert("Error saving settings."));
        },

        // Central Media Picker
        openMediaLibrary: function (callback = null) {
            this.mediaCallback = callback;
            const token = localStorage.getItem('jwt_token');
            
            const grid = document.getElementById('media-library-grid');
            grid.innerHTML = '<div class="col-12 text-center py-4">Loading library files...</div>';

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
                    col.className = 'col-md-3 col-6';
                    
                    let preview = '';
                    if (file.fileType === 'IMAGE') {
                        preview = `<img src="${file.fileUrl}" class="card-img-top object-fit-cover" style="height: 100px;">`;
                    } else {
                        preview = `<div class="d-flex align-items-center justify-content-center bg-dark" style="height: 100px;"><i class="bi bi-file-earmark-play-fill text-warning display-6"></i></div>`;
                    }

                    col.innerHTML = `
                        <div class="card bg-dark text-white media-item-card h-100" onclick="window.CmsBuilder.selectMediaItem('${file.fileUrl}')">
                            ${preview}
                            <div class="card-body p-2">
                                <p class="card-text text-truncate small m-0">${file.filename}</p>
                            </div>
                        </div>
                    `;
                    grid.appendChild(col);
                });
            });

            new bootstrap.Modal(document.getElementById('cmsMediaLibraryModal')).show();
        },

        selectMediaForImage: function () {
            // Closes active edits wrapper briefly or lays over
            this.openMediaLibrary((url) => {
                document.getElementById('cms-image-url').value = url;
            });
        },

        selectMediaItem: function (url) {
            if (this.mediaCallback) {
                this.mediaCallback(url);
                this.mediaCallback = null;
            } else {
                // If opened as general list, copy to clipboard
                navigator.clipboard.writeText(window.location.origin + url);
                alert("Copied link to clipboard: " + url);
            }
            bootstrap.Modal.getInstance(document.getElementById('cmsMediaLibraryModal')).hide();
        },

        uploadFileToLibrary: function () {
            const input = document.getElementById('media-library-upload-input');
            if (input.files.length === 0) {
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
                // Reload list
                this.openMediaLibrary(this.mediaCallback);
            })
            .catch(err => alert("File upload failed. Max limit is 10MB."));
        }
    };
})();
