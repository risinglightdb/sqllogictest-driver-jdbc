# sqllogictest-driver-jdbc

JDBC driver for [sqllogictest-rs](https://github.com/risinglightdb/sqllogictest-rs).

sqllogictest-rs is under active development, so only the latest version is supported.

## Build and run

Build and package the driver:

```sh
mvn package
```

Run with sqllogictest-rs:

```sh
EXTERNAL_ENGINE_COMMAND_TEMPLATE="java -cp ./target/sqllogictest-jdbc-runner-1.0-SNAPSHOT-jar-with-dependencies.jar com.risingwave.sqllogictest.App jdbc:postgresql://{host}:{port}/{db} {user}"
sqllogictest -- --engine=external --user=root --db=dev --port=4566 [slt]
```

## Contribution

Unless you explicitly state otherwise, any contribution intentionally submitted for inclusion in the work by you, as defined in the Apache-2.0 license, shall be dual licensed as above, without any additional terms or conditions.

Contributors should add a Signed-off-by line for [Developer Certificate of Origin](https://github.com/probot/dco#how-it-works) in their commits. Use git commit -s to sign off commits.
