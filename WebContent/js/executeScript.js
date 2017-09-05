/**
 * 
 */
var localCache = "";
var current = "";
var remote = "";

var arr = ["Host", "FileDirectory", "NextFileDirectory"];
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

function getParam(){
	var param = {};
	var i = 0;
	param.host = $('#' + arr[i++]).combobox('getValue');
	param.file_directory = $('#' + arr[i++]).combobox('getValue');
	param.next_file_directory = $('#' + arr[i++]).combobox('getValue');
	return param;
}

function exexuteCommand() {
	var param = {};
	param.host = $("#Host").val();
	param.script_dir = $("#script_dir").val();
	param.script_command = $("#script_command").val();
	var is_root=$("#is_root option:selected").val();
	param.is_root = is_root;
	var url = 'ExecuteScriptCommand.do';
	$.get(url, param, function(reply){
			if(reply.code == "0000"){
				$.messager.alert('执行脚本成功',reply.message,'info');				
			}else{
				$.messager.alert('错误',reply.message,'error');
			}
	}, "json");
}