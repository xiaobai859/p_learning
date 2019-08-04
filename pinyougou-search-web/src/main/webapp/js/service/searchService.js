app.service("searchService", function($http) {
	// 搜索
	this.search=function(searchMap) {
		//console.log(searchMap);
		return $http.post("itemSearch/search.do",searchMap);
	}
});
