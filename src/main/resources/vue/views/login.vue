<template id="login">
  <h1>hey</h1>
  <div class="login">
    <h1>Login Page</h1>
    <button @click="loginWithGoogle">Login with Google</button>
  </div>
</template>

<script>
app.component("login", {
  template: "#login",
  methods: {
    loginWithGoogle() {
      fetch('/api/googleLogin')
          .then(response => {
            if (!response.ok) {
              throw new Error('Network response was not ok');
            }
            // handle successful response
          })
          .catch(error => {
            console.error('There was a problem with the network request:', error);
            // handle error
          });
    }
  }
});
</script>