app.service("loginService", function($http){
	// 获取登陆名
	this.loginName=function() {
		return $http.get("../login/showName.do");
	}
});