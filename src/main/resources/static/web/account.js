window.onload = function () {
    $('#onload').fadeOut();
    $('body').removeClass('hidden');
}

Vue.createApp({
    data() {
        return {
            clients: [],
            transactions: [],
            account: [],
            url: "",
            activo: true,
            cuenta: "",

            desde: "",
            hasta: "",
        }
    },

    created() {

        const urlParams = new URLSearchParams(window.location.search);
        const myParam = urlParams.get('id');

        this.url = `/api/accounts/${myParam}`

        axios.get(this.url)
            .then(datos => {
                this.transactions = datos.data.transactions.sort((x, y) => y.id - x.id)
                this.account = datos.data
                this.cuenta = datos.data.number
            })

        axios.get("/api/clients/current")
        .then(datos => {
            this.clients = datos.data})

    },

    methods: {

        descargarPdf() {
            Swal.fire({
                title: '¿Estas seguro/a?',
                text: "No podrás revertir esto!",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Si, descargar!'
            }).then((result) => {
                if (result.isConfirmed) {
                    const urlParams = new URLSearchParams(window.location.search);
                    const myParam = urlParams.get('id');
                    axios.post(`/api/pdf/${myParam}`, `desde=${this.desde}&hasta=${this.hasta}`, {'responseType': 'blob'})
                        .then(response =>{
                            let url = window.URL.createObjectURL(new Blob([response.data]))
                            let link = document.createElement("a")
                            link.href = url;
                            link.setAttribute("download", `${this.cuenta}_(${this.desde})_(${this.hasta}).pdf`)
                            document.body.appendChild(link)
                            link.click()
                        })
                        .then(() => {
                            Swal.fire(
                                'pdf descargado!',
                            )
                        })
                        .catch((error) => {
                            Swal.fire({
                                icon: 'error',
                                text: error.response.data
                            })
                        })
                }
            })
        },

        logOut() {

            axios.post('/api/logout')
                .then(response => window.location.href = "./index.html")

        }
    }


}).mount('#app')