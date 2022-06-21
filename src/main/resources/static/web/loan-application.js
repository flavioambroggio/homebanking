window.onload = function(){
    $('#onload').fadeOut();
    $('body').removeClass('hidden');
}

Vue.createApp({
    data() {
        return {
            clients: [],
            accounts: [],
            loans: [],

            nameLoan: 1,
            amountLoan: 0,
            paymentsLoan: 0,
            accountDestinationLoan: "",

            error1: false,
            error2: false,
            error3: false,
            error4: false,
            error5: false,
            error6: false,
            error7: false
        }
    },

    created() {
        axios.get("/api/clients/current")
            .then(datos => {
                this.clients = datos.data
                this.accounts = datos.data.accounts.sort((x, y) => x.id - y.id)
            })

            axios.get("/api/loans")
            .then(datos => {
                console.log(this.loans = datos.data.sort((x, y) => x.id - y.id))
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
                            axios.post('/api/loans', {id:`${this.nameLoan}`,amount:`${this.amountLoan}`,payments:`${this.paymentsLoan}`,accountDestination:`${this.accountDestinationLoan}`})
                            .then(response => window.location.href = "./accounts.html")
                            .catch(error => {
                                console.log(error.response.data)
                                if (error.response.data == "Nonexistent loan") {
                                    this.error1 = true;
                                }
                                if (error.response.data == "The amount exceeds the maximum available") {
                                    this.error2 = true;
                                }
                                if (error.response.data == "Number of payments not available") {
                                    this.error3 = true;
                                }
                                if (error.response.data == "Destination account does not exist") {
                                    this.error4 = true;
                                }
                                if (error.response.data == "The destination account does not belong to you") {
                                    this.error5 = true;
                                }
                                if (error.response.data == "Missing data") {
                                    this.error6 = true;
                                }
                                if (error.response.data == "You already have a loan of this type") {
                                    this.error7 = true;
                                }
                            }));
                    }
                });

        },

        payments() {
            let aux = [...this.loans]
            aux = aux.filter(loan => this.nameLoan == loan.id)

            return aux[0].payments
        }

    }


}).mount('#app')