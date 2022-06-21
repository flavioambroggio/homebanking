window.onload = function () {
    $('#onload').fadeOut();
    $('body').removeClass('hidden');
}

Vue.createApp({
    data() {
        return {
            clients: [],
            cards: [],
            debit: [],
            credit: [],
        }
    },

    created() {

        this.cargardatosiniciales()

        // axios.get("http://localhost:8080/api/clients/current")
        //     .then(datos => {
        //         this.clients = datos.data
        //         this.cards = datos.data.cards.sort((x, y) => x.id - y.id)

        //         this.debit = this.cards.filter(card => card.type == "DEBIT")
        //         this.credit = this.cards.filter(card => card.type == "CREDIT")

        //         this.vencimiento()
        //     })


    },

    computed: {

    },

    methods: {

        ordenarFecha(fecha) {
            fecha = fecha.split("T")[0]
            fecha = fecha.split("-")
            fecha[0] = fecha[0].substring(2, 4)

            return fecha[1] + "/" + fecha[0]
        },

        eliminarCard(idCard) {
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
                    axios.patch(`/api/clients/current/cards/${idCard}`)
                        .then(() => {
                            Swal.fire(
                                'Card eliminada!',
                                'card eliminda con éxito',
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

        vencimiento() {
            let date = new Date()
            actual = date.toISOString().split("T")[0]
            console.log(actual)
            console.log(this.cards)
            this.cards.forEach(card => {
                console.log(card.thruDate.split("T")[0].valueOf())
                if (card.thruDate.split("T")[0].valueOf() < actual.valueOf()) {
                    console.log(card.thruDate.split("T")[0] + " esta vencida")
                    axios.patch(`/api/clients/current/cards/expired/${card.id}`)
                    .then(() => this.cargardatos())
                } else {
                    console.log(card.thruDate.split("T")[0] + " no esta vencida")
                }
            });
        },



        cargardatosiniciales() {
            axios.get("http://localhost:8080/api/clients/current")
                .then(datos => {
                    this.clients = datos.data
                    this.cards = datos.data.cards.sort((x, y) => x.id - y.id)

                    this.debit = this.cards.filter(card => card.type == "DEBIT")
                    this.credit = this.cards.filter(card => card.type == "CREDIT")
                }).then(() => {
                    this.vencimiento()
                })

                .catch(function (error) {

                    console.log(error);
                })
        },

        cargardatos() {
            axios.get("http://localhost:8080/api/clients/current")
                .then(datos => {
                    this.clients = datos.data
                    this.cards = datos.data.cards.sort((x, y) => x.id - y.id)

                    this.debit = this.cards.filter(card => card.type == "DEBIT")
                    this.credit = this.cards.filter(card => card.type == "CREDIT")
                })

                .catch(function (error) {

                    console.log(error);
                })
        },




        logOut() {
            axios.post('/api/logout')
                .then(response => window.location.href = "./index.html")
        }

    }


}).mount('#app')