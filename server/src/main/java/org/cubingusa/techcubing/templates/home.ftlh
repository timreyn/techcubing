<html>
<head>
  <title>Welcome!</title>
</head>
<body>
  <#if hasCompetitionId >
    <h1>${competitionId}</h1>
  <#else>
    <h1>TechCubing</h1>
    <p>
      No competition is selected.
    </p>
  </#if>
  <h3>Links</h3>
  <ul>
    <li>
      <a href="/competitions">Select a competition</a>
    </li>
    <#if hasCompetitionId >
      <li>
        <a href="/set_competition?id=${competitionId}">Refresh competition data</a>
      </li>
      <li>
        <a href="/manage_devices">Manage devices</a>
      </li>
    </#if >
  </ul>
</body>
</html>
