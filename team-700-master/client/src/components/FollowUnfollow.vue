<template>
    <div>
        <b-row>
            <b-button v-if="displayButton && !isSubscribed" @click="followActivity" variant="success">Follow</b-button>
            <b-button v-if="displayButton && isSubscribed" @click="unfollowActivity" variant="danger">Unfollow</b-button>
        </b-row>
    </div>

</template>

<script>
    import axios from "axios";

    const FollowUnfollow = {
        name: "FollowUnfollow",
        props: {
            activityId: {
                type: Number,
                default: null
            },
            activityOwnerId: {
                type: Number,
                default: null
            },
            loggedInId: {
                type: Number,
                default: null
            },
        },
        data: function () {
            return {
                displayButton: true,
                isSubscribed: false,
            }
        },
        mounted() {
            this.getCanFollow();
        },
        methods: {
            getCanFollow: function() {
                if (this.loggedInId != this.activityOwnerId) {
                    let vueObj = this;
                    let url = 'http://localhost:9499/profiles/' + this.loggedInId +'/subscriptions/activities/' + this.activityId;
                    axios.defaults.withCredentials = true;
                    axios.get(url)
                        .then((res) => {
                            vueObj.isSubscribed = res.data.subscribed;
                        })
                        .catch(() => {
                            console.log('500 error')
                        });
                } else {
                    this.displayButton = false;
                }
            },
            followActivity: function() {
                let vueObj = this;
                let url = 'http://localhost:9499/profiles/' + this.loggedInId +'/subscriptions/activities/' + this.activityId;
                axios.defaults.withCredentials = true;
                axios.post(url)
                    .then(() => {
                        vueObj.getCanFollow();
                    })
                    .catch((err) => {
                        alert(err.body)
                    });
            },
            unfollowActivity: function() {
                let vueObj = this;
                let url = 'http://localhost:9499/profiles/' + this.loggedInId +'/subscriptions/activities/' + this.activityId;
                axios.defaults.withCredentials = true;
                axios.delete(url)
                    .then(() => {
                        vueObj.getCanFollow();
                    })
                    .catch((err) => {
                        alert(err.body)
                    });
            }
        }
    };
    export default FollowUnfollow

</script>
