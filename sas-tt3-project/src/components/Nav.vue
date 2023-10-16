<script setup>
import { RouterLink } from 'vue-router';
</script>
 
<template>
<div>
<!-- <RouterLink to="/" active-class="active">Question Management</RouterLink> -->
<RouterLink :to="{name:'Data'}" active-class="active">All Data</RouterLink>
<!-- <RouterLink :to="{name:'Detail'}">Detail Data</RouterLink> -->
</div>
</template>
 
<style scoped>
.active{
    color:#0000ee
}
</style>