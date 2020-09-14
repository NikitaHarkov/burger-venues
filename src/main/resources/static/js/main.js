import Vue from 'https://cdn.jsdelivr.net/npm/vue@2.6.12/dist/vue.esm.browser.js'

async function request(url, method = 'GET') {
    try {
        const response = await fetch(url, {
            method
        })
        return await response.json()
    } catch (e) {
        console.warn('Error:', e.message)
    }
}

Vue.component('loader', {
    template: `
    <div style="display: flex; justify-content: center; align-items: center" >
        <div class="spinner-border text-warning" role="status">
            <span class="sr-only">Loading...</span>
        </div>
    </div>
    `
})

new Vue({
    el: "#app",
    data() {
        return {
            loading: false,
            venues: []
        }
    },
    methods: {
        viewOnMap(venue) {
            const location = venue.location.lat + ", " + venue.location.lng
            const url = "https://maps.google.com/?q=" + location
            window.open(url)
        }
    },
    async mounted() {
        this.loading = true
        this.venues = await request('/api/burgers')
        this.loading = false
    }

})