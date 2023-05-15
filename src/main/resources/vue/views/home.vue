<template id="home">
  <div class="home-container">
    <div class="top-left-pane">
      <div class="profile-pane">Profile 1</div>
      <div class="profile-pane">Profile 2</div>
      <div class="profile-pane">Profile 3</div>
      <div class="profile-pane">Profile 4</div>
    </div>
    <div class="right-pane">
      News
    </div>
    <div class="bottom-pane">
      <div v-if="liveStream" class="live-stream">
        Live Stream
      </div>
      <div v-else class="video-player">
        <iframe :src="currentVideo" width="100%" height="100%" frameborder="0" allowfullscreen></iframe>
        <button @click="nextVideo">Next Video</button>
      </div>
    </div>
  </div>
</template>

<script>
app.component("home", {
  template: "#home",
  data() {
    return {
      profiles: [], // Placeholder for profile data
      isLiveStream: false, // Flag to check if live stream is available
      videos: [], // Placeholder for video data
      currentVideoIndex: 0 // Index of the current video being played
    };
  },
  mounted() {
    // Fetch profiles data from the API and assign it to the 'profiles' array

    // Fetch live stream status from the API and update the 'isLiveStream' flag

    // Fetch video data from the API and assign it to the 'videos' array
  },
  computed: {
    currentVideo() {
      // Return the embed link of the current video being played
      return this.videos[this.currentVideoIndex];
    }
  },
  methods: {
    playNextVideo() {
      // Increment the current video index to move to the next video in the array
      this.currentVideoIndex = (this.currentVideoIndex + 1) % this.videos.length;
    }
  }
});
</script>

<style>
.home-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr auto;
  gap: 10px;
}

.top-left-pane {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 2fr 2fr;
  gap: 10px;
}

.profile-pane {
  border: 2px solid black;
  background-color: lightgray;
}

.profile1 {
  grid-row: span 2;
}

.right-pane {
  border: 2px solid black;
  background-color: lightblue;
}

.bottom-pane {
  border: 2px solid black;
  background-color: lightgreen;
}

.live-stream {
  width: 100%;
  height: 100%;
  background-color: yellow;
}

.video-player {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.video-player iframe {
  width: 100%;
  height: 80%;
}
</style>