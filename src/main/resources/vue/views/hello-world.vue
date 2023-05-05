<template id="hello-world">
  <h1 class="hello-world">Hello, World!</h1>
  <span v-if="$javalin.state.currentUser">Current user: '{{$javalin.state.currentUser}}'</span>
</template>

<script>
app.component("hello-world", {template: "#hello-world"});
</script>

<style>
.hello-world {
  color: goldenrod;
}
</style>