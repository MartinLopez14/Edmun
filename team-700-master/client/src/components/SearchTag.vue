<template>
    <div>
        <label>
            {{ titleLabel }}
        </label>
        <b-form-text>{{ helpText }}</b-form-text>
        <b-form-tags v-model="values" no-outer-focus
                     style="margin: 0; padding: 0; background-color: initial; border: 0;">
            <ul v-if="values.length > 0" class="list-inline d-inline-block mb-2">
                <li v-for="tag in values" :key="tag" class="list-inline-item">
                    <b-form-tag
                            @remove="removeTag(tag)"
                            :key="tag"
                            :title="tag"

                            class="mr-1"
                    >{{ tag }}
                    </b-form-tag>
                </li>
            </ul>
            <b-input-group>
                <b-form-input
                        v-model="value"
                        :placeholder="inputPlaceholder"
                        class="form-control"
                        :state="inputState()"
                        :disabled="maxEntriesReached"
                        :maxlength="maxCharacters"
                        list="autocomplete"
                        v-on:input="emitInputToParent">
                </b-form-input>

                <b-input-group-append>
                    <b-button @click="addTag(value)" variant="primary" :disabled="maxEntriesReached || noInput || !checkInput()">Add</b-button>
                </b-input-group-append>
                <b-form-invalid-feedback>
                    {{ inputErrorMessage }}
                </b-form-invalid-feedback>

            </b-input-group>
            <div v-if="!selected">
                <div v-for="option in options" :key="option">
                    <b-input
                            class="autocomplete-item"
                            :readonly=true
                            v-on:click="setInput(option)"
                            :value=option>
                    </b-input>
                </div>
            </div>
        </b-form-tags>
    </div>
</template>

<script>
    export default {
        name: "SearchTag.vue",
        props: {
            maxEntries: Number,
            options: Array,
            titleLabel: String,
            inputPlaceholder: String,
            helpText: String,
            values: Array,
            inputCharacterLimit: Number
        },
        data() {
            return {
                value: "",
                selected: false,
                inputErrorMessage: ""
            }
        },
        methods: {
            setInput(value) {
                this.value = value;
                this.selected = true;
            },
            addTag(value) {
                value = value.toLowerCase();
                if (value[0] !== "#") {
                    value = "#" + value;
                }
                if (!this.values.includes(value)) {
                    this.values.push(value);
                    this.value = "";
                    this.emitTagsToParent();
                }
            },
            removeTag(value) {
                const index = this.values.indexOf(value);
                if (index > -1) {
                    this.values.splice(index, 1);
                    this.emitTagsToParent();
                }
            },
            emitTagsToParent() {
                this.$emit('emitTags', this.values);
            },
            emitInputToParent() {
                this.selected = false;
                this.$emit('emitInput', this.value);
            },
            valueValid() {
                let pattern = /^#?[a-zA-Z0-9_]*$/;
                if (!pattern.test(this.value)) {
                    return false;
                } else {
                    return true;
                }
            },
            inputState() {
                if (this.checkInput()) {
                    return null;
                } else {
                    return false;
                }
            },
            checkInput() {
                if (!this.valueValid()) {
                    this.inputErrorMessage = "Hashtags may only contain alphabets, numbers and underscores.";
                    return false;
                }
                let value = this.value.toLowerCase();
                if (this.values.includes(value) || this.values.includes("#" + value)) {
                    this.inputErrorMessage = "This hashtag has already been added";
                    return false;
                } else {
                    return true;
                }
            },
        },
        computed: {
            maxEntriesReached() {
                return this.values.length >= this.maxEntries;
            },
            maxCharacters() {
                if (this.values[0] == "#") {
                    return this.inputCharacterLimit + 1;
                } else {
                    return this.inputCharacterLimit;
                }
            },

            noInput() {
                let tempValue = this.value;
                if (this.value[0] == "#") {
                    tempValue = tempValue.substr(1);
                }
                if (tempValue.length == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }
</script>

<style scoped>
    .autocomplete-item {
        cursor: pointer;
        background-color: white;

    }

    .autocomplete-item:focus {
        -webkit-box-shadow: none;
        box-shadow: none;

    }

    .autocomplete-item:hover {
        background-color: lightgrey;
    }
</style>