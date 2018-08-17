<html>
<head>
  <script type="application/javascript">
    hash = window.location.hash;
    window.location.hash = "";
    window.location = "/oauth_redirect?" + hash.substring(1);
  </script>
</head>
<body>
</body>
</html>
