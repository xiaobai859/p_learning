app.service("searchService", function($Http) {
	// 搜索
	this.search=function(searchMap) {
		return $http.post("itemSearch/search.do",searchMap);
	}
});