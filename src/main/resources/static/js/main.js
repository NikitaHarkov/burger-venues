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
    <div style="display: flex; justify-content: center; align-items: center" class="pt-2">
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
        viewOnMap(coordinates) {
            const location = coordinates.lat + ", " + coordinates.lng
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