<template>
    <div id="app" v-if="isLoggedIn">
        <NavBar v-bind:isLoggedIn="isLoggedIn"></NavBar>
        <div class="container">
            <b-container fluid>
                <b-row>
                    <b-col>
                        <h3>Add an Activity</h3>
                        <hr>
                    </b-col>
                </b-row>
                <b-form novalidate @submit.stop.prevent="onSubmit">
                    <b-row>
                        <b-col>
                            <b-form-group>
                                <b-form-radio-group id="duration-type-group" v-model="isContinuous">
                                    <b-form-radio name="duration-type" value="1">Continuous</b-form-radio>
                                    <b-form-radio name="duration-type" value="0">Duration</b-form-radio>
                                </b-form-radio-group>
                            </b-form-group>
                        </b-col>
                    </b-row>

                    <b-row v-if="isContinuous == '0'">
                        <b-col>
                            <b-form-group id="start-date-input-group" label="Start Date" label-for="start-date-input">
                                <b-form-input
                                        id="start-date-input"
                                        type="date"
                                        :state="validateDurationState('startDate')"
                                        v-model="$v.durationForm.startDate.$model"
                                        aria-describedby="start-date-feedback"
                                ></b-form-input>
                                <b-form-invalid-feedback id="start-date-feedback">This is a required field and cannot be
                                    in the past.
                                </b-form-invalid-feedback>
                            </b-form-group>
                        </b-col>
                        <b-col>
                            <b-form-group id="end-date-input-group" label="End Date" label-for="end-date-input">
                                <b-form-input
                                        id="end-date-input"
                                        type="date"
                                        :state="validateDurationState('endDate')"
                                        v-model="$v.durationForm.endDate.$model"
                                        aria-describedby="end-date-feedback"
                                ></b-form-input>
                                <b-form-invalid-feedback id="end-date-feedback">This is a required field and cannot be
                                    before start date.
                                </b-form-invalid-feedback>
                            </b-form-group>
                        </b-col>
                    </b-row>

                    <b-row v-if="isContinuous == '0'" style="margin-bottom:10px;border-bottom:1px solid #ececec;">
                        <b-col>
                            <b-form-group id="start-time-input-group" label="Start Time" label-for="start-time-input">
                                <b-form-input
                                        id="start-time-input"
                                        type="time"
                                        :state="validateDurationState('startTime')"
                                        v-model="$v.durationForm.startTime.$model"
                                        aria-describedby="start-time-feedback"
                                ></b-form-input>
                                <b-form-invalid-feedback id="start-time-feedback">Start time cannot be in the past.
                                </b-form-invalid-feedback>
                            </b-form-group>
                        </b-col>
                        <b-col>
                            <b-form-group id="end-time-input-group" label="End Time" label-for="end-time-input">
                                <b-form-input
                                        id="end-time-input"
                                        type="time"
                                        :state="validateDurationState('endTime')"
                                        v-model="$v.durationForm.endTime.$model"
                                        aria-describedby="end-time-feedback"
                                ></b-form-input>
                                <b-form-invalid-feedback id="end-time-feedback">End time cannot be before or the same as
                                    start time.
                                </b-form-invalid-feedback>
                            </b-form-group>
                        </b-col>
                    </b-row>

                    <b-row>
                        <b-col>
                            <span v-if="this.form.selectedActivityTypes.length > 0">
                                Activity Types:
                                <b-form-text>Click on the activity type to remove</b-form-text>
                            </span>
                            <b-list-group horizontal="md" v-if="this.form.selectedActivityTypes">
                                <b-list-group-item v-for="activityType in this.form.selectedActivityTypes"
                                                   :key="activityType" v-on:click="deleteActivityType(activityType)"
                                                   class="clickable">
                                    {{ activityType }}
                                </b-list-group-item>
                            </b-list-group>
                            <b-form-group id="activity-type-group" label="Add Activity Type" label-for="activity-type">
                                <b-form-select
                                        id="activity-type"
                                        name="activity-type"
                                        v-model="$v.form.selectedActivityType.$model"
                                        :state="validateState('selectedActivityType')"
                                        :options="activityTypes"
                                        aria-describedby="activity-type-feedback"
                                        v-on:change="addActivityType()"
                                ></b-form-select>
                                <b-form-invalid-feedback id="activity-type-feedback">Please select an activity type.
                                </b-form-invalid-feedback>
                            </b-form-group>
                            <hr>
                        </b-col>
                    </b-row>

                    <b-row>
                        <b-col>
                            <SearchTag :max-entries="30" :title-label="'Hashtags'" :options="hashtag.options"
                                       :values="hashtag.values"
                                       :help-text="'Max 30 hashtags'"
                                       :input-character-limit="140"
                                       v-on:emitInput="autocompleteInput"
                                       v-on:emitTags="manageTags"></SearchTag>
                        </b-col>
                    </b-row>
                    <hr>
                    <b-row>
                        <b-col>
                            <b-form-group id="name-input-group" label="Name" label-for="name-input">
                                <b-form-input
                                        id="name-input"
                                        name="name-input"
                                        v-model="$v.form.name.$model"
                                        :state="validateState('name')"
                                        aria-describedby="name-feedback"
                                ></b-form-input>
                                <b-form-invalid-feedback id="name-feedback">This is a required field.
                                </b-form-invalid-feedback>
                            </b-form-group>
                        </b-col>
                    </b-row>

                    <b-row>
                        <b-col>
                            <b-form-group id="description-input-group" label="Description"
                                          label-for="description-input">
                                <b-form-textarea
                                        id="description-input"
                                        name="description-input"
                                        v-model="$v.form.description.$model"
                                        :state="validateState('description')"
                                        placeholder="What is the activity about?"
                                ></b-form-textarea>
                            </b-form-group>
                        </b-col>
                    </b-row>

                    <b-row>
                        <b-col>
                            <b-form-group id="location-input-group" label="Location" label-for="location-input"
                                          description="Please enter the location you want to search for and select from the dropdown"
                                          invalid-feedback="The location of the activity must be chosen from the drop down">
                                <b-form-input id="location-input"
                                              name="location-input"
                                              placeholder="Search for a city/county"
                                              autocomplete="off"
                                              class="form-control"
                                              type="text"
                                              v-model="$v.form.location.$model"
                                              :state="validateState('location')"
                                              v-on:keyup="getLocationData(form.location)"
                                              v-on:input="locationData=null">
                                </b-form-input>
                                <div v-for="i in locations" :key="i.place_id">
                                    <b-input class="clickable" v-on:click="selectLocation(i)" type="button"
                                             :value=i.display_name></b-input>
                                </div>
                            </b-form-group>
                        </b-col>
                    </b-row>
                    <b-row>
                        <b-col >
                            <label>Select sharing</label>
                            <b-form-select v-model=selectedVisibility :options="options" size="sm"/>
                            <br><br>
                        </b-col>
                    </b-row>


                    <b-row>
                        <b-col sm="10">
                            <b-button type="submit" variant="primary">Submit</b-button>
                        </b-col>
                        <b-col sm="2">
                            <b-button @click="goToActivities">Your Activities</b-button>
                        </b-col>
                    </b-row>

                    <b-form-valid-feedback :state='activityUpdateMessage != ""'>
                        {{activityUpdateMessage}}
                    </b-form-valid-feedback>
                    <b-form-invalid-feedback :state='activityErrorMessage == ""'>
                        {{activityErrorMessage}}
                    </b-form-invalid-feedback>

                </b-form>
            </b-container>
        </div>
    </div>
