<template>
  <div>
    <div id="gpsWidget" class="mb-2">
      <div id="gpsMap" v-if="position">
        <l-map class="gps" :zoom="zoom" :center="position">
          <l-tile-layer :url="url" :attribution="attribution"></l-tile-layer>
          <l-marker :lat-lng="position" :icon="gpsIcon"></l-marker>
        </l-map>
      </div>

      <div
        id="gpsError"
        class="d-flex justify-content-center align-items-center"
        v-if="position && error"
      >
        <div class="gps-error-text text-white text-center">
          <h1>ACCESS FAILED</h1>
        </div>
      </div>

      <div
        id="gpsNoPosition"
        class="d-flex justify-content-center align-items-center"
        v-if="!position"
      >
        <div class="gps-error-text text-white text-center">
          <h1>NO POSITION RECEIVED YET</h1>
        </div>
      </div>
    </div>
    <div
      id="gpsSettings"
      class="d-flex justify-content-center align-items-center"
    >
      <div class="text-center col-6">
        <v-slider
          :min="500"
          :max="5000"
          v-model="refreshInterval"
          class="w-100"
        ></v-slider>

        GPS Refresh Interval:
        <b-form-input
          v-model="refreshInterval"
          type="number"
          required
        ></b-form-input>
      </div>
    </div>
  </div>
</template>

<script>
import L from "leaflet";
import GPSIcon from "@/assets/gps.png";

export default {
  name: "GPSMap",
  data() {
    return {
      url: "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
      attribution:
        '&copy; <a target="_blank" href="http://osm.org/copyright">OpenStreetMap</a> contributors',
      zoom: 18,
      error: false,
      position: null,
      refreshHandle: null,
      refreshInterval: 1000,
      gpsIcon: L.icon({
        iconUrl: GPSIcon,
        iconSize: [32, 32],
        iconAnchor: [16, 16],
      }),
    };
  },
  props: {
    endpoint: String,
  },
  methods: {
    async fetch() {
      let response = null;

      try {
        response = await this.$http.get(this.endpoint);
        this.position = [response.data.latitude, response.data.longitude];
        this.error = false;
      } catch (error) {
        this.error = true;
      }
    },
    async scheduleFetch() {
      this.refreshHandle = setTimeout(
        async () => {
          await this.fetch();
          await this.scheduleFetch();
        },
        this.refreshInterval ? this.refreshInterval : 1000
      );
    },
  },
  async mounted() {
    await this.fetch();
    await this.scheduleFetch();
  },
  watch: {},
};
</script>

<style scoped>
.gps {
  height: 250px;
  width: 100%;
}

#gpsWidget {
  position: relative;
}

#gpsError {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 9999;

  background-color: rgba(250, 0, 0, 0.5);

  width: 100%;
  height: 100%;
}

#gpsNoPosition {
  background-color: rgba(250, 0, 0, 0.5);
  height: 250px !important;

  width: 100%;
  height: 100%;
}

.gps-error-text {
  text-shadow: 1px 2px #000000;
}
</style>
