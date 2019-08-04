app.controller("searchController", function($scope, searchService){	
	
	// 定义搜索对象的结构
	$scope.searchMap={"keywords":"","category":"", "brand":"", "spec":{}, price:""};
	
	//搜索
	$scope.search=function(){
		searchService.search($scope.searchMap).success(
			function(response){
				$scope.resultMap=response;//搜索返回的结果
			}
		);	
	}
	
	// 添加搜索项
	$scope.addSearchItem=function(key, value) {
		if(key == "category" || key == "brand" || key == "price") { // 点击分类或品牌
			$scope.searchMap[key] = value;
		} else { // 点击规格
			$scope.searchMap.spec[key] = value;
		}
		$scope.search(); // 查询
	}
	
	// 撤销搜索项
	$scope.removeSearchItem=function(key, value) {
		if(key == "category" || key == "brand" || key == "price") { // 点击分类或品牌
			$scope.searchMap[key] = "";
		} else { // 点击规格
			delete $scope.searchMap.spec[key];
		}
		$scope.search(); // 查询
	}
	

	
	
	
});




