<template>
  <div class="card">
    <h5 class="card-header">{{ name }}</h5>
    <div class="card-body">
      <div v-if="value">
        <h2 class="card-title d-inline mr-1">{{ value }}</h2>
        {{ unit }}
      </div>

      <div class="text-center w-100">
        <v-slider
          :min="500"
          :max="5000"
          v-model="refreshInterval"
          class="w-100"
        ></v-slider>
        <div v-if="error">Access failed.</div>

        <div class="mt-3">
          Refresh Interval:
          <b-form-input
            v-model="refreshInterval"
            type="number"
            required
          ></b-form-input>
        </div>
      </div>

      <div v-if="!value && !error">Value not yet loaded.</div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ValueCard",
  data() {
    return {
      value: null,
      error: false,
      refreshInterval: 1000,
    };
  },
  props: {
    endpoint: String,
    name: String,
    unit: String,
    dataKey: String,
    roundNumber: Number
  },
  async mounted() {
    await this.fetch();
    await this.scheduleFetch();
  },
  methods: {
    async fetch() {
      let response = null;

      try {
        response = await this.$http.get(this.endpoint);

        if (this.roundNumber != null) {
            this.value = response.data[this.dataKey].toFixed(this.roundNumber);
        } else {
            this.value = response.data[this.dataKey];
        }

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
};
</script>

<style scoped>
</style>
