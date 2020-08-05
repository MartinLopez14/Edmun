<template>
    <div id="app" v-if="isLoggedIn">
        <NavBar v-bind:isLoggedIn="isLoggedIn" v-bind:userName="userName"></NavBar>
        <b-container>
            <b-row>
                <b-col cols="3">
                    <b-row align-h="center">
                        <b-avatar size="6rem" style="text-align: center"></b-avatar>
                    </b-row>
                    <b-row style="margin-top: 10px;" align-h="center">
                        <p style="padding-bottom:0;margin-bottom: 0;font-size:18px">{{ fullName }}</p>
                    </b-row>
                    <b-row align-h="center">
                        <p style="font-size:14px">Following: {{ followingCount }}</p>
                    </b-row>
                </b-col>
                <b-col cols="9">
                    <b-row align-h="center">
                        <h3>{{ userName }}'s Feed</h3>
                    </b-row>
                    <b-row style="margin-top:10px;">
                        <div v-for="(item, index) in items" :key="index" style="width:100%;margin-top:10px;">
                            <b-card class="feed" :sub-title="convertDateToReadableString(item.date_time).toString()"
                                    v-on:click="goToActivity(item.activity_id)">
                                <b-card-text>
                                    {{ item.message }}
                                </b-card-text>
                            </b-card>
                        </div>
                    </b-row>
                </b-col>
            </b-row>
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
        data() {
            return {
                isLoggedIn: false,
                userName: '',
                fullName: '',
                userId: null,
                followingCount: 46,
                items: [],
                limit: 20,
                offset: 0,
                creatorId: null
            }
        },
        methods: {
            getUser: async function () {
                await api.getLoggedInProfile()
                    .then((res) => {
                        this.userName = res.data.firstname;
                        this.fullName = res.data.firstname + ' ' + res.data.lastname;
                        this.isLoggedIn = true;
                        this.userId = res.data.id;
                    })
                    .catch(err => console.log(err));
            },
            getHomeFeed: async function () {
                await api.getHomeFeed(this.userId, this.offset, this.limit)
                    .then((res) => {
                        if (res.data.feeds.length > 0) {
                            this.items.push(...res.data.feeds);
                        }
                    })
                    .catch(err => {
                        console.log(err)
                    });
            },
            getActivityOwner: async function (activityId) {
                api.getActivityCreatorId(activityId)
                    .then((res) => {
                        this.creatorId = res.data;
                    })
                    .catch(err => {
                        console.log(err)
                    });

            },
            goToActivity: async function (activityId) {
                await this.getActivityOwner(activityId);
                this.$router.push('/profiles/' + this.creatorId + '/activities/' + activityId);
            },
            convertDateToReadableString(dateTimeString) {
                let dateTime = new Date(dateTimeString);
                let months = ["January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"];
                let date = dateTime.getDate();
                let month = dateTime.getMonth();
                let year = dateTime.getFullYear();
                let hour = dateTime.getHours();
                let hourStr;
                if (hour < 10) {
                    hourStr = "0" + hour;
                } else {
                    hourStr = hour;
                }
                let minutes = dateTime.getMinutes();
                let minuteStr;
                if (minutes < 10) {
                    minuteStr = "0" + minutes;
                } else {
                    minuteStr = minutes;
                }
                let readableString = date + " " + months[month] + " " + year + " " + hourStr + ":" + minuteStr;
                return readableString;
            },
            scroll() {
                window.onscroll = async () => {
                    let bottomOfWindow = document.documentElement.scrollTop + window.innerHeight === document.documentElement.offsetHeight;

                    if (bottomOfWindow) {
                        this.offset += this.limit;
                        await this.getHomeFeed();
                    }
                };
            }
        },
        async mounted() {
            this.scroll();
            await this.getUser();
            await this.getHomeFeed();
        }
    }
</script>

<style scoped>
    .feed {
        cursor: pointer;
    }

    .feed:hover {
        cursor: pointer;
        background-color: whitesmoke;
    }

</style>