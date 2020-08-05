<template>
    <div>
        <!-- Grouped Tabs -->
        <!-- Commented out sections are for future roles that are in the api calls but i didn't realise we weren't using yet -->
        <b-card no-body>
            <b-tabs card>
                <b-tab key="Participants" title="Participants" @click="currentGroup='Participants'">
                <b-card style="margin-top:10px;" :key="user.profile_id" v-for="user in participants">
                    <b-row class="text-center" align-v="center">
                        <b-col class="text-center">
                            {{ user.full_name }}
                        </b-col>
                        <b-col v-if="activityCreatorId==loggedInId" class="text-center">
                            <b-dropdown class="m-md-2" id="dropdown-1" text="Participant">
                                <b-dropdown-item @click="changeRole(user, 'organiser')">Organiser</b-dropdown-item>
                                <b-dropdown-item @click="removeRole(user)">Remove</b-dropdown-item>

                            </b-dropdown>
                        </b-col>
                    </b-row>
                </b-card>
            </b-tab>
                <b-tab key="Organisers" title="Organisers" @click="currentGroup='Organisers'">
                    <b-card style="margin-top:10px;" :key="user.profile_id" v-for="user in organisers">
                        <b-row class="text-center" align-v="center">
                            <b-col class="text-center">
                                {{ user.full_name }}
                            </b-col>
                            <b-col v-if="activityCreatorId==loggedInId" class="text-center">
                                <b-dropdown class="m-md-2" id="dropdown-1" text="Organiser">
                                    <b-dropdown-item @click="changeRole(user, 'participant')">Participant</b-dropdown-item>
                                    <b-dropdown-item @click="removeRole(user)">Remove</b-dropdown-item>
                                </b-dropdown>
                            </b-col>
                        </b-row>
                    </b-card>
                </b-tab>
                <b-tab key="Accessors" title="Accessors" @click="currentGroup='Accessors'">
                    <b-card style="margin-top:10px;" :key="user.profile_id" v-for="user in accessors">
                        <b-row class="text-center" align-v="center">
                            <b-col class="text-center">
                                {{ user.full_name }}
                            </b-col>
                            <b-col v-if="activityCreatorId==loggedInId" class="text-center">
                                <b-dropdown class="m-md-2" id="dropdown-1" text="Participant">
                                    <b-dropdown-item @click="removeRole(user)">Remove</b-dropdown-item>
                                </b-dropdown>
                            </b-col>
                        </b-row>
                    </b-card>
                </b-tab>
                <!-- followers tabs could be added if we please just uncomment

                <b-tab key="Followers" title="Followers" @click="currentGroup='Followers'">
                    <b-card style="margin-top:10px;" :key="user.profile_id" v-for="user in followers">
                        <b-row class="text-center" align-v="center">
                            <b-col class="text-center">
                                {{ user.full_name }}
                            </b-col>
                            <b-col v-if="activityCreatorId==loggedInId" class="text-center">
                                <b-dropdown class="m-md-2" id="dropdown-1" text="Participant">
                                    <b-dropdown-item>Organiser</b-dropdown-item>
                                </b-dropdown>
                            </b-col>
                        </b-row>
                    </b-card>
                </b-tab>-->
            </b-tabs>
        </b-card>

    </div>
</template>

