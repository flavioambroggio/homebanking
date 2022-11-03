document.documentElement.style.setProperty('--animate-duration', '2s');

Vue.createApp({
    data() {
        return {
            email: "",
            password: "",
            nombre: "",
            apellido: "",
            correo: "",
            contraseña: "",
            error1: false,
            error2: false
        }
    },

    created() {

    },

    mounted() {

        $('.message a').click(function () {
            $('form').animate({ height: "toggle", opacity: "toggle" }, "slow");
        });

    },

    methods: {

        signIn() {

            axios.post(`/api/login`, `email=${this.email}&password=${this.password}`)
                .then(response => this.email == "admin@admin.com" ? window.location.href = "../manager.html" : window.location.href = "./accounts.html")
                .catch(error => {
                    Swal.fire({
                        position: 'top-end',
                        icon: 'error',
                        title: "Contraseña o Email Incorrectos",
                        toast: true,
                        showConfirmButton: false,
                        timer: 1500
                    })
                });


        },

        signUp() {

            axios.post(`/api/clients`, `firstName=${this.nombre}&lastName=${this.apellido}&email=${this.correo}&password=${this.contraseña}`)
                .then(response => {
                    this.email = this.correo
                    this.password = this.contraseña
                    this.signIn()
                })
                .catch(error => {
                    Swal.fire({
                        position: 'top-end',
                        icon: 'error',
                        title: error.response.data,
                        toast: true,
                        showConfirmButton: false,
                        timer: 1500
                    })
                });
        },

        ocultar() {
            var tipo = document.getElementById("contraseña")
            if (tipo.type == "password") {
                tipo.type = "text"
            } else {
                tipo.type = "password"
            }
        }

    }


}).mount('#app')