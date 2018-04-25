
<html>
	<head>
		<body>
		学生列表：<br>
		<table border="1">
			<tr>
				<th>序号</th>
				<th>ID</th>
				<th>姓名</th>
				<th>性别</th>
				<th>年龄</th>
			</tr>
			<#list stuList as stu>
			<#if stu_index%2==0 >
			<tr bgcolor="red">
			<#else>
			<tr bgcolor="blue">
			</#if>
				<td>${stu_index+1}</td>
				<td>${stu.id}</td>
				<td>${stu.name}</td>
				<td>${stu.sex}</td>
				<td>${stu.age}</td>
			</tr>
			</#list>
			</table>
			<br>
			日期类型的处理:${date?string('yyyy/MM/dd HH:mm:ss')}
			<br>
			null值处理：${val!1}
			<#if val??>
			val有值
			<#else>
			val无值
			</#if>
			
		</body>
	</head>
</html>