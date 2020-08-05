import api from '../Api'


export default {
    data: function () {
        return {
            loggedInIsUserAdmin: false
        }
    },
    methods: {
        getUserRoles: function () {
            api.getProfileRoles()
                .then(function (response) {
                    return response.data;
                })
        },
        checkUserIsAdmin: function () {
            let vue = this;
            return api.getProfileRoles()
                .then((response) => {
                    if (response) {
                        let roles = response.data
                        for (let i = 0; i < roles.length; i++) {
                            if (roles[i].roleName === "ROLE_ADMIN" || roles[i].roleName === "ROLE_USER_ADMIN") {
                                if (roles[i].roleName === "ROLE_USER_ADMIN") {
                                    vue.loggedInIsUserAdmin = true;
                                } else {
                                    vue.loggedInIsUserAdmin = false;
                                }
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .catch((err) => {
                    console.log(err);
                    return null;
                })

        },
        checkUserIsUserAdmin: function () {
            // eslint-disable-next-line no-unused-vars
            return this.checkUserIsAdmin().then(r => {
                return this.loggedInIsUserAdmin;
            });
        }
    }
};