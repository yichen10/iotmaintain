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
	$.get(url, param, function(reply){
			if(reply.code == "0000"){
				localCache = reply.content;
				$("#alongFileContentEdit").html(localCache);
				//编辑文件；
				document.getElementById("alongFileContentEdit").contentEditable = true;
				//$("#alongFileContent").css("contentEditable","true");
				$("#alongFile").show();
				//$("#doubleFile").hide();
			}else{
				$.messager.alert('错误',reply.message,'error');
			}
		//}
	}, "json");
}


function updateFileContent() {
	
	var param = getParam();
	var new_filecontent = $("#alongFileContentEdit").html()
//	new_filecontent=encodeURI(encodeURI(new_filecontent));
	param.new_filecontent = new_filecontent;
	
	//将文件内容上传到linux系统中：
	$.get("UpdateFileContent.do",param, function(reply){
		if(reply.code == "0000"){
			$.messager.alert('上传文件','文件上传成功。','info');
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

