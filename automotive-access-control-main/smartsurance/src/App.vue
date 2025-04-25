<template>
  <div id="app">
    <div id="navbar">
      <b-navbar toggleable="lg" type="dark" variant="info" class="col-12">
        <b-navbar-brand href="#">smartsurance</b-navbar-brand>

        <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>

        <b-collapse id="nav-collapse" is-nav>
          <b-navbar-nav> </b-navbar-nav>

          <b-navbar-nav class="ml-auto"> </b-navbar-nav>
        </b-collapse>
      </b-navbar>
    </div>

    <div class="container-fluid">
      <div id="gps" class="row mb-4">
        <GPSMap class="w-100" endpoint="/vehicle/location" />
      </div>
    </div>

    <div class="container-fluid mt-4 mb-2">
      <div id="values" class="row">
        <div id="values" class="card-columns col-12">
          <ValueCard
            name="Vehicle Speed"
            unit="km/h"
            endpoint="/vehicle/vehicleSpeed"
            dataKey="vehicle_speed"
          />
          <ValueCard
            name="Engine Speed"
            unit="rpm"
            endpoint="/vehicle/engineSpeed"
            dataKey="engine_speed"
          />
          <ValueCard
            name="Throttle Position"
            unit="%"
            endpoint="/vehicle/throttlePosition"
            dataKey="throttle_position"
            :roundNumber="2"
          />
        </div>
      </div>
    </div>

    <b-modal ref="resource-manager" hide-footer title="Resource Management">
      <ResourceManager/>
      <b-button class="mt-3" variant="danger" block @click="hideModal"
        >Close</b-button
      >
    </b-modal>

    <div class="container-fluid mb-4">
      <div class="row">
        <div class="col-12 d-flex justify-content-center">
          <b-button id="show-btn" variant="primary" @click="showModal">Open Resource Manager</b-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import GPSMap from "./components/GPSMap.vue";
import ResourceManager from "./components/ResourceManager.vue";
import ValueCard from "./components/ValueCard.vue";

export default {
  name: "App",
  components: {
    GPSMap,
    ResourceManager,
    ValueCard,
  },
  data() {
    return {};
  },
  methods: {
    showModal() {
      this.$refs["resource-manager"].show();
    },
    hideModal() {
      this.$refs["resource-manager"].hide();
    },
  },
};
</script>

<style scoped>
</style>
