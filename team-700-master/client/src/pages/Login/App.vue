<template>
    <div id="app">
        <div>
            <NavBar></NavBar>
        </div>
        <div class="container">
            <h1> Login </h1>
            <hr>
            <b-form @submit="onSubmit" novalidate class="needs-validation">
                <b-form-group id="input-group-1" label="Email address" label-for="input-1">
                    <b-form-input
                            :state="emailState"
                            aria-describedby="input-live-feedback"
                            class="input-s"
                            id="input-1"
                            placeholder="Enter email"
                            required
                            trim
                            type="email"
                            v-model="email"
                            value="jacky@google.com"
                    ></b-form-input>

                    <!-- This will only be shown if the preceding input has an invalid state -->
                    <b-form-invalid-feedback id="input-live-feedback">
                        Enter a valid email
                    </b-form-invalid-feedback>

                </b-form-group>
                <b-form-group id="input-group-2" label="Password" label-for="input-2">
                    <b-form-input
                            :state="passwordState"
                            aria-describedby="input-live-feedback"
                            id="input-2"
                            placeholder="Enter password"
                            required
                            type="password"
                            v-model="password"
                            value="JackysSecuredPwd1"
                    ></b-form-input>

                    <b-form-invalid-feedback id="input-live-feedback">
                        Enter your password
                    </b-form-invalid-feedback>

                    <p id="user-error-feedback"></p>

                </b-form-group>
                <b-button type="submit" variant="primary">Submit</b-button>
            </b-form>
        </div>
        <b-container class="b-container">
            <a href="/#/register" id="signup-link">Sign up for Edmun</a>
        </b-container>

    </div>

</template>

<script>
    import NavBar from '@/components/NavBar.vue';
    import api from '@/Api';

    export default {
        components: {
            NavBar
        },
        computed: {
            emailState() {
                let state = null;
                if (this.submitted) {
                    if (this.email.length > 0) {
                        let pattern = new RegExp("^($|[a-zA-Z0-9_\\.\\+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-\\.]+)$");
                        state = pattern.test(this.email);
                    } else {
                        state = false;
                    }
                }
                if (!this.submitted) {
                    state = null;
                }
                return state;
            },
            passwordState() {
                let state = null;
                if (this.submitted) {
                    if (this.password.length == 0) {
                        state = false;
                    }
                }
                if (!this.submitted) {
                    state = null;
                }
                return state;
            }
        },
        data() {
            return {
                email: "",
                password: "",
                submitted: false,
            }
        },
        methods: {
            onSubmit(e) {
                this.submitted = true;
                if (this.emailState != false && this.passwordState != false) {
                    let currentObj = this;
                    api.login(this.email, this.password)
                        .then(function () {
                            let adminPattern = new RegExp("Admin");
                            if (adminPattern.test(currentObj.output)) {
                                currentObj.$router.push('/admin');
                            } else {
                                currentObj.$router.push('/home')
                            }
                        })
                        .catch(function (error) {
                            console.log(error)
                            currentObj.output = error;
                            document.getElementById("user-error-feedback").textContent = error.response.data + '\n';
                        });
                }
                e.preventDefault();
            },
        }
    }

</script>

<style scoped>
    [v-cloak] {
        display: none;
    }

    .container {
        background-color: #f2f2f2;
        padding: 5px 40px 15px 20px;
        border: 1px solid lightgrey;
        border-radius: 3px;
    }

    #user-error-feedback {
        color: red;
        padding-top: 10px;
    }

    .b-container {
        background-color: white;
        border: 0 white;
    }

</style>
