<template>
  <table class="table table-bordered table-striped mb-0 table-light " id="issue-table">
    <thead>
    <tr>
      <th scope="col">No</th>
      <th scope="col">제목</th>
      <th scope="col">등록자</th>
      <th scope="col">등록일</th>
      <th scope="col">해결일</th>
      <th scope="col">상태</th>
      <th scope="col">라벨</th>
    </tr>
    </thead>
    <tbody>
    <tr v-for="(issue, index) in issueList" :key="index">
      <td>{{ issue.no }}</td>
      <td>{{ issue.title }}</td>
      <td>{{ issue.user }}</td>
      <td>{{ issue.created }}</td>
      <td>{{ issue.cloased }}</td>
      <td>{{ issue.state }}</td>
      <td>
        <span v-for="(label, i) in issue.label" :key="i">{{ label }} <span> | </span> </span>
      </td>
    </tr>
    </tbody>
  </table>
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
