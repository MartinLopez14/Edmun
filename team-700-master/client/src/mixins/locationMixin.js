import axios from 'axios'

export default {
    data: function() {
        return {
            timeout: null,
            locations: [],

        }
    },
    methods: {
        getLocationData: async function (locationText) {

            clearTimeout(this.timeout);
            let _this = this;
            this.timeout = setTimeout( async function () {
                if (locationText == ''){
                    _this.locations = [];
                    return
                }
                console.log(locationText)

                let locationData = axios.create({
                    baseURL: 'https://nominatim.openstreetmap.org/search?q=' + locationText + '&format=json&limit=8&addressdetails=1',
                    timeout: 2000,
                    withCredentials: false,
                });
                let data = await (locationData.get());
                console.log(data);
                console.log("here");
                let fixedData = JSON.parse('{"data":[]}');
                for (var i = 0; i < data.data.length; i++) {
                    var obj = data.data[i];
                    if (!((obj.address.city || obj.address.county) && obj.address.country)) {
                        console.log("deleted json index " + i);
                    } else {
                        var state = null;
                        if (obj.address.state) {
                            state = obj.address.state;
                        }
                        let display_name;
                        if (obj.address.city) {
                            if (state) {
                                display_name = obj.address.city.toString() + ", " + state + ", " + obj.address.country.toString();
                            } else {
                                display_name = obj.address.city.toString() + ", " + obj.address.country.toString();
                            }
                            fixedData['data'].push({"address":
                                    {"city":obj.address.city.toString(),"state":state, "country":obj.address.country.toString()},
                                "display_name":display_name, "place_id":obj.place_id});
                        } else if (obj.address.county) {
                            if (state) {
                                display_name = obj.address.county.toString() + ", " + state + ", " + obj.address.country.toString();
                            } else {
                                display_name = obj.address.county.toString() + ", " + obj.address.country.toString();
                            }
                            fixedData['data'].push({"address":
                                    {"city":obj.address.county.toString(),"state":state, "country":obj.address.country.toString()},
                                "display_name":display_name, "place_id":obj.place_id});
                        }

                    }
                }
                if (fixedData.data.length > 0) {
                    console.log("fixedData " + JSON.stringify(fixedData));
                    console.log(fixedData.data[0].display_name.toString());
                    _this.locations = fixedData.data;
                }

            }, 1000);
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
    }
};