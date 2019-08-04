app.controller("searchController", function($scope, searchService){	
	
	// 定义搜索对象的结构
	//$scope.searchMap={"keywords":'',"catagory":'', "brand":'', "spec":{}}
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{}};//搜索对象
	
	//搜索
	$scope.search=function(){
		searchService.search($scope.searchMap).success(
			function(response){
				$scope.resultMap=response;//搜索返回的结果
			}
		);	
	}
	
	$scope.addSearchItem=function(key, value) {
		if(key == "catagory" || value == "brand") { // 点击分类或品牌
			$scope.searchMap[key] = value;
			console.log($scope.searchMap);
		} else {
			$scope.searchMap.spec[key] = value;
		}
	}
});




