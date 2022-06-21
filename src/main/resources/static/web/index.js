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

    created(){
        
    },

    methods: {

        signIn() {
            
            axios.post(`/api/login`,`email=${this.email}&password=${this.password}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                .then(response => this.email=="admin@admin.com" ? window.location.href = "../manager.html" : window.location.href = "./accounts.html")
                .catch(error => {
                    if(error.response.status == 401){
                        this.error1 = true;
                    }
                });
                

        },

        signUp() {

            axios.post(`/api/clients`,`firstName=${this.nombre}&lastName=${this.apellido}&email=${this.correo}&password=${this.contraseña}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                .then(response => {
                    this.email=this.correo
                    this.password=this.contraseña
                    this.signIn()
                })
                .catch(error => {
                    console.log(error.response.data)
                    if(error.response.data == "Name already in use"){
                        this.error2 = true;
                    }
                });
        },

        ocultar(){
            var tipo = document.getElementById("contraseña")
            if(tipo.type == "password"){
                tipo.type = "text"
            }else{
                tipo.type = "password"
            }
        }

    }


}).mount('#app')