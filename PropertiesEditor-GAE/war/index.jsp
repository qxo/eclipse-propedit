<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<%@page import="java.util.List"%><html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<META name="keywords" content="java,native2ascii,eclipse,multibyte,plugin,multibyte,property,editor,Code,conversion,application,unicode,properties,editor,setting,file,hover,コード変換,アプリケーション,ユニコード,プロパティ,エディタ,設定,ファイル,ホバー">
    <title>Unicode reference character changer</title>
    <script type="text/javascript">
	<!--
	function historyChange(textareaid, selectobject) {
		var t = document.getElementById(textareaid);
		t.value = selectobject.value;
	}
	// -->
    </script>
  </head>

  <body>
    <form name="form1" action="convert.do" method="post">
    <table style="width: 100%">
      <tr>
        <td colspan="2">convert to Unicode reference characters. <input type="submit" value="convert"/></td>
      </tr>
      <tr>
      	<% if (request.getAttribute("src1") == null) request.setAttribute("src1", ""); %>
      	<td style="width: 50%"><textarea id="src1" name="src1" style="width: 100%" rows="5"><%= request.getAttribute("src1") %></textarea></td>
      	<% if (request.getAttribute("cvt1") == null) request.setAttribute("cvt1", ""); %>
      	<td style="width: 50%"><textarea name="cvt1" style="width: 100%" rows="5" readonly="readonly"><%= request.getAttribute("cvt1") %></textarea></td>
      </tr>
      <tr>
      	<td>
      		<select onchange="historyChange('src1', this)">
      			<option value=""></option>
      			<%
      			List<String> options1 = (List<String>)request.getAttribute("options1");
      			for (String option : options1) {
      			%>
      			<option value="<%= option %>"><%= option %></option>
      			<% } %>
      		</select>
      	</td>
      	<td></td>
      </tr>
      <tr>
        <td colspan="2">unescape Unicode reference characters. <input type="submit" value="convert"/></td>
      </tr>
      <tr>
      	<% if (request.getAttribute("src2") == null) request.setAttribute("src2", ""); %>
        <td style="width: 50%"><textarea id="src2" name="src2" style="width: 100%" rows="5"><%= request.getAttribute("src2") %></textarea></td>
      	<% if (request.getAttribute("cvt2") == null) request.setAttribute("cvt2", ""); %>
        <td style="width: 50%"><textarea name="cvt2" style="width: 100%" rows="5" readonly="readonly"><%= request.getAttribute("cvt2") %></textarea></td>
      </tr>
      <tr>
      	<td>
      		<select onchange="historyChange('src2', this)">
      			<option value=""></option>
      			<%
      			List<String> options2 = (List<String>)request.getAttribute("options2");
      			for (String option : options2) {
      			%>
      			<option value="<%= option %>"><%= option %></option>
      			<% } %>
      		</select>
      	</td>
      	<td></td>
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
<!-- Start of Woopra Code -->
<script type="text/javascript">
var woo_settings = {idle_timeout:'60000', domain:'propedit.appspot.com'};
(function(){
var wsc = document.createElement('script');
wsc.src = document.location.protocol+'//static.woopra.com/js/woopra.js';
wsc.type = 'text/javascript';
wsc.async = true;
var ssc = document.getElementsByTagName('script')[0];
ssc.parentNode.insertBefore(wsc, ssc);
})();
</script>
<!-- End of Woopra Code -->
  </body>
</html>
