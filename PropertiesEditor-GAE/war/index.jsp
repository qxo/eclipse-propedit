<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<html>
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
    <style type="text/css">
    .title {
    	font-size: 1.3em;
    	font-weight: bold;
    }
    th {
    	background-color: #55F;
    	color: #FFF;
    	text-align: center;
    	padding: 3px;
    }
    </style>
  </head>

  <body>
  	<p class="title">Unicode converter</p>
    <form name="form1" action="index" method="post">
    <table style="width: 100%">
      <tr>
        <td colspan="2">convert to Unicode reference characters. <input type="submit" value="convert"/></td>
      </tr>
      <tr>
      	<th>plain text</th><th>text converted into Unicode reference characters</th>
      </tr>
      <tr>
      	<td style="width: 50%"><textarea id="src1" name="src1" style="width: 100%" rows="15">${f:h(src1)}</textarea></td>
      	<td style="width: 50%"><textarea name="cvt1" style="width: 100%" rows="15" readonly="readonly">${f:h(cvt1)}</textarea></td>
      </tr>
      <tr>
      	<td>history:
      		<select onchange="historyChange('src1', this)" style="width: 300px;">
      			<option value=""></option>
      			<c:forEach var="option" items="${options1}">
      			<option value="${f:h(option)}">${f:h(option)}</option>
      			</c:forEach>
      		</select>
      	</td>
      	<td></td>
      </tr>
      <tr>
        <td colspan="2">unescape Unicode reference characters. <input type="submit" value="convert"/></td>
      </tr>
      <tr>
      	<th>text converted into Unicode reference characters</th><th>plain text</th>
      </tr>
      <tr>
      	<% if (request.getAttribute("src2") == null) request.setAttribute("src2", ""); %>
        <td style="width: 50%"><textarea id="src2" name="src2" style="width: 100%" rows="15"><%= request.getAttribute("src2") %></textarea></td>
      	<% if (request.getAttribute("cvt2") == null) request.setAttribute("cvt2", ""); %>
        <td style="width: 50%"><textarea name="cvt2" style="width: 100%" rows="15" readonly="readonly"><%= request.getAttribute("cvt2") %></textarea></td>
      </tr>
      <tr>
      	<td>history:
      		<select onchange="historyChange('src2', this)" style="width: 300px;">
      			<option value=""></option>
      			<c:forEach var="option" items="${options2}">
      			<option value="${f:h(option)}">${f:h(option)}</option>
      			</c:forEach>
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
