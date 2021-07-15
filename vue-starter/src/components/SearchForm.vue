<template>
  <form
    name="searchFrm"
    id="searchFrm"
    class="border mb-3">

    <div class="d-flex justify-content-center form-dic" >
      <h6 class=" m-2">필터조건</h6>
      <div class="resolveStatus m-2">
        <span class="status-title">해결상태 : </span>
        <div class="radio-div">
          <input type="radio" name="filter" value="all" checked="checked">All
          <input type="radio" name="filter" value="open">Open
          <input type="radio" name="filter" value="closed">Closed
        </div>

      </div>

      <div class="labelFilter m-2">
        <span class="status-title">라벨 필터링 : </span>
        <select name="label" id="label">
          <option value="">선택하세요</option>
          <option :key="i" :value="label" v-for="(label, i) in labelList ">{{ label }}</option>
        </select>
      </div>



      <div class="termFilter m-2">
        <span class="status-title">기간 : </span>
        <input type="date" name="startDate" value="2021-07-08" min="2021-06-08" max="2021-07-08">
        <input type="date" name="endDate" value="2021-07-08" min="2021-06-08" max="2021-07-08">
      </div>
      <button type="submit" class="btn btn-outline-success" style="height: 65%"> 검색</button>
    </div>
  </form>

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
