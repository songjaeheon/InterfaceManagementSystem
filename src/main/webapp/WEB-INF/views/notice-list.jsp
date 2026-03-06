<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>EIMS Notice Board</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <script src="https://cdn.jsdelivr.net/npm/vue@2.6.14/dist/vue.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
</head>
<body>
<div id="app" class="container mt-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>Notice Board</h1>
        <a href="/notices/pinned" class="btn btn-outline-warning">View Pinned Notices</a>
    </div>

    <!-- Create Notice Form -->
    <div class="card mb-4">
        <div class="card-header">
            Create New Notice
        </div>
        <div class="card-body">
            <form @submit.prevent="createNotice" enctype="multipart/form-data">
                <div class="form-group">
                    <label>Title</label>
                    <input type="text" v-model="newNotice.title" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Content</label>
                    <textarea v-model="newNotice.content" class="form-control" required></textarea>
                </div>
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label>Start Date</label>
                        <input type="datetime-local" v-model="newNotice.startDate" class="form-control">
                    </div>
                    <div class="form-group col-md-6">
                        <label>End Date</label>
                        <input type="datetime-local" v-model="newNotice.endDate" class="form-control">
                    </div>
                </div>
                <div class="form-check mb-3">
                    <input type="checkbox" v-model="newNotice.isPinned" class="form-check-input" id="pinnedCheck">
                    <label class="form-check-label" for="pinnedCheck">Pin to Top</label>
                </div>
                <div class="form-group">
                    <label>Attachments (Multiple)</label>
                    <input type="file" ref="fileInput" class="form-control-file" multiple @change="handleFileUpload">
                </div>
                <button type="submit" class="btn btn-primary">Create Notice</button>
            </form>
        </div>
    </div>

    <!-- Notice List -->
    <h3>All Active Notices</h3>
    <ul class="list-group mb-5">
        <li v-for="notice in notices" :key="notice.id" class="list-group-item">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <div>
                    <span v-if="notice.pinned" class="badge badge-warning mr-2">PINNED</span>
                    <a href="#" @click.prevent="openNotice(notice.id)" class="h5">{{ notice.title }}</a>
                    <br>
                    <small class="text-muted">Created: {{ new Date(notice.createdAt).toLocaleString() }}</small>
                </div>
                <button @click="deleteNotice(notice.id)" class="btn btn-sm btn-danger">Delete</button>
            </div>
            <!-- Expanded Details (loaded on demand) -->
            <div v-if="expandedNoticeId === notice.id" class="mt-3 p-3 bg-light border">
                <div v-if="noticeDetails">
                    <p style="white-space: pre-wrap;">{{ noticeDetails.content }}</p>
                    <div v-if="noticeDetails.files && noticeDetails.files.length > 0">
                        <strong>Attachments:</strong>
                        <ul>
                            <li v-for="file in noticeDetails.files" :key="file.id">
                                <a :href="'/api/notices/files/' + file.id + '/download'">{{ file.originalFilename }}</a>
                                ({{ Math.round(file.fileSize / 1024) }} KB)
                            </li>
                        </ul>
                    </div>
                </div>
                <div v-else>Loading details...</div>
            </div>
        </li>
    </ul>
</div>

<script>
    new Vue({
        el: '#app',
        data: {
            notices: [],
            newNotice: {
                title: '',
                content: '',
                startDate: '',
                endDate: '',
                isPinned: false
            },
            files: [],
            expandedNoticeId: null,
            noticeDetails: null
        },
        mounted() {
            this.fetchNotices();
        },
        methods: {
            fetchNotices() {
                axios.get('/api/notices')
                    .then(response => {
                        this.notices = response.data;
                    })
                    .catch(error => console.error(error));
            },
            handleFileUpload(event) {
                this.files = Array.from(event.target.files);
            },
            createNotice() {
                let formData = new FormData();
                formData.append('title', this.newNotice.title);
                formData.append('content', this.newNotice.content);

                if (this.newNotice.startDate) {
                    let startDate = this.newNotice.startDate;
                    if (startDate.length === 16) startDate += ':00';
                    formData.append('startDate', startDate);
                }
                if (this.newNotice.endDate) {
                    let endDate = this.newNotice.endDate;
                    if (endDate.length === 16) endDate += ':00';
                    formData.append('endDate', endDate);
                }

                formData.append('isPinned', this.newNotice.isPinned);
                formData.append('isActive', true);

                this.files.forEach(file => {
                    formData.append('files', file);
                });

                axios.post('/api/notices', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                })
                .then(response => {
                    this.fetchNotices();
                    this.resetForm();
                })
                .catch(error => {
                    console.error(error);
                    alert("Error creating notice");
                });
            },
            openNotice(id) {
                if (this.expandedNoticeId === id) {
                    this.expandedNoticeId = null; // Toggle off
                    this.noticeDetails = null;
                } else {
                    this.expandedNoticeId = id;
                    this.noticeDetails = null;
                    axios.get('/api/notices/' + id)
                        .then(response => {
                            this.noticeDetails = response.data;
                        })
                        .catch(e => console.error(e));
                }
            },
            deleteNotice(id) {
                if(confirm("Are you sure? This will delete the database record and physical files.")) {
                    axios.delete('/api/notices/' + id)
                    .then(() => this.fetchNotices())
                    .catch(e => alert("Error deleting"));
                }
            },
            resetForm() {
                this.newNotice = {
                    title: '',
                    content: '',
                    startDate: '',
                    endDate: '',
                    isPinned: false
                };
                this.files = [];
                this.$refs.fileInput.value = '';
            }
        }
    });
</script>
</body>
</html>
