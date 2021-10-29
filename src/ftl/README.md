> :memo: **​If u decided to DIY ftl put them ALL(bundle at least  first 3 ftl) under same dir then choose this dir**
>
> ---
>
> if [mybatisPlusProperties.ftl] is missing, sbg-mpg.properties wont be created. sbg-mpg.properties is a datasource settings file for used-once-for-codegen-then-delete

### application.ftl

```java
${packageId} | package ${packageId};

${ApplicationClassName} | @SpringBootApplication public class ${ApplicationClassName} {}
```

### applicationTest.ftl

```java
${packageId} | package ${packageId};

${TestClassName} | @SpringBootTest class ${TestClassName} {}
```

### mybatisPlus.ftl

```java
${packageId} | package ${packageId}; | packageConfig.setParent("${packageId}");

${outputDir} | globalConfig.setOutputDir(outputDir);

${dataSourcePrefix} | related to datasource settings in settings file
```

### mybatisPlusProperties.ftl

```properties
${dataSourcePrefix} | ${dataSourcePrefix}password=root
```

### settingsFileProperties.ftl & settingsFileYml.ftl

```properties
${dataSourcePrefix} | ${dataSourcePrefix}driver-class-name=com.mysql.cj.jdbc.Driver
```

### pomXml.ftl

```java
${p_artifactId} | <parent><artifactId>${p_artifactId}</artifactId></parent>

${p_groupId} | <parent><groupId>${p_groupId}</groupId></parent>

${p_version} | <parent><version>${p_version}</version></parent>

${artifactId} | <artifactId>${artifactId}</artifactId>

${groupId} | <#if p_groupId != groupId><groupId>${groupId}</groupId></#if>

${version} | <version>${version}</version>
```
