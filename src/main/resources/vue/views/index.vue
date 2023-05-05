<template id="hello-world">
  <h1 class="hello-world">Hello, person person in person!</h1>
</template>

<script>
app.component("hello-world", {template: "#hello-world"});
</script>
<style>
.hello-world {
  color: goldenrod;
}
</style>