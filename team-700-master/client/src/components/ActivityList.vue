<template>
    <div>
        <slot></slot>
        <b-table hover
            :items="items"
            :fields="fields"
            @row-clicked="goToActivity"
            :busy="tableIsLoading"
        >
            <template v-slot:cell(selectedActivity)></template>
            <template v-slot:table-busy>
                <div class="text-center text-primary my-2">
                    <b-spinner class="align-middle"></b-spinner>
                    <strong> Loading...</strong>
                </div>
            </template>
        </b-table>
    </div>
</template>

<script>
    export default {
        name: "ActivityList",
        props: {
            items: Array,
            fields: Array,
            profileId: Number,
            tableIsLoading: Number
        },
        data() {
            return {
                selectedActivity: []
            }
        },
        methods: {
            goToActivity: function(items) {
                this.selectedActivity = items;
                let activityId = this.selectedActivity.id;
                this.$router.push('/profiles/' + this.profileId + '/activities/' + activityId);
            }
        }
    }
</script>