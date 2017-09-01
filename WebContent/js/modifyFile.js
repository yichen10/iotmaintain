/**
 * 
 */

var localCache = "";
var current = "";
var remote = "";

var arr = ["Host", "App", "Port", "File", "Time"];
var arrSite = [];

$(function(){
	$("#alongFile").show();
	$("#doubleFile").hide();
	for(var i = 0; i < arr.length - 1;i++){
		loadCombobox(arr[i], "Query" + arr[i + 1] + "Name.do");
	}
	$('#Time').combobox({
		onChange:function(record){
			if(record != undefined && record != "" && record != null){
				sidewayCompare();
			}
		}
	});
	$("#originalFile").scroll(function(){
        $("#referenceFile").scrollTop($(this).scrollTop()); // 纵向滚动条
        $("#referenceFile").scrollLeft($(this).scrollLeft()); // 横向滚动条
    });
    $("#referenceFile").scroll(function(){
        $("#originalFile").scrollTop($(this).scrollTop());
        $("#originalFile").scrollLeft($(this).scrollLeft());
    });
});

function loadCombobox(paramName, reloadUrl){
	$('#'+paramName).combobox({
	    onChange:function(record){
	    	var param = "?";
	    	var i = arr.length - 1;
	    	for(; paramName != arr[i] && i >= 0; i--){
	    		$('#' + arr[i]).combobox('clear');
	    	}
	    	tempName = arr[++i];
	    	for(i = 0; tempName != arr[i] && i < arr.length - 1; i++){
	    		param += arr[i].toLowerCase() + "=" + $('#' + arr[i]).combobox('getValue') + "&";
	    	}
    		$('#'+tempName).combobox("reload", reloadUrl + param);
	    }   
	});
}

/**
 * 编辑该文件：
 * @param type
 */
function sidewayCompare(type){
	var param = getParam();
	var url = "QueryFileContent.do";
	if(type == "sidewayCompare"){
		url = "QueryTimeName.do";
	}
	$.get(url, param, function(reply){
		if(type == "sidewayCompare"){
			if(reply.length > 0){
				var len = $('#ulList').datalist('getRows').length;
				for(var i = len - 1; i >= 0; i--){
					$('#ulList').datalist('deleteRow',i);
				}
				for(var j = 0; j < reply.length; j++){
					if(param.time != reply[j].value){
						$("#ulList").datalist("appendRow", reply[j]);
					}
				}
				$("#sidewayDialog").dialog("open");
			}else{
				$.messager.alert('警告',"没有数据！",'warning');
			}
		}else{
			if(reply.code == "0000"){
				localCache = reply.content;
				$("#alongFileContentEdit").html(localCache);
				//编辑文件；
				document.getElementById("alongFileContentEdit").contentEditable = true;
				//$("#alongFileContent").css("contentEditable","true");
				$("#alongFile").show();
				$("#doubleFile").hide();
			}else{
				$.messager.alert('错误',reply.message,'error');
			}
		}
	}, "json");
}

/**
 * 上传该文件：
 */
function vertical(){

	getTreeGrid();
	$("#vertical").dialog("open");
}

function updateFileContent() {
	
//	alert($("#alongFileContentEdit").text());
//	alert($("#alongFileContentEdit").html());
//	alert($("#alongFileContentEdit").val());
	var param = getParam();
	var new_filecontent = $("#alongFileContentEdit").html()
//	new_filecontent=encodeURI(encodeURI(new_filecontent));
	param.new_filecontent = new_filecontent;
	
	//将文件内容上传到linux系统中：
	$.get("UpdateFileContent.do",param, function(reply){
		if(reply.code == "0000"){
			var original = localCache;
			var reference = reply.content;
			compareFile(original, reference);
			$("#originalFile").html(current);
			$("#referenceFile").html(remote);
			$("#alongFile").hide();
			$("#doubleFile").show();
			synchro();
			$("body").layout("collapse", "west");
		}else{
			$.messager.alert('错误',reply.message,'error');
		}
	}, "json");
	
}
function onDblRow(index, row){
	$("#sidewayDialog").dialog("close");
	var param = getParam();
	param.time = row.value;
	$.get("QueryFileContent.do",param, function(reply){
		if(reply.code == "0000"){
			var original = localCache;
			var reference = reply.content;
			compareFile(original, reference);
			$("#originalFile").html(current);
			$("#referenceFile").html(remote);
			$("#alongFile").hide();
			$("#doubleFile").show();
			synchro();
			$("body").layout("collapse", "west");
		}else{
			$.messager.alert('错误',reply.message,'error');
		}
	}, "json");
}

