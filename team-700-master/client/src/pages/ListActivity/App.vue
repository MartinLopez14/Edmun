<template>
    <div id="app" v-if="isLoggedIn">
        <NavBar v-bind:isLoggedIn="isLoggedIn"></NavBar>
        <b-container>
            <ActivityList :items="this.items" :fields="this.fields" :profileId="this.profileId"
                          :tableIsLoading="this.tableIsLoading">
                <b-row class="my-1">
                    <b-col sm="11">
                        <h3>{{profileName}}'s Activities</h3>
                        <b-form-text>Click on an activity to view more information on the activity</b-form-text>
                    </b-col>
                    <b-col sm="1">
                        <b-button v-if="profileId === loggedinId" align="right" @click="goToCreateActivity"> Create
                        </b-button>
                    </b-col>
                </b-row>
            </ActivityList>
        </b-container>
    </div>

</template>

<script>
    import NavBar from "@/components/NavBar.vue"
    import ActivityList from "@/components/ActivityList.vue";
    import api from "@/Api"

    const List = {
        name: 'List',
        components: {
            NavBar,
            ActivityList
        },
        data: function () {
            return {
                tableIsLoading: true,
                profileId: null,
                isLoggedIn: false,
                loggedinId: null,
                profileName: '',
                userName: '',
                items: [],
                fields: ['activityName', 'description', 'activityTypes']
            }
        },
        methods: {
            getLoggedInId: function () {
                let currentObj = this;
                api.getProfileId()
                    .then(function (response) {
                        currentObj.loggedinId = response.data;
                    })
                    .catch(function (err) {
                        alert(err);
                    });
            },
            getProfileIdAndName: function () {
                let currentObj = this;
                api.getProfile(this.$route.params.id)
                    .then(function (response) {
                        currentObj.profileId = response.data.id;
                        currentObj.profileName = response.data.firstname;
                    })
                    .catch(function (err) {
                        alert(err);
                    });
            },
            getLoggedInName: function () {
                let currentObj = this;
                api.getFirstName()
                    .then(function (response) {
                        currentObj.userName = response.data;
                        currentObj.isLoggedIn = true;
                    })
                    .catch(function () {
                    });
            },
            getActivities: function (id) {
                let vueObject = this;
                api.getActivities(id)
                    .then((res) => {
                        this.items = res.data;
                        for (let i = 0; i < res.data.length; i++) {
                            this.items[i].activityTypes = this.items[i].activityTypes.join(", ");
                        }
                        vueObject.tableIsLoading = false;
                    })
                    .catch(err => {
                        console.log(err);
                        vueObject.tableIsLoading = false;
                    })
            },
            goToCreateActivity: function () {
                // double check, if user somehow clicks create button when hidden.
                if (parseInt(this.profileId) === parseInt(this.loggedinId)) {
                    this.$router.push('/profiles/' + this.profileId + '/activities/create');
                }
            }
        },
        beforeMount() {
            this.getProfileIdAndName();
            this.getLoggedInId();
            this.getLoggedInName();
        },
        mounted() {
        },
        watch: {
            profileId: function (val) {
                if (val) {
                    this.getActivities(val);
                }
            }
        }
    }
    export default List
</script>

<style scoped>
    .clickable {
        cursor: pointer;
    }
</style>