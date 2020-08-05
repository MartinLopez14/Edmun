<template>
    <div>
        <b-button @click="getOtherUserRole()" v-b-toggle.sidebar-1
                  v-if="loggedInIsAdmin && loggedInId != $route.params.id">Admin Controls
        </b-button>
        <b-sidebar id="sidebar-1" :title="'Admin Controls: ' + userData.firstname + ' ' + userData.lastname" shadow>
            <div class="px-3 py-2">
                Currently the user is a: <p v-if="profileIsAdmin">Admin</p>
                <p v-else>Standard User</p>
                Possible actions:
                <br>
                <b-button @click="toggleAdminRights" class="ui-button" v-if="profileIsAdmin" variant="danger">Remove
                    Admin Privilege
                </b-button>
                <b-button @click="toggleAdminRights" class="ui-button" v-else variant="success">Add Admin Privilege
                </b-button>
                <p style="color:red;" v-if="roleEditedErrorMsg != null">{{roleEditedErrorMsg}}</p>

                <b-button class="ui-button" v-b-modal.modal-pwd variant="primary">Change User Password</b-button>
                <b-modal
                        @hidden="resetModal"
                        @ok="handleOk"
                        @show="resetModal"
                        id="modal-pwd"
                        title="Change User Password">
                    <b-form @submit.stop.prevent="changeUserPassword">
                        <b-form-group
                                label="Enter new user password:">
                            <b-form-input
                                    :state="validateForm('newUserPassword')"
                                    type="password"
                                    v-model="$v.form.newUserPassword.$model">
                            </b-form-input>
                            <b-form-invalid-feedback> Password should contain at least 8 characters with at least one
                                digit, one lower case and one upper case
                            </b-form-invalid-feedback>
                        </b-form-group>
                        <b-form-group
                                label="Repeat new user password:">
                            <b-form-input
                                    :state="validateForm('repeatNewUserPassword')"
                                    type="password"
                                    v-model="$v.form.repeatNewUserPassword.$model">
                            </b-form-input>
                            <b-form-valid-feedback> {{ form.passwordEditSuccessMsg }}</b-form-valid-feedback>
                            <b-form-invalid-feedback> {{ form.passwordEditErrorMsg ? form.passwordEditErrorMsg:
                                "Repeated password must match new password"}}
                            </b-form-invalid-feedback>
                        </b-form-group>
                    </b-form>
                </b-modal>

                <br>
                Go to:
                <br>
                <b-button @click="goToUserEditProfilePage" class="ui-button">Edit User Profile</b-button>
                <b-button @click="goToUserActivityListPage" class="ui-button">User Activities</b-button>
            </div>
        </b-sidebar>
    </div>

</template>

<script>
    import {helpers, sameAs} from 'vuelidate/lib/validators'
    import api from '@/Api'

    const passwordValidate = helpers.regex('passwordValidate', new RegExp("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,}$"));

    const AdminSideBar = {
        name: "AdminSideBar",
        props: {
            loggedInIsAdmin: {
                type: Boolean,
                default: null,
            },
            userData: {
                type: Object,
                default: null,
            },
            loggedInId: {
                type: Number,
                default: null
            },
        },
        data: function () {
            return {
                profileIsAdmin: null,
                roleEditedErrorMsg: null,
                form: {
                    newUserPassword: null,
                    repeatNewUserPassword: null,
                    passwordEditSuccessMsg: null,
                    passwordEditErrorMsg: null
                }
            }
        },
        validations: {
            form: {
                newUserPassword: {
                    passwordValidate
                },
                repeatNewUserPassword: {
                    sameAsPassword: sameAs('newUserPassword')
                }
            }
        },
        methods: {
            validateForm: function (name) {
                const {$dirty, $error} = this.$v['form'][name];
                return $dirty ? !$error : null;
            },
            getOtherUserRole: function () {
                const currentObj = this;
                return api.getAdminRole(this.$route.params.id)
                    .then(function (response) {
                        currentObj.profileIsAdmin = false
                        response.data.forEach(function (item) {
                            if (item.roleName === "ROLE_ADMIN" || item.roleName === "ROLE_USER_ADMIN") {
                                currentObj.profileIsAdmin = true;
                            }
                        })
                    })
                    .catch(function (error) {
                        console.log(error)
                    });
            },
            toggleAdminRights: function () {
                let vue = this;
                let data = {
                    role: "ROLE_USER_ADMIN"
                }
                api.updateAdminRights(this.$route.params.id, data)
                    .then(function () {
                        vue.roleEditedErrorMsg = null;
                        vue.getOtherUserRole();
                    })
                    .catch(function (err) {
                        vue.roleEditedErrorMsg = err.data;
                    })
            },
            changeUserPassword: function () {
                let vue = this;
                this.$v.form.$touch();
                if (this.$v.form.$anyError || this.form.newUserPassword == null || this.form.repeatNewUserPassword == null) {
                    return;
                }
                let data = {
                    new_password: vue.form.newUserPassword,
                    repeat_password: vue.form.repeatNewUserPassword
                }
                api.updatePasswordWithAdmin(this.$route.params.id, data).then(function (response) {
                    vue.form.passwordEditSuccessMsg = response.data;
                    vue.form.newUserPassword = null;
                    vue.form.repeatNewUserPassword = null;
                }).catch(function (err) {
                    vue.form.passwordEditErrorMsg = err.response;
                })
            },
            handleOk: function (bvModalEvent) {
                bvModalEvent.preventDefault();
                this.changeUserPassword();
            },
            resetModal: function () {
                this.form.newUserPassword = null;
                this.form.repeatNewUserPassword = null;
                this.form.passwordEditSuccessMsg = null;
                this.form.passwordEditErrorMsg = null;
                this.$v.form.$reset();
            },
            goToUserEditProfilePage: function () {
                this.$router.push('/profiles/edit/' + this.$route.params.id);
            },
            goToUserActivityListPage: function () {
                this.$router.push('/profiles/' + this.$route.params.id + '/activities/');
            }
        }
    }
    export default AdminSideBar

</script>

<style scoped>
    .ui-button {
        margin: 5px;
    }

</style>