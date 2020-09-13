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

new Vue({
    el: "#app",
    data() {
        return {
            loading: false,
            venues: []
        }
    },
    methods: {
        viewOnMap(location) {
            console.log(location)
        }
    },
    async mounted() {
        this.venues = await request('/api/burgers')
    }

})