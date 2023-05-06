<template id="index">
  <div>
    <h1 class="hello-world">Hello, {{ user.name }}!</h1>
    <ul>
      <li v-for="item in inventory" :key="item.id">
        {{ item.name }} - {{ item.description }}
      </li>
    </ul>
  </div>
</template>

<script>
app.component("index", {
  template: "#hello-world",
  data() {
    return {
      user: {
        name: "",
      },
      inventory: [],
    };
  },
  mounted() {
    // Fetch the user's inventory
    this.fetchInventory();
  },
  methods: {
    fetchInventory() {
      // Call the UserInventoryService to get the user's inventory
      UserInventoryService.getInventory(this.userId)
          .then((response) => {
            this.inventory = response.data;
          })
          .catch((error) => {
            console.error("Error fetching user inventory:", error);
          });
    },
  },
});
</script>

<style>
.hello-world {
  color: goldenrod;
}
</style>
