<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Pinned Notices - EIMS</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <script src="https://cdn.jsdelivr.net/npm/vue@2.6.14/dist/vue.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
</head>
<body>
<div id="app" class="container mt-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>Pinned Notices</h1>
        <a href="/notices" class="btn btn-outline-primary">Back to All Notices</a>
    </div>

    <div class="alert alert-info">
        "This view highlights our most critical announcements! Pinned to the top for everyone to see."
    </div>

    <!-- Notice List (Pinned Only) -->
    <div class="row">
        <div class="col-md-12">
            <div v-if="loading" class="text-center mt-5">
                <div class="spinner-border" role="status">
                    <span class="sr-only">Loading...</span>
                </div>
            </div>
            <div v-else-if="pinnedNotices.length === 0" class="alert alert-warning">
                No pinned notices available at this time.
            </div>
            <ul class="list-group" v-else>
                <li v-for="notice in pinnedNotices" :key="notice.id" class="list-group-item">
                    <div class="d-flex w-100 justify-content-between">
                        <h5 class="mb-1 text-primary">
                            <span class="badge badge-warning mr-2">PINNED</span>
                            {{ notice.title }}
                        </h5>
                        <small>Created: {{ new Date(notice.createdAt).toLocaleDateString() }}</small>
                    </div>
                    <div class="mt-2 text-muted">
                        <p style="white-space: pre-wrap;">{{ notice.content }}</p>
                    </div>
                    <div v-if="notice.files && notice.files.length > 0" class="mt-3">
                        <strong>Attachments:</strong>
                        <ul class="list-unstyled ml-3">
                            <li v-for="file in notice.files" :key="file.id">
                                <i class="fas fa-paperclip"></i>
                                <a :href="'/api/notices/files/' + file.id + '/download'">{{ file.originalFilename }}</a>
                                <span class="text-muted small">({{ Math.round(file.fileSize / 1024) }} KB)</span>
                            </li>
                        </ul>
                    </div>
                </li>
            </ul>
        </div>
    </div>
</div>

<script>
    new Vue({
        el: '#app',
        data: {
            pinnedNotices: [],
            loading: true
        },
        mounted() {
            this.fetchPinnedNotices();
        },
        methods: {
            fetchPinnedNotices() {
                // Since our API currently returns all active notices, we filter on the client side for this dedicated view.
                // In an enterprise app, we'd add an explicit endpoint (e.g., /api/notices/pinned) for efficiency!
                axios.get('/api/notices')
                    .then(response => {
                        const allActive = response.data;
                        // Filter the list for pinned notices
                        const pinnedList = allActive.filter(n => n.pinned);

                        // We need the full details (content, files) for this view, so we fetch each
                        let detailPromises = pinnedList.map(p => axios.get('/api/notices/' + p.id));

                        Promise.all(detailPromises).then(results => {
                            this.pinnedNotices = results.map(r => r.data).map(detail => {
                                // merge list dto and detail dto data
                                const original = pinnedList.find(pl => pl.id === detail.id);
                                return {...original, ...detail};
                            });
                            this.loading = false;
                        });

                        if(pinnedList.length === 0) {
                             this.loading = false;
                        }
                    })
                    .catch(error => {
                        console.error("Error fetching pinned notices", error);
                        this.loading = false;
                    });
            }
        }
    });
</script>
</body>
</html>
