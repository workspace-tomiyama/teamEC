<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>

<html lang="ja">
<link rel="stylesheet" type="text/css" href="./css/latte.css">
<link rel="stylesheet" type="text/css" href="./css/table.css">
<link rel="stylesheet" type="text/css" href="./css/input.css">
<link rel="stylesheet" type="text/css" href="./css/message.css">
<head>
	<meta charset="UTF-8">

	<title>カート</title>

	<!-- チェックボックスの状態を取得し、削除ボタンの活性化／非活性化を切り替える -->
	<script type="text/javascript">
		function checkValue(){
			var checkList = document.getElementsByClassName("checkList");
			var checkFlg = 0;
			for (var i = 0;  i<checkList.length;  i++) {
				if(checkList[i].checked) {
					checkFlg = 1;
					break;
				}
			}
			if (checkFlg == 1) {
		    	document.getElementById('delete_btn').disabled="";
			} else {
				document.getElementById('delete_btn').disabled="true";
			}
		}
	</script>

</head>

<body>

<div id = "header">
	<jsp:include page="header.jsp"/>
</div>

<div id = "contents">
<h1>カート画面</h1>

	<s:if test="cartInfoDTO.size() == 0">
		<h3 class ="error">カート情報がありません。</h3>
	</s:if>
	<s:else><s:form action = "DeleteCartAction">
		<table class="r-table">
			<tr>
			<th>#</th>
			<th>商品名</th>
			<th>商品名ふりがな</th>
			<th>商品画像</th>
			<th>値段</th>
			<th>発売会社名</th>
			<th>発売年月日</th>
			<th>購入個数</th>
			<th>合計金額</th>
			</tr>
		<s:iterator value="cartInfoDTO">
			<tr>
			<td><input type="checkbox" name="checkList" class="checkList" value='<s:property value="productId" />' onchange="checkValue()" ></td>
			<td><s:property value="productName"/></td>
			<td><s:property value="productNameKana"/></td>
			<td><img src='<s:property value="imageFilePath"/>/<s:property value="imageFileName"/>' width="50px" height="50px"/></td>
			<td><s:property value="price"/><span>円</span></td>
			<td><s:property value="releaseCompany"/></td>
			<td><s:property value="releaseDate"/></td>
			<td><s:property value="productCount"/><span>個</span></td>
			<td><s:property value="subtotal"/><span>円</span></td>
			</tr>
		</s:iterator>
		</table>

		<h2><s:label value="カート合計金額："/><s:property value="totalPrice"/>円</h2><br>
		<div class = "delete_btn_box">
		<div class = "delete_btn">
			<s:submit value="削除" id="delete_btn" class ="submit_btn" disabled = "true"/>
		</div>
		</div>
		</s:form>


		<s:if test="#session.logined == 1">
		<div class = "settlement_btn_box">
			<s:form action="SettlementConfirmAction">
			<div class = "settlement_btn">
				<s:submit value="決済" class="submit_btn"/>
			</div>
			</s:form>
		</div>
		</s:if>
		<s:else>
		<div class = "settlement_btn_box">
			<s:form action="GoLoginAction">
			<div class = "settlement_btn">
				<s:submit value="決済" class="submit_btn" />
				<s:hidden name="cartFlg" value= "1"/>
			</div>
			</s:form>
		</div>
		</s:else>

	</s:else>

</div>

</body>
</html>