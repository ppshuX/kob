<template>
    <ContentField>
     我的bot
    </ContentField>
 </template>
 
 <script>
 import ContentField from '../../../components/ContentField.vue'
 import $ from 'jquery'
 import { useStore } from 'vuex';
 
 export default {
     components: {
         ContentField
     },
     setup() {
        const store = useStore();
        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/add/",
        //     type: "POST",
        //     data: {
        //         title: "Bot的标题",
        //         description: "Bot的描述",
        //         content: "Bot的代码",
        //     },
        //     headers: {
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp) {
        //         console.log(resp);
        //     },
        //     error(resp) {
        //         console.log(resp);
        //     }
        // })

        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/remove/",
        //     type: "POST",
        //     data: {
        //         bot_id: 3,
        //     },
        //     headers: {
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp) {
        //         console.log(resp);
        //     },
        //     error(resp) {
        //         console.log(resp);
        //     }
        // })

        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/update/",
        //     type: "POST",
        //     data: {
        //         bot_id: 4,
        //         title: "Bot的标题4",
        //         description: "Bot的描述4",
        //         content: "Bot的代码4",
        //     },
        //     headers: {
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp) {
        //         console.log(resp);
        //     },
        //     error(resp) {
        //         console.log(resp);
        //     }
        // })

        $.ajax({
            url: "http://127.0.0.1:3000/user/bot/getlist/",
            type: "GET",
            headers: {
                Authorization: "Bearer " + store.state.user.token,
            },
            success(resp) {
                console.log(resp);
            },
            error(resp) {
                console.log(resp);
            }
        })
     }
 }
 </script>
 
 <style scoped>
 
 </style>