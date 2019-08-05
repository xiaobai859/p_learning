app.controller("contentController", function($scope, contentService) {
	
	/**
	 * 根据分类id查询广告列表
	 */
	$scope.contentList=[];
	$scope.findByCategoryId=function(categoryId) {
		contentService.findByCategoryId(categoryId).success(
			function(response) {
				$scope.contentList[categoryId] = response;
				// console.log($scope.contentList[categoryId]);
			}
		);
	}
	
	/**
	 * 根据关键字搜索（实际将关键字传递到搜索页进行搜索）
	 */
	
	$scope.search=function() {
		location.href="http://localhost:9104/search.html#?keywords=" + $scope.keywords;
	}
	
	
});