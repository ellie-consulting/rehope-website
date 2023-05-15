<template id="view-accounts">
  <h1>Accounts</h1>
  <div>
    <button @click="bulkSelect" v-show="!isBulkSelect">Bulk Select</button>
    <button @click="cancelBulkSelect" v-show="isBulkSelect">Cancel</button>
    <button @click="deleteSelected" v-show="isBulkSelect">Delete Selected</button>
    <button @click="addUser">Add User</button>
    <ul class="user-overview-list">
      <li v-for="user in users" :key="user.id">
        <input type="checkbox" v-model="selectedUsers" :value="user.id" v-show="isBulkSelect">
        <a :href="`/users/${user.id}`">{{user.name}} ({{user.email}})</a>
        <button @click="deleteUser(user.id)" v-show="!isBulkSelect">Delete</button>
      </li>
    </ul>
  </div>
</template>

<script>
app.component("view-accounts", {
  template: "#view-accounts",
  data: () => ({
    users: [],
    isBulkSelect: false,
    selectedUsers: [],
  }),
  created() {
    this.fetchUsers();
  },
  methods: {
    fetchUsers() {
      fetch("/api/user")
          .then(res => res.json())
          .then(res => this.users = res)
          .catch(() => alert("Error while fetching users"));
    },
    bulkSelect() {
      this.isBulkSelect = true;
      this.selectedUsers = [];
    },
    cancelBulkSelect() {
      this.isBulkSelect = false;
      this.selectedUsers = [];
    },
    deleteSelected() {
      // Perform bulk deletion logic here
      if (this.selectedUsers.length > 0) {
        Promise.all(
            this.selectedUsers.map(userId =>
                fetch(`/api/user/${userId}`, { method: "DELETE" })
            )
        )
            .then(() => {
              // Refresh the user list
              this.fetchUsers();
              // Reset bulk select mode
              this.isBulkSelect = false;
              this.selectedUsers = [];
            })
            .catch(() => alert("Error while deleting users"));
      }
    },
    addUser() {
      // Perform add user logic here
      console.log("Adding a new user");
      // Refresh the user list
      this.fetchUsers();
    },
    deleteUser(userId) {
      // Perform delete user logic here
      fetch(`/api/user/${userId}`, { method: "DELETE" })
          .then(() => {
            // Refresh the user list
            this.fetchUsers();
          })
          .catch(() => alert("Error while deleting the user"));
    },
  },
});
</script>

<style>
.user-overview-list {
  list-style-type: none;
  padding: 0;
}

.user-overview-list li {
  margin-bottom: 10px;
}

button {
  margin-right: 10px;
}
</style>
