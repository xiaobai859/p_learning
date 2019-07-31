app.controller("brandController", function($scope, $http, $controller, brandService){
	
	$controller("baseController", {$scope:$scope}) // 继承
	
    		// 查询品牌列表
    		$scope.findAll = function() {
    			brandService.findAll().success(
    				function(response) {
    					$scope.list = response;
    				}
    				
    			);
    		} 
    		
    		

    		
    		// 分页
    		$scope.findPage = function(page, size) {
    			brandService.findPage(page, size).success(
    					function(response) {
    						$scope.list = response.rows; // 显示当前页面数
    						$scope.paginationConf.totalItems = response.total; // 更新总记录数
    					}
    			)
    		}
			
			// 添加
			$scope.save = function() {
				var object = null;
				if ($scope.entity.id != null) {
					object = brandService.update($scope.entity);
				} else {
					object = brandService.add($scope.entity);
				}
				object.success(
					function(response) {
						if(response.success) {
							$scope.reloadList(); // 刷新
						} else{
							alert(response.message);
						}
					}
				);
			}
			
			// 根据id查询实体
			$scope.findOne = function(id) {
				brandService.findOne(id).success(
					function(response) {
						$scope.entity = response;
					}
				);
			}
			
	
			
			// 删除品牌
			$scope.del = function () {
				if(confirm('确定要删除吗？')){
					brandService.del($scope.selectIds).success (
						function (response) {
							if (response.success) {
								$scope.reloadList();
							} else {
								alert(response.message);
							}
						}
					);
				}
			}
			
			
			// 根据条件进行查询
			
			$scope.searchEntity = {};
			$scope.search = function (page, size) {
				brandService.search(page, size, $scope.searchEntity).success(
					function(response) {
						$scope.list = response.rows; // 显示当提前页面数
						$scope.paginationConf.totalItems = response.total; // 更新总记录数
					}
				);
			}
		
    		
    	});