<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<META name="keywords" content="java,native2ascii,eclipse,multibyte,plugin,multibyte,property,editor,Code,conversion,application,unicode,properties,editor,setting,file,hover,コード変換,アプリケーション,ユニコード,プロパティ,エディタ,設定,ファイル,ホバー">
    <title>Unicode reference character changer</title>
    <script type="text/javascript" src="js/jquery-1.6.1.min.js"></script>
    <script type="text/javascript">
	<!--
	$(function() {
		$('.clearBtn1').click(function() {
			$('#form1').find("input[name='c']").val(1);
			$('#form1').submit();
		});
		$('.clearBtn2').click(function() {
			$('#form1').find("input[name='c']").val(2);
			$('#form1').submit();
		});
		$('.historySelect1').change(function() {
			$('#src1').val($(this).val());
		});
		$('.historySelect2').change(function() {
			$('#src2').val($(this).val());
		});
		$('.convertBtn1').click(function() {
			$('#form1').find("input[name='co']").val(1);
			$('#form1').submit();
		});
		$('.convertBtn2').click(function() {
			$('#form1').find("input[name='co']").val(2);
			$('#form1').submit();
		});
	});
	// -->
    </script>
    <style type="text/css">
    .title {
    	font-size: 1.4em;
    	font-weight: bold;
    }
    th {
    	background-color: #55F;
    	color: #FFF;
    	text-align: center;
    	padding: 3px;
    }
    td.space {
    	height: 20px;
    	border-bottom: 1px solid #999;
    }
    input.convertBtn1,input.convertBtn2 {
    	width: 100px;
    	font-size: 1.3em;
    	font-weight: bold;
    }
    select.historySelect1,select.historySelect2 {
    	width: 300px;
    }
    td.convertBtnArea {
    	text-align: center;
    }
    .subtitle {
    	padding: 10px 0px 5px 0px;
    	font-weight: bold;
    }
    </style>
  </head>

  <body>
  	<p class="title">Unicode converter</p>
    <form id="form1" name="form1" action="index" method="post">
    <input type="hidden" name="c" />
    <input type="hidden" name="co" />
    <table style="width: 100%">
      <tr>
        <td colspan="2" class="subtitle">Convert to unicode reference characters.</td>
      </tr>
      <tr>
      	<th>Plain text</th><th>Text converted into unicode reference characters</th>
      </tr>
      <tr>
      	<td style="width: 50%"><textarea id="src1" name="src1" style="width: 100%" rows="15">${f:h(src1)}</textarea></td>
      	<td style="width: 50%"><textarea name="cvt1" style="width: 100%" rows="15" readonly="readonly">${f:h(cvt1)}</textarea></td>
      </tr>
      <tr>
      	<td>History:
      		<select class="historySelect1">
      			<option value=""></option>
      			<c:forEach var="option" items="${options1}">
      			<option value="${f:h(option)}">${f:h(option)}</option>
      			</c:forEach>
      		</select>
      		<input type="button" value="Clear history" class="clearBtn1" />
      	</td>
      	<td></td>
      </tr>
      <tr><td colspan="2" class="convertBtnArea"><input type="button" value="Convert" class="convertBtn1" /></td></tr>
      <tr><td colspan="2" class="space"></td></tr>
      <tr>
        <td colspan="2" class="subtitle">Unescape unicode reference characters.</td>
      </tr>
      <tr>
      	<th>Text converted into unicode reference characters</th><th>Plain text</th>
      </tr>
      <tr>
      	<% if (request.getAttribute("src2") == null) request.setAttribute("src2", ""); %>
        <td style="width: 50%"><textarea id="src2" name="src2" style="width: 100%" rows="15"><%= request.getAttribute("src2") %></textarea></td>
      	<% if (request.getAttribute("cvt2") == null) request.setAttribute("cvt2", ""); %>
        <td style="width: 50%"><textarea name="cvt2" style="width: 100%" rows="15" readonly="readonly"><%= request.getAttribute("cvt2") %></textarea></td>
      </tr>
      <tr>
      	<td>History:
      		<select class="historySelect2">
      			<option value=""></option>
      			<c:forEach var="option" items="${options2}">
      			<option value="${f:h(option)}">${f:h(option)}</option>
      			</c:forEach>
      		</select>
      		<input type="button" value="Clear history" class="clearBtn2" />
      	</td>
      	<td></td>
      </tr>
      <tr><td colspan="2" class="convertBtnArea"><input type="button" value="Convert" class="convertBtn2" /></td></tr>
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
