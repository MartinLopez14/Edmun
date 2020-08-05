import { shallowMount, createLocalVue } from '@vue/test-utils';
import Component from '../pages/EditProfile/User.vue'; // name of your Vue component
import {BootstrapVue, IconsPlugin, NavbarPlugin} from 'bootstrap-vue'
import Vuelidate from "vuelidate";
import axios from "axios";
import VueRouter from 'vue-router'
import routes from '../routes'
//
const router = new VueRouter({routes})
const localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(IconsPlugin);
localVue.use(NavbarPlugin);
localVue.use(Vuelidate);
localVue.use(VueRouter);
localVue.use(axios);

let wrapper;

beforeEach(() => {
    wrapper = shallowMount(Component, {
        propsData: {},
        mocks: {},
        stubs: {},
        methods: {},
        localVue,
        router
    });
});

afterEach(() => {
    wrapper.destroy();
});

describe('Component', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

});

describe('Profile Info Validation', () => {
    test('Valid First name', () => {
        wrapper.vm.$v.profileForm.firstname.$model = "Wu-kong";
        expect(wrapper.vm.$v.profileForm.firstname.$error).toBe(false);
    });

    test('Invalid First name, name contains numbers', () => {
        wrapper.vm.$v.profileForm.firstname.$model = "Wu123";
        expect(wrapper.vm.$v.profileForm.firstname.$error).toBe(true);
    });
})