</template>

<script>
    import NavBar from "@/components/NavBar.vue";
    import SearchTag from "../../components/SearchTag";
    import {validationMixin} from "vuelidate";
    import {required} from 'vuelidate/lib/validators';
    import locationMixin from "../../mixins/locationMixin";
    import AdminMixin from "../../mixins/AdminMixin";
    import api from '@/Api'

    export default {
        mixins: [validationMixin, locationMixin],
        components: {
            NavBar,
            SearchTag,
        },
        data() {
            return {
                isLoggedIn: true,
                userName: '',
                isContinuous: 1,
                profileId: null,
                activityTypes: [],
                form: {
                    name: null,
                    description: null,
                    selectedActivityType: 0,
                    selectedActivityTypes: [],
                    date: null,
                    location: null
                },
                durationForm: {
                    startDate: null,
                    endDate: null,
                    startTime: null,
                    endTime: null
                },
                activityUpdateMessage: "",
                activityErrorMessage: "",
                locationData: null,
                loggedInIsAdmin: false,
                hashtag: {
                    options: [],
                    values: [],
                },
                selectedVisibility: 'Public',
                options: [
                    { value: 'Private', text: 'Private' },
                    { value: 'Restricted', text: 'Restricted' },
                    { value: 'Public', text: 'Public' }
                ],

            }
        },
        validations: {
            form: {
                name: {
                    required
                },
                description: {},
                selectedActivityType: {
                    required,
                    validateActivityType() {
                        if (this.form.selectedActivityTypes.length < 1) {
                            return false
                        }
                        return true
                    }
                },
                date: {},
                location: {
                    locationValidate() {
                        if (this.locations.length == 0 || this.locations == null) {
                            if (this.locationData != null && (this.form.location != null || this.form.location != "")) {
                                return true;
                            } else if (this.locationData == null && (this.form.location == null || this.form.location == "")) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                        return false;
                    }
                }
            },
            durationForm: {
                startDate: {
                    required,
                    dateValidate(val) {
                        return val >= new Date().toISOString().split('T')[0];
                    }
                },
                endDate: {
                    required,
                    dateValidate(val) {
                        let startDate = new Date(this.durationForm.startDate);
                        let endDate = new Date(val);
                        if (endDate < startDate) {
                            return false;
                        }
                        return true;
                    }
                },
                startTime: {},
                endTime: {
                    timeValidate(val) {
                        let startTime = this.durationForm.startTime;
                        //let startDate = new Date(this.durationForm.startDate);
                        //let endDate = new Date(this.durationForm.endDate);
                        if (this.durationForm.startDate == this.durationForm.endDate) {
                            if (val && startTime) {
                                let splitStartTime = startTime.split(":");
                                let splitEndTime = val.split(":");
                                let startTimeObj = new Date();
                                startTimeObj.setHours(splitStartTime[0], splitStartTime[1]);
                                let endTimeObj = new Date();
                                endTimeObj.setHours(splitEndTime[0], splitEndTime[1]);
                                if (endTimeObj <= startTimeObj) {
                                    return false;
                                }
                            }
                        }
                        return true;
                    }
                },
            },
        },
        methods: {
            manageTags: function (value) {
                this.hashtag.values = value;
                this.hashtag.options = [];
            },
            autocompleteInput: function (value) {
                let pattern = /^#?[a-zA-Z0-9_]*$/;
                if (!pattern.test(value)) {
                    this.hashtag.options = [];
                    return;
                }
                if (value[0] == "#") {
                    value = value.substr(1);
                }
                if (value.length > 2) {
                    let vue = this;
                    api.getHashtagAutocomplete(value)
                        .then(function (response) {
                            let results = response.data.results;
                            for (let i = 0; i < results.length; i++) {
                                results[i] = "#" + results[i];
                            }
                            vue.hashtag.options = results;
                        })
                        .catch(function () {

                        });
                } else {
                    this.hashtag.options = [];
                }
            },
            getActivities: function () {
                let currentObj = this;
                api.getProfileActivityTypes()
                    .then(function (response) {
                        currentObj.activityTypes = response.data;
                    })
                    .catch(function (error) {
                        console.log(error.response.data);
                    });
            },
            deleteActivityType: function (activityType) {
                this.$delete(this.form.selectedActivityTypes, this.form.selectedActivityTypes.indexOf(activityType));
                const selectedActivitysLength = this.form.selectedActivityTypes.length;
                if (selectedActivitysLength == 0) {
                    this.$v.form.selectedActivityType.$model = null
                } else {
                    this.$v.form.selectedActivityType.$model = this.form.selectedActivityTypes[selectedActivitysLength - 1]
                }

            },
            validateState(name) {
                const {$dirty, $error} = this.$v.form[name];
                return $dirty ? !$error : null;
            },
            validateDurationState(name) {
                const {$dirty, $error} = this.$v.durationForm[name];
                return $dirty ? !$error : null;
            },
            addActivityType() {
                if (this.form.selectedActivityType == 0) {
                    return;
                }

                if (!this.form.selectedActivityTypes.includes(this.form.selectedActivityType)) {
                    this.form.selectedActivityTypes.push(this.form.selectedActivityType);

                }
            },
            selectLocation(location) {
                this.form.location = location.display_name;
                this.locations = [];
                console.log(location);

                if (location !== null) {
                    let data = {
                        country: null,
                        state: null,
                        city: null
                    };
                    if (location.address.city) {
                        data.city = location.address.city;
                    }
                    if (location.address.state) {
                        data.state = location.address.state;
                    }
                    if (location.address.country) {
                        data.country = location.address.country;
                    }
                    this.locationData = data;
                }
            },
            onSubmit() {
                this.$v.form.$touch();
                let currentObj = this;
                let userId = this.profileId;
                if (this.loggedInIsAdmin) {
                    userId = this.$route.params.id;
                }
                if (this.isContinuous == '1') {
                    if (this.$v.form.$anyError) {
                        return;
                    }
                    let data = {
                        activity_name: this.form.name,
                        description: this.form.description,
                        activity_type: this.form.selectedActivityTypes,
                        continuous: true,
                        location: this.locationData,
                        hashtags: this.hashtag.values,
                        visibility: this.selectedVisibility
                    }
                    api.createActivity(userId, data)
                        .then(function () {
                            currentObj.activityErrorMessage = "";
                            currentObj.activityUpdateMessage = "'" + currentObj.form.name + "' was successfully added to your activities";
                            alert(currentObj.form.name + " was successfully added to your list of activities");
                            currentObj.$router.go(0);
                        })
                        .catch(function (error) {
                            currentObj.activityUpdateMessage = "";
                            currentObj.activityErrorMessage = "Failed to update activity: " + error.response.data + ". Please try again";
                            console.log(error);
                        });

                } else {
                    this.$v.durationForm.$touch();
                    if (this.$v.durationForm.$anyError) {
                        return;
                    }
                    const isoDates = this.getDates();
                    let data = {
                        activity_name: this.form.name,
                        description: this.form.description,
                        activity_type: this.form.selectedActivityTypes, continuous: false, start_time: isoDates[0],
                        visibility: this.selectedVisibility,
                        end_time: isoDates[1]
                    }
                    api.createActivity(userId, data)
                        .then(function (response) {
                            console.log(response);
                            currentObj.activityErrorMessage = "";
                            currentObj.activityUpdateMessage = "'" + currentObj.form.name + "' was successfully added to your activities";
                            alert(currentObj.form.name + " was successfully added to your list of activities");
                            currentObj.goToActivities();
                        })
                        .catch(function (error) {
                            currentObj.activityUpdateMessage = "";
                            currentObj.activityErrorMessage = "Failed to update activity: " + error.response.data + ". Please try again";
                        });
                }
            },

            getDates: function () {
                let startDate = new Date(this.durationForm.startDate);
                let endDate = new Date(this.durationForm.endDate);

                if (this.durationForm.startTime != "" && this.durationForm.startTime != null) {
                    startDate = new Date(this.durationForm.startDate + " " + this.durationForm.startTime + " UTC");
                }

                if (this.durationForm.endTime != "" && this.durationForm.startTime != null) {
                    endDate = new Date(this.durationForm.endDate + " " + this.durationForm.endTime + " UTC");
                }
                let startDateISO = startDate.toISOString().slice(0, -5);
                let endDateISO = endDate.toISOString().slice(0, -5);

                var currentTime = new Date();
                const offset = (currentTime.getTimezoneOffset());

                const currentTimezone = (offset / 60) * -1;
                if (currentTimezone !== 0) {
                    startDateISO += currentTimezone > 0 ? '+' : '';
                    endDateISO += currentTimezone > 0 ? '+' : '';
                }
                startDateISO += currentTimezone.toString() + "00";
                endDateISO += currentTimezone.toString() + "00";

                if (this.durationForm.startTime == "" || this.durationForm.startTime == null) {
                    startDateISO = startDateISO.substring(0, 11) + "24" + startDateISO.substring(13, startDateISO.length);
                }
                if (this.durationForm.endTime == "" || this.durationForm.endTime == null) {
                    endDateISO = endDateISO.substring(0, 11) + "24" + endDateISO.substring(13, endDateISO.length);
                }
                return [startDateISO, endDateISO];

            },
            getUserId: function () {
                let currentObj = this;
                api.getProfileId()
                    .then(function (response) {
                        currentObj.profileId = response.data;
                    })
                    .catch(function () {
                    });
            },
            getUserName: function () {
                let currentObj = this;
                api.getFirstName()
                    .then(function (response) {
                        currentObj.userName = response.data;
                    })
                    .catch(function () {
                    });
            },
            goToActivities() {
                const profileId = this.$route.params.id;
                this.$router.push('/profiles/' + profileId + '/activities');
            },
            checkAuthorized: async function () {
                let currentObj = this;
                this.loggedInIsAdmin = AdminMixin.methods.checkUserIsAdmin();
                return api.getProfileId()
                    .then(function (response) {
                        currentObj.profileId = response.data;
                        if (parseInt(currentObj.profileId) !== parseInt(currentObj.$route.params.id) && !currentObj.loggedInIsAdmin) {
                            currentObj.$router.push("/login");
                        }
                    })
                    .catch(function () {
                    });
            },
            onChildClick: function(val) {
               this.selectedVisibility = val
            }



        },
        mounted: async function () {
            await this.checkAuthorized();
            this.getActivities();
            this.getUserId();
            this.getUserName();
        }
    }
</script>

<style scoped>
    [v-cloak] {
        display: none;
    }

    .container {
        background-color: #f2f2f2;
        padding: 20px 20px 20px 20px;
        border: 1px solid lightgrey;
        border-radius: 3px;
    }

    .clickable {
        cursor: pointer;
    }
</style>