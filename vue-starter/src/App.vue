<template>
  <div class="container">
    <div class="text-center">
      <router-view class="view" name="header"></router-view>
      <router-view class="view chartWrap" name="userChart"></router-view>
      <router-view class="view chartWrap" name="assigneeChart"></router-view>
      <router-view class="view chartWrap" name="dayChart"></router-view>
    </div>
    <router-view class="view" name="issuesList"></router-view>
  </div>
</template>


<script>
</script>

<style>
  #table-container{
    font-size: 13px;
  }
  .chartWrap{
    display: inline-block;
    width: 300px;
    height: 400px;
  }
  .count{
    width: 500px;
    border: 3px solid gray;
    display: inline-block;
  }
  #title{
    width: 1000px;
    border: 2px solid gray;
    display: inline-block;
  }
  .form-dic{
    height: 55px;
  }
  .radio-div{
    display: inline-block;
  }
  .radio-div input{
    margin-left: 1rem;
  }
</style>
