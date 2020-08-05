import Vue from 'vue';

// Global store for variables that are needed across multiple components

export const store = Vue.observable({
    adminAccess: true
})

export const mutations = {
    switchAccessControl(value) {
        store.adminAccess = value;
    }
}
