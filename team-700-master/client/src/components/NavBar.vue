<template>
    <b-navbar class="nav-bar" fixed toggleable="lg" type="dark" variant="dark">
        <b-navbar-brand v-if="!isLoggedIn" to='/'>Edmun</b-navbar-brand>
        <b-navbar-brand v-if="isLoggedIn" @click="goToProfile">Edmun</b-navbar-brand>
        <b-navbar-toggle fixed target="nav-collapse" toggleable="true"></b-navbar-toggle>

        <b-collapse id="nav-collapse" is-nav>
            <b-navbar-nav>
                <b-nav-item v-if="!isLoggedIn" to='/register'>Register</b-nav-item>
                <b-nav-item v-if="isLoggedIn" to="/home">Home</b-nav-item>
                <b-nav-item v-if="isLoggedIn" @click="goToProfile">Profile</b-nav-item>
                <b-nav-item v-if="isLoggedIn" @click="goToActivities">Activities</b-nav-item>
                <b-nav-item @click="goToAdminDashBoard" v-if="isLoggedIn && loggedInIsAdmin && isAdminAccess">Admin
                    Dashboard
                </b-nav-item>
                <b-collapse id="my-collapse" v-if="isLoggedIn">
                    <b-form inline>
                        <b-input-group>
                            <template v-slot:prepend>
                                <b-form-select v-model="searchBy" :options="searchOptions"></b-form-select>
                            </template>

                            <b-form-input placeholder="Search" v-model="searchQuery"></b-form-input>

                            <b-input-group-append>
                                <b-button @click="search()">
                                    <b-icon-search></b-icon-search>
                                </b-button>
                            </b-input-group-append>
                        </b-input-group>
                    </b-form>
                </b-collapse>
                <b-nav-item v-if="isLoggedIn">
                    <span v-b-toggle.my-collapse>
                        <b-icon-x class="when-opened">Close</b-icon-x> <b-icon-search
                            class="when-closed">Open</b-icon-search>
                    </span>
                </b-nav-item>
            </b-navbar-nav>

            <b-navbar-nav class="ml-auto">
                <b-button class="loginbtn" to="/login" size="sm" type="button" v-if="!isLoggedIn" variant="primary">
                    Log in
                </b-button>
                <div v-else>
                    <b-button-group class="access-control-button" title="Toggle to switch role access"
                                    v-b-popover.hover.bottom="" v-if="loggedInIsUserAdmin">
                        <b-button @click="switchAccessControl(false)" pill v-if="isAdminAccess" variant="success">Admin
                        </b-button>
                        <b-button @click="switchAccessControl(true)" pill v-else variant="outline-success">Standard User
                        </b-button>
                    </b-button-group>
                    <b-nav-item-dropdown right>
                        <template v-slot:button-content>
                            <em>{{ userName }}</em>
                        </template>
                        <b-dropdown-item @click="goToEdit" v-if="!hideElements">Edit Profile</b-dropdown-item>
                        <b-dropdown-item @click="logout">Log Out</b-dropdown-item>
                    </b-nav-item-dropdown>
                </div>
            </b-navbar-nav>


        </b-collapse>
    </b-navbar>
</template>

<script>
    import 'bootstrap/dist/css/bootstrap.css'
    import 'bootstrap-vue/dist/bootstrap-vue.css'
    import {mutations, store} from "../store";
    import api from '@/Api';
    import AdminMixin from "../mixins/AdminMixin";

    const NavBar = {
        name: 'NavBar',
        components: {},
        props: ['isLoggedIn', 'hideElements', 'loggedInId'],
        data: function () {
            return {
                userName: "",
                name: "",
                searchBy: 1,
                searchQuery: "",
                searchOptions: [
                    {value: 1, text: 'Users'}
                ],
                profileId: "",
                loggedInIsAdmin: null,
                loggedInIsUserAdmin: null
            }
        },
        computed: {
            isAdminAccess() {
                return store.adminAccess
            }
        },
        methods: {
            logout() {
                const vueObj = this;
                api.logout()
                    .then(function (response) {
                        console.log(response.data);
                        vueObj.$router.push('/');
                    })
                    .catch(function (error) {
                        console.log(error.response.data);
                    });
            },
            getUserId: async function () {
                this.loggedInIsAdmin = await AdminMixin.methods.checkUserIsAdmin();
                this.loggedInIsUserAdmin = await AdminMixin.methods.checkUserIsUserAdmin();
                let currentObj = this;
                api.getProfileId()
                    .then(function (response) {
                        currentObj.profileId = response.data;
                    })
                    .catch(function () {
                    });
            },
            goToEdit() {
                this.$router.push('/profiles/edit/' + this.profileId);
                this.$router.go('/profiles/edit/' + this.profileId);
            },
            goToProfile() {
                this.$router.push('/profiles/' + this.profileId);
                // this.$router.go(0);
            },
            goToActivities() {
                this.$router.push('/profiles/' + this.profileId + '/activities');
            },
            goToAdminDashBoard() {
                this.$router.push('/admin/dashboard');
            },
            getUserName() {
                let currentObj = this;
                api.getFirstName()
                    .then(function (response) {
                        currentObj.userName = response.data;
                    })
                    .catch(function () {
                    });
            },
            search() {
                if (this.searchQuery === '') return;
                this.$router.push('/profiles?fullname=' + this.searchQuery);
            },
            switchAccessControl(value) {
                mutations.switchAccessControl(value);
            }
        },
        beforeMount: function () {
            this.getUserId();
            this.getUserName();
        }
    };
    export default NavBar
</script>

<style scoped>
    [v-cloak] {
        display: none;
    }

    .nav-bar {
        margin-bottom: 50px;
        margin-right: 0px;
    }

    .collapsed > .when-opened,
    :not(.collapsed) > .when-closed {
        display: none;
    }

    .access-control-button {
        position: absolute;
        right: 150px;
    }

</style>
