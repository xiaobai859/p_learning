// 购物车前端控制层
app.controller("cartController", function($scope, cartService){
	
	// 查询购物车列表
	$scope.findCartList = function() {
		cartService.findCartList().success(
			function(resopnse) {
				$scope.cartList = resopnse;
				$scope.totalValue = cartService.sum($scope.cartList); // 获得累计数量和累计金额
			}
		);
	}
	
	// 添加商品到购物车
	$scope.addGoodsToCartList = function(itemId, num) {
		cartService.addGoodsToCartList(itemId, num).success(
			function(response) {
				if(response.success) {
					$scope.findCartList(); // 添加成功，刷新购物车列表					
				} else {
					alert(response.message);
				}
			}
		);
	}
	
});