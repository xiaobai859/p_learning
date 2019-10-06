app.controller("baseController", function ($scope) {
  //分页控件配置currentPage:当前页   totalItems :总记录数  itemsPerPage:每页记录数  perPageOptions :分页选项  onChange:当页码变更后自动触发的方法 
  $scope.paginationConf = {
    currentPage: 1,
    totalItems: 10,
    itemsPerPage: 10,
    perPageOptions: [5, 10, 20, 50, 100],
    onChange: function () {
      $scope.reloadList();
    }
  };

  // 刷新列表
  $scope.reloadList = function () {
    $scope.search($scope.paginationConf.currentPage,
        $scope.paginationConf.itemsPerPage)
  }

  // 获取用户选中的所有品牌id
  $scope.selectIds = []; // 数组获取用户所选的品牌id
  $scope.updateSelection = function ($event, id) {
    if ($event.target.checked) { //  选中状态
      $scope.selectIds.push(id); // 向集合添加元素
    } else {
      var index = $scope.selectIds.indexOf(id);// 获取取消选中的索引值
      $scope.selectIds.splice(index, 1); // 根据索引值,将其从集合中删除
    }
  };

  // json对象转换为字符串
  $scope.jsonToString = function (jsonString, key) {
    var value = "";
    var json = JSON.parse(jsonString);
    for (var i = 0; i < json.length; i++) {
      if (i > 0) {
        value += ",";
      }
      value += json[i][key];
    }
    return value;
  }

})	
