window.onload = function(){
    $('#onload').fadeOut();
    $('body').removeClass('hidden');
}

Vue.createApp({
    data() {
        return {
            clients: [],
            type: "",
            color: "",
            error1: false,
            error2: false

        }
    },

    created() {
        axios.get("/api/clients/current")
            .then(datos => {
                this.clients = datos.data
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
                            axios.post(`/api/clients/current/cards`, `type=${this.type}&color=${this.color}`)
                            .then(response => window.location.href = "./cards.html")
                            .catch(error => {
                                console.log(error.response.data)
                                if (error.response.data == "No puedes tener mas de 3 tarjetas del mismo tipo") {
                                    this.error1 = true;
                                }
                                if (error.response.status == 400) {
                                    this.error2 = true;
                                }
                            }));
                    }
                });

        }

    }


}).mount('#app')