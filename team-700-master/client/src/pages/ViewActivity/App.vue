<template>
    <div id="app" v-if="isLoggedIn">
        <NavBar v-bind:isLoggedIn="isLoggedIn" v-bind:userName="userName"></NavBar>
        <div v-if="archived">
            <h1 align="center">This activity has been deleted</h1>
        </div>
        <div v-else-if="notFound">
            <h1 align="center">This activity does not exist</h1>
        </div>
        <div v-else-if="!locationDataLoading" class="container">
            <div>
                <!-- Image and Name -->
                <b-row>
                    <b-img center rounded="circle" width="150px" height="150px"
                           src="https://library.kissclipart.com/20180919/uke/kissclipart-running-clipart-running-logo-walking-8d4133548d1b34c4.jpg"
                           alt="Center image"></b-img>
                </b-row>
                <b-row align-h="center">
                    <h3>{{ activityName }}</h3>
                </b-row>

                <!-- Summary -->
                <FollowerSummary class="text-center" :activityId="$route.params.activityId"></FollowerSummary>
                <b-row align-h="center">
                    <ShareActivity :modal="profileId == activityOwner.id" :visibility="visibility" :profileId="profileId" :activityId="$route.params.activityId"></ShareActivity>
                </b-row>

                <!-- Actions -->
                <b-row align-h="center">
                    <FollowUnfollow v-bind:activityId="this.$route.params.activityId"
                                    v-bind:activityOwnerId="this.$route.params.id"
                                    v-bind:loggedInId="loggedInId"></FollowUnfollow>
                    <b-dropdown v-if="profileId == loggedInId" text="Actions" class="m-md-2">
                        <b-dropdown-item @click="editActivity()">Edit</b-dropdown-item>
                        <b-dropdown-item @click="deleteActivity()">Delete</b-dropdown-item>
                    </b-dropdown>
                </b-row>

                <!-- Content -->
                <b-row align-h="center">
                    <b-col cols="9">
                <b-card style="margin: 1em" title="About:">
                    <div v-if="locationDataLoading">
                        <div class="text-center text-primary my-2">
                            <b-spinner class="align-middle"></b-spinner>
                            <strong> Loading...</strong>
                        </div>
                    </div>
                    <div v-else>
                        <b-row>
                            <b-col cols="3"><b>Activity Type(s):</b></b-col>
                            <b-col><p>{{activityTypes}}</p></b-col>
                        </b-row>
                        <b-row v-if="!continuous">
                            <b-col cols="3"><b>Start:</b></b-col>
                            <b-col><p>{{startTime}}</p></b-col>
                        </b-row>
                        <b-row v-if="!continuous">
                            <b-col cols="3"><b>End:</b></b-col>
                            <b-col><p>{{endTime}}</p></b-col>
                        </b-row>
                        <b-row v-if="location==null">
                            <b-col cols="3"><b>Location:</b></b-col>
                            <b-col><p>No location available</p></b-col>
                        </b-row>
                        <b-row v-if="location!=null">
                            <b-col cols="3"><b>Location:</b></b-col>
                            <b-col><p>{{locationString}}</p></b-col>
                        </b-row>
                        <b-row>
                            <b-col cols="3"><b>Description:</b></b-col>
                            <b-col><p>{{description}}</p></b-col>
                        </b-row>
                        <b-row>
                            <b-col cols="3"><b>Hashtags:</b></b-col>
                            <b-col>
                                <p>
                      <span v-for="hashtag in hashtags" v-bind:key="hashtag">
                        <b-link @click="clickHashtag(hashtag)"
                        >{{ hashtag }}&nbsp;</b-link
                        >
                      </span>
                                </p>
                            </b-col>
                        </b-row>
                    </div>
                </b-card>
                    </b-col>
                </b-row>

                <!-- Participants -->
                <b-row align-h="center">
                    <b-col cols="9">
                        <b-card style="margin: 1em" title="Participants:">
                            <FollowerUserList :activity-id="$route.params.activityId" :logged-in-id="loggedInId" :activity-creator-id="activityOwner.id"></FollowerUserList>
                        </b-card>
                    </b-col>
                </b-row>
            </div>
        </div>
    </div>
</template>

