<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Unicode reference character changer</title>
  </head>

  <body>
    <form action="convert" method="post">
    <table style="width: 100%">
      <tr>
        <td colspan="2">convert to Unicode reference characters. <input type="submit" value="convert"/></td>
      </tr>
      <tr>
      	<% if (request.getAttribute("src1") == null) request.setAttribute("src1", ""); %>
      	<td style="width: 50%"><textarea name="src1" style="width: 100%" rows="5"><%= request.getAttribute("src1") %></textarea></td>
      	<% if (request.getAttribute("cvt1") == null) request.setAttribute("cvt1", ""); %>
      	<td style="width: 50%"><textarea name="cvt1" style="width: 100%" rows="5" readonly="readonly"><%= request.getAttribute("cvt1") %></textarea></td>
      </tr>
      <tr>
        <td colspan="2">unescape Unicode reference characters. <input type="submit" value="convert"/></td>
      </tr>
      <tr>
      	<% if (request.getAttribute("src2") == null) request.setAttribute("src2", ""); %>
        <td style="width: 50%"><textarea name="src2" style="width: 100%" rows="5"><%= request.getAttribute("src2") %></textarea></td>
      	<% if (request.getAttribute("cvt2") == null) request.setAttribute("cvt2", ""); %>
        <td style="width: 50%"><textarea name="cvt2" style="width: 100%" rows="5" readonly="readonly"><%= request.getAttribute("cvt2") %></textarea></td>
      </tr>
    </table>
    </form>
<script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-10552398-4']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>
  </body>
</html>
