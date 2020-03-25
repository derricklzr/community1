$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");
    // 发送AJAX请求之前,将CSRF令牌设置到请求的消息头中.
//    var token = $("meta[name='_csrf']").attr("content");
//    var header = $("meta[name='_csrf_header']").attr("content");
//    $(document).ajaxSend(function(e, xhr, options){
//        xhr.setRequestHeader(header, token);
//    });
	// 获取浏览器传入的标题和内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	// 发送异步请求给服务器
	$.post(
	        // 访问路径
	        CONTEXT_PATH+"/discuss/add",
	        // 要传入的数据的标题和内容
	        {"title":title,"content":content},
	        // 回调函数？处理返回的结果
	        function(data){
	        data = $.parseJSON(data);
	        // 把提示消息弄到提示框中
	        $("#hintBody").text(data.msg);
	        // 提示框显示两秒隐藏
        	$("#hintModal").modal("show");
           	setTimeout(function(){
       		$("#hintModal").modal("hide");
       		// 发布成功就刷新页面
       		if(data.code==0){
       		window.location.reload();
       	    	}
           	}, 2000);
	     }
	);


}