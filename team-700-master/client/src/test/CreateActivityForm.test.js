import { shallowMount, createLocalVue } from '@vue/test-utils';
import Component from '../pages/CreateActivity/App.vue'; // name of your Vue component
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
    test('Valid First Name', () => {
        wrapper.vm.$v.form.name.$model = "Wu-kong";
        expect(wrapper.vm.$v.form.name.$error).toBe(false);
    });

    test('Empty First Name', () => {
        wrapper.vm.$v.form.name.$model = "";
        expect(wrapper.vm.$v.form.name.$error).toBe(true);
    });

    test('Valid Activity Type', () => {
       wrapper.vm.$v.form.selectedActivityType.$model = "Run";
        expect(wrapper.vm.$v.form.selectedActivityType.$error).toBe(true);
    });

    test('Empty Activity Type', () => {
        wrapper.vm.$v.form.selectedActivityType.$model = "";
        expect(wrapper.vm.$v.form.selectedActivityType.$error).toBe(true);
    });

    test('Valid Start Date', () => {
        wrapper.vm.$v.durationForm.startDate.$model = "2040-06-19";
        expect(wrapper.vm.$v.durationForm.startDate.$error).toBe(false);
    });

    test('Empty Start Date', () => {
        wrapper.vm.$v.durationForm.startDate.$model = "";
        expect(wrapper.vm.$v.durationForm.startDate.$error).toBe(true);
    });

    test('Valid End Date', () => {
        wrapper.vm.$v.durationForm.endDate.$model = "2020-06-19";
        expect(wrapper.vm.$v.durationForm.endDate.$error).toBe(false);
    });

    test('Empty End Date', () => {
        wrapper.vm.$v.durationForm.endDate.$model = "";
        expect(wrapper.vm.$v.durationForm.endDate.$error).toBe(true);
    });

    test('End Date after Start Date', () => {
        wrapper.vm.$v.durationForm.startDate.$model = "2020-10-10";
        wrapper.vm.$v.durationForm.endDate.$model = "2020-10-11";
        expect(wrapper.vm.$v.durationForm.endDate.$error).toBe(false);
    });

    test('End Date before Start Date (Invalid)', () => {
        wrapper.vm.$v.durationForm.startDate.$model = "2020-10-10";
        wrapper.vm.$v.durationForm.endDate.$model = "2020-10-09";
        expect(wrapper.vm.$v.durationForm.endDate.$error).toBe(true);
    });

    test('Invalid Start Date (Before Now)', () => {
        wrapper.vm.$v.durationForm.startDate.$model = "2019-06-06";
        expect(wrapper.vm.$v.durationForm.startDate.$error).toBe(true);
    });

    test('Start Date and End date the same and start time before end time (Invalid)', () => {
        wrapper.vm.$v.durationForm.startDate.$model = "2020-06-06";
        wrapper.vm.$v.durationForm.endDate.$model = "2020-06-06";
        wrapper.vm.$v.durationForm.startTime.$model = "09:00";
        wrapper.vm.$v.durationForm.endTime.$model = "08:00";
        expect(wrapper.vm.$v.durationForm.endTime.$error).toBe(true);
    });

    test('Invalid Activity Type', () => {
        wrapper.vm.$v.form.selectedActivityType.$model = "Chill";
        expect(wrapper.vm.$v.form.selectedActivityType.$error).toBe(true);
    });
})