function getParam(){
	var param = {};
	var i = 0;
	param.host = $('#' + arr[i++]).combobox('getValue');
	param.app = $('#' + arr[i++]).combobox('getValue');
	param.port = $('#' + arr[i++]).combobox('getValue');
	param.file = $('#' + arr[i++]).combobox('getValue');
	param.time = $('#' + arr[i++]).combobox('getValue');
	return param;
}

function compareFile(original, reference){
	var originals = original.split("<br>");
	var references = reference.split("<br>");
	current = "";
	remote = "";
	var i = 0;
	var j = 0;
	var k = 0;
	var objSite = {};
	objSite.sBase = 0;
	objSite.vBase = 0;
	objSite.sAdd = 0;
	objSite.vAdd = 0;
	arrSite[k++] = objSite;
	for(; i < originals.length && j < references.length; ){
		if(originals[i] == references[j]){
			current += originals[i] + "<br>";
			remote += references[j] + "<br>";
			i++;
			j++;
		} else {
			Outer: for (var m = i; m < originals.length; m++) {
				for (var n = j; n < references.length; n++) {
					if (originals[m] == references[n] && originals[m] != "") {
						break Outer;
					}
				}
			}
			var o = m - i;
			var p = n - j;
			if(m != i){
				current += structure(i, m, originals);
			}
			if(n != j){
				remote += structure(j, n, references);
			}
			i = m;
			j = n;
			var objSite = {};
			objSite.sBase = i;
			objSite.vBase = j;
			objSite.sAdd = o - p;
			objSite.vAdd = p - o;
			arrSite[k++] = objSite;
		}
	}
	if(i < originals.length){
		current += structure(i, originals.length, originals);
	}
	if(j < references.length){
		remote += structure(j, references.length, references);
	}
}

function structure(p, q, strs){
	 var str = "<font color='red'>";
	 for(var o = p; o < q; o++){
		 str += strs[o] + "<br>";
	 }
	 str += "</font>";
	 return str;
}

function synchro(){
	$("#originalFile").scroll(function(){
        $("#referenceFile").scrollTop(transformation($(this).scrollTop(), "x")); // 纵向滚动条
        $("#referenceFile").scrollLeft($(this).scrollLeft()); // 横向滚动条
    });
    $("#referenceFile").scroll(function(){
        $("#originalFile").scrollTop(transformation($(this).scrollTop(), "y"));
        $("#originalFile").scrollLeft($(this).scrollLeft());
    });
}

function transformation(site, type){
	var c = 20;
	if(type == "x"){
		for(var k = 1; k < arrSite.length; k++){
			if(arrSite[k - 1].sBase * c < site && arrSite[k].sBase * c > site){
				console.log(site + "_" + (arrSite[k - 1].sAdd  * c));
				return site + arrSite[k - 1].sAdd  * c;
			}
		}
	}else if(type == "y"){
		for(var k = 1; k < arrSite.length; k++){
			if(arrSite[k - 1].vBase * c < site && arrSite[k].vBase * c > site){
				console.log(site + "_" + arrSite[k - 1].vAdd);
				return site + arrSite[k - 1].vAdd * c;
			}
		}
	}
}

function getTreeGrid(){
	var param = "?";
	for(i = 0; i < arr.length - 1; i++){
		param += arr[i].toLowerCase() + "=" + $('#' + arr[i]).combobox('getValue') + "&";
	}
    $('#ttList').tree({
    	url:"QueryVerticalTreeGrid.do" + param,
    	onDblClick:function(node){
    		if(node.id.length < 6){
    			$.messager.alert('错误',"请选择文件",'error');
    			return;
    		}
    		$("#vertical").dialog("close");
    		var param = getParam();
    		param.time = node.text;
    		$.get("QueryFileContent.do",param, function(reply){
    			if(reply.code == "0000"){
    				var original = localCache;
    				var reference = reply.content;
    				compareFile(original, reference);
    				$("#originalFile").html(current);
    				$("#referenceFile").html(remote);
    				$("#alongFile").hide();
    				$("#doubleFile").show();
    				synchro();
    				$("body").layout("collapse", "west");
    			}else{
    				$.messager.alert('错误',reply.message,'error');
    			}
    		}, "json");
    	}
    	/*idField:'id',    
    	treeField:'name',  
    	param: getParam(),
    	columns:[[
    		{field:'name',title:'主机-端口',width:262},
    		{field:'time',title:'修改时间',width:60,align:'right'}
    	]]*/
	}); 
}