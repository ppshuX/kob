<template>
  <NavBar />
  <router-view></router-view>
</template>

<script>
import NavBar from './components/NavBar.vue'
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap" 
import $ from 'jquery'

export default {
 components: {
  NavBar,
 },
 setup() {
  $.ajax({
    url: "http://127.0.0.1:3000/user/account/token/",
    type: "POST",
    data: {
      username: "ppshu",
      password: "1234567",
    },
    success(resp) {
      console.log(resp);
    },
    error(resp) {
      console.log(resp);
    }
  });

  $.ajax({
    url: "http://127.0.0.1:3000/user/account/info/",
    type: "GET",
    headers: {
      Authorization: "Bearer  " + "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjOTU5NGQwM2NiYWE0MTY1ODVjNWZkNDkwZjc4MTA2YiIsInN1YiI6IjEiLCJpc3MiOiJzZyIsImlhdCI6MTc0MjU2MTM4OSwiZXhwIjoxNzQzNzcwOTg5fQ.GCByDxjCvRuH_Cpm4VlDY2dU3BpzP7xLzbDPkKIcDso"
    },
    success(resp) {
      console.log(resp);
    },
    error(resp) {
      console.log(resp);
    }
  });

  $.ajax({
    url: "http://127.0.0.1:3000/user/account/register/",
    type: "POST",
    data: {
      username: "ppshu2",
      password: "12345",
      confirmedPassword: "12345",
    },
    success(resp) {
      console.log(resp);
    },
    error(resp) {
      console.log(resp);
    },
  })
 }
}
</script>

<style>
body {
  background-image: url("@/assets/images/background.png");
  background-size: cover;
}
</style>