<script>
    import NavBar from "@/components/NavBar.vue";
    import FollowUnfollow from "@/components/FollowUnfollow.vue";
    import FollowerSummary from "../../components/Activity/FollowerSummary.vue";
    import FollowerUserList from "../../components/Activity/FollowerUserList";
    import ShareActivity from "@/components/ShareActivity.vue";
    import api from '@/Api'

    const App = {
        name: "App",
        components: {
            NavBar,
            FollowUnfollow,
            FollowerUserList,
            FollowerSummary,
            ShareActivity,
        },
        data: function () {
            return {
                //isActivityOwner: false,
                userData: "",
                isLoggedIn: false,
                userName: "",
                loggedInId: null,
                profileId: null,
                activityName: "",
                description: "",
                activityTypes: [],
                continuous: false,
                startTime: "",
                endTime: "",
                location: null,
                hashtags: [],
                activityOwner: null,
                locationString: "",
                locationDataLoading: true,
                archived: false,
                notFound: false,
                visibility: null
            }
        },
        mounted() {
            this.getUserName();
            this.getActivityData();
            this.getLoggedInId();
            this.setProfileId();
        },
        methods: {
            getUserName: function () {
                let vueObj = this;

                api.getFirstName()
                    .then((res) => {
                        vueObj.userName = res.data;
                    })
                    .catch(() => {
                        vueObj.isLoggedIn = false;
                        vueObj.$router.push('/login');
                    });
            },
            getLoggedInId: function () {
                let vueObj = this;

                api.getProfileId()
                    .then((res) => {
                        vueObj.loggedInId = res.data;
                        vueObj.isLoggedIn = true;
                    }).catch(() => {
                    vueObj.isLoggedIn = false;
                    vueObj.$router.push('/login');
                })
            },
            setProfileId: function () {
                let vueObj = this;
                vueObj.profileId = this.$route.params.id;
            },
            deleteActivity() {
                // double check for if user clicks button when hidden.
                if (parseInt(this.profileId) === parseInt(this.loggedInId)) {
                    if (!confirm("Are you sure you want to delete this activity?")) {
                        return;
                    }

                    let profileId = this.$route.params.id;
                    let activityId = this.$route.params.activityId;
                    api.deleteActivity(profileId, activityId)
                        .then(() => {
                            this.$router.push("/profiles/" + profileId + "/activities/");
                        })
                        .catch(err => alert(err));
                }
            },
            editActivity() {
                let profileId = this.$route.params.id;
                let activityId = this.$route.params.activityId;
                // double check for it somehow user clicks edit button when hidden.
                console.log("profile Id: " + this.profileId);
                console.log("loggedin Id: " + this.loggedInId);

                if (parseInt(this.profileId) === parseInt(this.loggedInId)) {
                    this.$router.push(
                        "/profiles/" + profileId + "/activities/" + activityId + "/edit"
                    );
                }
            },
            getActivityData() {
                let vueObj = this;
                let activityId = this.$route.params.activityId;


                api.getActivity(activityId)
                    .then((res) => {
                        if (res.data == "Activity is archived") {
                            this.archived = true;
                        } else {
                            vueObj.activityOwner = res.data.profile;
                            vueObj.activityName = res.data.activityName;
                            vueObj.description = res.data.description;
                            vueObj.activityTypes = res.data.activityTypes;
                            vueObj.continuous = res.data.continuous;
                            vueObj.startTime = res.data.startTime;
                            vueObj.endTime = res.data.endTime;
                            vueObj.location = res.data.location;
                            vueObj.visibility = res.data.visibilityType;
                            if(res.data.visibilityType == null) {
                                vueObj.visibility = "Public"
                            }
                            if (vueObj.location != null) {
                                vueObj.locationString = vueObj.location.city + ", ";
                                if (vueObj.location.state) {
                                    vueObj.locationString += vueObj.location.state + ", ";
                                }
                                vueObj.locationString += vueObj.location.country;
                            }
                            if (res.data.tags.length > 0) {
                                for (var i = 0; i < res.data.tags.length; i++) {
                                    vueObj.hashtags.push("#" + res.data.tags[i].name);
                                }
                            }
                            if (vueObj.activityOwner.id != this.profileId && vueObj.visibility == 'Private') {
                                vueObj.$router.push('/profiles/' + this.profileId);
                            }
                            vueObj.hashtags.sort();
                            if (!vueObj.continuous) {
                                this.getCorrectDateFormat(vueObj.startTime, vueObj.endTime, vueObj);
                            }
                            this.getActivityTypeDisplay(vueObj);
                        }
                        vueObj.locationDataLoading = false;
                    }).catch((err) => {
                    if (err.response && err.response.status == 404) {
                        this.notFound = true;
                    } else {
                        let profileId = this.$route.params.id;
                        vueObj.$router.push("/profiles/" + profileId);
                    }
                });
            },
            getCorrectDateFormat: function (start, end, currentObj) {
                const startDate = new Date(start);
                const endDate = new Date(end);
                currentObj.startTime = startDate.toString();
                currentObj.endTime = endDate.toString();
            },
            getActivityTypeDisplay: function (currentObj) {
                let result = "";
                for (let i = 0; i < currentObj.activityTypes.length; i++) {
                    result += currentObj.activityTypes[i];
                    if (i + 1 < currentObj.activityTypes.length) {
                        result += ", ";
                    }
                }
                currentObj.activityTypes = result;
            },
            clickHashtag(hashtag) {
                hashtag = hashtag.substring(1);
                this.$router.push("/hashtag/" + hashtag);
            }
        }
    };
    export default App;
</script>