<script>
    import api from '@/Api';

    export default {
        name: "FollowerUserList",

        // Component Properties
        props: {
            activityId: {
                type: Number,
                default: null,
            },
            activityCreatorId: {
                type: Number,
                default: null,
            },
            loggedInId: {
                type: Number,
                default: null,
            }
        },

        // Component Members
        data() {
            return {
                organisers: [],
                participants: [],
                accessors: [],
                followers: [],
                currentGroup: "Participants",
                limit: 10,
                organiserOffset: 0,
                participantOffset: 0,
                accessorOffset: 0,
                followerOffset: 0,
                roleData: null,
            }
        },
        async mounted() {
            this.scroll();
            this.getMembers();
        },

        // Component Methods
        methods: {
            getMembers: async function() {
                await api.getActivityMembers(this.activityId)
                    .then((res) => {
                        this.organisers = res.data.Organiser;
                        this.participants = res.data.Participant;
                        this.accessors = res.data.Access;
                        this.followers = res.data.Follower;
                    })
                    .catch(err => {
                        console.log(err)
                    });
            },
            getMoreOrganisers: async function() {
                this.organiserOffset += this.limit;
                await api.getActivityOrganisers(this.activityId, this.organiserOffset, this.limit)
                    .then((res) => {
                        for (let i = 0; i < res.data.Organiser.length; i++) {
                            if (!this.organisers.includes(res.data.Organiser[i])) {
                                this.organisers.push(res.data.Organiser[i]);
                            }
                        }
                    })
                    .catch(err => {
                        console.log(err)
                    });
            },
            getMoreParticipants: async function() {
                this.participantOffset += this.limit;
                await api.getActivityParticipants(this.activityId, this.participantOffset, this.limit)
                    .then((res) => {
                        for (let i = 0; i < res.data.Participant.length; i++) {
                            if (!this.participants.includes(res.data.Participant[i])) {
                                this.participants.push(res.data.Participant[i]);
                            }
                        }
                    })
                    .catch(err => {
                        console.log(err)
                    });
            },
            /* Uncomment for scroll loading for accessor and follower roles */
            getMoreAccessors: async function() {
                this.accessorOffset += this.limit;
                await api.getActivityAccessors(this.activityId, this.accessorOffset, this.limit)
                    .then((res) => {
                        for (let i = 0; i < res.data.Participant.length; i++) {
                            if (!this.accessors.includes(res.data.Access[i])) {
                                this.accessors.push(res.data.Access[i]);
                            }
                        }
                    })
                    .catch(err => {
                        console.log(err)
                    });
            },
            /*
            getMoreFollowers: async function() {
                this.followerOffset += this.limit;
                await api.getActivityFollowers(this.activityId, this.followerOffset, this.limit)
                    .then((res) => {
                        for (let i = 0; i < res.data.Follower.length; i++) {
                            this.followers.push(res.data.Follower[i]);
                        }
                    })
                    .catch(err => {
                        console.log(err)
                    });
            },*/
            changeRole: async function (user, role) {
                await api.getProfile(user.profile_id)
                    .then((res) => {
                        this.roleData = {
                            subscriber: {
                                email: res.data.primary_email.address,
                                role: role
                            }
                        };
                        console.log(res.data.primary_email.address);
                        console.log(role);
                    }).catch(err => {
                        console.log(err)
                        return;
                    });
                await api.updateRole(this.activityCreatorId, this.activityId, this.roleData)
                    .then(() => {
                        if (role == "organiser") {
                            this.participantOffset -= 1;
                            const index = this.participants.indexOf(user);
                            this.participants.splice(index, 1);
                            this.organisers.push(user);
                        } else {
                            this.organiserOffset -= 1;
                            const index = this.participants.indexOf(user);
                            this.organisers.splice(index, 1);
                            this.participants.push(user);
                        }
                    })
                    .catch(err => {
                        console.log(err);
                        return;
                    });
            },
            removeRole: async function (user) {
                await api.getProfile(user.profile_id)
                    .then((res) => {
                        this.roleData = {
                            email: res.data.primary_email.address
                        };
                        console.log(res.data.primary_email.address);
                    }).catch(err => {
                        console.log(err)
                        return;
                    });
                await api.removeRole(this.activityCreatorId, this.activityId, this.roleData)
                    .then(() => {
                        console.log("there is a bug so this will never run");
                    })
                    .catch(err => {
                        console.log(err);
                    });
                if (this.currentGroup == "Participants") {
                    this.participantOffset -= 1;
                    const index = this.participants.indexOf(user);
                    this.participants.splice(index, 1);
                }
                if (this.currentGroup == "Organisers") {
                    this.organiserOffset -= 1;
                    const index = this.organisers.indexOf(user);
                    this.organisers.splice(index, 1);
                }
                if (this.currentGroup == "Accessors") {
                    this.accessorOffset -= 1;
                    const index = this.accessors.indexOf(user);
                    this.accessors.splice(index, 1);
                }
            },
            scroll() {
                window.onscroll = async () => {
                    let bottomOfWindow = document.documentElement.scrollTop + window.innerHeight === document.documentElement.offsetHeight;
                    if (bottomOfWindow) {
                        if (this.currentGroup == "Organisers") {
                            await this.getMoreOrganisers();
                        }
                        if (this.currentGroup == "Participants") {
                            await this.getMoreParticipants();
                        }
                        if (this.currentGroup == "Accessors") {
                            await this.getMoreAccessors();
                        }
                        if (this.currentGroup == "Followers") {
                            await this.getMoreFollowers();
                        }
                    }
                }
            }
        }
    }
</script>

<style scoped>

</style>