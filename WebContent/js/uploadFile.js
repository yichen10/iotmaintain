/**
 * 
 */
$(function(){
	loadCombobox("QueryHostName.do");
});

function loadCombobox(reloadUrl){
	$('#host').combobox({
	    onChange:function(record){
	    	var param = "?host=" + $('#host').combobox('getValue') + "&";
    		$('#host').combobox("reload", reloadUrl + param);
	    }   
	});
}
