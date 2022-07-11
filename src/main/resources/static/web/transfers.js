window.onload = function(){
    $('#onload').fadeOut();
    $('body').removeClass('hidden');
}

Vue.createApp({
    data() {
        return {
            clients: [],
            accounts: [],
            accountsT: [],
            amount: 0,
            description: "",
            accountOrigin: "",
            accountDestination: "",
            cuentaTerceros: false,
            error1: false,
            error2: false,
            error3: false,
            error4: false,
            error5: false,
            error6: false,
        }
    },

    created() {
        axios.get("/api/clients/current")
            .then(datos => {
                this.clients = datos.data
                this.accounts = datos.data.accounts.sort((x, y) => x.id - y.id)
            })
    },

    methods: {

        logOut() {

            axios.post('/api/logout')
                .then(response => window.location.href = "./index.html")

        },

        confirmacion() {

            swal({
                title: "¿Está seguro/a?",
                icon: "warning",
                buttons: true,
                dangerMode: true,
            })
                .then((willDelete) => {
                    if (willDelete) {
                        swal(
                            axios.post(`/api/transactions`, `amount=${this.amount}&description=${this.description}&accountOrigin=${this.accountOrigin}&accountDestination=${this.accountDestination}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                            .then(response => location.reload())
                            .catch(error => {
                                console.log(error.response.data)
                                if (error.response.data == "Missing data") {
                                    this.error1 = true;
                                }
                                if (error.response.data == "Equal accounts") {
                                    this.error2 = true;
                                }
                                if (error.response.data == "Nonexistent accountOrigin") {
                                    this.error3 = true;
                                }
                                if (error.response.data == "You are not the account owner") {
                                    this.error4 = true;
                                }
                                if (error.response.data == "Nonexistent account") {
                                    this.error5 = true;
                                }
                                if (error.response.data == "Insufficient fund") {
                                    this.error6 = true;
                                }
                            }));
                    }
                });

        }

    }


}).mount('#app')