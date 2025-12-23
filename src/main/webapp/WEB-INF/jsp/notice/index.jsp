<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Notice Dashboard - Vue.js Edition</title>
    <!-- Load Vue.js from CDN (Bootifully simple!) -->
    <script src="https://cdn.jsdelivr.net/npm/vue@2.6.14/dist/vue.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
    <style>
        body { font-family: sans-serif; padding: 20px; }
        .notice-card { border: 1px solid #ddd; padding: 15px; margin-bottom: 10px; border-radius: 5px; }
        .notice-title { font-weight: bold; font-size: 1.2em; }
        .notice-date { color: #888; font-size: 0.9em; }
    </style>
</head>
<body>

<h1>Latest Notices (Powered by Vue.js)</h1>

<div id="app">
    <div v-if="loading">Loading notices...</div>
    <div v-else>
        <div v-for="notice in notices" :key="notice.id" class="notice-card">
            <div class="notice-title">[[ notice.title ]]</div>
            <div class="notice-date">[[ notice.createdAt ]]</div>
            <p>[[ notice.content ]]</p>
        </div>

        <p v-if="notices.length === 0">No notices found.</p>
    </div>

    <br/>
    <a href="/notices">View All (Standard JSP List)</a>
</div>

<script>
    // Look at that! We're using Vue to bring interactivity to our legacy JSP.
    // This is progressive enhancement at its finest!
    new Vue({
        el: '#app',
        delimiters: ['[[', ']]'], // Changing delimiters to avoid conflict with JSP's ${}
        data: {
            notices: [],
            loading: true
        },
        mounted() {
            // Fetching data from our Bootiful REST API
            axios.get('/api/notices')
                .then(response => {
                    // Taking just the first 3 for the dashboard
                    this.notices = response.data.slice(0, 3);
                    this.loading = false;
                })
                .catch(error => {
                    console.error("Error fetching notices:", error);
                    this.loading = false;
                });
        }
    });
</script>

</body>
</html>
