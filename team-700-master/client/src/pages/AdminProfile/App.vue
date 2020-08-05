<template>
    <div id="app">
        <NavBar v-bind:isLoggedIn="isLoggedIn" v-bind:userName="userName"></NavBar>

        <div class="container">
            <div>
                <b-row>
                    <b-img center v-bind="mainProps" rounded="circle"
                           src="https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftse1.mm.bing.net%2Fth%3Fid%3DOIP.f4t8FtSbfNUs94KKgMhhBAHaHr%26pid%3DApi%26h%3D160&f=1"
                           alt="Center image"></b-img>
                </b-row>
                <b-row><h3></h3></b-row>
                <b-row align-h="center">
                    <h3>{{userData.firstname}} {{userData.lastname}} (Admin User)</h3>
                </b-row>
                <b-tabs content-class="mt-3" align="center">
                    <b-tab title="About" active>
                        <b-card style="margin: 1em" title="About:">
                            <b-row>
                                <b-col><strong>Email:</strong></b-col>
                                <b-col v-if="primaryEmail != null"><p>{{primaryEmail}}</p></b-col>
                                <b-col v-else><p>No nickname</p></b-col>
                            </b-row>
                            <b-row>
                                <b-col><strong>Roles:</strong></b-col>
                                <b-col>
                                    <div v-bind:key="role" v-for="role in userRoles">
                                        {{ role.roleName }}
                                    </div>
                                </b-col>
                            </b-row>
                        </b-card>
                    </b-tab>

                </b-tabs>
            </div>

        </div>
    </div>
</template>


<script>
    import api from '@/Api';
    import NavBar from "@/components/NavBar.vue"

    const App = {
        name: 'App',
        components: {
            NavBar
        },
        data: function () {
            return {
                userData: '',
                isLoggedIn: false,
                userName: "",
                primaryEmail: "",
                userRoles: []
            }
        },
        methods: {
            getUserSession: function () {
                let currentObj = this;
                api.getLoggedInProfile()
                    .then(function (response) {
                        currentObj.userData = response.data;
                        currentObj.isLoggedIn = true;
                        currentObj.userName = response.data.firstname;
                        currentObj.primaryEmail = response.data.primary_email.address;
                        currentObj.userRoles = response.data.roles;
                        let isAdmin = false;
                        for (let i = 0; i < currentObj.userRoles.length; i++) {
                            if (currentObj.userRoles[i].roleName === "ROLE_ADMIN") {
                                isAdmin = true;
                            }
                        }
                        if (isAdmin === false) {
                            const profileId = response.data.id.toString();
                            currentObj.$router.push('/profiles/' + profileId)
                        }
                    })
                    .catch(function () {
                        currentObj.isLoggedIn = false;
                        currentObj.$router.push('/');
                    });
            }
        },
        mounted: function () {
            this.getUserSession();
        }
    };

    export default App;
</script>
