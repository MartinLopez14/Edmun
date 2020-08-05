<template>
    <div>
        <div v-if="modal == true">
            <b-button v-b-modal.modal-1 v-if="visibility == 'Public'" variant="success">{{visibility}}</b-button>
            <b-button v-b-modal.modal-1 v-if="visibility == 'Restricted'" variant="warning">{{visibility}}</b-button>
            <b-button v-b-modal.modal-1 v-if="visibility == 'Private'" variant="danger">{{visibility}}</b-button>

            <b-modal size="xl" style="padding: 1em" id="modal-1" title="Share" hide-footer hide-header @show="updateSelectedValue" body-class="p-0" >
                <b-button-close style="padding: 10px" @click="$bvModal.hide('modal-1')"></b-button-close>
                <b-row>
                    <b-col style="color: white; background: #4d4d4d; max-width: 40%; horiz-align: center">
                        <h4>Select Sharing Option</h4>
                        <div style="margin-top: 30%; font-size: small">
                            <p v-if="selected == 'Public'">Anyone will be able to view this activity.</p>
                            <p v-if="selected == 'Private'">Only you will be able to view this activity.</p>
                            <p v-if="selected == 'Restricted'">Only people you allow will be able to view this activity.</p>
                        </div>
                        <b-form-select style="margin-bottom: 40%;" v-model="selected" :options="options" size="sm"></b-form-select>
                        <b-alert
                                :show="showWarning"
                                dismissible
                                variant="danger"
                        >You have {{organiserCount}} organisers, {{followerCount}} followers and {{partCount}} participants. You are changing visibility type to be more restrictive, are you sure?
                        </b-alert>
                        <b-form-group v-if="selected =='Restricted'" style="color: white" for="emailInput">
                            <p style="font-size: small">Add multiple members by separating emails by space or semi-colon</p>
                            <label>Input member emails</label>
                            <b-input
                                     name="email"
                                     id="emailInput"
                                     placeholder="john@example.com kevin@example.com"
                                     v-model="emailInput"
                                     ></b-input>
                            <p style="color: #cc9a9a">{{followerCount}} Followers {{partCount}} Participants {{organiserCount}} Organisers</p>
                        </b-form-group>
                        <b-button style="margin: 15px" @click="submit()">Ok</b-button>
                    </b-col>

                    <b-col style="size: auto">
                        <br>
                        <follower-user-list :activity-id=activityId :logged-in-id=profileId :activity-creator-id="profileId" style="size: auto"></follower-user-list>
                    </b-col>
                </b-row>
            </b-modal>
        </div>

    </div>
</template>

<script>
    import api from '@/Api'
    import FollowerUserList from "./Activity/FollowerUserList";
    export default {

        name: "ShareActivity",
        components: {FollowerUserList},
        props: {
            profileId: String,
            activityId: String,
            modal: Boolean,
            visibility: String,
        },
        data() {
            return {
                // selectedVisibility: null,
                selected: 'Public',
                description: 'Reeee',
                emailInput: '',
                options: [
                    { value: 'Private', text: 'Private' },
                     { value: 'Restricted', text: 'Restricted' },
                    { value: 'Public', text: 'Public' }
                ],
                organisers: [],
                participants: [],
                accessors: [],
                followers: [],
                showWarning:false,
                partCount:null,
                organiserCount:null,
                followerCount:null,

            }
        },
        methods: {
            emitInputToParent() {
                this.$emit('emitInput', this.selected);
            },
            async updateSelectedValue() {
                this.emailInput = "";
                this.getCount();
                this.selected = this.visibility
                await api.getActivityMembers(this.activityId, 0, 10)
                    .then((res) => {
                        this.organisers = res.data.Organiser;
                        this.participants = res.data.Participant;
                        this.accessors = res.data.Access;
                        this.followers = res.data.Follower;
                        console.log(this.organisers.length)
                    })
                    .catch(err => {
                        console.log(err)
                    });
                this.showWarning = false
            },
            deleteUser(id) {
                alert(id)

            },

            changeVisibilityType() {

                let data = {
                    visibility: this.selected.toLowerCase(),
                }
                if (this.selected == "Restricted" && this.email != "") {
                    data["accessors"] = this.email;
                }
                const currentObj = this;
                api.updateActivityVisibility(this.profileId, this.activityId, data)
                    .then(function () {
                        currentObj.visibility = currentObj.selected
                        currentObj.$bvModal.hide('modal-1')
                    })
                    .catch(function (error) {
                        alert(error.response.data)
                    });

            },
            submit() {
                this.parseEmailInput();
                this.changeVisibilityType();
            },
            parseEmailInput() {
                var entryArray
                let data = this.emailInput
                if (data.includes(";")){
                    entryArray = data.split(';')
                } else {
                    entryArray = data.split(' ')
                }
                this.email = entryArray
            },
            beforeMount() {
                this.updateSelectedValue()
            },
            getCount() {
                const currentObj = this;
                api.getActivityMemberCounts(this.activityId)
                    .then(function (response) {
                        currentObj.partCount = response.data.participants
                        currentObj.followerCount = response.data.followers
                        currentObj.organiserCount = response.data.organisers
                    })
                    .catch(function (error) {
                        console.log(error)
                    });
            }

    },
        computed:{

        },
        watch: {
            selected: function(){
                if (this.organiserCount === 0 && this.partCount === 0 && this.followerCount === 0) {
                    this.showWarning = false
                    return
                }
                if(this.visibility === "Restricted" && this.selected === "Private" || (this.visibility === "Public" && this.selected !== "Public")){
                    this.showWarning = true
                }else {
                    this.showWarning = false
                }
            }
        }
    }
</script>
