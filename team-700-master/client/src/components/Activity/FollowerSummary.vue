<template>
    <p>
        <span v-for="(group, group_index) in user_groups" :key="group.name">
            {{group.count}} {{group.name}}
            <span v-if="group_index !== user_groups.length - 1">
                |
            </span>
        </span>
    </p>
</template>

<script>
    import api from '@/Api'

    export default {
        name: "FollowerSummary.vue",

        // Component Properties
        props: {
            activityId: {
                type: Number,
                default: null,
            },
        },

        // Component Members
        data() {
            return {
                user_groups: []
            }
        },

        mounted() {
            this.loadFollowerSummary()
        },

        // Component Methods
        methods: {
            loadFollowerSummary: function () {
                const currentObj = this;
                api.getActivityMemberCounts(this.activityId)
                    .then(function (response) {
                        currentObj.user_groups.push({
                            "name": "Participants",
                            "count": response.data.participants
                        });
                        currentObj.user_groups.push({
                            "name": "Followers",
                            "count": response.data.followers
                        });
                        currentObj.user_groups.push({
                            "name": "Organizers",
                            "count": response.data.organisers
                        });
                    })
                    .catch(function (error) {
                        console.log(error)
                    });


                // NOTE(Connor): This should have the limitation of not showing organisers for limited rights

            },
        }
    }
</script>

<style scoped>

</style>