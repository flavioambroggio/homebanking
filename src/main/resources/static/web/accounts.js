window.onload = function () {
    $('#onload').fadeOut();
    $('body').removeClass('hidden');
}

document.documentElement.style.setProperty('--animate-duration', '2s');

Vue.createApp({
    data() {
        return {
            clients: [],
            accounts: [],
            loans: [],

            accountType: ""
        }
    },

    created() {
        axios.get("/api/clients/current")
            .then(datos => {
                this.clients = datos.data
                this.accounts = datos.data.accounts.sort((x, y) => x.id - y.id)
                this.loans = datos.data.loans.sort((x, y) => x.id - y.id)
            })
    },

    methods: {

        logOut() {

            axios.post('/api/logout')
                .then(response => window.location.href = "./index.html")

        },

        crearAccount() {

            Swal.fire({
                title: 'Tipo de cuenta',
                input: 'select',
                inputOptions: {
                    AHORRO: "Caja de Ahorro",
                    CORRIENTE: "Cuenta corriente"
                },
                inputPlaceholder: 'Seleccione el tipo de cuenta',
                showCancelButton: true,
                inputValidator: (value) => {
                    return new Promise((resolve) => {
                        if (value === 'AHORRO') {
                            this.accountType = "AHORRO"
                            resolve()
                        }
                        if (value === 'CORRIENTE') {
                            this.accountType = "CORRIENTE"
                            resolve()
                        }
                    })
                }
            })
                .then(response => {
                    if (response.isConfirmed) {
                        Swal.fire(
                            'Cuenta creada',
                            'Tu cuenta ha sido creada',
                            'success'
                        )
                            .then(response => {
                                axios.post("/api/clients/current/accounts", `type=${this.accountType}`)
                                    .then(response => {
                                        location.reload()
                                    })
                            })
                            .catch(error => {
                                console.log(error);
                            })
                    }
                })
        },

        eliminarCuenta(id) {
            Swal.fire({
                title: '¿Estas seguro/a?',
                text: "No podrás revertir esto!",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Si, eliminar!'
            }).then((result) => {
                if (result.isConfirmed) {
                    axios.patch(`/api/clients/current/accounts/${id}`)
                        .then(() => {
                            Swal.fire(
                                'Cuenta eliminada!',
                                'cuenta eliminda con éxito',
                                'success'
                            )
                                .then(response => {
                                    location.reload()
                                })
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

    }


}).mount('#app')

