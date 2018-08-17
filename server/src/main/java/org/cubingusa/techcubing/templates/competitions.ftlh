<html>
<head>
  <title>Select a Competition</title>
</head>
<body>
  <h1>
    Select Competition
  </h1>
  <ul>
    <#list competitions as competition>
      <li>
        <a href="/set_competition?id=${competition.id}">
          ${competition.short_name}
        </a>
      </li>
    </#list>
  </ul>
</body>
</html>
