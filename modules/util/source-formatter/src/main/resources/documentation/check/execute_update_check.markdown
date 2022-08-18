## ExecuteUpdateCheck

For better performance, Use `addBatch()` and `executeBatch()`, instead of
`executeUpdate()` in a loop.

### Example

Incorrect:

```
@Override
protected void doUpgrade() throws Exception {
  try (PreparedStatement preparedStatement1 = connection.prepareStatement(
        "select id_, content from JournalArticle");
     ResultSet resultSet = preparedStatement1.executeQuery();
     PreparedStatement preparedStatement2 =
        AutoBatchPreparedStatementUtil.concurrentAutoBatch(
           connection,
           "update JournalArticle set content = ? where id_ = ?")) {

     while (resultSet.next()) {
        preparedStatement2.setString(
           1,
           _journalContentCompatibilityConverter.convert(
              resultSet.getString("content")));
        preparedStatement2.setLong(2, resultSet.getLong("id_"));

        preparedStatement2.executeUpdate();
     }
  }
}
```

Correct:

```
@Override
protected void doUpgrade() throws Exception {
  try (PreparedStatement preparedStatement1 = connection.prepareStatement(
        "select id_, content from JournalArticle");
     ResultSet resultSet = preparedStatement1.executeQuery();
     PreparedStatement preparedStatement2 =
        AutoBatchPreparedStatementUtil.concurrentAutoBatch(
           connection,
           "update JournalArticle set content = ? where id_ = ?")) {

     while (resultSet.next()) {
        preparedStatement2.setString(
           1,
           _journalContentCompatibilityConverter.convert(
              resultSet.getString("content")));
        preparedStatement2.setLong(2, resultSet.getLong("id_"));

        preparedStatement2.addBatch();
     }

     preparedStatement2.executeBatch();
  }
}
```