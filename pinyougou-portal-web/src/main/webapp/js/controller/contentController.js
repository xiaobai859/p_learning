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
	
	
});