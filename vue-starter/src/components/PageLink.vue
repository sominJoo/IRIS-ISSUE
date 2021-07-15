<template>
  <div id="pagination" class="text-center mt-3">
    <div class="pagination-div overflow-auto">
      <b-pagination-nav :link-gen="linkGen" :number-of-pages="rowNum" use-router></b-pagination-nav>
    </div>
  </div>
</template>

<script>
export default {
  name: 'IssueTable',
  props: {
    msg: String
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style>
</style>
