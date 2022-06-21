const prueba = Vue.createApp({
    data() {
        return {
            clients: [],
            json: {},
            nombre: "",
            apellido: "",
            correo: "",
            contraseña: "",

            nameLoan: "",
            maxAmountLoan: 0,
            paymentsLoan: "",
            interestLoan: 0,

            

        }
    },
    
    created() {
        axios.get("http://localhost:8080/api/clients")
            .then(datos => {
                this.clients = datos.data
                this.json = datos
            })
            

    },

    methods: {

        addClient() {

            if(this.nombre != "" && this.apellido != "" && this.correo != "" && this.contraseña != "" && this.correo.includes("@" && ".com")) {

                axios.post(`/api/createClient`,`firstName=${this.nombre}&lastName=${this.apellido}&email=${this.correo}&password=${this.contraseña}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                    .catch(error => {
                        console.log(error);
                    });

            }


        },

        eliminar(cliente) {
            console.log(cliente)
            axios.delete(cliente)
                // .then(location.reload())
                .catch(error => {
                    console.log(error);
                });
        },

        editar(id) {

            Swal.fire({
                title: 'Edit Client',
                html:
                    `<input id="swal-input1" class="swal2-input" value="${this.nombre}">` +
                    `<input id="swal-input2" class="swal2-input" value="${this.apellido}">` +
                    `<input id="swal-input3" class="swal2-input" value="${this.correo}">`,
                focusConfirm: false,
                
                preConfirm: () => {
                    
                    let nombreEditado = document.getElementById('swal-input1').value;
                    let apellidoEditado = document.getElementById('swal-input2').value;
                    let correoEditado = document.getElementById('swal-input3').value;

                    console.log(nombreEditado)
                    console.log(apellidoEditado)
                    console.log(correoEditado)                    
                    
                    axios.patch(`/api/editClient/${id}`,`firstName=${nombreEditado}&lastName=${apellidoEditado}&email=${correoEditado}`)
                    .then(location.reload())
                    .catch(error => {
                        console.log(error);
                    });                    
                }
            })

        },

        crearLoan() {            
            let arrayPayments = this.paymentsLoan.split(",")
            axios.post('/api/createLoan',{ name: this.nameLoan, maxAmount: this.maxAmountLoan, payments: arrayPayments, interest: this.interestLoan})
            .then(response => console.log("todo ok"))
            .catch(error => {
                console.log(error);
            })
        },

        logOut() {
            axios.post('/api/logout')
                .then(response => window.location.href = "./web/index.html")
        }

    }


}).mount('#app')