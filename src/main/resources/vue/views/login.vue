<template id="login">
  <div class="login-container">
    <h1>Login</h1>
    <form @submit.prevent="login">
      <button @click="loginWithGoogle">Login with Google</button>
    </form>
  </div>
</template>

<script>
app.component("login", {
  template: "#login",
  methods: {
    loginWithGoogle() {
      // Redirect the user to the Google login endpoint
      window.location.href = "/api/auth/login/google";
    },
  },
});
</script>

<style>
.login-container {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.form-group {
  margin-bottom: 10px;
}

label {
  display: block;
}

input[type="email"],
input[type="password"] {
  width: 100%;
  padding: 5px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

button {
  margin-top: 10px;
}
</style>
